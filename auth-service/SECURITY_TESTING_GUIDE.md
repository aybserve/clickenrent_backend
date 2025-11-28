# Security Testing Guide

## Quick Testing Instructions

### Prerequisites
1. Services running (Eureka, Auth Service, Gateway)
2. Test data loaded (global roles, companies)
3. PostgreSQL running

### Setup Test Users

```sql
-- Connect to database
psql -U postgres -d clickenrent-auth

-- Create test users with different roles

-- 1. Create Admin user
INSERT INTO users (user_name, email, password, first_name, last_name, is_active, is_deleted)
VALUES ('admin_user', 'admin@test.com', '$2a$10$YourBcryptHashedPassword', 'Admin', 'User', true, false);

-- Assign Admin role (assuming global_role_id 2 = Admin)
INSERT INTO user_global_role (user_id, global_role_id)
SELECT id, 2 FROM users WHERE user_name = 'admin_user';

-- 2. Create B2B user
INSERT INTO users (user_name, email, password, first_name, last_name, is_active, is_deleted)
VALUES ('b2b_user', 'b2b@test.com', '$2a$10$YourBcryptHashedPassword', 'B2B', 'User', true, false);

-- Assign B2B role (assuming global_role_id 3 = B2B)
INSERT INTO user_global_role (user_id, global_role_id)
SELECT id, 3 FROM users WHERE user_name = 'b2b_user';

-- Assign B2B user to Company 1
INSERT INTO user_company (user_id, company_id, company_role_id)
SELECT u.id, 1, 2 FROM users u WHERE u.user_name = 'b2b_user';

-- 3. Create Customer user
INSERT INTO users (user_name, email, password, first_name, last_name, is_active, is_deleted)
VALUES ('customer_user', 'customer@test.com', '$2a$10$YourBcryptHashedPassword', 'Customer', 'User', true, false);

-- Assign Customer role (assuming global_role_id 4 = Customer)
INSERT INTO user_global_role (user_id, global_role_id)
SELECT id, 4 FROM users WHERE user_name = 'customer_user';
```

Or register via API and manually assign roles:

```bash
# Register users
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "userName": "admin_user",
    "email": "admin@test.com",
    "password": "Admin123!",
    "firstName": "Admin",
    "lastName": "User"
  }'

# Then assign roles via SQL
```

## Test Scenarios

### Test 1: Admin User - Full Access

```bash
# Login as admin
ADMIN_TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"usernameOrEmail": "admin_user", "password": "Admin123!"}' \
  | jq -r '.accessToken')

# Test: View all companies (should succeed)
curl -X GET "http://localhost:8080/api/companies" \
  -H "Authorization: Bearer $ADMIN_TOKEN" | jq '.'

# Test: View all users (should succeed)
curl -X GET "http://localhost:8080/api/users" \
  -H "Authorization: Bearer $ADMIN_TOKEN" | jq '.'

# Test: View users in any company (should succeed)
curl -X GET "http://localhost:8080/api/user-companies/company/1" \
  -H "Authorization: Bearer $ADMIN_TOKEN" | jq '.'

# Test: Create company (should succeed)
curl -X POST "http://localhost:8080/api/companies" \
  -H "Authorization: Bearer $ADMIN_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test Company",
    "description": "Created by admin",
    "companyTypeId": 1
  }' | jq '.'
```

**Expected Results**: ✅ All requests return 200/201 with data

---

### Test 2: B2B User - Limited to Their Companies

```bash
# Login as B2B user
B2B_TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"usernameOrEmail": "b2b_user", "password": "B2B123!"}' \
  | jq -r '.accessToken')

# Test: View companies (should only see Company 1)
curl -X GET "http://localhost:8080/api/companies" \
  -H "Authorization: Bearer $B2B_TOKEN" | jq '.content[] | .name'

# Test: View Company 1 (should succeed)
curl -X GET "http://localhost:8080/api/companies/1" \
  -H "Authorization: Bearer $B2B_TOKEN" | jq '.'

# Test: View Company 2 (should fail with 403)
curl -X GET "http://localhost:8080/api/companies/2" \
  -H "Authorization: Bearer $B2B_TOKEN" | jq '.'

# Test: View users (should only see users in Company 1)
curl -X GET "http://localhost:8080/api/users" \
  -H "Authorization: Bearer $B2B_TOKEN" | jq '.'

# Test: View users in Company 1 (should succeed with full details)
curl -X GET "http://localhost:8080/api/user-companies/company/1" \
  -H "Authorization: Bearer $B2B_TOKEN" | jq '.'

# Test: View users in Company 2 (should fail with 403)
curl -X GET "http://localhost:8080/api/user-companies/company/2" \
  -H "Authorization: Bearer $B2B_TOKEN" | jq '.'

# Test: Create company (should fail with 403)
curl -X POST "http://localhost:8080/api/companies" \
  -H "Authorization: Bearer $B2B_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"name": "Unauthorized", "companyTypeId": 1}' | jq '.'
```

