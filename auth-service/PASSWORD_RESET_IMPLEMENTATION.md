# Password Reset Implementation

## Overview
Complete password reset functionality following security best practices and matching the existing email verification patterns in the codebase.

## Architecture

### Flow Diagram
```
User Request → Forgot Password → Token Generation → Email Sent
                                        ↓
                                 Token Stored (30 min expiry)
                                        ↓
User Receives Email → Reset Password → Token Validation
                                        ↓
                                 Password Updated → Confirmation Email
```

## Security Features

### 1. Token Security
- **6-digit numeric tokens** generated using `SecureRandom`
- **30-minute expiration** (configurable via `password.reset.expiration-minutes`)
- **Single-use tokens** - marked as used after successful reset
- **Soft deletion** - old tokens invalidated when new ones are generated

### 2. Rate Limiting & Attempt Tracking
- **Maximum 3 attempts** per token (configurable via `password.reset.max-attempts`)
- **Separate transaction** for attempt tracking using `REQUIRES_NEW` propagation
- Attempts persist even if parent transaction rolls back
- Detailed error messages with remaining attempts

### 3. Email Enumeration Protection
- `/forgot-password` endpoint always returns success (204 No Content)
- No indication whether email exists in system
- Prevents attackers from discovering valid email addresses

### 4. Password Security
- Passwords hashed using BCrypt
- Minimum 8 characters enforced via validation
- Additional constraints can be added in DTO validation

## API Endpoints

### 1. Initiate Password Reset
```http
POST /api/v1/auth/forgot-password
Content-Type: application/json

{
  "email": "user@example.com"
}
```

**Response:** `204 No Content` (always, for security)

**Security:** Public endpoint, no authentication required

---

### 2. Reset Password
```http
POST /api/v1/auth/reset-password
Content-Type: application/json

{
  "email": "user@example.com",
  "token": "123456",
  "newPassword": "NewSecurePass123!"
}
```

**Success Response:** `200 OK`
```json
{
  "success": true,
  "message": "Password has been reset successfully"
}
```

**Error Responses:**
- `400 Bad Request` - Invalid token, expired, or max attempts reached
- `404 Not Found` - User not found

**Security:** Public endpoint, validated via reset token

---

## Database Schema

### password_reset_token Table
```sql
CREATE TABLE password_reset_token (
    id                          BIGSERIAL PRIMARY KEY,
    user_id                     BIGINT NOT NULL,
    email                       VARCHAR(255) NOT NULL,
    token                       VARCHAR(6) NOT NULL,
    expires_at                  TIMESTAMP NOT NULL,
    attempts                    INTEGER NOT NULL DEFAULT 0,
    is_used                     BOOLEAN NOT NULL DEFAULT false,
    used_at                     TIMESTAMP,
    
    -- Audit fields
    date_created                TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_date_modified          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by                  VARCHAR(255),
    last_modified_by            VARCHAR(255),
    is_deleted                  BOOLEAN NOT NULL DEFAULT false,
    
    CONSTRAINT fk_password_reset_user FOREIGN KEY (user_id) 
        REFERENCES users(id) ON DELETE CASCADE
);

-- Indexes for performance
CREATE INDEX idx_password_reset_user ON password_reset_token(user_id);
CREATE INDEX idx_password_reset_email ON password_reset_token(email);
CREATE INDEX idx_password_reset_token ON password_reset_token(token);
```

## Implementation Files

### Entities
- **PasswordResetToken.java** - JPA entity with soft delete support

### Repositories
- **PasswordResetTokenRepository.java** - Data access layer with custom queries

### Services
- **PasswordResetService.java** - Business logic for password reset operations
  - Token generation and validation
  - Attempt tracking with separate transactions
  - Password update logic
  - Email notifications

### DTOs
- **ForgotPasswordRequest.java** - Request to initiate password reset
- **ResetPasswordRequest.java** - Request to reset password with token
- **PasswordResetResponse.java** - Response for successful password reset

### Controllers
- **AuthController.java** - Added two endpoints:
  - `POST /api/v1/auth/forgot-password`
  - `POST /api/v1/auth/reset-password`

