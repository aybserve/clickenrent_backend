# ClickEnRent Backend - Microservices

Spring Boot microservices architecture for the ClickEnRent bike rental platform.

## About the Project

ClickEnRent is a production-ready bike rental platform built with microservices architecture. The system handles bike-sharing operations for multiple companies (B2B) and end-users, including real-time fleet management, payments with multiple providers, and advanced analytics.

**Key capabilities:**
- Multi-tenant B2B platform with company isolation
- Real-time bike tracking and smart lock integration
- Payment processing (Stripe, MultiSafepay) with split payouts
- Global search with Elasticsearch
- Analytics dashboards for revenue and fleet metrics

## Key Features

- **Production-Ready Security**: 5-layer multi-tenant isolation (JWT → PostgreSQL RLS)
- **Advanced Rate Limiting**: Redis-based with IP blocking and attack detection
- **Observability**: Sentry APM, Prometheus metrics, Grafana dashboards
- **Payment Integration**: Stripe + MultiSafepay with webhook handling and split payouts
- **Event-Driven Architecture**: Kafka for async indexing and notifications
- **OAuth Integration**: Google and Apple Sign-In with token verification
- **CI/CD**: GitHub Actions with automated testing and Kubernetes deployment

## Architecture

- **7 Microservices** + **1 Service Discovery** + **1 API Gateway**
- **Spring Boot 3.3.2** + **Java 17** + **Spring Cloud 2023.0.1**
- **PostgreSQL** for persistence (per-service databases)
- **Elasticsearch** for full-text and global search
- **Kafka** for async events (search indexing, notifications)
- **Redis** for rate limiting and attack detection
- **Eureka** for service discovery
- **Spring Cloud Gateway** for API routing and JWT validation
- **JWT** authentication; **Google** and **Apple** OAuth

### System Architecture

```
┌─────────────────────────────────────────────────────────────────────────┐
│                        Web/Mobile Clients                                │
└───────────────────────────────┬─────────────────────────────────────────┘
                                │
                                ▼
                    ┌───────────────────────┐
                    │   API Gateway :8080   │◄──────┐
                    │   (Rate Limiting)     │       │
                    └───────────┬───────────┘       │
                                │                   │
                ┌───────────────┼───────────────┐   │
                │               │               │   │
                ▼               ▼               ▼   │ Service
    ┌─────────────────┐  ┌─────────────┐  ┌──────────────┐  Discovery
    │  Auth Service   │  │   Rental    │  │   Payment    │     │
    │     :8081       │  │   :8082     │  │    :8084     │     │
    └────────┬────────┘  └──────┬──────┘  └──────┬───────┘     │
             │                  │                 │             │
    ┌────────▼────────┐  ┌──────▼──────┐  ┌──────▼───────┐    │
    │    Support      │  │Notification │  │    Search    │    │
    │     :8083       │  │   :8085     │  │    :8086     │    │
    └────────┬────────┘  └──────┬──────┘  └──────┬───────┘    │
             │                  │                 │             │
             │           ┌──────▼──────┐          │             │
             │           │  Analytics  │          │             │
             │           │   :8087     │          │             │
             │           └──────┬──────┘          │             │
             │                  │                 │             │
             └──────────────────┼─────────────────┼─────────────┘
                                │                 │
                    ┌───────────▼─────────────────▼──────┐
                    │   Eureka Server :8761               │
                    │   (Service Registry)                │
                    └─────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────┐
│                           Data Layer                                     │
├─────────────────┬─────────────────┬─────────────────┬───────────────────┤
│   PostgreSQL    │     Redis       │ Elasticsearch   │      Kafka        │
│   (Per-Service  │  (Rate Limit)   │  (Search Index) │   (Events Bus)    │
│    Databases)   │                 │                 │                   │
└─────────────────┴─────────────────┴─────────────────┴───────────────────┘
      ▲                   ▲                 ▲                 ▲
      │                   │                 │                 │
   All Services        Gateway           Search         Search + Auth
```

**Request Flow:**
1. Client → Gateway (JWT validation, rate limiting)
2. Gateway → Microservice (via Eureka discovery)
3. Microservice → PostgreSQL (with multi-tenant filtering)
4. Auth/Search → Kafka (async events)
5. Search → Elasticsearch (indexing & queries)

## Services

| Service | Port | Storage | Description |
|---------|------|---------|-------------|
| Gateway | 8080 | Redis | API Gateway (main entry point), rate limiting |
| Eureka Server | 8761 | - | Service Discovery |
| Auth Service | 8081 | clickenrent-auth | Authentication, users, companies, invitations |
| Rental Service | 8082 | clickenrent-rental | Bikes, rentals, rides, locations, hubs, B2B |
| Support Service | 8083 | clickenrent-support | Support requests, inspections, bike issues |
| Payment Service | 8084 | clickenrent-payment | Payments (Stripe, MultiSafepay), payouts |
| Notification Service | 8085 | clickenrent-notification | Push notifications (Expo), preferences |
| Search Service | 8086 | Elasticsearch, Kafka | Global search, index sync via Kafka |
| Analytics Service | 8087 | clickenrent-analytics | Dashboards, metrics, revenue & fleet analytics |

