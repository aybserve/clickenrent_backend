-- =====================================================================================================================
-- FIX SERVICE ACCOUNT FOR INTER-SERVICE AUTHENTICATION
-- =====================================================================================================================
-- Run this on the clickenrent_auth database to add the service account
-- Usage: psql -U postgres -d clickenrent_auth -f fix_service_account.sql

-- 1. Add SYSTEM role (if not exists)
INSERT INTO global_role (id, name) VALUES (6, 'SYSTEM') ON CONFLICT (id) DO NOTHING;

-- 2. Add service account user (if not exists)
-- Password is 'password' (bcrypt hashed: $2a$10$59i5SxRWkbcxt2rfTyjJ2.dZrlXFchPqyw1p56D/Ltp6jvYGVh2YK)
INSERT INTO users (id, external_id, user_name, email, password, first_name, last_name, phone, language_id, is_active, is_email_verified, is_accepted_terms, is_accepted_privacy_policy, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(14, 'usr-ext-service-payment', 'service_payment', 'service.payment@clickenrent.internal', '$2a$10$59i5SxRWkbcxt2rfTyjJ2.dZrlXFchPqyw1p56D/Ltp6jvYGVh2YK', 'Payment', 'Service', '+00-00-00000000', 1, true, true, true, true, NOW(), NOW(), 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- 3. Link service account to SYSTEM role (if not exists)
INSERT INTO user_global_role (id, user_id, global_role_id, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(14, 14, 6, NOW(), NOW(), 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- 4. Update sequences to ensure future inserts work correctly
SELECT setval('global_role_id_seq', (SELECT COALESCE(MAX(id), 1) FROM global_role));
SELECT setval('users_id_seq', (SELECT COALESCE(MAX(id), 1) FROM users));
SELECT setval('user_global_role_id_seq', (SELECT COALESCE(MAX(id), 1) FROM user_global_role));

-- 5. Verify the service account was created successfully
SELECT 
    u.id, 
    u.external_id, 
    u.user_name, 
    u.email, 
    u.is_active,
    gr.name as role_name
FROM users u
LEFT JOIN user_global_role ugr ON u.id = ugr.user_id
LEFT JOIN global_role gr ON ugr.global_role_id = gr.id
WHERE u.user_name = 'service_payment';
