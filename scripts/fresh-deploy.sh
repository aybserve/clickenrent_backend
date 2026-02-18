#!/bin/bash

# =====================================================
# Fresh Kubernetes Deployment Script
# =====================================================
# This script helps you do a clean deployment from scratch

set -e

echo "===================================="
echo "FRESH DEPLOYMENT SCRIPT"
echo "===================================="
echo ""

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Step 1: Verify we're on the server
echo -e "${YELLOW}Step 1: Checking environment...${NC}"
if ! command -v kubectl &> /dev/null; then
    echo -e "${RED}❌ kubectl not found. Are you on the server?${NC}"
    exit 1
fi
echo -e "${GREEN}✅ kubectl found${NC}"

# Step 2: Clean Kubernetes
echo ""
echo -e "${YELLOW}Step 2: Cleaning Kubernetes namespace...${NC}"
read -p "This will DELETE everything in the clickenrent namespace. Continue? (yes/no): " confirm
if [ "$confirm" != "yes" ]; then
    echo "Aborted."
    exit 1
fi

echo "Deleting all resources in clickenrent namespace..."
kubectl delete all --all -n clickenrent 2>/dev/null || true
kubectl delete configmap --all -n clickenrent 2>/dev/null || true
kubectl delete secret --all -n clickenrent 2>/dev/null || true

echo -e "${GREEN}✅ Namespace cleaned${NC}"

# Step 3: Verify PostgreSQL
echo ""
echo -e "${YELLOW}Step 3: Verifying PostgreSQL...${NC}"
if nc -zv cnr.aybserve.com 5432 2>&1 | grep -q "succeeded"; then
    echo -e "${GREEN}✅ PostgreSQL accessible${NC}"
else
    echo -e "${RED}❌ PostgreSQL not accessible at cnr.aybserve.com:5432${NC}"
    echo "Fix PostgreSQL configuration before continuing."
    exit 1
fi

# Step 4: Verify Redis
echo ""
echo -e "${YELLOW}Step 4: Verifying Redis...${NC}"
if nc -zv cnr.aybserve.com 6379 2>&1 | grep -q "succeeded"; then
    echo -e "${GREEN}✅ Redis accessible${NC}"
else
    echo -e "${YELLOW}⚠️  Redis not accessible at cnr.aybserve.com:6379${NC}"
    echo "Services without Redis will work, but rate limiting may be disabled."
fi

# Step 5: Verify Elasticsearch
echo ""
echo -e "${YELLOW}Step 5: Verifying Elasticsearch...${NC}"
if nc -zv cnr.aybserve.com 9200 2>&1 | grep -q "succeeded"; then
    echo -e "${GREEN}✅ Elasticsearch accessible${NC}"
else
    echo -e "${YELLOW}⚠️  Elasticsearch not accessible at cnr.aybserve.com:9200${NC}"
    echo "Search service may not work properly."
fi

# Step 6: Ready to deploy
echo ""
echo -e "${GREEN}========================================${NC}"
echo -e "${GREEN}✅ PRE-DEPLOYMENT CHECKS PASSED${NC}"
echo -e "${GREEN}========================================${NC}"
echo ""
echo "Next steps:"
echo "1. Go to your local machine"
echo "2. Run: git add k8s/"
echo "3. Run: git commit -m 'Fix Kubernetes configuration'"
echo "4. Run: git push origin features/ci-cd"
echo "5. Watch GitHub Actions: https://github.com/YOUR_REPO/actions"
echo "6. Monitor deployment: kubectl get pods -n clickenrent -w"
echo ""
echo "Expected deployment time: 20-30 minutes"
echo ""
