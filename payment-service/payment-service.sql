-- =====================================================================================================================
-- PAYMENT SERVICE - DATABASE SCHEMA
-- =====================================================================================================================
-- Module: payment-service
-- Database: PostgreSQL
-- Description: Complete database schema for the payment and financial transaction service.
--              Handles payments, Stripe integration, B2B revenue sharing, and financial transactions.
-- 
-- Usage:
--   1. Create database: CREATE DATABASE clickenrent_payment;
--   2. Import schema: psql -U postgres -d clickenrent_payment -f payment-service.sql
--
-- Author: Vitaliy Shvetsov
-- =====================================================================================================================

-- Drop existing tables if they exist (in correct order to handle foreign key dependencies)
DROP TABLE IF EXISTS payout_fin_transactions CASCADE;
DROP TABLE IF EXISTS b2b_revenue_share_payout_items CASCADE;
DROP TABLE IF EXISTS b2b_revenue_share_payouts CASCADE;
DROP TABLE IF EXISTS b2b_subscription_fin_transactions CASCADE;
DROP TABLE IF EXISTS b2b_sale_fin_transactions CASCADE;
DROP TABLE IF EXISTS rental_fin_transactions CASCADE;
DROP TABLE IF EXISTS user_payment_methods CASCADE;
DROP TABLE IF EXISTS financial_transactions CASCADE;
DROP TABLE IF EXISTS user_payment_profiles CASCADE;
DROP TABLE IF EXISTS service_providers CASCADE;
DROP TABLE IF EXISTS payment_statuses CASCADE;
DROP TABLE IF EXISTS payment_methods CASCADE;
DROP TABLE IF EXISTS currencies CASCADE;

-- =====================================================================================================================
-- SECTION 1: LOOKUP/REFERENCE TABLES
-- =====================================================================================================================
-- These tables contain relatively static reference data that other tables will reference.

-- ---------------------------------------------------------------------------------------------------------------------
-- Table: currencies
-- Description: Currency types supported by the system (USD, EUR, GBP, JPY, etc.)
-- ---------------------------------------------------------------------------------------------------------------------
CREATE TABLE currencies (
    id                  BIGSERIAL PRIMARY KEY,
    external_id         UUID NOT NULL UNIQUE,
    code                VARCHAR(3) NOT NULL UNIQUE,
    name                VARCHAR(255) NOT NULL,
    
    -- Audit fields
    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by          VARCHAR(255),
    last_modified_by    VARCHAR(255),
    
    CONSTRAINT chk_currency_code_not_empty CHECK (code <> ''),
    CONSTRAINT chk_currency_name_not_empty CHECK (name <> '')
);

-- ---------------------------------------------------------------------------------------------------------------------
-- Table: payment_methods
-- Description: Payment method types (CREDIT_CARD, DEBIT_CARD, BANK_TRANSFER, PAYPAL, APPLE_PAY)
-- ---------------------------------------------------------------------------------------------------------------------
CREATE TABLE payment_methods (
    id                  BIGSERIAL PRIMARY KEY,
    external_id         UUID NOT NULL UNIQUE,
    code                VARCHAR(50) NOT NULL UNIQUE,
    name                VARCHAR(255) NOT NULL,
    is_active           BOOLEAN NOT NULL DEFAULT true,
    
    -- Audit fields
    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by          VARCHAR(255),
    last_modified_by    VARCHAR(255),
    
    CONSTRAINT chk_payment_method_code_not_empty CHECK (code <> ''),
    CONSTRAINT chk_payment_method_name_not_empty CHECK (name <> '')
);

-- ---------------------------------------------------------------------------------------------------------------------
-- Table: payment_statuses
-- Description: Payment status types (PENDING, SUCCEEDED, FAILED, CANCELED, REFUNDED, PARTIALLY_REFUNDED)
-- ---------------------------------------------------------------------------------------------------------------------
CREATE TABLE payment_statuses (
    id                  BIGSERIAL PRIMARY KEY,
    external_id         UUID NOT NULL UNIQUE,
    code                VARCHAR(50) NOT NULL UNIQUE,
    name                VARCHAR(255) NOT NULL,
    
    -- Audit fields
    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by          VARCHAR(255),
    last_modified_by    VARCHAR(255),
    
    CONSTRAINT chk_payment_status_code_not_empty CHECK (code <> ''),
    CONSTRAINT chk_payment_status_name_not_empty CHECK (name <> '')
);

-- ---------------------------------------------------------------------------------------------------------------------
-- Table: service_providers
-- Description: Payment service providers (STRIPE, PAYPAL, SQUARE)
-- ---------------------------------------------------------------------------------------------------------------------
CREATE TABLE service_providers (
    id                  BIGSERIAL PRIMARY KEY,
    external_id         UUID NOT NULL UNIQUE,
    code                VARCHAR(50) NOT NULL UNIQUE,
    name                VARCHAR(255) NOT NULL,
    
    -- Audit fields
    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by          VARCHAR(255),
    last_modified_by    VARCHAR(255),
    
    CONSTRAINT chk_service_provider_code_not_empty CHECK (code <> ''),
    CONSTRAINT chk_service_provider_name_not_empty CHECK (name <> '')
);

