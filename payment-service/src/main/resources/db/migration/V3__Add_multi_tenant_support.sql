-- =====================================================================================================================
-- PAYMENT SERVICE - MULTI-TENANT SUPPORT v1.0 (Flyway Migration)
-- =====================================================================================================================
-- Module: payment-service
-- Database: PostgreSQL
-- Version: 1.0
-- Description: Add multi-tenant support columns and indexes
-- 
-- Author: Vitaliy Shvetsov
-- =====================================================================================================================

-- Add company_external_id column to financial_transactions for tenant isolation
ALTER TABLE financial_transactions ADD COLUMN IF NOT EXISTS company_external_id VARCHAR(100);

-- Index for company-based queries
CREATE INDEX IF NOT EXISTS idx_financial_transaction_company ON financial_transactions(company_external_id);

-- =====================================================================================================================
-- END OF MULTI-TENANT SCHEMA UPDATES
-- =====================================================================================================================