**Expected Results**:
- ✅ View own company: 200 OK
- ❌ View other company: 403 Forbidden
- ✅ View users in own company: 200 OK with full nested details
- ❌ View users in other company: 403 Forbidden
- ❌ Create company: 403 Forbidden

---

### Test 3: Customer User - Only Own Data

```bash
# Login as customer
CUSTOMER_TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"usernameOrEmail": "customer_user", "password": "Customer123!"}' \
  | jq -r '.accessToken')

# Get customer user ID
CUSTOMER_ID=$(curl -s -X GET "http://localhost:8080/api/auth/me" \
  -H "Authorization: Bearer $CUSTOMER_TOKEN" | jq -r '.id')

# Test: View companies (should fail with 403)
curl -X GET "http://localhost:8080/api/companies" \
  -H "Authorization: Bearer $CUSTOMER_TOKEN" | jq '.'

# Test: View any company (should fail with 403)
curl -X GET "http://localhost:8080/api/companies/1" \
  -H "Authorization: Bearer $CUSTOMER_TOKEN" | jq '.'

# Test: View users list (should only see themselves)
curl -X GET "http://localhost:8080/api/users" \
  -H "Authorization: Bearer $CUSTOMER_TOKEN" | jq '.'

# Test: View own profile (should succeed)
curl -X GET "http://localhost:8080/api/users/$CUSTOMER_ID" \
  -H "Authorization: Bearer $CUSTOMER_TOKEN" | jq '.'

# Test: View another user (should fail with 403)
curl -X GET "http://localhost:8080/api/users/1" \
  -H "Authorization: Bearer $CUSTOMER_TOKEN" | jq '.'

# Test: Update own profile (should succeed)
curl -X PUT "http://localhost:8080/api/users/$CUSTOMER_ID" \
  -H "Authorization: Bearer $CUSTOMER_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "UpdatedCustomer"
  }' | jq '.'

# Test: View own company associations (should succeed)
curl -X GET "http://localhost:8080/api/user-companies/user/$CUSTOMER_ID" \
  -H "Authorization: Bearer $CUSTOMER_TOKEN" | jq '.'

# Test: View other user's associations (should fail with 403)
curl -X GET "http://localhost:8080/api/user-companies/user/1" \
  -H "Authorization: Bearer $CUSTOMER_TOKEN" | jq '.'
```

**Expected Results**:
- ❌ View companies: 403 Forbidden
- ✅ View own profile: 200 OK
- ❌ View other profiles: 403 Forbidden
- ✅ Update own profile: 200 OK
- ❌ Update other profiles: 403 Forbidden
- ✅ View own associations: 200 OK
- ❌ View others' associations: 403 Forbidden

---

### Test 4: Enhanced Response Structure

```bash
# Login as B2B user
B2B_TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"usernameOrEmail": "b2b_user", "password": "B2B123!"}' \
  | jq -r '.accessToken')

# Get company users with full nested details
curl -X GET "http://localhost:8080/api/user-companies/company/1" \
  -H "Authorization: Bearer $B2B_TOKEN" | jq '.'
```

**Expected Response Structure**:
```json
[
  {
    "id": 4,
    "user": {
      "id": 1,
      "userName": "john_doe",
      "email": "john@example.com",
      "firstName": "John",
      "lastName": "Doe",
      "phone": "+1234567890",
      "city": "New York",
      "isActive": true,
      "isDeleted": false
    },
    "company": {
      "id": 1,
      "externalId": "comp-001",
      "name": "Click & Rent",
      "description": "...",
      "website": "https://clickenrent.com",
      "companyTypeId": 1
    },
    "companyRole": {
      "id": 2,
      "name": "Admin"
    }
  }
]
```