-- =====================================================================================================================
-- SECTION 2: USER PAYMENT TABLES
-- =====================================================================================================================
-- Tables for managing user payment profiles and methods

-- ---------------------------------------------------------------------------------------------------------------------
-- Table: user_payment_profiles
-- Description: Links users to Stripe customer IDs and payment profiles
-- ---------------------------------------------------------------------------------------------------------------------
CREATE TABLE user_payment_profiles (
    id                  BIGSERIAL PRIMARY KEY,
    external_id         UUID NOT NULL UNIQUE,
    user_id             BIGINT NOT NULL,
    stripe_customer_id  VARCHAR(255) UNIQUE,
    is_active           BOOLEAN NOT NULL DEFAULT true,
    
    -- Audit fields
    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by          VARCHAR(255),
    last_modified_by    VARCHAR(255),
    
    CONSTRAINT chk_user_payment_profile_user_id CHECK (user_id > 0)
);

-- ---------------------------------------------------------------------------------------------------------------------
-- Table: user_payment_methods
-- Description: User's saved payment methods linked to Stripe payment methods
-- ---------------------------------------------------------------------------------------------------------------------
CREATE TABLE user_payment_methods (
    id                          BIGSERIAL PRIMARY KEY,
    external_id                 UUID NOT NULL UNIQUE,
    user_payment_profile_id     BIGINT NOT NULL,
    payment_method_id           BIGINT NOT NULL,
    stripe_payment_method_id    VARCHAR(255),
    is_default                  BOOLEAN NOT NULL DEFAULT false,
    is_active                   BOOLEAN NOT NULL DEFAULT true,
    
    -- Audit fields
    date_created                TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_date_modified          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by                  VARCHAR(255),
    last_modified_by            VARCHAR(255),
    is_deleted                  BOOLEAN NOT NULL DEFAULT FALSE,
    
    CONSTRAINT fk_user_payment_method_profile FOREIGN KEY (user_payment_profile_id) 
        REFERENCES user_payment_profiles(id) ON DELETE CASCADE,
    CONSTRAINT fk_user_payment_method_method FOREIGN KEY (payment_method_id) 
        REFERENCES payment_methods(id) ON DELETE RESTRICT
);

-- =====================================================================================================================
-- SECTION 3: CORE TRANSACTION TABLES
-- =====================================================================================================================
-- Main financial transaction tables

-- ---------------------------------------------------------------------------------------------------------------------
-- Table: financial_transactions
-- Description: Core financial transaction records with Stripe integration
-- ---------------------------------------------------------------------------------------------------------------------
CREATE TABLE financial_transactions (
    id                          BIGSERIAL PRIMARY KEY,
    external_id                 UUID NOT NULL UNIQUE,
    payer_id                    BIGINT NOT NULL,
    recipient_id                BIGINT NOT NULL,
    amount                      DECIMAL(19, 2) NOT NULL,
    currency_id                 BIGINT NOT NULL,
    date_time                   TIMESTAMP NOT NULL,
    payment_method_id           BIGINT NOT NULL,
    payment_status_id           BIGINT NOT NULL,
    service_provider_id         BIGINT,
    
    -- Stripe integration fields
    stripe_payment_intent_id    VARCHAR(255),
    stripe_charge_id            VARCHAR(255),
    stripe_refund_id            VARCHAR(255),
    
    -- Self-reference for refunds
    original_transaction_id     BIGINT,
    
    -- Audit fields
    created_at                  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at                  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by                  VARCHAR(255),
    last_modified_by            VARCHAR(255),
    
    CONSTRAINT fk_fin_transaction_currency FOREIGN KEY (currency_id) 
        REFERENCES currencies(id) ON DELETE RESTRICT,
    CONSTRAINT fk_fin_transaction_payment_method FOREIGN KEY (payment_method_id) 
        REFERENCES payment_methods(id) ON DELETE RESTRICT,
    CONSTRAINT fk_fin_transaction_payment_status FOREIGN KEY (payment_status_id) 
        REFERENCES payment_statuses(id) ON DELETE RESTRICT,
    CONSTRAINT fk_fin_transaction_service_provider FOREIGN KEY (service_provider_id) 
        REFERENCES service_providers(id) ON DELETE SET NULL,
    CONSTRAINT fk_fin_transaction_original FOREIGN KEY (original_transaction_id) 
        REFERENCES financial_transactions(id) ON DELETE SET NULL,
    CONSTRAINT chk_fin_transaction_amount CHECK (amount >= 0),
    CONSTRAINT chk_fin_transaction_payer_id CHECK (payer_id > 0),
    CONSTRAINT chk_fin_transaction_recipient_id CHECK (recipient_id > 0)
);

-- =====================================================================================================================
-- SECTION 4: TRANSACTION TYPE TABLES (JUNCTION TABLES)
-- =====================================================================================================================
-- Tables linking specific transaction types to financial transactions

