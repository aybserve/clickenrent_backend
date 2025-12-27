-- ========================================
-- Comprehensive Test Data for Payment Service
-- ========================================

-- ========================================
-- 1. CURRENCIES
-- ========================================
INSERT INTO currencies (id, external_id, code, name, created_at, updated_at, created_by, last_modified_by)
VALUES (1, '550e8400-e29b-41d4-a716-446655440001', 'USD', 'US Dollar', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system');

INSERT INTO currencies (id, external_id, code, name, created_at, updated_at, created_by, last_modified_by)
VALUES (2, '550e8400-e29b-41d4-a716-446655440002', 'EUR', 'Euro', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system');

INSERT INTO currencies (id, external_id, code, name, created_at, updated_at, created_by, last_modified_by)
VALUES (3, '550e8400-e29b-41d4-a716-446655440003', 'GBP', 'British Pound', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system');

INSERT INTO currencies (id, external_id, code, name, created_at, updated_at, created_by, last_modified_by)
VALUES (4, '550e8400-e29b-41d4-a716-446655440004', 'JPY', 'Japanese Yen', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system');

-- ========================================
-- 2. PAYMENT METHODS
-- ========================================
INSERT INTO payment_methods (id, external_id, code, name, is_active, created_at, updated_at, created_by, last_modified_by)
VALUES (1, '550e8400-e29b-41d4-a716-446655440011', 'CREDIT_CARD', 'Credit Card', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system');

INSERT INTO payment_methods (id, external_id, code, name, is_active, created_at, updated_at, created_by, last_modified_by)
VALUES (2, '550e8400-e29b-41d4-a716-446655440012', 'DEBIT_CARD', 'Debit Card', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system');

INSERT INTO payment_methods (id, external_id, code, name, is_active, created_at, updated_at, created_by, last_modified_by)
VALUES (3, '550e8400-e29b-41d4-a716-446655440013', 'BANK_TRANSFER', 'Bank Transfer', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system');

INSERT INTO payment_methods (id, external_id, code, name, is_active, created_at, updated_at, created_by, last_modified_by)
VALUES (4, '550e8400-e29b-41d4-a716-446655440014', 'PAYPAL', 'PayPal', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system');

INSERT INTO payment_methods (id, external_id, code, name, is_active, created_at, updated_at, created_by, last_modified_by)
VALUES (5, '550e8400-e29b-41d4-a716-446655440015', 'APPLE_PAY', 'Apple Pay', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system');

-- ========================================
-- 3. PAYMENT STATUSES
-- ========================================
INSERT INTO payment_statuses (id, external_id, code, name, created_at, updated_at, created_by, last_modified_by)
VALUES (1, '550e8400-e29b-41d4-a716-446655440021', 'PENDING', 'Payment Pending', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system');

INSERT INTO payment_statuses (id, external_id, code, name, created_at, updated_at, created_by, last_modified_by)
VALUES (2, '550e8400-e29b-41d4-a716-446655440022', 'SUCCEEDED', 'Payment Succeeded', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system');

INSERT INTO payment_statuses (id, external_id, code, name, created_at, updated_at, created_by, last_modified_by)
VALUES (3, '550e8400-e29b-41d4-a716-446655440023', 'FAILED', 'Payment Failed', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system');

INSERT INTO payment_statuses (id, external_id, code, name, created_at, updated_at, created_by, last_modified_by)
VALUES (4, '550e8400-e29b-41d4-a716-446655440024', 'CANCELED', 'Payment Canceled', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system');

INSERT INTO payment_statuses (id, external_id, code, name, created_at, updated_at, created_by, last_modified_by)
VALUES (5, '550e8400-e29b-41d4-a716-446655440025', 'REFUNDED', 'Payment Refunded', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system');

INSERT INTO payment_statuses (id, external_id, code, name, created_at, updated_at, created_by, last_modified_by)
VALUES (6, '550e8400-e29b-41d4-a716-446655440026', 'PARTIALLY_REFUNDED', 'Payment Partially Refunded', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system');

-- ========================================
-- 4. SERVICE PROVIDERS
-- ========================================
INSERT INTO service_providers (id, external_id, code, name, created_at, updated_at, created_by, last_modified_by)
VALUES (1, '550e8400-e29b-41d4-a716-446655440031', 'STRIPE', 'Stripe', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system');

INSERT INTO service_providers (id, external_id, code, name, created_at, updated_at, created_by, last_modified_by)
VALUES (2, '550e8400-e29b-41d4-a716-446655440032', 'PAYPAL', 'PayPal', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system');

INSERT INTO service_providers (id, external_id, code, name, created_at, updated_at, created_by, last_modified_by)
VALUES (3, '550e8400-e29b-41d4-a716-446655440033', 'SQUARE', 'Square', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system');

-- ========================================
-- 5. USER PAYMENT PROFILES
-- ========================================
INSERT INTO user_payment_profiles (id, external_id, user_id, stripe_customer_id, is_active, created_at, updated_at, created_by, last_modified_by)
VALUES (1, '550e8400-e29b-41d4-a716-446655440041', 1, 'cus_test_user1', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system');

INSERT INTO user_payment_profiles (id, external_id, user_id, stripe_customer_id, is_active, created_at, updated_at, created_by, last_modified_by)
VALUES (2, '550e8400-e29b-41d4-a716-446655440042', 2, 'cus_test_user2', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system');

INSERT INTO user_payment_profiles (id, external_id, user_id, stripe_customer_id, is_active, created_at, updated_at, created_by, last_modified_by)
VALUES (3, '550e8400-e29b-41d4-a716-446655440043', 3, 'cus_test_user3', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system');

-- ========================================
-- 6. USER PAYMENT METHODS
-- ========================================
INSERT INTO user_payment_methods (id, external_id, user_payment_profile_id, stripe_payment_method_id, is_default, is_active, created_at, updated_at, created_by, last_modified_by)
VALUES (1, '550e8400-e29b-41d4-a716-446655440051', 1, 'pm_test_card1', true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system');

INSERT INTO user_payment_methods (id, external_id, user_payment_profile_id, stripe_payment_method_id, is_default, is_active, created_at, updated_at, created_by, last_modified_by)
VALUES (2, '550e8400-e29b-41d4-a716-446655440052', 1, 'pm_test_card2', false, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system');

INSERT INTO user_payment_methods (id, external_id, user_payment_profile_id, stripe_payment_method_id, is_default, is_active, created_at, updated_at, created_by, last_modified_by)
VALUES (3, '550e8400-e29b-41d4-a716-446655440053', 2, 'pm_test_card3', true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system');

-- ========================================
-- 7. FINANCIAL TRANSACTIONS
-- ========================================
INSERT INTO financial_transactions (id, external_id, payer_id, recipient_id, amount, currency_id, date_time, payment_method_id, payment_status_id, service_provider_id, stripe_payment_intent_id, stripe_charge_id, created_at, updated_at, created_by, last_modified_by)
VALUES (1, '550e8400-e29b-41d4-a716-446655440061', 1, 2, 100.00, 1, CURRENT_TIMESTAMP, 1, 2, 1, 'pi_test_intent1', 'ch_test_charge1', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system');

INSERT INTO financial_transactions (id, external_id, payer_id, recipient_id, amount, currency_id, date_time, payment_method_id, payment_status_id, service_provider_id, stripe_payment_intent_id, stripe_charge_id, created_at, updated_at, created_by, last_modified_by)
VALUES (2, '550e8400-e29b-41d4-a716-446655440062', 2, 1, 50.00, 2, CURRENT_TIMESTAMP, 1, 2, 1, 'pi_test_intent2', 'ch_test_charge2', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system');

INSERT INTO financial_transactions (id, external_id, payer_id, recipient_id, amount, currency_id, date_time, payment_method_id, payment_status_id, service_provider_id, created_at, updated_at, created_by, last_modified_by)
VALUES (3, '550e8400-e29b-41d4-a716-446655440063', 3, 1, 25.00, 1, CURRENT_TIMESTAMP, 2, 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system');

-- ========================================
-- 8. RENTAL FIN TRANSACTIONS
-- ========================================
INSERT INTO rental_fin_transactions (id, external_id, rental_id, financial_transaction_id, created_at, updated_at, created_by, last_modified_by)
VALUES (1, '550e8400-e29b-41d4-a716-446655440071', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system');

INSERT INTO rental_fin_transactions (id, external_id, rental_id, financial_transaction_id, created_at, updated_at, created_by, last_modified_by)
VALUES (2, '550e8400-e29b-41d4-a716-446655440072', 2, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system');

-- ========================================
-- 9. B2B SALE FIN TRANSACTIONS
-- ========================================
INSERT INTO b2b_sale_fin_transactions (id, external_id, b2b_sale_id, financial_transaction_id, created_at, updated_at, created_by, last_modified_by)
VALUES (1, '550e8400-e29b-41d4-a716-446655440081', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system');

-- ========================================
-- 10. B2B SUBSCRIPTION FIN TRANSACTIONS
-- ========================================
INSERT INTO b2b_subscription_fin_transactions (id, external_id, b2b_subscription_id, financial_transaction_id, created_at, updated_at, created_by, last_modified_by)
VALUES (1, '550e8400-e29b-41d4-a716-446655440091', 1, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system');

-- ========================================
-- 11. B2B REVENUE SHARE PAYOUTS
-- ========================================
INSERT INTO b2b_revenue_share_payouts (id, external_id, company_id, payment_status_id, due_date, total_amount, paid_amount, remaining_amount, created_at, updated_at, created_by, last_modified_by)
VALUES (1, '550e8400-e29b-41d4-a716-446655440101', 1, 1, CURRENT_DATE + INTERVAL '30' DAY, 1000.00, 0.00, 1000.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system');

INSERT INTO b2b_revenue_share_payouts (id, external_id, company_id, payment_status_id, due_date, total_amount, paid_amount, remaining_amount, created_at, updated_at, created_by, last_modified_by)
VALUES (2, '550e8400-e29b-41d4-a716-446655440102', 2, 2, CURRENT_DATE + INTERVAL '15' DAY, 2500.00, 2500.00, 0.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system');

INSERT INTO b2b_revenue_share_payouts (id, external_id, company_id, payment_status_id, due_date, total_amount, paid_amount, remaining_amount, created_at, updated_at, created_by, last_modified_by)
VALUES (3, '550e8400-e29b-41d4-a716-446655440103', 3, 6, CURRENT_DATE - INTERVAL '10' DAY, 750.00, 500.00, 250.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system');

-- ========================================
-- 12. B2B REVENUE SHARE PAYOUT ITEMS
-- ========================================
INSERT INTO b2b_revenue_share_payout_items (id, external_id, b2b_revenue_share_payout_id, bike_rental_id, amount, created_at, updated_at, created_by, last_modified_by)
VALUES (1, '550e8400-e29b-41d4-a716-446655440111', 1, 1, 50.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system');

INSERT INTO b2b_revenue_share_payout_items (id, external_id, b2b_revenue_share_payout_id, bike_rental_id, amount, created_at, updated_at, created_by, last_modified_by)
VALUES (2, '550e8400-e29b-41d4-a716-446655440112', 1, 2, 75.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system');

INSERT INTO b2b_revenue_share_payout_items (id, external_id, b2b_revenue_share_payout_id, bike_rental_id, amount, created_at, updated_at, created_by, last_modified_by)
VALUES (3, '550e8400-e29b-41d4-a716-446655440113', 2, 1, 125.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system');

-- ========================================
-- 13. PAYOUT FIN TRANSACTIONS
-- ========================================
INSERT INTO payout_fin_transactions (id, external_id, b2b_revenue_share_payout_id, financial_transaction_id, created_at, updated_at, created_by, last_modified_by)
VALUES (1, '550e8400-e29b-41d4-a716-446655440121', 2, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system');







