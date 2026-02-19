# Accessing Prometheus & Grafana

Due to nginx ingress controller security restrictions (server-snippets disabled), monitoring tools are accessed via `kubectl port-forward` instead of public URLs.

## Access Prometheus

```bash
kubectl port-forward -n clickenrent svc/prometheus 9090:9090
```

Then open in your browser: **http://localhost:9090**

## Access Grafana

```bash
kubectl port-forward -n clickenrent svc/grafana 3000:3000
```

Then open in your browser: **http://localhost:3000**

### Grafana Login
- **Username**: `admin`
- **Password**: Value from your `GRAFANA_ADMIN_PASSWORD` GitHub secret

## Keep Port-Forward Running in Background

### Option 1: Terminal Tab
Run port-forward in a separate terminal tab and keep it open.

### Option 2: Background Process (Linux/Mac)
```bash
# Prometheus
nohup kubectl port-forward -n clickenrent svc/prometheus 9090:9090 > /dev/null 2>&1 &

# Grafana
nohup kubectl port-forward -n clickenrent svc/grafana 3000:3000 > /dev/null 2>&1 &

# List background port-forwards
ps aux | grep "port-forward"

# Kill when done
kill <PID>
```

### Option 3: Use k9s
```bash
k9s -n clickenrent
# Navigate to services
# Select prometheus or grafana
# Press Shift+F to port-forward
```

## Alternative: Enable Ingress Access

If you have cluster admin access and want to enable ingress access, you need to:

### 1. Enable Server Snippets in Nginx Ingress Controller

Edit the nginx ingress controller configmap:

```bash
kubectl edit configmap ingress-nginx-controller -n ingress-nginx
```

Add:
```yaml
data:
  allow-snippet-annotations: "true"
```

Then restart the controller:
```bash
kubectl rollout restart deployment ingress-nginx-controller -n ingress-nginx
```

### 2. Apply the Full Ingress Configuration

Once snippets are enabled, you can use the advanced ingress configuration with path-specific basic auth and rewrites.

## Quick Reference

| Service | Local URL | Credentials |
|---------|-----------|-------------|
| Prometheus | http://localhost:9090 | None (internal) |
| Grafana | http://localhost:3000 | admin / GRAFANA_ADMIN_PASSWORD |
| Application | http://cnr.aybserve.com | Your JWT tokens |

## Grafana Dashboards

Once logged into Grafana:

1. Go to **Dashboards** → **Browse**
2. You'll see 3 pre-configured dashboards:
   - **ClickEnRent - Services Overview**: Request rates, errors, latency, availability
   - **ClickEnRent - JVM Metrics**: Memory, GC, threads, CPU
   - **ClickEnRent - HTTP Metrics**: Endpoints, status codes, slowest routes

## Troubleshooting

### Port Already in Use
```bash
# Find process using port 9090
lsof -i :9090
# Kill it
kill -9 <PID>
```

### Connection Refused
```bash
# Check if pods are running
kubectl get pods -n clickenrent | grep -E 'prometheus|grafana'

# Check pod logs
kubectl logs -n clickenrent deployment/prometheus
kubectl logs -n clickenrent deployment/grafana
```

### Grafana Shows "No Data"
1. Check Prometheus datasource in Grafana: Configuration → Data Sources
2. Verify Prometheus is scraping metrics: http://localhost:9090/targets
3. All services should show as "UP"