-- ---------------------------------------------------------------------------------------------------------------------
-- Table: rental_fin_transactions
-- Description: Links rental transactions to financial transactions
-- ---------------------------------------------------------------------------------------------------------------------
CREATE TABLE rental_fin_transactions (
    id                          BIGSERIAL PRIMARY KEY,
    external_id                 UUID NOT NULL UNIQUE,
    rental_external_id          VARCHAR(100),
    financial_transaction_id    BIGINT NOT NULL,
    
    -- Audit fields
    date_created                TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_date_modified          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by                  VARCHAR(255),
    last_modified_by            VARCHAR(255),
    is_deleted                  BOOLEAN NOT NULL DEFAULT FALSE,
    
    CONSTRAINT fk_rental_fin_transaction FOREIGN KEY (financial_transaction_id) 
        REFERENCES financial_transactions(id) ON DELETE CASCADE
);

-- ---------------------------------------------------------------------------------------------------------------------
-- Table: b2b_sale_fin_transactions
-- Description: Links B2B sale transactions to financial transactions
-- ---------------------------------------------------------------------------------------------------------------------
CREATE TABLE b2b_sale_fin_transactions (
    id                          BIGSERIAL PRIMARY KEY,
    external_id                 UUID NOT NULL UNIQUE,
    b2b_sale_id                 BIGINT NOT NULL,
    financial_transaction_id    BIGINT NOT NULL,
    
    -- Audit fields
    created_at                  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at                  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by                  VARCHAR(255),
    last_modified_by            VARCHAR(255),
    
    CONSTRAINT fk_b2b_sale_fin_transaction FOREIGN KEY (financial_transaction_id) 
        REFERENCES financial_transactions(id) ON DELETE CASCADE,
    CONSTRAINT chk_b2b_sale_id CHECK (b2b_sale_id > 0)
);

-- ---------------------------------------------------------------------------------------------------------------------
-- Table: b2b_subscription_fin_transactions
-- Description: Links B2B subscription transactions to financial transactions
-- ---------------------------------------------------------------------------------------------------------------------
CREATE TABLE b2b_subscription_fin_transactions (
    id                          BIGSERIAL PRIMARY KEY,
    external_id                 UUID NOT NULL UNIQUE,
    b2b_subscription_id         BIGINT NOT NULL,
    financial_transaction_id    BIGINT NOT NULL,
    
    -- Audit fields
    created_at                  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at                  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by                  VARCHAR(255),
    last_modified_by            VARCHAR(255),
    
    CONSTRAINT fk_b2b_subscription_fin_transaction FOREIGN KEY (financial_transaction_id) 
        REFERENCES financial_transactions(id) ON DELETE CASCADE,
    CONSTRAINT chk_b2b_subscription_id CHECK (b2b_subscription_id > 0)
);

-- =====================================================================================================================
-- SECTION 5: REVENUE SHARE & PAYOUT TABLES
-- =====================================================================================================================
-- Tables for managing B2B revenue sharing and payouts

-- ---------------------------------------------------------------------------------------------------------------------
-- Table: b2b_revenue_share_payouts
-- Description: B2B revenue share payout aggregations for companies
-- ---------------------------------------------------------------------------------------------------------------------
CREATE TABLE b2b_revenue_share_payouts (
    id                      BIGSERIAL PRIMARY KEY,
    external_id             UUID NOT NULL UNIQUE,
    company_id              BIGINT NOT NULL,
    payment_status_id       BIGINT NOT NULL,
    due_date                DATE NOT NULL,
    total_amount            DECIMAL(19, 2) NOT NULL,
    paid_amount             DECIMAL(19, 2) NOT NULL DEFAULT 0.00,
    remaining_amount        DECIMAL(19, 2) NOT NULL,
    
    -- Audit fields
    created_at              TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at              TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by              VARCHAR(255),
    last_modified_by        VARCHAR(255),
    
    CONSTRAINT fk_b2b_payout_payment_status FOREIGN KEY (payment_status_id) 
        REFERENCES payment_statuses(id) ON DELETE RESTRICT,
    CONSTRAINT chk_b2b_payout_company_id CHECK (company_id > 0),
    CONSTRAINT chk_b2b_payout_total_amount CHECK (total_amount >= 0),
    CONSTRAINT chk_b2b_payout_paid_amount CHECK (paid_amount >= 0),
    CONSTRAINT chk_b2b_payout_remaining_amount CHECK (remaining_amount >= 0)
);

-- ---------------------------------------------------------------------------------------------------------------------
-- Table: b2b_revenue_share_payout_items
-- Description: Individual bike rental items in a revenue share payout
-- ---------------------------------------------------------------------------------------------------------------------
CREATE TABLE b2b_revenue_share_payout_items (
    id                              BIGSERIAL PRIMARY KEY,
    external_id                     UUID NOT NULL UNIQUE,
    b2b_revenue_share_payout_id     BIGINT NOT NULL,
    bike_rental_id                  BIGINT NOT NULL,
    amount                          DECIMAL(19, 2) NOT NULL,
    
    -- Audit fields
    created_at                      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at                      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by                      VARCHAR(255),
    last_modified_by                VARCHAR(255),
    
    CONSTRAINT fk_payout_item_payout FOREIGN KEY (b2b_revenue_share_payout_id) 
        REFERENCES b2b_revenue_share_payouts(id) ON DELETE CASCADE,
    CONSTRAINT chk_payout_item_bike_rental_id CHECK (bike_rental_id > 0),
    CONSTRAINT chk_payout_item_amount CHECK (amount >= 0)
);

