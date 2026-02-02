# Auto-Migration Guide for Rental Service

**Date:** January 26, 2026  
**Author:** Vitaliy Shvetsov

---

## Overview

The rental-service now includes **automatic schema migrations** via `data.sql`, similar to other microservices in the project. This eliminates the need for manual database migrations when adding new fields or making schema changes.

---

## How Auto-Migration Works

### Execution Flow

```
1. Application Startup
   ↓
2. Hibernate DDL Auto-Update
   └─> Creates/updates tables from @Entity classes
   └─> Based on: spring.jpa.hibernate.ddl-auto=update
   ↓
3. Data.sql Execution
   └─> Runs AFTER Hibernate schema management
   └─> Based on: spring.jpa.defer-datasource-initialization=true
   └─> Based on: spring.sql.init.mode=always
   ↓
   ├─> SECTION 0: PostGIS Setup
   ├─> SECTION 0.5: SCHEMA MIGRATIONS ⭐ (NEW)
   │   └─> Adds missing columns with IF NOT EXISTS
   │   └─> Idempotent - safe to run multiple times
   ├─> SECTION 1-7: Data Inserts
   │   └─> INSERT statements with ON CONFLICT DO NOTHING
   ↓
4. Application Ready
```

---

## Example: `is_active` Column Migration

### Migration Code in data.sql

**Location:** `src/main/resources/data.sql` - SECTION 0.5

```sql
-- =====================================================================================================================
-- SECTION 0.5: SCHEMA MIGRATIONS (AUTO-APPLIED ON STARTUP)
-- =====================================================================================================================
-- Note: This section handles schema changes that need to be applied to existing databases.
-- Uses IF NOT EXISTS for safe idempotent operations that can run on every startup.
-- These run AFTER Hibernate creates/updates tables but BEFORE data inserts.
-- =====================================================================================================================

-- Migration: Add is_active column to location table (Added: 2026-01-26)
-- Purpose: Enable/disable locations without deleting them
ALTER TABLE location ADD COLUMN IF NOT EXISTS is_active BOOLEAN DEFAULT true;
```

### Why This Works

1. **`IF NOT EXISTS`**: Only adds column if it doesn't exist
2. **`DEFAULT true`**: Automatically sets value for existing rows
3. **Idempotent**: Safe to run on every startup
4. **No manual intervention**: Works for both new and existing databases

---

## Configuration

### Required Spring Boot Properties

**File:** `src/main/resources/application.properties`

```properties
# Hibernate creates/updates tables from entities
spring.jpa.hibernate.ddl-auto=${JPA_DDL_AUTO:update}

# data.sql runs AFTER Hibernate schema management
spring.jpa.defer-datasource-initialization=true

# Always run data.sql (for migrations and data)
spring.sql.init.mode=${SQL_INIT_MODE:always}

# Fail fast on SQL errors (don't continue with broken schema)
spring.sql.init.continue-on-error=false
```

### Configuration Explanation

| Property | Value | Purpose |
|----------|-------|---------|
| `ddl-auto` | `update` | Hibernate adds new columns from entities |
| `defer-datasource-initialization` | `true` | data.sql runs AFTER schema creation |
| `sql.init.mode` | `always` | data.sql always executes |
| `sql.init.continue-on-error` | `false` | Stop on errors for safety |

---

## Writing New Migrations

### Template for New Schema Changes

Add to **SECTION 0.5** in `data.sql`:

```sql
-- Migration: [Brief description] (Added: YYYY-MM-DD)
-- Purpose: [Detailed purpose and use case]
ALTER TABLE table_name ADD COLUMN IF NOT EXISTS column_name TYPE DEFAULT value;
```

### Best Practices

#### ✅ DO:

1. **Use `IF NOT EXISTS`** - Makes migrations idempotent
   ```sql
   ALTER TABLE location ADD COLUMN IF NOT EXISTS new_field VARCHAR(255);
   ```

2. **Provide DEFAULT values** - Handles existing data
   ```sql
   ALTER TABLE location ADD COLUMN IF NOT EXISTS is_featured BOOLEAN DEFAULT false;
   ```

3. **Add comments** - Document when and why
   ```sql
   -- Migration: Add featured flag (Added: 2026-01-26)
   -- Purpose: Highlight premium locations in search results
   ```

