-- =====================================================================================================================
-- ANALYTICS SERVICE - DATABASE SCHEMA v1.0 (Flyway Migration)
-- =====================================================================================================================
-- Module: analytics-service
-- Database: PostgreSQL
-- Version: 1.0
-- Description: Create analytics database schema for aggregated metrics and reporting
-- 
-- Author: Vitaliy Shvetsov
-- =====================================================================================================================

-- =====================================================================================================================
-- TABLE: analytics_daily_summary
-- =====================================================================================================================
-- Daily aggregated analytics summary
-- Stores pre-computed metrics for fast query performance
CREATE TABLE IF NOT EXISTS analytics_daily_summary (
    id BIGSERIAL PRIMARY KEY,
    external_id VARCHAR(100) UNIQUE NOT NULL,
    company_external_id VARCHAR(100) NOT NULL,
    summary_date DATE NOT NULL,
    
    -- User Metrics
    new_customers INTEGER NOT NULL DEFAULT 0,
    active_customers INTEGER NOT NULL DEFAULT 0,
    total_customers INTEGER NOT NULL DEFAULT 0,
    
    -- Bike Rental Metrics
    total_bike_rentals INTEGER NOT NULL DEFAULT 0,
    completed_bike_rentals INTEGER NOT NULL DEFAULT 0,
    cancelled_bike_rentals INTEGER NOT NULL DEFAULT 0,
    total_bike_rental_duration_minutes BIGINT NOT NULL DEFAULT 0,
    average_bike_rental_duration_minutes DECIMAL(10, 2),
    
    -- Revenue Metrics (in cents to avoid floating point issues)
    total_revenue_cents BIGINT NOT NULL DEFAULT 0,
    total_refunds_cents BIGINT NOT NULL DEFAULT 0,
    average_bike_rental_revenue_cents DECIMAL(10, 2),
    
    -- Fleet Metrics
    total_bikes INTEGER NOT NULL DEFAULT 0,
    available_bikes INTEGER NOT NULL DEFAULT 0,
    in_use_bikes INTEGER NOT NULL DEFAULT 0,
    maintenance_bikes INTEGER NOT NULL DEFAULT 0,
    
    -- Location Metrics
    total_locations INTEGER NOT NULL DEFAULT 0,
    active_locations INTEGER NOT NULL DEFAULT 0,
    
    -- Support Metrics
    new_tickets INTEGER NOT NULL DEFAULT 0,
    resolved_tickets INTEGER NOT NULL DEFAULT 0,
    open_tickets INTEGER NOT NULL DEFAULT 0,
    
    -- Audit Fields
    date_created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_date_modified TIMESTAMP,
    created_by VARCHAR(255),
    last_modified_by VARCHAR(255),
    is_deleted BOOLEAN NOT NULL DEFAULT false,
    
    -- Constraints
    CONSTRAINT uk_analytics_daily_summary_company_date UNIQUE (company_external_id, summary_date)
);

-- =====================================================================================================================
-- TABLE: analytics_hourly_metrics
-- =====================================================================================================================
-- Hourly aggregated analytics metrics
-- Stores pre-computed hourly metrics for fast query performance
CREATE TABLE IF NOT EXISTS analytics_hourly_metrics (
    id BIGSERIAL PRIMARY KEY,
    external_id VARCHAR(100) UNIQUE NOT NULL,
    company_external_id VARCHAR(100) NOT NULL,
    metric_hour TIMESTAMP WITH TIME ZONE NOT NULL,
    
    -- Metrics
    bike_rentals_started INTEGER NOT NULL DEFAULT 0,
    bike_rentals_completed INTEGER NOT NULL DEFAULT 0,
    bike_rental_revenue_cents BIGINT NOT NULL DEFAULT 0,
    active_customers INTEGER NOT NULL DEFAULT 0,
    new_registrations INTEGER NOT NULL DEFAULT 0,
    
    -- Audit Fields
    date_created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_date_modified TIMESTAMP,
    created_by VARCHAR(255),
    last_modified_by VARCHAR(255),
    is_deleted BOOLEAN NOT NULL DEFAULT false,
    
    -- Constraints
    CONSTRAINT uk_analytics_hourly_metrics_company_hour UNIQUE (company_external_id, metric_hour)
);

-- =====================================================================================================================
-- TABLE: analytics_bike_metrics
-- =====================================================================================================================
-- Bike-based analytics metrics by date
-- Stores pre-computed bike metrics for fast query performance
CREATE TABLE IF NOT EXISTS analytics_bike_metrics (
    id BIGSERIAL PRIMARY KEY,
    external_id VARCHAR(100) UNIQUE NOT NULL,
    company_external_id VARCHAR(100) NOT NULL,
    metric_date DATE NOT NULL,
    bike_external_id VARCHAR(100) NOT NULL,
    bike_code VARCHAR(50),
    
    -- Metrics
    total_bike_rentals INTEGER NOT NULL DEFAULT 0,
    total_duration_minutes INTEGER NOT NULL DEFAULT 0,
    bike_rental_revenue_cents BIGINT NOT NULL DEFAULT 0,
    
    -- Status tracking
    available_hours DECIMAL(5, 2),
    in_use_hours DECIMAL(5, 2),
    maintenance_hours DECIMAL(5, 2),
    
    -- Audit Fields
    date_created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_date_modified TIMESTAMP,
    created_by VARCHAR(255),
    last_modified_by VARCHAR(255),
    is_deleted BOOLEAN NOT NULL DEFAULT false,
    
    -- Constraints
    CONSTRAINT uk_analytics_bike_metrics UNIQUE (company_external_id, metric_date, bike_external_id)
);

