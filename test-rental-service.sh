#!/bin/bash

# Quick test script for rental service
# Usage: ./test-rental-service.sh [username] [password]

USERNAME="${1:-admin}"
PASSWORD="${2:-admin}"

AUTH_URL="http://localhost:8081"
RENTAL_URL="http://localhost:8082"

echo "======================================"
echo "Rental Service Quick Test"
echo "======================================"
echo "Auth URL: $AUTH_URL"
echo "Rental URL: $RENTAL_URL"
echo "Username: $USERNAME"
echo ""

# Step 1: Check services
echo "Step 1: Checking services..."
if ! curl -sf "$AUTH_URL/actuator/health" > /dev/null 2>&1; then
    echo "❌ Auth service not running on $AUTH_URL"
    exit 1
fi
echo "✅ Auth service is running"

if ! curl -sf "$RENTAL_URL/actuator/health" > /dev/null 2>&1; then
    echo "❌ Rental service not running on $RENTAL_URL"
    exit 1
fi
echo "✅ Rental service is running"
echo ""

# Step 2: Login
echo "Step 2: Logging in as $USERNAME..."
RESPONSE=$(curl -s -X POST "$AUTH_URL/api/auth/login" \
  -H "Content-Type: application/json" \
  -d "{\"usernameOrEmail\":\"$USERNAME\",\"password\":\"$PASSWORD\"}")

# Extract token (works on both Linux and macOS)
TOKEN=$(echo "$RESPONSE" | grep -o '"accessToken":"[^"]*' | sed 's/"accessToken":"//')

if [ -z "$TOKEN" ]; then
    echo "❌ Login failed!"
    echo "Response: $RESPONSE"
    echo ""
    echo "Try creating a user first:"
    echo "curl -X POST $AUTH_URL/api/auth/register \\"
    echo "  -H 'Content-Type: application/json' \\"
    echo "  -d '{\"userName\":\"admin\",\"email\":\"admin@test.com\",\"password\":\"admin\",\"firstName\":\"Admin\",\"lastName\":\"User\"}'"
    exit 1
fi

echo "✅ Login successful"
echo "Token (first 50 chars): ${TOKEN:0:50}..."
echo ""

# Step 3: Decode token
echo "Step 3: Checking token structure..."
PAYLOAD=$(echo "$TOKEN" | cut -d'.' -f2)
# Add padding
PADDING=$((4 - ${#PAYLOAD} % 4))
if [ $PADDING -lt 4 ]; then
    PAYLOAD="${PAYLOAD}$(printf '%*s' $PADDING | tr ' ' '=')"
fi

DECODED=$(echo "$PAYLOAD" | base64 -d 2>/dev/null)
echo "$DECODED"
echo ""

# Check for roles claim
if echo "$DECODED" | grep -q '"roles"'; then
    echo "✅ Token contains 'roles' claim"
else
    echo "❌ Token missing 'roles' claim - this will cause 401!"
    echo "   Restart auth-service and get a new token"
fi
echo ""

# Step 4: Test rental service
echo "Step 4: Testing GET /api/bikes..."
HTTP_CODE=$(curl -s -o /tmp/rental-response.txt -w "%{http_code}" \
  -X GET "$RENTAL_URL/api/bikes" \
  -H "Authorization: Bearer $TOKEN")

if [ "$HTTP_CODE" = "200" ]; then
    echo "✅ SUCCESS! Status: 200 OK"
    echo "Response:"
    cat /tmp/rental-response.txt | head -20
elif [ "$HTTP_CODE" = "401" ]; then
    echo "❌ FAILED: 401 Unauthorized"
    echo "Response:"
    cat /tmp/rental-response.txt
    echo ""
    echo "DIAGNOSIS:"
    echo "  - Check JWT_SECRET matches in both services"
    echo "  - Verify token has 'roles' claim (see Step 3)"
    echo "  - Check rental-service logs for JWT errors"
elif [ "$HTTP_CODE" = "403" ]; then
    echo "⚠️  403 Forbidden"
    echo "Token is valid but user lacks permissions"
    cat /tmp/rental-response.txt
else
    echo "❌ Unexpected status: $HTTP_CODE"
    cat /tmp/rental-response.txt
fi

echo ""
echo "======================================"
echo ""
echo "To use this token in other requests:"
echo "export ACCESS_TOKEN=\"$TOKEN\""
echo ""
echo "Then test with:"
echo "curl -X GET $RENTAL_URL/api/bikes \\"
echo "  -H \"Authorization: Bearer \$ACCESS_TOKEN\""
echo ""
