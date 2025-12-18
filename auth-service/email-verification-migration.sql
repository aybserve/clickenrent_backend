-- =====================================================================================================================
-- EMAIL VERIFICATION TABLE MIGRATION
-- =====================================================================================================================
-- Description: Creates email_verification table for storing email verification codes with attempt tracking
-- Author: Vitaliy Shvetsov
-- Date: 2024-12-16
-- =====================================================================================================================

-- Create email_verification table
CREATE TABLE IF NOT EXISTS email_verification (
    id                      BIGSERIAL PRIMARY KEY,
    user_id                 BIGINT NOT NULL,
    email                   VARCHAR(255) NOT NULL,
    code                    VARCHAR(6) NOT NULL,
    expires_at              TIMESTAMP NOT NULL,
    attempts                INTEGER NOT NULL DEFAULT 0,
    is_used                 BOOLEAN NOT NULL DEFAULT false,
    used_at                 TIMESTAMP,
    
    -- Audit fields
    date_created            TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_date_modified      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by              VARCHAR(255),
    last_modified_by        VARCHAR(255),
    is_deleted              BOOLEAN NOT NULL DEFAULT false,
    
    -- Foreign key constraint
    CONSTRAINT fk_email_verification_user FOREIGN KEY (user_id) 
        REFERENCES users(id) ON DELETE CASCADE
);

-- Create indexes for performance
CREATE INDEX IF NOT EXISTS idx_email_verification_user ON email_verification(user_id);
CREATE INDEX IF NOT EXISTS idx_email_verification_code ON email_verification(code);
CREATE INDEX IF NOT EXISTS idx_email_verification_email ON email_verification(email);

-- Add comments for documentation
COMMENT ON TABLE email_verification IS 'Stores email verification codes with expiration and attempt tracking';
COMMENT ON COLUMN email_verification.user_id IS 'Foreign key to users table';
COMMENT ON COLUMN email_verification.email IS 'Email address being verified (for reference)';
COMMENT ON COLUMN email_verification.code IS '6-digit numeric verification code';
COMMENT ON COLUMN email_verification.expires_at IS 'Expiration timestamp (15 minutes from creation)';
COMMENT ON COLUMN email_verification.attempts IS 'Number of failed verification attempts (max 3)';
COMMENT ON COLUMN email_verification.is_used IS 'Whether the code has been successfully used';
COMMENT ON COLUMN email_verification.used_at IS 'Timestamp when code was successfully used';
COMMENT ON COLUMN email_verification.is_deleted IS 'Soft delete flag for audit trail';

-- =====================================================================================================================
-- MIGRATION COMPLETE
-- =====================================================================================================================
-- The email_verification table has been created successfully.
-- 
-- Usage:
--   1. Run this migration: psql -U postgres -d clickenrent_auth -f email-verification-migration.sql
--   2. Verify table creation: \d email_verification
--   3. Check indexes: \di email_verification*
-- =====================================================================================================================

