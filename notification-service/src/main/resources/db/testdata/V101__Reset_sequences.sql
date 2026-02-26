-- =====================================================================================================================
-- NOTIFICATION SERVICE - SEQUENCE RESET (Flyway Testdata V101)
-- =====================================================================================================================
-- Module: notification-service
-- Database: PostgreSQL
-- Description: Reset sequences to correct values after inserting sample data.
--              Safe version: checks if sequence and table exist before resetting.
-- 
-- Author: Vitaliy Shvetsov
-- =====================================================================================================================

DO $$
BEGIN
    -- Reset each sequence only if both the sequence and table exist
    IF EXISTS (SELECT 1 FROM pg_class WHERE relkind = 'S' AND relname = 'push_tokens_id_seq')
       AND EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'push_tokens') THEN
        PERFORM setval('push_tokens_id_seq', GREATEST((SELECT COALESCE(MAX(id), 0) FROM push_tokens), 1));
    END IF;

    IF EXISTS (SELECT 1 FROM pg_class WHERE relkind = 'S' AND relname = 'notification_logs_id_seq')
       AND EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'notification_logs') THEN
        PERFORM setval('notification_logs_id_seq', GREATEST((SELECT COALESCE(MAX(id), 0) FROM notification_logs), 1));
    END IF;

    IF EXISTS (SELECT 1 FROM pg_class WHERE relkind = 'S' AND relname = 'notification_preferences_id_seq')
       AND EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'notification_preferences') THEN
        PERFORM setval('notification_preferences_id_seq', GREATEST((SELECT COALESCE(MAX(id), 0) FROM notification_preferences), 1));
    END IF;
END $$;

-- =====================================================================================================================
-- END OF SEQUENCE RESET
-- =====================================================================================================================
