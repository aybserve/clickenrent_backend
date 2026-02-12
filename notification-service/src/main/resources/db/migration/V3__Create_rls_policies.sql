-- =====================================================================================================================
-- NOTIFICATION SERVICE - ROW LEVEL SECURITY POLICIES v1.0 (Flyway Migration)
-- =====================================================================================================================
-- Module: notification-service
-- Database: PostgreSQL
-- Version: 1.0
-- Description: Create RLS policies for multi-tenant isolation
-- 
-- IMPORTANT: Uses DROP POLICY IF EXISTS for idempotency
-- This allows the migration to run on both fresh and existing databases without errors
-- 
-- Author: Vitaliy Shvetsov
-- =====================================================================================================================

-- =====================================================================================================================
-- RLS SETUP: notification_logs
-- =====================================================================================================================
-- Enable row level security on notification_logs table
ALTER TABLE notification_logs ENABLE ROW LEVEL SECURITY;
ALTER TABLE notification_logs FORCE ROW LEVEL SECURITY;

-- Drop existing policy if it exists (idempotency)
DROP POLICY IF EXISTS notification_logs_tenant_isolation ON notification_logs;

-- Create tenant isolation policy
-- Policy: Allow superadmins to see all notifications, B2B users see their company's notifications,
-- and all users see user-scoped notifications (company_external_id IS NULL)
CREATE POLICY notification_logs_tenant_isolation ON notification_logs
USING (
    current_setting('app.is_superadmin', true)::boolean = true
    OR company_external_id IS NULL
    OR company_external_id IN (
        SELECT unnest(string_to_array(current_setting('app.company_external_ids', true), ','))
    )
);

-- =====================================================================================================================
-- END OF RLS POLICY CREATION
-- =====================================================================================================================
-- Note: The PostgresRLSConfig.java component sets the session variables (app.is_superadmin, 
-- app.company_external_ids) before each repository operation via AOP.
-- =====================================================================================================================
