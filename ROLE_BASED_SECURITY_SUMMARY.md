# Role-Based Security Implementation Summary

## Overview

Implemented comprehensive role-based access control (RBAC) following microservice best practices with fine-grained permissions for different user roles.

## Security Roles

### 1. **SUPERADMIN & ADMIN** (Global Roles)
- **Scope**: System-wide access
- **Permissions**: Full access to all resources
- Can view/manage all companies, users, and relationships

### 2. **B2B** (Global Role)
- **Scope**: Limited to companies they belong to
- **Permissions**:
  - View companies they belong to
  - View users in their companies
  - View their own profile and company associations
  - **Cannot**: Create/update/delete companies or assign users

### 3. **CUSTOMER** (Global Role)
- **Scope**: Only their own data
- **Permissions**:
  - View only their own profile
  - View only their own company associations
  - **Cannot**: View other users or companies

## Changes Implemented

### 1. Enhanced Response DTOs

#### Created: `UserCompanyDetailDTO`
**Purpose**: Provide rich, nested information instead of just IDs

**Before** (only IDs):
```json
{
  "id": 4,
  "userId": 1,
  "companyId": 1,
  "companyRoleId": 2
}
```

**After** (full details):
```json
{
  "id": 4,
  "user": {
    "id": 1,
    "userName": "john_doe",
    "email": "john@example.com",
    "firstName": "John",
    "lastName": "Doe"
  },
  "company": {
    "id": 1,
    "name": "Click & Rent",
    "description": "...",
    "companyTypeId": 1
  },
  "companyRole": {
    "id": 2,
    "name": "Admin"
  }
}
```

### 2. Security Service

#### Created: `SecurityService.java`
**Location**: `org.clickenrent.authservice.service.SecurityService`

**Key Methods**:
- `isAdmin()` - Check if user is SuperAdmin or Admin
- `isB2B()` - Check if user is B2B
- `isCustomer()` - Check if user is Customer
- `hasAccessToCompany(Long companyId)` - Check company access
- `hasAccessToUser(Long userId)` - Check user access
- `getAccessibleCompanyIds()` - Get list of accessible company IDs
- `getCurrentUser()` - Get current authenticated user
- `getCurrentUserId()` - Get current user's ID

**Benefits**:
- Centralized security logic
- Reusable across services
- Easy to test and maintain
- Follows Single Responsibility Principle

### 3. Updated Services

#### CompanyService
- `getAllCompanies()` - Filters based on user role
  - Admin: All companies
  - B2B: Only their companies
  - Customer: Access denied
- `getCompanyById()` - Checks access before returning

#### UserService
- `getAllUsers()` - Filters based on user role
  - Admin: All users
  - B2B: Users in their companies
  - Customer: Only themselves
- `getUserById()` - Checks access before returning
- `updateUser()` - Only admins or the user themselves can update

#### UserCompanyService
- `getUserCompanies()` - Checks permission (admin or self)
- `getCompanyUsers()` - Checks company access
- Returns `UserCompanyDetailDTO` with full nested objects

### 4. Updated Controllers

#### CompanyController
**Endpoint Access Control**:
- `GET /api/companies` - ✅ All authenticated (filtered by role in service)
- `GET /api/companies/{id}` - ✅ All authenticated (filtered by role in service)
- `POST /api/companies` - 🔒 Admin only
- `PUT /api/companies/{id}` - 🔒 Admin only
- `DELETE /api/companies/{id}` - 🔒 Admin only

#### UserController
**Endpoint Access Control**:
- `GET /api/users` - ✅ All authenticated (filtered by role in service)
- `GET /api/users/{id}` - ✅ All authenticated (filtered by role in service)
- `GET /api/users/external/{externalId}` - 🔒 Admin only
- `POST /api/users` - 🔒 Admin only
- `PUT /api/users/{id}` - ✅ All authenticated (service checks if self or admin)
- `DELETE /api/users/{id}` - 🔒 Admin only
- `PUT /api/users/{id}/activate` - 🔒 Admin only
- `PUT /api/users/{id}/deactivate` - 🔒 Admin only