-- ---------------------------------------------------------------------------------------------------------------------
-- Table: payout_fin_transactions
-- Description: Links B2B revenue share payouts to financial transactions
-- ---------------------------------------------------------------------------------------------------------------------
CREATE TABLE payout_fin_transactions (
    id                              BIGSERIAL PRIMARY KEY,
    external_id                     UUID NOT NULL UNIQUE,
    b2b_revenue_share_payout_id     BIGINT NOT NULL,
    financial_transaction_id        BIGINT NOT NULL,
    
    -- Audit fields
    date_created                    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_date_modified              TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by                      VARCHAR(255),
    last_modified_by                VARCHAR(255),
    is_deleted                      BOOLEAN NOT NULL DEFAULT FALSE,
    
    CONSTRAINT fk_payout_fin_transaction_payout FOREIGN KEY (b2b_revenue_share_payout_id) 
        REFERENCES b2b_revenue_share_payouts(id) ON DELETE CASCADE,
    CONSTRAINT fk_payout_fin_transaction_transaction FOREIGN KEY (financial_transaction_id) 
        REFERENCES financial_transactions(id) ON DELETE CASCADE
);

-- =====================================================================================================================
-- SECTION 6: INDEXES
-- =====================================================================================================================
-- Indexes for improved query performance

-- Currencies indexes
CREATE INDEX idx_currencies_external_id ON currencies(external_id);
CREATE INDEX idx_currencies_code ON currencies(code);

-- Payment Methods indexes
CREATE INDEX idx_payment_methods_external_id ON payment_methods(external_id);
CREATE INDEX idx_payment_methods_code ON payment_methods(code);
CREATE INDEX idx_payment_methods_is_active ON payment_methods(is_active);

-- Payment Statuses indexes
CREATE INDEX idx_payment_statuses_external_id ON payment_statuses(external_id);
CREATE INDEX idx_payment_statuses_code ON payment_statuses(code);

-- Service Providers indexes
CREATE INDEX idx_service_providers_external_id ON service_providers(external_id);
CREATE INDEX idx_service_providers_code ON service_providers(code);

-- User Payment Profiles indexes
CREATE INDEX idx_user_payment_profiles_external_id ON user_payment_profiles(external_id);
CREATE INDEX idx_user_payment_profiles_user_id ON user_payment_profiles(user_id);
CREATE INDEX idx_user_payment_profiles_stripe_customer_id ON user_payment_profiles(stripe_customer_id);

-- User Payment Methods indexes
CREATE INDEX idx_user_payment_methods_external_id ON user_payment_methods(external_id);
CREATE INDEX idx_user_payment_methods_profile_id ON user_payment_methods(user_payment_profile_id);
CREATE INDEX idx_user_payment_methods_stripe_pm_id ON user_payment_methods(stripe_payment_method_id);

-- Financial Transactions indexes
CREATE INDEX idx_financial_transactions_external_id ON financial_transactions(external_id);
CREATE INDEX idx_financial_transactions_payer_id ON financial_transactions(payer_id);
CREATE INDEX idx_financial_transactions_recipient_id ON financial_transactions(recipient_id);
CREATE INDEX idx_financial_transactions_currency_id ON financial_transactions(currency_id);
CREATE INDEX idx_financial_transactions_payment_status_id ON financial_transactions(payment_status_id);
CREATE INDEX idx_financial_transactions_stripe_payment_intent_id ON financial_transactions(stripe_payment_intent_id);
CREATE INDEX idx_financial_transactions_stripe_charge_id ON financial_transactions(stripe_charge_id);
CREATE INDEX idx_financial_transactions_date_time ON financial_transactions(date_time);

-- Rental Fin Transactions indexes
CREATE INDEX idx_rental_fin_transactions_external_id ON rental_fin_transactions(external_id);
CREATE INDEX idx_rental_fin_transactions_rental_external_id ON rental_fin_transactions(rental_external_id);
CREATE INDEX idx_rental_fin_transactions_fin_transaction_id ON rental_fin_transactions(financial_transaction_id);

-- B2B Sale Fin Transactions indexes
CREATE INDEX idx_b2b_sale_fin_transactions_external_id ON b2b_sale_fin_transactions(external_id);
CREATE INDEX idx_b2b_sale_fin_transactions_sale_id ON b2b_sale_fin_transactions(b2b_sale_id);
CREATE INDEX idx_b2b_sale_fin_transactions_fin_transaction_id ON b2b_sale_fin_transactions(financial_transaction_id);

-- B2B Subscription Fin Transactions indexes
CREATE INDEX idx_b2b_subscription_fin_transactions_external_id ON b2b_subscription_fin_transactions(external_id);
CREATE INDEX idx_b2b_subscription_fin_transactions_subscription_id ON b2b_subscription_fin_transactions(b2b_subscription_id);
CREATE INDEX idx_b2b_subscription_fin_transactions_fin_transaction_id ON b2b_subscription_fin_transactions(financial_transaction_id);

