-- Test data for tenant isolation testing (H2 and PostgreSQL compatible)
-- Creates sample rentals for two companies: Nike and Adidas

-- Clear existing data (order: child tables first due to FK)
TRUNCATE TABLE bike_rental;
TRUNCATE TABLE rental;
TRUNCATE TABLE rental_status;

-- Insert rental statuses (schema matches RentalStatus + BaseAuditEntity)
INSERT INTO rental_status (id, external_id, name, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, '550e8400-e29b-41d4-a716-446655440061', 'Pending', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(2, '550e8400-e29b-41d4-a716-446655440062', 'Active', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(3, '550e8400-e29b-41d4-a716-446655440063', 'Completed', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false);

-- Create test rentals for Nike company (schema matches Rental + BaseAuditEntity)
INSERT INTO rental (id, external_id, user_external_id, company_external_id, rental_status_id, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, 'rental-nike-1', 'user-1', 'nike-uuid', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(2, 'rental-nike-2', 'user-2', 'nike-uuid', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(3, 'rental-nike-3', 'user-3', 'nike-uuid', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false);

-- Create test rentals for Adidas company
INSERT INTO rental (id, external_id, user_external_id, company_external_id, rental_status_id, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(100, 'rental-adidas-1', 'user-4', 'adidas-uuid', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(101, 'rental-adidas-2', 'user-5', 'adidas-uuid', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false);
