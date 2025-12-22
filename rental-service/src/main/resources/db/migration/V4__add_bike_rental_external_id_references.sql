-- Migration: Add cross-service externalId reference fields to bike_rental table
-- This enables storing externalIds from related entities for better resilience and cross-service queries

-- Add externalId reference columns to bike_rental
ALTER TABLE bike_rental ADD COLUMN bike_external_id VARCHAR(100);
ALTER TABLE bike_rental ADD COLUMN location_external_id VARCHAR(100);
ALTER TABLE bike_rental ADD COLUMN rental_external_id VARCHAR(100);

-- Create indexes for performance
CREATE INDEX idx_bike_rental_bike_external_id ON bike_rental(bike_external_id);
CREATE INDEX idx_bike_rental_location_external_id ON bike_rental(location_external_id);
CREATE INDEX idx_bike_rental_rental_external_id ON bike_rental(rental_external_id);

-- Backfill existing records with externalIds from related entities
-- This ensures existing bike rentals have the cross-service reference fields populated
UPDATE bike_rental br
SET bike_external_id = (SELECT p.external_id FROM product p WHERE p.id = br.bike_id)
WHERE br.bike_id IS NOT NULL AND br.bike_external_id IS NULL;

UPDATE bike_rental br
SET location_external_id = (SELECT l.external_id FROM location l WHERE l.id = br.location_id)
WHERE br.location_id IS NOT NULL AND br.location_external_id IS NULL;

UPDATE bike_rental br
SET rental_external_id = (SELECT r.external_id FROM rental r WHERE r.id = br.rental_id)
WHERE br.rental_id IS NOT NULL AND br.rental_external_id IS NULL;

