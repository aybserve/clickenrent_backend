-- =====================================================================================================================
-- VERIFY SERVICE ACCOUNT EXISTS
-- =====================================================================================================================
-- Run on clickenrent_auth database to check if service account was created
-- Usage: psql -U postgres -d clickenrent_auth -f verify_service_account.sql

-- Check if SYSTEM role exists
SELECT 'SYSTEM role:' as check_type, id, name FROM global_role WHERE name = 'SYSTEM';

-- Check if service_payment user exists
SELECT 'service_payment user:' as check_type, id, external_id, user_name, email, is_active 
FROM users WHERE user_name = 'service_payment';

-- Check if user is linked to SYSTEM role
SELECT 
    'User-Role mapping:' as check_type,
    u.user_name, 
    gr.name as role_name,
    ugr.id as mapping_id
FROM user_global_role ugr
JOIN users u ON ugr.user_id = u.id
JOIN global_role gr ON ugr.global_role_id = gr.id
WHERE u.user_name = 'service_payment';

-- If all above return results, the service account is properly set up
-- If any return empty, run the fix_service_account.sql script
