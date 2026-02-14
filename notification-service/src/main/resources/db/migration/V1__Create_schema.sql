-- =====================================================================================================================
-- NOTIFICATION SERVICE - DATABASE SCHEMA v1.0 (Flyway Migration)
-- =====================================================================================================================
-- Module: notification-service
-- Database: PostgreSQL
-- Version: 1.0
-- Description: Create notification database schema for push notification management
-- 
-- Author: Vitaliy Shvetsov
-- =====================================================================================================================

-- =====================================================================================================================
-- TABLE: notification_preferences
-- =====================================================================================================================
-- User notification preferences for different notification types
CREATE TABLE IF NOT EXISTS notification_preferences (
    id BIGSERIAL PRIMARY KEY,
    user_external_id VARCHAR(100) UNIQUE NOT NULL,
    
    -- Notification preferences with granular controls
    rental_updates_enabled BOOLEAN NOT NULL DEFAULT true,
    rental_start_enabled BOOLEAN NOT NULL DEFAULT true,
    rental_end_reminders_enabled BOOLEAN NOT NULL DEFAULT true,
    rental_completion_enabled BOOLEAN NOT NULL DEFAULT true,
    payment_updates_enabled BOOLEAN NOT NULL DEFAULT true,
    support_messages_enabled BOOLEAN NOT NULL DEFAULT true,
    marketing_enabled BOOLEAN NOT NULL DEFAULT false,
    
    -- Timestamps
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    
    -- Audit fields
    date_created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_date_modified TIMESTAMP,
    created_by VARCHAR(255),
    last_modified_by VARCHAR(255),
    is_deleted BOOLEAN NOT NULL DEFAULT false
);

-- =====================================================================================================================
-- TABLE: push_tokens
-- =====================================================================================================================
-- Expo push tokens for user devices with device tracking
CREATE TABLE IF NOT EXISTS push_tokens (
    id BIGSERIAL PRIMARY KEY,
    user_external_id VARCHAR(100) NOT NULL,
    expo_push_token VARCHAR(255) UNIQUE NOT NULL,
    
    -- Platform and device info
    platform VARCHAR(20),
    device_name VARCHAR(100),
    device_id VARCHAR(255),
    app_version VARCHAR(50),
    device_model VARCHAR(100),
    os_version VARCHAR(50),
    
    -- Status
    is_active BOOLEAN NOT NULL DEFAULT true,
    
    -- Timestamps
    created_at TIMESTAMP,
    last_used_at TIMESTAMP,
    
    -- Audit fields
    date_created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_date_modified TIMESTAMP,
    created_by VARCHAR(255),
    last_modified_by VARCHAR(255),
    is_deleted BOOLEAN NOT NULL DEFAULT false
);

-- =====================================================================================================================
-- TABLE: notification_logs
-- =====================================================================================================================
-- Log of notifications sent or attempted
-- Supports hybrid tenant isolation: user-scoped (personal) and company-scoped (B2B) notifications
CREATE TABLE IF NOT EXISTS notification_logs (
    id BIGSERIAL PRIMARY KEY,
    user_external_id VARCHAR(100) NOT NULL,
    
    -- Multi-tenant fields
    company_external_id VARCHAR(100),
    notification_category VARCHAR(20) DEFAULT 'USER', -- USER or COMPANY
    
    -- Notification content
    notification_type VARCHAR(50),
    title VARCHAR(255),
    body TEXT,
    data JSONB,
    
    -- Status tracking
    status VARCHAR(20) NOT NULL,
    expo_receipt_id VARCHAR(255),
    error_message TEXT,
    
    -- Read tracking
    is_read BOOLEAN NOT NULL DEFAULT false,
    read_at TIMESTAMP,
    
    -- Delivery status
    delivery_status VARCHAR(20),
    
    -- Timestamps
    created_at TIMESTAMP,
    
    -- Audit fields
    date_created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_date_modified TIMESTAMP,
    created_by VARCHAR(255),
    last_modified_by VARCHAR(255),
    is_deleted BOOLEAN NOT NULL DEFAULT false
);

-- =====================================================================================================================
-- END OF SCHEMA CREATION
-- =====================================================================================================================
