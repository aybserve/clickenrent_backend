-- =====================================================================================================================
-- PAYMENT SERVICE - SEQUENCE RESET (Flyway Testdata V100)
-- =====================================================================================================================
-- Module: payment-service
-- Database: PostgreSQL
-- Description: Reset sequences to correct values after inserting data.
-- 
-- Author: Vitaliy Shvetsov
-- =====================================================================================================================

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
SELECT setval('refund_statuses_id_seq', (SELECT COALESCE(MAX(id), 1) FROM refund_statuses));
SELECT setval('refund_reasons_id_seq', (SELECT COALESCE(MAX(id), 1) FROM refund_reasons));
SELECT setval('refunds_id_seq', (SELECT COALESCE(MAX(id), 1) FROM refunds));

-- =====================================================================================================================
-- END OF SEQUENCE RESET
-- =====================================================================================================================