**Repository layout:** `shared-contracts/` (shared DTOs), `eureka-server/`, `gateway/`, `*-service/` (auth, rental, support, payment, notification, search, analytics), `docker-services/` (Elasticsearch, Kafka, Kibana, Kafka UI), `k8s/` (Kubernetes manifests), `scripts/` (build, deploy, Flyway).

## Quick Start

### Build

```bash
# Build all modules (including shared-contracts)
mvn clean package

# Skip tests
mvn clean package -DskipTests

# Or use the project script
./scripts/build-all.sh
./scripts/build-all.sh --skip-tests
```

### Configure environment

```bash
cp .env.example .env
# Edit .env: DB_PASSWORD, JWT_SECRET, and optional ES_PASSWORD, REDIS_PASSWORD, Kafka, OAuth, payments, etc.
```

### Run infrastructure (Docker)

Search, auth, and notification services depend on **Elasticsearch**, **Kafka**, and **Redis**. Use the Docker Compose stack for local development:

```bash
cd docker-services
cp .env.example .env
# Set ELASTIC_PASSWORD and any overrides
docker-compose up -d
# Elasticsearch :9200, Kibana :5601, Kafka :9092, Kafka UI :8090
```

### Run services locally

Start **Eureka** first, then **Gateway**, then the rest (each in its own terminal or via your IDE). Example:

```bash
cd eureka-server && mvn spring-boot:run
cd gateway && mvn spring-boot:run
cd auth-service && mvn spring-boot:run
# ... rental-service, support-service, payment-service, notification-service, search-service, analytics-service
```

API entry point: **http://localhost:8080** (Gateway). Swagger UI: **http://localhost:8080/swagger-ui.html**

### Kubernetes

Manifests are in `k8s/` (namespace, configmap, secrets, ingress, and per-service deployments). Use `scripts/deploy.sh` or `scripts/fresh-deploy.sh` as referenced in the repo.

## Development

- **Build**: `mvn clean package` or `./scripts/build-all.sh` (optionally `--skip-tests`, `--docker`).
- **Single service**: e.g. `cd auth-service && mvn spring-boot:run`. Start Eureka and Gateway first.
- **Infrastructure**: `docker-services/docker-compose.yml` runs Elasticsearch, Kibana, Zookeeper, Kafka, and Kafka UI only. For full app deployment use the `k8s/` manifests and scripts in `scripts/`.
- **Shared contracts**: The `shared-contracts` module (version 2.3.0) holds DTOs and contracts used by multiple services.

## Requirements

- **Java 17**
- **Maven 3.6+**
- **PostgreSQL 15** (per-service DBs: clickenrent-auth, clickenrent-rental, clickenrent-support, clickenrent-payment, clickenrent-notification, clickenrent-analytics)
- **Redis** (rate limiting at Gateway)
- **Elasticsearch 7.x** (search-service; use `docker-services` for local)
- **Kafka** (search indexing, notification events; use `docker-services` for local)
- **Docker** (optional, for infra and/or containerized deploy)

## Technology Stack

**Backend:**
- Java 17, Spring Boot 3.3.2, Spring Cloud 2023.0.1
- Spring Security (JWT, OAuth2 Resource Server)
- Spring Data JPA, Hibernate
- Spring Cloud Gateway, Netflix Eureka
- Feign (declarative REST clients)

**Data & Storage:**
- PostgreSQL 15 (with Row Level Security)
- Elasticsearch 7.x (full-text search)
- Redis (rate limiting, caching)
- Apache Kafka (event streaming)
- Flyway (database migrations)

**Integrations:**
- Stripe & MultiSafepay (payment processing)
- Google & Apple OAuth (authentication)
- Azure Blob Storage (bike images)
- MapBox API (location services)
- Expo (push notifications)

**DevOps & Monitoring:**
- Docker & Docker Compose
- Kubernetes (production deployment)
- GitHub Actions (CI/CD pipeline)
- Sentry (error tracking & APM)
- Prometheus & Grafana (metrics)
- Maven (build & dependency management)

## Configuration

Configuration is driven by environment variables (e.g. root `.env` from `.env.example`). Main options:

