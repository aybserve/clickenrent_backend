#!/bin/bash

# Test script for Rental Service Endpoints
# This script tests JWT authentication and various endpoints

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo "======================================"
echo "Rental Service Endpoint Test Script"
echo "======================================"
echo ""

# Configuration
AUTH_SERVICE_URL="${AUTH_SERVICE_URL:-http://localhost:8081}"
RENTAL_SERVICE_URL="${RENTAL_SERVICE_URL:-http://localhost:8082}"
GATEWAY_URL="${GATEWAY_URL:-http://localhost:8080}"

# Test credentials (update these with your actual test credentials)
USERNAME="${TEST_USERNAME:-admin}"
PASSWORD="${TEST_PASSWORD:-admin}"

echo "Configuration:"
echo "  Auth Service: $AUTH_SERVICE_URL"
echo "  Rental Service: $RENTAL_SERVICE_URL"
echo "  Gateway: $GATEWAY_URL"
echo "  Test User: $USERNAME"
echo ""

# Function to print colored output
print_success() {
    echo -e "${GREEN}✓ $1${NC}"
}

print_error() {
    echo -e "${RED}✗ $1${NC}"
}

print_info() {
    echo -e "${YELLOW}ℹ $1${NC}"
}

# Function to check if service is running
check_service() {
    local url=$1
    local service_name=$2
    
    if curl -s -f -o /dev/null "$url/actuator/health"; then
        print_success "$service_name is running"
        return 0
    else
        print_error "$service_name is not running at $url"
        return 1
    fi
}

# Step 1: Check if services are running
echo "Step 1: Checking if services are running..."
echo "-------------------------------------------"

AUTH_RUNNING=false
RENTAL_RUNNING=false

if check_service "$AUTH_SERVICE_URL" "Auth Service"; then
    AUTH_RUNNING=true
fi

if check_service "$RENTAL_SERVICE_URL" "Rental Service"; then
    RENTAL_RUNNING=true
fi

echo ""

if [ "$AUTH_RUNNING" = false ] || [ "$RENTAL_RUNNING" = false ]; then
    print_error "Not all services are running. Please start them first."
    echo ""
    echo "To start services:"
    echo "  Terminal 1: cd auth-service && ./mvnw spring-boot:run"
    echo "  Terminal 2: cd rental-service && ./mvnw spring-boot:run"
    echo "  Terminal 3: cd gateway && ./mvnw spring-boot:run (optional)"
    exit 1
fi

# Step 2: Login to get access token
echo "Step 2: Logging in to get access token..."
echo "-------------------------------------------"

LOGIN_RESPONSE=$(curl -s -X POST "$AUTH_SERVICE_URL/api/auth/login" \
    -H "Content-Type: application/json" \
    -d "{
        \"usernameOrEmail\": \"$USERNAME\",
        \"password\": \"$PASSWORD\"
    }")

if [ $? -ne 0 ]; then
    print_error "Failed to connect to auth service"
    exit 1
fi

ACCESS_TOKEN=$(echo "$LOGIN_RESPONSE" | grep -o '"accessToken":"[^"]*' | cut -d'"' -f4)

if [ -z "$ACCESS_TOKEN" ]; then
    print_error "Failed to get access token"
    echo "Response: $LOGIN_RESPONSE"
    echo ""
    echo "Possible issues:"
    echo "  1. Invalid credentials (username: $USERNAME)"
    echo "  2. User doesn't exist in database"
    echo "  3. Auth service configuration issue"
    echo ""
    echo "To create a test user, use the registration endpoint:"
    echo "  curl -X POST $AUTH_SERVICE_URL/api/auth/register \\"
    echo "    -H 'Content-Type: application/json' \\"
    echo "    -d '{\"userName\":\"admin\",\"email\":\"admin@test.com\",\"password\":\"admin\",\"firstName\":\"Admin\",\"lastName\":\"User\"}'"
    exit 1
fi

print_success "Successfully obtained access token"
print_info "Token: ${ACCESS_TOKEN:0:50}..."
echo ""

# Decode JWT to check claims
echo "Step 3: Verifying JWT token structure..."
echo "-------------------------------------------"

