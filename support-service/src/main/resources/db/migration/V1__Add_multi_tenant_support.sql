-- =====================================================================================================================
-- SUPPORT SERVICE - MULTI-TENANT SUPPORT v1.0 (Flyway Migration)
-- =====================================================================================================================
-- Module: support-service
-- Database: PostgreSQL
-- Version: 1.0
-- Description: Add multi-tenant support columns and indexes
-- 
-- Author: Vitaliy Shvetsov
-- =====================================================================================================================

-- Add company_external_id column to support_request for multi-tenant isolation
ALTER TABLE support_request ADD COLUMN IF NOT EXISTS company_external_id VARCHAR(100);

-- Index for company-based queries
CREATE INDEX IF NOT EXISTS idx_support_request_company ON support_request(company_external_id);

-- =====================================================================================================================
-- END OF MULTI-TENANT SCHEMA UPDATES
-- =====================================================================================================================
