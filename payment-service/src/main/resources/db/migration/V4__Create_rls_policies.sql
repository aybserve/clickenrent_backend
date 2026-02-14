-- =====================================================================================================================
-- PAYMENT SERVICE - ROW LEVEL SECURITY POLICIES v1.0 (Flyway Migration)
-- =====================================================================================================================
-- Module: payment-service
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
-- ENABLE RLS ON TENANT-SCOPED TABLES
-- =====================================================================================================================
ALTER TABLE b2b_revenue_share_payouts ENABLE ROW LEVEL SECURITY;
ALTER TABLE financial_transactions ENABLE ROW LEVEL SECURITY;
ALTER TABLE location_bank_accounts ENABLE ROW LEVEL SECURITY;

-- Force RLS even for table owner (important for security)
ALTER TABLE b2b_revenue_share_payouts FORCE ROW LEVEL SECURITY;
ALTER TABLE financial_transactions FORCE ROW LEVEL SECURITY;
ALTER TABLE location_bank_accounts FORCE ROW LEVEL SECURITY;

-- =====================================================================================================================
-- RLS POLICY: b2b_revenue_share_payouts
-- =====================================================================================================================
-- Allows access if:
-- 1. User is superadmin (bypasses all filters), OR
-- 2. Payout belongs to one of user's companies
DROP POLICY IF EXISTS b2b_revenue_share_payouts_tenant_isolation ON b2b_revenue_share_payouts;
CREATE POLICY b2b_revenue_share_payouts_tenant_isolation ON b2b_revenue_share_payouts
    FOR ALL
    USING (
        COALESCE(current_setting('app.is_superadmin', true)::boolean, false) = true
        OR
        company_external_id = ANY(
            string_to_array(
                COALESCE(current_setting('app.company_external_ids', true), ''),
                ','
            )
        )
    );

-- =====================================================================================================================
-- RLS POLICY: financial_transactions
-- =====================================================================================================================
-- Allows access if:
-- 1. User is superadmin (bypasses all filters), OR
-- 2. Transaction belongs to one of user's companies (via company_external_id), OR
-- 3. company_external_id is NULL (for backward compatibility or customer transactions)
DROP POLICY IF EXISTS financial_transactions_tenant_isolation ON financial_transactions;
CREATE POLICY financial_transactions_tenant_isolation ON financial_transactions
    FOR ALL
    USING (
        COALESCE(current_setting('app.is_superadmin', true)::boolean, false) = true
        OR
        company_external_id = ANY(
            string_to_array(
                COALESCE(current_setting('app.company_external_ids', true), ''),
                ','
            )
        )
        OR
        company_external_id IS NULL
    );

-- =====================================================================================================================
-- RLS POLICY: location_bank_accounts
-- =====================================================================================================================
-- Allows access if:
-- 1. User is superadmin (bypasses all filters), OR
-- 2. Bank account belongs to one of user's companies
DROP POLICY IF EXISTS location_bank_accounts_tenant_isolation ON location_bank_accounts;
CREATE POLICY location_bank_accounts_tenant_isolation ON location_bank_accounts
    FOR ALL
    USING (
        COALESCE(current_setting('app.is_superadmin', true)::boolean, false) = true
        OR
        company_external_id = ANY(
            string_to_array(
                COALESCE(current_setting('app.company_external_ids', true), ''),
                ','
            )
        )
    );

-- =====================================================================================================================
-- PERFORMANCE INDEXES FOR RLS
-- =====================================================================================================================
-- These indexes ensure RLS policies don't slow down queries
CREATE INDEX IF NOT EXISTS idx_b2b_revenue_share_payouts_company_rls 
    ON b2b_revenue_share_payouts(company_external_id) WHERE is_deleted = false;
CREATE INDEX IF NOT EXISTS idx_financial_transactions_company_rls 
    ON financial_transactions(company_external_id) WHERE is_deleted = false;
CREATE INDEX IF NOT EXISTS idx_location_bank_accounts_company_rls 
    ON location_bank_accounts(company_external_id) WHERE is_deleted = false;

-- =====================================================================================================================
-- END OF RLS POLICY CREATION
-- =====================================================================================================================
-- Note: The PostgresRLSConfig.java component sets the session variables (app.is_superadmin, 
-- app.company_external_ids) before each repository operation via AOP.
-- =====================================================================================================================
