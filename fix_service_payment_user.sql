-- =====================================================================================================================
-- FIX SERVICE_PAYMENT USER
-- =====================================================================================================================
-- This script ensures the service_payment user exists with the correct password
-- Password: Test123!
-- BCrypt hash: $2a$10$59i5SxRWkbcxt2rfTyjJ2.dZrlXFchPqyw1p56D/Ltp6jvYGVh2YK
--
-- Usage: Run this against the clickenrent_auth database
-- =====================================================================================================================

-- First, verify if the SYSTEM role exists
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM global_role WHERE id = 6) THEN
        INSERT INTO global_role (id, name) VALUES (6, 'SYSTEM');
        RAISE NOTICE 'Created SYSTEM role with id=6';
    ELSE
        RAISE NOTICE 'SYSTEM role already exists';
    END IF;
END $$;

-- Update or insert the service_payment user
INSERT INTO users (
    id, external_id, user_name, email, password, 
    first_name, last_name, phone, language_id, 
    is_active, is_email_verified, is_accepted_terms, is_accepted_privacy_policy,
    date_created, last_date_modified, created_by, last_modified_by, is_deleted
) VALUES (
    14, 
    'usr-ext-service-payment', 
    'service_payment', 
    'service.payment@clickenrent.internal', 
    '$2a$10$59i5SxRWkbcxt2rfTyjJ2.dZrlXFchPqyw1p56D/Ltp6jvYGVh2YK',  -- Password: Test123!
    'Payment', 
    'Service', 
    '+00-00-00000000', 
    1,
    true,  -- is_active
    true,  -- is_email_verified
    true,  -- is_accepted_terms
    true,  -- is_accepted_privacy_policy
    NOW(), 
    NOW(), 
    'system', 
    'system', 
    false  -- is_deleted
)
ON CONFLICT (id) DO UPDATE SET
    password = EXCLUDED.password,
    is_active = true,
    is_email_verified = true,
    is_deleted = false,
    last_date_modified = NOW(),
    last_modified_by = 'system';

-- Link service_payment user to SYSTEM role
INSERT INTO user_global_role (
    id, user_id, global_role_id, 
    date_created, last_date_modified, created_by, last_modified_by, is_deleted
) VALUES (
    14, 14, 6, 
    NOW(), NOW(), 'system', 'system', false
)
ON CONFLICT (id) DO UPDATE SET
    user_id = EXCLUDED.user_id,
    global_role_id = EXCLUDED.global_role_id,
    is_deleted = false,
    last_date_modified = NOW(),
    last_modified_by = 'system';

-- Verify the setup
SELECT 
    u.id,
    u.user_name,
    u.email,
    u.is_active,
    u.is_email_verified,
    u.is_deleted,
    gr.name as role_name
FROM users u
LEFT JOIN user_global_role ugr ON u.id = ugr.user_id AND ugr.is_deleted = false
LEFT JOIN global_role gr ON ugr.global_role_id = gr.id
WHERE u.user_name = 'service_payment';
