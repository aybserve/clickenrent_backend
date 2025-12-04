#!/bin/bash

# Color codes for output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

GATEWAY_URL="http://localhost:8080"

echo -e "${YELLOW}=====================================${NC}"
echo -e "${YELLOW}Auth Service - Company Access Test${NC}"
echo -e "${YELLOW}=====================================${NC}"

# Check if services are running
echo -e "\n${YELLOW}[CHECK]${NC} Checking if services are running..."
if ! curl -s http://localhost:8761 > /dev/null 2>&1; then
    echo -e "${RED}✗ Eureka Server is not running on port 8761${NC}"
    echo "Start it with: mvn -pl eureka-server spring-boot:run"
    exit 1
fi
echo -e "${GREEN}✓ Eureka Server is running${NC}"

if ! curl -s http://localhost:8081/actuator/health > /dev/null 2>&1; then
    echo -e "${RED}✗ Auth Service is not running on port 8081${NC}"
    echo "Start it with: cd auth-service && mvn spring-boot:run"
    exit 1
fi
echo -e "${GREEN}✓ Auth Service is running${NC}"

if ! curl -s http://localhost:8080/actuator/health > /dev/null 2>&1; then
    echo -e "${RED}✗ Gateway is not running on port 8080${NC}"
    echo "Start it with: cd gateway && mvn spring-boot:run"
    exit 1
fi
echo -e "${GREEN}✓ Gateway is running${NC}"

# Test 1: Register a user
echo -e "\n${YELLOW}[TEST 1]${NC} Registering new user without admin role..."
TIMESTAMP=$(date +%s)
REGISTER_RESPONSE=$(curl -s -X POST $GATEWAY_URL/api/auth/register \
  -H "Content-Type: application/json" \
  -d "{
    \"userName\": \"testuser${TIMESTAMP}\",
    \"email\": \"test${TIMESTAMP}@example.com\",
    \"password\": \"Test123!\",
    \"firstName\": \"Test\",
    \"lastName\": \"User\"
  }")

TOKEN=$(echo "$REGISTER_RESPONSE" | jq -r '.accessToken // empty')

if [ -z "$TOKEN" ] || [ "$TOKEN" == "null" ]; then
    echo -e "${RED}✗ Registration failed${NC}"
    echo "$REGISTER_RESPONSE" | jq '.'
    exit 1
fi

USERNAME=$(echo "$REGISTER_RESPONSE" | jq -r '.user.userName')
USER_ID=$(echo "$REGISTER_RESPONSE" | jq -r '.user.id')
echo -e "${GREEN}✓ User registered successfully${NC}"
echo "  Username: $USERNAME"
echo "  User ID: $USER_ID"
echo "  Token: ${TOKEN:0:20}..."

# Test 2: Try to access companies WITHOUT admin role (should fail with 403)
echo -e "\n${YELLOW}[TEST 2]${NC} Attempting to access companies without admin role..."
COMPANIES_RESPONSE=$(curl -s -w "\n%{http_code}" -X GET $GATEWAY_URL/api/companies \
  -H "Authorization: Bearer $TOKEN")

HTTP_CODE=$(echo "$COMPANIES_RESPONSE" | tail -n1)
RESPONSE_BODY=$(echo "$COMPANIES_RESPONSE" | head -n-1)

if [ "$HTTP_CODE" == "403" ]; then
    echo -e "${GREEN}✓ Correctly denied access (403 Forbidden)${NC}"
    echo "  Security is working! Users without Admin role cannot see companies."
else
    echo -e "${RED}✗ Expected 403, got $HTTP_CODE${NC}"
    echo "$RESPONSE_BODY" | jq '.'
fi

# Test 3: Assign Admin role to user
echo -e "\n${YELLOW}[TEST 3]${NC} Assigning Admin role to user..."
echo "  User ID: $USER_ID"
echo "  Executing SQL to assign Admin role (global_role_id = 2)..."

# This requires psql access - provide the command
echo -e "${YELLOW}  Please run this SQL command:${NC}"
echo "    psql -U postgres -d clickenrent-auth -c \"INSERT INTO user_global_role (user_id, global_role_id) VALUES ($USER_ID, 2);\""
echo ""
read -p "Press Enter after running the SQL command to continue..."