| Category | Variables |
|----------|-----------|
| **Database** | `DB_USERNAME`, `DB_PASSWORD`; per-service `DB_URL` override if needed |
| **JWT** | `JWT_SECRET` (required), `JWT_EXPIRATION`, `JWT_REFRESH_EXPIRATION` |
| **OAuth** | `GOOGLE_CLIENT_ID`, `GOOGLE_CLIENT_SECRET`; `APPLE_*` for Apple Sign-In |
| **Payments** | `STRIPE_*`, `MULTISAFEPAY_*` (API key, webhook secret, URLs) |
| **Rental** | `AZURE_STORAGE_*`, `MAPBOX_API_KEY`, `LOCK_ENCRYPTION_KEY` |
| **Infra** | `REDIS_HOST`, `REDIS_PORT`, `REDIS_PASSWORD`; `ES_URIS`, `ES_USERNAME`, `ES_PASSWORD`; `KAFKA_BOOTSTRAP_SERVERS` |
| **Service auth** | `SERVICE_AUTH_USERNAME`, `SERVICE_AUTH_PASSWORD` (inter-service) |
| **Sentry** | `SENTRY_DSN_*` per service, `SENTRY_ENVIRONMENT`, `SENTRY_TRACES_SAMPLE_RATE` |
| **Profile** | `SPRING_PROFILES_ACTIVE=staging` (or `prod`); Flyway: `FLYWAY_MIGRATE`, `FLYWAY_SKIP_TESTDATA` |

## Rate Limiting

ClickEnRent implements **production-ready rate limiting** at the API Gateway level using Redis:

### Architecture
- **Single Layer:** All rate limiting enforced at Gateway (no duplicate service-level limiting)
- **Dual Strategy:** IP-based (anonymous) + User-based (authenticated)
- **Storage:** Redis for distributed rate limiting across instances
- **Security:** Attack detection with automatic IP blocking

### Rate Limit Policies

| Endpoint Type | Strategy | Limit | Burst | Use Case |
|---------------|----------|-------|-------|----------|
| Public Auth (login, register) | IP-based | 20 req/sec | 30 | Prevent brute force attacks |
| Protected APIs | User-based | 50 req/sec | 100 | Normal user operations |

### Configuration

```bash
# Redis (required for rate limiting)
REDIS_HOST=localhost
REDIS_PORT=6379
REDIS_PASSWORD=your-redis-password

# Rate Limiting
RATE_LIMIT_ENABLED=true
RATE_LIMIT_IP_REPLENISH=20
RATE_LIMIT_IP_BURST=30
RATE_LIMIT_USER_REPLENISH=50
RATE_LIMIT_USER_BURST=100

# Attack Detection
RATE_LIMIT_ATTACK_THRESHOLD=10
RATE_LIMIT_BLOCK_DURATION=15
```

### Testing Rate Limits

```bash
# Monitor Redis keys
redis-cli KEYS '*'

# Check rate limit violations in logs (e.g. when running via Docker/K8s)
# logs -f gateway | grep "Rate limit"
```

### Response Headers

When rate limited, clients receive:
- **Status:** 429 Too Many Requests
- **X-RateLimit-Limit:** Total requests allowed
- **X-RateLimit-Remaining:** Remaining requests
- **X-RateLimit-Retry-After-Seconds:** Seconds to wait
- **Retry-After:** Standard retry header

### Attack Detection

The system automatically:
- Tracks rate limit violations per IP
- Blocks IPs exceeding threshold (default: 10 violations)
- Applies progressive penalties (exponential backoff)
- Logs security alerts for monitoring

## Monitoring

- **Eureka Dashboard**: `http://<host>:8761`
- **Gateway health**: `http://<host>:8080/actuator/health` (includes Redis when configured)
- **Prometheus metrics**: `http://<host>:8080/actuator/prometheus`
- **K8s**: See `k8s/monitoring/` for Prometheus and Grafana manifests.

### Sentry Error Tracking & Performance Monitoring

ClickEnRent integrates **Sentry** for real-time error tracking and performance monitoring across all services (Gateway + 7 microservices):

**Features:**
- Automatic error capture and grouping
- Multi-tenant context enrichment (company IDs, user roles)
- Performance transaction tracing (APM)
- OAuth flow monitoring with detailed spans
- Smart alerting (Slack, Email, PagerDuty)
- Release tracking and error trends

**Configuration:**

Each service requires its own Sentry DSN in the `.env` file:

```bash
# Get DSN values from https://sentry.io
SENTRY_DSN_AUTH=https://xxx@yyy.ingest.sentry.io/zzz
SENTRY_DSN_RENTAL=https://xxx@yyy.ingest.sentry.io/zzz
SENTRY_DSN_SUPPORT=https://xxx@yyy.ingest.sentry.io/zzz
SENTRY_DSN_PAYMENT=https://xxx@yyy.ingest.sentry.io/zzz
SENTRY_DSN_NOTIFICATION=https://xxx@yyy.ingest.sentry.io/zzz
SENTRY_DSN_SEARCH=https://xxx@yyy.ingest.sentry.io/zzz
SENTRY_DSN_GATEWAY=https://xxx@yyy.ingest.sentry.io/zzz
SENTRY_DSN_ANALYTICS=https://xxx@yyy.ingest.sentry.io/zzz

# Environment and sampling
SENTRY_ENVIRONMENT=production
SENTRY_TRACES_SAMPLE_RATE=1.0  # 1.0 = 100% of transactions
```

