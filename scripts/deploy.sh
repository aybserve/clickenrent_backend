#!/bin/bash

# Deploy ClickEnRent to Kubernetes
# Usage: ./scripts/deploy.sh [namespace]

set -e

NAMESPACE="${1:-clickenrent}"
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

echo -e "${BLUE}========================================${NC}"
echo -e "${BLUE}  ClickEnRent Kubernetes Deployment${NC}"
echo -e "${BLUE}========================================${NC}"
echo ""

# Check if kubectl is installed
if ! command -v kubectl &> /dev/null; then
    echo -e "${RED}❌ kubectl is not installed. Please install it first.${NC}"
    exit 1
fi

# Check cluster connection
echo -e "${YELLOW}📡 Checking cluster connection...${NC}"
if ! kubectl cluster-info &> /dev/null; then
    echo -e "${RED}❌ Cannot connect to Kubernetes cluster.${NC}"
    echo -e "${YELLOW}   Make sure your kubeconfig is set up correctly.${NC}"
    exit 1
fi
echo -e "${GREEN}✅ Connected to cluster${NC}"
echo ""

# Create namespace
echo -e "${YELLOW}📦 Creating namespace: ${NAMESPACE}${NC}"
kubectl create namespace ${NAMESPACE} --dry-run=client -o yaml | kubectl apply -f -
echo ""

# Apply configurations
echo -e "${YELLOW}⚙️  Applying ConfigMap...${NC}"
kubectl apply -f k8s/configmap.yml
echo ""

echo -e "${YELLOW}🔐 Note: Secrets must be created via GitHub Actions or manually${NC}"
echo -e "${YELLOW}   Run: kubectl create secret generic app-secrets ...${NC}"
echo ""

# Apply service deployments
echo -e "${YELLOW}🚀 Deploying services...${NC}"
kubectl apply -f k8s/services/
echo ""

# Apply ingress
echo -e "${YELLOW}🌐 Applying Ingress...${NC}"
kubectl apply -f k8s/ingress.yml
echo ""

# Wait for deployments
echo -e "${YELLOW}⏳ Waiting for deployments to be ready...${NC}"
echo ""

services=("eureka-server" "gateway" "auth-service" "rental-service" "payment-service" "support-service" "notification-service" "search-service" "analytics-service")

for service in "${services[@]}"; do
    echo -e "${BLUE}Waiting for ${service}...${NC}"
    kubectl rollout status deployment/${service} -n ${NAMESPACE} --timeout=5m || echo -e "${YELLOW}⚠️  ${service} deployment timeout (may still be starting)${NC}"
done

echo ""
echo -e "${GREEN}========================================${NC}"
echo -e "${GREEN}  Deployment Summary${NC}"
echo -e "${GREEN}========================================${NC}"
echo ""

# Show pods status
echo -e "${BLUE}📊 Pods Status:${NC}"
kubectl get pods -n ${NAMESPACE}
echo ""

# Show services
echo -e "${BLUE}🔗 Services:${NC}"
kubectl get svc -n ${NAMESPACE}
echo ""

# Show ingress
echo -e "${BLUE}🌍 Ingress:${NC}"
kubectl get ingress -n ${NAMESPACE}
echo ""

# Show useful commands
echo -e "${GREEN}========================================${NC}"
echo -e "${GREEN}  Useful Commands${NC}"
echo -e "${GREEN}========================================${NC}"
echo ""
echo -e "${BLUE}View logs:${NC}"
echo "  kubectl logs -f deployment/gateway -n ${NAMESPACE}"
echo ""
echo -e "${BLUE}Scale deployment:${NC}"
echo "  kubectl scale deployment/gateway --replicas=3 -n ${NAMESPACE}"
echo ""
echo -e "${BLUE}Restart deployment:${NC}"
echo "  kubectl rollout restart deployment/gateway -n ${NAMESPACE}"
echo ""
echo -e "${BLUE}Access services:${NC}"
echo "  Gateway:     http://cnr.aybserve.com"
echo "  Eureka:      http://cnr.aybserve.com/eureka"
echo "  Swagger UI:  http://cnr.aybserve.com/swagger-ui.html"
echo ""
echo -e "${GREEN}✅ Deployment completed!${NC}"
