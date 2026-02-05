# Soft Delete + Unique Constraint Fix for UserPreference

## Problem

The `user_preferences` table was experiencing conflicts when using the reset endpoint due to a combination of:
1. **Soft delete** implementation (sets `is_deleted = true` instead of deleting rows)
2. **Simple UNIQUE constraint** on `user_id` that applies to ALL rows (including soft-deleted ones)

### Error
```
ERROR: duplicate key value violates unique constraint "user_preferences_user_id_key"
Detail: Key (user_id)=(16) already exists.
```

### Root Cause
When resetting preferences:
1. Existing preference is soft-deleted (`is_deleted = true`)
2. System tries to INSERT new preference with same `user_id`
3. Database rejects INSERT because `user_id` already exists (in soft-deleted row)

---

## Solution: Partial Unique Index

Replaced simple UNIQUE constraint with a **partial unique index** that only enforces uniqueness on active (non-deleted) records.

### Changes Made

#### 1. Database Schema (`data.sql`)

**Before:**
```sql
CREATE TABLE user_preferences (
    id       BIGSERIAL PRIMARY KEY,
    user_id  BIGINT NOT NULL UNIQUE,  -- ❌ UNIQUE applies to ALL rows
    ...
);
```

**After:**
```sql
CREATE TABLE user_preferences (
    id       BIGSERIAL PRIMARY KEY,
    user_id  BIGINT NOT NULL,  -- ✅ No column-level UNIQUE constraint
    ...
);

-- Partial unique index: only active records must be unique
CREATE UNIQUE INDEX user_preferences_user_id_active_unique 
ON user_preferences(user_id) 
WHERE is_deleted = false;
```

#### 2. JPA Entity (`UserPreference.java`)

**Before:**
```java
@JoinColumn(name = "user_id", nullable = false, unique = true)
```

**After:**
```java
@JoinColumn(name = "user_id", nullable = false)
// Uniqueness enforced by partial index, not JPA
```

---

## Migration for Existing Databases

If you already have a database with the old schema, run this migration:

```sql
-- Step 1: Drop the old unique constraint
ALTER TABLE user_preferences 
DROP CONSTRAINT IF EXISTS user_preferences_user_id_key;

-- Step 2: Create the partial unique index
CREATE UNIQUE INDEX IF NOT EXISTS user_preferences_user_id_active_unique 
ON user_preferences(user_id) 
WHERE is_deleted = false;

-- Step 3 (Optional): Clean up old soft-deleted records if desired
-- DELETE FROM user_preferences WHERE is_deleted = true;
```

---

## Benefits

✅ **Soft delete works correctly** - Can delete and recreate preferences without conflicts  
✅ **Audit trail maintained** - Soft-deleted records are kept for history  
✅ **Data integrity preserved** - Only one active preference per user  
✅ **Standard pattern** - Common PostgreSQL solution for soft-delete + unique constraints  

---

## How It Works

### Active Records (is_deleted = false)
```
user_id | is_deleted
--------|------------
   1    |   false    ✅ Only one active record per user
   2    |   false    ✅
   3    |   false    ✅
```

### With Soft Deletes (Audit Trail)
```
user_id | is_deleted
--------|------------
   1    |   false    ✅ Current active preference
   1    |   true     ✅ Old preference (soft-deleted, kept for audit)
   1    |   true     ✅ Even older preference (soft-deleted)
   2    |   false    ✅
```

The partial index only enforces uniqueness WHERE `is_deleted = false`, so:
- ✅ Multiple soft-deleted records for same user = OK
- ✅ One active record per user = Enforced by index
- ❌ Two active records for same user = Blocked by index

---

## Testing

### Test Reset Endpoint
```bash
# First reset - should work
POST /api/v1/users/16/preferences/reset

# Second reset - should now work (previously failed)
POST /api/v1/users/16/preferences/reset

# Third reset - should still work
POST /api/v1/users/16/preferences/reset
```

### Verify Database State
```sql
-- Check active preference
SELECT * FROM user_preferences 
WHERE user_id = 16 AND is_deleted = false;
-- Should return 1 row

-- Check audit trail
SELECT * FROM user_preferences 
WHERE user_id = 16 AND is_deleted = true;
-- Should return previous soft-deleted records
```

---

## Related Files

- `src/main/resources/data.sql` - Schema definition
- `src/main/java/org/clickenrent/authservice/entity/UserPreference.java` - Entity
- `src/main/java/org/clickenrent/authservice/service/UserPreferenceService.java` - Reset logic
- `src/main/java/org/clickenrent/authservice/controller/UserPreferenceController.java` - Endpoint

---

## PostgreSQL Reference

Partial indexes are a standard PostgreSQL feature for this exact use case:
- [PostgreSQL Partial Indexes Documentation](https://www.postgresql.org/docs/current/indexes-partial.html)
- Syntax: `CREATE UNIQUE INDEX name ON table(column) WHERE condition;`