**What Gets Tracked:**

1. **Errors**: Unhandled exceptions across all services
2. **Performance**: Transaction traces for API requests
3. **OAuth**: Spans for Google/Apple OAuth flows
4. **Context**: Tenant/company tags on events
5. **Releases**: Grouping by service version (e.g. 1.0-SNAPSHOT)

## Troubleshooting

- **Infrastructure (docker-services)**: `docker-compose -f docker-services/docker-compose.yml ps | logs | restart <service> | down`
- **Kubernetes**: `kubectl get pods -n clickenrent`, `kubectl logs -f <pod> -n clickenrent`
- **Local**: Ensure Eureka and Gateway are up first; check `.env` and DB/Redis/ES/Kafka connectivity for each service.

## Security

### Multi-Tenant Isolation

ClickEnRent implements **company-based multi-tenancy** with defense-in-depth security:

- **5 Security Layers:** JWT claims → Tenant context → Hibernate filters → PostgreSQL RLS → Runtime validation
- **Company Isolation:** B2B users can only access their company's data
- **Database-Level Protection:** PostgreSQL Row Level Security (RLS) enforces isolation
- **Automatic Filtering:** Hibernate filters add WHERE clauses to all queries
- **Audit Trail:** All security violations are logged

**Security coverage:**
-  **Gateway**: JWT validation and header forwarding
-  **Auth service**: User authentication with company claims
-  **Rental service**: Full 5-layer security stack
-  **Payment service**: Full 5-layer security stack
-  **Support service**: Full 5-layer security stack
-  **Notification service**: Hybrid isolation (user + company scoped)
-  **Search service**: Tenant-aware search and indexing
-  **Analytics service**: Company-scoped analytics (Admin/B2B)

### Authentication & Authorization

- JWT authentication on all endpoints
- Role-based access control (SUPERADMIN, ADMIN, B2B, CUSTOMER)
- Environment variables for secrets
- HTTPS recommended (use Nginx reverse proxy)
- Firewall: Only expose port 8080

## Technical Highlights

### Microservices Architecture
- **Independent Deployments**: Each service has its own database and can be deployed independently
- **Service Discovery**: Eureka for dynamic service registration and discovery
- **Inter-Service Communication**: Feign clients with circuit breaker patterns
- **Shared Contracts**: Type-safe DTOs shared across services via Maven module
- **Event-Driven**: Kafka for async communication (search indexing, notifications)

### Security & Multi-Tenancy
- **PostgreSQL Row Level Security (RLS)**: Database-level tenant isolation
- **Hibernate Filters**: Automatic query scoping per company
- **JWT Claims Validation**: Spring Security with custom converters
- **Resource Security Expressions**: Custom SpEL expressions for fine-grained access control
- **Defense in Depth**: 5-layer security model from JWT to database

### Scalability & Performance
- **Stateless Authentication**: JWT-based auth enables horizontal scaling
- **Distributed Rate Limiting**: Redis-backed rate limiting across Gateway instances
- **Search Optimization**: Elasticsearch for sub-second full-text queries
- **Async Processing**: Kafka decouples heavy operations from request paths
- **Connection Pooling**: HikariCP with optimized settings per service

### Production Readiness
- **Comprehensive Monitoring**: Sentry APM with distributed tracing across services
- **Metrics & Dashboards**: Prometheus metrics with Grafana visualization
- **Health Checks**: Actuator endpoints with dependency health indicators
- **Database Migrations**: Flyway with versioned schema changes
- **CI/CD Pipeline**: Automated testing, building, and Kubernetes deployment
- **Error Handling**: Global exception handlers with structured error responses

## License

Copyright (c) 2025–2026 Vitaliy Shvetsov. All rights reserved.

This project and its source code are proprietary. Unauthorized copying, distribution, modification, or use of the code or any part of this repository is prohibited without prior written permission from the author.

## Author

**Vitaliy Shvetsov**

Email: aybserve@gmail.com

LinkedIn: https://linkedin.com/in/vitaliy-shvetsov/

Backend Developer specializing in microservices architecture, distributed systems, and cloud-native applications.

**Skills demonstrated in this project:**
- Microservices design and implementation
- Multi-tenant security architecture
- Payment gateway integration
- Real-time data processing with Kafka
- Kubernetes deployment and orchestration
- Production monitoring and observability

