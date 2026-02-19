# Prometheus & Grafana Monitoring - Setup Complete

Congratulations! The Prometheus and Grafana monitoring stack has been configured for your ClickEnRent microservices platform.

## 📋 What Was Implemented

### 1. Persistent Storage
✅ PersistentVolumeClaims created:
- Prometheus: 30GB (30 days retention)
- Grafana: 10GB (dashboards & config)
- Location: `/var/lib/rancher/k3s/storage/` on your Hetzner server

### 2. Prometheus Monitoring
✅ Features implemented:
- Auto-discovery of all 9 microservices via Kubernetes annotations
- 15-second scrape interval
- 7 pre-configured alerting rules
- ServiceAccount with RBAC permissions
- Resource limits: 512Mi-2Gi RAM, 250m-1000m CPU

✅ Alert Rules:
- High error rate (>5% 5xx errors)
- High latency (p95 >1s)
- Service down detection
- High memory usage (>90% heap)
- High CPU usage (>80%)
- Pod restart detection
- High traffic alerts

### 3. Grafana Dashboards
✅ Pre-configured dashboards:
- **Services Overview**: Request rates, error rates, latency (p95/p99), service availability
- **JVM Metrics**: Heap memory, GC pauses, thread counts, CPU usage
- **HTTP Metrics**: Requests by endpoint, status codes, slowest endpoints

### 4. Security
✅ Multi-layer security:
- Basic authentication for Prometheus/Grafana access
- Separate Grafana admin credentials
- Dedicated Kubernetes RBAC roles
- Non-root containers with security contexts

### 5. Service Integration
✅ All services configured with Prometheus annotations:
- Gateway (8080)
- Eureka Server (8761)
- Auth Service (8081)
- Rental Service (8082)
- Support Service (8083)
- Payment Service (8084)
- Notification Service (8085)
- Search Service (8086)
- Analytics Service (8087)

## 🚀 Next Steps - Before Deployment

### Step 1: Add GitHub Secrets

Go to your repository: **Settings → Secrets and variables → Actions**

Add these 3 new secrets:

| Secret Name | How to Generate | Example Value |
|------------|-----------------|---------------|
| `GRAFANA_ADMIN_PASSWORD` | `openssl rand -base64 24` | `xK9mP4vL2nQ8wR5yT7uZ1aB6cD3eF` |
| `MONITORING_AUTH_USERNAME` | Choose username | `monitoring` or `admin` |
| `MONITORING_AUTH_PASSWORD` | `openssl rand -base64 24` | `zY8xW6vU4tS2rQ0pN9mL7kJ5hG3fD` |

### Step 2: Deploy to Production

Once secrets are added, deploy:

```bash
git add .
git commit -m "Add Prometheus and Grafana monitoring"
git push origin features/ci-cd
```

The CI/CD pipeline will automatically:
1. Generate basic auth credentials
2. Create monitoring secrets
3. Deploy Prometheus
4. Deploy Grafana
5. Configure ingress with basic auth
6. Wait for monitoring stack to be ready

## 🔍 Post-Deployment Verification

### 1. Check Deployment Status

```bash
# Check if monitoring pods are running
kubectl get pods -n clickenrent | grep -E 'prometheus|grafana'

# Expected output:
# prometheus-xxxxx     1/1     Running   0          2m
# grafana-xxxxx        1/1     Running   0          2m
```

### 2. Verify Storage

```bash
kubectl get pvc -n clickenrent
```

Should show both `prometheus-storage` (30Gi) and `grafana-storage` (10Gi) as Bound.

### 3. Access Prometheus

Visit: **http://cnr.aybserve.com/prometheus**

- Enter basic auth credentials (MONITORING_AUTH_USERNAME/PASSWORD)
- Click "Status" → "Targets"
- All services should show state "UP"

### 4. Access Grafana

Visit: **http://cnr.aybserve.com/grafana**

1. Enter basic auth credentials first
2. Login with Grafana credentials:
   - Username: `admin`
   - Password: Your `GRAFANA_ADMIN_PASSWORD`
3. Navigate to "Dashboards" → "Browse"
4. Open "ClickEnRent - Services Overview"

You should see live metrics from all services!

## 📊 Using the Monitoring Stack

### Quick Start Guide

1. **Check Service Health**
   - Open Grafana → ClickEnRent - Services Overview
   - View request rates, error rates, and latency for all services

2. **Investigate Performance Issues**
   - Open ClickEnRent - JVM Metrics dashboard
   - Check heap memory usage and GC pause times
   - Look for memory leaks or excessive GC

3. **Analyze HTTP Traffic**
   - Open ClickEnRent - HTTP Metrics dashboard
   - See which endpoints are slowest
   - Check status code distribution

