# Rental Service Testing Checklist

## Quick Diagnosis

Run this command to quickly test everything:

```bash
cd /Users/vitaliyshvetsov/IdeaProjects/backend
chmod +x test-rental-service.sh
./test-rental-service.sh admin admin
```

## Manual Testing Steps

### ✅ Step 1: Verify Services Running

```bash
# Auth Service
curl http://localhost:8081/actuator/health
# Expected: {"status":"UP"}

# Rental Service
curl http://localhost:8082/actuator/health
# Expected: {"status":"UP"}
```

**❌ If services aren't running:**
```bash
# Start auth-service
cd auth-service
./mvnw spring-boot:run

# Start rental-service (in new terminal)
cd rental-service
./mvnw spring-boot:run
```

### ✅ Step 2: Login and Get Token

```bash
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"usernameOrEmail":"admin","password":"admin"}'
```

**Save the `accessToken` from response!**

**❌ If user doesn't exist, register:**
```bash
curl -X POST http://localhost:8081/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "userName": "admin",
    "email": "admin@test.com",
    "password": "admin",
    "firstName": "Admin",
    "lastName": "User"
  }'
```

### ✅ Step 3: Test Rental Service

Replace `YOUR_TOKEN_HERE` with your actual token:

```bash
export TOKEN="YOUR_TOKEN_HERE"

curl -X GET http://localhost:8082/api/bikes \
  -H "Authorization: Bearer $TOKEN"
```

**✅ Success Response (200 OK):**
```json
{
  "content": [],
  "totalElements": 0,
  "totalPages": 0,
  "size": 20
}
```

**❌ 401 Unauthorized Response:**
```json
{
  "timestamp": "2025-12-05T...",
  "status": 401,
  "error": "Unauthorized",
  "path": "/api/bikes"
}
```

## Common Issues and Fixes

### Issue 1: 401 - Token Missing Roles

**Cause:** JWT token doesn't have "roles" claim

**Fix:**
1. Restart auth-service
2. Get a NEW token
3. Verify token at https://jwt.io has "roles" field

### Issue 2: 401 - JWT Secret Mismatch

**Check:**
```bash
grep "jwt.secret" auth-service/src/main/resources/application.properties
grep "jwt.secret" rental-service/src/main/resources/application.properties
```

**Fix:** Make sure both show same value:
```
jwt.secret=${JWT_SECRET:Y2xpY2tlbnJlbnQtc2VjcmV0LWtleS1jaGFuZ2UtaW4tcHJvZHVjdGlvbi0yNTYtYml0}
```

### Issue 3: Wrong Token Format

**❌ Wrong:**
```bash
-H "Authorization: eyJhbGci..."
```

**✅ Correct:**
```bash
-H "Authorization: Bearer eyJhbGci..."
```

### Issue 4: Services Not Restarted

After fixing JWT configuration:
1. Stop both services (Ctrl+C)
2. Restart auth-service
3. Restart rental-service
4. Get NEW token
5. Test again

## Postman Testing

Import the collection:
```
File > Import > 
/Users/vitaliyshvetsov/IdeaProjects/backend/Rental-Service-Tests.postman_collection.json
```

**Test order:**
1. "0. Setup & Health Checks" → Both should return 200
2. "1. Authentication" → "Login" → Auto-saves token
3. "2. Direct - Rental Service" → "GET All Bikes" → Should return 200

## Debugging Checklist

- [ ] Both services running (health checks pass)
- [ ] Can login successfully
- [ ] Token contains "roles" claim
- [ ] Using correct Authorization header format
- [ ] Token not expired
- [ ] JWT secrets match in both services
- [ ] Services restarted after configuration changes
- [ ] Getting NEW token after service restarts

## Expected Test Results

| Endpoint | Method | Auth | Expected |
|----------|--------|------|----------|
| `/api/bikes` | GET | Required | 200 OK |
| `/api/bikes` | POST | ADMIN | 201 Created |
| `/api/bikes/{id}` | GET | Required | 200 OK or 404 |
| `/api/rentals` | GET | Required | 200 OK |
| `/api/charging-stations` | GET | Required | 200 OK |
| `/api/locations` | GET | Required | 200 OK |

## Still Having Issues?

1. **Check service logs** for JWT validation errors
2. **Enable debug logging** in rental-service:
   ```properties
   logging.level.org.springframework.security=DEBUG
   ```
3. **Verify JWT structure** at https://jwt.io
4. **Check database** - does user have roles?
5. **Try test script**: `./test-rental-service.sh`

## Success Indicators

✅ You're good when:
- Health checks return 200
- Login returns accessToken
- Token has "roles" in payload
- GET /api/bikes returns 200 (not 401)
- Response is valid JSON

---

**Quick Reference:**

```bash
# Full test sequence
curl http://localhost:8082/actuator/health  # Check service
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"usernameOrEmail":"admin","password":"admin"}'  # Get token
  
export TOKEN="paste-your-token"
curl http://localhost:8082/api/bikes -H "Authorization: Bearer $TOKEN"  # Test
```
