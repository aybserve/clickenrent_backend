-- Migration V3: Migrate to externalId-only cross-microservice references
-- This migration removes internal ID columns and adds missing externalId columns
-- It also adds audit fields (is_deleted) and renames existing audit columns

-- ============================================================================
-- STEP 1: Add new externalId columns where missing
-- ============================================================================

-- Add b2b_sale_external_id to b2b_sale_fin_transactions
ALTER TABLE b2b_sale_fin_transactions 
ADD COLUMN IF NOT EXISTS b2b_sale_external_id VARCHAR(100);

-- Add b2b_subscription_external_id to b2b_subscription_fin_transactions
ALTER TABLE b2b_subscription_fin_transactions 
ADD COLUMN IF NOT EXISTS b2b_subscription_external_id VARCHAR(100);

-- ============================================================================
-- STEP 2: Add is_deleted column to all tables for soft delete
-- ============================================================================

ALTER TABLE rental_fin_transactions 
ADD COLUMN IF NOT EXISTS is_deleted BOOLEAN NOT NULL DEFAULT FALSE;

ALTER TABLE b2b_sale_fin_transactions 
ADD COLUMN IF NOT EXISTS is_deleted BOOLEAN NOT NULL DEFAULT FALSE;

ALTER TABLE b2b_subscription_fin_transactions 
ADD COLUMN IF NOT EXISTS is_deleted BOOLEAN NOT NULL DEFAULT FALSE;

ALTER TABLE b2b_revenue_share_payout_items 
ADD COLUMN IF NOT EXISTS is_deleted BOOLEAN NOT NULL DEFAULT FALSE;

ALTER TABLE financial_transactions 
ADD COLUMN IF NOT EXISTS is_deleted BOOLEAN NOT NULL DEFAULT FALSE;

ALTER TABLE b2b_revenue_share_payouts 
ADD COLUMN IF NOT EXISTS is_deleted BOOLEAN NOT NULL DEFAULT FALSE;

ALTER TABLE user_payment_profiles 
ADD COLUMN IF NOT EXISTS is_deleted BOOLEAN NOT NULL DEFAULT FALSE;

-- Lookup tables
ALTER TABLE payment_statuses 
ADD COLUMN IF NOT EXISTS is_deleted BOOLEAN NOT NULL DEFAULT FALSE;

ALTER TABLE payment_methods 
ADD COLUMN IF NOT EXISTS is_deleted BOOLEAN NOT NULL DEFAULT FALSE;

ALTER TABLE currencies 
ADD COLUMN IF NOT EXISTS is_deleted BOOLEAN NOT NULL DEFAULT FALSE;

ALTER TABLE service_providers 
ADD COLUMN IF NOT EXISTS is_deleted BOOLEAN NOT NULL DEFAULT FALSE;

-- ============================================================================
-- STEP 3: Rename audit columns to match BaseAuditEntity naming convention
-- ============================================================================

-- rental_fin_transactions
ALTER TABLE rental_fin_transactions 
RENAME COLUMN created_at TO date_created;

ALTER TABLE rental_fin_transactions 
RENAME COLUMN updated_at TO last_date_modified;

-- b2b_sale_fin_transactions
ALTER TABLE b2b_sale_fin_transactions 
RENAME COLUMN created_at TO date_created;

ALTER TABLE b2b_sale_fin_transactions 
RENAME COLUMN updated_at TO last_date_modified;

-- b2b_subscription_fin_transactions
ALTER TABLE b2b_subscription_fin_transactions 
RENAME COLUMN created_at TO date_created;

ALTER TABLE b2b_subscription_fin_transactions 
RENAME COLUMN updated_at TO last_date_modified;

-- b2b_revenue_share_payout_items
ALTER TABLE b2b_revenue_share_payout_items 
RENAME COLUMN created_at TO date_created;

ALTER TABLE b2b_revenue_share_payout_items 
RENAME COLUMN updated_at TO last_date_modified;

-- financial_transactions
ALTER TABLE financial_transactions 
RENAME COLUMN created_at TO date_created;

ALTER TABLE financial_transactions 
RENAME COLUMN updated_at TO last_date_modified;

-- b2b_revenue_share_payouts
ALTER TABLE b2b_revenue_share_payouts 
RENAME COLUMN created_at TO date_created;

ALTER TABLE b2b_revenue_share_payouts 
RENAME COLUMN updated_at TO last_date_modified;

