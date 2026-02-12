-- =====================================================================================================================
-- NOTIFICATION SERVICE - MULTI-TENANT SUPPORT (Flyway Migration V5)
-- =====================================================================================================================
-- Module: notification-service
-- Database: PostgreSQL
-- Description: Add multi-tenant columns and indexes to notification_logs.
-- 
-- Author: Vitaliy Shvetsov
-- =====================================================================================================================

ALTER TABLE notification_logs ADD COLUMN IF NOT EXISTS company_external_id VARCHAR(100);
ALTER TABLE notification_logs ADD COLUMN IF NOT EXISTS notification_category VARCHAR(20) DEFAULT 'USER';
CREATE INDEX IF NOT EXISTS idx_notification_logs_company ON notification_logs(company_external_id);

-- =====================================================================================================================
-- END OF MULTI-TENANT SUPPORT
-- =====================================================================================================================
