-- Notification Service Database Schema

-- Create database (run separately as superuser)
-- CREATE DATABASE notification_db;

-- Connect to notification_db before running the rest

-- Table: push_tokens
-- Stores Expo Push Tokens for user devices
CREATE TABLE push_tokens (
    id BIGSERIAL PRIMARY KEY,
    user_external_id VARCHAR(100) NOT NULL,
    expo_push_token VARCHAR(255) NOT NULL UNIQUE,
    device_type VARCHAR(20),
    device_name VARCHAR(100),
    is_active BOOLEAN DEFAULT true,
    is_deleted BOOLEAN DEFAULT false,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_used_at TIMESTAMP,
    date_created TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_date_modified TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    last_modified_by VARCHAR(255)
);

CREATE INDEX idx_push_tokens_user_external_id ON push_tokens(user_external_id);
CREATE INDEX idx_push_tokens_expo_token ON push_tokens(expo_push_token);
CREATE INDEX idx_push_tokens_active ON push_tokens(is_active) WHERE is_active = true;

COMMENT ON TABLE push_tokens IS 'Stores Expo Push Tokens for user devices';
COMMENT ON COLUMN push_tokens.user_external_id IS 'References User.externalId from auth-service';
COMMENT ON COLUMN push_tokens.expo_push_token IS 'Expo Push Token in format: ExponentPushToken[xxx]';
COMMENT ON COLUMN push_tokens.device_type IS 'Device platform: ios or android';

-- Table: notification_logs
-- Audit trail of all notifications sent
CREATE TABLE notification_logs (
    id BIGSERIAL PRIMARY KEY,
    user_external_id VARCHAR(100) NOT NULL,
    notification_type VARCHAR(50),
    title VARCHAR(255),
    body TEXT,
    data JSONB,
    status VARCHAR(20) NOT NULL,
    expo_receipt_id VARCHAR(255),
    error_message TEXT,
    is_deleted BOOLEAN DEFAULT false,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    date_created TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_date_modified TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    last_modified_by VARCHAR(255)
);

CREATE INDEX idx_notification_logs_user_external_id ON notification_logs(user_external_id);
CREATE INDEX idx_notification_logs_type ON notification_logs(notification_type);
CREATE INDEX idx_notification_logs_status ON notification_logs(status);
CREATE INDEX idx_notification_logs_created_at ON notification_logs(created_at DESC);

COMMENT ON TABLE notification_logs IS 'Audit trail of all notifications sent';
COMMENT ON COLUMN notification_logs.notification_type IS 'Type: BIKE_UNLOCKED, BIKE_LOCKED, RIDE_STARTED, RIDE_ENDED, etc.';
COMMENT ON COLUMN notification_logs.status IS 'Status: sent, failed, pending';
COMMENT ON COLUMN notification_logs.data IS 'Additional JSON payload sent with notification';

-- Table: notification_preferences
-- User preferences for notification types
CREATE TABLE notification_preferences (
    id BIGSERIAL PRIMARY KEY,
    user_external_id VARCHAR(100) NOT NULL UNIQUE,
    rental_updates_enabled BOOLEAN DEFAULT true,
    payment_updates_enabled BOOLEAN DEFAULT true,
    support_messages_enabled BOOLEAN DEFAULT true,
    marketing_enabled BOOLEAN DEFAULT false,
    is_deleted BOOLEAN DEFAULT false,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    date_created TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_date_modified TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    last_modified_by VARCHAR(255)
);

CREATE INDEX idx_notification_preferences_user_external_id ON notification_preferences(user_external_id);

COMMENT ON TABLE notification_preferences IS 'User preferences for notification types';
COMMENT ON COLUMN notification_preferences.user_external_id IS 'References User.externalId from auth-service (unique per user)';

-- Insert default preferences for existing users (optional)
-- This would be done via application logic when a user first registers a token

