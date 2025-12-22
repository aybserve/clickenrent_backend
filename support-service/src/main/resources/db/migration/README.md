# Database Migration Scripts

This directory contains Flyway migration scripts for the Support Service database.

## Migration Order

1. **V1__add_external_id_reference_fields.sql** - Adds new columns for cross-service externalId references
2. **V2__backfill_entity_external_ids.sql** - Backfills missing externalIds for existing records

## Important Notes

### Phase 2: Populate Cross-Service References

After these migrations run, you need to populate the cross-service reference fields by:

1. Calling Auth Service API to fetch User externalIds
2. Calling Rental Service API to fetch Bike/BikeRental externalIds
3. Updating the new externalId reference fields

This must be done via a data migration job or Spring Boot service that coordinates between services.

## Running Migrations

Migrations will run automatically on application startup if Flyway is configured in `application.properties`:

```properties
spring.flyway.enabled=true
spring.flyway.locations=classpath:db/migration
spring.flyway.baseline-on-migrate=true
```


