-- =====================================================================================================================
-- NOTIFICATION SERVICE - SEQUENCE RESET (Flyway Testdata V101)
-- =====================================================================================================================
-- Module: notification-service
-- Database: PostgreSQL
-- Description: Reset sequences to correct values after inserting sample data.
--              Only loaded when 'staging' or 'dev' profile is active.
-- 
-- Author: Vitaliy Shvetsov
-- =====================================================================================================================

SELECT setval('push_tokens_id_seq', (SELECT COALESCE(MAX(id), 1) FROM push_tokens));
SELECT setval('notification_logs_id_seq', (SELECT COALESCE(MAX(id), 1) FROM notification_logs));
SELECT setval('notification_preferences_id_seq', (SELECT COALESCE(MAX(id), 1) FROM notification_preferences));

-- =====================================================================================================================
-- END OF SEQUENCE RESET
-- =====================================================================================================================
