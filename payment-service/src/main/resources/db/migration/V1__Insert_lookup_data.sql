-- =====================================================================================================================
-- PAYMENT SERVICE - LOOKUP DATA (Flyway Migration V1)
-- =====================================================================================================================
-- Module: payment-service
-- Database: PostgreSQL
-- Description: Insert required lookup data for payment statuses, methods, currencies, and providers.
--              All inserts use ON CONFLICT to ensure idempotency.
-- 
-- Author: Vitaliy Shvetsov
-- =====================================================================================================================

-- Payment Status
INSERT INTO payment_statuses (id, external_id, code, name, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, '550e8400-e29b-41d4-a716-446655440001', 'PENDING', 'Pending', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(2, '550e8400-e29b-41d4-a716-446655440002', 'SUCCEEDED', 'Succeeded', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(3, '550e8400-e29b-41d4-a716-446655440003', 'FAILED', 'Failed', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(4, '550e8400-e29b-41d4-a716-446655440004', 'CANCELED', 'Canceled', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(5, '550e8400-e29b-41d4-a716-446655440005', 'REFUNDED', 'Refunded', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(6, '550e8400-e29b-41d4-a716-446655440006', 'PARTIALLY_REFUNDED', 'Partially Refunded', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (code) DO NOTHING;

-- Payment Methods (full MultiSafePay catalog)
INSERT INTO payment_methods (external_id, code, name, is_active, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
('550e8400-e29b-41d4-a716-446655440011', 'CREDIT_CARD', 'Credit Card', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
('550e8400-e29b-41d4-a716-446655440012', 'DEBIT_CARD', 'Debit Card', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
('550e8400-e29b-41d4-a716-446655440013', 'BANK_TRANSFER', 'Bank Transfer', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
('550e8400-e29b-41d4-a716-446655440014', 'DIGITAL_WALLET', 'Digital Wallet', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
('550e8400-e29b-41d4-a716-446655440015', 'CASH', 'Cash', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
('550e8400-e29b-41d4-a716-446655440016', 'IDEAL', 'iDEAL', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
('550e8400-e29b-41d4-a716-446655440017', 'IDEALQR', 'iDEAL QR', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
('550e8400-e29b-41d4-a716-446655440018', 'BANCONTACT', 'Bancontact', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
('550e8400-e29b-41d4-a716-446655440019', 'BANCONTACTQR', 'Bancontact QR', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
('550e8400-e29b-41d4-a716-446655440020', 'BELFIUS', 'Belfius', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
('550e8400-e29b-41d4-a716-446655440021', 'BIZUM', 'Bizum', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
('550e8400-e29b-41d4-a716-446655440022', 'CBC', 'CBC', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
('550e8400-e29b-41d4-a716-446655440023', 'KBC', 'KBC', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
('550e8400-e29b-41d4-a716-446655440024', 'DIRDEB', 'Direct Debit (SEPA)', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
('550e8400-e29b-41d4-a716-446655440025', 'DIRECTBANK', 'Direct Bank Transfer', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
('550e8400-e29b-41d4-a716-446655440026', 'DOTPAY', 'Dotpay', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
('550e8400-e29b-41d4-a716-446655440027', 'EPS', 'EPS', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
('550e8400-e29b-41d4-a716-446655440028', 'GIROPAY', 'Giropay', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
('550e8400-e29b-41d4-a716-446655440029', 'MBWAY', 'MB WAY', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
('550e8400-e29b-41d4-a716-446655440030', 'MULTIBANCO', 'Multibanco', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
('550e8400-e29b-41d4-a716-446655440031', 'MYBANK', 'MyBank', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
('550e8400-e29b-41d4-a716-446655440032', 'SOFORT', 'Sofort', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
('550e8400-e29b-41d4-a716-446655440033', 'TRUSTLY', 'Trustly', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
('550e8400-e29b-41d4-a716-446655440034', 'VISA', 'Visa', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
('550e8400-e29b-41d4-a716-446655440035', 'MASTERCARD', 'Mastercard', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
('550e8400-e29b-41d4-a716-446655440036', 'MAESTRO', 'Maestro', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
('550e8400-e29b-41d4-a716-446655440037', 'AMEX', 'American Express', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
('550e8400-e29b-41d4-a716-446655440038', 'DANKORT', 'Dankort', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
('550e8400-e29b-41d4-a716-446655440039', 'CARTEBANCAIRE', 'Cartes Bancaires', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
('550e8400-e29b-41d4-a716-446655440040', 'POSTEPAY', 'Postepay', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
('550e8400-e29b-41d4-a716-446655440041', 'BILLINK', 'Billink', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
('550e8400-e29b-41d4-a716-446655440042', 'EINVOICE', 'E-Invoicing', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
('550e8400-e29b-41d4-a716-446655440043', 'IN3', 'iDEAL in3', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
('550e8400-e29b-41d4-a716-446655440044', 'KLARNA', 'Klarna', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
('550e8400-e29b-41d4-a716-446655440045', 'PAYAFTER', 'Pay After Delivery', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
('550e8400-e29b-41d4-a716-446655440046', 'AFTERPAY', 'Riverty (AfterPay)', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
('550e8400-e29b-41d4-a716-446655440047', 'EDENRED', 'Edenred', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
('550e8400-e29b-41d4-a716-446655440048', 'BEAUTYANDWELLNESS', 'Beauty & Wellness Gift Card', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
('550e8400-e29b-41d4-a716-446655440049', 'BOEKENBON', 'Boekenbon', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
('550e8400-e29b-41d4-a716-446655440050', 'FASHIONCHEQUE', 'Fashioncheque', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
('550e8400-e29b-41d4-a716-446655440051', 'FASHIONGIFTCARD', 'Fashion Gift Card', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
('550e8400-e29b-41d4-a716-446655440052', 'VVVGIFTCARD', 'VVV Cadeaukaart', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
('550e8400-e29b-41d4-a716-446655440053', 'WEBSHOPGIFTCARD', 'Webshop Giftcard', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
('550e8400-e29b-41d4-a716-446655440054', 'MONIZZE', 'Monizze', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
('550e8400-e29b-41d4-a716-446655440055', 'PAYSAFECARD', 'Paysafecard', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
('550e8400-e29b-41d4-a716-446655440056', 'SODEXO', 'Sodexo', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
('550e8400-e29b-41d4-a716-446655440057', 'ALIPAY', 'Alipay', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
('550e8400-e29b-41d4-a716-446655440058', 'ALIPAYPLUS', 'Alipay+', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
('550e8400-e29b-41d4-a716-446655440059', 'AMAZONPAY', 'Amazon Pay', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
('550e8400-e29b-41d4-a716-446655440060', 'APPLEPAY', 'Apple Pay', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
('550e8400-e29b-41d4-a716-446655440061', 'GOOGLEPAY', 'Google Pay', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
('550e8400-e29b-41d4-a716-446655440062', 'PAYPAL', 'PayPal', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
('550e8400-e29b-41d4-a716-446655440063', 'WECHAT', 'WeChat Pay', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (code) DO UPDATE SET
    name = EXCLUDED.name,
    external_id = EXCLUDED.external_id,
    is_active = EXCLUDED.is_active,
    last_date_modified = CURRENT_TIMESTAMP,
    last_modified_by = 'system';

-- Currency
INSERT INTO currencies (id, external_id, code, name, symbol, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, '550e8400-e29b-41d4-a716-446655440021', 'USD', 'US Dollar', '$', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(2, '550e8400-e29b-41d4-a716-446655440022', 'EUR', 'Euro', '€', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(3, '550e8400-e29b-41d4-a716-446655440023', 'GBP', 'British Pound', '£', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(4, '550e8400-e29b-41d4-a716-446655440024', 'UAH', 'Ukrainian Hryvnia', '₴', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(5, '550e8400-e29b-41d4-a716-446655440025', 'PLN', 'Polish Zloty', 'zł', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (code) DO NOTHING;

-- Service Provider
INSERT INTO service_providers (id, external_id, code, name, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, '550e8400-e29b-41d4-a716-446655440031', 'STRIPE', 'Stripe', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(2, '550e8400-e29b-41d4-a716-446655440032', 'PAYPAL', 'PayPal', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(3, '550e8400-e29b-41d4-a716-446655440033', 'INTERNAL', 'Internal Processing', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(4, '550e8400-e29b-41d4-a716-446655440034', 'MULTISAFEPAY', 'MultiSafePay', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (code) DO NOTHING;

-- =====================================================================================================================
-- END OF LOOKUP DATA
-- =====================================================================================================================
