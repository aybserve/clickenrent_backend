-- =====================================================================================================================
-- COMPREHENSIVE SERVICE ACCOUNT DEBUG
-- =====================================================================================================================
-- Run on clickenrent_auth database
-- This will show you EXACTLY what's wrong with the service account

\echo '========================================='
\echo '1. Check if service_payment user exists'
\echo '========================================='
SELECT 
    id,
    external_id,
    user_name,
    email,
    is_active,
    is_email_verified,
    is_accepted_terms,
    is_accepted_privacy_policy,
    is_deleted,
    CASE 
        WHEN password IS NULL OR password = '' THEN 'NO PASSWORD!'
        WHEN password = '$2a$10$59i5SxRWkbcxt2rfTyjJ2.dZrlXFchPqyw1p56D/Ltp6jvYGVh2YK' THEN 'Correct password hash'
        ELSE 'Different password hash: ' || LEFT(password, 20) || '...'
    END as password_status
FROM users 
WHERE user_name = 'service_payment';

\echo ''
\echo '========================================='
\echo '2. Check SYSTEM role'
\echo '========================================='
SELECT id, name FROM global_role WHERE name = 'SYSTEM';

\echo ''
\echo '========================================='
\echo '3. Check user-role mapping'
\echo '========================================='
SELECT 
    ugr.id as mapping_id,
    ugr.user_id,
    u.user_name,
    ugr.global_role_id,
    gr.name as role_name,
    ugr.is_deleted as mapping_deleted
FROM user_global_role ugr
JOIN users u ON ugr.user_id = u.id
JOIN global_role gr ON ugr.global_role_id = gr.id
WHERE u.user_name = 'service_payment';

\echo ''
\echo '========================================='
\echo '4. Issues detected (if any)'
\echo '========================================='
SELECT 
    CASE 
        WHEN (SELECT COUNT(*) FROM users WHERE user_name = 'service_payment') = 0 
        THEN 'ERROR: User does not exist!'
        WHEN (SELECT is_active FROM users WHERE user_name = 'service_payment') = false 
        THEN 'ERROR: User is not active!'
        WHEN (SELECT is_deleted FROM users WHERE user_name = 'service_payment') = true 
        THEN 'ERROR: User is marked as deleted!'
        WHEN (SELECT password FROM users WHERE user_name = 'service_payment') IS NULL 
        THEN 'ERROR: User has no password!'
        WHEN (SELECT COUNT(*) FROM user_global_role ugr JOIN users u ON ugr.user_id = u.id WHERE u.user_name = 'service_payment') = 0 
        THEN 'ERROR: User is not linked to any role!'
        WHEN (SELECT COUNT(*) FROM user_global_role ugr JOIN users u ON ugr.user_id = u.id JOIN global_role gr ON ugr.global_role_id = gr.id WHERE u.user_name = 'service_payment' AND gr.name = 'SYSTEM') = 0 
        THEN 'ERROR: User is not linked to SYSTEM role!'
        ELSE 'All checks passed! User should be able to authenticate.'
    END as status;

\echo ''
\echo '========================================='
\echo '5. All users with SYSTEM role (for comparison)'
\echo '========================================='
SELECT 
    u.id,
    u.user_name,
    u.email,
    u.is_active,
    gr.name as role_name
FROM users u
JOIN user_global_role ugr ON u.id = ugr.user_id
JOIN global_role gr ON ugr.global_role_id = gr.id
WHERE gr.name = 'SYSTEM'
ORDER BY u.id;