### Email Service
- **EmailService.java** - Extended with:
  - `sendPasswordResetEmail()` - Sends reset token
  - `sendPasswordChangedEmail()` - Confirmation after password change

## Configuration

Add to `application.properties`:
```properties
# Password Reset Configuration
password.reset.token-length=6
password.reset.expiration-minutes=30
password.reset.max-attempts=3
```

## Email Templates

### Password Reset Email
```
Subject: Reset Your Password

Hello {firstName},

You have requested to reset your password.
Your password reset code is: {token}

This code will expire in 30 minutes.
Please do not share this code with anyone.

If you did not request a password reset, please ignore this email.
Your password will remain unchanged.
```

### Password Changed Confirmation
```
Subject: Password Successfully Changed

Hello {firstName},

Your password has been successfully changed.

If you did not make this change, please contact our support team immediately.

Thank you for using ClickenRent!
```

## Testing

### Test Scenarios

1. **Happy Path**
   - Request reset token
   - Receive email with 6-digit code
   - Reset password with valid token
   - Receive confirmation email
   - Login with new password

2. **Token Expiration**
   - Request reset token
   - Wait 31 minutes
   - Attempt reset → Error: Token expired

3. **Invalid Token**
   - Request reset token
   - Use wrong token → Error: Invalid token
   - Max 3 attempts, then token blocked

4. **Email Enumeration Protection**
   - Request reset for non-existent email
   - Endpoint returns success (no error)
   - No email sent (logged internally)

5. **Token Reuse Prevention**
   - Reset password successfully
   - Try using same token again → Error: Token already used

6. **Multiple Reset Requests**
   - Request reset token
   - Request another reset token (invalidates first)
   - First token no longer valid

### Curl Examples

```bash
# 1. Request password reset
curl -X POST http://localhost:8080/api/v1/auth/forgot-password \
  -H "Content-Type: application/json" \
  -d '{"email": "user@example.com"}'

# 2. Reset password with token
curl -X POST http://localhost:8080/api/v1/auth/reset-password \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "token": "123456",
    "newPassword": "NewSecurePass123!"
  }'
```

## Swagger/OpenAPI Documentation

All endpoints are fully documented with:
- Operation summaries and descriptions
- Request/response schemas
- HTTP status codes
- Security requirements
- Example values

Access Swagger UI at: `http://localhost:8080/swagger-ui.html`

## Best Practices Followed

1. ✅ **Security First**
   - Secure token generation
   - Attempt tracking
   - Email enumeration protection
   - Single-use tokens
   - Time-limited tokens

2. ✅ **Code Quality**
   - Consistent with existing codebase patterns
   - Same structure as EmailVerification
   - Comprehensive logging
   - Proper exception handling
   - Transaction management

3. ✅ **Database Design**
   - Soft deletes
   - Proper indexing
   - Foreign key constraints
   - Audit fields

4. ✅ **API Design**
   - RESTful endpoints
   - Clear error messages
   - Proper HTTP status codes
   - Swagger documentation

5. ✅ **Maintainability**
   - Configurable parameters
   - Clear separation of concerns
   - Comprehensive documentation
   - Test scenarios provided

## Future Enhancements

1. **Rate Limiting by IP**
   - Limit password reset requests per IP address
   - Prevent automated attacks

2. **Real Email Service**
   - Replace mock EmailService with SMTP implementation
   - Support HTML email templates
   - Add email queuing system

3. **Additional Validations**
   - Password strength requirements
   - Password history (prevent reusing old passwords)
   - Common password blacklist

4. **2FA Integration**
   - Optional 2FA for password reset
   - SMS/Authenticator app support

5. **Admin Dashboard**
   - View password reset statistics
   - Manual token invalidation
   - Suspicious activity monitoring

## Support

For issues or questions:
- Check logs: `auth-service/logs/`
- Review exception handling in `GlobalExceptionHandler`
- Verify database schema: `auth-service.sql`

---

**Author:** Vitaliy Shvetsov  
**Date:** January 26, 2026  
**Version:** 1.0
