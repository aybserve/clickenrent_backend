-- =====================================================================================================================
-- NOTIFICATION SERVICE - INDEXES v1.0 (Flyway Migration)
-- =====================================================================================================================
-- Module: notification-service
-- Database: PostgreSQL
-- Version: 1.0
-- Description: Create indexes for notification service tables
-- 
-- Author: Vitaliy Shvetsov
-- =====================================================================================================================

-- =====================================================================================================================
-- INDEXES: notification_preferences
-- =====================================================================================================================
CREATE INDEX IF NOT EXISTS idx_notification_preferences_user_external_id 
    ON notification_preferences(user_external_id);

-- =====================================================================================================================
-- INDEXES: push_tokens
-- =====================================================================================================================
CREATE INDEX IF NOT EXISTS idx_push_tokens_user_external_id 
    ON push_tokens(user_external_id);

CREATE INDEX IF NOT EXISTS idx_push_tokens_expo_token 
    ON push_tokens(expo_push_token);

-- =====================================================================================================================
-- INDEXES: notification_logs
-- =====================================================================================================================
CREATE INDEX IF NOT EXISTS idx_notification_logs_user_external_id 
    ON notification_logs(user_external_id);

CREATE INDEX IF NOT EXISTS idx_notification_logs_type 
    ON notification_logs(notification_type);

CREATE INDEX IF NOT EXISTS idx_notification_logs_status 
    ON notification_logs(status);

CREATE INDEX IF NOT EXISTS idx_notification_logs_company 
    ON notification_logs(company_external_id);

-- =====================================================================================================================
-- END OF INDEX CREATION
-- =====================================================================================================================