4. **Create Custom Queries**
   - Go to Prometheus: http://cnr.aybserve.com/prometheus
   - Enter PromQL queries like:
     ```promql
     sum(rate(http_server_requests_seconds_count[5m])) by (service)
     ```

### Example Queries

**Total request rate across all services:**
```promql
sum(rate(http_server_requests_seconds_count[5m]))
```

**Error rate per service:**
```promql
sum(rate(http_server_requests_seconds_count{status=~"5.."}[5m])) by (service)
```

**95th percentile latency:**
```promql
histogram_quantile(0.95, sum(rate(http_server_requests_seconds_bucket[5m])) by (service, le))
```

**Memory usage percentage:**
```promql
(jvm_memory_used_bytes{area="heap"} / jvm_memory_max_bytes{area="heap"}) * 100
```

## 🎯 Key Features

### Auto-Discovery
Services are automatically discovered by Prometheus using Kubernetes pod annotations. No manual configuration needed when adding new services!

### Alerting
While alerts are configured, they currently log to Prometheus. To receive notifications:
1. Configure AlertManager (optional)
2. Set up notification channels in Grafana
3. Connect to Slack, email, or PagerDuty

### Data Retention
- Prometheus retains 30 days of metrics
- Grafana stores dashboards persistently
- Data survives pod restarts

### Resource Efficiency
- Prometheus: Optimized for minimal overhead
- Grafana: Lightweight configuration
- Both have resource limits to prevent cluster issues

## 🛠 Maintenance

### View Prometheus Storage Usage
```bash
kubectl exec -n clickenrent deployment/prometheus -- du -sh /prometheus
```

### Restart Monitoring Stack
```bash
kubectl rollout restart deployment/prometheus -n clickenrent
kubectl rollout restart deployment/grafana -n clickenrent
```

### Update Dashboards
Edit `k8s/monitoring/grafana-dashboards.yml` and apply:
```bash
kubectl apply -f k8s/monitoring/grafana-dashboards.yml
kubectl rollout restart deployment/grafana -n clickenrent
```

### Backup Grafana Data
```bash
# Copy Grafana database
kubectl exec -n clickenrent deployment/grafana -- tar czf - /var/lib/grafana > grafana-backup.tar.gz
```

## 📁 File Structure

```
k8s/monitoring/
├── README.md                      # Detailed technical documentation
├── persistent-volumes.yml         # PVC for Prometheus & Grafana
├── prometheus-config.yml          # Scrape config & alert rules
├── prometheus-deployment.yml      # Prometheus deployment & RBAC
├── grafana-deployment.yml         # Grafana deployment & datasource
├── grafana-dashboards.yml         # Pre-configured dashboards
├── auth-secret.yml               # Basic auth secret (template)
└── ingress-monitoring.yml         # Ingress with basic auth
```

## 🔐 Security Notes

1. **Basic Auth**: Protects Prometheus and Grafana from unauthorized access
2. **Grafana Login**: Additional layer of security for Grafana
3. **RBAC**: Prometheus has minimal required permissions
4. **Non-Root**: Both containers run as non-root users
5. **Secrets**: All credentials stored in Kubernetes secrets

## 📖 Additional Resources

- **Detailed Verification**: See `k8s/monitoring/README.md`
- **PromQL Guide**: https://prometheus.io/docs/prometheus/latest/querying/basics/
- **Grafana Docs**: https://grafana.com/docs/grafana/latest/

## 🆘 Troubleshooting

### Prometheus Not Showing Targets
- Check if service pods have annotations: `kubectl get deployment gateway -n clickenrent -o yaml | grep prometheus.io`
- Verify Prometheus has RBAC permissions: `kubectl get clusterrolebinding prometheus`

### Grafana Shows "No Data"
- Check Prometheus datasource: Grafana → Configuration → Data Sources
- Test Prometheus connection: Click "Save & Test"
- Verify services are exposing metrics: `curl http://gateway:8080/actuator/prometheus`

### Can't Access Monitoring URLs
- Verify ingress exists: `kubectl get ingress monitoring-ingress -n clickenrent`
- Check basic auth secret: `kubectl get secret monitoring-basic-auth -n clickenrent`
- Test without ingress: `kubectl port-forward svc/prometheus 9090:9090 -n clickenrent`

## ✅ Checklist

Before considering the setup complete, verify:

- [ ] All 3 GitHub secrets added
- [ ] CI/CD pipeline deployed successfully
- [ ] Prometheus pod is Running
- [ ] Grafana pod is Running
- [ ] Both PVCs are Bound
- [ ] Prometheus shows all 9 services as UP
- [ ] Grafana login works
- [ ] All 3 dashboards display data
- [ ] Basic auth works on monitoring URLs

---

**Status**: ✅ Implementation Complete - Ready for Deployment

**Next Action**: Add GitHub secrets and push to `features/ci-cd` branch
