-- =====================================================================================================================
-- RENTAL SERVICE - SCHEMA MIGRATIONS (Flyway Migration V2)
-- =====================================================================================================================
-- Module: rental-service
-- Database: PostgreSQL
-- Description: Schema changes that need to be applied to existing databases.
--              Uses IF NOT EXISTS for safe idempotent operations.
-- 
-- Author: Vitaliy Shvetsov
-- =====================================================================================================================

-- Migration: Add is_active column to location table
-- Purpose: Enable/disable locations without deleting them
DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'location') THEN
        ALTER TABLE location ADD COLUMN IF NOT EXISTS is_active BOOLEAN DEFAULT true;
    END IF;
END $$;

-- =====================================================================================================================
-- END OF SCHEMA MIGRATIONS
-- =====================================================================================================================
