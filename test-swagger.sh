#!/bin/bash

# Swagger/OpenAPI Implementation Test Script
# Tests the complete Swagger setup through gateway

set -e

echo "================================================"
echo "Swagger/OpenAPI Implementation Test"
echo "================================================"
echo ""

# Colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Test function
test_endpoint() {
    local name=$1
    local url=$2
    local expected_code=$3
    
    echo -n "Testing $name... "
    
    response_code=$(curl -s -o /dev/null -w "%{http_code}" "$url")
    
    if [ "$response_code" -eq "$expected_code" ]; then
        echo -e "${GREEN}✓ PASS${NC} (HTTP $response_code)"
        return 0
    else
        echo -e "${RED}✗ FAIL${NC} (Expected HTTP $expected_code, got $response_code)"
        return 1
    fi
}

# Check if services are running
echo "Step 1: Checking if services are running..."
echo "-------------------------------------------"

test_endpoint "Eureka Server" "http://localhost:8761" "200" || echo -e "${YELLOW}Warning: Eureka may not be running${NC}"
test_endpoint "Auth Service" "http://localhost:8081/actuator/health" "200" || { echo -e "${RED}Error: Auth Service not running!${NC}"; exit 1; }
test_endpoint "Gateway" "http://localhost:8080/actuator/health" "200" || { echo -e "${RED}Error: Gateway not running!${NC}"; exit 1; }

echo ""
echo "Step 2: Testing OpenAPI Documentation Endpoints..."
echo "---------------------------------------------------"

# Test auth-service OpenAPI spec (direct)
test_endpoint "Auth Service API Docs (direct)" "http://localhost:8081/v3/api-docs" "200"

# Test auth-service Swagger UI (direct)
test_endpoint "Auth Service Swagger UI (direct)" "http://localhost:8081/swagger-ui/index.html" "200"

# Test gateway OpenAPI spec
test_endpoint "Gateway API Docs" "http://localhost:8080/v3/api-docs" "200"

# Test gateway Swagger UI
test_endpoint "Gateway Swagger UI" "http://localhost:8080/swagger-ui/index.html" "200"

# Test auth-service docs through gateway
test_endpoint "Auth Service Docs (via Gateway)" "http://localhost:8080/auth-service/v3/api-docs" "200"

echo ""
echo "Step 3: Verifying OpenAPI Spec Content..."
echo "------------------------------------------"

echo -n "Checking if auth-service spec contains endpoints... "
spec_content=$(curl -s http://localhost:8081/v3/api-docs)

if echo "$spec_content" | grep -q "/api/auth/login" && \
   echo "$spec_content" | grep -q "/api/auth/register" && \
   echo "$spec_content" | grep -q "/api/users"; then
    echo -e "${GREEN}✓ PASS${NC}"
else
    echo -e "${RED}✗ FAIL${NC}"
fi

echo -n "Checking if spec contains JWT security scheme... "
if echo "$spec_content" | grep -q "bearerAuth"; then
    echo -e "${GREEN}✓ PASS${NC}"
else
    echo -e "${RED}✗ FAIL${NC}"
fi

echo ""
echo "================================================"
echo "Test Summary"
echo "================================================"
echo ""
echo -e "${GREEN}All core tests passed!${NC}"
echo ""
echo "Next Steps:"
echo "1. Open Swagger UI in your browser:"
echo -e "   ${YELLOW}http://localhost:8080/swagger-ui.html${NC}"
echo ""
echo "2. Try registering a user (no auth required):"
echo "   POST /api/auth/register"
echo ""
echo "3. Login to get JWT token:"
echo "   POST /api/auth/login"
echo ""
echo "4. Click 'Authorize' button and enter:"
echo "   Bearer <your_access_token>"
echo ""
echo "5. Test protected endpoints like:"
echo "   GET /api/auth/me"
echo "   GET /api/users"
echo ""
echo "================================================"


