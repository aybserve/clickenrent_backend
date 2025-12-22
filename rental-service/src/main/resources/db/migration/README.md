# Database Migration Scripts

This directory contains Flyway migration scripts for the Rental Service database.

## Migration Order

1. **V1__add_external_id_reference_fields.sql** - Adds new columns for cross-service externalId references
2. **V2__backfill_entity_external_ids.sql** - Backfills missing externalIds for existing records

## Important Notes

### Phase 2: Populate Cross-Service References

After these migrations run, you need to populate the cross-service reference fields (userExternalId, companyExternalId, etc.) by:

1. Calling the Auth Service API to fetch User/Company externalIds based on the existing userId/companyId
2. Updating the new externalId reference fields

This can be done via:
- A Spring Boot `@Component` with `@PostConstruct` that runs on startup
- A separate migration script after data sync
- Manual data population script

### Example Population Query

```sql
-- This is just an example - actual implementation needs to call Auth Service API
-- UPDATE rental r 
-- SET user_external_id = (SELECT external_id FROM auth_service.users WHERE id = r.user_id),
--     company_external_id = (SELECT external_id FROM auth_service.company WHERE id = r.company_id);
```

## Running Migrations

Migrations will run automatically on application startup if Flyway is configured in `application.properties`:

```properties
spring.flyway.enabled=true
spring.flyway.locations=classpath:db/migration
spring.flyway.baseline-on-migrate=true
```


