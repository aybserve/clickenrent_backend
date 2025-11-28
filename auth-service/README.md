# Auth Service

A comprehensive authentication and authorization microservice built with Spring Boot, featuring JWT-based authentication, role-based access control, and user management.

## Features

- **User Registration & Authentication**
  - User registration with validation
  - Login with username or email
  - JWT-based authentication (access & refresh tokens)
  - Token refresh mechanism
  - Secure logout with token blacklisting

- **Authorization**
  - Role-based access control (RBAC)
  - Global roles (Admin, B2B, Customer, etc.)
  - Company-specific roles (Owner, Admin, Staff)
  - Method-level security with `@PreAuthorize`

- **User Management**
  - CRUD operations for users
  - User activation/deactivation
  - Soft delete functionality
  - User profile management
  - Language preferences

- **Security**
  - BCrypt password encoding
  - JWT token validation
  - Token blacklisting on logout
  - Stateless session management
  - Protection against inactive/deleted accounts

## Technology Stack

- **Framework**: Spring Boot 3.x
- **Security**: Spring Security with JWT
- **Database**: PostgreSQL (Production), H2 (Testing)
- **ORM**: Spring Data JPA / Hibernate
- **Validation**: Jakarta Validation (Bean Validation)
- **Build Tool**: Maven
- **Testing**: JUnit 5, Mockito, Spring Boot Test

## Project Structure

```
auth-service/
├── src/
│   ├── main/
│   │   ├── java/org/clickenrent/authservice/
│   │   │   ├── config/              # Security and JPA configuration
│   │   │   ├── controller/          # REST API endpoints
│   │   │   ├── dto/                 # Data Transfer Objects
│   │   │   ├── entity/              # JPA entities
│   │   │   ├── exception/           # Custom exceptions and handlers
│   │   │   ├── mapper/              # Entity-DTO mappers
│   │   │   ├── repository/          # Spring Data repositories
│   │   │   ├── service/             # Business logic
│   │   │   └── util/                # Utility classes
│   │   └── resources/
│   │       ├── application.properties
│   │       └── application-prod.properties.template
│   └── test/
│       ├── java/                    # Unit and integration tests
│       └── resources/
│           └── application-test.properties
└── pom.xml
```

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.6+
- PostgreSQL 12+ (for production)

### Configuration

#### Environment Variables

Set the following environment variables for production:

```bash
# Database Configuration
export DB_URL=jdbc:postgresql://localhost:5432/clickenrent-auth
export DB_USERNAME=your_db_username
export DB_PASSWORD=your_secure_password

# JWT Configuration
export JWT_SECRET=$(openssl rand -base64 32)
export JWT_EXPIRATION=3600000           # 1 hour in milliseconds
export JWT_REFRESH_EXPIRATION=604800000  # 7 days in milliseconds

# JPA Configuration
export JPA_DDL_AUTO=validate
export JPA_SHOW_SQL=false
```

#### Database Setup

Create the PostgreSQL database:

```sql
CREATE DATABASE clickenrent_auth;
CREATE USER clickenrent WITH ENCRYPTED PASSWORD 'your_password';
GRANT ALL PRIVILEGES ON DATABASE clickenrent_auth TO clickenrent;
```

### Running the Application

#### Development Mode

```bash
mvn spring-boot:run
```

#### Production Mode

```bash
# Build the application
mvn clean package -DskipTests

# Run with production profile
java -jar target/auth-service-1.0-SNAPSHOT.jar --spring.profiles.active=prod
```

### Running Tests

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=AuthServiceTest

# Run with coverage
mvn test jacoco:report
```

## API Endpoints

### Authentication Endpoints (Public)

#### Register User
```http
POST /api/auth/register
Content-Type: application/json

{
  "userName": "johndoe",
  "email": "john@example.com",
  "password": "SecurePass123",
  "firstName": "John",
  "lastName": "Doe",
  "phone": "+1234567890",
  "languageId": 1
}
```

#### Login
```http
POST /api/auth/login
Content-Type: application/json

{
  "usernameOrEmail": "johndoe",
  "password": "SecurePass123"
}
```

#### Refresh Token
```http
POST /api/auth/refresh
Content-Type: application/json

