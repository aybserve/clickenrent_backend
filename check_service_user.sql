-- Check if service_payment user exists
SELECT 
    'User exists?' as check,
    COUNT(*) as count,
    CASE WHEN COUNT(*) > 0 THEN 'YES' ELSE 'NO - RUN INSERT BELOW' END as result
FROM users 
WHERE user_name = 'service_payment';

-- Check SYSTEM role
SELECT 
    'SYSTEM role exists?' as check,
    COUNT(*) as count,
    CASE WHEN COUNT(*) > 0 THEN 'YES' ELSE 'NO - RUN INSERT BELOW' END as result
FROM global_role 
WHERE name = 'SYSTEM';

-- If user doesn't exist, run this:
-- INSERT INTO users (id, external_id, user_name, email, password, first_name, last_name, phone, language_id, is_active, is_email_verified, is_accepted_terms, is_accepted_privacy_policy, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
-- (14, 'usr-ext-service-payment', 'service_payment', 'service.payment@clickenrent.internal', '$2a$10$59i5SxRWkbcxt2rfTyjJ2.dZrlXFchPqyw1p56D/Ltp6jvYGVh2YK', 'Payment', 'Service', '+00-00-00000000', 1, true, true, true, true, NOW(), NOW(), 'system', 'system', false);

-- Link to SYSTEM role (run after creating user):
-- INSERT INTO user_global_role (user_id, global_role_id, date_created, last_date_modified, created_by, last_modified_by, is_deleted) 
-- SELECT 14, 6, NOW(), NOW(), 'system', 'system', false
-- WHERE NOT EXISTS (SELECT 1 FROM user_global_role WHERE user_id = 14 AND global_role_id = 6);
