-- =====================================================================================================================
-- NOTIFICATION SERVICE - SAMPLE DATA (Flyway Testdata V100)
-- =====================================================================================================================
-- Module: notification-service
-- Database: PostgreSQL
-- Description: Insert default notification preferences, sample push tokens, and notification logs.
--              All inserts use ON CONFLICT to ensure idempotency.
--              Only loaded when 'staging' or 'dev' profile is active.
-- 
-- Author: Vitaliy Shvetsov
-- =====================================================================================================================

-- =====================================================================================================================
-- SECTION 1: DEFAULT NOTIFICATION PREFERENCES (with v2.0 granular controls)
-- =====================================================================================================================

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
-- SECTION 2: SAMPLE PUSH TOKENS (DEVELOPMENT/TESTING ONLY)
-- =====================================================================================================================

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
-- SECTION 3: SAMPLE NOTIFICATION LOGS (DEVELOPMENT/TESTING ONLY)
-- =====================================================================================================================

INSERT INTO notification_logs (
    id, user_external_id, notification_type, title, body, data,
    status, expo_receipt_id, error_message, is_read, read_at, delivery_status,
    created_at, date_created, last_date_modified, created_by, last_modified_by, is_deleted
) VALUES
-- Read notifications
(1, 'usr-ext-00007', 'BIKE_UNLOCKED', 'Bike Unlocked', 
 'Your bike has been unlocked. Have a great ride!',
 '{"bikeRentalId": "bike-rental-ext-00101", "bikeId": "550e8400-e29b-41d4-a716-446655440401"}'::jsonb,
 'sent', 'receipt-001', NULL, true, CURRENT_TIMESTAMP - INTERVAL '1 hour', 'delivered',
 CURRENT_TIMESTAMP - INTERVAL '2 hours', CURRENT_TIMESTAMP - INTERVAL '2 hours', CURRENT_TIMESTAMP, 'system', 'system', false),

(2, 'usr-ext-00007', 'RIDE_ENDED', 'Ride Completed', 
 'Your ride has ended. Total time: 45 minutes.',
 '{"bikeRentalId": "bike-rental-ext-00101", "duration": 45}'::jsonb,
 'sent', 'receipt-002', NULL, true, CURRENT_TIMESTAMP - INTERVAL '30 minutes', 'delivered',
 CURRENT_TIMESTAMP - INTERVAL '1 hour', CURRENT_TIMESTAMP - INTERVAL '1 hour', CURRENT_TIMESTAMP, 'system', 'system', false),

-- Unread notifications
(3, 'usr-ext-00008', 'RENTAL_ENDING_30MIN', 'Rental Ending Soon', 
 'Your rental ends in 30 minutes. Please return the bike to avoid extra charges.',
 '{"bikeRentalId": "bike-rental-ext-00102", "minutesRemaining": 30}'::jsonb,
 'sent', 'receipt-003', NULL, false, NULL, 'delivered',
 CURRENT_TIMESTAMP - INTERVAL '15 minutes', CURRENT_TIMESTAMP - INTERVAL '15 minutes', CURRENT_TIMESTAMP, 'system', 'system', false),

(4, 'usr-ext-00009', 'PAYMENT_SUCCESS', 'Payment Successful', 
 'Your payment of 25.00 EUR has been processed successfully.',
 '{"amount": 25.00, "currency": "EUR", "rentalId": "rental-ext-00103"}'::jsonb,
 'sent', 'receipt-004', NULL, false, NULL, 'delivered',
 CURRENT_TIMESTAMP - INTERVAL '5 minutes', CURRENT_TIMESTAMP - INTERVAL '5 minutes', CURRENT_TIMESTAMP, 'system', 'system', false),

(5, 'usr-ext-00010', 'SUPPORT_MESSAGE', 'New Support Message', 
 'You have received a response to your support ticket.',
 '{"ticketId": "ticket-001", "message": "We are looking into your issue"}'::jsonb,
 'sent', 'receipt-005', NULL, false, NULL, 'delivered',
 CURRENT_TIMESTAMP - INTERVAL '2 minutes', CURRENT_TIMESTAMP - INTERVAL '2 minutes', CURRENT_TIMESTAMP, 'system', 'system', false),

-- Failed notification (for testing error handling)
(6, 'usr-ext-00013', 'BIKE_UNLOCKED', 'Bike Unlocked', 
 'Your bike has been unlocked.',
 '{"bikeRentalId": "bike-rental-ext-00104"}'::jsonb,
 'failed', NULL, 'DeviceNotRegistered', false, NULL, 'failed',
 CURRENT_TIMESTAMP - INTERVAL '10 minutes', CURRENT_TIMESTAMP - INTERVAL '10 minutes', CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- =====================================================================================================================
-- END OF SAMPLE DATA
-- =====================================================================================================================