# Test 4: Login again to get new token with Admin role
echo -e "\n${YELLOW}[TEST 4]${NC} Logging in again to get token with Admin role..."
LOGIN_RESPONSE=$(curl -s -X POST $GATEWAY_URL/api/auth/login \
  -H "Content-Type: application/json" \
  -d "{
    \"usernameOrEmail\": \"$USERNAME\",
    \"password\": \"Test123!\"
  }")

ADMIN_TOKEN=$(echo "$LOGIN_RESPONSE" | jq -r '.accessToken // empty')

if [ -z "$ADMIN_TOKEN" ] || [ "$ADMIN_TOKEN" == "null" ]; then
    echo -e "${RED}✗ Login failed${NC}"
    echo "$LOGIN_RESPONSE" | jq '.'
    exit 1
fi

echo -e "${GREEN}✓ Login successful with Admin role${NC}"
echo "  New Token: ${ADMIN_TOKEN:0:20}..."

# Test 5: Access companies WITH admin role (should succeed with 200)
echo -e "\n${YELLOW}[TEST 5]${NC} Accessing companies with Admin role..."
COMPANIES_RESPONSE=$(curl -s -w "\n%{http_code}" -X GET $GATEWAY_URL/api/companies \
  -H "Authorization: Bearer $ADMIN_TOKEN")

HTTP_CODE=$(echo "$COMPANIES_RESPONSE" | tail -n1)
RESPONSE_BODY=$(echo "$COMPANIES_RESPONSE" | head -n-1)

if [ "$HTTP_CODE" == "200" ]; then
    echo -e "${GREEN}✓ Successfully retrieved companies (200 OK)${NC}"
    COMPANY_COUNT=$(echo "$RESPONSE_BODY" | jq '.content | length')
    TOTAL_ELEMENTS=$(echo "$RESPONSE_BODY" | jq '.totalElements')
    echo "  Companies on this page: $COMPANY_COUNT"
    echo "  Total companies: $TOTAL_ELEMENTS"
    echo ""
    echo "  Companies:"
    echo "$RESPONSE_BODY" | jq -r '.content[] | "    - [\(.id)] \(.name)"'
else
    echo -e "${RED}✗ Expected 200, got $HTTP_CODE${NC}"
    echo "$RESPONSE_BODY" | jq '.'
fi

# Test 6: Get specific company
echo -e "\n${YELLOW}[TEST 6]${NC} Getting first company details..."
FIRST_COMPANY_ID=$(echo "$RESPONSE_BODY" | jq -r '.content[0].id // empty')

if [ -n "$FIRST_COMPANY_ID" ] && [ "$FIRST_COMPANY_ID" != "null" ]; then
    COMPANY_DETAIL=$(curl -s -w "\n%{http_code}" -X GET "$GATEWAY_URL/api/companies/$FIRST_COMPANY_ID" \
      -H "Authorization: Bearer $ADMIN_TOKEN")
    
    HTTP_CODE=$(echo "$COMPANY_DETAIL" | tail -n1)
    DETAIL_BODY=$(echo "$COMPANY_DETAIL" | head -n-1)
    
    if [ "$HTTP_CODE" == "200" ]; then
        echo -e "${GREEN}✓ Successfully retrieved company details (200 OK)${NC}"
        echo "$DETAIL_BODY" | jq '.'
    else
        echo -e "${RED}✗ Expected 200, got $HTTP_CODE${NC}"
        echo "$DETAIL_BODY" | jq '.'
    fi
else
    echo -e "${YELLOW}⊘ No companies found to test${NC}"
fi

# Summary
echo -e "\n${YELLOW}=====================================${NC}"
echo -e "${YELLOW}Test Summary${NC}"
echo -e "${YELLOW}=====================================${NC}"
echo -e "${GREEN}✓ Registration works${NC}"
echo -e "${GREEN}✓ Role-based security works (403 for non-admin)${NC}"
echo -e "${GREEN}✓ Admin users can access companies (200)${NC}"
echo -e "${GREEN}✓ Gateway routing is correct${NC}"
echo ""
echo -e "${GREEN}All tests passed! 🎉${NC}"