-- user_payment_profiles
ALTER TABLE user_payment_profiles 
RENAME COLUMN created_at TO date_created;

ALTER TABLE user_payment_profiles 
RENAME COLUMN updated_at TO last_date_modified;

-- payment_statuses
ALTER TABLE payment_statuses 
RENAME COLUMN created_at TO date_created;

ALTER TABLE payment_statuses 
RENAME COLUMN updated_at TO last_date_modified;

-- payment_methods
ALTER TABLE payment_methods 
RENAME COLUMN created_at TO date_created;

ALTER TABLE payment_methods 
RENAME COLUMN updated_at TO last_date_modified;

-- currencies
ALTER TABLE currencies 
RENAME COLUMN created_at TO date_created;

ALTER TABLE currencies 
RENAME COLUMN updated_at TO last_date_modified;

-- Add symbol column to currencies if it doesn't exist
ALTER TABLE currencies 
ADD COLUMN IF NOT EXISTS symbol VARCHAR(10);

-- service_providers
ALTER TABLE service_providers 
RENAME COLUMN created_at TO date_created;

ALTER TABLE service_providers 
RENAME COLUMN updated_at TO last_date_modified;

-- ============================================================================
-- STEP 4: Add indexes for new externalId columns
-- ============================================================================

CREATE INDEX IF NOT EXISTS idx_rental_fin_trans_rental_ext_id 
ON rental_fin_transactions(rental_external_id);

CREATE INDEX IF NOT EXISTS idx_rental_fin_trans_bike_rental_ext_id 
ON rental_fin_transactions(bike_rental_external_id);

CREATE INDEX IF NOT EXISTS idx_b2b_sale_fin_trans_b2b_sale_ext_id 
ON b2b_sale_fin_transactions(b2b_sale_external_id);

CREATE INDEX IF NOT EXISTS idx_b2b_sub_fin_trans_b2b_sub_ext_id 
ON b2b_subscription_fin_transactions(b2b_subscription_external_id);

CREATE INDEX IF NOT EXISTS idx_b2b_payout_item_bike_rental_ext_id 
ON b2b_revenue_share_payout_items(bike_rental_external_id);

CREATE INDEX IF NOT EXISTS idx_financial_trans_payer_ext_id 
ON financial_transactions(payer_external_id);

CREATE INDEX IF NOT EXISTS idx_financial_trans_recipient_ext_id 
ON financial_transactions(recipient_external_id);

CREATE INDEX IF NOT EXISTS idx_b2b_payout_company_ext_id 
ON b2b_revenue_share_payouts(company_external_id);

CREATE INDEX IF NOT EXISTS idx_user_payment_profile_user_ext_id 
ON user_payment_profiles(user_external_id);

-- ============================================================================
-- STEP 5: Drop old internal ID columns (after data migration)
-- WARNING: Only run this after ensuring all externalId fields are populated!
-- ============================================================================

-- Uncomment these lines after data migration is complete:

-- ALTER TABLE rental_fin_transactions DROP COLUMN IF EXISTS rental_id;
-- ALTER TABLE rental_fin_transactions DROP COLUMN IF EXISTS bike_rental_id;

-- ALTER TABLE b2b_sale_fin_transactions DROP COLUMN IF EXISTS b2b_sale_id;

-- ALTER TABLE b2b_subscription_fin_transactions DROP COLUMN IF EXISTS b2b_subscription_id;

-- ALTER TABLE b2b_revenue_share_payout_items DROP COLUMN IF EXISTS bike_rental_id;

-- ALTER TABLE financial_transactions DROP COLUMN IF EXISTS payer_id;
-- ALTER TABLE financial_transactions DROP COLUMN IF EXISTS recipient_id;

-- ALTER TABLE b2b_revenue_share_payouts DROP COLUMN IF EXISTS company_id;

-- ALTER TABLE user_payment_profiles DROP COLUMN IF EXISTS user_id;

-- ============================================================================
-- NOTES:
-- ============================================================================
-- 1. This migration adds new externalId columns and soft delete support
-- 2. It renames audit columns to match BaseAuditEntity naming convention
-- 3. Internal ID columns are commented out for safety - uncomment after data migration
-- 4. Ensure all applications are updated to use externalIds before dropping internal IDs
-- 5. Run a data migration script to populate externalId fields from internal IDs if needed
-- ============================================================================

