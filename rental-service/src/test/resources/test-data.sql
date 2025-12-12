-- Test data for Rental Service

-- Insert Bike Types
INSERT INTO bike_type (id, name) VALUES (1, 'Electric bike');
INSERT INTO bike_type (id, name) VALUES (2, 'Non-electric bike');

-- Insert Bike Statuses
INSERT INTO bike_status (id, name) VALUES (1, 'Available');
INSERT INTO bike_status (id, name) VALUES (2, 'Broken');
INSERT INTO bike_status (id, name) VALUES (3, 'Disabled');
INSERT INTO bike_status (id, name) VALUES (4, 'In use');
INSERT INTO bike_status (id, name) VALUES (5, 'Paused');
INSERT INTO bike_status (id, name) VALUES (6, 'Reserved');

-- Insert Battery Charge Statuses
INSERT INTO battery_charge_status (id, name) VALUES (1, 'Idle');
INSERT INTO battery_charge_status (id, name) VALUES (2, 'Identifying');
INSERT INTO battery_charge_status (id, name) VALUES (3, 'Pre charging');
INSERT INTO battery_charge_status (id, name) VALUES (4, 'Charging');
INSERT INTO battery_charge_status (id, name) VALUES (5, 'Fully charged');
INSERT INTO battery_charge_status (id, name) VALUES (6, 'Error');
INSERT INTO battery_charge_status (id, name) VALUES (7, 'Idle on ride');

-- Insert Rental Statuses
INSERT INTO rental_status (id, name) VALUES (1, 'Pending');
INSERT INTO rental_status (id, name) VALUES (2, 'Active');
INSERT INTO rental_status (id, name) VALUES (3, 'Completed');
INSERT INTO rental_status (id, name) VALUES (4, 'Cancelled');

-- Insert Rental Units
INSERT INTO rental_unit (id, name) VALUES (1, 'Day');
INSERT INTO rental_unit (id, name) VALUES (2, 'Hour');
INSERT INTO rental_unit (id, name) VALUES (3, 'Week');

-- Insert Ride Statuses
INSERT INTO ride_status (id, name) VALUES (1, 'Active');
INSERT INTO ride_status (id, name) VALUES (2, 'Finished');

-- Insert Location Roles
INSERT INTO location_role (id, name) VALUES (1, 'Admin');
INSERT INTO location_role (id, name) VALUES (2, 'Manager');
INSERT INTO location_role (id, name) VALUES (3, 'Staff');

-- Insert B2B Sale Statuses
INSERT INTO b2b_sale_status (id, name) VALUES (1, 'Request');
INSERT INTO b2b_sale_status (id, name) VALUES (2, 'Ordered');
INSERT INTO b2b_sale_status (id, name) VALUES (3, 'Pending');
INSERT INTO b2b_sale_status (id, name) VALUES (4, 'Payment');
INSERT INTO b2b_sale_status (id, name) VALUES (5, 'Delivered');

-- Insert B2B Subscription Statuses
INSERT INTO b2b_subscription_status (id, name) VALUES (1, 'Pending');
INSERT INTO b2b_subscription_status (id, name) VALUES (2, 'Active');
INSERT INTO b2b_subscription_status (id, name) VALUES (3, 'Cancelled');

-- Insert Charging Station Statuses
INSERT INTO charging_station_status (id, name) VALUES (1, 'Idle');
INSERT INTO charging_station_status (id, name) VALUES (2, 'Pre charging');
INSERT INTO charging_station_status (id, name) VALUES (3, 'Charging');
INSERT INTO charging_station_status (id, name) VALUES (4, 'Fully charged');
INSERT INTO charging_station_status (id, name) VALUES (5, 'Error');

-- Insert Part Types
INSERT INTO part_type (id, name) VALUES (1, 'With serial numbers');
INSERT INTO part_type (id, name) VALUES (2, 'Without serial numbers');

-- Insert Coordinates
INSERT INTO coordinates (id, latitude, longitude) VALUES (1, 52.370216, 4.895168); -- Amsterdam
INSERT INTO coordinates (id, latitude, longitude) VALUES (2, 51.507351, -0.127758); -- London

-- Insert Test Location
INSERT INTO location (id, external_id, name, address, company_id, is_public, date_created, last_date_modified, created_by, last_modified_by)
VALUES (1, 'LOC001', 'Test Location Amsterdam', 'Test Street 1, Amsterdam', 1, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system');

-- Insert Test Hub
INSERT INTO hub (id, external_id, name, location_id)
VALUES (1, 'HUB001', 'Main', 1);

-- Insert Test Bike Brand
INSERT INTO bike_brand (id, external_id, name, company_id)
VALUES (1, 'BB001', 'Test Brand', 1);

-- Insert Test Bike Engine
INSERT INTO bike_engine (id, external_id, name)
VALUES (1, 'BE001', 'Electric Motor 250W');

-- Insert Test Bike Model
INSERT INTO bike_model (id, external_id, name, bike_brand_id, bike_type_id, bike_engine_id, image_url)
VALUES (1, 'BM001', 'Test Model X', 1, 1, 1, 'https://example.com/bike.jpg');

-- Insert Test Bikes (using Product table with discriminator)
INSERT INTO product (id, external_id, product_type, is_b2b_rentable, code, bike_status_id, bike_type_id, bike_model_id, hub_id, date_created, last_date_modified, created_by, last_modified_by)
VALUES (1, 'BIKE001', 'BIKE', false, 'BIKE001', 1, 1, 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system');

INSERT INTO product (id, external_id, product_type, is_b2b_rentable, code, bike_status_id, bike_type_id, bike_model_id, hub_id, date_created, last_date_modified, created_by, last_modified_by)
VALUES (2, 'BIKE002', 'BIKE', false, 'BIKE002', 1, 2, 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system');
