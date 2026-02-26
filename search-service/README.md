# Search Service

Elasticsearch-based global search microservice for Click & Rent platform. Provides unified search across users, bikes, locations, and hubs with multi-tenant isolation.

## Features

- **Global Search**: Unified search across multiple entity types (users, bikes, locations, hubs)
- **Autocomplete**: Search suggestions for real-time autocomplete functionality
- **Multi-Tenant**: Secure tenant-scoped results with multiple isolation layers
- **Hybrid Indexing**: Bulk synchronization + event-driven updates
- **Advanced Search Matching**:
  - **Prefix Matching**: "joh" finds "john", "johnson" (perfect for autocomplete)
  - **Wildcard Matching**: "mitt" finds "schmidt", "committee" (finds partial words)
  - **Fuzzy Matching**: "jhon" finds "john" (typo tolerance)
  - **Multi-Field Boosting**: Username matches score higher than email matches
- **JWT Authentication**: Secure access via gateway with JWT token propagation
- **Health Monitoring**: Actuator endpoints for health checks and metrics

## Architecture

### Technology Stack

- **Java 17** - Modern Java features
- **Spring Boot 3.3.2** - Framework
- **Spring Data Elasticsearch** - Elasticsearch integration
- **Elasticsearch 8.11.0** - Search engine
- **Spring Cloud** - Service discovery (Eureka), OpenFeign
- **Spring Security** - OAuth2 Resource Server with JWT
- **Lombok** - Boilerplate reduction
- **SpringDoc OpenAPI** - API documentation

### Port Configuration

- Service: **8086**
- Elasticsearch: **9200** (HTTP), **9300** (Transport)

## Prerequisites

- Java 17 or higher
- Maven 3.6+
- Elasticsearch 8.11.0
- Running Eureka Server (port 8761)
- Running auth-service and rental-service (for Feign clients)

## Quick Start

### 1. Start Elasticsearch

Using Docker Compose (recommended):

```bash
cd search-service
docker-compose up -d elasticsearch
```

Or manually with Docker:

```bash
docker run -d \
  --name clickenrent-elasticsearch \
  -p 9200:9200 -p 9300:9300 \
  -e "discovery.type=single-node" \
  -e "xpack.security.enabled=false" \
  -e "ES_JAVA_OPTS=-Xms512m -Xmx512m" \
  docker.elastic.co/elasticsearch/elasticsearch:8.11.0
```

Verify Elasticsearch is running:

```bash
curl http://localhost:9200
```

### 2. Configure Environment Variables

Create or update your environment configuration:

```bash
export JWT_SECRET="your-base64-encoded-secret"
export ES_URIS="http://localhost:9200"
export EUREKA_URL="http://localhost:8761/eureka/"
```

### 3. Build and Run

```bash
# Build the service
mvn clean install

# Run the service
mvn spring-boot:run
```

Or using Docker:

```bash
# Build Docker image
docker build -t clickenrent/search-service .

# Run with Docker Compose
docker-compose up
```

### 4. Verify Service

Check health status:

```bash
curl http://localhost:8086/actuator/health
```

Access Swagger UI:

```
http://localhost:8086/swagger-ui.html
```

Or via Gateway:

```
http://localhost:8080/swagger-ui.html
```

## API Endpoints

### Search Endpoints

#### Global Search

Search across all entity types:

```http
GET /api/v1/search?q={query}&types=users,bikes,locations,hubs&limit=20
Authorization: Bearer {jwt_token}
```

