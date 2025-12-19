-- Migration to remove legacy userId column from bike_rental_feedback
-- All code now uses userExternalId for cross-service references

-- Drop the index first
DROP INDEX IF EXISTS idx_bike_rental_feedback_user_id;

-- Drop the column
ALTER TABLE bike_rental_feedback DROP COLUMN IF EXISTS user_id;
