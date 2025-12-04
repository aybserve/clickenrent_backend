#!/bin/bash

# Quick Start Script for Testing Company Access Fix
# This script helps rebuild and test the company access fix

set -e  # Exit on error

# Color codes
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}========================================${NC}"
echo -e "${BLUE}Company Access Fix - Quick Start${NC}"
echo -e "${BLUE}========================================${NC}"

# Step 1: Rebuild Gateway
echo -e "\n${YELLOW}[STEP 1]${NC} Rebuilding Gateway to pick up new routes..."
cd gateway
if mvn clean package -DskipTests; then
    echo -e "${GREEN}✓ Gateway rebuilt successfully${NC}"
else
    echo -e "${RED}✗ Gateway build failed${NC}"
    exit 1
fi
cd ..

# Step 2: Check Database
echo -e "\n${YELLOW}[STEP 2]${NC} Checking database connection..."
if psql -U postgres -d clickenrent-auth -c "SELECT 1" > /dev/null 2>&1; then
    echo -e "${GREEN}✓ Database connection successful${NC}"
else
    echo -e "${RED}✗ Cannot connect to database${NC}"
    echo "Please ensure PostgreSQL is running and database 'clickenrent-auth' exists"
    exit 1
fi

# Step 3: Load Test Data
echo -e "\n${YELLOW}[STEP 3]${NC} Loading test data (global roles, companies, etc.)..."
if psql -U postgres -d clickenrent-auth -f auth-service/test-data.sql > /dev/null 2>&1; then
    echo -e "${GREEN}✓ Test data loaded successfully${NC}"
else
    echo -e "${YELLOW}⚠ Test data may already exist or there was an error${NC}"
fi

# Step 4: Check Services
echo -e "\n${YELLOW}[STEP 4]${NC} Checking if services are running..."

EUREKA_RUNNING=false
AUTH_RUNNING=false
GATEWAY_RUNNING=false

if curl -s http://localhost:8761 > /dev/null 2>&1; then
    echo -e "${GREEN}✓ Eureka Server is running (port 8761)${NC}"
    EUREKA_RUNNING=true
else
    echo -e "${YELLOW}⚠ Eureka Server is not running${NC}"
fi

if curl -s http://localhost:8081/actuator/health > /dev/null 2>&1; then
    echo -e "${GREEN}✓ Auth Service is running (port 8081)${NC}"
    AUTH_RUNNING=true
else
    echo -e "${YELLOW}⚠ Auth Service is not running${NC}"
fi

if curl -s http://localhost:8080/actuator/health > /dev/null 2>&1; then
    echo -e "${GREEN}✓ Gateway is running (port 8080)${NC}"
    GATEWAY_RUNNING=true
else
    echo -e "${YELLOW}⚠ Gateway is not running${NC}"
fi

# Step 5: Instructions
echo -e "\n${BLUE}========================================${NC}"
echo -e "${BLUE}Next Steps${NC}"
echo -e "${BLUE}========================================${NC}"

if [ "$EUREKA_RUNNING" = false ] || [ "$AUTH_RUNNING" = false ] || [ "$GATEWAY_RUNNING" = false ]; then
    echo -e "\n${YELLOW}Services need to be started. Run in separate terminals:${NC}"
    
    if [ "$EUREKA_RUNNING" = false ]; then
        echo -e "\n${YELLOW}Terminal 1 - Eureka Server:${NC}"
        echo "  cd $PWD"
        echo "  mvn -pl eureka-server spring-boot:run"
    fi
    
    if [ "$AUTH_RUNNING" = false ]; then
        echo -e "\n${YELLOW}Terminal 2 - Auth Service:${NC}"
        echo "  cd $PWD/auth-service"
        echo "  mvn spring-boot:run"
    fi
    
    if [ "$GATEWAY_RUNNING" = false ]; then
        echo -e "\n${YELLOW}Terminal 3 - Gateway (with new routes):${NC}"
        echo "  cd $PWD/gateway"
        echo "  mvn spring-boot:run"
    fi
    
    echo -e "\n${YELLOW}After all services are running, come back and run the test:${NC}"
    echo "  cd auth-service"
    echo "  ./test-api.sh"
else
    echo -e "\n${GREEN}All services are running!${NC}"
    echo -e "\n${YELLOW}IMPORTANT: You need to restart Gateway to pick up the new routes${NC}"
    echo "  1. Stop Gateway (Ctrl+C in its terminal)"
    echo "  2. Start it again: cd gateway && mvn spring-boot:run"
    echo ""
    echo -e "${YELLOW}Then run the automated test:${NC}"
    echo "  cd auth-service"
    echo "  ./test-api.sh"
fi

# Summary
echo -e "\n${BLUE}========================================${NC}"
echo -e "${BLUE}What Was Fixed${NC}"
echo -e "${BLUE}========================================${NC}"
echo -e "${GREEN}✓${NC} Gateway now routes /api/companies to auth-service"
echo -e "${GREEN}✓${NC} Company endpoints require Admin or SuperAdmin role"
echo -e "${GREEN}✓${NC} Test data script created with sample companies"
echo -e "${GREEN}✓${NC} Automated test script ready to run"

echo -e "\n${BLUE}========================================${NC}"
echo -e "${BLUE}Documentation${NC}"
echo -e "${BLUE}========================================${NC}"
echo "  📄 COMPANY_ACCESS_FIX.md - Complete fix summary"
echo "  📄 auth-service/TESTING_GUIDE.md - Step-by-step testing guide"
echo "  📄 auth-service/test-data.sql - Database test data"
echo "  🧪 auth-service/test-api.sh - Automated test script"

echo ""


