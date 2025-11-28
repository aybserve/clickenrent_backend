# Testing Guide - Company Access Fix

## Changes Made

### 1. Gateway Configuration
**File**: `gateway/src/main/java/org/clickenrent/gateway/config/GatewayConfig.java`

Added proper routing for company endpoints:
```java
.route("companies", r -> r
    .path("/api/companies/**")
    .filters(f -> f.filter(jwtAuthenticationFilter))
    .uri("lb://auth-service"))
```

### 2. CompanyController Security
**File**: `auth-service/src/main/java/org/clickenrent/authservice/controller/CompanyController.java`

Added role-based access control to all endpoints:
```java
@PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
```

Now only users with `SuperAdmin` or `Admin` roles can access company endpoints.

---

## Prerequisites

1. **PostgreSQL Database** must be running with `clickenrent-auth` database
2. **Services must be running** in this order:
   - Eureka Server (port 8761)
   - Auth Service (port 8081)
   - Gateway (port 8080)

---

## Setup Steps

### Step 1: Start Services

#### Terminal 1 - Eureka Server
```bash
cd /Users/vitaliyshvetsov/IdeaProjects/backend
mvn -pl eureka-server spring-boot:run
```

Wait for: `"Started EurekaServerApplication"`

#### Terminal 2 - Auth Service
```bash
cd /Users/vitaliyshvetsov/IdeaProjects/backend/auth-service
mvn spring-boot:run
```

Wait for: `"Started AuthServiceApplication"`

#### Terminal 3 - Gateway
```bash
cd /Users/vitaliyshvetsov/IdeaProjects/backend/gateway
mvn clean package && mvn spring-boot:run
```

Wait for: `"Started GatewayApplication"`

---

### Step 2: Load Test Data

Connect to PostgreSQL and run the test data script:

```bash
psql -U postgres -d clickenrent-auth -f auth-service/test-data.sql
```

Or manually via psql:
```bash
psql -U postgres -d clickenrent-auth
\i /Users/vitaliyshvetsov/IdeaProjects/backend/auth-service/test-data.sql
```

This will create:
- ✅ Global Roles (SuperAdmin, Admin, B2B, Customer)
- ✅ Company Types (Hotel, B&B, Hostel, etc.)
- ✅ 5 Test Companies
- ✅ Company Roles (Owner, Admin, Manager, Staff, Viewer)

---

## Testing

### Test 1: Register a Regular User (No Admin Role)

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "userName": "testuser",
    "email": "test@example.com",
    "password": "Test123!",
    "firstName": "Test",
    "lastName": "User"
  }'
```

**Expected Response**: 201 Created with JWT tokens

Save the `accessToken` from the response.

---

### Test 2: Try to Access Companies WITHOUT Admin Role

```bash
curl -X GET http://localhost:8080/api/companies \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

**Expected Response**: `403 Forbidden`
```json
{
  "timestamp": "2025-11-28T...",
  "status": 403,
  "error": "Forbidden",
  "message": "Access Denied"
}
```

This proves the role-based security is working! ✅

---

### Test 3: Assign Admin Role to User

Connect to PostgreSQL:
```sql
-- Find your user ID
SELECT id, user_name, email FROM users WHERE user_name = 'testuser';

-- Assign Admin role (global_role_id = 2)
INSERT INTO user_global_role (user_id, global_role_id) 
VALUES (YOUR_USER_ID, 2);

-- Verify
SELECT u.user_name, gr.name as role_name
FROM users u
JOIN user_global_role ugr ON u.id = ugr.user_id
JOIN global_role gr ON ugr.global_role_id = gr.id
WHERE u.user_name = 'testuser';
```

---

### Test 4: Login Again to Get New Token with Admin Role

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "usernameOrEmail": "testuser",
    "password": "Test123!"
  }'
```

**Important**: You must login again because the JWT token is generated at login time and contains the user's roles. The old token doesn't have the Admin role.

Save the NEW `accessToken`.

---

### Test 5: Access Companies WITH Admin Role

```bash
curl -X GET http://localhost:8080/api/companies \
  -H "Authorization: Bearer NEW_ACCESS_TOKEN"
