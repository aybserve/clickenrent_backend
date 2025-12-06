#!/bin/bash

echo "Testing Gateway JWT Fix"
echo "======================="
echo ""

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

# Check gateway is running
echo "0. Checking if gateway is running..."
if ! curl -sf http://localhost:8080/actuator/health > /dev/null 2>&1; then
    echo -e "${RED}❌ Gateway is NOT running on port 8080${NC}"
    echo "   Start with: cd gateway && ./mvnw spring-boot:run"
    exit 1
fi
echo -e "${GREEN}✅ Gateway is running${NC}"
echo ""

# Login
echo "1. Logging in to get token..."
RESPONSE=$(curl -s -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"usernameOrEmail":"admin","password":"admin"}')

TOKEN=$(echo "$RESPONSE" | grep -o '"accessToken":"[^"]*' | cut -d'"' -f4)

if [ -z "$TOKEN" ]; then
    echo -e "${RED}❌ Login failed${NC}"
    echo "Response: $RESPONSE"
    exit 1
fi

echo -e "${GREEN}✅ Login successful${NC}"
echo "Token: ${TOKEN:0:50}..."
echo ""

# Test direct access (baseline)
echo "2. Testing DIRECT access to rental-service (port 8082)..."
DIRECT_STATUS=$(curl -s -o /tmp/direct-response.txt -w "%{http_code}" \
  "http://localhost:8082/api/bikes" \
  -H "Authorization: Bearer $TOKEN")

if [ "$DIRECT_STATUS" = "200" ]; then
    echo -e "${GREEN}✅ Direct access works: 200 OK${NC}"
elif [ "$DIRECT_STATUS" = "401" ]; then
    echo -e "${RED}❌ Direct access failed: 401 (rental service JWT issue)${NC}"
    echo "This means the rental service itself has a problem"
else
    echo -e "${YELLOW}⚠ Direct access status: $DIRECT_STATUS${NC}"
fi

echo ""

# Test gateway access (the fix)
echo "3. Testing GATEWAY access (port 8080)..."
GATEWAY_STATUS=$(curl -s -o /tmp/gateway-response.txt -w "%{http_code}" \
  "http://localhost:8080/api/bikes" \
  -H "Authorization: Bearer $TOKEN")

if [ "$GATEWAY_STATUS" = "200" ]; then
    echo -e "${GREEN}✅✅✅ SUCCESS! Gateway works: 200 OK ✅✅✅${NC}"
    echo ""
    echo "Gateway JWT fix is working correctly!"
    echo ""
    echo "Response preview:"
    cat /tmp/gateway-response.txt | head -10
elif [ "$GATEWAY_STATUS" = "401" ]; then
    echo -e "${RED}❌ Still getting 401 Unauthorized${NC}"
    echo ""
    echo "TROUBLESHOOTING:"
    echo "  1. Did you restart the gateway after the fix?"
    echo "     → cd gateway && ./mvnw spring-boot:run"
    echo ""
    echo "  2. Check gateway logs for JWT errors"
    echo ""
    echo "  3. Verify the fix was applied:"
    echo "     → Check gateway/util/JwtUtil.java uses Base64.getDecoder().decode()"
    echo ""
    echo "Response:"
    cat /tmp/gateway-response.txt
elif [ "$GATEWAY_STATUS" = "503" ]; then
    echo -e "${RED}❌ 503 Service Unavailable${NC}"
    echo ""
    echo "Gateway can't reach rental-service"
    echo "Check Eureka: http://localhost:8761"
else
    echo -e "${YELLOW}⚠ Unexpected status: $GATEWAY_STATUS${NC}"
    echo "Response:"
    cat /tmp/gateway-response.txt
fi

echo ""

# Test with query parameters (original failing request)
echo "4. Testing with query parameters (page=2633&size=8546)..."
PARAM_STATUS=$(curl -s -o /dev/null -w "%{http_code}" \
  "http://localhost:8080/api/bikes?page=2633&size=8546" \
  -H "Authorization: Bearer $TOKEN")

if [ "$PARAM_STATUS" = "200" ]; then
    echo -e "${GREEN}✅ Query parameters work: 200 OK${NC}"
else
    echo -e "${YELLOW}⚠ Status: $PARAM_STATUS (may be valid if no data)${NC}"
fi

echo ""
echo "======================="
echo ""
echo "Summary:"
echo "  Direct (8082):  $DIRECT_STATUS"
echo "  Gateway (8080): $GATEWAY_STATUS"
echo "  With params:    $PARAM_STATUS"
echo ""

if [ "$GATEWAY_STATUS" = "200" ]; then
    echo -e "${GREEN}✅ All tests passed! Gateway is working correctly.${NC}"
    echo ""
    echo "You can now use:"
    echo "  http://localhost:8080/api/bikes  (through gateway)"
    echo "  http://localhost:8082/api/bikes  (direct)"
    echo ""
    echo "Export token for manual testing:"
    echo "  export ACCESS_TOKEN=\"$TOKEN\""
else
    echo -e "${RED}❌ Gateway still has issues. See troubleshooting above.${NC}"
fi
