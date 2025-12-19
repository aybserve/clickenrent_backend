-- Migration: Convert UUID externalId columns to VARCHAR(100)
-- This aligns payment-service with other microservices (auth, rental, support)

-- Financial Transactions
ALTER TABLE financial_transactions 
    ALTER COLUMN external_id TYPE VARCHAR(100) USING external_id::TEXT;

-- User Payment Profiles
ALTER TABLE user_payment_profiles 
    ALTER COLUMN external_id TYPE VARCHAR(100) USING external_id::TEXT;

-- Rental Financial Transactions
ALTER TABLE rental_fin_transactions 
    ALTER COLUMN external_id TYPE VARCHAR(100) USING external_id::TEXT;

-- B2B Revenue Share Payouts
ALTER TABLE b2b_revenue_share_payouts 
    ALTER COLUMN external_id TYPE VARCHAR(100) USING external_id::TEXT;

-- B2B Revenue Share Payout Items
ALTER TABLE b2b_revenue_share_payout_items 
    ALTER COLUMN external_id TYPE VARCHAR(100) USING external_id::TEXT;

-- B2B Sale Financial Transactions
ALTER TABLE b2b_sale_fin_transactions 
    ALTER COLUMN external_id TYPE VARCHAR(100) USING external_id::TEXT;

-- B2B Subscription Financial Transactions
ALTER TABLE b2b_subscription_fin_transactions 
    ALTER COLUMN external_id TYPE VARCHAR(100) USING external_id::TEXT;

-- Payout Financial Transactions
ALTER TABLE payout_fin_transactions 
    ALTER COLUMN external_id TYPE VARCHAR(100) USING external_id::TEXT;

-- Payment Methods (lookup table)
ALTER TABLE payment_methods 
    ALTER COLUMN external_id TYPE VARCHAR(100) USING external_id::TEXT;

-- Payment Statuses (lookup table)
ALTER TABLE payment_statuses 
    ALTER COLUMN external_id TYPE VARCHAR(100) USING external_id::TEXT;

-- Service Providers (lookup table)
ALTER TABLE service_providers 
    ALTER COLUMN external_id TYPE VARCHAR(100) USING external_id::TEXT;

-- Currencies (lookup table)
ALTER TABLE currencies 
    ALTER COLUMN external_id TYPE VARCHAR(100) USING external_id::TEXT;

-- User Payment Methods
ALTER TABLE user_payment_methods 
    ALTER COLUMN external_id TYPE VARCHAR(100) USING external_id::TEXT;
