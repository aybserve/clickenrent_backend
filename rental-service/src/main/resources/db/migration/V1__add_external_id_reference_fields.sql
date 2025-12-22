-- Migration to add externalId reference fields for cross-service communication
-- This is Phase 1: Add new columns (non-breaking change)

-- Rental table: Add userExternalId and companyExternalId
ALTER TABLE rental ADD COLUMN user_external_id VARCHAR(100);
ALTER TABLE rental ADD COLUMN company_external_id VARCHAR(100);
CREATE INDEX idx_rental_user_external_id ON rental(user_external_id);
CREATE INDEX idx_rental_company_external_id ON rental(company_external_id);

-- Location table: Add companyExternalId
ALTER TABLE location ADD COLUMN company_external_id VARCHAR(100);
CREATE INDEX idx_location_company_external_id ON location(company_external_id);

-- BikeReservation table: Add userExternalId
ALTER TABLE bike_reservation ADD COLUMN user_external_id VARCHAR(100);
CREATE INDEX idx_bike_reservation_user_external_id ON bike_reservation(user_external_id);

-- UserLocation table: Add userExternalId
ALTER TABLE user_location ADD COLUMN user_external_id VARCHAR(100);
CREATE INDEX idx_user_location_user_external_id ON user_location(user_external_id);

-- BikeBrand table: Add companyExternalId
ALTER TABLE bike_brand ADD COLUMN company_external_id VARCHAR(100);
CREATE INDEX idx_bike_brand_company_external_id ON bike_brand(company_external_id);

-- PartBrand table: Add companyExternalId
ALTER TABLE part_brand ADD COLUMN company_external_id VARCHAR(100);
CREATE INDEX idx_part_brand_company_external_id ON part_brand(company_external_id);

-- ChargingStationBrand table: Add companyExternalId
ALTER TABLE charging_station_brand ADD COLUMN company_external_id VARCHAR(100);
CREATE INDEX idx_charging_station_brand_company_external_id ON charging_station_brand(company_external_id);


