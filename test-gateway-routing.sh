#!/bin/bash

echo "Gateway Routing Diagnostic Script"
echo "=================================="
echo ""

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

# Check Eureka
echo "1. Checking Eureka Server..."
if curl -sf http://localhost:8761/actuator/health > /dev/null 2>&1; then
    echo -e "${GREEN}✓ Eureka is running${NC}"
else
    echo -e "${RED}✗ Eureka is NOT running${NC}"
    echo "   Start with: cd eureka-server && ./mvnw spring-boot:run"
    exit 1
fi

# Check Gateway
echo "2. Checking Gateway..."
if curl -sf http://localhost:8080/actuator/health > /dev/null 2>&1; then
    echo -e "${GREEN}✓ Gateway is running${NC}"
else
    echo -e "${RED}✗ Gateway is NOT running${NC}"
    echo "   Start with: cd gateway && ./mvnw spring-boot:run"
    exit 1
fi

# Check Rental Service
echo "3. Checking Rental Service..."
if curl -sf http://localhost:8082/actuator/health > /dev/null 2>&1; then
    echo -e "${GREEN}✓ Rental Service is running${NC}"
else
    echo -e "${RED}✗ Rental Service is NOT running${NC}"
    echo "   Start with: cd rental-service && ./mvnw spring-boot:run"
    exit 1
fi

echo ""

# Check Eureka Registration
echo "4. Checking service registration in Eureka..."
APPS=$(curl -s http://localhost:8761/eureka/apps)

if echo "$APPS" | grep -q "RENTAL-SERVICE"; then
    echo -e "${GREEN}✓ rental-service is registered in Eureka${NC}"
else
    echo -e "${RED}✗ rental-service NOT registered in Eureka${NC}"
    echo "   Wait 30 seconds and check again"
    echo "   Or check rental-service logs for registration errors"
fi

if echo "$APPS" | grep -q "GATEWAY"; then
    echo -e "${GREEN}✓ gateway is registered in Eureka${NC}"
else
    echo -e "${YELLOW}⚠ gateway NOT registered in Eureka (may be OK)${NC}"
fi

echo ""

# Check Gateway Routes
echo "5. Checking gateway routes..."
ROUTES=$(curl -s http://localhost:8080/actuator/gateway/routes 2>/dev/null)

if [ $? -eq 0 ]; then
    if echo "$ROUTES" | grep -q "bikes"; then
        echo -e "${GREEN}✓ Bikes route is configured${NC}"
    else
        echo -e "${RED}✗ Bikes route NOT found in gateway routes${NC}"
        echo "   Check GatewayConfig.java"
    fi
else
    echo -e "${YELLOW}⚠ Could not access gateway routes endpoint${NC}"
    echo "   Enable with: management.endpoint.gateway.enabled=true"
fi

echo ""

# Test Login
echo "6. Testing login..."
LOGIN_RESPONSE=$(curl -s -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"usernameOrEmail":"admin","password":"admin"}')

TOKEN=$(echo "$LOGIN_RESPONSE" | grep -o '"accessToken":"[^"]*' | cut -d'"' -f4)

if [ -z "$TOKEN" ]; then
    echo -e "${RED}✗ Login failed${NC}"
    echo "   Create user first or check credentials"
    exit 1
fi

echo -e "${GREEN}✓ Login successful${NC}"
echo "   Token: ${TOKEN:0:50}..."

echo ""

# Test Direct Access
echo "7. Testing DIRECT access (http://localhost:8082/api/bikes)..."
DIRECT_STATUS=$(curl -s -o /tmp/direct-response.txt -w "%{http_code}" \
  http://localhost:8082/api/bikes \
  -H "Authorization: Bearer $TOKEN")

if [ "$DIRECT_STATUS" = "200" ]; then
    echo -e "${GREEN}✓ Direct access works (200 OK)${NC}"
else
    echo -e "${RED}✗ Direct access failed ($DIRECT_STATUS)${NC}"
    cat /tmp/direct-response.txt
fi

echo ""

# Test Gateway Access
echo "8. Testing GATEWAY access (http://localhost:8080/api/bikes)..."
GATEWAY_STATUS=$(curl -s -o /tmp/gateway-response.txt -w "%{http_code}" \
  http://localhost:8080/api/bikes \
  -H "Authorization: Bearer $TOKEN")

if [ "$GATEWAY_STATUS" = "200" ]; then
    echo -e "${GREEN}✓✓✓ SUCCESS! Gateway access works (200 OK) ✓✓✓${NC}"
    echo ""
    echo "Gateway routing is working correctly!"
    cat /tmp/gateway-response.txt | head -10
elif [ "$GATEWAY_STATUS" = "404" ]; then
    echo -e "${RED}✗ Gateway access failed (404 Not Found)${NC}"
    echo ""
    echo "DIAGNOSIS: Gateway routes not configured or not loaded"
    echo "FIX:"
    echo "  1. Check GatewayConfig.java has rental service routes"
    echo "  2. Restart gateway"
    echo "  3. Check gateway logs for 'Configuring Gateway routes...'"
    echo ""
    echo "Response:"
    cat /tmp/gateway-response.txt
elif [ "$GATEWAY_STATUS" = "503" ]; then
    echo -e "${RED}✗ Gateway access failed (503 Service Unavailable)${NC}"
    echo ""
    echo "DIAGNOSIS: Gateway can't find rental-service in Eureka"
    echo "FIX:"
    echo "  1. Wait 30 seconds for Eureka registration"
    echo "  2. Check Eureka dashboard: http://localhost:8761"
    echo "  3. Verify rental-service shows as UP"
    echo "  4. Restart rental-service if not registered"
    echo ""
    echo "Response:"
    cat /tmp/gateway-response.txt
elif [ "$GATEWAY_STATUS" = "401" ]; then
    echo -e "${RED}✗ Gateway access failed (401 Unauthorized)${NC}"
    echo ""
    echo "DIAGNOSIS: Gateway JWT filter is rejecting the token"
    echo "FIX:"
    echo "  1. Check gateway JwtUtil configuration"
    echo "  2. Verify JWT_SECRET matches auth-service"
    echo ""
    echo "Response:"
    cat /tmp/gateway-response.txt
else
    echo -e "${RED}✗ Gateway access failed ($GATEWAY_STATUS)${NC}"
    echo ""
    echo "Response:"
    cat /tmp/gateway-response.txt
fi

echo ""
echo "=================================="
echo "Quick Links:"
echo "  Eureka Dashboard: http://localhost:8761"
echo "  Gateway Swagger:  http://localhost:8080/swagger-ui.html"
echo "  Direct Access:    http://localhost:8082/api/bikes"
echo "  Gateway Access:   http://localhost:8080/api/bikes"
echo ""
echo "To manually test:"
echo "  export ACCESS_TOKEN=\"$TOKEN\""
echo "  curl http://localhost:8080/api/bikes -H \"Authorization: Bearer \$ACCESS_TOKEN\""