#### UserCompanyController
**Endpoint Access Control**:
- `GET /api/user-companies/user/{userId}` - ✅ All authenticated (service checks access)
- `GET /api/user-companies/company/{companyId}` - ✅ All authenticated (service checks access)
- `POST /api/user-companies` - 🔒 Admin only
- `PUT /api/user-companies/{id}/role` - 🔒 Admin only
- `DELETE /api/user-companies/{id}` - 🔒 Admin only

## Security Architecture

### Layer-Based Security (Defense in Depth)

```
┌─────────────────────────────────────┐
│     Gateway (JWT Validation)        │
│   - Validates JWT token             │
│   - Extracts user + roles           │
└─────────────────────────────────────┘
              ↓
┌─────────────────────────────────────┐
│   Controller (@PreAuthorize)        │
│   - Basic role check                │
│   - isAuthenticated()               │
│   - hasAnyRole('ADMIN', 'B2B')      │
└─────────────────────────────────────┘
              ↓
┌─────────────────────────────────────┐
│   Service (Fine-grained Logic)      │
│   - SecurityService checks          │
│   - Resource-level permissions      │
│   - Data filtering by role          │
└─────────────────────────────────────┘
```

### Benefits of This Approach

1. **Separation of Concerns**
   - Controllers: Basic authentication/role checks
   - Services: Business logic + fine-grained permissions
   - SecurityService: Reusable permission logic

2. **Scalability**
   - Easy to add new roles
   - Permission logic in one place
   - No duplication across controllers

3. **Testability**
   - SecurityService can be unit tested
   - Services can be tested with mocked SecurityService
   - Controllers remain thin

4. **Microservice Best Practices**
   - Stateless authentication (JWT)
   - Service-level authorization
   - Clear API contracts

## Files Changed

### New Files (1)
1. ✅ `dto/UserCompanyDetailDTO.java` - Enhanced DTO with nested objects
2. ✅ `service/SecurityService.java` - Centralized security logic

### Modified Files (6)
1. ✅ `mapper/UserCompanyMapper.java` - Added `toDetailDto()` method
2. ✅ `service/CompanyService.java` - Added role-based filtering
3. ✅ `service/UserService.java` - Added role-based filtering
4. ✅ `service/UserCompanyService.java` - Returns detailed DTOs + security checks
5. ✅ `controller/CompanyController.java` - Updated security annotations + docs
6. ✅ `controller/UserController.java` - Updated security annotations + docs
7. ✅ `controller/UserCompanyController.java` - Updated security annotations + docs

## Testing Scenarios

### Scenario 1: Admin User

```bash
# Admin can see all companies
GET /api/companies
→ 200 OK with all companies

# Admin can see all users
GET /api/users
→ 200 OK with all users

# Admin can view any company's users
GET /api/user-companies/company/1
→ 200 OK with full user details
```

### Scenario 2: B2B User (belongs to Company ID 1 and 3)

```bash
# B2B sees only their companies
GET /api/companies
→ 200 OK with only companies 1 and 3

# B2B can't see other companies
GET /api/companies/5
→ 403 Unauthorized

# B2B sees users in their companies
GET /api/users
→ 200 OK with users from companies 1 and 3

# B2B can view users in their company
GET /api/user-companies/company/1
→ 200 OK with full user details

# B2B can't view users in other companies
GET /api/user-companies/company/5
→ 403 Unauthorized
```

### Scenario 3: Customer User (ID 10)

```bash
# Customer can't see companies
GET /api/companies
→ 403 Unauthorized

# Customer sees only themselves
GET /api/users
→ 200 OK with only their own user

# Customer can view their own profile
GET /api/users/10
→ 200 OK

# Customer can't view other users
GET /api/users/5
→ 403 Unauthorized

# Customer can view their own company associations
GET /api/user-companies/user/10
→ 200 OK

# Customer can't view other users' associations
GET /api/user-companies/user/5
→ 403 Unauthorized
```

## API Response Examples

### GET /api/user-companies/company/1 (With Full Details)

