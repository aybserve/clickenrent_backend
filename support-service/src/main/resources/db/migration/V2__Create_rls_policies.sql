-- =====================================================================================================================
-- SUPPORT SERVICE - ROW LEVEL SECURITY POLICIES v1.0 (Flyway Migration)
-- =====================================================================================================================
-- Module: support-service
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
-- RLS SETUP: support_request
-- =====================================================================================================================
-- Enable row level security on support_request table
ALTER TABLE support_request ENABLE ROW LEVEL SECURITY;
ALTER TABLE support_request FORCE ROW LEVEL SECURITY;

-- Drop existing policy if it exists (idempotency)
DROP POLICY IF EXISTS support_request_tenant_isolation ON support_request;

-- Create tenant isolation policy
-- Policy: Allow superadmins to see all support requests,
-- B2B users see only their company's support requests
CREATE POLICY support_request_tenant_isolation ON support_request
USING (
    current_setting('app.is_superadmin', true)::boolean = true
    OR company_external_id IN (
        SELECT unnest(string_to_array(current_setting('app.company_external_ids', true), ','))
    )
);

-- Create index for RLS performance
CREATE INDEX IF NOT EXISTS idx_support_request_company_rls 
    ON support_request(company_external_id) WHERE is_deleted = false;

-- =====================================================================================================================
-- END OF RLS POLICY CREATION
-- =====================================================================================================================
-- Note: The PostgresRLSConfig.java component sets the session variables (app.is_superadmin, 
-- app.company_external_ids) before each repository operation via AOP.
-- =====================================================================================================================
