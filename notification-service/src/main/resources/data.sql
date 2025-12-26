-- =====================================================================================================================
-- NOTIFICATION SERVICE - DATABASE INITIALIZATION v2.0
-- =====================================================================================================================
-- Module: notification-service
-- Database: PostgreSQL
-- Version: 2.0 (Mobile App Integration)
-- Description: Production database initialization for the push notification service.
--              Contains optional default notification preferences and reference data.
-- 
-- Usage:
--   This file is automatically executed by Spring Boot on application startup when:
--   - spring.jpa.hibernate.ddl-auto is set to 'create', 'create-drop', or 'update'
--   - spring.sql.init.mode is set to 'always'
--
-- Note: For production deployment, this file can remain empty as:
--       - Push tokens are registered by mobile apps at runtime
--       - Notification preferences are created on-demand with defaults
--       - Notification logs are created when notifications are sent
--
-- Author: Vitaliy Shvetsov
-- =====================================================================================================================

-- =====================================================================================================================
-- SECTION 1: DEFAULT NOTIFICATION PREFERENCES (with v2.0 granular controls)
-- =====================================================================================================================
-- Pre-create notification preferences for existing users from auth-service.
-- These preferences will be used when notifications are sent to these users.
-- Users can later update their preferences through the mobile app.
--
-- v2.0 Changes:
-- - Added rental_start_enabled: Controls bike unlock and ride start notifications
-- - Added rental_end_reminders_enabled: Controls rental ending soon reminders
-- - Added rental_completion_enabled: Controls bike lock and ride end notifications