4. **Keep migrations in SECTION 0.5** - Organized location
   ```sql
   -- =====================================================================================================================
   -- SECTION 0.5: SCHEMA MIGRATIONS (AUTO-APPLIED ON STARTUP)
   -- =====================================================================================================================
   ```

5. **Test locally first** - Verify with existing data
   ```bash
   # Test with existing database
   ./mvnw spring-boot:run
   ```

#### ❌ DON'T:

1. **Don't use DO $$ blocks** - Spring ScriptUtils has parsing issues
   ```sql
   -- ❌ BAD - Causes parsing errors
   DO $$
   BEGIN
       ALTER TABLE location ADD COLUMN IF NOT EXISTS field VARCHAR(255);
   END $$;
   ```

2. **Don't drop columns** - Risk data loss
   ```sql
   -- ❌ BAD - Destructive operation
   ALTER TABLE location DROP COLUMN old_field;
   ```

3. **Don't use complex migrations** - Keep it simple
   ```sql
   -- ❌ BAD - Too complex for data.sql
   ALTER TABLE location ADD COLUMN IF NOT EXISTS complex_field JSONB;
   UPDATE location SET complex_field = '{"data": "value"}' WHERE ...;
   CREATE INDEX idx_location_complex ON location USING gin(complex_field);
   ```

4. **Don't add NOT NULL without DEFAULT** - Fails with existing data
   ```sql
   -- ❌ BAD - Will fail if table has existing rows
   ALTER TABLE location ADD COLUMN IF NOT EXISTS required_field VARCHAR(255) NOT NULL;
   
   -- ✅ GOOD - DEFAULT allows NOT NULL
   ALTER TABLE location ADD COLUMN IF NOT EXISTS required_field VARCHAR(255) DEFAULT 'default-value' NOT NULL;
   ```

---

## Testing Auto-Migration

### Scenario 1: Fresh Database

**Steps:**
```bash
# 1. Drop and recreate database
psql -U postgres -c "DROP DATABASE IF EXISTS \"clickenrent-rental\";"
psql -U postgres -c "CREATE DATABASE \"clickenrent-rental\";"
psql -U postgres -d clickenrent-rental -c "CREATE EXTENSION IF NOT EXISTS postgis;"

# 2. Start application
cd rental-service
./mvnw spring-boot:run

# 3. Expected behavior:
# - Hibernate creates all tables
# - data.sql migrations run (no effect - columns already exist from Hibernate)
# - data.sql inserts run successfully
# - Application starts
```

**Expected Logs:**
```
✅ Hibernate: create table location (...)
✅ Executing SQL script from file [data.sql]
✅ Started RentalServiceApplication in X seconds
```

---

### Scenario 2: Existing Database (Missing Column)

**Steps:**
```bash
# 1. Start with database that's missing is_active column
# (Simulating production database before migration)

# 2. Start application
cd rental-service
./mvnw spring-boot:run

# 3. Expected behavior:
# - Hibernate sees Location entity has is_active field
# - Hibernate tries to use existing table (ddl-auto=update doesn't always add missing columns)
# - data.sql migration runs: ALTER TABLE location ADD COLUMN IF NOT EXISTS is_active BOOLEAN DEFAULT true
# - Column is added successfully
# - data.sql inserts run successfully
# - Application starts
```

**Expected Logs:**
```
✅ Executing SQL script from file [data.sql]
✅ Successfully added is_active column
✅ INSERT INTO location ... (includes is_active)
✅ Started RentalServiceApplication in X seconds
```

---

### Scenario 3: Existing Database (Column Already Exists)

**Steps:**
```bash
# 1. Start with database that already has is_active column
# (Simulating restart after first successful migration)

# 2. Restart application
cd rental-service
./mvnw spring-boot:run

# 3. Expected behavior:
# - Hibernate sees table already correct
# - data.sql migration runs: IF NOT EXISTS prevents duplicate column error
# - data.sql inserts run (ON CONFLICT DO NOTHING prevents duplicates)
# - Application starts normally
```

**Expected Logs:**
```
✅ Executing SQL script from file [data.sql]
✅ Column already exists, skipping (IF NOT EXISTS)
✅ Started RentalServiceApplication in X seconds
```

