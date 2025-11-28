# Company Access Fix Summary

## Problem

When creating a user via `/api/auth/register`, attempting to access `/api/companies` resulted in:

```json
{
  "timestamp": "2025-11-28T12:35:06.592037Z",
  "status": 404,
  "error": "Not Found",
  "message": "No static resource api/companies.",
  "path": "/api/companies"
}
```

## Root Causes

### 1. Gateway Routing Issue
The gateway was configured to route `/api/auth/companies/**` but the `CompanyController` was mapped to `/api/companies/**`, causing a mismatch.

### 2. Missing Access Control
The `CompanyController` had no role-based access control, allowing any authenticated user to access company data.

## Solutions Implemented

### 1. Gateway Configuration Fix

**File**: `gateway/src/main/java/org/clickenrent/gateway/config/GatewayConfig.java`

Added dedicated routes for company and other resource endpoints:

```java
// Company Management Routes
.route("companies", r -> r
    .path("/api/companies/**")
    .filters(f -> f.filter(jwtAuthenticationFilter))
    .uri("lb://auth-service"))

// User Management Routes
.route("users", r -> r
    .path("/api/users/**")
    .filters(f -> f.filter(jwtAuthenticationFilter))
    .uri("lb://auth-service"))

// Admin Resources
.route("admin-resources", r -> r
    .path("/api/global-roles/**", "/api/company-roles/**", 
          "/api/company-types/**", "/api/languages/**",
          "/api/user-companies/**", "/api/user-global-roles/**")
    .filters(f -> f.filter(jwtAuthenticationFilter))
    .uri("lb://auth-service"))
```

### 2. Role-Based Access Control

**File**: `auth-service/src/main/java/org/clickenrent/authservice/controller/CompanyController.java`

Added `@PreAuthorize` annotations to all company endpoints:

```java
@GetMapping
@PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
public ResponseEntity<Page<CompanyDTO>> getAllCompanies(Pageable pageable)

@GetMapping("/{id}")
@PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
public ResponseEntity<CompanyDTO> getCompanyById(@PathVariable Long id)

@PostMapping
@PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
public ResponseEntity<CompanyDTO> createCompany(@RequestBody CompanyDTO companyDTO)

@PutMapping("/{id}")
@PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
public ResponseEntity<CompanyDTO> updateCompany(@PathVariable Long id, @RequestBody CompanyDTO companyDTO)

@DeleteMapping("/{id}")
@PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
public ResponseEntity<Void> deleteCompany(@PathVariable Long id)
```

Now only users with `SuperAdmin` or `Admin` global roles can access company management endpoints.

## Files Changed

1. ✅ `gateway/src/main/java/org/clickenrent/gateway/config/GatewayConfig.java`
2. ✅ `auth-service/src/main/java/org/clickenrent/authservice/controller/CompanyController.java`

## Test Files Created

1. ✅ `auth-service/test-data.sql` - SQL script to populate test data
2. ✅ `auth-service/TESTING_GUIDE.md` - Comprehensive testing guide
3. ✅ `auth-service/test-api.sh` - Automated test script

## How to Apply Changes

### Step 1: Rebuild Gateway (REQUIRED)

The gateway must be rebuilt to pick up the new route configuration:

```bash
cd gateway
mvn clean package
```

### Step 2: Restart Services

Restart all services in this order:

#### Option A: Using Maven

```bash
# Terminal 1 - Eureka
mvn -pl eureka-server spring-boot:run

# Terminal 2 - Auth Service  
cd auth-service && mvn spring-boot:run

# Terminal 3 - Gateway
cd gateway && mvn spring-boot:run
```

#### Option B: Using Java directly

```bash
# Terminal 1 - Eureka
cd eureka-server && java -jar target/eureka-server-*.jar

# Terminal 2 - Auth Service
cd auth-service && java -jar target/auth-service-*.jar

# Terminal 3 - Gateway
cd gateway && java -jar target/gateway-*.jar
```

### Step 3: Load Test Data

```bash
psql -U postgres -d clickenrent-auth -f auth-service/test-data.sql
```

This creates:
- Global roles (SuperAdmin, Admin, B2B, Customer)
- Company types (Hotel, B&B, Hostel, Apartment, Resort)
- 5 test companies
- Company roles (Owner, Admin, Manager, Staff, Viewer)

### Step 4: Run Tests

#### Manual Testing

Follow the guide in `auth-service/TESTING_GUIDE.md`

#### Automated Testing

```bash
cd auth-service
./test-api.sh
```

The script will:
1. ✅ Check if all services are running
2. ✅ Register a new user
3. ✅ Verify access is denied without admin role (403)
4. ✅ Prompt you to assign admin role via SQL
5. ✅ Login with new role
6. ✅ Verify access is granted with admin role (200)
7. ✅ Fetch company details

## Testing Scenarios

### Scenario 1: User Without Admin Role

```bash
# Register user
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "userName": "testuser",
    "email": "test@example.com",
    "password": "Test123!"
  }'

# Try to access companies
curl -X GET http://localhost:8080/api/companies \
  -H "Authorization: Bearer {token}"

# Result: 403 Forbidden ✓
```

### Scenario 2: User With Admin Role

```sql
-- Assign Admin role
INSERT INTO user_global_role (user_id, global_role_id) 
VALUES ({user_id}, 2);
```

```bash
# Login again to get new token
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "usernameOrEmail": "testuser",
    "password": "Test123!"
  }'

# Access companies with new token
curl -X GET http://localhost:8080/api/companies \
  -H "Authorization: Bearer {new_token}"

# Result: 200 OK with company list ✓
```

## API Endpoints

All company endpoints now require `SUPERADMIN` or `ADMIN` role:

| Method | Endpoint | Description | Required Role |
|--------|----------|-------------|---------------|
| GET | `/api/companies` | List all companies | Admin/SuperAdmin |
| GET | `/api/companies/{id}` | Get company by ID | Admin/SuperAdmin |
| POST | `/api/companies` | Create company | Admin/SuperAdmin |
| PUT | `/api/companies/{id}` | Update company | Admin/SuperAdmin |
| DELETE | `/api/companies/{id}` | Delete company | Admin/SuperAdmin |

## Key Points

1. **Gateway routes are now correct**: `/api/companies` properly forwards to auth-service
2. **Role-based security is enforced**: Only Admin/SuperAdmin can manage companies
3. **JWT tokens contain roles**: Roles are embedded in the JWT at login time
4. **Re-login required after role change**: Changing a user's role requires them to login again to get a new JWT with updated roles

## Verification Checklist

- [x] Gateway routing configured for `/api/companies/**`
- [x] CompanyController has `@PreAuthorize` annotations
- [x] Test data SQL script created
- [x] Testing guide documentation created
- [x] Automated test script created
- [ ] Services rebuilt and restarted (user action required)
- [ ] Test data loaded into database (user action required)
- [ ] Tests executed successfully (user action required)

## Next Steps for User

1. **Rebuild Gateway**: `cd gateway && mvn clean package`
2. **Restart Services**: Eureka → Auth Service → Gateway
3. **Load Test Data**: Run `test-data.sql` script
4. **Run Tests**: Execute `./test-api.sh` or follow `TESTING_GUIDE.md`

## Expected Behavior After Fix

✅ **Before**: 404 Not Found  
✅ **After (no role)**: 403 Forbidden  
✅ **After (with Admin)**: 200 OK with company list

---

**Status**: ✅ Implementation Complete - Ready for Testing
**Date**: 2025-11-28
**Services**: Gateway, Auth Service