**Query Parameters:**
- `q` (required): Search query (min 2 characters)
- `types` (optional): Comma-separated entity types (default: all)
- `companyId` (optional): Filter by company (default: user's companies)
- `limit` (optional): Max results per type (default: 20, max: 100)

**Example Response:**

```json
{
  "query": "john",
  "results": {
    "users": [
      {
        "externalId": "user-123",
        "type": "user",
        "title": "John Doe",
        "subtitle": "john@example.com",
        "url": "/users/user-123",
        "imageUrl": "https://...",
        "metadata": {
          "userName": "johndoe",
          "isActive": true
        }
      }
    ],
    "bikes": [...],
    "locations": [...],
    "hubs": [...]
  },
  "totalResults": 15,
  "searchTimeMs": 42
}
```

#### Search Suggestions

Get autocomplete suggestions:

```http
GET /api/v1/search/suggestions?q={prefix}&limit=10
Authorization: Bearer {jwt_token}
```

**Query Parameters:**
- `q` (required): Search prefix (min 1 character)
- `companyId` (optional): Filter by company
- `limit` (optional): Max suggestions (default: 10, max: 20)

**Example Response:**

```json
[
  {
    "text": "John Doe",
    "type": "user",
    "category": "Users",
    "url": "/users/user-123"
  },
  {
    "text": "BIKE-045",
    "type": "bike",
    "category": "Bikes",
    "url": "/bikes/bike-045"
  }
]
```

### Indexing Endpoints

#### Bulk Synchronization

Perform initial bulk indexing (Admin only):

```http
POST /api/v1/index/sync
Authorization: Bearer {admin_jwt_token}
Content-Type: application/json

{
  "entityTypes": ["users", "bikes", "locations", "hubs"],
  "companyId": null
}
```

**Response:**

```json
{
  "indexedCounts": {
    "users": 1500,
    "bikes": 3000,
    "locations": 150,
    "hubs": 25
  },
  "errors": {},
  "status": "SUCCESS",
  "durationMs": 12543
}
```

#### Index Event

Process single entity change (for other services):

```http
POST /api/v1/index/event
Authorization: Bearer {jwt_token}
Content-Type: application/json

{
  "entityType": "user",
  "entityId": "user-123",
  "operation": "UPDATE"
}
```

**Operations:** `CREATE`, `UPDATE`, `DELETE`

## Search Behavior & Scoring

### Limit Behavior

**Important:** The `limit` parameter specifies **maximum results per entity type**, not total results across all types.

**Example:**
```bash
# Search with limit=20 across 3 types
GET /api/v1/search?q=customer&types=users,bikes,locations&limit=20

# Returns UP TO:
# - 20 users matching "customer"
# - 20 bikes matching "customer"  
# - 20 locations matching "customer"
# = Maximum 60 total results (20 per type)
```

If you want to limit **total results** across all types, retrieve results and slice in your application.

### How Search Matching Works

The search service uses a sophisticated multi-layered approach to find relevant results:

#### 1. **Prefix Matching** (Highest Priority - Boost: 3-4x)
Best for autocomplete and "starts with" queries:
- Query: `"joh"` → Matches: "**joh**n", "**joh**nson", "**joh**nstone"
- Query: `"admin"` → Matches: "**admin**_user", "**admin**istrator"

**Field Priority for Users:**
- `userName`: 6x boost
- `firstName`, `lastName`: 5x boost
- `email`: 2x boost

#### 2. **Exact Match** (High Priority - Boost: 2x)
Matches complete words across multiple fields:
- Query: `"john"` → Matches: "John Doe", "john@example.com", "john_admin"
- Searches across: username, name, email, and combined searchable text

#### 3. **Wildcard Matching** (Medium Priority - Boost: 1.5x)
Finds partial words anywhere in the text:
- Query: `"mitt"` → Matches: "sch**mitt**", "com**mitt**ee", "per**mitt**ed"
- Query: `"bike"` → Matches: "my**bike**", "super**bike**", "**bike**123"

#### 4. **Fuzzy Matching** (Fallback - Boost: 1x)
Handles typos and misspellings (1-2 character differences):
- Query: `"jhon"` → Matches: "john" (1 transposition)
- Query: `"admni"` → Matches: "admin" (1 character difference)
- `AUTO` fuzziness: 0 edits for 1-2 chars, 1 edit for 3-5 chars, 2 edits for 6+ chars

### Entity-Specific Field Weights

**Users:**
- Username: 5x (highest)
- First Name / Last Name: 4x
- Email: 2x
- Phone, searchableText: 1x

**Bikes:**
- Code: 3x
- Frame Number: 2x
- searchableText: 1x

**Locations:**
- Name: 4x
- Address: 2x
- searchableText: 1x

**Hubs:**
- Name: 4x
- searchableText: 1x

### Search Examples

```bash
# Example 1: Find user by partial first name
GET /api/v1/search?q=joh&types=users&limit=10
# Returns: John, Johnson, Johnathan (prefix match)

# Example 2: Find bike by partial code
GET /api/v1/search?q=BK&types=bikes&limit=10
# Returns: BK001, BK045, BK123 (prefix match)

# Example 3: Find location with partial word
GET /api/v1/search?q=dam&types=locations&limit=10
# Returns: "Amsterdam", "Rotterdam" (wildcard match)

# Example 4: Typo tolerance
GET /api/v1/search?q=admni&types=users&limit=10
# Returns: admin_user, administrator (fuzzy match)

# Example 5: Multi-entity search
GET /api/v1/search?q=test&types=users,bikes,locations&limit=20
# Returns: test users, test bikes, test locations
```

### Autocomplete (Suggestions) Behavior

The `/api/v1/search/suggestions` endpoint is optimized for autocomplete:
- **Even higher prefix boosting** (4-6x) for instant results
- **Minimal fuzzy matching** (less relevant for real-time suggestions)
- **Fast response** (< 100ms typical)

```bash
# Suggestions for "jo"
GET /api/v1/search/suggestions?q=jo&limit=10

# Returns immediately as user types:
# - John Doe (john_doe)
# - Johnson Smith (johnson)
# - Joey Martinez (joey_m)
```

## Configuration

### Application Properties

Key configuration properties in `application.properties`:

```properties
# Service
spring.application.name=search-service
server.port=8086

# Elasticsearch
spring.elasticsearch.uris=${ES_URIS:http://localhost:9200}
spring.elasticsearch.username=${ES_USERNAME:}
spring.elasticsearch.password=${ES_PASSWORD:}

# Eureka
eureka.client.service-url.defaultZone=${EUREKA_URL:http://localhost:8761/eureka/}

# JWT
jwt.secret=${JWT_SECRET}

# Indexing
search.indexing.batch-size=500
search.indexing.async-pool-size=5
```

### Environment Variables

Recommended environment variables:

- `JWT_SECRET` - Base64-encoded JWT secret (must match auth-service)
- `ES_URIS` - Elasticsearch connection URIs
- `EUREKA_URL` - Eureka server URL
- `ES_USERNAME` - Elasticsearch username (optional)
- `ES_PASSWORD` - Elasticsearch password (optional)

## Integration with Other Services

### Gateway Integration

The search-service is accessible through the gateway:

```
http://localhost:8080/api/v1/search?q=...
```

Gateway routes are configured in `gateway/src/main/java/org/clickenrent/gateway/config/GatewayConfig.java`

### Event-Driven Indexing (Real-Time Sync)

The search-service receives real-time updates from auth-service and rental-service via Feign clients. When entities are created, updated, or deleted, the source services automatically notify search-service to update the Elasticsearch index.

#### How It Works

1. **Source service performs CRUD operation** (e.g., create user, update bike)
2. **Entity saved to PostgreSQL**
3. **Source service calls SearchServiceClient.notifyIndexEvent()**
4. **search-service receives event** (HTTP 202 Accepted)
5. **Background thread processes event** (fetches data, indexes in Elasticsearch)
6. **Search results updated** within 100-500ms

#### Implementation in Source Services

**Step 1: Create SearchServiceClient**

```java
// auth-service or rental-service
package org.clickenrent.authservice.client;

import org.clickenrent.contracts.search.IndexEventRequest;
import org.clickenrent.authservice.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "search-service", configuration = FeignConfig.class)
public interface SearchServiceClient {
    @PostMapping("/api/v1/index/event")
    void notifyIndexEvent(@RequestBody IndexEventRequest event);
}
```

**Step 2: Add notifications to service classes**

```java
// Example: UserService in auth-service
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final SearchServiceClient searchServiceClient;
    
    @Transactional
    public UserDTO createUser(UserDTO userDTO, String password) {
        User user = userRepository.save(...);
        
        // Notify search-service (fail-safe)
        notifySearchService("user", user.getExternalId(), "CREATE");
        
        return userMapper.toDto(user);
    }
    
    @Transactional
    public UserDTO updateUser(Long id, UserDTO userDTO) {
        User user = userRepository.save(...);
        
        // Notify search-service
        notifySearchService("user", user.getExternalId(), "UPDATE");
        
        return userMapper.toDto(user);
    }
    
    @Transactional
    public void deleteUser(Long id) {
        String externalId = user.getExternalId();
        userRepository.delete(user);
        
        // Notify search-service
        notifySearchService("user", externalId, "DELETE");
    }
    
    private void notifySearchService(String entityType, String entityId, String operation) {
        try {
            searchServiceClient.notifyIndexEvent(
                IndexEventRequest.builder()
                    .entityType(entityType)
                    .entityId(entityId)
                    .operation(IndexEventRequest.IndexOperation.valueOf(operation))
                    .build()
            );
        } catch (Exception e) {
            // Don't fail main operation if search indexing fails
            log.warn("Failed to notify search-service: {}", e.getMessage());
        }
    }
}
```

**Services with notifications implemented:**
- `auth-service/UserService` - User CRUD operations
- `rental-service/BikeService` - Bike CRUD operations
- `rental-service/LocationService` - Location CRUD operations
- `rental-service/HubService` - Hub CRUD operations

#### Scheduled Sync (Safety Net)

A scheduled task runs nightly at 2 AM to re-sync all entities, catching any events that may have been missed:

```java
// Runs automatically every night
@Scheduled(cron = "0 0 2 * * *")
public void scheduledFullSync() {
    indexingService.bulkSync(
        BulkSyncRequest.builder()
            .entityTypes(List.of("users", "bikes", "locations", "hubs"))
            .build()
    );
}
```

**Configuration:**

```properties
# Enable/disable scheduled sync
search.scheduled-sync.enabled=true

# Cron expression (default: 2 AM daily)
search.scheduled-sync.cron=0 0 2 * * *
```

**To disable scheduled sync:**

```bash
export SCHEDULED_SYNC_ENABLED=false
```

#### Synchronization Strategy Summary

| Method | Trigger | Latency | Use Case |
|--------|---------|---------|----------|
| **Event-Driven** | Automatic (on entity changes) | 100-500ms | Primary sync method |
| **Scheduled Sync** | Automatic (daily at 2 AM) | N/A | Safety net for missed events |
| **Manual Bulk Sync** | Admin API call | Minutes | Initial setup, disaster recovery |

**Best Practice:** Event-driven handles 99% of updates, scheduled sync catches the remaining 1% of edge cases.

## Multi-Tenant Security

Search results are automatically filtered based on the authenticated user's company associations:

1. **JWT Claims**: User's `companyExternalIds` extracted from token
2. **Tenant Context**: Stored in ThreadLocal via `TenantInterceptor`
3. **Elasticsearch Filters**: Applied to all search queries
4. **Admin Bypass**: Superadmin/Admin users can see all results

## Development

### Running Tests

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=SecurityServiceTest

# Run with coverage
mvn test jacoco:report
```

### Building

```bash
# Clean and build
mvn clean package

# Skip tests
mvn clean package -DskipTests

# Build Docker image
docker build -t clickenrent/search-service .
```

### Accessing Elasticsearch

View indices:

```bash
curl http://localhost:9200/_cat/indices?v
```

View mappings:

```bash
curl http://localhost:9200/users/_mapping
```

Search directly:

```bash
curl -X GET "http://localhost:9200/users/_search?q=john&pretty"
```

## Monitoring

### Health Check

```bash
curl http://localhost:8086/actuator/health
```

### Metrics

```bash
curl http://localhost:8086/actuator/metrics
```

### Prometheus

```bash
curl http://localhost:8086/actuator/prometheus
```

## Troubleshooting

### Elasticsearch Not Starting

Check if port 9200 is available:

```bash
lsof -i :9200
```

Increase Docker memory if needed:

```bash
# In docker-compose.yml
environment:
  - "ES_JAVA_OPTS=-Xms1g -Xmx1g"
```

### Search Returns No Results

1. Check if indices exist:
   ```bash
   curl http://localhost:9200/_cat/indices?v
   ```

2. Perform bulk sync:
   ```bash
   curl -X POST http://localhost:8086/api/v1/index/sync \
     -H "Authorization: Bearer {admin_token}" \
     -H "Content-Type: application/json" \
     -d '{"entityTypes":["users","bikes","locations","hubs"]}'
   ```

3. Verify Feign clients can reach auth-service and rental-service

### JWT Authentication Errors

Ensure `JWT_SECRET` matches across all services (auth-service, gateway, search-service)

## Production Considerations

1. **Elasticsearch Cluster**: Use multi-node cluster for production
2. **Security**: Enable Elasticsearch X-Pack security
3. **Backup**: Configure snapshot repositories for index backups
4. **Monitoring**: Set up Elasticsearch monitoring and alerting
5. **Scaling**: Consider read replicas and shard allocation
6. **Rate Limiting**: Already configured in gateway
7. **Logging**: Configure centralized logging (ELK stack)

## License

Copyright © 2024 Click & Rent. All rights reserved.

## Author

Vitaliy Shvetsov - aybserve@gmail.com