**✅ Verification**: Response contains full nested objects, not just IDs

---

## Automated Test Script

Save as `test-security.sh`:

```bash
#!/bin/bash

GREEN='\033[0;32m'
RED='\033[0;31m'
NC='\033[0m'

echo "Testing Role-Based Security..."

# Login as Admin
echo -e "\n${GREEN}[1] Testing ADMIN user${NC}"
ADMIN_TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"usernameOrEmail":"admin_user","password":"Admin123!"}' | jq -r '.accessToken')

if [ "$ADMIN_TOKEN" != "null" ]; then
  echo "✓ Admin login successful"
  
  # Test view all companies
  COMPANIES=$(curl -s -X GET http://localhost:8080/api/companies -H "Authorization: Bearer $ADMIN_TOKEN")
  COUNT=$(echo "$COMPANIES" | jq '.totalElements')
  echo "✓ Admin can see $COUNT companies"
else
  echo -e "${RED}✗ Admin login failed${NC}"
fi

# Login as B2B
echo -e "\n${GREEN}[2] Testing B2B user${NC}"
B2B_TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"usernameOrEmail":"b2b_user","password":"B2B123!"}' | jq -r '.accessToken')

if [ "$B2B_TOKEN" != "null" ]; then
  echo "✓ B2B login successful"
  
  # Test view companies (should be filtered)
  COMPANIES=$(curl -s -X GET http://localhost:8080/api/companies -H "Authorization: Bearer $B2B_TOKEN")
  COUNT=$(echo "$COMPANIES" | jq '.totalElements')
  echo "✓ B2B can see $COUNT company(ies) (should be limited)"
  
  # Test access to own company
  STATUS=$(curl -s -o /dev/null -w "%{http_code}" -X GET http://localhost:8080/api/companies/1 \
    -H "Authorization: Bearer $B2B_TOKEN")
  if [ "$STATUS" = "200" ]; then
    echo "✓ B2B can view own company"
  else
    echo -e "${RED}✗ B2B cannot view own company${NC}"
  fi
else
  echo -e "${RED}✗ B2B login failed${NC}"
fi

# Login as Customer
echo -e "\n${GREEN}[3] Testing CUSTOMER user${NC}"
CUSTOMER_TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"usernameOrEmail":"customer_user","password":"Customer123!"}' | jq -r '.accessToken')

if [ "$CUSTOMER_TOKEN" != "null" ]; then
  echo "✓ Customer login successful"
  
  # Test view companies (should fail)
  STATUS=$(curl -s -o /dev/null -w "%{http_code}" -X GET http://localhost:8080/api/companies \
    -H "Authorization: Bearer $CUSTOMER_TOKEN")
  if [ "$STATUS" = "403" ]; then
    echo "✓ Customer correctly denied access to companies"
  else
    echo -e "${RED}✗ Customer should not see companies (got $STATUS)${NC}"
  fi
else
  echo -e "${RED}✗ Customer login failed${NC}"
fi

echo -e "\n${GREEN}Testing complete!${NC}"
```

Run with:
```bash
chmod +x test-security.sh
./test-security.sh
```

---

## Common Issues

### Issue: All users see all companies
**Cause**: SecurityService not properly injected or JWT roles not extracted  
**Fix**: Check that JWT contains roles, restart services

### Issue: 403 for admin user
**Cause**: Role name mismatch (ADMIN vs ROLE_ADMIN)  
**Fix**: Check global_role table, ensure names match exactly

### Issue: Nested objects are null
**Cause**: Lazy loading or mapper not using proper mappers  
**Fix**: Check UserCompanyMapper uses @RequiredArgsConstructor and injects other mappers

---

## Success Criteria

✅ Admin users can see/manage all resources  
✅ B2B users only see companies they belong to  
✅ B2B users only see users in their companies  
✅ Customer users only see their own data  
✅ Enhanced DTOs return full nested objects  
✅ 403 errors for unauthorized access  
✅ 200 OK for authorized access  

---

**For detailed information**, see: `ROLE_BASED_SECURITY_SUMMARY.md`

