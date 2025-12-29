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

### Rental System
- `BikeRental` - Separate entity that links bikes to rental orders with revenue sharing support

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
- **Database**: PostgreSQL with PostGIS (production), H2 (tests)
- **Security**: Spring Security with JWT
- **Documentation**: SpringDoc OpenAPI
- **Build Tool**: Maven
- **Location Services**: Mapbox REST API via WebClient for geocoding and directions
- **Spatial Database**: PostGIS for location-based queries

## Prerequisites

- Java 17 or higher
- Maven 3.6+
- PostgreSQL 12+ with PostGIS extension (for production)
- Eureka Server (running on port 8761)
- Auth Service (for user/company lookups)
- Mapbox API key (for geocoding and directions)

## Configuration

### Database Setup

Create PostgreSQL database with PostGIS extension:
```sql
CREATE DATABASE "clickenrent-rental";

-- Connect to the database and enable PostGIS
\c clickenrent-rental
CREATE EXTENSION IF NOT EXISTS postgis;
```

Run the schema creation script:
```bash
psql -U postgres -d clickenrent-rental -f rental-service.sql
```

### Environment Variables

- `DB_URL`: Database URL (default: `jdbc:postgresql://localhost:5432/clickenrent-rental`)
- `DB_USERNAME`: Database username (default: `postgres`)
- `DB_PASSWORD`: Database password (default: `yourStrongPassword`)
- `JWT_SECRET`: JWT secret key (base64 encoded 256-bit key)
- `JPA_DDL_AUTO`: Hibernate DDL mode (default: `update`)
- `JPA_SHOW_SQL`: Show SQL queries (default: `true`)
- `MAPBOX_API_KEY`: Mapbox API access token (required for location features)

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
- `GET /api/bikes/nearby` - Find bikes near a location (see Location Features below)
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

### Location Services
- `GET /api/bikes/nearby` - Find bikes near a location
- `POST /api/location/geocode` - Convert address to coordinates
- `POST /api/location/reverse-geocode` - Convert coordinates to address
- `POST /api/location/directions` - Get directions between two points

## Entity Structure

### Core Entities (41 total)

**Product Hierarchy (Abstract SINGLE_TABLE)**:
- Product (abstract base)
- Bike, ChargingStation, Part, ServiceProduct

**Status/Type Entities**:
- BikeType, BikeStatus, ChargingStationStatus
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

## Location Features

### Nearby Bike Search

Find bikes within a specified radius of a location using PostGIS spatial queries.

**Endpoint**: `GET /api/bikes/nearby`

**Query Parameters**:
- `lat` (required): Latitude of center point (e.g., 52.374)
- `lng` (required): Longitude of center point (e.g., 4.9)
- `radius` (required): Search radius in kilometers (max: 100)
- `limit` (optional): Maximum results (default: 50, max: 200)
- `status` (optional): Filter by bike status ID

**Example Request**:
```bash
curl -X GET "http://localhost:8082/api/bikes/nearby?lat=52.374&lng=4.9&radius=5&limit=50" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

**Example Response**:
```json
{
  "bikes": [
    {
      "id": "bike-uuid",
      "code": "EB001",
      "name": "Zigma E-bike",
      "bikeStatus": 1,
      "bikeStatusName": "Available",
      "batteryLevel": 75,
      "location": {
        "latitude": 52.374,
        "longitude": 4.901
      },
      "distance": 0.3,
      "distanceUnit": "km",
      "hubExternalId": "550e8400-e29b-41d4-a716-446655440201",
      "hubName": "Hub A - Main"
    }
  ],
  "total": 12
}
```

### Geocoding

Convert addresses to coordinates using Mapbox Geocoding API.

**Endpoint**: `POST /api/location/geocode`

**Request Body**:
```json
{
  "address": "Amsterdam, Netherlands",
  "country": "NL",
  "language": "en"
}
```

**Response**:
```json
{
  "results": [
    {
      "placeName": "Amsterdam, Netherlands",
      "latitude": 52.3676,
      "longitude": 4.9041,
      "placeType": "place",
      "relevance": 0.99
    }
  ]
}
```

### Reverse Geocoding

Convert coordinates to addresses.

**Endpoint**: `POST /api/location/reverse-geocode`

**Request Body**:
```json
{
  "location": {
    "latitude": 52.374,
    "longitude": 4.9
  },
  "language": "en"
}
```

### Directions

Get routing directions between two points.

**Endpoint**: `POST /api/location/directions`

**Request Body**:
```json
{
  "origin": {
    "latitude": 52.374,
    "longitude": 4.9
  },
  "destination": {
    "latitude": 52.375,
    "longitude": 4.901
  },
  "profile": "cycling",
  "alternatives": true,
  "steps": true
}
```

**Profiles**: `driving`, `walking`, `cycling`

### Mapbox API Key Setup

1. Sign up at [Mapbox](https://www.mapbox.com/)
2. Create an API access token
3. Set the environment variable:
   ```bash
   export MAPBOX_API_KEY=pk.your-mapbox-public-key-here
   ```
4. Or add to `application.properties`:
   ```properties
   mapbox.api.key=pk.your-mapbox-public-key-here
   ```

### PostGIS Spatial Features

The service uses PostGIS for efficient spatial queries:
- **ST_DWithin**: Fast proximity searches using spatial indexes
- **ST_Distance**: Accurate distance calculations on Earth's surface
- **Geography type**: Handles coordinates in WGS84 (SRID 4326)
- **Automatic geometry updates**: Trigger maintains geometry column from lat/lng

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

### Mapbox Integration
Uses Spring WebClient to call Mapbox REST API for:
- Geocoding and reverse geocoding
- Turn-by-turn directions
- Distance calculations
- Lightweight, Spring-native approach without external SDK dependencies

## Future Enhancements

1. Event-driven architecture for company creation sync
2. Database migrations with Flyway/Liquibase
3. Caching with Redis for frequently accessed data (especially for nearby searches)
4. Rate limiting for API endpoints (especially Mapbox API calls)
5. Advanced search and filtering
6. Real-time ride tracking with WebSocket
7. Integration with payment services
8. Mobile app push notifications
9. Geofencing for bike availability zones
10. Heat maps for popular bike locations

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