{
  "refreshToken": "eyJhbGciOiJIUzI1NiIs..."
}
```

### Protected Endpoints

#### Get Current User
```http
GET /api/auth/me
Authorization: Bearer {accessToken}
```

#### Logout
```http
POST /api/auth/logout
Authorization: Bearer {accessToken}
```

### User Management Endpoints

#### Get All Users (Admin Only)
```http
GET /api/users?page=0&size=20
Authorization: Bearer {adminToken}
```

#### Get User by ID
```http
GET /api/users/{id}
Authorization: Bearer {accessToken}
```

#### Update User
```http
PUT /api/users/{id}
Authorization: Bearer {accessToken}
Content-Type: application/json

{
  "userName": "johndoe",
  "email": "john@example.com",
  "firstName": "John",
  "lastName": "Doe Updated"
}
```

#### Delete User (Admin Only)
```http
DELETE /api/users/{id}
Authorization: Bearer {adminToken}
```

## Database Schema

### Core Tables

- **users**: User accounts with credentials and profile information
- **global_role**: System-wide roles (Admin, B2B, Customer)
- **company**: Company/organization entities
- **company_role**: Company-specific roles (Owner, Admin, Staff)
- **user_global_role**: Junction table for user-global role assignments
- **user_company**: Junction table for user-company-role assignments
- **language**: Supported languages for user preferences
- **company_type**: Types of companies (Hotel, B&B, etc.)

## Security Features

### Password Security
- BCrypt hashing with strength of 10
- Minimum password length enforced
- No plain-text password storage

### JWT Token Security
- HS256 algorithm for token signing
- Configurable expiration times
- Refresh token rotation
- Token blacklisting on logout
- Token validation on each request

### Authorization
- Role-based access control (RBAC)
- Method-level security
- Company-scoped permissions
- Hierarchical role structure

## Testing

The project includes comprehensive test coverage:

- **Unit Tests**: Service layer logic with mocked dependencies
- **Integration Tests**: Full request-response cycles with real Spring context
- **Repository Tests**: Database operations with H2 in-memory database
- **Security Tests**: Authentication and authorization flows

### Test Coverage

- `AuthServiceTest`: Registration, login, token refresh
- `JwtServiceTest`: Token generation, validation, extraction
- `UserServiceTest`: User CRUD operations
- `CustomUserDetailsServiceTest`: User loading with roles
- `AuthControllerIntegrationTest`: Full authentication flows
- `UserControllerIntegrationTest`: User management endpoints
- `SecurityIntegrationTest`: Security configurations and access control

## Error Handling

The service provides consistent error responses:

```json
{
  "timestamp": "2025-11-28T10:30:00",
  "status": 401,
  "error": "Unauthorized",
  "message": "Invalid username or password",
  "path": "/api/auth/login"
}
```

Common HTTP status codes:
- `200 OK`: Successful request
- `201 Created`: Resource created successfully
- `400 Bad Request`: Validation errors
- `401 Unauthorized`: Authentication failed
- `403 Forbidden`: Insufficient permissions
- `404 Not Found`: Resource not found
- `409 Conflict`: Duplicate resource
- `500 Internal Server Error`: Unexpected error

## Monitoring and Maintenance

### Token Blacklist Cleanup

The in-memory token blacklist automatically removes expired tokens. For production deployments with multiple instances, consider using Redis:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>
```

### Performance Considerations

- Use pagination for large result sets
- Index frequently queried fields
- Monitor JWT token size
- Regular database vacuum/analyze operations

## Future Enhancements

- [ ] Email verification on registration
- [ ] Password reset flow
- [ ] Account lockout after failed login attempts
- [ ] OAuth2 integration (Google, Facebook)
- [ ] Two-factor authentication (2FA)
- [ ] Rate limiting for authentication endpoints
- [ ] Audit logging for security events
- [ ] Redis-based token blacklist for distributed systems

## Troubleshooting

### Common Issues

**Issue**: Database connection errors
- **Solution**: Check PostgreSQL is running and credentials are correct

**Issue**: JWT token validation fails
- **Solution**: Ensure JWT_SECRET is properly set and consistent across restarts

**Issue**: Tests fail with "Database not found"
- **Solution**: H2 dependency should be in test scope only

**Issue**: 403 Forbidden on public endpoints
- **Solution**: Check SecurityConfig permits the endpoint pattern

## Contributing

1. Create feature branch from `main`
2. Write tests for new functionality
3. Ensure all tests pass
4. Update documentation
5. Submit pull request

## License

Copyright © 2025 ClickenRent. All rights reserved.

## Support

For issues and questions, please contact the development team.

