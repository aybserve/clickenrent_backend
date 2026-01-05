# ClickEnRent Backend - Microservices

Spring Boot microservices architecture for bike rental platform.

## 🏗️ Architecture

- **7 Microservices** + **1 Service Discovery** + **1 API Gateway**
- **Spring Boot 3.3.2** + **Java 17**
- **PostgreSQL** databases
- **Eureka** for service discovery
- **Spring Cloud Gateway** for API routing
- **JWT** authentication

## 📦 Services

| Service | Port | Database | Description |
|---------|------|----------|-------------|
| Gateway | 8080 | - | API Gateway (main entry point) |
| Eureka Server | 8761 | - | Service Discovery |
| Auth Service | 8081 | clickenrent-auth | Authentication & Users |
| Rental Service | 8082 | clickenrent-rental | Bikes & Rentals |
| Support Service | 8083 | clickenrent-support | Customer Support |
| Payment Service | 8084 | clickenrent-payment | Payments & Billing |
| Notification Service | 8085 | clickenrent-notification | Push Notifications |

## 🚀 Quick Start (Docker)

### Simple 3-Step Deployment:

```bash
# 1. Build JARs
mvn clean package -DskipTests

# 2. Configure environment
cp .env.example .env
nano .env  # Edit POSTGRES_PASSWORD and JWT_SECRET

# 3. Run with Docker
chmod +x build-and-run.sh
bash build-and-run.sh
```

**Or use the automated script:**
```bash
bash build-and-run.sh
```

Access your API: http://46.224.148.235:8080

## 📚 Documentation

- **Simple Deployment Guide**: [DEPLOYMENT-SIMPLE.md](DEPLOYMENT-SIMPLE.md) ⭐ **START HERE**
- **Detailed Deployment Guide**: [DEPLOYMENT.md](DEPLOYMENT.md)
- **API Documentation**: http://46.224.148.235:8080/swagger-ui.html

## 🛠️ Development

### Build locally:
```bash
mvn clean package
```

### Run single service:
```bash
cd auth-service
mvn spring-boot:run
```

### Run all services with Docker:
```bash
docker-compose up -d
```

## 📋 Requirements

- Java 17
- Maven 3.6+
- Docker & Docker Compose
- PostgreSQL 15 (or use Docker)

## 🔧 Configuration

All configuration is done via environment variables in `.env` file:

- `POSTGRES_PASSWORD` - Database password
- `JWT_SECRET` - JWT secret key
- `GOOGLE_CLIENT_ID` - Google OAuth (optional)
- `STRIPE_API_KEY` - Stripe payments (optional)
- `AZURE_STORAGE_CONNECTION_STRING` - Azure storage (optional)

## 📊 Monitoring

- **Eureka Dashboard**: http://46.224.148.235:8761
- **Health Checks**: http://46.224.148.235:8080/actuator/health
- **Logs**: `docker-compose logs -f`

## 🐛 Troubleshooting

```bash
# Check service status
docker-compose ps

# View logs
docker-compose logs -f service-name

# Restart service
docker-compose restart service-name

# Stop everything
docker-compose down
```

## 🔐 Security

### Multi-Tenant Isolation

ClickEnRent implements **company-based multi-tenancy** with defense-in-depth security:

- **5 Security Layers:** JWT claims → Tenant context → Hibernate filters → PostgreSQL RLS → Runtime validation
- **Company Isolation:** B2B users can only access their company's data
- **Database-Level Protection:** PostgreSQL Row Level Security (RLS) enforces isolation
- **Automatic Filtering:** Hibernate filters add WHERE clauses to all queries
- **Audit Trail:** All security violations are logged

**Security Coverage:**
- ✅ gateway: JWT validation and header forwarding
- ✅ auth-service: User authentication with company claims
- ✅ rental-service: Full 5-layer security stack
- ✅ payment-service: Full 5-layer security stack
- ✅ support-service: Full 5-layer security stack
- ✅ notification-service: Hybrid isolation (user + company scoped)

**Documentation:**
- 📖 [SECURITY_ARCHITECTURE.md](SECURITY_ARCHITECTURE.md) - Complete security guide
- 📊 [IMPLEMENTATION_STATUS.md](IMPLEMENTATION_STATUS.md) - Implementation status

### Authentication & Authorization

- JWT authentication on all endpoints
- Role-based access control (SUPERADMIN, ADMIN, B2B, CUSTOMER)
- Environment variables for secrets
- HTTPS recommended (use Nginx reverse proxy)
- Firewall: Only expose port 8080

## 📄 License

Private project - All rights reserved

## 👤 Author

Vitaliy Shvetsov

