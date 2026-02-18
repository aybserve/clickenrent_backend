# ClickEnRent Deployment Guide

Complete guide for deploying ClickEnRent to production using Kubernetes (K3s) on Hetzner server.

## Table of Contents

- [Architecture Overview](#architecture-overview)
- [Prerequisites](#prerequisites)
- [Server Setup](#server-setup)
- [GitHub Secrets Configuration](#github-secrets-configuration)
- [First Deployment](#first-deployment)
- [Monitoring & Maintenance](#monitoring--maintenance)
- [Troubleshooting](#troubleshooting)
- [Rollback Procedures](#rollback-procedures)

## Architecture Overview


```
┌─────────────────────────────────────────────────────────────┐
│                      GitHub Actions                          │
│  (Build → Docker → Deploy to K8s on push to features/ci-cd)│
└─────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────┐
│              Hetzner Server (cnr.aybserve.com)              │
│                                                              │
│  ┌────────────────────────────────────────────────────┐    │
│  │              K3s Kubernetes Cluster                 │    │
│  │                                                      │    │
│  │  ┌──────────────────────────────────────────────┐ │    │
│  │  │  Microservices (9 services)                  │ │    │
│  │  │  - Gateway (2 replicas)                      │ │    │
│  │  │  - Eureka Server (1 replica)                 │ │    │
│  │  │  - Auth Service (2 replicas)                 │ │    │
│  │  │  - Rental Service (2 replicas)               │ │    │
│  │  │  - Payment Service (2 replicas)              │ │    │
│  │  │  - Support Service (2 replicas)              │ │    │
│  │  │  - Notification Service (2 replicas)         │ │    │
│  │  │  - Search Service (2 replicas)               │ │    │
│  │  │  - Analytics Service (2 replicas)            │ │    │
│  │  └──────────────────────────────────────────────┘ │    │
│  └────────────────────────────────────────────────────┘    │
│                                                              │
│  External Services (on host):                               │
│  - PostgreSQL (FastPanel) - 6 databases                     │
│  - Redis (FastPanel with password)                          │
│  - Elasticsearch + Kibana (Docker)                          │
└─────────────────────────────────────────────────────────────┘
```

## Prerequisites

### Local Machine
- Git
- kubectl (`brew install kubectl` or download from kubernetes.io)
- (Optional) k9s for cluster monitoring (`brew install k9s`)


### Hetzner Server
- Ubuntu 20.04+ or Debian 10+
- Root access
- At least 4GB RAM, 2 CPU cores (recommended: 8GB RAM, 4 cores)
- Domain pointed to server IP: `cnr.aybserve.com`

### GitHub
- Repository with admin access
- All secrets configured (see below)

## Server Setup

### Step 1: Install K3s

SSH into your Hetzner server:

```bash
ssh root@cnr.aybserve.com
```

Install K3s (lightweight Kubernetes):

```bash
curl -sfL https://get.k3s.io | sh -

# Wait for K3s to start
systemctl status k3s

# Verify installation
k3s kubectl get nodes
```

### Step 2: Configure Firewall

```bash
# Allow Kubernetes API
ufw allow 6443/tcp

# Allow HTTP/HTTPS
ufw allow 80/tcp
ufw allow 443/tcp

# Verify firewall status
ufw status
```

### Step 3: Get Kubeconfig

```bash
# Copy kubeconfig
sudo cat /etc/rancher/k3s/k3s.yaml > /tmp/k3s-config.yaml

# Replace localhost with your server domain/IP
sudo sed -i 's/127.0.0.1/cnr.aybserve.com/g' /tmp/k3s-config.yaml

# Display the config
cat /tmp/k3s-config.yaml

# Encode to base64 for GitHub Secret
cat /tmp/k3s-config.yaml | base64 -w 0
# Copy this output for GitHub Secret: KUBE_CONFIG
```

### Step 4: Install Nginx Ingress Controller

```bash
kubectl apply -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/main/deploy/static/provider/cloud/deploy.yaml

# Wait for ingress controller to be ready
kubectl wait --namespace ingress-nginx \
  --for=condition=ready pod \
  --selector=app.kubernetes.io/component=controller \
  --timeout=120s
```

### Step 5: Install cert-manager (for SSL)

```bash
# Install cert-manager
kubectl apply -f https://github.com/cert-manager/cert-manager/releases/download/v1.13.0/cert-manager.yaml

# Wait for cert-manager to be ready
kubectl wait --namespace cert-manager \
  --for=condition=ready pod \
  --selector=app.kubernetes.io/instance=cert-manager \
  --timeout=120s
```

### Step 6: Setup Elasticsearch & Kibana

```bash
# Navigate to docker-services directory (or create it)
mkdir -p /opt/clickenrent/docker-services
cd /opt/clickenrent/docker-services

# Copy docker-compose.yml from your repo
# Or create it with the content from docker-services/docker-compose.yml

# Create production environment file
cat > .env << 'EOF'
ELASTIC_PASSWORD=YOUR_STRONG_PASSWORD_HERE
ES_JAVA_OPTS=-Xms1g -Xmx1g
XPACK_SECURITY_ENABLED=true
EOF

# Start Elasticsearch and Kibana
docker-compose up -d elasticsearch kibana

# Check status
docker-compose ps

# View logs
docker-compose logs -f elasticsearch
```

### Step 7: Test Elasticsearch

```bash
# Test connection (from server)
curl -u elastic:YOUR_PASSWORD http://localhost:9200

# Should return cluster info
```

### Step 8: Configure Redis Password (if not done)

If Redis is managed by FastPanel, set a password through the FastPanel interface or:

```bash
redis-cli
CONFIG SET requirepass "YOUR_REDIS_PASSWORD"
CONFIG REWRITE
exit

# Test
redis-cli -a YOUR_REDIS_PASSWORD PING
# Should return: PONG
```

## GitHub Secrets Configuration

Go to: **Repository → Settings → Secrets and variables → Actions → New repository secret**

Add each of these secrets:

### Infrastructure Secrets

| Secret Name | Description | How to Get |
|------------|-------------|------------|
| `KUBE_CONFIG` | Kubernetes config file (base64) | See Step 3 above |
| `DB_HOST` | PostgreSQL host | `cnr.aybserve.com` |
| `DB_USERNAME` | Database user | From FastPanel or `clickenrent_app` |
| `DB_PASSWORD` | Database password | From FastPanel |
| `REDIS_HOST` | Redis host | `cnr.aybserve.com` |
| `REDIS_PORT` | Redis port | `6379` |
| `REDIS_PASSWORD` | Redis password | From Step 8 or FastPanel |
| `ES_URIS` | Elasticsearch URL | `http://cnr.aybserve.com:9200` |
| `ES_USERNAME` | Elasticsearch user | `elastic` |
| `ES_PASSWORD` | Elasticsearch password | From Step 6 |

### Application Secrets

| Secret Name | Value/Source |
|------------|--------------|
| `JWT_SECRET` | Generate: `openssl rand -base64 32` |
| `JWT_EXPIRATION` | `86400000` (24 hours) |
| `JWT_REFRESH_EXPIRATION` | `604800000` (7 days) |
| `LOCK_ENCRYPTION_KEY` | Generate: `openssl rand -hex 16` (32 chars) |
| `SERVICE_AUTH_USERNAME` | `service_payment` |
| `SERVICE_AUTH_PASSWORD` | Generate: `openssl rand -base64 24` |

### OAuth Secrets

| Secret Name | Source |
|------------|--------|
| `GOOGLE_CLIENT_ID` | Google Cloud Console |
| `GOOGLE_CLIENT_SECRET` | Google Cloud Console |
| `APPLE_TEAM_ID` | Apple Developer |
| `APPLE_CLIENT_ID` | Apple Developer |
| `APPLE_KEY_ID` | Apple Developer |
| `APPLE_PRIVATE_KEY` | Apple Developer |

### Payment & Cloud Services

| Secret Name | Source |
|------------|--------|
| `MULTISAFEPAY_API_KEY` | MultiSafePay Dashboard |
| `MULTISAFEPAY_WEBHOOK_SECRET` | Generate: `openssl rand -base64 32` |
| `AZURE_STORAGE_CONNECTION_STRING` | Azure Portal |
| `MAPBOX_API_KEY` | MapBox Account |

## First Deployment

### Method 1: Automatic (via GitHub Actions)

1. **Ensure all GitHub Secrets are configured**

2. **Push to features/ci-cd branch:**
   ```bash
   git checkout -b features/ci-cd
   git add .
   git commit -m "Initial deployment setup"
   git push origin features/ci-cd
   ```

3. **Monitor deployment:**
   - Go to GitHub → Actions tab
   - Watch the workflow run
   - Should complete in 10-15 minutes

4. **Verify on server:**
   ```bash
   # On your local machine with kubectl configured
   export KUBECONFIG=~/.kube/config-hetzner
   
   kubectl get pods -n clickenrent
   kubectl get svc -n clickenrent
   kubectl get ingress -n clickenrent
   ```

### Method 2: Manual Deployment

If you prefer to deploy manually:

```bash
# 1. Build locally
./scripts/build-all.sh --skip-tests

# 2. Build Docker images
./scripts/build-all.sh --docker

# 3. Tag and push to GitHub Container Registry
docker tag clickenrent/gateway:latest ghcr.io/YOUR_USERNAME/backend/gateway:latest
docker push ghcr.io/YOUR_USERNAME/backend/gateway:latest
# Repeat for all services...

# 4. Deploy to Kubernetes
./scripts/deploy.sh clickenrent
```

## Monitoring & Maintenance

### Access Services

- **Gateway API:** http://cnr.aybserve.com
- **Eureka Dashboard:** http://cnr.aybserve.com/eureka
- **Swagger UI:** http://cnr.aybserve.com/swagger-ui.html
- **Health Check:** http://cnr.aybserve.com/actuator/health

### Monitor Pods

```bash
# Watch all pods
kubectl get pods -n clickenrent -w

# View logs for a service
kubectl logs -f deployment/gateway -n clickenrent

# View logs for all replicas
kubectl logs -f deployment/gateway -n clickenrent --all-containers=true

# Get pod details
kubectl describe pod POD_NAME -n clickenrent
```

### Using k9s (Recommended)

```bash
# Install k9s
brew install k9s

# Launch k9s
k9s -n clickenrent

# Keyboard shortcuts:
# - :pods    → View pods
# - :svc     → View services
# - :deploy  → View deployments
# - l        → View logs
# - d        → Describe resource
# - shift+f  → Port forward
# - ctrl+d   → Delete resource
```

### Check Resource Usage

```bash
# Node resources
kubectl top nodes

# Pod resources
kubectl top pods -n clickenrent

# Detailed resource usage
kubectl describe node
```

### Scale Services

```bash
# Scale up gateway
kubectl scale deployment/gateway --replicas=3 -n clickenrent

# Scale down a service
kubectl scale deployment/analytics-service --replicas=1 -n clickenrent

# Auto-scaling (requires metrics server)
kubectl autoscale deployment/gateway --min=2 --max=5 --cpu-percent=80 -n clickenrent
```

## Troubleshooting

### Pods Not Starting

**Check pod status:**
```bash
kubectl get pods -n clickenrent
kubectl describe pod POD_NAME -n clickenrent
```

**Common issues:**
- **ImagePullBackOff:** Image not found in registry
  - Solution: Check image name in deployment YAML
  - Verify GitHub Container Registry permissions
  
- **CrashLoopBackOff:** Application crashing on startup
  - Solution: Check logs with `kubectl logs POD_NAME -n clickenrent`
  - Common causes: database connection issues, missing environment variables

- **Pending:** Not enough resources
  - Solution: Check node resources with `kubectl describe nodes`

### Database Connection Issues

```bash
# Check if database is accessible from cluster
kubectl run -it --rm debug --image=postgres:15 --restart=Never -n clickenrent -- \
  psql -h cnr.aybserve.com -U clickenrent_app -d clickenrent-auth

# Test from a running pod
kubectl exec -it deployment/auth-service -n clickenrent -- \
  curl -v telnet://cnr.aybserve.com:5432
```

### Service Not Accessible

**Check ingress:**
```bash
kubectl get ingress -n clickenrent
kubectl describe ingress clickenrent-ingress -n clickenrent
```

**Check nginx ingress controller:**
```bash
kubectl get pods -n ingress-nginx
kubectl logs -n ingress-nginx deployment/ingress-nginx-controller
```

**Check DNS:**
```bash
# From your local machine
dig cnr.aybserve.com
nslookup cnr.aybserve.com
```

### SSL Certificate Issues

```bash
# Check certificate status
kubectl get certificate -n clickenrent
kubectl describe certificate clickenrent-tls -n clickenrent

# Check cert-manager logs
kubectl logs -n cert-manager deployment/cert-manager

# Manually trigger certificate request
kubectl delete certificate clickenrent-tls -n clickenrent
kubectl apply -f k8s/ingress.yml
```

### High Memory/CPU Usage

```bash
# Check which pods are using most resources
kubectl top pods -n clickenrent --sort-by=memory
kubectl top pods -n clickenrent --sort-by=cpu

# Restart a problematic pod
kubectl rollout restart deployment/SERVICE_NAME -n clickenrent
```

### View All Events

```bash
# Recent cluster events
kubectl get events -n clickenrent --sort-by='.lastTimestamp'

# Watch events in real-time
kubectl get events -n clickenrent --watch
```

## Rollback Procedures

### Rollback Single Service

```bash
# View rollout history
kubectl rollout history deployment/gateway -n clickenrent

# Rollback to previous version
kubectl rollout undo deployment/gateway -n clickenrent

# Rollback to specific revision
kubectl rollout undo deployment/gateway --to-revision=2 -n clickenrent

# Check rollback status
kubectl rollout status deployment/gateway -n clickenrent
```

### Emergency Rollback (All Services)

```bash
#!/bin/bash
# Save as rollback-all.sh

SERVICES=("eureka-server" "gateway" "auth-service" "rental-service" "payment-service" "support-service" "notification-service" "search-service" "analytics-service")

for service in "${SERVICES[@]}"; do
    echo "Rolling back ${service}..."
    kubectl rollout undo deployment/${service} -n clickenrent
done

echo "Waiting for rollback to complete..."
for service in "${SERVICES[@]}"; do
    kubectl rollout status deployment/${service} -n clickenrent
done
```

### Restore from Backup

If you need to restore the entire cluster:

```bash
# 1. Delete namespace
kubectl delete namespace clickenrent

# 2. Redeploy from last known good commit
git checkout LAST_GOOD_COMMIT
git push origin features/ci-cd --force
```

## Maintenance Tasks

### Update Dependencies

```bash
# Update Maven dependencies
mvn versions:display-dependency-updates

# Update a specific dependency in pom.xml
# Then commit and push to trigger deployment
```

### Rotate Secrets

```bash
# Generate new JWT secret
NEW_SECRET=$(openssl rand -base64 32)

# Update GitHub Secret
# Then redeploy:
git commit --allow-empty -m "Trigger redeploy for secret rotation"
git push origin features/ci-cd
```

### Database Migrations

Database migrations run automatically via Flyway on service startup.

**To skip migrations temporarily:**
```bash
kubectl set env deployment/auth-service FLYWAY_MIGRATE=false -n clickenrent
```

### Backup Strategy

**Kubernetes manifests:**
- Already in Git repository

**Database backups:**
```bash
# Create backup script on server
cat > /opt/clickenrent/backup-db.sh << 'EOF'
#!/bin/bash
DATE=$(date +%Y%m%d_%H%M%S)
BACKUP_DIR="/opt/clickenrent/backups"
mkdir -p $BACKUP_DIR

# Backup all databases
pg_dump -h localhost -U clickenrent_app clickenrent-auth > $BACKUP_DIR/auth-${DATE}.sql
pg_dump -h localhost -U clickenrent_app clickenrent-rental > $BACKUP_DIR/rental-${DATE}.sql
# ... repeat for all databases

# Keep only last 7 days
find $BACKUP_DIR -name "*.sql" -mtime +7 -delete
EOF

chmod +x /opt/clickenrent/backup-db.sh

# Add to crontab (daily at 2 AM)
crontab -e
# Add: 0 2 * * * /opt/clickenrent/backup-db.sh
```

## Performance Tuning

### Optimize JVM Settings

Edit deployment YAML to add JVM options:

```yaml
env:
- name: JAVA_OPTS
  value: "-Xms512m -Xmx1g -XX:+UseG1GC -XX:MaxGCPauseMillis=200"
```

### Connection Pool Tuning

Add to ConfigMap:

```yaml
SPRING_DATASOURCE_HIKARI_MAXIMUM_POOL_SIZE: "20"
SPRING_DATASOURCE_HIKARI_MINIMUM_IDLE: "5"
```

## Security Best Practices

1. **Keep secrets in GitHub Secrets only** - Never commit to Git
2. **Use RBAC** - Limit kubectl access
3. **Network Policies** - Restrict pod-to-pod communication
4. **Regular updates** - Keep K3s, images, and dependencies updated
5. **SSL/TLS** - Always use HTTPS (cert-manager handles this)
6. **Database access** - Use dedicated user with minimum privileges

## Support & Resources

- **Kubernetes Documentation:** https://kubernetes.io/docs/
- **K3s Documentation:** https://docs.k3s.io/
- **Spring Boot Actuator:** https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html
- **Nginx Ingress:** https://kubernetes.github.io/ingress-nginx/

## Quick Reference Commands

```bash
# View everything
kubectl get all -n clickenrent

# Restart all services
kubectl rollout restart deployment -n clickenrent

# Delete and recreate namespace (nuclear option)
kubectl delete namespace clickenrent
./scripts/deploy.sh

# Port forward to a service (for debugging)
kubectl port-forward svc/gateway 8080:8080 -n clickenrent

# Execute command in pod
kubectl exec -it deployment/gateway -n clickenrent -- bash

# Copy files from/to pod
kubectl cp clickenrent/POD_NAME:/app/logs/app.log ./local-app.log
```

---

**Last Updated:** 2024
**Maintained by:** ClickEnRent Team
