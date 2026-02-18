# Fresh Deployment Checklist

## ✅ Step 1: Clean Kubernetes Environment

Run on server (`java-testing`):

```bash
# Delete everything in the namespace
kubectl delete all --all -n clickenrent

# Delete configmaps and secrets
kubectl delete configmap --all -n clickenrent
kubectl delete secret --all -n clickenrent

# Optional: Delete and recreate namespace
kubectl delete namespace clickenrent
kubectl create namespace clickenrent

# Verify everything is gone
kubectl get all -n clickenrent
```

---

## ✅ Step 2: Verify Server Prerequisites

### 2.1 PostgreSQL Configuration

```bash
# Check PostgreSQL is running
systemctl status postgresql

# Verify it's listening on all interfaces (not just localhost)
ss -tlnp | grep 5432
# Should show: 0.0.0.0:5432 (not 127.0.0.1:5432)

# Test connection
nc -zv cnr.aybserve.com 5432
# Should show: Connection succeeded
```

**PostgreSQL Config Files:**
- `/etc/postgresql/*/main/postgresql.conf` → `listen_addresses = '*'`
- `/etc/postgresql/*/main/pg_hba.conf` → Allow connections from pod network:
  ```
  host    all             all             10.0.0.0/8              md5
  ```

### 2.2 Redis Configuration

```bash
# Check Redis is running
systemctl status redis

# Test connection
nc -zv cnr.aybserve.com 6379
# Should show: Connection succeeded
```

### 2.3 Elasticsearch Configuration

```bash
# Check Elasticsearch is running
docker ps | grep elasticsearch

# Test connection
curl -u elastic:PASSWORD http://cnr.aybserve.com:9200
```

---

## ✅ Step 3: Verify GitHub Secrets

Go to: https://github.com/YOUR_USERNAME/YOUR_REPO/settings/secrets/actions

Required secrets:

### Database
- [x] `DB_USERNAME` - PostgreSQL username
- [x] `DB_PASSWORD` - PostgreSQL password

### JWT
- [x] `JWT_SECRET` - Long random string (e.g., 64+ chars)
- [x] `JWT_EXPIRATION` - e.g., `3600000` (1 hour in ms)
- [x] `JWT_REFRESH_EXPIRATION` - e.g., `86400000` (24 hours in ms)

### Redis
- [x] `REDIS_PASSWORD` - Redis password (or empty if no auth)

### Elasticsearch
- [x] `ES_PASSWORD` - Elasticsearch password

### OAuth (Google)
- [x] `GOOGLE_CLIENT_ID`
- [x] `GOOGLE_CLIENT_SECRET`

### OAuth (Apple)
- [x] `APPLE_TEAM_ID`
- [x] `APPLE_CLIENT_ID`
- [x] `APPLE_KEY_ID`
- [x] `APPLE_PRIVATE_KEY` - The full private key content

### Payment (MultiSafePay)
- [x] `MULTISAFEPAY_API_KEY`
- [x] `MULTISAFEPAY_WEBHOOK_SECRET`

### Service Authentication
- [x] `SERVICE_AUTH_USERNAME`
- [x] `SERVICE_AUTH_PASSWORD`

### Encryption
- [x] `LOCK_ENCRYPTION_KEY` - 32-byte hex string

### Azure Storage
- [x] `AZURE_STORAGE_CONNECTION_STRING`

### MapBox
- [x] `MAPBOX_API_KEY`

### Kubernetes
- [x] `KUBE_CONFIG` - Base64-encoded kubeconfig file from server

---

## ✅ Step 4: Verify Project Configuration

### 4.1 ConfigMap Settings

File: `k8s/configmap.yml`

Key settings:
- `JPA_DDL_AUTO: "update"` ✅ (Fixed)
- `FLYWAY_MIGRATE: "true"` ✅
- `DB_HOST: "cnr.aybserve.com"` ✅
- `DB_PORT: "5432"` ✅

### 4.2 Service YAMLs

All services have correct env var ordering:
```yaml
- name: DB_HOST
  valueFrom:
    configMapKeyRef:
      name: app-config
      key: DB_HOST
- name: DB_PORT
  valueFrom:
    configMapKeyRef:
      name: app-config
      key: DB_PORT
- name: DB_URL
  value: "jdbc:postgresql://$(DB_HOST):$(DB_PORT)/clickenrent-xxx"
```

✅ All 6 services verified (auth, rental, payment, support, analytics, notification)

---

## ✅ Step 5: Commit and Push Changes

```bash
# On your local machine

# Stage all changes
git add k8s/

# Commit
git commit -m "Fix Kubernetes configuration for fresh deployment

- Set JPA_DDL_AUTO to 'update' to let Hibernate create schema
- DB_HOST/DB_PORT env vars ordered before DB_URL for proper substitution
- Ready for clean deployment"

# Push to trigger CI/CD
git push origin features/ci-cd
```

---

## ✅ Step 6: Monitor Deployment

After pushing, watch the GitHub Actions workflow:
https://github.com/YOUR_USERNAME/YOUR_REPO/actions

Expected timeline:
1. **Build** (~10-15 min) - Maven builds all services
2. **Push images** (~5 min) - Docker images to GitHub Container Registry
3. **Deploy** (~5-10 min) - Kubernetes rollout

### Monitor on Server

```bash
# Watch pods starting
kubectl get pods -n clickenrent -w

# Check logs if any pod fails
kubectl logs -n clickenrent POD_NAME --tail=100

# Check specific service
kubectl logs -n clickenrent -l app=auth-service --tail=50
```

---

## Expected Final State

All pods should show `1/1 Running`:

```
NAME                                    READY   STATUS    RESTARTS   AGE
analytics-service-xxx-xxx               1/1     Running   0          5m
auth-service-xxx-xxx                    1/1     Running   0          5m
eureka-server-xxx-xxx                   1/1     Running   0          5m
gateway-xxx-xxx                         1/1     Running   0          5m
notification-service-xxx-xxx            1/1     Running   0          5m
payment-service-xxx-xxx                 1/1     Running   0          5m
rental-service-xxx-xxx                  1/1     Running   0          5m
search-service-xxx-xxx                  1/1     Running   0          5m
support-service-xxx-xxx                 1/1     Running   0          5m
```

### Test Access

```bash
# Eureka Dashboard
curl http://cnr.aybserve.com/eureka

# API Health Check
curl http://cnr.aybserve.com/api/v1/auth/health

# Swagger UI
curl http://cnr.aybserve.com/swagger-ui.html
```

--- 

## Troubleshooting

### If pods are CrashLoopBackOff:


```bash
# Get logs
kubectl logs -n clickenrent POD_NAME --previous

# Common issues:
# 1. Database connection refused → Check PostgreSQL listen_addresses
# 2. relation "xxx" does not exist → Hibernate DDL issue, check JPA_DDL_AUTO
# 3. Port already in use → Clean old deployments
# 4. Out of memory → Increase pod memory limits
```

### If CI/CD fails:

1. Check GitHub Secrets are all set
2. Verify KUBE_CONFIG secret is valid
3. Check build logs for Maven errors
4. Verify Docker registry permissions
