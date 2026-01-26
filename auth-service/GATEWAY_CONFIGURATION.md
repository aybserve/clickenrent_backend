# Gateway Configuration for Password Reset Endpoints

## Overview
The API Gateway has been updated to route the new password reset endpoints from the auth-service to external clients.

## Added Routes

### 1. Forgot Password Route
```java
.route("auth-v1-forgot-password", r -> r
        .path("/api/v1/auth/forgot-password")
        .filters(f -> f.requestRateLimiter(c -> c
                .setRateLimiter(ipRateLimiter)
                .setKeyResolver(ipKeyResolver)
                .setStatusCode(HttpStatus.TOO_MANY_REQUESTS)))
        .uri("lb://auth-service"))
```

**Details:**
- **Route ID:** `auth-v1-forgot-password`
- **Path:** `/api/v1/auth/forgot-password`
- **Method:** POST
- **Authentication:** Not required (public endpoint)
- **Rate Limiting:** IP-based rate limiter
- **Target Service:** `auth-service` (load balanced)

---

### 2. Reset Password Route
```java
.route("auth-v1-reset-password", r -> r
        .path("/api/v1/auth/reset-password")
        .filters(f -> f.requestRateLimiter(c -> c
                .setRateLimiter(ipRateLimiter)
                .setKeyResolver(ipKeyResolver)
                .setStatusCode(HttpStatus.TOO_MANY_REQUESTS)))
        .uri("lb://auth-service"))
```

**Details:**
- **Route ID:** `auth-v1-reset-password`
- **Path:** `/api/v1/auth/reset-password`
- **Method:** POST
- **Authentication:** Not required (validated via reset token)
- **Rate Limiting:** IP-based rate limiter
- **Target Service:** `auth-service` (load balanced)

---

## Security Features

### Rate Limiting
Both endpoints use **IP-based rate limiting** to prevent abuse:
- Prevents brute force attacks
- Limits requests per IP address
- Returns `429 TOO_MANY_REQUESTS` when limit exceeded
- Configuration controlled by `RateLimitConfig.java`

### Public Access
These endpoints are intentionally public (no JWT required):
- **Forgot Password:** User may have forgotten credentials
- **Reset Password:** Validated via secure 6-digit token sent to email

### Load Balancing
Both routes use Spring Cloud LoadBalancer:
- `lb://auth-service` resolves to registered auth-service instances
- Automatic failover and distribution
- Registered via Eureka service discovery

---

## Route Placement

The routes were added in the **"Public Auth Routes (v1 API)"** section of `GatewayConfig.java`, positioned:

```
✓ /api/v1/auth/register
✓ /api/v1/auth/login
✓ /api/v1/auth/refresh
✓ /api/v1/auth/verify-email
✓ /api/v1/auth/send-verification-code
✅ /api/v1/auth/forgot-password       (NEW)
✅ /api/v1/auth/reset-password        (NEW)
✓ /api/v1/auth/google/**
✓ /api/v1/auth/apple/**
```

This placement ensures:
- Logical grouping with other public auth endpoints
- Consistent security configuration
- Easy maintenance and discoverability

---

## Testing Through Gateway

### Via Gateway (Recommended)
```bash
# Through API Gateway (port 8080)
curl -X POST http://localhost:8080/api/v1/auth/forgot-password \
  -H "Content-Type: application/json" \
  -d '{"email": "user@example.com"}'

curl -X POST http://localhost:8080/api/v1/auth/reset-password \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "token": "123456",
    "newPassword": "NewSecurePass123!"
  }'
```

### Direct to Auth Service (Development Only)
```bash
# Direct to auth-service (port 8081)
curl -X POST http://localhost:8081/api/v1/auth/forgot-password \
  -H "Content-Type: application/json" \
  -d '{"email": "user@example.com"}'
```

---

## Configuration Files

### Modified File
- **Location:** `gateway/src/main/java/org/clickenrent/gateway/config/GatewayConfig.java`
- **Lines Added:** 8 lines (2 routes)
- **Section:** Public Auth Routes (v1 API)

### Related Configuration
- **Rate Limit Config:** `gateway/src/main/java/org/clickenrent/gateway/config/RateLimitConfig.java`
- **IP Key Resolver:** Resolves rate limit key by client IP
- **Redis Backend:** Rate limiting backed by Redis for distributed deployments

---

## Rate Limit Configuration

Default IP-based rate limits (configurable in `application.properties`):

```properties
# Rate limiting
rate-limit.enabled=true
rate-limit.ip.replenish-rate=10
rate-limit.ip.burst-capacity=20
```

**Meaning:**
- 10 requests per second per IP (steady state)
- Burst capacity of 20 requests
- Applies to all public auth endpoints including password reset

---

## Monitoring & Logs

### Gateway Logs
Monitor gateway routing:
```bash
# View gateway logs
tail -f gateway/logs/gateway.log | grep "auth-v1"
```

### Success Indicators
```
Mapped [POST] /api/v1/auth/forgot-password -> lb://auth-service
Mapped [POST] /api/v1/auth/reset-password -> lb://auth-service
```

### Rate Limit Exceeded
```
Rate limit exceeded for IP: 192.168.1.100
Returning 429 TOO_MANY_REQUESTS
```

---

## Production Considerations

### 1. Rate Limiting
- ✅ IP-based rate limiting active
- ✅ Prevents brute force attacks
- ✅ Protects email enumeration (forgot-password always returns success)

### 2. Load Balancing
- ✅ Multiple auth-service instances supported
- ✅ Automatic failover
- ✅ Health checks via Eureka

### 3. Security
- ✅ HTTPS required in production
- ✅ CORS properly configured
- ✅ No JWT bypass (public by design)

### 4. Monitoring
- Monitor 429 responses (rate limit hits)
- Track response times
- Alert on high error rates

---

## Troubleshooting

### Route Not Found (404)
**Problem:** Gateway returns 404
**Solution:** 
- Verify Eureka registration: `http://localhost:8761/`
- Check auth-service is running
- Verify route path in GatewayConfig.java

### Too Many Requests (429)
**Problem:** Rate limit exceeded
**Solution:**
- Normal behavior for abuse prevention
- Adjust limits in application.properties if needed
- Check if legitimate traffic is being blocked

### Service Unavailable (503)
**Problem:** Auth-service not responding
**Solution:**
- Check auth-service health: `http://localhost:8081/actuator/health`
- Verify Eureka registration
- Check service logs

---

## Related Documentation
- [PASSWORD_RESET_IMPLEMENTATION.md](PASSWORD_RESET_IMPLEMENTATION.md) - Full implementation guide
- [PASSWORD_RESET_FILES.md](PASSWORD_RESET_FILES.md) - File changes summary
- Gateway Swagger UI: `http://localhost:8080/swagger-ui.html`
- Auth Service Swagger: `http://localhost:8081/swagger-ui.html`

---

**Author:** Vitaliy Shvetsov  
**Date:** January 26, 2026  
**Version:** 1.0