-- B2B Revenue Share Payouts indexes
CREATE INDEX idx_b2b_revenue_share_payouts_external_id ON b2b_revenue_share_payouts(external_id);
CREATE INDEX idx_b2b_revenue_share_payouts_company_id ON b2b_revenue_share_payouts(company_id);
CREATE INDEX idx_b2b_revenue_share_payouts_payment_status_id ON b2b_revenue_share_payouts(payment_status_id);
CREATE INDEX idx_b2b_revenue_share_payouts_due_date ON b2b_revenue_share_payouts(due_date);

-- B2B Revenue Share Payout Items indexes
CREATE INDEX idx_b2b_payout_items_external_id ON b2b_revenue_share_payout_items(external_id);
CREATE INDEX idx_b2b_payout_items_payout_id ON b2b_revenue_share_payout_items(b2b_revenue_share_payout_id);
CREATE INDEX idx_b2b_payout_items_bike_rental_id ON b2b_revenue_share_payout_items(bike_rental_id);

-- Payout Fin Transactions indexes
CREATE INDEX idx_payout_fin_transactions_external_id ON payout_fin_transactions(external_id);
CREATE INDEX idx_payout_fin_transactions_payout_id ON payout_fin_transactions(b2b_revenue_share_payout_id);
CREATE INDEX idx_payout_fin_transactions_fin_transaction_id ON payout_fin_transactions(financial_transaction_id);

-- =====================================================================================================================
-- SECTION 7: TEST/MOCKUP DATA (OPTIONAL)
-- =====================================================================================================================
-- This section contains sample data for testing and development purposes.
-- Comment out or remove this section before deploying to production if you don't want test data.

