# Docker Services Setup Guide

This directory contains the docker-compose configuration for running Elasticsearch and Kibana.

**Note:** Redis is provided by FastPanel and is not included in this docker-compose file.

## 🔐 Security Configuration

This setup includes **security enabled** for Elasticsearch with authentication required.

## 📋 Prerequisites

- Docker and Docker Compose installed on your server
- SSH access to your webserver
- Firewall configured to block external access to ports 9200, 9300, 5601

## 🚀 Deployment Steps for Webserver

### 1. Copy files to your webserver

```bash
# From your local machine, copy the docker-services directory to your server
scp -r docker-services/ user@your-server:/path/to/deployment/
```

### 2. Create and configure `.env` file on the server

```bash
# SSH into your server
ssh user@your-server

# Go to the docker-services directory
cd /path/to/deployment/docker-services/

# Copy the example file
cp .env.example .env

# Edit the .env file
nano .env
```

### 3. Generate a strong password

Generate a strong password for Elasticsearch:

```bash
# Generate a random password (recommended)
openssl rand -base64 32

# Or use this command for a password with special characters
openssl rand -base64 24 | tr -d "=+/" | cut -c1-25
```

### 4. Update the `.env` file

Replace `CHANGE_THIS_TO_STRONG_PASSWORD` with your generated password:

```env
ELASTIC_PASSWORD=your-generated-strong-password-here
```

Save and exit (Ctrl+X, then Y, then Enter in nano).

### 5. Secure the `.env` file

```bash
# Make sure only root/owner can read the .env file
chmod 600 .env

# Verify permissions
ls -la .env
# Should show: -rw------- (only owner can read/write)
```

### 6. Start the Docker containers

```bash
# Pull the latest images
docker-compose pull

# Start all services
docker-compose up -d

# Check if containers are running
docker-compose ps

# Check logs
docker-compose logs -f elasticsearch
docker-compose logs -f kibana
```

### 7. Verify Elasticsearch is secured

```bash
# This should FAIL (no authentication)
curl http://localhost:9200

# This should SUCCEED (with authentication)
curl -u elastic:your-password-here http://localhost:9200

# Check cluster health
curl -u elastic:your-password-here http://localhost:9200/_cluster/health
```

### 8. Test from outside (should be blocked)

```bash
# From your LOCAL machine (not the server), this should FAIL:
curl http://your-server-ip:9200
# Should timeout or connection refused
```

## 🔗 Connecting Your Application

Your Spring Boot `search-service` needs these environment variables:

```env
ES_URIS=http://elasticsearch:9200
ES_USERNAME=elastic
ES_PASSWORD=your-elasticsearch-password-here
```

### Option A: If running search-service in Docker on the same server

Add to your search-service docker-compose or docker run:

```yaml
environment:
  - ES_URIS=http://elasticsearch:9200
  - ES_USERNAME=elastic
  - ES_PASSWORD=${ELASTIC_PASSWORD}
```

Make sure your search-service container is in the same Docker network:

```bash
# If using docker-compose.yml for search-service, ensure:
networks:
  - default  # or specify the network name
```

### Option B: If running search-service outside Docker

Update the `.env` file in your project root:

```env
ES_URIS=http://localhost:9200
ES_USERNAME=elastic
ES_PASSWORD=your-elasticsearch-password-here
```

## 📊 Accessing Kibana

Kibana is available at `http://localhost:5601` (only from the server).

**Login credentials:**
- Username: `elastic`
- Password: (the password you set in `.env`)

To access Kibana from your local machine:

```bash
# Create SSH tunnel
ssh -L 5601:localhost:5601 user@your-server

# Then open in browser:
# http://localhost:5601
```

## 🛑 Stopping Services

```bash
# Stop all services
docker-compose down

# Stop and remove volumes (WARNING: deletes all data)
docker-compose down -v
```

## 🔄 Updating Services

```bash
# Pull new images
docker-compose pull

# Restart with new images
docker-compose up -d
```

## 📝 Useful Commands

```bash
# View logs
docker-compose logs -f elasticsearch
docker-compose logs -f kibana

# Restart a specific service
docker-compose restart elasticsearch

# Check Elasticsearch indices
curl -u elastic:password http://localhost:9200/_cat/indices?v

# Check which containers are using Elasticsearch
docker ps | grep elastic
```

## ⚠️ Security Checklist

- [ ] Firewall blocks external access to ports 9200, 9300, 5601
- [ ] Strong password set in `.env` file (minimum 20 characters)
- [ ] `.env` file has correct permissions (chmod 600)
- [ ] Ports bound to localhost only (127.0.0.1) in docker-compose.yml
- [ ] Tested that Elasticsearch is not accessible from external IP
- [ ] All services can connect with authentication
- [ ] `.env` file is not committed to git

## 🆘 Troubleshooting

### Elasticsearch won't start
```bash
# Check logs
docker-compose logs elasticsearch

# Common issues:
# 1. Port 9200 already in use
sudo netstat -tlnp | grep 9200

# 2. Insufficient memory
# Increase in docker-compose.yml: ES_JAVA_OPTS=-Xms1g -Xmx1g
```

### Can't connect from application
```bash
# Check if Elasticsearch is healthy
curl -u elastic:password http://localhost:9200/_cluster/health

# Check if containers are in the same network
docker network ls
docker network inspect docker-services_default
```

### Password not working
```bash
# If you changed the password after first start, you need to:
docker-compose down -v  # WARNING: deletes all data
docker-compose up -d    # Start fresh with new password
```

## 📚 Additional Resources

- [Elasticsearch Security Documentation](https://www.elastic.co/guide/en/elasticsearch/reference/7.17/security-minimal-setup.html)
- [Docker Compose Documentation](https://docs.docker.com/compose/)
