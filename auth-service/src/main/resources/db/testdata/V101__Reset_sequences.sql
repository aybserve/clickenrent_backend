-- =====================================================================================================================
-- AUTH SERVICE - SEQUENCE RESET (Flyway Testdata V101)
-- =====================================================================================================================
-- Module: auth-service
-- Database: PostgreSQL
-- Description: Reset sequences to correct values after inserting sample data.
-- 
-- Author: Vitaliy Shvetsov
-- =====================================================================================================================

SELECT setval('language_id_seq', (SELECT COALESCE(MAX(id), 1) FROM language));
SELECT setval('global_role_id_seq', (SELECT COALESCE(MAX(id), 1) FROM global_role));
SELECT setval('company_type_id_seq', (SELECT COALESCE(MAX(id), 1) FROM company_type));
SELECT setval('company_role_id_seq', (SELECT COALESCE(MAX(id), 1) FROM company_role));
SELECT setval('country_id_seq', (SELECT COALESCE(MAX(id), 1) FROM country));
SELECT setval('users_id_seq', (SELECT COALESCE(MAX(id), 1) FROM users));
SELECT setval('user_preferences_id_seq', (SELECT COALESCE(MAX(id), 1) FROM user_preferences));
SELECT setval('user_global_role_id_seq', (SELECT COALESCE(MAX(id), 1) FROM user_global_role));
SELECT setval('company_id_seq', (SELECT COALESCE(MAX(id), 1) FROM company));
SELECT setval('user_company_id_seq', (SELECT COALESCE(MAX(id), 1) FROM user_company));
SELECT setval('address_id_seq', (SELECT COALESCE(MAX(id), 1) FROM address));
SELECT setval('user_address_id_seq', (SELECT COALESCE(MAX(id), 1) FROM user_address));
SELECT setval('email_verification_id_seq', (SELECT COALESCE(MAX(id), 1) FROM email_verification));
SELECT setval('password_reset_token_id_seq', (SELECT COALESCE(MAX(id), 1) FROM password_reset_token));
SELECT setval('invitation_id_seq', (SELECT COALESCE(MAX(id), 1) FROM invitation));

-- =====================================================================================================================
-- END OF SEQUENCE RESET
-- =====================================================================================================================
