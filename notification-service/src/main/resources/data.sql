-- =====================================================================================================================
-- NOTIFICATION SERVICE - DATABASE INITIALIZATION
-- =====================================================================================================================
-- Module: notification-service
-- Database: PostgreSQL
-- Description: Production database initialization for the push notification service.
--              Contains optional default notification preferences and reference data.
-- 
-- Usage:
--   This file is automatically executed by Spring Boot on application startup when:
--   - spring.jpa.hibernate.ddl-auto is set to 'create', 'create-drop', or 'update'
--   - spring.sql.init.mode is set to 'always' (default is 'embedded')
--
-- Note: For production deployment, this file can remain empty as:
--       - Push tokens are registered by mobile apps at runtime
--       - Notification preferences are created on-demand with defaults
--       - Notification logs are created when notifications are sent
--
-- Author: Vitaliy Shvetsov
-- =====================================================================================================================

-- =====================================================================================================================
-- SECTION 1: DEFAULT NOTIFICATION PREFERENCES
-- =====================================================================================================================
-- Pre-create notification preferences for existing users from auth-service.
-- These preferences will be used when notifications are sent to these users.
-- Users can later update their preferences through the mobile app.

INSERT INTO notification_preferences (id, user_external_id, rental_updates_enabled, payment_updates_enabled, 
                                      support_messages_enabled, marketing_enabled, 
                                      created_at, updated_at, date_created, last_date_modified, 
                                      created_by, last_modified_by, is_deleted) VALUES
-- CUSTOMER Users (from auth-service)
(1, 'usr-ext-00007', true, true, true, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 
 CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(2, 'usr-ext-00008', true, true, true, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 
 CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(3, 'usr-ext-00009', true, true, true, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 
 CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(4, 'usr-ext-00010', true, true, true, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 
 CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(5, 'usr-ext-00011', true, true, true, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 
 CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(6, 'usr-ext-00012', true, true, true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 
 CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(7, 'usr-ext-00013', true, true, false, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 
 CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (user_external_id) DO NOTHING;

-- =====================================================================================================================
-- SECTION 2: SAMPLE PUSH TOKENS (DEVELOPMENT/TESTING ONLY)
-- =====================================================================================================================
-- IMPORTANT: This section is for DEVELOPMENT and TESTING purposes ONLY.
-- These are FAKE tokens for testing the database structure.
-- NEVER use real Expo Push Tokens in this file.
-- Real tokens should only be registered through the mobile app at runtime.
--
-- COMMENT OUT this entire section in production!

INSERT INTO push_tokens (id, user_external_id, expo_push_token, device_type, device_name, 
                         is_active, created_at, last_used_at, 
                         date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
-- Sample tokens for testing (FAKE tokens - will not work with real Expo API)
(1, 'usr-ext-00007', 'ExponentPushToken[xxxxxxxxxxxxxx-TEST-001]', 'ios', 'iPhone 14 Pro', 
 true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 
 CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(2, 'usr-ext-00008', 'ExponentPushToken[xxxxxxxxxxxxxx-TEST-002]', 'android', 'Samsung Galaxy S23', 
 true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 
 CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(3, 'usr-ext-00009', 'ExponentPushToken[xxxxxxxxxxxxxx-TEST-003]', 'ios', 'iPhone 13', 
 true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 
 CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(4, 'usr-ext-00010', 'ExponentPushToken[xxxxxxxxxxxxxx-TEST-004]', 'android', 'Google Pixel 7', 
 false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP - INTERVAL '30 days', 
 CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (expo_push_token) DO NOTHING;

-- =====================================================================================================================
-- SECTION 3: NOTIFICATION TYPE REFERENCE (DOCUMENTATION)
-- =====================================================================================================================
-- This section documents the notification types used in the application.
-- These are not stored in the database but are used as constants in the code.
--
-- RENTAL UPDATES:
--   - BIKE_UNLOCKED: Sent when a bike is unlocked
--   - BIKE_LOCKED: Sent when a bike is locked
--   - RIDE_STARTED: Sent when a ride begins
--   - RIDE_ENDED: Sent when a ride is completed
--
-- PAYMENT UPDATES:
--   - PAYMENT_SUCCESS: Sent when payment is successful
--   - PAYMENT_FAILED: Sent when payment fails
--   - REFUND_PROCESSED: Sent when a refund is processed
--
-- SUPPORT MESSAGES:
--   - SUPPORT_MESSAGE: Sent when there's a new support message
--   - TICKET_RESOLVED: Sent when a support ticket is resolved
--
-- MARKETING:
--   - MARKETING: General marketing notifications
--   - PROMOTION: Promotional offers
--
-- These types are mapped to user preferences in NotificationService.shouldSendNotification()

-- =====================================================================================================================
-- SECTION 4: SEQUENCE RESET
-- =====================================================================================================================
-- Reset sequences to the correct values after inserting data.
-- This ensures that new records will have IDs starting after the existing data IDs.

SELECT setval('push_tokens_id_seq', (SELECT COALESCE(MAX(id), 1) FROM push_tokens));
SELECT setval('notification_logs_id_seq', (SELECT COALESCE(MAX(id), 1) FROM notification_logs));
SELECT setval('notification_preferences_id_seq', (SELECT COALESCE(MAX(id), 1) FROM notification_preferences));

-- =====================================================================================================================
-- END OF INITIALIZATION
-- =====================================================================================================================
-- Database initialization completed successfully!
-- 
-- Summary:
-- - Notification preferences: 7 users (from auth-service)
-- - Sample push tokens: 4 tokens (DEVELOPMENT ONLY - comment out in production)
-- - Notification logs: Empty (will be populated at runtime)
-- 
-- Cross-Service References:
-- - User External IDs: Referenced from auth-service (usr-ext-XXXXX)
-- - This follows the microservices pattern of using external IDs for cross-service references
-- 
-- Production Deployment Notes:
-- - Keep Section 1 (Notification Preferences) for existing users
-- - COMMENT OUT Section 2 (Sample Push Tokens) - these are fake tokens for testing only
-- - Real push tokens will be registered by mobile apps at runtime
-- 
-- For more information, see README.md
-- =====================================================================================================================
