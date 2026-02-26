-- =====================================================================================================================
-- GRANT BYPASSRLS PERMISSION TO FLYWAY USER
-- =====================================================================================================================
-- Purpose: Allow the postgres user to bypass Row-Level Security (RLS) policies during Flyway migrations.
--          This is needed because test data migrations insert rows without application context (user_id, 
--          company_id, etc.) which would normally be blocked by RLS policies.
-- 
-- Security Note: This affects ALL operations by the postgres user, not just migrations.
--                In production, consider using a separate migration-specific user instead.
-- =====================================================================================================================

-- Show current RLS bypass status for postgres user
SELECT rolname, rolbypassrls 
FROM pg_roles 
WHERE rolname = 'postgres';

-- Grant BYPASSRLS permission to postgres user
ALTER USER postgres BYPASSRLS;

-- Verify the change
SELECT rolname, rolbypassrls 
FROM pg_roles 
WHERE rolname = 'postgres';

-- Expected result: rolbypassrls should now be 't' (true)

-- =====================================================================================================================
-- After granting BYPASSRLS, restart the affected pods so Flyway can run successfully:
--   kubectl rollout restart deployment/auth-service -n clickenrent
--   kubectl rollout restart deployment/rental-service -n clickenrent
--   kubectl rollout restart deployment/payment-service -n clickenrent
--   kubectl rollout restart deployment/support-service -n clickenrent
--   kubectl rollout restart deployment/notification-service -n clickenrent
-- =====================================================================================================================
