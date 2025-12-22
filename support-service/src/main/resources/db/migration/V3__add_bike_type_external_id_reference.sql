-- Migration: Add bikeTypeExternalId reference to bike_type_bike_issue table
-- This enables cross-service references to rental-service's BikeType entity

-- Add bikeTypeExternalId column to bike_type_bike_issue
ALTER TABLE bike_type_bike_issue ADD COLUMN bike_type_external_id VARCHAR(100);

-- Create index for performance
CREATE INDEX idx_bike_type_bike_issue_bike_type_ext_id ON bike_type_bike_issue(bike_type_external_id);

-- Note: Backfill will need to be done separately once BikeType external IDs are established
-- in rental-service and communicated to support-service


