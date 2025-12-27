-- Migration script to update Ride table coordinates structure
-- Replaces single coordinates_id with start_coordinates_id and end_coordinates_id

-- Add new columns for start and end coordinates
ALTER TABLE ride ADD COLUMN start_coordinates_id BIGINT;
ALTER TABLE ride ADD COLUMN end_coordinates_id BIGINT;

-- Add foreign key constraints
ALTER TABLE ride ADD CONSTRAINT fk_ride_start_coordinates 
    FOREIGN KEY (start_coordinates_id) REFERENCES coordinates(id);

ALTER TABLE ride ADD CONSTRAINT fk_ride_end_coordinates 
    FOREIGN KEY (end_coordinates_id) REFERENCES coordinates(id);

-- Optionally migrate existing data (if coordinates_id was being used)
-- This copies the old coordinates_id to start_coordinates_id
-- Uncomment the next line if you want to preserve existing coordinate data
-- UPDATE ride SET start_coordinates_id = coordinates_id WHERE coordinates_id IS NOT NULL;

-- Drop the old coordinates_id column and its constraint
ALTER TABLE ride DROP CONSTRAINT IF EXISTS fk_ride_coordinates;
ALTER TABLE ride DROP COLUMN coordinates_id;

-- Add indexes for better query performance
CREATE INDEX idx_ride_start_coordinates ON ride(start_coordinates_id);
CREATE INDEX idx_ride_end_coordinates ON ride(end_coordinates_id);


