-- Migration to add externalId reference fields for cross-service communication
-- This is Phase 1: Add new columns (non-breaking change)

-- SupportRequest table: Add userExternalId and bikeExternalId
ALTER TABLE support_request ADD COLUMN user_external_id VARCHAR(100);
ALTER TABLE support_request ADD COLUMN bike_external_id VARCHAR(100);
CREATE INDEX idx_support_request_user_external_id ON support_request(user_external_id);
CREATE INDEX idx_support_request_bike_external_id ON support_request(bike_external_id);

-- Feedback table: Add userExternalId
ALTER TABLE feedback ADD COLUMN user_external_id VARCHAR(100);
CREATE INDEX idx_feedback_user_external_id ON feedback(user_external_id);

-- BikeRentalFeedback table: Add userExternalId and bikeRentalExternalId
ALTER TABLE bike_rental_feedback ADD COLUMN user_external_id VARCHAR(100);
ALTER TABLE bike_rental_feedback ADD COLUMN bike_rental_external_id VARCHAR(100);
-- Add external_id column if it doesn't exist (BikeRentalFeedback was missing it)
ALTER TABLE bike_rental_feedback ADD COLUMN IF NOT EXISTS external_id VARCHAR(100) UNIQUE;
CREATE INDEX idx_bike_rental_feedback_user_external_id ON bike_rental_feedback(user_external_id);
CREATE INDEX idx_bike_rental_feedback_bike_rental_ext_id ON bike_rental_feedback(bike_rental_external_id);
CREATE INDEX IF NOT EXISTS idx_bike_rental_feedback_external_id ON bike_rental_feedback(external_id);
