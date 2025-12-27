# ClickEnRent Backend - Docker Deployment Guide

## 🚀 Deployment Instructions for Server: 46.224.148.236

This guide will help you deploy all 7 microservices using Docker and Docker Compose.

---

## 📋 Prerequisites

Your server must have:
- **Docker** (version 20.10+)
- **Docker Compose** (version 2.0+)
- **Git**
- At least **4GB RAM** (8GB recommended)
- At least **20GB free disk space**

---

## 🏗️ Architecture Overview

The system consists of:
1. **PostgreSQL** - Single database server with 5 databases
2. **Eureka Server** (port 8761) - Service Discovery
3. **Gateway** (port 8080) - API Gateway (main entry point)
4. **Auth Service** (port 8081) - Authentication & Authorization
5. **Rental Service** (port 8082) - Bike rental management
6. **Support Service** (port 8083) - Customer support
7. **Payment Service** (port 8084) - Payment processing
8. **Notification Service** (port 8085) - Push notifications

---

## 📦 Step 1: Install Docker & Docker Compose on Server

SSH into your server:
```bash
ssh root@46.224.148.236
```

### Install Docker:
```bash
# Update system
apt update && apt upgrade -y

# Install Docker
curl -fsSL https://get.docker.com -o get-docker.sh
sh get-docker.sh

# Start Docker service
systemctl start docker
systemctl enable docker

# Verify installation
docker --version
```

### Install Docker Compose:
```bash
# Download Docker Compose
curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose

# Make it executable
chmod +x /usr/local/bin/docker-compose

# Verify installation
docker-compose --version
```

---

## 📥 Step 2: Clone Repository on Server

```bash
# Create application directory
mkdir -p /opt/clickenrent
cd /opt/clickenrent

# Clone your repository
git clone <your-git-repository-url> backend
cd backend
```

---

## 🔐 Step 3: Configure Environment Variables

Create `.env` file from the example:
```bash
cp .env.example .env
nano .env
```

**Edit the following values:**

### Required (MUST CHANGE):
```bash
# Database password (use a strong password!)
POSTGRES_PASSWORD=YourStrongPasswordHere123!

# JWT Secret (generate with: openssl rand -base64 32)
JWT_SECRET=your_generated_jwt_secret_here
```

### Optional (Change if you have these services):
```bash
# Google OAuth2 (if you use Google login)
GOOGLE_CLIENT_ID=your_google_client_id
GOOGLE_CLIENT_SECRET=your_google_client_secret

# Stripe (if you use Stripe payments)
STRIPE_API_KEY=sk_live_your_stripe_key
STRIPE_WEBHOOK_SECRET=whsec_your_webhook_secret

# Azure Storage (if you use Azure for bike photos)
AZURE_STORAGE_CONNECTION_STRING=your_azure_connection_string
AZURE_STORAGE_CONTAINER_NAME=bike-rental-photos

# Lock Encryption (32 characters exactly)
LOCK_ENCRYPTION_KEY=your_32_character_encryption_key
```

**Save and exit** (Ctrl+X, then Y, then Enter)

---

## 🔨 Step 4: Prepare Database Initialization Scripts

Make the database init script executable:
```bash
chmod +x docker/init-databases.sh
```

---

## 🏗️ Step 5: Build Docker Images

This will build all 7 microservices (takes 10-15 minutes):
```bash
docker-compose build
```

**Expected output:**
```
Building eureka-server... done
Building gateway... done
Building auth-service... done
Building rental-service... done
Building support-service... done
Building payment-service... done
Building notification-service... done
```

---

## 🚀 Step 6: Start All Services

Start everything in detached mode:
```bash
docker-compose up -d
```

**Services will start in this order:**
1. PostgreSQL (waits until healthy)
2. Eureka Server (waits until healthy)
3. All microservices (wait for Eureka and PostgreSQL)
4. Gateway (waits for all services)

---

## 📊 Step 7: Check Status

### View all containers:
```bash
docker-compose ps
```

