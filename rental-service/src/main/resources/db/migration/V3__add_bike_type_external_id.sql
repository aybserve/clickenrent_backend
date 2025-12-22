-- Migration: Add externalId to bike_type table
-- This enables cross-service references using external identifiers

-- Add externalId column to bike_type
ALTER TABLE bike_type ADD COLUMN external_id VARCHAR(100) UNIQUE;

-- Create index for performance
CREATE INDEX idx_bike_type_external_id ON bike_type(external_id);

-- Backfill existing records with UUID
-- Note: Using gen_random_uuid() for PostgreSQL
UPDATE bike_type SET external_id = gen_random_uuid()::text WHERE external_id IS NULL;

-- Make it non-nullable after backfill
ALTER TABLE bike_type ALTER COLUMN external_id SET NOT NULL;


