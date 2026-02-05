-- =====================================================================================================================
-- Migration: Add Partial Unique Index for user_preferences
-- =====================================================================================================================
-- Purpose: Fix soft delete conflict with unique constraint on user_id
-- Date: 2026-02-05
-- 
-- Problem: 
--   Simple UNIQUE constraint on user_id prevented soft-deleted records from being recreated
--   because the constraint applies to ALL rows (including soft-deleted ones).
--
-- Solution:
--   Replace column-level UNIQUE constraint with partial unique index that only applies
--   to active records (WHERE is_deleted = false).
--
-- This allows:
--   ✅ Multiple soft-deleted records for same user (audit trail)
--   ✅ Only one active preference per user (enforced by partial index)
-- =====================================================================================================================

-- Step 1: Drop the old unique constraint if it exists
-- Note: Constraint name may vary, check with: 
-- SELECT constraint_name FROM information_schema.table_constraints 
-- WHERE table_name = 'user_preferences' AND constraint_type = 'UNIQUE';
ALTER TABLE user_preferences 
DROP CONSTRAINT IF EXISTS user_preferences_user_id_key;

-- Some databases may have a different constraint name
ALTER TABLE user_preferences 
DROP CONSTRAINT IF EXISTS uk_user_preferences_user_id;

-- Step 2: Create the partial unique index
-- This index only enforces uniqueness on active (non-deleted) records
CREATE UNIQUE INDEX IF NOT EXISTS user_preferences_user_id_active_unique 
ON user_preferences(user_id) 
WHERE is_deleted = false;

-- Step 3: Verify the index was created
-- You can verify with: 
-- SELECT indexname, indexdef FROM pg_indexes 
-- WHERE tablename = 'user_preferences' AND indexname = 'user_preferences_user_id_active_unique';

-- =====================================================================================================================
-- OPTIONAL CLEANUP (Uncomment if you want to remove audit trail)
-- =====================================================================================================================
-- WARNING: This will permanently delete soft-deleted records!
-- Only run this if you don't need the audit trail.

-- Clean up soft-deleted preferences (OPTIONAL - keeps audit trail by default)
-- DELETE FROM user_preferences WHERE is_deleted = true;

-- Reset sequence if needed (after cleanup)
-- SELECT setval('user_preferences_id_seq', (SELECT COALESCE(MAX(id), 1) FROM user_preferences));

-- =====================================================================================================================
-- END OF MIGRATION
-- =====================================================================================================================