**Expected output:**
```
NAME                      STATUS              PORTS
clickenrent-postgres      Up (healthy)        0.0.0.0:5432->5432/tcp
clickenrent-eureka        Up (healthy)        0.0.0.0:8761->8761/tcp
clickenrent-auth          Up (healthy)        0.0.0.0:8081->8081/tcp
clickenrent-rental        Up (healthy)        0.0.0.0:8082->8082/tcp
clickenrent-support       Up (healthy)        0.0.0.0:8083->8083/tcp
clickenrent-payment       Up (healthy)        0.0.0.0:8084->8084/tcp
clickenrent-notification  Up (healthy)        0.0.0.0:8085->8085/tcp
clickenrent-gateway       Up (healthy)        0.0.0.0:8080->8080/tcp
```

### View logs:
```bash
# All services
docker-compose logs -f

# Specific service
docker-compose logs -f auth-service
docker-compose logs -f gateway

# Last 100 lines
docker-compose logs --tail=100
```

### Check health:
```bash
# Gateway health
curl http://localhost:8080/actuator/health

# Eureka dashboard
curl http://localhost:8761
```

---

## 🌐 Step 8: Access Your Services

From your local machine or any client:

### Main Entry Point:
- **API Gateway**: `http://46.224.148.236:8080`
- **Swagger UI**: `http://46.224.148.236:8080/swagger-ui.html`

### Service Discovery:
- **Eureka Dashboard**: `http://46.224.148.236:8761`

### Individual Services (for debugging):
- **Auth Service**: `http://46.224.148.236:8081`
- **Rental Service**: `http://46.224.148.236:8082`
- **Support Service**: `http://46.224.148.236:8083`
- **Payment Service**: `http://46.224.148.236:8084`
- **Notification Service**: `http://46.224.148.236:8085`

### Database:
- **PostgreSQL**: `46.224.148.236:5432`

---

## 🔥 Firewall Configuration

Make sure your server firewall allows these ports:

```bash
# Using UFW (Ubuntu)
ufw allow 8080/tcp   # Gateway
ufw allow 8761/tcp   # Eureka (optional, for monitoring)
ufw allow 5432/tcp   # PostgreSQL (optional, for remote access)

# Using firewalld (CentOS/RHEL)
firewall-cmd --permanent --add-port=8080/tcp
firewall-cmd --permanent --add-port=8761/tcp
firewall-cmd --permanent --add-port=5432/tcp
firewall-cmd --reload
```

**Security Note**: Only expose port 8080 (Gateway) to the public. Keep other ports internal or behind VPN.

---

## 🗄️ Database Management

### Connect to PostgreSQL:
```bash
docker exec -it clickenrent-postgres psql -U postgres
```

### List databases:
```sql
\l
```

### Connect to specific database:
```sql
\c clickenrent-auth
```

### View tables:
```sql
\dt
```

### Backup all databases:
```bash
docker exec clickenrent-postgres pg_dumpall -U postgres > backup_$(date +%Y%m%d).sql
```

### Restore from backup:
```bash
docker exec -i clickenrent-postgres psql -U postgres < backup_20231228.sql
```

---

## 🔄 Update & Restart Services

### Update code and rebuild:
```bash
cd /opt/clickenrent/backend
git pull
docker-compose build
docker-compose up -d
```

### Restart specific service:
```bash
docker-compose restart auth-service
```

### Restart all services:
```bash
docker-compose restart
```

### Stop all services:
```bash
docker-compose down
```

### Stop and remove volumes (⚠️ DELETES DATA):
```bash
docker-compose down -v
```

---

## 📈 Monitoring & Logs

### View resource usage:
```bash
docker stats
```

### View logs in real-time:
```bash
docker-compose logs -f --tail=50
```

### Export logs to file:
```bash
docker-compose logs > logs_$(date +%Y%m%d).txt
```

### Check disk usage:
```bash
docker system df
```

### Clean up unused images:
```bash
docker system prune -a
```

---

## 🐛 Troubleshooting

### Service won't start:
```bash
# Check logs
docker-compose logs service-name

# Check if port is already in use
netstat -tulpn | grep 8080

# Restart service
docker-compose restart service-name
```

