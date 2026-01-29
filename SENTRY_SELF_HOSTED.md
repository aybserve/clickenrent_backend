# Self-Hosted Sentry Setup Guide

Complete guide for deploying your own Sentry instance for ClickEnRent microservices.

## Table of Contents

1. [Why Self-Hosted Sentry?](#why-self-hosted-sentry)
2. [Prerequisites](#prerequisites)
3. [Installation](#installation)
4. [Configuration](#configuration)
5. [Creating Projects](#creating-projects)
6. [Obtaining DSN Values](#obtaining-dsn-values)
7. [Configuring ClickEnRent Services](#configuring-clickenrent-services)
8. [Testing the Integration](#testing-the-integration)
9. [Maintenance & Updates](#maintenance--updates)
10. [Troubleshooting](#troubleshooting)

---

## Why Self-Hosted Sentry?

**Benefits:**
- ✅ **100% Free** - No monthly costs, unlimited errors/performance data
- ✅ **Full Control** - Your data stays on your infrastructure
- ✅ **No Limits** - Unlimited events, users, and retention
- ✅ **Customizable** - Modify Sentry to fit your needs
- ✅ **Privacy** - Sensitive data never leaves your servers

**Requirements:**
- Server with 4GB+ RAM (8GB recommended for production)
- Docker & Docker Compose
- 20GB+ disk space
- Domain name (optional, but recommended)

---

## Prerequisites

### Server Requirements

**Minimum Specs:**
- CPU: 2 cores
- RAM: 4GB (8GB recommended)
- Disk: 20GB free space
- OS: Ubuntu 20.04+ / Debian 11+ / CentOS 8+

**Software Required:**
```bash
# Install Docker
curl -fsSL https://get.docker.com -o get-docker.sh
sh get-docker.sh

# Install Docker Compose
sudo curl -L "https://github.com/docker/compose/releases/download/v2.24.0/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose

# Verify installation
docker --version
docker-compose --version
```

### Domain Setup (Optional but Recommended)

If you want to use a custom domain like `sentry.yourdomain.com`:

1. Create an A record pointing to your server IP:
   ```
   sentry.yourdomain.com → 46.224.148.235
   ```

2. Or use your main domain with a port:
   ```
   yourdomain.com:9000
   ```

---

## Installation

### Step 1: Download Sentry

```bash
# Create directory for Sentry
cd ~
git clone https://github.com/getsentry/self-hosted.git sentry
cd sentry

# Checkout the latest stable version
git checkout 24.1.0
```

### Step 2: Run Installation Script

```bash
# Run the install script (takes 5-10 minutes)
sudo ./install.sh

# You'll be prompted to create a superuser account
# Username: admin
# Email: your-email@example.com
# Password: [choose a strong password]
```

**What this does:**
- Installs all required services (PostgreSQL, Redis, Kafka, etc.)
- Generates configuration files
- Sets up initial database
- Creates admin user

### Step 3: Start Sentry

```bash
# Start all Sentry services
docker-compose up -d

# Check if all containers are running
docker-compose ps

# You should see 20+ containers running:
# - sentry-web
# - sentry-worker
# - sentry-cron
# - postgres
# - redis
# - kafka
# - nginx
# ... and more
```

### Step 4: Access Sentry

Open your browser and navigate to:
```
http://YOUR_SERVER_IP:9000
```

Or if you configured a domain:
```
http://sentry.yourdomain.com:9000
```

**First Login:**
- Username: `admin` (or what you chose)
- Password: [your password]

---

## Configuration

### Basic Configuration (config.yml)

Edit the configuration file:

```bash
cd ~/sentry
nano config.yml
```

**Key settings to configure:**

```yaml
# System URL - IMPORTANT: Change this to your actual URL
system.url-prefix: 'http://YOUR_SERVER_IP:9000'
# Or if using domain:
# system.url-prefix: 'http://sentry.yourdomain.com:9000'

# Email configuration (optional, for alerts)
mail.backend: 'smtp'
mail.host: 'smtp.gmail.com'
mail.port: 587
mail.username: 'your-email@gmail.com'
mail.password: 'your-app-password'
mail.use-tls: true
mail.from: 'sentry@yourdomain.com'

# Redis configuration (already set by install script)
redis.clusters:
  default:
    hosts:
      0:
        host: redis
        port: 6379
```

**Apply configuration changes:**

```bash
# Restart Sentry services
docker-compose restart web worker cron
```

### Optional: Configure SSL/HTTPS with Nginx

If you want HTTPS (recommended for production):

```bash
# Install Certbot for Let's Encrypt SSL
sudo apt install certbot python3-certbot-nginx

# Get SSL certificate
sudo certbot --nginx -d sentry.yourdomain.com

# Certbot will automatically configure Nginx
```

**Update Sentry URL:**

```bash
nano config.yml
# Change to:
system.url-prefix: 'https://sentry.yourdomain.com'

docker-compose restart web worker cron
```

### Configure Retention & Cleanup

Edit retention settings to prevent disk from filling up:

```bash
nano sentry.conf.py
```

Add these settings:

```python
# Event retention (days)
SENTRY_EVENT_RETENTION_DAYS = 90

# Delete old data automatically
SENTRY_CLEANUP_DAYS = 90

# Performance data retention
SENTRY_PERFORMANCE_RETENTION_DAYS = 30
```

**Set up automatic cleanup cron job:**

```bash
# The sentry-cleanup container handles this automatically
# Verify it's running:
docker-compose ps | grep cleanup
```

---

## Creating Projects

### Via Web UI

1. **Log in to Sentry**: http://YOUR_SERVER_IP:9000

2. **Create Organization** (if not exists):
   - Click "Create Organization"
   - Name: `ClickEnRent`
   - Click "Create Organization"

3. **Create Projects** for each microservice:

| Project Name | Platform | Description |
|--------------|----------|-------------|
| `clickenrent-auth` | Java/Spring Boot | Authentication & Users |
| `clickenrent-rental` | Java/Spring Boot | Bikes & Rentals |
| `clickenrent-payment` | Java/Spring Boot | Payment Processing |
| `clickenrent-support` | Java/Spring Boot | Customer Support |
| `clickenrent-notification` | Java/Spring Boot | Push Notifications |
| `clickenrent-search` | Java/Spring Boot | Elasticsearch Search |
| `clickenrent-gateway` | Java/Spring Boot | API Gateway |

**For each project:**

1. Click **"Create Project"**
2. Select **"Java"** or **"Spring Boot"**
3. Enter project name (e.g., `clickenrent-auth`)
4. Choose alert frequency: **"Alert on every new issue"**
5. Click **"Create Project"**

### Via CLI (Alternative)

```bash
# Access Sentry CLI container
docker-compose run --rm web createuser
```

---

## Obtaining DSN Values

### Step 1: Get DSN for Each Project

1. Navigate to **Settings** → **Projects**
2. Click on a project (e.g., `clickenrent-auth`)
3. Go to **Client Keys (DSN)** in the left sidebar
4. Copy the **DSN** value

**Example DSN format for self-hosted:**
```
http://PUBLIC_KEY@YOUR_SERVER_IP:9000/PROJECT_ID
```

Or with domain:
```
https://PUBLIC_KEY@sentry.yourdomain.com/PROJECT_ID
```

### Step 2: Save All DSN Values

Create a temporary file to store your DSN values:

```bash
# Example DSNs (these are your actual values from Sentry UI)
SENTRY_DSN_AUTH=http://abc123def456@46.224.148.235:9000/1
SENTRY_DSN_RENTAL=http://ghi789jkl012@46.224.148.235:9000/2
SENTRY_DSN_PAYMENT=http://mno345pqr678@46.224.148.235:9000/3
SENTRY_DSN_SUPPORT=http://stu901vwx234@46.224.148.235:9000/4
SENTRY_DSN_NOTIFICATION=http://yza567bcd890@46.224.148.235:9000/5
SENTRY_DSN_SEARCH=http://efg123hij456@46.224.148.235:9000/6
SENTRY_DSN_GATEWAY=http://klm789nop012@46.224.148.235:9000/7
```

---

## Configuring ClickEnRent Services

### Step 1: Update .env File

On your ClickEnRent backend server:

```bash
cd /path/to/backend
nano .env
```

**Add your self-hosted Sentry DSN values:**

```bash
# ========================================
# SENTRY ERROR TRACKING (SELF-HOSTED)
# ========================================
# Replace with your actual DSN values from Sentry UI
SENTRY_DSN_AUTH=http://YOUR_PUBLIC_KEY@YOUR_SENTRY_SERVER:9000/1
SENTRY_DSN_RENTAL=http://YOUR_PUBLIC_KEY@YOUR_SENTRY_SERVER:9000/2
SENTRY_DSN_SUPPORT=http://YOUR_PUBLIC_KEY@YOUR_SENTRY_SERVER:9000/3
SENTRY_DSN_PAYMENT=http://YOUR_PUBLIC_KEY@YOUR_SENTRY_SERVER:9000/4
SENTRY_DSN_NOTIFICATION=http://YOUR_PUBLIC_KEY@YOUR_SENTRY_SERVER:9000/5
SENTRY_DSN_SEARCH=http://YOUR_PUBLIC_KEY@YOUR_SENTRY_SERVER:9000/6
SENTRY_DSN_GATEWAY=http://YOUR_PUBLIC_KEY@YOUR_SENTRY_SERVER:9000/7

# Sentry Environment
SENTRY_ENVIRONMENT=production

# Sentry Traces Sample Rate (1.0 = 100%)
SENTRY_TRACES_SAMPLE_RATE=1.0
```

### Step 2: Network Considerations

**If Sentry is on the same server as ClickEnRent:**
- Use `localhost:9000` or `127.0.0.1:9000` in DSN
- Faster, no external network traffic

**If Sentry is on a different server:**
- Use server IP or domain in DSN
- Ensure firewall allows port 9000 (or your configured port)
- Consider using internal network/VPN for better security

**Firewall Configuration:**

```bash
# If on same server - no changes needed

# If on different server - open Sentry port:
sudo ufw allow 9000/tcp
sudo ufw reload
```

### Step 3: Restart ClickEnRent Services

```bash
# If using Docker Compose
docker-compose restart

# Or restart individual services
docker-compose restart auth-service rental-service payment-service
```

---

## Testing the Integration

### Step 1: Trigger Test Errors

After restarting services with Sentry DSN configured:

```bash
# Test auth-service error tracking
curl -X GET http://localhost:8080/api/v1/users/99999999

# This should trigger a "User not found" error
# Error should appear in Sentry within seconds
```

### Step 2: Check Sentry Dashboard

1. Log in to Sentry: http://YOUR_SERVER_IP:9000
2. Go to **Issues**
3. You should see the error appear
4. Click on the error to see:
   - Full stack trace
   - Request details
   - Tenant/company context
   - Environment info

### Step 3: Test Performance Monitoring

```bash
# Make API requests to generate performance data
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"Test123"}'
```

Check **Performance** tab in Sentry:
- Transaction: `POST /api/v1/auth/login`
- Duration, throughput metrics
- Database query spans

### Step 4: Verify All Services

Test each service sends data to Sentry:

```bash
# Check Sentry logs
cd ~/sentry
docker-compose logs -f web | grep "Accepted event"

# You should see log entries when errors are sent:
# [info] Accepted event for project_id=1 (auth-service)
```

---

## Maintenance & Updates

### Monitoring Sentry Health

```bash
cd ~/sentry

# Check container status
docker-compose ps

# View logs
docker-compose logs -f web worker

# Check resource usage
docker stats
```

### Disk Space Management

Sentry can consume significant disk space over time:

```bash
# Check disk usage
df -h

# Check Sentry database size
docker-compose exec postgres psql -U postgres -c "SELECT pg_size_pretty(pg_database_size('postgres'));"

# Manually trigger cleanup (removes old data)
docker-compose run --rm web cleanup --days 90
```

### Backup Strategy

**Backup PostgreSQL Database:**

```bash
# Create backup directory
mkdir -p ~/sentry-backups

# Backup database
docker-compose exec -T postgres pg_dump -U postgres postgres > ~/sentry-backups/sentry-backup-$(date +%Y%m%d).sql

# Compress backup
gzip ~/sentry-backups/sentry-backup-$(date +%Y%m%d).sql
```

**Automated Backup Script:**

```bash
nano ~/sentry-backup.sh
```

```bash
#!/bin/bash
cd ~/sentry
BACKUP_DIR=~/sentry-backups
DATE=$(date +%Y%m%d)

# Create backup
docker-compose exec -T postgres pg_dump -U postgres postgres | gzip > $BACKUP_DIR/sentry-backup-$DATE.sql.gz

# Keep only last 30 days
find $BACKUP_DIR -name "sentry-backup-*.sql.gz" -mtime +30 -delete

echo "Backup completed: sentry-backup-$DATE.sql.gz"
```

```bash
chmod +x ~/sentry-backup.sh

# Add to crontab (daily at 2 AM)
crontab -e
# Add line:
0 2 * * * /root/sentry-backup.sh
```

### Updating Sentry

```bash
cd ~/sentry

# Stop services
docker-compose down

# Backup current version
cp -r ~/sentry ~/sentry-backup-$(date +%Y%m%d)

# Pull latest version
git fetch --tags
git checkout 24.2.0  # or latest version

# Run upgrade
./install.sh --skip-user-creation

# Start services
docker-compose up -d

# Check logs for errors
docker-compose logs -f web
```

---

## Troubleshooting

### Issue: Sentry containers not starting

**Check logs:**
```bash
docker-compose logs web worker
```

**Common causes:**
- Insufficient memory (need 4GB+)
- Port 9000 already in use
- Docker daemon not running

**Solution:**
```bash
# Free up memory
sudo systemctl restart docker

# Check port availability
sudo lsof -i :9000

# Restart Sentry
docker-compose restart
```

### Issue: Events not appearing in Sentry

**Verify DSN:**
```bash
# Check ClickEnRent service logs
docker-compose logs auth-service | grep -i sentry

# Test DSN connectivity from app server
curl http://YOUR_SENTRY_SERVER:9000/api/0/

# Should return: {"detail":"Authentication credentials were not provided."}
```

**Check Sentry is receiving data:**
```bash
cd ~/sentry
docker-compose logs -f web | grep "Accepted"
```

### Issue: High disk usage

**Clean up old data:**
```bash
cd ~/sentry

# Run cleanup manually
docker-compose run --rm web cleanup --days 30

# Check database size
docker-compose exec postgres psql -U postgres -c "SELECT pg_size_pretty(pg_database_size('postgres'));"

# Vacuum database
docker-compose exec postgres vacuumdb -U postgres -d postgres -f -z
```

### Issue: Sentry web UI slow

**Scale workers:**

```bash
nano docker-compose.override.yml
```

```yaml
version: '3.4'
services:
  worker:
    deploy:
      replicas: 4  # Increase from default 1
```

```bash
docker-compose up -d --scale worker=4
```

### Issue: Email alerts not working

**Test email configuration:**

```bash
# Access Sentry shell
docker-compose run --rm web shell

# In Python shell:
>>> from sentry.utils.email import send_mail
>>> send_mail('Test', 'This is a test email', 'sentry@yourdomain.com', ['your-email@example.com'])
```

**Check SMTP settings** in `config.yml`

---

## Security Best Practices

### 1. Change Default Ports

Edit `docker-compose.yml`:

```yaml
services:
  nginx:
    ports:
      - "9100:9000"  # Use different port
```

### 2. Enable Authentication

Sentry requires login by default, but ensure:
- Strong admin password
- Enable 2FA for admin users

### 3. Restrict Network Access

```bash
# Allow only your app server to access Sentry
sudo ufw allow from YOUR_APP_SERVER_IP to any port 9000

# Or use internal network only
# Edit docker-compose.yml and remove ports: section
# Access via internal Docker network
```

### 4. Regular Updates

```bash
# Subscribe to Sentry security advisories
# https://github.com/getsentry/self-hosted/releases

# Check for updates monthly
cd ~/sentry
git fetch --tags
git tag --sort=version:refname | tail -5
```

---

## Performance Tuning

### For Production with High Traffic

Edit `docker-compose.override.yml`:

```yaml
version: '3.4'

services:
  web:
    deploy:
      replicas: 2
    environment:
      SENTRY_WEB_WORKERS: 4

  worker:
    deploy:
      replicas: 4
    environment:
      SENTRY_WORKER_CONCURRENCY: 2

  postgres:
    environment:
      POSTGRES_MAX_CONNECTIONS: 500
    command:
      - postgres
      - -c
      - shared_buffers=2GB
      - -c
      - max_connections=500

  redis:
    command:
      - redis-server
      - --maxmemory 2gb
      - --maxmemory-policy allkeys-lru
```

```bash
docker-compose up -d
```

---

## Resource Requirements by Scale

| Traffic Level | RAM | CPU | Disk | Workers |
|---------------|-----|-----|------|---------|
| Small (<1K events/day) | 4GB | 2 cores | 20GB | 1 |
| Medium (1K-10K events/day) | 8GB | 4 cores | 50GB | 2-4 |
| Large (10K-100K events/day) | 16GB | 8 cores | 100GB | 4-8 |
| Very Large (100K+ events/day) | 32GB+ | 16+ cores | 500GB+ | 8+ |

---

## Useful Commands

```bash
# View all logs
docker-compose logs -f

# Restart specific service
docker-compose restart web

# View resource usage
docker stats

# Access Sentry shell (for debugging)
docker-compose run --rm web shell

# Run Django management commands
docker-compose run --rm web <command>

# Cleanup old data
docker-compose run --rm web cleanup --days 90

# Create new user
docker-compose run --rm web createuser

# Check Sentry version
docker-compose run --rm web --version

# Database migrations (after updates)
docker-compose run --rm web upgrade
```

---

## Monitoring Sentry Itself

Create a simple health check script:

```bash
nano ~/sentry-health-check.sh
```

```bash
#!/bin/bash

SENTRY_URL="http://localhost:9000"
SLACK_WEBHOOK="YOUR_SLACK_WEBHOOK_URL"

# Check if Sentry is responding
if ! curl -f -s "$SENTRY_URL" > /dev/null; then
    # Send alert to Slack
    curl -X POST "$SLACK_WEBHOOK" \
        -H 'Content-Type: application/json' \
        -d "{\"text\":\"🚨 Sentry is DOWN!\"}"
    
    # Try to restart
    cd ~/sentry && docker-compose restart
fi
```

```bash
chmod +x ~/sentry-health-check.sh

# Run every 5 minutes
crontab -e
# Add:
*/5 * * * * ~/sentry-health-check.sh
```

---

## Cost Analysis

**Self-Hosted vs Cloud:**

| Aspect | Self-Hosted | Sentry.io Cloud |
|--------|-------------|-----------------|
| **Cost** | Server costs only (~$20-40/mo) | $26-80/mo + overage fees |
| **Setup Time** | 1-2 hours | 15 minutes |
| **Maintenance** | You manage | Fully managed |
| **Limits** | Unlimited | 50K-250K events/mo |
| **Data Location** | Your server | Sentry's cloud |
| **Customization** | Full control | Limited |

**Recommended for:**
- ✅ Budget-conscious projects
- ✅ High-volume event logging
- ✅ Data privacy requirements
- ✅ Custom integrations needed

---

## Next Steps

1. ✅ Install Sentry on your server
2. ✅ Create 7 projects for ClickEnRent services
3. ✅ Copy DSN values to `.env`
4. ✅ Restart ClickEnRent services
5. ✅ Test error tracking
6. ✅ Set up email alerts
7. ✅ Configure backups
8. ✅ Monitor and tune performance

---

## Resources

- **Official Docs**: https://develop.sentry.dev/self-hosted/
- **GitHub Repo**: https://github.com/getsentry/self-hosted
- **Docker Hub**: https://hub.docker.com/u/getsentry
- **Community Forum**: https://forum.sentry.io/
- **Status Updates**: https://github.com/getsentry/self-hosted/releases

---

**Your self-hosted Sentry is ready!** 🎉

All your ClickEnRent services are already configured to work with Sentry. Just install Sentry, create projects, and update the DSN values in your `.env` file.
