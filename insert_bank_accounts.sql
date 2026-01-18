-- Quick fix: Insert bank accounts for locations with unpaid rentals
-- Run this against clickenrent-payment database

-- Clear old test data first
DELETE FROM location_bank_accounts WHERE id IN (1, 2, 3, 4);

-- Insert bank accounts for locations with unpaid rentals
INSERT INTO location_bank_accounts (id, external_id, company_external_id, location_external_id, account_holder_name, iban, bic, currency, is_verified, is_active, verification_notes, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
-- Bank accounts for locations with unpaid rentals (Dec 2025)
(1, '550e8400-e29b-41d4-a716-446655440201', 'company-ext-001', '550e8400-e29b-41d4-a716-446655440101', 'Downtown Bike Hub BV', 'NL91ABNA0417164300', 'ABNANL2A', 'EUR', true, true, 'Verified via bank statement - Test account', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(2, '550e8400-e29b-41d4-a716-446655440202', 'company-ext-001', '550e8400-e29b-41d4-a716-446655440102', 'Park Side Station Ltd', 'NL20INGB0001234567', 'INGBNL2A', 'EUR', true, true, 'Verified via bank statement - Test account', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
-- Additional test bank account
(3, '550e8400-e29b-41d4-a716-446655440203', 'company-ext-002', '550e8400-e29b-41d4-a716-446655440103', 'City Center Rentals BV', 'NL86RABO0123456789', 'RABONL2U', 'EUR', true, true, 'Verified via bank statement - Test account', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO UPDATE SET
    company_external_id = EXCLUDED.company_external_id,
    location_external_id = EXCLUDED.location_external_id,
    account_holder_name = EXCLUDED.account_holder_name,
    is_verified = EXCLUDED.is_verified,
    is_active = EXCLUDED.is_active;

-- Reset sequence
SELECT setval('location_bank_accounts_id_seq', (SELECT COALESCE(MAX(id), 1) FROM location_bank_accounts));

-- Verify the setup
SELECT id, external_id, company_external_id, location_external_id, account_holder_name, iban, is_verified, is_active 
FROM location_bank_accounts 
WHERE location_external_id IN ('550e8400-e29b-41d4-a716-446655440101', '550e8400-e29b-41d4-a716-446655440102')
ORDER BY id;
