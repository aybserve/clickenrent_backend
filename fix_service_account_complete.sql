-- =====================================================================================================================
-- COMPLETE FIX FOR SERVICE ACCOUNT
-- =====================================================================================================================
-- Run on clickenrent_auth database
-- This will ensure the service account is properly set up

-- 1. Delete existing service_payment user if it has issues (to start fresh)
DELETE FROM user_global_role WHERE user_id IN (SELECT id FROM users WHERE user_name = 'service_payment');
DELETE FROM users WHERE user_name = 'service_payment';

-- 2. Ensure SYSTEM role exists
INSERT INTO global_role (id, name) 
VALUES (6, 'SYSTEM') 
ON CONFLICT (id) DO UPDATE SET name = 'SYSTEM';

-- 3. Create service_payment user with ALL required fields
-- Password: 'password' (bcrypt hash: $2a$10$59i5SxRWkbcxt2rfTyjJ2.dZrlXFchPqyw1p56D/Ltp6jvYGVh2YK)
INSERT INTO users (
    external_id, 
    user_name, 
    email, 
    password, 
    first_name, 
    last_name, 
    phone, 
    language_id, 
    is_active, 
    is_email_verified, 
    is_accepted_terms, 
    is_accepted_privacy_policy, 
    date_created, 
    last_date_modified, 
    created_by, 
    last_modified_by, 
    is_deleted
) VALUES (
    'usr-ext-service-payment', 
    'service_payment', 
    'service.payment@clickenrent.internal', 
    '$2a$10$59i5SxRWkbcxt2rfTyjJ2.dZrlXFchPqyw1p56D/Ltp6jvYGVh2YK', 
    'Payment', 
    'Service', 
    '+00-00-00000000', 
    1, 
    true,  -- is_active MUST be true
    true,  -- is_email_verified MUST be true
    true,  -- is_accepted_terms MUST be true
    true,  -- is_accepted_privacy_policy MUST be true
    NOW(), 
    NOW(), 
    'system', 
    'system', 
    false  -- is_deleted MUST be false
);

-- 4. Link user to SYSTEM role
INSERT INTO user_global_role (user_id, global_role_id, date_created, last_date_modified, created_by, last_modified_by, is_deleted)
SELECT 
    u.id,
    gr.id,
    NOW(),
    NOW(),
    'system',
    'system',
    false
FROM users u
CROSS JOIN global_role gr
WHERE u.user_name = 'service_payment' 
  AND gr.name = 'SYSTEM'
  AND NOT EXISTS (
    SELECT 1 FROM user_global_role ugr 
    WHERE ugr.user_id = u.id AND ugr.global_role_id = gr.id
  );

-- 5. Verify everything is correct
SELECT 
    '✓ Service account created successfully!' as status,
    u.id as user_id,
    u.user_name,
    u.email,
    u.is_active,
    u.is_email_verified,
    gr.name as role_name
FROM users u
JOIN user_global_role ugr ON u.id = ugr.user_id
JOIN global_role gr ON ugr.global_role_id = gr.id
WHERE u.user_name = 'service_payment';

-- 6. Show credentials for testing
SELECT 
    'Use these credentials:' as info,
    'service_payment' as username,
    'password' as password,
    'service.payment@clickenrent.internal' as email;