```

**Expected Response**: `200 OK` with list of companies
```json
{
  "content": [
    {
      "id": 1,
      "externalId": "comp-001",
      "name": "Grand Hotel Plaza",
      "description": "Luxury 5-star hotel in city center",
      "website": "https://grandhotelplaza.com",
      "logo": "https://example.com/logos/grand-hotel.png",
      "companyTypeId": 1
    },
    ...
  ],
  "pageable": { ... },
  "totalElements": 5,
  "totalPages": 1
}
```

Success! 🎉

---

### Test 6: Get Specific Company

```bash
curl -X GET http://localhost:8080/api/companies/1 \
  -H "Authorization: Bearer YOUR_ADMIN_TOKEN"
```

**Expected Response**: `200 OK` with company details

---

### Test 7: Create New Company

```bash
curl -X POST http://localhost:8080/api/companies \
  -H "Authorization: Bearer YOUR_ADMIN_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "New Test Hotel",
    "description": "Test hotel created via API",
    "website": "https://testhotel.com",
    "companyTypeId": 1
  }'
```

**Expected Response**: `201 Created`

---

## Troubleshooting

### Issue: 404 Not Found - "No static resource api/companies"

**Cause**: Gateway routing not configured or gateway not running

**Solution**: 
1. Ensure Gateway is running
2. Rebuild gateway to pick up new routes: `mvn clean package`
3. Check gateway logs for route registration

---

### Issue: 403 Forbidden

**Cause**: User doesn't have Admin or SuperAdmin role

**Solution**: 
1. Assign Admin role via SQL (see Test 3)
2. Login again to get new token with updated roles
3. Use the NEW token in requests

---

### Issue: 401 Unauthorized

**Cause**: Invalid or expired JWT token

**Solution**: 
1. Login again to get fresh token
2. Ensure you're including `Authorization: Bearer {token}` header
3. Check JWT_SECRET matches between auth-service and gateway

---

### Issue: Services won't start

**Cause**: Port conflicts or database connection issues

**Solution**: 
1. Check if ports are free: `lsof -i :8761,8080,8081`
2. Verify PostgreSQL is running: `pg_isready`
3. Check database credentials in `auth-service/src/main/resources/application.properties`

---

## Quick Verification Script

Save this as `test-companies.sh`:

```bash
#!/bin/bash

GATEWAY_URL="http://localhost:8080"

echo "=== Step 1: Register User ==="
REGISTER_RESPONSE=$(curl -s -X POST $GATEWAY_URL/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "userName": "admin'$(date +%s)'",
    "email": "admin'$(date +%s)'@example.com",
    "password": "Admin123!",
    "firstName": "Admin",
    "lastName": "User"
  }')

echo "$REGISTER_RESPONSE" | jq '.'
TOKEN=$(echo "$REGISTER_RESPONSE" | jq -r '.accessToken')

echo -e "\n=== Step 2: Try to access companies (should fail with 403) ==="
curl -s -X GET $GATEWAY_URL/api/companies \
  -H "Authorization: Bearer $TOKEN" | jq '.'

echo -e "\n=== Note: Assign Admin role in database and login again to access companies ==="
echo "Run: psql -U postgres -d clickenrent-auth"
echo "Then: INSERT INTO user_global_role (user_id, global_role_id) VALUES ((SELECT id FROM users WHERE email LIKE 'admin%' ORDER BY id DESC LIMIT 1), 2);"
```

Make it executable:
```bash
chmod +x test-companies.sh
./test-companies.sh
```

---

## Summary

✅ **Fixed**: Gateway routing now correctly forwards `/api/companies/**` to auth-service
✅ **Secured**: Only SuperAdmin and Admin roles can access company endpoints  
✅ **Tested**: Use the test data and steps above to verify functionality

**Key Point**: After assigning a role to a user, you MUST login again to get a new JWT token that includes the new role!

