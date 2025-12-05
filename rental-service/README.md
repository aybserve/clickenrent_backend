# Rental Service

Rental Management Service for Click & Rent Platform. This microservice manages bikes, locations, hubs, rentals, charging stations, parts, and B2B operations.

## Overview

The Rental Service is part of the Click & Rent microservices architecture and handles all rental-related operations including:

- Bike inventory management (bikes, models, brands, types)
- Location and hub management
- Rental orders and bike rentals
- Ride tracking
- Bike reservations
- Charging station management
- Parts inventory
- B2B sales and subscriptions

## Architecture

- **Database**: Separate PostgreSQL database (`clickenrent-rental`)
- **Service Discovery**: Eureka Client for service registration
- **Inter-service Communication**: OpenFeign for calling auth-service
- **Security**: JWT-based authentication using OAuth2 Resource Server
- **API Documentation**: Swagger/OpenAPI 3.0
- **Audit**: JPA Auditing for tracking entity changes

## Key Features

### Auto-Creation Logic
- **Location Creation**: Automatically creates a "Main" hub when a new location is created
- **Hub Management**: Each location has at least one hub for inventory management

### Product Hierarchy
Uses SINGLE_TABLE inheritance strategy for all product types:
- `Bike` - Electric and non-electric bikes with full rental tracking
- `ChargingStation` - Charging infrastructure
- `Part` - Spare parts and accessories
- `ServiceProduct` - Service-related products
- `BikeRental` - Links bikes to rental orders

### Security & Access Control
- **Admin**: Full access to all resources
- **B2B**: Access to company-specific resources
- **Customer**: Access to own rentals and reservations

### B2B Features
- Revenue sharing for B2B-rentable bikes
- B2B sales tracking
- Subscription management

## Technology Stack

- **Java**: 17+
- **Spring Boot**: 3.x
- **Spring Cloud**: Netflix Eureka, OpenFeign
- **Database**: PostgreSQL (production), H2 (tests)
- **Security**: Spring Security with JWT
- **Documentation**: SpringDoc OpenAPI
- **Build Tool**: Maven

## Prerequisites

- Java 17 or higher
- Maven 3.6+
- PostgreSQL 12+ (for production)
- Eureka Server (running on port 8761)
- Auth Service (for user/company lookups)

## Configuration

### Database Setup

Create PostgreSQL database:
```sql
CREATE DATABASE "clickenrent-rental";
```

### Environment Variables

- `DB_URL`: Database URL (default: `jdbc:postgresql://localhost:5432/clickenrent-rental`)
- `DB_USERNAME`: Database username (default: `postgres`)
- `DB_PASSWORD`: Database password (default: `yourStrongPassword`)
- `JWT_SECRET`: JWT secret key (base64 encoded 256-bit key)
- `JPA_DDL_AUTO`: Hibernate DDL mode (default: `update`)
- `JPA_SHOW_SQL`: Show SQL queries (default: `true`)

## Running the Service

### Development Mode

```bash
./mvnw spring-boot:run
```

The service will start on port **8082** and register with Eureka at `http://localhost:8761`.

### Build

```bash
./mvnw clean package
```

### Run Tests

```bash
./mvnw test
```

## API Documentation

Once the service is running, access the Swagger UI at:
```
http://localhost:8082/swagger-ui.html
```

API documentation is available at:
```
http://localhost:8082/v3/api-docs
```

## Main Endpoints

### Bikes
- `GET /api/bikes` - Get all bikes (paginated)
- `GET /api/bikes/{id}` - Get bike by ID
- `GET /api/bikes/code/{code}` - Get bike by code
- `POST /api/bikes` - Create new bike (Admin only)
- `PUT /api/bikes/{id}` - Update bike (Admin only)
- `DELETE /api/bikes/{id}` - Delete bike (Admin only)

### Locations
- `GET /api/locations` - Get all locations
- `GET /api/locations/{id}` - Get location by ID
- `POST /api/locations` - Create location (auto-creates Main hub)
- `PUT /api/locations/{id}` - Update location
- `DELETE /api/locations/{id}` - Delete location (Admin only)

### Rentals
- `GET /api/rentals` - Get rentals (filtered by user role)
- `GET /api/rentals/{id}` - Get rental by ID
- `POST /api/rentals` - Create new rental
- `PUT /api/rentals/{id}` - Update rental
- `DELETE /api/rentals/{id}` - Delete rental (Admin only)

## Entity Structure

### Core Entities (41 total)

**Product Hierarchy (Abstract SINGLE_TABLE)**:
- Product (abstract base)
- Bike, ChargingStation, Part, ServiceProduct, BikeRental

**Status/Type Entities**:
- BikeType, BikeStatus, BatteryChargeStatus, ChargingStationStatus
- PartType, RideStatus, RentalStatus, RentalUnit
- LocationRole, B2BSaleStatus, B2BSubscriptionStatus

**Models & Brands**:
- BikeModel, BikeBrand, BikeEngine
- ChargingStationModel, ChargingStationBrand
- PartModel, PartBrand, PartCategory

**Location & Hub**:
- Location, LocationImage, Hub, HubImage, UserLocation, StockMovement, Coordinates

**Rental System**:
- Rental, BikeRental, BikeReservation, RentalPlan, BikeModelRentalPlan

**Ride System**:
- Ride, Lock, Key

**B2B System**:
- B2BSale, B2BSaleProduct, B2BSubscription, B2BSubscriptionItem

**Other**:
- Service

## Integration with Other Services

### Auth Service Integration
Uses OpenFeign client to fetch user and company details:
```java
@FeignClient(name = "auth-service", path = "/api")
public interface AuthServiceClient {
    UserDTO getUserById(Long id);
    CompanyDTO getCompanyById(Long id);
}
```

### Gateway Integration
All requests should come through the API Gateway which handles JWT validation.

## Future Enhancements

1. Event-driven architecture for company creation sync
2. Database migrations with Flyway/Liquibase
3. Caching with Redis for frequently accessed data
4. Rate limiting for API endpoints
5. Advanced search and filtering
6. Real-time ride tracking with WebSocket
7. Integration with payment services
8. Mobile app push notifications

## Development Guidelines

### Code Style
- Follow auth-service patterns for consistency
- Use Lombok for reducing boilerplate
- Comprehensive Swagger documentation for all endpoints
- Proper exception handling with global exception handler
- Security checks in service layer
- Transactional annotations on write operations

### Testing
- Unit tests for services
- Integration tests for controllers
- Test data available in `test-data.sql`

## Support

For issues and questions, please contact the development team.

## License

Proprietary - Click & Rent Platform