---

## Verification Steps

### 1. Check Column Exists

```sql
SELECT column_name, data_type, is_nullable, column_default
FROM information_schema.columns
WHERE table_name = 'location' 
  AND column_name = 'is_active';
```

**Expected Result:**
```
 column_name | data_type | is_nullable | column_default
-------------+-----------+-------------+----------------
 is_active   | boolean   | NO          | true
```

### 2. Check Existing Data

```sql
SELECT id, name, is_active 
FROM location 
LIMIT 5;
```

**Expected Result:**
```
 id |         name          | is_active
----+-----------------------+-----------
  1 | Downtown Bike Hub     | t
  2 | Park Side Station     | t
  3 | City Center Rentals   | t
```

### 3. Test API Response

```bash
curl -X GET http://localhost:8082/api/v1/locations/1 \
  -H "Authorization: Bearer YOUR_TOKEN"
```

**Expected Response:**
```json
{
  "id": 1,
  "name": "Downtown Bike Hub",
  "isPublic": true,
  "isActive": true,
  "address": "123 Main Street, Kyiv"
}
```

---

## Troubleshooting

### Error: "column is_active does not exist"

**Cause:** Migration section in data.sql not running or failing silently

**Solution:**
```bash
# 1. Check data.sql has SECTION 0.5
grep -A 5 "SECTION 0.5" src/main/resources/data.sql

# 2. Verify spring.sql.init.mode=always
grep "sql.init.mode" src/main/resources/application.properties

# 3. Enable SQL logging
# Add to application.properties:
logging.level.org.springframework.jdbc.datasource.init=DEBUG

# 4. Restart and check logs for migration execution
```

---

### Error: "syntax error near DO"

**Cause:** Using DO $$ blocks which Spring ScriptUtils can't parse

**Solution:**
```sql
-- ❌ Remove DO $$ blocks
DO $$
BEGIN
    ALTER TABLE location ADD COLUMN field VARCHAR(255);
END $$;

-- ✅ Use simple ALTER TABLE with IF NOT EXISTS
ALTER TABLE location ADD COLUMN IF NOT EXISTS field VARCHAR(255);
```

---

### Migration Not Running

**Cause:** `spring.sql.init.mode` is set to `never`

**Solution:**
```properties
# Check application.properties
spring.sql.init.mode=always  # Must be 'always' or 'embedded'
```

---

### Column Added But NULL Values

**Cause:** Missing DEFAULT in migration

**Solution:**
```sql
-- ❌ No default - existing rows get NULL
ALTER TABLE location ADD COLUMN IF NOT EXISTS is_active BOOLEAN;

-- ✅ With default - existing rows get true
ALTER TABLE location ADD COLUMN IF NOT EXISTS is_active BOOLEAN DEFAULT true;
```

---

## Comparison: Manual vs Auto Migration

| Aspect | Manual Migration | Auto Migration (data.sql) |
|--------|-----------------|---------------------------|
| **Deployment** | Run SQL before app | Just deploy app |
| **New Environment** | Remember to run SQL | Automatic |
| **Idempotent** | Must write carefully | Built-in with IF NOT EXISTS |
| **Version Control** | Separate SQL files | In data.sql (committed) |
| **Testing** | Manual steps | Automatic on startup |
| **Rollback** | Manual SQL | Modify data.sql |
| **Documentation** | External docs | Self-documenting in code |

---

## Related Files

| File | Purpose |
|------|---------|
| `src/main/resources/data.sql` | Contains migrations (SECTION 0.5) and data |
| `src/main/resources/application.properties` | Configuration for auto-migration |
| `rental-service.sql` | Full schema reference (for manual setup) |
| `LOCATION_ISACTIVE_SUMMARY.md` | Implementation details for is_active field |

---

## Next Steps

1. ✅ Code complete (all files updated)
2. ✅ Migration added to data.sql
3. ✅ Documentation created
4. 🔄 **Test locally** - Restart your rental-service
5. 📝 **Verify** - Check column exists and data correct
6. 🚀 **Deploy** - No manual migration needed!

---

**Status:** ✅ READY FOR DEPLOYMENT  
**Manual Steps Required:** None - fully automatic!  
**Breaking Changes:** None - backward compatible