-- ---------------------------------------------------------------------------------------------------------------------
-- 7.1 CURRENCIES
-- ---------------------------------------------------------------------------------------------------------------------
INSERT INTO currencies (id, external_id, code, name, created_at, updated_at, created_by, last_modified_by) VALUES
(1, '550e8400-e29b-41d4-a716-446655440001'::uuid, 'USD', 'US Dollar', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),
(2, '550e8400-e29b-41d4-a716-446655440002'::uuid, 'EUR', 'Euro', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),
(3, '550e8400-e29b-41d4-a716-446655440003'::uuid, 'GBP', 'British Pound', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),
(4, '550e8400-e29b-41d4-a716-446655440004'::uuid, 'JPY', 'Japanese Yen', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system')
ON CONFLICT (code) DO NOTHING;

-- ---------------------------------------------------------------------------------------------------------------------
-- 7.2 PAYMENT METHODS
-- ---------------------------------------------------------------------------------------------------------------------
INSERT INTO payment_methods (id, external_id, code, name, is_active, created_at, updated_at, created_by, last_modified_by) VALUES
-- Generic/Legacy Methods
(1, '550e8400-e29b-41d4-a716-446655440011'::uuid, 'CREDIT_CARD', 'Credit Card', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),
(2, '550e8400-e29b-41d4-a716-446655440012'::uuid, 'DEBIT_CARD', 'Debit Card', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),
(3, '550e8400-e29b-41d4-a716-446655440013'::uuid, 'BANK_TRANSFER', 'Bank Transfer', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),
(4, '550e8400-e29b-41d4-a716-446655440014'::uuid, 'DIGITAL_WALLET', 'Digital Wallet', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),
(5, '550e8400-e29b-41d4-a716-446655440015'::uuid, 'CASH', 'Cash', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),

-- Banking Methods
(6, '550e8400-e29b-41d4-a716-446655440016'::uuid, 'IDEAL', 'iDEAL', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),
(7, '550e8400-e29b-41d4-a716-446655440017'::uuid, 'IDEALQR', 'iDEAL QR', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),
(8, '550e8400-e29b-41d4-a716-446655440018'::uuid, 'BANCONTACT', 'Bancontact', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),
(9, '550e8400-e29b-41d4-a716-446655440019'::uuid, 'BANCONTACTQR', 'Bancontact QR', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),
(10, '550e8400-e29b-41d4-a716-446655440020'::uuid, 'BELFIUS', 'Belfius', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),
(11, '550e8400-e29b-41d4-a716-446655440021'::uuid, 'BIZUM', 'Bizum', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),
(12, '550e8400-e29b-41d4-a716-446655440022'::uuid, 'CBC', 'CBC', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),
(13, '550e8400-e29b-41d4-a716-446655440023'::uuid, 'KBC', 'KBC', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),
(14, '550e8400-e29b-41d4-a716-446655440024'::uuid, 'DIRDEB', 'Direct Debit (SEPA)', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),
(15, '550e8400-e29b-41d4-a716-446655440025'::uuid, 'DIRECTBANK', 'Direct Bank Transfer', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),
(16, '550e8400-e29b-41d4-a716-446655440026'::uuid, 'DOTPAY', 'Dotpay', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),
(17, '550e8400-e29b-41d4-a716-446655440027'::uuid, 'EPS', 'EPS', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),
(18, '550e8400-e29b-41d4-a716-446655440028'::uuid, 'GIROPAY', 'Giropay', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),
(19, '550e8400-e29b-41d4-a716-446655440029'::uuid, 'MBWAY', 'MB WAY', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),
(20, '550e8400-e29b-41d4-a716-446655440030'::uuid, 'MULTIBANCO', 'Multibanco', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),
(21, '550e8400-e29b-41d4-a716-446655440031'::uuid, 'MYBANK', 'MyBank', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),
(22, '550e8400-e29b-41d4-a716-446655440032'::uuid, 'SOFORT', 'Sofort', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),
(23, '550e8400-e29b-41d4-a716-446655440033'::uuid, 'TRUSTLY', 'Trustly', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),

-- Card Schemes
(24, '550e8400-e29b-41d4-a716-446655440034'::uuid, 'VISA', 'Visa', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),
(25, '550e8400-e29b-41d4-a716-446655440035'::uuid, 'MASTERCARD', 'Mastercard', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),
(26, '550e8400-e29b-41d4-a716-446655440036'::uuid, 'MAESTRO', 'Maestro', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),
(27, '550e8400-e29b-41d4-a716-446655440037'::uuid, 'AMEX', 'American Express', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),
(28, '550e8400-e29b-41d4-a716-446655440038'::uuid, 'DANKORT', 'Dankort', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),
(29, '550e8400-e29b-41d4-a716-446655440039'::uuid, 'CARTEBANCAIRE', 'Cartes Bancaires', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),
(30, '550e8400-e29b-41d4-a716-446655440040'::uuid, 'POSTEPAY', 'Postepay', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),

-- BNPL (Buy Now Pay Later) Methods
(31, '550e8400-e29b-41d4-a716-446655440041'::uuid, 'BILLINK', 'Billink', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),
(32, '550e8400-e29b-41d4-a716-446655440042'::uuid, 'EINVOICE', 'E-Invoicing', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),
(33, '550e8400-e29b-41d4-a716-446655440043'::uuid, 'IN3', 'iDEAL in3', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),
(34, '550e8400-e29b-41d4-a716-446655440044'::uuid, 'KLARNA', 'Klarna', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),
(35, '550e8400-e29b-41d4-a716-446655440045'::uuid, 'PAYAFTER', 'Pay After Delivery', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),
(36, '550e8400-e29b-41d4-a716-446655440046'::uuid, 'AFTERPAY', 'Riverty (AfterPay)', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),

-- Prepaid Cards
(37, '550e8400-e29b-41d4-a716-446655440047'::uuid, 'EDENRED', 'Edenred', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),
(38, '550e8400-e29b-41d4-a716-446655440048'::uuid, 'BEAUTYANDWELLNESS', 'Beauty & Wellness Gift Card', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),
(39, '550e8400-e29b-41d4-a716-446655440049'::uuid, 'BOEKENBON', 'Boekenbon', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),
(40, '550e8400-e29b-41d4-a716-446655440050'::uuid, 'FASHIONCHEQUE', 'Fashioncheque', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),
(41, '550e8400-e29b-41d4-a716-446655440051'::uuid, 'FASHIONGIFTCARD', 'Fashion Gift Card', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),
(42, '550e8400-e29b-41d4-a716-446655440052'::uuid, 'VVVGIFTCARD', 'VVV Cadeaukaart', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),
(43, '550e8400-e29b-41d4-a716-446655440053'::uuid, 'WEBSHOPGIFTCARD', 'Webshop Giftcard', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),
(44, '550e8400-e29b-41d4-a716-446655440054'::uuid, 'MONIZZE', 'Monizze', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),
(45, '550e8400-e29b-41d4-a716-446655440055'::uuid, 'PAYSAFECARD', 'Paysafecard', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),
(46, '550e8400-e29b-41d4-a716-446655440056'::uuid, 'SODEXO', 'Sodexo', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),

-- Wallets
(47, '550e8400-e29b-41d4-a716-446655440057'::uuid, 'ALIPAY', 'Alipay', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),
(48, '550e8400-e29b-41d4-a716-446655440058'::uuid, 'ALIPAYPLUS', 'Alipay+', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),
(49, '550e8400-e29b-41d4-a716-446655440059'::uuid, 'AMAZONPAY', 'Amazon Pay', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),
(50, '550e8400-e29b-41d4-a716-446655440060'::uuid, 'APPLEPAY', 'Apple Pay', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),
(51, '550e8400-e29b-41d4-a716-446655440061'::uuid, 'GOOGLEPAY', 'Google Pay', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),
(52, '550e8400-e29b-41d4-a716-446655440062'::uuid, 'PAYPAL', 'PayPal', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),
(53, '550e8400-e29b-41d4-a716-446655440063'::uuid, 'WECHAT', 'WeChat Pay', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system')
ON CONFLICT (code) DO NOTHING;

-- ---------------------------------------------------------------------------------------------------------------------
-- 7.3 PAYMENT STATUSES
-- ---------------------------------------------------------------------------------------------------------------------
INSERT INTO payment_statuses (id, external_id, code, name, created_at, updated_at, created_by, last_modified_by) VALUES
(1, '550e8400-e29b-41d4-a716-446655440021'::uuid, 'PENDING', 'Payment Pending', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),
(2, '550e8400-e29b-41d4-a716-446655440022'::uuid, 'SUCCEEDED', 'Payment Succeeded', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),
(3, '550e8400-e29b-41d4-a716-446655440023'::uuid, 'FAILED', 'Payment Failed', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),
(4, '550e8400-e29b-41d4-a716-446655440024'::uuid, 'CANCELED', 'Payment Canceled', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),
(5, '550e8400-e29b-41d4-a716-446655440025'::uuid, 'REFUNDED', 'Payment Refunded', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),
(6, '550e8400-e29b-41d4-a716-446655440026'::uuid, 'PARTIALLY_REFUNDED', 'Payment Partially Refunded', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system')
ON CONFLICT (code) DO NOTHING;

-- ---------------------------------------------------------------------------------------------------------------------
-- 7.4 SERVICE PROVIDERS
-- ---------------------------------------------------------------------------------------------------------------------
INSERT INTO service_providers (id, external_id, code, name, created_at, updated_at, created_by, last_modified_by) VALUES
(1, '550e8400-e29b-41d4-a716-446655440031'::uuid, 'STRIPE', 'Stripe', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),
(2, '550e8400-e29b-41d4-a716-446655440032'::uuid, 'PAYPAL', 'PayPal', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),
(3, '550e8400-e29b-41d4-a716-446655440033'::uuid, 'SQUARE', 'Square', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system')
ON CONFLICT (code) DO NOTHING;

-- ---------------------------------------------------------------------------------------------------------------------
-- 7.5 USER PAYMENT PROFILES
-- ---------------------------------------------------------------------------------------------------------------------
INSERT INTO user_payment_profiles (id, external_id, user_id, stripe_customer_id, is_active, created_at, updated_at, created_by, last_modified_by) VALUES
(1, '550e8400-e29b-41d4-a716-446655440041'::uuid, 1, 'cus_test_user1', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),
(2, '550e8400-e29b-41d4-a716-446655440042'::uuid, 2, 'cus_test_user2', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),
(3, '550e8400-e29b-41d4-a716-446655440043'::uuid, 3, 'cus_test_user3', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system')
ON CONFLICT (code) DO NOTHING;

-- ---------------------------------------------------------------------------------------------------------------------
-- 7.6 USER PAYMENT METHODS
-- ---------------------------------------------------------------------------------------------------------------------
INSERT INTO user_payment_methods (id, external_id, user_payment_profile_id, payment_method_id, stripe_payment_method_id, is_default, is_active, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, '550e8400-e29b-41d4-a716-446655440051'::uuid, 1, 1, 'pm_test_card1', true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', FALSE),
(2, '550e8400-e29b-41d4-a716-446655440052'::uuid, 1, 1, 'pm_test_card2', false, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', FALSE),
(3, '550e8400-e29b-41d4-a716-446655440053'::uuid, 2, 1, 'pm_test_card3', true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', FALSE)
ON CONFLICT (code) DO NOTHING;

-- ---------------------------------------------------------------------------------------------------------------------
-- 7.7 FINANCIAL TRANSACTIONS
-- ---------------------------------------------------------------------------------------------------------------------
INSERT INTO financial_transactions (id, external_id, payer_id, recipient_id, amount, currency_id, date_time, payment_method_id, payment_status_id, service_provider_id, stripe_payment_intent_id, stripe_charge_id, created_at, updated_at, created_by, last_modified_by) VALUES
(1, '550e8400-e29b-41d4-a716-446655440061'::uuid, 1, 2, 100.00, 1, CURRENT_TIMESTAMP, 1, 2, 1, 'pi_test_intent1', 'ch_test_charge1', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),
(2, '550e8400-e29b-41d4-a716-446655440062'::uuid, 2, 1, 50.00, 2, CURRENT_TIMESTAMP, 1, 2, 1, 'pi_test_intent2', 'ch_test_charge2', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),
(3, '550e8400-e29b-41d4-a716-446655440063'::uuid, 3, 1, 25.00, 1, CURRENT_TIMESTAMP, 2, 1, 1, NULL, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system')
ON CONFLICT (code) DO NOTHING;

-- ---------------------------------------------------------------------------------------------------------------------
-- 7.8 RENTAL FIN TRANSACTIONS
-- ---------------------------------------------------------------------------------------------------------------------
INSERT INTO rental_fin_transactions (id, external_id, rental_external_id, financial_transaction_id, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, '550e8400-e29b-41d4-a716-446655440071'::uuid, 'rental-ext-00101', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', FALSE),
(2, '550e8400-e29b-41d4-a716-446655440072'::uuid, 'rental-ext-00102', 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', FALSE)
ON CONFLICT (code) DO NOTHING;

-- ---------------------------------------------------------------------------------------------------------------------
-- 7.9 B2B SALE FIN TRANSACTIONS
-- ---------------------------------------------------------------------------------------------------------------------
INSERT INTO b2b_sale_fin_transactions (id, external_id, b2b_sale_id, financial_transaction_id, created_at, updated_at, created_by, last_modified_by) VALUES
(1, '550e8400-e29b-41d4-a716-446655440081'::uuid, 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system')
ON CONFLICT (code) DO NOTHING;

-- ---------------------------------------------------------------------------------------------------------------------
-- 7.10 B2B SUBSCRIPTION FIN TRANSACTIONS
-- ---------------------------------------------------------------------------------------------------------------------
INSERT INTO b2b_subscription_fin_transactions (id, external_id, b2b_subscription_id, financial_transaction_id, created_at, updated_at, created_by, last_modified_by) VALUES
(1, '550e8400-e29b-41d4-a716-446655440091'::uuid, 1, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system')
ON CONFLICT (code) DO NOTHING;

-- ---------------------------------------------------------------------------------------------------------------------
-- 7.11 B2B REVENUE SHARE PAYOUTS
-- ---------------------------------------------------------------------------------------------------------------------
INSERT INTO b2b_revenue_share_payouts (id, external_id, company_id, payment_status_id, due_date, total_amount, paid_amount, remaining_amount, created_at, updated_at, created_by, last_modified_by) VALUES
(1, '550e8400-e29b-41d4-a716-446655440101'::uuid, 1, 1, CURRENT_DATE + INTERVAL '30 days', 1000.00, 0.00, 1000.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),
(2, '550e8400-e29b-41d4-a716-446655440102'::uuid, 2, 2, CURRENT_DATE + INTERVAL '15 days', 2500.00, 2500.00, 0.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),
(3, '550e8400-e29b-41d4-a716-446655440103'::uuid, 3, 6, CURRENT_DATE - INTERVAL '10 days', 750.00, 500.00, 250.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system')
ON CONFLICT (code) DO NOTHING;

-- ---------------------------------------------------------------------------------------------------------------------
-- 7.12 B2B REVENUE SHARE PAYOUT ITEMS
-- ---------------------------------------------------------------------------------------------------------------------
INSERT INTO b2b_revenue_share_payout_items (id, external_id, b2b_revenue_share_payout_id, bike_rental_id, amount, created_at, updated_at, created_by, last_modified_by) VALUES
(1, '550e8400-e29b-41d4-a716-446655440111'::uuid, 1, 1, 50.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),
(2, '550e8400-e29b-41d4-a716-446655440112'::uuid, 1, 2, 75.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),
(3, '550e8400-e29b-41d4-a716-446655440113'::uuid, 2, 1, 125.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system')
ON CONFLICT (code) DO NOTHING;

-- ---------------------------------------------------------------------------------------------------------------------
-- 7.13 PAYOUT FIN TRANSACTIONS
-- ---------------------------------------------------------------------------------------------------------------------
INSERT INTO payout_fin_transactions (id, external_id, b2b_revenue_share_payout_id, financial_transaction_id, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, '550e8400-e29b-41d4-a716-446655440121'::uuid, 2, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', FALSE)
ON CONFLICT (code) DO NOTHING;

-- =====================================================================================================================
-- SECTION 8: SEQUENCE RESET
-- =====================================================================================================================
-- Reset sequences to the correct values after inserting test data
-- This ensures that new records will have IDs starting after the test data IDs

SELECT setval('currencies_id_seq', (SELECT COALESCE(MAX(id), 1) FROM currencies));
SELECT setval('payment_methods_id_seq', (SELECT COALESCE(MAX(id), 1) FROM payment_methods));
SELECT setval('payment_statuses_id_seq', (SELECT COALESCE(MAX(id), 1) FROM payment_statuses));
SELECT setval('service_providers_id_seq', (SELECT COALESCE(MAX(id), 1) FROM service_providers));
SELECT setval('user_payment_profiles_id_seq', (SELECT COALESCE(MAX(id), 1) FROM user_payment_profiles));
SELECT setval('user_payment_methods_id_seq', (SELECT COALESCE(MAX(id), 1) FROM user_payment_methods));
SELECT setval('financial_transactions_id_seq', (SELECT COALESCE(MAX(id), 1) FROM financial_transactions));
SELECT setval('rental_fin_transactions_id_seq', (SELECT COALESCE(MAX(id), 1) FROM rental_fin_transactions));
SELECT setval('b2b_sale_fin_transactions_id_seq', (SELECT COALESCE(MAX(id), 1) FROM b2b_sale_fin_transactions));
SELECT setval('b2b_subscription_fin_transactions_id_seq', (SELECT COALESCE(MAX(id), 1) FROM b2b_subscription_fin_transactions));
SELECT setval('b2b_revenue_share_payouts_id_seq', (SELECT COALESCE(MAX(id), 1) FROM b2b_revenue_share_payouts));
SELECT setval('b2b_revenue_share_payout_items_id_seq', (SELECT COALESCE(MAX(id), 1) FROM b2b_revenue_share_payout_items));
SELECT setval('payout_fin_transactions_id_seq', (SELECT COALESCE(MAX(id), 1) FROM payout_fin_transactions));

-- =====================================================================================================================
-- END OF SCHEMA
-- =====================================================================================================================
-- Schema created successfully!
-- Total tables: 13
-- Stripe integration: Fully configured with payment intents, charges, refunds, and customer IDs
-- Test data: Sample currencies, payment methods, users, and transactions
-- =====================================================================================================================








