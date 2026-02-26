-- =====================================================================================================================
-- AUTH SERVICE - SEQUENCE RESET (Flyway Testdata V101)
-- =====================================================================================================================
-- Module: auth-service
-- Database: PostgreSQL
-- Description: Reset sequences to correct values after inserting sample data.
--              Safe version: checks if sequence and table exist before resetting.
-- 
-- Author: Vitaliy Shvetsov
-- =====================================================================================================================

DO $$
BEGIN
    -- Reset each sequence only if both the sequence and table exist
    IF EXISTS (SELECT 1 FROM pg_class WHERE relkind = 'S' AND relname = 'language_id_seq') 
       AND EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'language') THEN
        PERFORM setval('language_id_seq', GREATEST((SELECT COALESCE(MAX(id), 0) FROM language), 1));
    END IF;

    IF EXISTS (SELECT 1 FROM pg_class WHERE relkind = 'S' AND relname = 'global_role_id_seq')
       AND EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'global_role') THEN
        PERFORM setval('global_role_id_seq', GREATEST((SELECT COALESCE(MAX(id), 0) FROM global_role), 1));
    END IF;

    IF EXISTS (SELECT 1 FROM pg_class WHERE relkind = 'S' AND relname = 'company_type_id_seq')
       AND EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'company_type') THEN
        PERFORM setval('company_type_id_seq', GREATEST((SELECT COALESCE(MAX(id), 0) FROM company_type), 1));
    END IF;

    IF EXISTS (SELECT 1 FROM pg_class WHERE relkind = 'S' AND relname = 'company_role_id_seq')
       AND EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'company_role') THEN
        PERFORM setval('company_role_id_seq', GREATEST((SELECT COALESCE(MAX(id), 0) FROM company_role), 1));
    END IF;

    IF EXISTS (SELECT 1 FROM pg_class WHERE relkind = 'S' AND relname = 'country_id_seq')
       AND EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'country') THEN
        PERFORM setval('country_id_seq', GREATEST((SELECT COALESCE(MAX(id), 0) FROM country), 1));
    END IF;

    IF EXISTS (SELECT 1 FROM pg_class WHERE relkind = 'S' AND relname = 'users_id_seq')
       AND EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'users') THEN
        PERFORM setval('users_id_seq', GREATEST((SELECT COALESCE(MAX(id), 0) FROM users), 1));
    END IF;

    IF EXISTS (SELECT 1 FROM pg_class WHERE relkind = 'S' AND relname = 'user_preferences_id_seq')
       AND EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'user_preferences') THEN
        PERFORM setval('user_preferences_id_seq', GREATEST((SELECT COALESCE(MAX(id), 0) FROM user_preferences), 1));
    END IF;

    IF EXISTS (SELECT 1 FROM pg_class WHERE relkind = 'S' AND relname = 'user_global_role_id_seq')
       AND EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'user_global_role') THEN
        PERFORM setval('user_global_role_id_seq', GREATEST((SELECT COALESCE(MAX(id), 0) FROM user_global_role), 1));
    END IF;

    IF EXISTS (SELECT 1 FROM pg_class WHERE relkind = 'S' AND relname = 'company_id_seq')
       AND EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'company') THEN
        PERFORM setval('company_id_seq', GREATEST((SELECT COALESCE(MAX(id), 0) FROM company), 1));
    END IF;

    IF EXISTS (SELECT 1 FROM pg_class WHERE relkind = 'S' AND relname = 'user_company_id_seq')
       AND EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'user_company') THEN
        PERFORM setval('user_company_id_seq', GREATEST((SELECT COALESCE(MAX(id), 0) FROM user_company), 1));
    END IF;

    IF EXISTS (SELECT 1 FROM pg_class WHERE relkind = 'S' AND relname = 'address_id_seq')
       AND EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'address') THEN
        PERFORM setval('address_id_seq', GREATEST((SELECT COALESCE(MAX(id), 0) FROM address), 1));
    END IF;

    IF EXISTS (SELECT 1 FROM pg_class WHERE relkind = 'S' AND relname = 'user_address_id_seq')
       AND EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'user_address') THEN
        PERFORM setval('user_address_id_seq', GREATEST((SELECT COALESCE(MAX(id), 0) FROM user_address), 1));
    END IF;

    IF EXISTS (SELECT 1 FROM pg_class WHERE relkind = 'S' AND relname = 'email_verification_id_seq')
       AND EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'email_verification') THEN
        PERFORM setval('email_verification_id_seq', GREATEST((SELECT COALESCE(MAX(id), 0) FROM email_verification), 1));
    END IF;

    IF EXISTS (SELECT 1 FROM pg_class WHERE relkind = 'S' AND relname = 'password_reset_token_id_seq')
       AND EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'password_reset_token') THEN
        PERFORM setval('password_reset_token_id_seq', GREATEST((SELECT COALESCE(MAX(id), 0) FROM password_reset_token), 1));
    END IF;

    IF EXISTS (SELECT 1 FROM pg_class WHERE relkind = 'S' AND relname = 'invitation_id_seq')
       AND EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'invitation') THEN
        PERFORM setval('invitation_id_seq', GREATEST((SELECT COALESCE(MAX(id), 0) FROM invitation), 1));
    END IF;
END $$;

-- =====================================================================================================================
-- END OF SEQUENCE RESET
-- =====================================================================================================================
