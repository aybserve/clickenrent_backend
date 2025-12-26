-- Notification Service Database Migration v2
-- Mobile App Integration Enhancements
-- Run this script on existing notification_db to add new features

-- ============================================
-- Push Tokens Enhancements
-- ============================================

-- Add device tracking fields
ALTER TABLE push_tokens ADD COLUMN IF NOT EXISTS device_id VARCHAR(255);
ALTER TABLE push_tokens ADD COLUMN IF NOT EXISTS app_version VARCHAR(50);
ALTER TABLE push_tokens ADD COLUMN IF NOT EXISTS device_model VARCHAR(100);
ALTER TABLE push_tokens ADD COLUMN IF NOT EXISTS os_version VARCHAR(50);

-- Add index for device_id lookups
CREATE INDEX IF NOT EXISTS idx_push_tokens_device_id ON push_tokens(device_id);

-- Add comments
COMMENT ON COLUMN push_tokens.device_id IS 'Unique device identifier (UUID) from mobile app';
COMMENT ON COLUMN push_tokens.app_version IS 'Mobile app version (e.g., 1.0.0)';
COMMENT ON COLUMN push_tokens.device_model IS 'Device model (e.g., iPhone 15 Pro, Pixel 8)';
COMMENT ON COLUMN push_tokens.os_version IS 'OS version (e.g., iOS 17.2, Android 14)';

-- ============================================
-- Notification Logs Read Tracking
-- ============================================

-- Add read tracking fields
ALTER TABLE notification_logs ADD COLUMN IF NOT EXISTS is_read BOOLEAN DEFAULT false;
ALTER TABLE notification_logs ADD COLUMN IF NOT EXISTS read_at TIMESTAMP;
ALTER TABLE notification_logs ADD COLUMN IF NOT EXISTS delivery_status VARCHAR(20);

-- Add indexes for read tracking queries
CREATE INDEX IF NOT EXISTS idx_notification_logs_is_read ON notification_logs(is_read);
CREATE INDEX IF NOT EXISTS idx_notification_logs_user_unread ON notification_logs(user_external_id, is_read) WHERE is_read = false;

-- Update existing records to mark as unread
UPDATE notification_logs SET is_read = false WHERE is_read IS NULL;

-- Add comments
COMMENT ON COLUMN notification_logs.is_read IS 'Whether user has read this notification';
COMMENT ON COLUMN notification_logs.read_at IS 'Timestamp when user marked notification as read';
COMMENT ON COLUMN notification_logs.delivery_status IS 'Delivery status: pending, sent, delivered, failed';

-- ============================================
-- Notification Preferences Granular Controls
-- ============================================

-- Add granular preference fields
ALTER TABLE notification_preferences ADD COLUMN IF NOT EXISTS rental_start_enabled BOOLEAN DEFAULT true;
ALTER TABLE notification_preferences ADD COLUMN IF NOT EXISTS rental_end_reminders_enabled BOOLEAN DEFAULT true;
ALTER TABLE notification_preferences ADD COLUMN IF NOT EXISTS rental_completion_enabled BOOLEAN DEFAULT true;

-- Update existing records with default values
UPDATE notification_preferences 
SET 
    rental_start_enabled = COALESCE(rental_start_enabled, rental_updates_enabled, true),
    rental_end_reminders_enabled = COALESCE(rental_end_reminders_enabled, rental_updates_enabled, true),
    rental_completion_enabled = COALESCE(rental_completion_enabled, rental_updates_enabled, true)
WHERE rental_start_enabled IS NULL 
   OR rental_end_reminders_enabled IS NULL 
   OR rental_completion_enabled IS NULL;

-- Add comments
COMMENT ON COLUMN notification_preferences.rental_start_enabled IS 'Enable notifications for rental start (bike unlock, ride start)';
COMMENT ON COLUMN notification_preferences.rental_end_reminders_enabled IS 'Enable reminders for rental ending soon';
COMMENT ON COLUMN notification_preferences.rental_completion_enabled IS 'Enable notifications for rental completion (bike lock, ride end)';

-- ============================================
-- Verification Queries
-- ============================================

-- Verify push_tokens columns
SELECT column_name, data_type, character_maximum_length 
FROM information_schema.columns 
WHERE table_name = 'push_tokens' 
  AND column_name IN ('device_id', 'app_version', 'device_model', 'os_version')
ORDER BY column_name;

-- Verify notification_logs columns
SELECT column_name, data_type, column_default
FROM information_schema.columns 
WHERE table_name = 'notification_logs' 
  AND column_name IN ('is_read', 'read_at', 'delivery_status')
ORDER BY column_name;

-- Verify notification_preferences columns
SELECT column_name, data_type, column_default
FROM information_schema.columns 
WHERE table_name = 'notification_preferences' 
  AND column_name IN ('rental_start_enabled', 'rental_end_reminders_enabled', 'rental_completion_enabled')
ORDER BY column_name;

-- Show index information
SELECT indexname, indexdef 
FROM pg_indexes 
WHERE tablename IN ('push_tokens', 'notification_logs', 'notification_preferences')
  AND indexname LIKE '%device_id%' OR indexname LIKE '%is_read%' OR indexname LIKE '%unread%'
ORDER BY tablename, indexname;

-- Migration complete
SELECT 'Migration v2 completed successfully!' AS status;

