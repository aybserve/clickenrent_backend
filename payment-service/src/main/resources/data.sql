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
(5, '550e8400-e29b-41d4-a716-446655440015', 'CASH', 'Cash', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(6, '550e8400-e29b-41d4-a716-446655440016', 'IDEAL', 'iDEAL', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(7, '550e8400-e29b-41d4-a716-446655440017', 'BANCONTACT', 'Bancontact', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(8, '550e8400-e29b-41d4-a716-446655440018', 'DIRECTBANK', 'Direct Bank Transfer', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
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
(3, '550e8400-e29b-41d4-a716-446655440033', 'INTERNAL', 'Internal Processing', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(4, '550e8400-e29b-41d4-a716-446655440034', 'MULTISAFEPAY', 'MultiSafePay', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
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
-- Stripe customer examples
INSERT INTO user_payment_profiles (id, external_id, user_external_id, stripe_customer_id, is_active, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, '550e8400-e29b-41d4-a716-446655440101', 'usr-ext-00007', 'cus_test_customer001', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(2, '550e8400-e29b-41d4-a716-446655440102', 'usr-ext-00008', 'cus_test_customer002', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(3, '550e8400-e29b-41d4-a716-446655440103', 'usr-ext-00009', 'cus_test_customer003', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- MultiSafePay customer examples (customer ID is typically the email address)
INSERT INTO user_payment_profiles (id, external_id, user_external_id, multi_safepay_customer_id, is_active, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(4, '550e8400-e29b-41d4-a716-446655440104', 'usr-ext-00010', 'john.doe@example.com', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(5, '550e8400-e29b-41d4-a716-446655440105', 'usr-ext-00011', 'jane.smith@example.com', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(6, '550e8400-e29b-41d4-a716-446655440106', 'usr-ext-00012', 'bob.johnson@example.com', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- ---------------------------------------------------------------------------------------------------------------------
-- 2.2 USER PAYMENT METHODS (Sample data)
-- ---------------------------------------------------------------------------------------------------------------------
-- Stripe payment methods
INSERT INTO user_payment_methods (id, external_id, user_payment_profile_id, payment_method_id, stripe_payment_method_id, is_default, is_active, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, '550e8400-e29b-41d4-a716-446655440051', 1, 1, 'pm_test_card1', true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(2, '550e8400-e29b-41d4-a716-446655440052', 1, 1, 'pm_test_card2', false, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(3, '550e8400-e29b-41d4-a716-446655440053', 2, 1, 'pm_test_card3', true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- MultiSafePay payment methods (tokens for recurring payments)
INSERT INTO user_payment_methods (id, external_id, user_payment_profile_id, payment_method_id, multi_safepay_token_id, is_default, is_active, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(4, '550e8400-e29b-41d4-a716-446655440054', 4, 1, 'msp_token_visa_001', true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(5, '550e8400-e29b-41d4-a716-446655440055', 4, 2, 'msp_token_maestro_001', false, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(6, '550e8400-e29b-41d4-a716-446655440056', 5, 1, 'msp_token_mastercard_001', true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- ---------------------------------------------------------------------------------------------------------------------
-- 2.3 FINANCIAL TRANSACTIONS (Sample data)
-- ---------------------------------------------------------------------------------------------------------------------
-- Stripe transactions
INSERT INTO financial_transactions (id, external_id, payer_external_id, recipient_external_id, amount, currency_id, date_time, payment_method_id, payment_status_id, service_provider_id, stripe_payment_intent_id, stripe_charge_id, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, '550e8400-e29b-41d4-a716-446655440201', 'usr-ext-00007', 'company-ext-001', 25.00, 2, CURRENT_TIMESTAMP, 1, 2, 1, 'pi_test_001', 'ch_test_001', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(2, '550e8400-e29b-41d4-a716-446655440202', 'usr-ext-00008', 'company-ext-001', 35.50, 2, CURRENT_TIMESTAMP, 1, 2, 1, 'pi_test_002', 'ch_test_002', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(3, '550e8400-e29b-41d4-a716-446655440203', 'usr-ext-00009', 'company-ext-002', 15.75, 2, CURRENT_TIMESTAMP, 2, 2, 1, 'pi_test_003', 'ch_test_003', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(4, '550e8400-e29b-41d4-a716-446655440204', 'usr-ext-00007', 'company-ext-001', 50.00, 2, CURRENT_TIMESTAMP, 1, 1, 1, 'pi_test_004', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- MultiSafePay transactions
INSERT INTO financial_transactions (id, external_id, payer_external_id, recipient_external_id, amount, currency_id, date_time, payment_method_id, payment_status_id, service_provider_id, multi_safepay_order_id, multi_safepay_transaction_id, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(5, '550e8400-e29b-41d4-a716-446655440205', 'usr-ext-00010', 'company-ext-001', 45.00, 2, CURRENT_TIMESTAMP, 1, 2, 4, 'order_msp_test_001', '123456789', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(6, '550e8400-e29b-41d4-a716-446655440206', 'usr-ext-00011', 'company-ext-001', 60.25, 2, CURRENT_TIMESTAMP, 1, 2, 4, 'order_msp_test_002', '123456790', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(7, '550e8400-e29b-41d4-a716-446655440207', 'usr-ext-00012', 'company-ext-002', 28.50, 2, CURRENT_TIMESTAMP, 4, 2, 4, 'order_msp_test_003', '123456791', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(8, '550e8400-e29b-41d4-a716-446655440208', 'usr-ext-00010', 'company-ext-001', 75.00, 2, CURRENT_TIMESTAMP, 1, 1, 4, 'order_msp_test_004', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- ---------------------------------------------------------------------------------------------------------------------
-- 2.4 RENTAL FIN TRANSACTIONS (Sample data - references rentals from rental-service)
-- ---------------------------------------------------------------------------------------------------------------------
-- Stripe rental transactions
INSERT INTO rental_fin_transactions (id, external_id, rental_external_id, financial_transaction_id, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, '550e8400-e29b-41d4-a716-446655440301', 'rental-ext-00101', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(2, '550e8400-e29b-41d4-a716-446655440302', 'rental-ext-00102', 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(3, '550e8400-e29b-41d4-a716-446655440303', 'rental-ext-00103', 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- MultiSafePay rental transactions
INSERT INTO rental_fin_transactions (id, external_id, rental_external_id, financial_transaction_id, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(4, '550e8400-e29b-41d4-a716-446655440304', 'rental-ext-00104', 5, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(5, '550e8400-e29b-41d4-a716-446655440305', 'rental-ext-00105', 6, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(6, '550e8400-e29b-41d4-a716-446655440306', 'rental-ext-00106', 7, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- ---------------------------------------------------------------------------------------------------------------------
-- 2.5 B2B SALE FIN TRANSACTIONS (Sample data - references B2B sales from rental-service)
-- ---------------------------------------------------------------------------------------------------------------------
-- Stripe B2B transaction
INSERT INTO b2b_sale_fin_transactions (id, external_id, b2b_sale_external_id, financial_transaction_id, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, '550e8400-e29b-41d4-a716-446655440401', 'b2b-sale-ext-001', 4, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- MultiSafePay B2B transaction
INSERT INTO b2b_sale_fin_transactions (id, external_id, b2b_sale_external_id, financial_transaction_id, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(2, '550e8400-e29b-41d4-a716-446655440402', 'b2b-sale-ext-002', 8, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- ---------------------------------------------------------------------------------------------------------------------
-- 2.6 B2B SUBSCRIPTION FIN TRANSACTIONS (Sample data)
-- ---------------------------------------------------------------------------------------------------------------------
-- Stripe subscription transaction
INSERT INTO b2b_subscription_fin_transactions (id, external_id, b2b_subscription_external_id, financial_transaction_id, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, '550e8400-e29b-41d4-a716-446655440501', 'b2b-subscription-ext-001', 4, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- MultiSafePay subscription transaction
INSERT INTO b2b_subscription_fin_transactions (id, external_id, b2b_subscription_external_id, financial_transaction_id, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(2, '550e8400-e29b-41d4-a716-446655440502', 'b2b-subscription-ext-002', 8, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- ---------------------------------------------------------------------------------------------------------------------
-- 2.7 B2B REVENUE SHARE PAYOUTS (Sample data - references companies from auth-service)
-- ---------------------------------------------------------------------------------------------------------------------
INSERT INTO b2b_revenue_share_payouts (id, external_id, company_external_id, location_bank_account_id, multisafepay_payout_id, payment_status_id, due_date, payout_date, total_amount, paid_amount, remaining_amount, status, currency, failure_reason, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
-- Payout 1: Pending payout for Amsterdam location (company-ext-00001)
(1, '550e8400-e29b-41d4-a716-446655440601', 'company-ext-00001', 1, NULL, 1, CURRENT_DATE + INTERVAL '30 days', NULL, 500.00, 0.00, 500.00, 'PENDING', 'EUR', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
-- Payout 2: Completed payout for Rotterdam location (company-ext-00001)
(2, '550e8400-e29b-41d4-a716-446655440602', 'company-ext-00001', 2, 'payout_msp_test_123456', 2, CURRENT_DATE - INTERVAL '5 days', CURRENT_DATE - INTERVAL '2 days', 350.75, 350.75, 0.00, 'COMPLETED', 'EUR', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
-- Payout 3: Failed payout for Utrecht location (unverified bank account)
(3, '550e8400-e29b-41d4-a716-446655440603', 'company-ext-00002', 3, NULL, 3, CURRENT_DATE - INTERVAL '10 days', NULL, 225.00, 0.00, 225.00, 'FAILED', 'EUR', 'Bank account not verified', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- ---------------------------------------------------------------------------------------------------------------------
-- 2.8 B2B REVENUE SHARE PAYOUT ITEMS (Sample data)
-- ---------------------------------------------------------------------------------------------------------------------
INSERT INTO b2b_revenue_share_payout_items (id, external_id, b2b_revenue_share_payout_id, bike_rental_external_id, bike_rental_total_price, revenue_share_percent, amount, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
-- Items for Payout 1 (PENDING - Amsterdam)
(1, '550e8400-e29b-41d4-a716-446655440701', 1, 'bike-rental-ext-00101', 200.00, 75.00, 150.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(2, '550e8400-e29b-41d4-a716-446655440702', 1, 'bike-rental-ext-00102', 250.00, 80.00, 200.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(3, '550e8400-e29b-41d4-a716-446655440703', 1, 'bike-rental-ext-00103', 214.29, 70.00, 150.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
-- Items for Payout 2 (COMPLETED - Rotterdam)
(4, '550e8400-e29b-41d4-a716-446655440704', 2, 'bike-rental-ext-00104', 250.71, 70.00, 175.50, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(5, '550e8400-e29b-41d4-a716-446655440705', 2, 'bike-rental-ext-00105', 250.36, 70.00, 175.25, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
-- Items for Payout 3 (FAILED - Utrecht)
(6, '550e8400-e29b-41d4-a716-446655440706', 3, 'bike-rental-ext-00106', 321.43, 70.00, 225.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- ---------------------------------------------------------------------------------------------------------------------
-- 2.9 PAYOUT FIN TRANSACTIONS (Sample data - links payouts to financial transactions)
-- ---------------------------------------------------------------------------------------------------------------------
INSERT INTO payout_fin_transactions (id, external_id, b2b_revenue_share_payout_id, financial_transaction_id, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, '550e8400-e29b-41d4-a716-446655440121', 2, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', FALSE)
ON CONFLICT (id) DO NOTHING;

-- ---------------------------------------------------------------------------------------------------------------------
-- 2.7 LOCATION BANK ACCOUNTS (Sample data for payout testing)
-- ---------------------------------------------------------------------------------------------------------------------
-- Sample bank accounts for location payout testing
-- These reference locations from rental-service
-- IMPORTANT: Replace with real bank accounts before production use
INSERT INTO location_bank_accounts (id, external_id, company_external_id, location_external_id, account_holder_name, iban, bic, currency, is_verified, is_active, verification_notes, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
-- Bank accounts for locations with unpaid rentals (Dec 2025)
(1, '550e8400-e29b-41d4-a716-446655440201', 'company-ext-001', '550e8400-e29b-41d4-a716-446655440101', 'Downtown Bike Hub BV', 'NL91ABNA0417164300', 'ABNANL2A', 'EUR', true, true, 'Verified via bank statement - Test account', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(2, '550e8400-e29b-41d4-a716-446655440202', 'company-ext-001', '550e8400-e29b-41d4-a716-446655440102', 'Park Side Station Ltd', 'NL20INGB0001234567', 'INGBNL2A', 'EUR', true, true, 'Verified via bank statement - Test account', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
-- Additional test bank accounts
(3, '550e8400-e29b-41d4-a716-446655440203', 'company-ext-002', '550e8400-e29b-41d4-a716-446655440103', 'City Center Rentals BV', 'NL86RABO0123456789', 'RABONL2U', 'EUR', true, true, 'Verified via bank statement - Test account', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
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
SELECT setval('location_bank_accounts_id_seq', (SELECT COALESCE(MAX(id), 1) FROM location_bank_accounts));

-- =====================================================================================================================
-- END OF INITIALIZATION
-- =====================================================================================================================
-- Database initialization completed successfully!
-- 
-- Summary:
-- - Required lookup tables: payment_statuses (6), payment_methods (8), currencies (5), service_providers (4)
-- - Sample data: user_payment_profiles (6: 3 Stripe + 3 MultiSafePay)
-- - Sample data: user_payment_methods (6: 3 Stripe + 3 MultiSafePay)
-- - Sample data: financial_transactions (8: 4 Stripe + 4 MultiSafePay)
-- - Sample data: rental_fin_transactions (6: 3 Stripe + 3 MultiSafePay)
-- - Sample data: b2b_sale_fin_transactions (2: 1 Stripe + 1 MultiSafePay)
-- - Sample data: b2b_subscription_fin_transactions (2: 1 Stripe + 1 MultiSafePay)
-- - Sample data: b2b_revenue_share_payouts (3: 1 pending, 1 completed, 1 failed)
-- - Sample data: b2b_revenue_share_payout_items (6: 3 for payout 1, 2 for payout 2, 1 for payout 3)
-- - Sample data: payout_fin_transactions (1)
-- - Sample data: location_bank_accounts (4: 2 verified+active, 1 unverified, 1 inactive)
-- 
-- Cross-Service References:
-- - User External IDs: Referenced from auth-service (usr-ext-XXXXX)
-- - Company External IDs: Referenced from auth-service (company-ext-XXXXX)
-- - Location External IDs: Referenced from rental-service (location-ext-XXXXX)
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
ALTER TABLE location_bank_accounts ENABLE ROW LEVEL SECURITY;

-- Force RLS even for table owner (important for security)
ALTER TABLE b2b_revenue_share_payouts FORCE ROW LEVEL SECURITY;
ALTER TABLE financial_transactions FORCE ROW LEVEL SECURITY;
ALTER TABLE location_bank_accounts FORCE ROW LEVEL SECURITY;

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
-- 3.3 RLS POLICY: location_bank_accounts table
-- ---------------------------------------------------------------------------------------------------------------------
-- Allows access if:
-- 1. User is superadmin (bypasses all filters), OR
-- 2. Bank account belongs to one of user's companies
-- ---------------------------------------------------------------------------------------------------------------------
DROP POLICY IF EXISTS location_bank_accounts_tenant_isolation ON location_bank_accounts;
CREATE POLICY location_bank_accounts_tenant_isolation ON location_bank_accounts
    FOR ALL
    USING (
        -- Allow if user is superadmin
        COALESCE(current_setting('app.is_superadmin', true)::boolean, false) = true
        OR
        -- Allow if bank account belongs to one of user's companies
        company_external_id = ANY(
            string_to_array(
                COALESCE(current_setting('app.company_external_ids', true), ''),
                ','
            )
        )
    );

-- ---------------------------------------------------------------------------------------------------------------------
-- 3.4 PERFORMANCE INDEXES for RLS
-- ---------------------------------------------------------------------------------------------------------------------
-- These indexes ensure RLS policies don't slow down queries
-- ---------------------------------------------------------------------------------------------------------------------
CREATE INDEX IF NOT EXISTS idx_b2b_revenue_share_payouts_company_rls ON b2b_revenue_share_payouts(company_external_id) WHERE is_deleted = false;
CREATE INDEX IF NOT EXISTS idx_financial_transactions_company_rls ON financial_transactions(company_external_id) WHERE is_deleted = false;
CREATE INDEX IF NOT EXISTS idx_location_bank_accounts_company_rls ON location_bank_accounts(company_external_id) WHERE is_deleted = false;

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