-- =====================================================================================================================
-- TABLE: analytics_location_metrics
-- =====================================================================================================================
-- Location-based analytics metrics by date
-- Stores pre-computed location metrics for fast query performance
CREATE TABLE IF NOT EXISTS analytics_location_metrics (
    id BIGSERIAL PRIMARY KEY,
    external_id VARCHAR(100) UNIQUE NOT NULL,
    company_external_id VARCHAR(100) NOT NULL,
    metric_date DATE NOT NULL,
    location_external_id VARCHAR(100) NOT NULL,
    
    -- Metrics
    total_pickups INTEGER NOT NULL DEFAULT 0,
    total_dropoffs INTEGER NOT NULL DEFAULT 0,
    unique_customers INTEGER NOT NULL DEFAULT 0,
    bike_rental_revenue_cents BIGINT NOT NULL DEFAULT 0,
    average_bikes_available DECIMAL(5, 2),
    
    -- Audit Fields
    date_created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_date_modified TIMESTAMP,
    created_by VARCHAR(255),
    last_modified_by VARCHAR(255),
    is_deleted BOOLEAN NOT NULL DEFAULT false,
    
    -- Constraints
    CONSTRAINT uk_analytics_location_metrics UNIQUE (company_external_id, metric_date, location_external_id)
);

-- =====================================================================================================================
-- TABLE: audit_logs
-- =====================================================================================================================
-- Security audit logs for compliance and incident response
CREATE TABLE IF NOT EXISTS audit_logs (
    id BIGSERIAL PRIMARY KEY,
    event_type VARCHAR(50) NOT NULL,
    user_external_id VARCHAR(100),
    company_external_ids TEXT,
    resource_type VARCHAR(100),
    resource_id VARCHAR(100),
    endpoint VARCHAR(500),
    http_method VARCHAR(10),
    client_ip VARCHAR(45),
    success BOOLEAN NOT NULL,
    error_message TEXT,
    metadata TEXT,
    timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- =====================================================================================================================
-- INDEXES
-- =====================================================================================================================

-- analytics_daily_summary indexes
CREATE INDEX IF NOT EXISTS idx_analytics_daily_summary_date ON analytics_daily_summary(summary_date DESC);
CREATE INDEX IF NOT EXISTS idx_analytics_daily_summary_company ON analytics_daily_summary(company_external_id);
CREATE INDEX IF NOT EXISTS idx_analytics_daily_summary_external_id ON analytics_daily_summary(external_id);

-- analytics_hourly_metrics indexes
CREATE INDEX IF NOT EXISTS idx_analytics_hourly_hour ON analytics_hourly_metrics(metric_hour DESC);
CREATE INDEX IF NOT EXISTS idx_analytics_hourly_company ON analytics_hourly_metrics(company_external_id);
CREATE INDEX IF NOT EXISTS idx_analytics_hourly_external_id ON analytics_hourly_metrics(external_id);

-- analytics_bike_metrics indexes
CREATE INDEX IF NOT EXISTS idx_analytics_bike_date ON analytics_bike_metrics(metric_date DESC);
CREATE INDEX IF NOT EXISTS idx_analytics_bike_external_id ON analytics_bike_metrics(bike_external_id);
CREATE INDEX IF NOT EXISTS idx_analytics_bike_company ON analytics_bike_metrics(company_external_id);
CREATE INDEX IF NOT EXISTS idx_analytics_bike_entity_external_id ON analytics_bike_metrics(external_id);
CREATE INDEX IF NOT EXISTS idx_analytics_bike_code ON analytics_bike_metrics(bike_code);

-- analytics_location_metrics indexes
CREATE INDEX IF NOT EXISTS idx_analytics_location_date ON analytics_location_metrics(metric_date DESC);
CREATE INDEX IF NOT EXISTS idx_analytics_location_external_id ON analytics_location_metrics(location_external_id);
CREATE INDEX IF NOT EXISTS idx_analytics_location_company ON analytics_location_metrics(company_external_id);
CREATE INDEX IF NOT EXISTS idx_analytics_location_entity_external_id ON analytics_location_metrics(external_id);

-- audit_logs indexes
CREATE INDEX IF NOT EXISTS idx_audit_logs_timestamp ON audit_logs(timestamp);
CREATE INDEX IF NOT EXISTS idx_audit_logs_user ON audit_logs(user_external_id);
CREATE INDEX IF NOT EXISTS idx_audit_logs_event_type ON audit_logs(event_type);
CREATE INDEX IF NOT EXISTS idx_audit_logs_success ON audit_logs(success);

-- =====================================================================================================================
-- END OF SCHEMA CREATION
-- =====================================================================================================================
