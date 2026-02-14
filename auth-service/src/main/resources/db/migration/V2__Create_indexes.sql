-- =====================================================================================================================
-- AUTH SERVICE - INDEXES (Flyway Migration V2)
-- =====================================================================================================================
-- Module: auth-service
-- Database: PostgreSQL
-- Description: Create performance indexes for all auth-service tables.
--              Uses IF NOT EXISTS for idempotency.
-- 
-- Author: Vitaliy Shvetsov
-- =====================================================================================================================

-- Users table indexes
CREATE INDEX IF NOT EXISTS idx_user_external_id ON users(external_id);
CREATE INDEX IF NOT EXISTS idx_user_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_user_username ON users(user_name);
CREATE INDEX IF NOT EXISTS idx_user_provider ON users(provider_id, provider_user_id);

-- User preferences table indexes
CREATE INDEX IF NOT EXISTS idx_user_preferences_user_id ON user_preferences(user_id);

-- Partial unique index for user_preferences: ensures one active preference per user
-- Allows multiple soft-deleted records for the same user (for audit trail)
-- Using DO block to handle the case where the index already exists
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_indexes WHERE indexname = 'user_preferences_user_id_active_unique'
    ) THEN
        CREATE UNIQUE INDEX user_preferences_user_id_active_unique 
        ON user_preferences(user_id) 
        WHERE is_deleted = false;
    END IF;
END $$;

-- Company table indexes
CREATE INDEX IF NOT EXISTS idx_company_external_id ON company(external_id);

-- Language table indexes
CREATE INDEX IF NOT EXISTS idx_language_external_id ON language(external_id);

-- Global role table indexes
CREATE INDEX IF NOT EXISTS idx_global_role_external_id ON global_role(external_id);

-- Company type table indexes
CREATE INDEX IF NOT EXISTS idx_company_type_external_id ON company_type(external_id);

-- Company role table indexes
CREATE INDEX IF NOT EXISTS idx_company_role_external_id ON company_role(external_id);

-- Country table indexes
CREATE INDEX IF NOT EXISTS idx_country_external_id ON country(external_id);
CREATE INDEX IF NOT EXISTS idx_country_name ON country(name);

-- Address table indexes
CREATE INDEX IF NOT EXISTS idx_address_external_id ON address(external_id);
CREATE INDEX IF NOT EXISTS idx_address_country ON address(country_id);

-- User global role table indexes
CREATE INDEX IF NOT EXISTS idx_user_global_role_external_id ON user_global_role(external_id);

-- User company table indexes
CREATE INDEX IF NOT EXISTS idx_user_company_external_id ON user_company(external_id);

-- User address table indexes
CREATE INDEX IF NOT EXISTS idx_user_address_external_id ON user_address(external_id);

-- Email verification table indexes
CREATE INDEX IF NOT EXISTS idx_email_verification_user ON email_verification(user_id);
CREATE INDEX IF NOT EXISTS idx_email_verification_code ON email_verification(code);
CREATE INDEX IF NOT EXISTS idx_email_verification_email ON email_verification(email);

-- Password reset token table indexes
CREATE INDEX IF NOT EXISTS idx_password_reset_user ON password_reset_token(user_id);
CREATE INDEX IF NOT EXISTS idx_password_reset_email ON password_reset_token(email);
CREATE INDEX IF NOT EXISTS idx_password_reset_token ON password_reset_token(token);
CREATE INDEX IF NOT EXISTS idx_password_reset_ip ON password_reset_token(ip_address);
CREATE INDEX IF NOT EXISTS idx_password_reset_used_ip ON password_reset_token(used_ip_address);

-- Invitation table indexes
CREATE INDEX IF NOT EXISTS idx_invitation_token ON invitation(token);
CREATE INDEX IF NOT EXISTS idx_invitation_email ON invitation(email);
CREATE INDEX IF NOT EXISTS idx_invitation_status ON invitation(status);

-- RLS performance indexes
CREATE INDEX IF NOT EXISTS idx_user_company_company_external_id ON user_company(company_id);
CREATE INDEX IF NOT EXISTS idx_company_external_id_rls ON company(external_id);

-- =====================================================================================================================
-- END OF INDEX CREATION
-- =====================================================================================================================
