#!/bin/bash

# Build all ClickEnRent services
# Usage: ./scripts/build-all.sh [--skip-tests] [--docker]

set -e

GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

SKIP_TESTS=false
BUILD_DOCKER=false

# Parse arguments
for arg in "$@"; do
    case $arg in
        --skip-tests)
            SKIP_TESTS=true
            shift
            ;;
        --docker)
            BUILD_DOCKER=true
            shift
            ;;
    esac
done

echo -e "${BLUE}========================================${NC}"
echo -e "${BLUE}  ClickEnRent Build Script${NC}"
echo -e "${BLUE}========================================${NC}"
echo ""

# Check if Maven is installed
if ! command -v mvn &> /dev/null; then
    echo -e "${RED}❌ Maven is not installed. Please install it first.${NC}"
    exit 1
fi

# Build with Maven
echo -e "${YELLOW}📦 Building all services with Maven...${NC}"
if [ "$SKIP_TESTS" = true ]; then
    echo -e "${YELLOW}   (Skipping tests)${NC}"
    mvn clean package -DskipTests -B
else
    echo -e "${YELLOW}   (Running tests)${NC}"
    mvn clean package -B
fi
echo ""

if [ $? -eq 0 ]; then
    echo -e "${GREEN}✅ Maven build successful${NC}"
else
    echo -e "${RED}❌ Maven build failed${NC}"
    exit 1
fi

# Build Docker images if requested
if [ "$BUILD_DOCKER" = true ]; then
    echo ""
    echo -e "${YELLOW}🐳 Building Docker images...${NC}"
    
    if ! command -v docker &> /dev/null; then
        echo -e "${RED}❌ Docker is not installed. Please install it first.${NC}"
        exit 1
    fi
    
    SERVICES=("eureka-server" "gateway" "auth-service" "rental-service" "payment-service" "support-service" "notification-service" "search-service" "analytics-service")
    
    for service in "${SERVICES[@]}"; do
        echo -e "${BLUE}Building ${service}...${NC}"
        docker build -t clickenrent/${service}:latest ./${service}
        
        if [ $? -eq 0 ]; then
            echo -e "${GREEN}✅ ${service} image built${NC}"
        else
            echo -e "${RED}❌ ${service} image build failed${NC}"
            exit 1
        fi
    done
    
    echo ""
    echo -e "${GREEN}========================================${NC}"
    echo -e "${GREEN}  Docker Images Built${NC}"
    echo -e "${GREEN}========================================${NC}"
    docker images | grep clickenrent
fi

echo ""
echo -e "${GREEN}========================================${NC}"
echo -e "${GREEN}  Build Summary${NC}"
echo -e "${GREEN}========================================${NC}"
echo ""
echo -e "${GREEN}✅ All builds completed successfully!${NC}"
echo ""

# Show JAR files
echo -e "${BLUE}📦 Built JAR files:${NC}"
find . -name "*.jar" -path "*/target/*" ! -path "*/original-*" | grep -v "shared-contracts" | sed 's|^\./||'
echo ""

if [ "$BUILD_DOCKER" = true ]; then
    echo -e "${BLUE}🐳 Next steps:${NC}"
    echo "  - Push images: docker push clickenrent/gateway:latest"
    echo "  - Or deploy locally: kubectl apply -f k8s/"
else
    echo -e "${BLUE}💡 Tip:${NC}"
    echo "  Build Docker images: ./scripts/build-all.sh --docker"
    echo "  Skip tests: ./scripts/build-all.sh --skip-tests"
fi
echo ""
