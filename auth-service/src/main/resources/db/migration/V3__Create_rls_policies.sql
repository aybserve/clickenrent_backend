-- =====================================================================================================================
-- AUTH SERVICE - ROW LEVEL SECURITY POLICIES v1.0 (Flyway Migration)
-- =====================================================================================================================
-- Module: auth-service
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
-- RLS SETUP: user_company
-- =====================================================================================================================
-- Enable row level security on user_company table
ALTER TABLE user_company ENABLE ROW LEVEL SECURITY;
ALTER TABLE user_company FORCE ROW LEVEL SECURITY;

-- Drop existing policy if it exists (idempotency)
DROP POLICY IF EXISTS user_company_tenant_isolation ON user_company;

-- Create tenant isolation policy
-- Policy: Allow superadmins to see all user_company relationships,
-- B2B users see only relationships for their companies
CREATE POLICY user_company_tenant_isolation ON user_company
USING (
    current_setting('app.is_superadmin', true)::boolean = true
    OR EXISTS (
        SELECT 1 FROM company c 
        WHERE c.id = user_company.company_id 
        AND c.external_id IN (
            SELECT unnest(string_to_array(current_setting('app.company_external_ids', true), ','))
        )
    )
);

-- =====================================================================================================================
-- RLS SETUP: company
-- =====================================================================================================================
-- Enable row level security on company table
ALTER TABLE company ENABLE ROW LEVEL SECURITY;
ALTER TABLE company FORCE ROW LEVEL SECURITY;

-- Drop existing policy if it exists (idempotency)
DROP POLICY IF EXISTS company_tenant_isolation ON company;

-- Create tenant isolation policy
-- Policy: Allow superadmins to see all companies,
-- B2B users see only their companies
CREATE POLICY company_tenant_isolation ON company
USING (
    current_setting('app.is_superadmin', true)::boolean = true
    OR external_id IN (
        SELECT unnest(string_to_array(current_setting('app.company_external_ids', true), ','))
    )
);

-- =====================================================================================================================
-- PERFORMANCE INDEXES FOR RLS
-- =====================================================================================================================
-- Create indexes for RLS performance
CREATE INDEX IF NOT EXISTS idx_user_company_company_external_id ON user_company(company_id);
CREATE INDEX IF NOT EXISTS idx_company_external_id_rls ON company(external_id);

-- =====================================================================================================================
-- END OF RLS POLICY CREATION
-- =====================================================================================================================
-- Note: The PostgresRLSConfig.java component sets the session variables (app.is_superadmin, 
-- app.company_external_ids) before each repository operation via AOP.
-- =====================================================================================================================
