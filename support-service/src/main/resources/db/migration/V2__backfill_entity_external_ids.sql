-- Migration to backfill missing externalIds for existing entities
-- This ensures all records have a unique externalId

-- Backfill SupportRequest table
UPDATE support_request 
SET external_id = CONCAT('SR-', UUID()) 
WHERE external_id IS NULL OR external_id = '';

-- Backfill Feedback table
UPDATE feedback 
SET external_id = CONCAT('FB-', UUID()) 
WHERE external_id IS NULL OR external_id = '';

-- Backfill BikeRentalFeedback table
UPDATE bike_rental_feedback 
SET external_id = CONCAT('BRF-', UUID()) 
WHERE external_id IS NULL OR external_id = '';
