# Prometheus & Grafana Monitoring Setup

This directory contains the Prometheus and Grafana monitoring configuration for ClickEnRent microservices.

## Components

### 1. Persistent Storage
- **File**: `persistent-volumes.yml`
- Prometheus: 30GB for 30 days of metrics retention
- Grafana: 10GB for dashboards and configurations
- Uses K3s local-path storage provisioner

### 2. Prometheus
- **Files**: `prometheus-config.yml`, `prometheus-deployment.yml`
- Scrapes metrics from all services every 15 seconds
- Includes alerting rules for common issues
- Accessible at: `http://cnr.aybserve.com/prometheus`

### 3. Grafana
- **Files**: `grafana-deployment.yml`, `grafana-dashboards.yml`
- Pre-configured with 3 dashboards:
  - Services Overview
  - JVM Metrics
  - HTTP Metrics
- Accessible at: `http://cnr.aybserve.com/grafana`

### 4. Security
- **File**: `auth-secret.yml`
- Basic authentication for Prometheus and Grafana
- Separate Grafana admin login

### 5. Ingress
- **File**: `ingress-monitoring.yml`
- Routes for `/prometheus` and `/grafana`
- Basic auth enabled

## Required GitHub Secrets

Add these secrets to your GitHub repository:

1. **GRAFANA_ADMIN_PASSWORD**
   ```bash
   # Generate a strong password
   openssl rand -base64 24
   ```

2. **MONITORING_AUTH_USERNAME**
   ```
   monitoring  # or any username you prefer
   ```

3. **MONITORING_AUTH_PASSWORD**
   ```bash
   # Generate a strong password
   openssl rand -base64 24
   ```

## Verification Steps

After deployment, verify the monitoring stack:

### 1. Check Pod Status
```bash
kubectl get pods -n clickenrent | grep -E 'prometheus|grafana'
```

Expected output:
```
prometheus-xxxxx     1/1     Running   0          2m
grafana-xxxxx        1/1     Running   0          2m
```

### 2. Check PVCs
```bash
kubectl get pvc -n clickenrent
```

Expected output:
```
NAME                 STATUS   VOLUME       CAPACITY   ACCESS MODES
prometheus-storage   Bound    pvc-xxxxx    30Gi       RWO
grafana-storage      Bound    pvc-xxxxx    10Gi       RWO
```

### 3. Verify Prometheus Scraping
```bash
# Port-forward to Prometheus
kubectl port-forward -n clickenrent svc/prometheus 9090:9090
```

Then visit `http://localhost:9090/targets` in your browser. All services should show as "UP".

### 4. Access Grafana Dashboards

1. Visit `http://cnr.aybserve.com/grafana`
2. Enter basic auth credentials (MONITORING_AUTH_USERNAME/PASSWORD)
3. Login with Grafana credentials (admin / GRAFANA_ADMIN_PASSWORD)
4. Navigate to Dashboards → Browse
5. You should see:
   - ClickEnRent - Services Overview
   - ClickEnRent - JVM Metrics
   - ClickEnRent - HTTP Metrics

### 5. Test Metrics Query

In Prometheus, run this query:
```promql
sum(rate(http_server_requests_seconds_count[5m])) by (service)
```

You should see request rates for all services.

### 6. Check Alerting Rules
```bash
# Check if alerting rules are loaded
kubectl exec -n clickenrent deployment/prometheus -- promtool check rules /etc/prometheus/alerts.yml
```

## Troubleshooting

### Prometheus Not Scraping Services

1. Check if services have Prometheus annotations:
```bash
kubectl get deployment gateway -n clickenrent -o yaml | grep -A 3 annotations
```

Should show:
```yaml
annotations:
  prometheus.io/scrape: "true"
  prometheus.io/path: "/actuator/prometheus"
  prometheus.io/port: "8080"
```

2. Check Prometheus logs:
```bash
kubectl logs -n clickenrent deployment/prometheus
```

### Grafana Can't Connect to Prometheus

1. Check if Prometheus service is running:
```bash
kubectl get svc prometheus -n clickenrent
```

2. Test connection from Grafana pod:
```bash
kubectl exec -n clickenrent deployment/grafana -- curl -s http://prometheus:9090/-/healthy
```

Should return: `Prometheus is Healthy.`

### Basic Auth Not Working

1. Check if secret exists:
```bash
kubectl get secret monitoring-basic-auth -n clickenrent
```

2. Verify ingress annotations:
```bash
kubectl get ingress monitoring-ingress -n clickenrent -o yaml | grep auth
```

### Dashboards Not Showing Data

1. Verify datasource in Grafana:
   - Go to Configuration → Data Sources
   - Check if Prometheus datasource is configured
   - Click "Test" to verify connection

2. Check if metrics are being scraped:
   - Go to Prometheus: `http://cnr.aybserve.com/prometheus`
   - Run query: `up{job="kubernetes-pods"}`
   - Should show all services with value `1`

## Key Metrics to Monitor

### RED Metrics (for each service)
- **Rate**: Requests per second
  ```promql
  sum(rate(http_server_requests_seconds_count[5m])) by (service)
  ```

- **Errors**: Error rate
  ```promql
  sum(rate(http_server_requests_seconds_count{status=~"5.."}[5m])) by (service)
  ```

- **Duration**: Response time (p95)
  ```promql
  histogram_quantile(0.95, sum(rate(http_server_requests_seconds_bucket[5m])) by (service, le))
  ```

### JVM Metrics
- **Heap Memory Usage**
  ```promql
  jvm_memory_used_bytes{area="heap"} / jvm_memory_max_bytes{area="heap"}
  ```

- **GC Pause Time**
  ```promql
  rate(jvm_gc_pause_seconds_sum[5m])
  ```

- **Thread Count**
  ```promql
  jvm_threads_live_threads
  ```

### Infrastructure Metrics
- **Pod Restarts**
  ```promql
  kube_pod_container_status_restarts_total
  ```

- **CPU Usage**
  ```promql
  process_cpu_usage
  ```

## Alert Rules

The following alerts are configured:

1. **HighErrorRate** - Triggers when 5xx error rate > 5% for 5 minutes
2. **HighLatency** - Triggers when p95 latency > 1 second for 5 minutes
3. **ServiceDown** - Triggers when service stops reporting metrics for 2 minutes
4. **HighMemoryUsage** - Triggers when heap usage > 90% for 10 minutes
5. **HighCPUUsage** - Triggers when CPU usage > 80% for 10 minutes
6. **PodRestartingFrequently** - Triggers when pod restarts in 15 minutes
7. **TooManyRequests** - Info alert when service receives > 1000 req/s

## Maintenance

### Backup Grafana Dashboards
```bash
# Export all dashboards
kubectl exec -n clickenrent deployment/grafana -- \
  grafana-cli --homepath /usr/share/grafana admin export-dashboard > dashboards-backup.json
```

### Check Prometheus Storage Usage
```bash
kubectl exec -n clickenrent deployment/prometheus -- \
  du -sh /prometheus
```

### Restart Monitoring Stack
```bash
kubectl rollout restart deployment/prometheus -n clickenrent
kubectl rollout restart deployment/grafana -n clickenrent
```

### Update Retention Period
Edit `prometheus-deployment.yml` and change:
```yaml
--storage.tsdb.retention.time=30d  # Change to desired retention
```

Then apply:
```bash
kubectl apply -f k8s/monitoring/prometheus-deployment.yml
```

## URLs

- **Prometheus**: http://cnr.aybserve.com/prometheus
- **Grafana**: http://cnr.aybserve.com/grafana

Both require basic authentication with credentials from GitHub secrets.
