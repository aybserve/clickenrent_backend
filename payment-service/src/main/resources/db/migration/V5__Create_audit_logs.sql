-- =====================================================================================================================
-- PAYMENT SERVICE - AUDIT LOGS TABLE v1.0 (Flyway Migration)
-- =====================================================================================================================
-- Module: payment-service
-- Database: PostgreSQL
-- Version: 1.0
-- Description: Create audit logs table for security tracking
-- 
-- Author: Vitaliy Shvetsov
-- =====================================================================================================================

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
-- INDEXES: audit_logs
-- =====================================================================================================================
CREATE INDEX IF NOT EXISTS idx_audit_logs_timestamp ON audit_logs(timestamp);
CREATE INDEX IF NOT EXISTS idx_audit_logs_user ON audit_logs(user_external_id);
CREATE INDEX IF NOT EXISTS idx_audit_logs_event_type ON audit_logs(event_type);
CREATE INDEX IF NOT EXISTS idx_audit_logs_success ON audit_logs(success);

-- =====================================================================================================================
-- END OF AUDIT LOGS CREATION
-- =====================================================================================================================