### Database connection issues:
```bash
# Check if PostgreSQL is healthy
docker-compose ps postgres

# Check PostgreSQL logs
docker-compose logs postgres

# Verify databases exist
docker exec clickenrent-postgres psql -U postgres -c "\l"
```

### Eureka registration issues:
```bash
# Check Eureka logs
docker-compose logs eureka-server

# Verify services can reach Eureka
docker exec clickenrent-auth ping eureka-server

# Check Eureka dashboard
curl http://localhost:8761
```

### Out of memory:
```bash
# Check memory usage
free -h

# Add memory limits to docker-compose.yml:
services:
  auth-service:
    deploy:
      resources:
        limits:
          memory: 512M
```

### Services not communicating:
```bash
# Check network
docker network ls
docker network inspect backend_backend-network

# Test connectivity between services
docker exec clickenrent-auth ping postgres
docker exec clickenrent-auth ping eureka-server
```

---

## 🔒 Security Best Practices

1. **Change default passwords** in `.env`
2. **Use strong JWT secret** (generate with `openssl rand -base64 32`)
3. **Don't expose all ports** - only Gateway (8080) should be public
4. **Use HTTPS** - Add Nginx reverse proxy with SSL certificate
5. **Regular backups** - Schedule daily database backups
6. **Update regularly** - Keep Docker and images updated
7. **Monitor logs** - Set up log monitoring and alerts
8. **Firewall rules** - Restrict access to internal ports

---

## 🔐 Setting up HTTPS with Nginx (Optional but Recommended)

### Install Nginx:
```bash
apt install nginx certbot python3-certbot-nginx -y
```

### Configure Nginx:
```bash
nano /etc/nginx/sites-available/clickenrent
```

Add:
```nginx
server {
    listen 80;
    server_name 46.224.148.236;  # or your domain name

    location / {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

Enable site:
```bash
ln -s /etc/nginx/sites-available/clickenrent /etc/nginx/sites-enabled/
nginx -t
systemctl restart nginx
```

### Get SSL certificate (if you have a domain):
```bash
certbot --nginx -d yourdomain.com
```

---

## 📊 Performance Tuning

### Increase Java heap size:
Edit `docker-compose.yml` and add to each service:
```yaml
environment:
  JAVA_OPTS: "-Xms256m -Xmx512m"
```

### Scale services:
```bash
docker-compose up -d --scale rental-service=3
```

---

## 🎯 Quick Reference Commands

```bash
# Start everything
docker-compose up -d

# Stop everything
docker-compose down

# View status
docker-compose ps

# View logs
docker-compose logs -f

# Restart service
docker-compose restart service-name

# Rebuild and restart
docker-compose up -d --build

# Backup database
docker exec clickenrent-postgres pg_dumpall -U postgres > backup.sql

# Check health
curl http://localhost:8080/actuator/health
```

---

## 📞 Support

If you encounter issues:
1. Check logs: `docker-compose logs -f`
2. Check service health: `docker-compose ps`
3. Verify environment variables in `.env`
4. Ensure all ports are available
5. Check firewall rules

---

## 📝 Notes

- **First startup** takes 2-3 minutes for all services to become healthy
- **Database initialization** happens automatically on first run
- **Data persists** in Docker volume `postgres-data`
- **Logs** are stored in Docker and can be viewed with `docker-compose logs`
- **Updates** require rebuilding images: `docker-compose build`

---

## ✅ Deployment Checklist

- [ ] Docker and Docker Compose installed
- [ ] Repository cloned to `/opt/clickenrent/backend`
- [ ] `.env` file created with strong passwords
- [ ] Database init script is executable
- [ ] Firewall configured (port 8080 open)
- [ ] Docker images built successfully
- [ ] All services started and healthy
- [ ] Gateway accessible at `http://46.224.148.236:8080`
- [ ] Eureka shows all services registered
- [ ] Database backup scheduled
- [ ] (Optional) HTTPS configured with Nginx

---

**🎉 Congratulations! Your ClickEnRent backend is now running in production!**

Access your API at: **http://46.224.148.236:8080**

