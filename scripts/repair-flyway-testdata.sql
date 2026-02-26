-- =====================================================================================================================
-- REPAIR FLYWAY - Remove Test Data Migration Entries
-- =====================================================================================================================
-- Purpose: Delete V100/V101 test data migrations from flyway_schema_history so Flyway can re-run them
--          with the new checksums after we fixed the sequence reset scripts.
-- 
-- Run this script on each database that has test data migrations:
-- - clickenrent-auth
-- - clickenrent-rental
-- - clickenrent-payment
-- - clickenrent-support
-- - clickenrent-notification
-- =====================================================================================================================

-- Show current state before deletion
SELECT version, description, type, installed_on, success, checksum 
FROM flyway_schema_history 
WHERE version IN ('100', '101')
ORDER BY version;

-- Delete the test data migration entries
DELETE FROM flyway_schema_history WHERE version = '100' AND description LIKE '%sample%';
DELETE FROM flyway_schema_history WHERE version = '100' AND description LIKE '%Reset%';
DELETE FROM flyway_schema_history WHERE version = '101' AND description LIKE '%Reset%';

-- Confirm deletion
SELECT version, description, type, installed_on, success 
FROM flyway_schema_history 
ORDER BY installed_rank DESC 
LIMIT 10;

-- =====================================================================================================================
-- After running this script, restart the affected pods and Flyway will re-run V100/V101 with new checksums
-- =====================================================================================================================
