-- Migration to add externalId reference fields for cross-service communication
-- This is Phase 1: Add new columns (non-breaking change)

-- FinancialTransaction table: Add payerExternalId and recipientExternalId
ALTER TABLE financial_transactions ADD COLUMN payer_external_id VARCHAR(100);
ALTER TABLE financial_transactions ADD COLUMN recipient_external_id VARCHAR(100);
CREATE INDEX idx_financial_transactions_payer_ext_id ON financial_transactions(payer_external_id);
CREATE INDEX idx_financial_transactions_recipient_ext_id ON financial_transactions(recipient_external_id);

-- UserPaymentProfile table: Add userExternalId
ALTER TABLE user_payment_profiles ADD COLUMN user_external_id VARCHAR(100);
CREATE INDEX idx_user_payment_profiles_user_ext_id ON user_payment_profiles(user_external_id);

-- RentalFinTransaction table: Add rentalExternalId and bikeRentalExternalId
ALTER TABLE rental_fin_transactions ADD COLUMN rental_external_id VARCHAR(100);
ALTER TABLE rental_fin_transactions ADD COLUMN bike_rental_external_id VARCHAR(100);
CREATE INDEX idx_rental_fin_trans_rental_ext_id ON rental_fin_transactions(rental_external_id);
CREATE INDEX idx_rental_fin_trans_bike_rental_ext_id ON rental_fin_transactions(bike_rental_external_id);

-- B2BRevenueSharePayout table: Add companyExternalId
ALTER TABLE b2b_revenue_share_payouts ADD COLUMN company_external_id VARCHAR(100);
CREATE INDEX idx_b2b_revenue_share_payouts_company_ext_id ON b2b_revenue_share_payouts(company_external_id);