```json
[
  {
    "id": 4,
    "user": {
      "id": 1,
      "externalId": "uuid-123",
      "userName": "john_doe",
      "email": "john@example.com",
      "firstName": "John",
      "lastName": "Doe",
      "phone": "+1234567890",
      "city": "New York",
      "isActive": true,
      "isDeleted": false,
      "dateCreated": "2025-01-15T10:30:00Z"
    },
    "company": {
      "id": 1,
      "externalId": "comp-001",
      "name": "Click & Rent",
      "description": "Premium rental service",
      "website": "https://clickenrent.com",
      "logo": "https://cdn.example.com/logo.png",
      "companyTypeId": 1
    },
    "companyRole": {
      "id": 2,
      "name": "Admin"
    }
  }
]
```

### GET /api/companies (B2B User)

```json
{
  "content": [
    {
      "id": 1,
      "name": "Click & Rent",
      "description": "...",
      "companyTypeId": 1
    },
    {
      "id": 3,
      "name": "Another Company",
      "description": "...",
      "companyTypeId": 2
    }
  ],
  "totalElements": 2,
  "totalPages": 1
}
```

## Error Responses

### 403 Forbidden (Insufficient Permissions)

```json
{
  "timestamp": "2025-11-28T14:30:00Z",
  "status": 403,
  "error": "Forbidden",
  "message": "You don't have permission to view this company",
  "path": "/api/companies/5"
}
```

### 401 Unauthorized (Not Authenticated)

```json
{
  "timestamp": "2025-11-28T14:30:00Z",
  "status": 401,
  "error": "Unauthorized",
  "message": "Full authentication is required to access this resource",
  "path": "/api/companies"
}
```

## Migration Notes

### Breaking Changes
- ❌ `UserCompanyController` endpoints now return `UserCompanyDetailDTO` instead of `UserCompanyDTO`
  - **Impact**: Frontend must handle nested objects
  - **Benefit**: Eliminates need for multiple API calls

### Non-Breaking Changes
- ✅ Company endpoints now filter based on user role (transparent to frontend)
- ✅ User endpoints now filter based on user role (transparent to frontend)
- ✅ Additional security checks (improve security without breaking existing flows)

## Performance Considerations

### Potential Issues
1. **Manual Pagination for B2B Users**
   - Currently loads all companies/users then filters in memory
   - Works fine for < 1000 records
   - For larger datasets, consider database-level filtering

### Optimization Opportunities
```java
// TODO: For large datasets, optimize with native queries
// Example:
@Query("SELECT c FROM Company c JOIN UserCompany uc ON c.id = uc.company.id WHERE uc.user.id = :userId")
Page<Company> findCompaniesByUserId(@Param("userId") Long userId, Pageable pageable);
```

## Security Best Practices Applied

✅ **Principle of Least Privilege** - Users only see what they need  
✅ **Defense in Depth** - Multiple security layers (Gateway → Controller → Service)  
✅ **Fail Secure** - Default deny, explicit allow  
✅ **Centralized Security Logic** - SecurityService as single source of truth  
✅ **Separation of Concerns** - Security logic separate from business logic  
✅ **Audit Trail Ready** - All security checks can be logged  
✅ **JWT-Based** - Stateless authentication for microservices  
✅ **Role-Based** - Not user-based, easier to manage  

## Next Steps (Optional Enhancements)

1. **Add Audit Logging**
   ```java
   @Aspect
   public class SecurityAuditAspect {
       @Around("@annotation(PreAuthorize)")
       public Object auditSecurityCheck(ProceedingJoinPoint joinPoint) {
           // Log all security checks
       }
   }
   ```

2. **Add Caching for Performance**
   ```java
   @Cacheable("userCompanies")
   public List<Long> getAccessibleCompanyIds() { ... }
   ```

3. **Add Pagination Optimization**
   - Implement database-level filtering for B2B users
   - Use native queries with JOIN conditions

4. **Add GraphQL Support**
   - Field-level security
   - Client controls response structure

---

**Status**: ✅ Complete - Ready for Testing  
**Date**: 2025-11-28  
**Follows**: Microservice Best Practices, SOLID Principles, Security-First Design

