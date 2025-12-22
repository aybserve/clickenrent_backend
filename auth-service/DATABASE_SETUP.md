# Database Setup Guide

This guide explains how to set up the database for the auth-service in different environments.

## Quick Start (Development - Auto Setup) ✅ **DEFAULT**

**The easiest way - tables and test data are created automatically!**

### 1. Make sure PostgreSQL is running

### 2. Create the database (one-time only)

```bash
psql -U postgres -c "CREATE DATABASE clickenrent_auth;"
```

### 3. Start the application

```bash
cd /Users/vitaliyshvetsov/IdeaProjects/backend/auth-service
mvn spring-boot:run
```

**That's it!** The application will:
- Automatically create all tables from your entities
- Load test data from `data.sql` (copy of auth-service.sql with 13 users, countries, addresses, etc.)

### Test Users (Password: Test123!)
- **Superadmin**: `superadmin`
- **Admin**: `admin_john`
- **B2B User**: `hotelowner_max`
- **Customer**: `customer_tom`

---

## Production Setup (Manual - Recommended for Production)

### 1. Create the Database

```bash
# Connect to PostgreSQL
psql -U postgres

# Create the database
CREATE DATABASE clickenrent_auth;

# Exit psql
\q
```

### 2. Run the Schema Script

```bash
# Import the complete schema (includes tables + optional test data)
psql -U postgres -d clickenrent_auth -f auth-service.sql
```

The `auth-service.sql` file contains:
- All table definitions
- Indexes and constraints
- Sample/test data (users, countries, addresses, etc.)

**Note**: If you don't want the test data in production, you can:
- Remove lines 380-520 (test data section) from `auth-service.sql` before running it
- Or manually delete the test data after import

### 3. Configure the Application

Set environment variables:

```bash
export DB_URL=jdbc:postgresql://localhost:5432/clickenrent_auth
export DB_USERNAME=postgres
export DB_PASSWORD=your_secure_password
export JWT_SECRET=$(openssl rand -base64 32)
```

Or use `application-prod.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/clickenrent_auth
spring.datasource.username=postgres
spring.datasource.password=your_secure_password
spring.jpa.hibernate.ddl-auto=validate
spring.sql.init.mode=never
jwt.secret=your_generated_secret
```

### 4. Start the Application

```bash
# Using Maven
mvn spring-boot:run -Dspring-boot.run.profiles=prod

# Or using the JAR
java -jar auth-service.jar --spring.profiles.active=prod
```

## Development Setup

### Option 1: Use auth-service.sql (Recommended)

Same as production setup above, but you can keep the test data.

### Option 2: Use JPA Auto-Schema + data.sql

If you want JPA to create tables automatically:

1. Update `application.properties`:
```properties
spring.jpa.hibernate.ddl-auto=update
spring.sql.init.mode=always
```

2. Start the application - JPA will create tables and `data.sql` will populate test data

**Warning**: This approach is not recommended for production!

## Test Environment

Tests automatically use H2 in-memory database with `src/test/resources/data.sql`.

No manual setup required - just run:

```bash
mvn test
```

## Configuration Summary

| Environment | ddl-auto | sql.init.mode | Database Setup |
|-------------|----------|---------------|----------------|
| **Production** | `validate` | `never` | Manual via `auth-service.sql` |
| **Development (Default)** | `update` | `always` | **Auto via JPA + `data.sql`** ✅ |
| **Development (Manual)** | `validate` | `never` | Manual via `auth-service.sql` |
| **Tests** | `create-drop` | `always` | Auto via H2 + `test/data.sql` |

## Database Schema Updates

When you modify entities:

1. **Development**: 
   - Update the entity classes
   - JPA will validate against existing schema
   - Manually create migration SQL if needed

2. **Production**:
   - Create a migration script (e.g., `V2__add_new_column.sql`)
   - Test in development first
   - Apply to production database manually
   - Update `auth-service.sql` for future deployments

## Troubleshooting

### "Table already exists" error
- You're using `ddl-auto=create` or `update` with an existing database
- Solution: Use `ddl-auto=validate` and manage schema manually

### "Table doesn't exist" error
- Schema not created yet
- Solution: Run `auth-service.sql` to create tables

### Test data appearing in production
- `sql.init.mode=always` is set
- Solution: Set `sql.init.mode=never` in production

### Schema validation fails
- Entity definitions don't match database schema
- Solution: Update database schema or entity classes to match

## Files Reference

- **`auth-service.sql`**: Complete PostgreSQL schema + test data (for manual setup)
- **`src/main/resources/data.sql`**: Test data only (for auto-initialization)
- **`src/test/resources/data.sql`**: Test data for H2 in-memory database
- **`application.properties`**: Default configuration (development)
- **`application-prod.properties.template`**: Production configuration template