# Extract payload (second part of JWT)
PAYLOAD=$(echo "$ACCESS_TOKEN" | cut -d'.' -f2)
# Add padding if needed
PADDING=$((4 - ${#PAYLOAD} % 4))
if [ $PADDING -lt 4 ]; then
    PAYLOAD="${PAYLOAD}$(printf '%*s' $PADDING | tr ' ' '=')"
fi

DECODED=$(echo "$PAYLOAD" | base64 -d 2>/dev/null || echo "{}")
echo "$DECODED" | python3 -m json.tool 2>/dev/null || echo "$DECODED"

print_info "Check that the token contains 'roles' claim"
echo ""

# Step 4: Test direct access to rental service
echo "Step 4: Testing DIRECT access to Rental Service..."
echo "-------------------------------------------"

echo "Test 4a: GET /api/bikes (List all bikes)"
RESPONSE=$(curl -s -w "\nHTTP_STATUS:%{http_code}" -X GET "$RENTAL_SERVICE_URL/api/bikes" \
    -H "Authorization: Bearer $ACCESS_TOKEN")

HTTP_STATUS=$(echo "$RESPONSE" | grep HTTP_STATUS | cut -d':' -f2)
BODY=$(echo "$RESPONSE" | sed '/HTTP_STATUS/d')

echo "HTTP Status: $HTTP_STATUS"

if [ "$HTTP_STATUS" = "200" ]; then
    print_success "Successfully retrieved bikes"
    echo "$BODY" | python3 -m json.tool 2>/dev/null | head -20 || echo "$BODY" | head -20
elif [ "$HTTP_STATUS" = "401" ]; then
    print_error "401 Unauthorized - JWT token not accepted"
    echo "Response: $BODY"
    echo ""
    echo "DIAGNOSIS:"
    echo "  This means the rental service is rejecting the JWT token."
    echo "  Common causes:"
    echo "    1. JWT_SECRET mismatch between auth-service and rental-service"
    echo "    2. Token format issue (missing 'roles' claim)"
    echo "    3. SecurityConfig not properly extracting authorities"
    echo ""
    echo "Check rental-service logs for JWT validation errors"
elif [ "$HTTP_STATUS" = "403" ]; then
    print_error "403 Forbidden - User doesn't have required role"
    echo "Response: $BODY"
    echo ""
    echo "DIAGNOSIS:"
    echo "  JWT token is valid but user lacks required permissions."
    echo "  This endpoint requires authentication only (not specific role)."
    echo "  Check that roles are being properly extracted in SecurityConfig"
else
    print_error "Unexpected status: $HTTP_STATUS"
    echo "Response: $BODY"
fi

echo ""

# Step 5: Test through Gateway (if running)
if curl -s -f -o /dev/null "$GATEWAY_URL/actuator/health" 2>/dev/null; then
    echo "Step 5: Testing access through GATEWAY..."
    echo "-------------------------------------------"
    
    echo "Test 5a: GET /api/bikes through gateway"
    RESPONSE=$(curl -s -w "\nHTTP_STATUS:%{http_code}" -X GET "$GATEWAY_URL/api/bikes" \
        -H "Authorization: Bearer $ACCESS_TOKEN")
    
    HTTP_STATUS=$(echo "$RESPONSE" | grep HTTP_STATUS | cut -d':' -f2)
    BODY=$(echo "$RESPONSE" | sed '/HTTP_STATUS/d')
    
    echo "HTTP Status: $HTTP_STATUS"
    
    if [ "$HTTP_STATUS" = "200" ]; then
        print_success "Successfully retrieved bikes through gateway"
        echo "$BODY" | python3 -m json.tool 2>/dev/null | head -20 || echo "$BODY" | head -20
    else
        print_error "Failed with status $HTTP_STATUS"
        echo "Response: $BODY"
    fi
    
    echo ""
fi

# Step 6: Test admin endpoints
echo "Step 6: Testing admin endpoints..."
echo "-------------------------------------------"

echo "Test 6a: POST /api/bikes (Create bike - requires ADMIN role)"
RESPONSE=$(curl -s -w "\nHTTP_STATUS:%{http_code}" -X POST "$RENTAL_SERVICE_URL/api/bikes" \
    -H "Authorization: Bearer $ACCESS_TOKEN" \
    -H "Content-Type: application/json" \
    -d '{
        "code": "TEST001",
        "externalId": "test-bike-001"
    }')

HTTP_STATUS=$(echo "$RESPONSE" | grep HTTP_STATUS | cut -d':' -f2)
BODY=$(echo "$RESPONSE" | sed '/HTTP_STATUS/d')

echo "HTTP Status: $HTTP_STATUS"

if [ "$HTTP_STATUS" = "201" ]; then
    print_success "Successfully created bike (user has ADMIN role)"
    echo "$BODY" | python3 -m json.tool 2>/dev/null || echo "$BODY"
elif [ "$HTTP_STATUS" = "403" ]; then
    print_info "403 Forbidden - User doesn't have ADMIN role (expected for non-admin users)"
elif [ "$HTTP_STATUS" = "401" ]; then
    print_error "401 Unauthorized - Authentication failed"
    echo "Response: $BODY"
else
    print_info "Status $HTTP_STATUS - Response: $BODY"
fi

echo ""

# Step 7: Configuration verification
echo "Step 7: Configuration Verification..."
echo "-------------------------------------------"

echo "Checking JWT_SECRET consistency..."
print_info "Both auth-service and rental-service should use the same JWT_SECRET"
print_info "Default: Y2xpY2tlbnJlbnQtc2VjcmV0LWtleS1jaGFuZ2UtaW4tcHJvZHVjdGlvbi0yNTYtYml0"
echo ""

echo "======================================"
echo "Test Summary"
echo "======================================"
echo ""
echo "If you're seeing 401 errors, check:"
echo "  1. JWT_SECRET matches in both services"
echo "  2. Token contains 'roles' claim"
echo "  3. SecurityConfig RolesClaimConverter is working"
echo "  4. Services are using the same Base64 encoding"
echo ""
echo "Logs to check:"
echo "  - Auth service startup logs"
echo "  - Rental service startup logs"
echo "  - Look for JWT validation errors"
echo ""
echo "Configuration files:"
echo "  - auth-service/src/main/resources/application.properties"
echo "  - rental-service/src/main/resources/application.properties"
echo ""
