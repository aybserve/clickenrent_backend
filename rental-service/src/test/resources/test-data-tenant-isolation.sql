-- Test data for tenant isolation testing
-- Creates sample rentals for two companies: Nike and Adidas

-- Clear existing data
TRUNCATE TABLE bike_rental CASCADE;
TRUNCATE TABLE rental CASCADE;
TRUNCATE TABLE rental_status CASCADE;

-- Insert rental statuses (required lookup data)
INSERT INTO rental_status (id, name, description) VALUES
(1, 'PENDING', 'Rental is pending'),
(2, 'ACTIVE', 'Rental is active'),
(3, 'COMPLETED', 'Rental is completed')
ON CONFLICT (id) DO NOTHING;

-- Create test rentals for Nike company
INSERT INTO rental (id, external_id, user_external_id, company_external_id, rental_status_id, date_created, last_date_modified, is_deleted) VALUES
(1, 'rental-nike-1', 'user-1', 'nike-uuid', 1, NOW(), NOW(), false),
(2, 'rental-nike-2', 'user-2', 'nike-uuid', 1, NOW(), NOW(), false),
(3, 'rental-nike-3', 'user-3', 'nike-uuid', 1, NOW(), NOW(), false);

-- Create test rentals for Adidas company
INSERT INTO rental (id, external_id, user_external_id, company_external_id, rental_status_id, date_created, last_date_modified, is_deleted) VALUES
(100, 'rental-adidas-1', 'user-4', 'adidas-uuid', 1, NOW(), NOW(), false),
(101, 'rental-adidas-2', 'user-5', 'adidas-uuid', 1, NOW(), NOW(), false);

-- Reset sequence for rental table
SELECT setval('rental_id_seq', 200, false);