INSERT INTO notification_preferences (
    id, user_external_id, 
    rental_updates_enabled, rental_start_enabled, rental_end_reminders_enabled, rental_completion_enabled,
    payment_updates_enabled, support_messages_enabled, marketing_enabled, 
    created_at, updated_at, date_created, last_date_modified, 
    created_by, last_modified_by, is_deleted
) VALUES
-- CUSTOMER Users (from auth-service) - All rental notifications enabled
(1, 'usr-ext-00007', true, true, true, true, true, true, false, 
 CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(2, 'usr-ext-00008', true, true, true, true, true, true, false, 
 CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(3, 'usr-ext-00009', true, true, true, true, true, true, false, 
 CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(4, 'usr-ext-00010', true, true, true, true, true, true, false, 
 CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(5, 'usr-ext-00011', true, true, true, true, true, true, false, 
 CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
-- User with marketing enabled
(6, 'usr-ext-00012', true, true, true, true, true, true, true, 
 CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
-- User with support messages disabled but rental reminders enabled
(7, 'usr-ext-00013', true, true, true, true, true, false, false, 
 CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
-- User with only rental start notifications (no reminders or completion)
(8, 'usr-ext-00014', true, true, false, false, true, true, false, 
 CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
-- User with only rental end reminders (no start or completion)
(9, 'usr-ext-00015', true, false, true, false, true, true, false, 
 CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (user_external_id) DO NOTHING;

-- =====================================================================================================================
-- SECTION 2: SAMPLE PUSH TOKENS (DEVELOPMENT/TESTING ONLY) - v2.0 with device tracking
-- =====================================================================================================================
-- IMPORTANT: This section is for DEVELOPMENT and TESTING purposes ONLY.
-- These are FAKE tokens for testing the database structure.
-- NEVER use real Expo Push Tokens in this file.
-- Real tokens should only be registered through the mobile app at runtime.
--
-- COMMENT OUT this entire section in production!
--
-- v2.0 Changes:
-- - Added device_id: Unique device identifier
-- - Added app_version: Mobile app version
-- - Added device_model: Device model name
-- - Added os_version: Operating system version

INSERT INTO push_tokens (
    id, user_external_id, expo_push_token, 
    platform, device_id, app_version, device_name, device_model, os_version,
    is_active, created_at, last_used_at, 
    date_created, last_date_modified, created_by, last_modified_by, is_deleted
) VALUES
-- iOS devices
(1, 'usr-ext-00007', 'ExponentPushToken[xxxxxxxxxxxxxx-TEST-001]', 
 'ios', '550e8400-e29b-41d4-a716-446655440001', '1.0.0', 'iPhone 14 Pro', 'iPhone 15,2', 'iOS 17.2',
 true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 
 CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(2, 'usr-ext-00008', 'ExponentPushToken[xxxxxxxxxxxxxx-TEST-002]', 
 'ios', '550e8400-e29b-41d4-a716-446655440002', '1.0.0', 'iPhone 13', 'iPhone 14,5', 'iOS 17.1',
 true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 
 CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),

-- Android devices
(3, 'usr-ext-00009', 'ExponentPushToken[xxxxxxxxxxxxxx-TEST-003]', 
 'android', '550e8400-e29b-41d4-a716-446655440003', '1.0.0', 'Samsung Galaxy S23', 'SM-S911B', 'Android 14',
 true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 
 CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(4, 'usr-ext-00010', 'ExponentPushToken[xxxxxxxxxxxxxx-TEST-004]', 
 'android', '550e8400-e29b-41d4-a716-446655440004', '1.0.0', 'Google Pixel 7', 'Pixel 7', 'Android 14',
 false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP - INTERVAL '30 days', 
 CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),

-- Multiple devices for same user (testing multi-device support)
(5, 'usr-ext-00011', 'ExponentPushToken[xxxxxxxxxxxxxx-TEST-005]', 
 'ios', '550e8400-e29b-41d4-a716-446655440005', '1.0.0', 'iPad Pro', 'iPad13,8', 'iOS 17.2',
 true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 
 CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(6, 'usr-ext-00011', 'ExponentPushToken[xxxxxxxxxxxxxx-TEST-006]', 
 'android', '550e8400-e29b-41d4-a716-446655440006', '1.0.0', 'Samsung Tablet', 'SM-X906B', 'Android 14',
 true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 
 CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),

-- Older app version (testing version tracking)
(7, 'usr-ext-00012', 'ExponentPushToken[xxxxxxxxxxxxxx-TEST-007]', 
 'ios', '550e8400-e29b-41d4-a716-446655440007', '0.9.5', 'iPhone 12', 'iPhone 13,2', 'iOS 16.5',
 true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 
 CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (expo_push_token) DO NOTHING;

-- =====================================================================================================================
-- SECTION 3: SAMPLE NOTIFICATION LOGS (DEVELOPMENT/TESTING ONLY) - v2.0 with read tracking
-- =====================================================================================================================
-- Sample notification logs to demonstrate the notification history feature.
-- COMMENT OUT this section in production!
--
-- v2.0 Changes:
-- - Added is_read: Whether notification has been read
-- - Added read_at: When notification was marked as read
-- - Added delivery_status: Delivery status from Expo

INSERT INTO notification_logs (
    id, user_external_id, notification_type, title, body, data,
    status, expo_receipt_id, error_message, is_read, read_at, delivery_status,
    created_at, date_created, last_date_modified, created_by, last_modified_by, is_deleted
) VALUES
-- Read notifications
(1, 'usr-ext-00007', 'BIKE_UNLOCKED', 'Bike Unlocked 🚴', 
 'Your bike has been unlocked. Have a great ride!',
 '{"bikeRentalId": "bike-rental-ext-00101", "bikeId": "550e8400-e29b-41d4-a716-446655440401"}'::jsonb,
 'sent', 'receipt-001', NULL, true, CURRENT_TIMESTAMP - INTERVAL '1 hour', 'delivered',
 CURRENT_TIMESTAMP - INTERVAL '2 hours', CURRENT_TIMESTAMP - INTERVAL '2 hours', CURRENT_TIMESTAMP, 'system', 'system', false),

(2, 'usr-ext-00007', 'RIDE_ENDED', 'Ride Completed 🎉', 
 'Your ride has ended. Total time: 45 minutes.',
 '{"bikeRentalId": "bike-rental-ext-00101", "duration": 45}'::jsonb,
 'sent', 'receipt-002', NULL, true, CURRENT_TIMESTAMP - INTERVAL '30 minutes', 'delivered',
 CURRENT_TIMESTAMP - INTERVAL '1 hour', CURRENT_TIMESTAMP - INTERVAL '1 hour', CURRENT_TIMESTAMP, 'system', 'system', false),

-- Unread notifications
(3, 'usr-ext-00008', 'RENTAL_ENDING_30MIN', 'Rental Ending Soon ⏰', 
 'Your rental ends in 30 minutes. Please return the bike to avoid extra charges.',
 '{"bikeRentalId": "bike-rental-ext-00102", "minutesRemaining": 30}'::jsonb,
 'sent', 'receipt-003', NULL, false, NULL, 'delivered',
 CURRENT_TIMESTAMP - INTERVAL '15 minutes', CURRENT_TIMESTAMP - INTERVAL '15 minutes', CURRENT_TIMESTAMP, 'system', 'system', false),

(4, 'usr-ext-00009', 'PAYMENT_SUCCESS', 'Payment Successful ✅', 
 'Your payment of €25.00 has been processed successfully.',
 '{"amount": 25.00, "currency": "EUR", "rentalId": "rental-ext-00103"}'::jsonb,
 'sent', 'receipt-004', NULL, false, NULL, 'delivered',
 CURRENT_TIMESTAMP - INTERVAL '5 minutes', CURRENT_TIMESTAMP - INTERVAL '5 minutes', CURRENT_TIMESTAMP, 'system', 'system', false),

(5, 'usr-ext-00010', 'SUPPORT_MESSAGE', 'New Support Message 💬', 
 'You have received a response to your support ticket.',
 '{"ticketId": "ticket-001", "message": "We are looking into your issue"}'::jsonb,
 'sent', 'receipt-005', NULL, false, NULL, 'delivered',
 CURRENT_TIMESTAMP - INTERVAL '2 minutes', CURRENT_TIMESTAMP - INTERVAL '2 minutes', CURRENT_TIMESTAMP, 'system', 'system', false),

-- Failed notification (for testing error handling)
(6, 'usr-ext-00013', 'BIKE_UNLOCKED', 'Bike Unlocked 🚴', 
 'Your bike has been unlocked.',
 '{"bikeRentalId": "bike-rental-ext-00104"}'::jsonb,
 'failed', NULL, 'DeviceNotRegistered', false, NULL, 'failed',
 CURRENT_TIMESTAMP - INTERVAL '10 minutes', CURRENT_TIMESTAMP - INTERVAL '10 minutes', CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- =====================================================================================================================
-- SECTION 4: NOTIFICATION TYPE REFERENCE (DOCUMENTATION)
-- =====================================================================================================================
-- This section documents the notification types used in the application.
-- These are not stored in the database but are used as constants in the code.
--
-- RENTAL START NOTIFICATIONS (controlled by rental_start_enabled):
--   - BIKE_UNLOCKED: Sent when a bike is unlocked
--   - RIDE_STARTED: Sent when a ride begins
--
-- RENTAL END REMINDER NOTIFICATIONS (controlled by rental_end_reminders_enabled):
--   - RENTAL_ENDING_SOON: Generic rental ending soon notification
--   - RENTAL_ENDING_30MIN: 30 minutes remaining reminder
--   - RENTAL_ENDING_10MIN: 10 minutes remaining reminder
--
-- RENTAL COMPLETION NOTIFICATIONS (controlled by rental_completion_enabled):
--   - BIKE_LOCKED: Sent when a bike is locked
--   - RIDE_ENDED: Sent when a ride is completed
--
-- PAYMENT UPDATES (controlled by payment_updates_enabled):
--   - PAYMENT_SUCCESS: Sent when payment is successful
--   - PAYMENT_FAILED: Sent when payment fails
--   - REFUND_PROCESSED: Sent when a refund is processed
--
-- SUPPORT MESSAGES (controlled by support_messages_enabled):
--   - SUPPORT_MESSAGE: Sent when there's a new support message
--   - TICKET_RESOLVED: Sent when a support ticket is resolved
--
-- MARKETING (controlled by marketing_enabled):
--   - MARKETING: General marketing notifications
--   - PROMOTION: Promotional offers
--
-- These types are mapped to user preferences in NotificationService.shouldSendNotification()

-- =====================================================================================================================
-- SECTION 5: SEQUENCE RESET
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
-- - Notification preferences: 9 users with various preference combinations
-- - Sample push tokens: 7 tokens with device tracking (DEVELOPMENT ONLY - comment out in production)
-- - Sample notification logs: 6 notifications with read tracking (DEVELOPMENT ONLY - comment out in production)
-- 
-- v2.0 Features Demonstrated:
-- - Granular rental preferences (start, end reminders, completion)
-- - Device tracking (device_id, app_version, device_model, os_version)
-- - Read tracking (is_read, read_at)
-- - Multi-device support (user usr-ext-00011 has 2 devices)
-- - Different app versions (testing version tracking)
-- - Delivery status tracking
-- 
-- Cross-Service References:
-- - User External IDs: Referenced from auth-service (usr-ext-XXXXX)
-- - This follows the microservices pattern of using external IDs for cross-service references
-- 
-- Production Deployment Notes:
-- 1. Keep Section 1 (Notification Preferences) for existing users
-- 2. COMMENT OUT Section 2 (Sample Push Tokens) - these are fake tokens for testing only
-- 3. COMMENT OUT Section 3 (Sample Notification Logs) - for testing only
-- 4. Real push tokens will be registered by mobile apps at runtime
-- 5. Real notification logs will be created when notifications are sent
-- 
-- Migration from v1.0:
-- - If upgrading from v1.0, run migration-v2.sql first to add new columns
-- - Then this data.sql will populate the new fields with default values
-- 
-- For more information, see README.md and IMPLEMENTATION_SUMMARY.md
-- =====================================================================================================================
