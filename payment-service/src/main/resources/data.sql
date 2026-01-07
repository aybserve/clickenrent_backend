-- =====================================================================================================================
-- PAYMENT SERVICE - DATABASE INITIALIZATION
-- =====================================================================================================================
-- Module: payment-service
-- Database: PostgreSQL
-- Description: Production database initialization for the payment processing and financial transaction service.
--              Contains required lookup data and optional reference/test data.
-- 
-- Usage:
--   This file is automatically executed by Spring Boot on application startup when:
--   - spring.jpa.hibernate.ddl-auto is set to 'create', 'create-drop', or 'update'
--   - spring.sql.init.mode is set to 'always' (default is 'embedded')
--
-- Note: For production deployment, ensure only required lookup data is uncommented.
--       Test/sample data should be commented out or removed.
--
-- Author: Vitaliy Shvetsov
-- =====================================================================================================================

-- =====================================================================================================================
-- SECTION 1: REQUIRED LOOKUP DATA
-- =====================================================================================================================
-- This data is REQUIRED for the application to function properly.
-- Do NOT comment out or remove this section.

-- ---------------------------------------------------------------------------------------------------------------------
-- 1.1 PAYMENT STATUS
-- ---------------------------------------------------------------------------------------------------------------------
INSERT INTO payment_statuses (id, external_id, code, name, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, '550e8400-e29b-41d4-a716-446655440001', 'PENDING', 'Pending', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(2, '550e8400-e29b-41d4-a716-446655440002', 'SUCCEEDED', 'Succeeded', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(3, '550e8400-e29b-41d4-a716-446655440003', 'FAILED', 'Failed', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(4, '550e8400-e29b-41d4-a716-446655440004', 'CANCELED', 'Canceled', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(5, '550e8400-e29b-41d4-a716-446655440005', 'REFUNDED', 'Refunded', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(6, '550e8400-e29b-41d4-a716-446655440006', 'PARTIALLY_REFUNDED', 'Partially Refunded', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- ---------------------------------------------------------------------------------------------------------------------
-- 1.2 PAYMENT METHOD
-- ---------------------------------------------------------------------------------------------------------------------
INSERT INTO payment_methods (id, external_id, code, name, is_active, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, '550e8400-e29b-41d4-a716-446655440011', 'CREDIT_CARD', 'Credit Card', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(2, '550e8400-e29b-41d4-a716-446655440012', 'DEBIT_CARD', 'Debit Card', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(3, '550e8400-e29b-41d4-a716-446655440013', 'BANK_TRANSFER', 'Bank Transfer', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(4, '550e8400-e29b-41d4-a716-446655440014', 'DIGITAL_WALLET', 'Digital Wallet', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(5, '550e8400-e29b-41d4-a716-446655440015', 'CASH', 'Cash', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- ---------------------------------------------------------------------------------------------------------------------
-- 1.3 CURRENCY
-- ---------------------------------------------------------------------------------------------------------------------
INSERT INTO currencies (id, external_id, code, name, symbol, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, '550e8400-e29b-41d4-a716-446655440021', 'USD', 'US Dollar', '$', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(2, '550e8400-e29b-41d4-a716-446655440022', 'EUR', 'Euro', '€', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(3, '550e8400-e29b-41d4-a716-446655440023', 'GBP', 'British Pound', '£', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(4, '550e8400-e29b-41d4-a716-446655440024', 'UAH', 'Ukrainian Hryvnia', '₴', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(5, '550e8400-e29b-41d4-a716-446655440025', 'PLN', 'Polish Zloty', 'zł', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- ---------------------------------------------------------------------------------------------------------------------
-- 1.4 SERVICE PROVIDER
-- ---------------------------------------------------------------------------------------------------------------------
INSERT INTO service_providers (id, external_id, code, name, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, '550e8400-e29b-41d4-a716-446655440031', 'STRIPE', 'Stripe', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(2, '550e8400-e29b-41d4-a716-446655440032', 'PAYPAL', 'PayPal', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(3, '550e8400-e29b-41d4-a716-446655440033', 'INTERNAL', 'Internal Processing', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- =====================================================================================================================
-- SECTION 2: SAMPLE/TEST DATA (OPTIONAL)
-- =====================================================================================================================
-- This section contains sample data for testing and development purposes.
-- COMMENT OUT or REMOVE this entire section before deploying to production.
-- Sample data references external IDs from other services (auth-service, rental-service).

-- ---------------------------------------------------------------------------------------------------------------------
-- 2.1 USER PAYMENT PROFILES (Sample data - references users from auth-service)
-- ---------------------------------------------------------------------------------------------------------------------
INSERT INTO user_payment_profiles (id, external_id, user_external_id, stripe_customer_id, is_active, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, '550e8400-e29b-41d4-a716-446655440101', 'usr-ext-00007', 'cus_test_customer001', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(2, '550e8400-e29b-41d4-a716-446655440102', 'usr-ext-00008', 'cus_test_customer002', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(3, '550e8400-e29b-41d4-a716-446655440103', 'usr-ext-00009', 'cus_test_customer003', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- ---------------------------------------------------------------------------------------------------------------------
-- 2.2 USER PAYMENT METHODS (Sample data)
-- ---------------------------------------------------------------------------------------------------------------------
INSERT INTO user_payment_methods (id, external_id, user_payment_profile_id, payment_method_id, stripe_payment_method_id, is_default, is_active, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, '550e8400-e29b-41d4-a716-446655440051', 1, 1, 'pm_test_card1', true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(2, '550e8400-e29b-41d4-a716-446655440052', 1, 1, 'pm_test_card2', false, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(3, '550e8400-e29b-41d4-a716-446655440053', 2, 1, 'pm_test_card3', true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- ---------------------------------------------------------------------------------------------------------------------
-- 2.3 FINANCIAL TRANSACTIONS (Sample data)
-- ---------------------------------------------------------------------------------------------------------------------
INSERT INTO financial_transactions (id, external_id, payer_external_id, recipient_external_id, amount, currency_id, date_time, payment_method_id, payment_status_id, service_provider_id, stripe_payment_intent_id, stripe_charge_id, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, '550e8400-e29b-41d4-a716-446655440201', 'usr-ext-00007', 'company-ext-001', 25.00, 2, CURRENT_TIMESTAMP, 1, 2, 1, 'pi_test_001', 'ch_test_001', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(2, '550e8400-e29b-41d4-a716-446655440202', 'usr-ext-00008', 'company-ext-001', 35.50, 2, CURRENT_TIMESTAMP, 1, 2, 1, 'pi_test_002', 'ch_test_002', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(3, '550e8400-e29b-41d4-a716-446655440203', 'usr-ext-00009', 'company-ext-002', 15.75, 2, CURRENT_TIMESTAMP, 2, 2, 1, 'pi_test_003', 'ch_test_003', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(4, '550e8400-e29b-41d4-a716-446655440204', 'usr-ext-00007', 'company-ext-001', 50.00, 2, CURRENT_TIMESTAMP, 1, 1, 1, 'pi_test_004', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- ---------------------------------------------------------------------------------------------------------------------
-- 2.4 RENTAL FIN TRANSACTIONS (Sample data - references rentals from rental-service)
-- ---------------------------------------------------------------------------------------------------------------------
INSERT INTO rental_fin_transactions (id, external_id, rental_external_id, financial_transaction_id, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, '550e8400-e29b-41d4-a716-446655440301', 'rental-ext-00101', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(2, '550e8400-e29b-41d4-a716-446655440302', 'rental-ext-00102', 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(3, '550e8400-e29b-41d4-a716-446655440303', 'rental-ext-00103', 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- ---------------------------------------------------------------------------------------------------------------------
-- 2.5 B2B SALE FIN TRANSACTIONS (Sample data - references B2B sales from rental-service)
-- ---------------------------------------------------------------------------------------------------------------------
INSERT INTO b2b_sale_fin_transactions (id, external_id, b2b_sale_external_id, financial_transaction_id, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, '550e8400-e29b-41d4-a716-446655440401', 'b2b-sale-ext-001', 4, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- ---------------------------------------------------------------------------------------------------------------------
-- 2.6 B2B SUBSCRIPTION FIN TRANSACTIONS (Sample data)
-- ---------------------------------------------------------------------------------------------------------------------
INSERT INTO b2b_subscription_fin_transactions (id, external_id, b2b_subscription_external_id, financial_transaction_id, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, '550e8400-e29b-41d4-a716-446655440501', 'b2b-subscription-ext-001', 4, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- ---------------------------------------------------------------------------------------------------------------------
-- 2.7 B2B REVENUE SHARE PAYOUTS (Sample data - references companies from auth-service)
-- ---------------------------------------------------------------------------------------------------------------------
INSERT INTO b2b_revenue_share_payouts (id, external_id, company_external_id, payment_status_id, due_date, total_amount, paid_amount, remaining_amount, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, '550e8400-e29b-41d4-a716-446655440601', 'company-ext-001', 1, CURRENT_DATE + INTERVAL '30 days', 500.00, 0.00, 500.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(2, '550e8400-e29b-41d4-a716-446655440602', 'company-ext-002', 2, CURRENT_DATE - INTERVAL '5 days', 350.75, 350.75, 0.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- ---------------------------------------------------------------------------------------------------------------------
-- 2.8 B2B REVENUE SHARE PAYOUT ITEMS (Sample data)
-- ---------------------------------------------------------------------------------------------------------------------
INSERT INTO b2b_revenue_share_payout_items (id, external_id, b2b_revenue_share_payout_id, bike_rental_external_id, amount, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, '550e8400-e29b-41d4-a716-446655440701', 1, 'bike-rental-ext-00101', 150.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(2, '550e8400-e29b-41d4-a716-446655440702', 1, 'bike-rental-ext-00102', 200.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(3, '550e8400-e29b-41d4-a716-446655440703', 1, 'bike-rental-ext-00103', 150.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(4, '550e8400-e29b-41d4-a716-446655440704', 2, 'bike-rental-ext-00104', 175.50, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(5, '550e8400-e29b-41d4-a716-446655440705', 2, 'bike-rental-ext-00105', 175.25, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- ---------------------------------------------------------------------------------------------------------------------
-- 2.9 PAYOUT FIN TRANSACTIONS (Sample data - links payouts to financial transactions)
-- ---------------------------------------------------------------------------------------------------------------------
INSERT INTO payout_fin_transactions (id, external_id, b2b_revenue_share_payout_id, financial_transaction_id, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, '550e8400-e29b-41d4-a716-446655440121', 2, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', FALSE)
ON CONFLICT (id) DO NOTHING;

-- =====================================================================================================================
-- SECTION 3: SEQUENCE RESET
-- =====================================================================================================================
-- Reset sequences to the correct values after inserting data.
-- This ensures that new records will have IDs starting after the existing data IDs.

SELECT setval('payment_statuses_id_seq', (SELECT COALESCE(MAX(id), 1) FROM payment_statuses));
SELECT setval('payment_methods_id_seq', (SELECT COALESCE(MAX(id), 1) FROM payment_methods));
SELECT setval('currencies_id_seq', (SELECT COALESCE(MAX(id), 1) FROM currencies));
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
-- END OF INITIALIZATION
-- =====================================================================================================================
-- Database initialization completed successfully!
-- 
-- Summary:
-- - Required lookup tables: payment_statuses (6), payment_methods (5), currencies (5), service_providers (3)
-- - Sample data: user_payment_profiles (3), user_payment_methods (3), financial_transactions (4)
-- - Sample data: rental_fin_transactions (3), b2b_sale_fin_transactions (1), b2b_subscription_fin_transactions (1)
-- - Sample data: b2b_revenue_share_payouts (2), b2b_revenue_share_payout_items (5), payout_fin_transactions (1)
-- 
-- Cross-Service References:
-- - User External IDs: Referenced from auth-service (usr-ext-XXXXX)
-- - Company External IDs: Referenced from auth-service (company-ext-XXXXX)
-- - Rental External IDs: Referenced from rental-service (rental-ext-XXXXX)
-- - Bike Rental External IDs: Referenced from rental-service (bike-rental-ext-XXXXX)
-- - B2B Sale External IDs: Referenced from rental-service (b2b-sale-ext-XXXXX)
-- - B2B Subscription External IDs: Referenced from rental-service (b2b-subscription-ext-XXXXX)
-- =====================================================================================================================

-- =====================================================================================================================
-- SECTION 3: ROW LEVEL SECURITY (RLS) FOR MULTI-TENANT ISOLATION
-- =====================================================================================================================
-- Description: Adds PostgreSQL Row Level Security policies to enforce tenant (company) isolation at database level.
--              This is the 3rd layer of defense (after JWT claims and Hibernate filters).
--              RLS ensures that even direct SQL queries cannot access cross-tenant data.
-- 
-- Note: RLS policies use PostgreSQL session variables set by the application:
--       - app.is_superadmin: boolean flag for admin bypass
--       - app.company_external_ids: comma-separated list of company UUIDs user can access
-- =====================================================================================================================

-- Add company_external_id column to financial_transactions for tenant isolation
ALTER TABLE financial_transactions ADD COLUMN IF NOT EXISTS company_external_id VARCHAR(100);
CREATE INDEX IF NOT EXISTS idx_financial_transaction_company ON financial_transactions(company_external_id);

-- Enable RLS on tenant-scoped tables
ALTER TABLE b2b_revenue_share_payouts ENABLE ROW LEVEL SECURITY;
ALTER TABLE financial_transactions ENABLE ROW LEVEL SECURITY;

-- Force RLS even for table owner (important for security)
ALTER TABLE b2b_revenue_share_payouts FORCE ROW LEVEL SECURITY;
ALTER TABLE financial_transactions FORCE ROW LEVEL SECURITY;

-- ---------------------------------------------------------------------------------------------------------------------
-- 3.1 RLS POLICY: b2b_revenue_share_payouts table
-- ---------------------------------------------------------------------------------------------------------------------
-- Allows access if:
-- 1. User is superadmin (bypasses all filters), OR
-- 2. Payout belongs to one of user's companies
-- ---------------------------------------------------------------------------------------------------------------------
DROP POLICY IF EXISTS b2b_revenue_share_payouts_tenant_isolation ON b2b_revenue_share_payouts;
CREATE POLICY b2b_revenue_share_payouts_tenant_isolation ON b2b_revenue_share_payouts
    FOR ALL
    USING (
        -- Allow if user is superadmin
        COALESCE(current_setting('app.is_superadmin', true)::boolean, false) = true
        OR
        -- Allow if payout belongs to one of user's companies
        company_external_id = ANY(
            string_to_array(
                COALESCE(current_setting('app.company_external_ids', true), ''),
                ','
            )
        )
    );

-- ---------------------------------------------------------------------------------------------------------------------
-- 3.2 RLS POLICY: financial_transactions table
-- ---------------------------------------------------------------------------------------------------------------------
-- Allows access if:
-- 1. User is superadmin (bypasses all filters), OR
-- 2. Transaction belongs to one of user's companies (via company_external_id)
-- Note: company_external_id typically represents the recipient company (service provider)
-- ---------------------------------------------------------------------------------------------------------------------
DROP POLICY IF EXISTS financial_transactions_tenant_isolation ON financial_transactions;
CREATE POLICY financial_transactions_tenant_isolation ON financial_transactions
    FOR ALL
    USING (
        -- Allow if user is superadmin
        COALESCE(current_setting('app.is_superadmin', true)::boolean, false) = true
        OR
        -- Allow if transaction belongs to one of user's companies
        company_external_id = ANY(
            string_to_array(
                COALESCE(current_setting('app.company_external_ids', true), ''),
                ','
            )
        )
        OR
        -- Allow if company_external_id is NULL (for backward compatibility or customer transactions)
        company_external_id IS NULL
    );

-- ---------------------------------------------------------------------------------------------------------------------
-- 3.3 PERFORMANCE INDEXES for RLS
-- ---------------------------------------------------------------------------------------------------------------------
-- These indexes ensure RLS policies don't slow down queries
-- ---------------------------------------------------------------------------------------------------------------------
CREATE INDEX IF NOT EXISTS idx_b2b_revenue_share_payouts_company_rls ON b2b_revenue_share_payouts(company_external_id) WHERE is_deleted = false;
CREATE INDEX IF NOT EXISTS idx_financial_transactions_company_rls ON financial_transactions(company_external_id) WHERE is_deleted = false;

-- =====================================================================================================================
-- SECTION 4: AUDIT LOGGING
-- =====================================================================================================================

-- ---------------------------------------------------------------------------------------------------------------------
-- 4.1 AUDIT LOGS TABLE
-- ---------------------------------------------------------------------------------------------------------------------
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

-- ---------------------------------------------------------------------------------------------------------------------
-- 4.2 AUDIT LOGS INDEXES
-- ---------------------------------------------------------------------------------------------------------------------
CREATE INDEX IF NOT EXISTS idx_audit_logs_timestamp ON audit_logs(timestamp);
CREATE INDEX IF NOT EXISTS idx_audit_logs_user ON audit_logs(user_external_id);
CREATE INDEX IF NOT EXISTS idx_audit_logs_event_type ON audit_logs(event_type);
CREATE INDEX IF NOT EXISTS idx_audit_logs_success ON audit_logs(success);

-- =====================================================================================================================
-- END OF INITIALIZATION
-- =====================================================================================================================





