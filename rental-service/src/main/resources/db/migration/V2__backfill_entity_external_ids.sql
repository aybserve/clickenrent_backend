-- Migration to backfill missing externalIds for existing entities
-- This ensures all records have a unique externalId

-- Backfill Product table (covers Bike, ChargingStation, Part, ServiceProduct)
UPDATE product 
SET external_id = CONCAT('PROD-', UUID()) 
WHERE external_id IS NULL OR external_id = '';

-- Backfill Rental table
UPDATE rental 
SET external_id = CONCAT('RENT-', UUID()) 
WHERE external_id IS NULL OR external_id = '';

-- Backfill BikeRental table
UPDATE bike_rental 
SET external_id = CONCAT('BR-', UUID()) 
WHERE external_id IS NULL OR external_id = '';

-- Backfill Location table
UPDATE location 
SET external_id = CONCAT('LOC-', UUID()) 
WHERE external_id IS NULL OR external_id = '';

-- Backfill Ride table
UPDATE ride 
SET external_id = CONCAT('RIDE-', UUID()) 
WHERE external_id IS NULL OR external_id = '';

-- Backfill BikeReservation table
UPDATE bike_reservation 
SET external_id = CONCAT('RES-', UUID()) 
WHERE external_id IS NULL OR external_id = '';

-- Backfill B2BSale table
UPDATE b2b_sale 
SET external_id = CONCAT('B2BS-', UUID()) 
WHERE external_id IS NULL OR external_id = '';

-- Backfill B2BSubscription table
UPDATE b2b_subscription 
SET external_id = CONCAT('B2BSUB-', UUID()) 
WHERE external_id IS NULL OR external_id = '';


