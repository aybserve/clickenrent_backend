-- ========================================
-- Comprehensive Test Data for Rental Service
-- ========================================

-- ========================================
-- 1. REFERENCE DATA / STATUS TABLES
-- ========================================

-- Bike Types
INSERT INTO bike_type (id, name) VALUES (1, 'Electric bike');
INSERT INTO bike_type (id, name) VALUES (2, 'Non-electric bike');

-- Bike Statuses
INSERT INTO bike_status (id, name) VALUES (1, 'Available');
INSERT INTO bike_status (id, name) VALUES (2, 'Broken');
INSERT INTO bike_status (id, name) VALUES (3, 'Disabled');
INSERT INTO bike_status (id, name) VALUES (4, 'In use');
INSERT INTO bike_status (id, name) VALUES (5, 'Paused');
INSERT INTO bike_status (id, name) VALUES (6, 'Reserved');

-- Bike Rental Statuses
INSERT INTO bike_rental_status (id, name) VALUES (1, 'Active');
INSERT INTO bike_rental_status (id, name) VALUES (2, 'Completed');
INSERT INTO bike_rental_status (id, name) VALUES (3, 'Cancelled');

-- Battery Charge Statuses
INSERT INTO battery_charge_status (id, name) VALUES (1, 'Idle');
INSERT INTO battery_charge_status (id, name) VALUES (2, 'Identifying');
INSERT INTO battery_charge_status (id, name) VALUES (3, 'Pre charging');
INSERT INTO battery_charge_status (id, name) VALUES (4, 'Charging');
INSERT INTO battery_charge_status (id, name) VALUES (5, 'Fully charged');
INSERT INTO battery_charge_status (id, name) VALUES (6, 'Error');
INSERT INTO battery_charge_status (id, name) VALUES (7, 'Idle on ride');

-- Rental Statuses
INSERT INTO rental_status (id, name) VALUES (1, 'Pending');
INSERT INTO rental_status (id, name) VALUES (2, 'Active');
INSERT INTO rental_status (id, name) VALUES (3, 'Completed');
INSERT INTO rental_status (id, name) VALUES (4, 'Cancelled');

-- Rental Units
INSERT INTO rental_unit (id, name) VALUES (1, 'Day');
INSERT INTO rental_unit (id, name) VALUES (2, 'Hour');
INSERT INTO rental_unit (id, name) VALUES (3, 'Week');
INSERT INTO rental_unit (id, name) VALUES (4, 'Month');

-- Ride Statuses
INSERT INTO ride_status (id, name) VALUES (1, 'Active');
INSERT INTO ride_status (id, name) VALUES (2, 'Finished');
INSERT INTO ride_status (id, name) VALUES (3, 'Paused');

-- Location Roles
INSERT INTO location_role (id, name) VALUES (1, 'Admin');
INSERT INTO location_role (id, name) VALUES (2, 'Manager');
INSERT INTO location_role (id, name) VALUES (3, 'Staff');
INSERT INTO location_role (id, name) VALUES (4, 'Viewer');

-- B2B Sale Statuses
INSERT INTO b2b_sale_status (id, name) VALUES (1, 'Request');
INSERT INTO b2b_sale_status (id, name) VALUES (2, 'Ordered');
INSERT INTO b2b_sale_status (id, name) VALUES (3, 'Pending');
INSERT INTO b2b_sale_status (id, name) VALUES (4, 'Payment');
INSERT INTO b2b_sale_status (id, name) VALUES (5, 'Delivered');
INSERT INTO b2b_sale_status (id, name) VALUES (6, 'Cancelled');

-- B2B Sale Order Statuses
INSERT INTO b2b_sale_order_status (id, name) VALUES (1, 'Pending');
INSERT INTO b2b_sale_order_status (id, name) VALUES (2, 'Processing');
INSERT INTO b2b_sale_order_status (id, name) VALUES (3, 'Shipped');
INSERT INTO b2b_sale_order_status (id, name) VALUES (4, 'Delivered');
INSERT INTO b2b_sale_order_status (id, name) VALUES (5, 'Cancelled');

-- B2B Subscription Statuses
INSERT INTO b2b_subscription_status (id, name) VALUES (1, 'Pending');
INSERT INTO b2b_subscription_status (id, name) VALUES (2, 'Active');
INSERT INTO b2b_subscription_status (id, name) VALUES (3, 'Cancelled');
INSERT INTO b2b_subscription_status (id, name) VALUES (4, 'Expired');

-- B2B Subscription Order Statuses
INSERT INTO b2b_subscription_order_status (id, name) VALUES (1, 'Pending');
INSERT INTO b2b_subscription_order_status (id, name) VALUES (2, 'Active');
INSERT INTO b2b_subscription_order_status (id, name) VALUES (3, 'Completed');
INSERT INTO b2b_subscription_order_status (id, name) VALUES (4, 'Cancelled');

-- Charging Station Statuses
INSERT INTO charging_station_status (id, name) VALUES (1, 'Idle');
INSERT INTO charging_station_status (id, name) VALUES (2, 'Pre charging');
INSERT INTO charging_station_status (id, name) VALUES (3, 'Charging');
INSERT INTO charging_station_status (id, name) VALUES (4, 'Fully charged');
INSERT INTO charging_station_status (id, name) VALUES (5, 'Error');
INSERT INTO charging_station_status (id, name) VALUES (6, 'Offline');

-- ========================================
-- 2. COORDINATES
-- ========================================
INSERT INTO coordinates (id, latitude, longitude) VALUES (1, 52.370216, 4.895168);   -- Amsterdam
INSERT INTO coordinates (id, latitude, longitude) VALUES (2, 51.507351, -0.127758);  -- London
INSERT INTO coordinates (id, latitude, longitude) VALUES (3, 48.856614, 2.352222);   -- Paris
INSERT INTO coordinates (id, latitude, longitude) VALUES (4, 52.520008, 13.404954);  -- Berlin
INSERT INTO coordinates (id, latitude, longitude) VALUES (5, 41.902782, 12.496366);  -- Rome

-- ========================================
-- 3. LOCATIONS (across multiple companies)
-- ========================================
INSERT INTO location (id, external_id, name, address, company_id, is_public, description, date_created, last_date_modified, created_by, last_modified_by, is_deleted)
VALUES (1, 'LOC001', 'Amsterdam Central', 'Stationsplein 1, 1012 AB Amsterdam', 1, true, 'Main location in Amsterdam', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false);

INSERT INTO location (id, external_id, name, address, company_id, is_public, description, date_created, last_date_modified, created_by, last_modified_by, is_deleted)
VALUES (2, 'LOC002', 'Amsterdam West', 'Westermarkt 20, 1016 DK Amsterdam', 1, true, 'West district location', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false);

INSERT INTO location (id, external_id, name, address, company_id, is_public, description, date_created, last_date_modified, created_by, last_modified_by, is_deleted)
VALUES (3, 'LOC003', 'London Bridge', 'London Bridge Street, London SE1 9SG', 2, true, 'London main station', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false);

INSERT INTO location (id, external_id, name, address, company_id, is_public, description, date_created, last_date_modified, created_by, last_modified_by, is_deleted)
VALUES (4, 'LOC004', 'Paris Nord', 'Gare du Nord, 75010 Paris', 2, true, 'Paris North Station', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false);

INSERT INTO location (id, external_id, name, address, company_id, is_public, description, date_created, last_date_modified, created_by, last_modified_by, is_deleted)
VALUES (5, 'LOC005', 'Berlin Mitte', 'Alexanderplatz 1, 10178 Berlin', 3, true, 'Berlin city center', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false);

INSERT INTO location (id, external_id, name, address, company_id, is_public, description, date_created, last_date_modified, created_by, last_modified_by, is_deleted)
VALUES (6, 'LOC006', 'Private Company 3 Location', 'Test Street 100, Berlin', 3, false, 'Private location for testing', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false);

-- ========================================
-- 4. HUBS
-- ========================================
INSERT INTO hub (id, external_id, name, location_id, is_deleted) VALUES (1, 'HUB001', 'Main', 1, false);
INSERT INTO hub (id, external_id, name, location_id, is_deleted) VALUES (2, 'HUB002', 'South', 1, false);
INSERT INTO hub (id, external_id, name, location_id, is_deleted) VALUES (3, 'HUB003', 'Main', 2, false);
INSERT INTO hub (id, external_id, name, location_id, is_deleted) VALUES (4, 'HUB004', 'Main', 3, false);
INSERT INTO hub (id, external_id, name, location_id, is_deleted) VALUES (5, 'HUB005', 'Main', 4, false);
INSERT INTO hub (id, external_id, name, location_id, is_deleted) VALUES (6, 'HUB006', 'Main', 5, false);

-- ========================================
-- 5. BIKE BRANDS, ENGINES, AND MODELS
-- ========================================

-- Bike Brands
INSERT INTO bike_brand (id, external_id, name, company_id, is_deleted) VALUES (1, 'BB001', 'VanMoof', 1, false);
INSERT INTO bike_brand (id, external_id, name, company_id, is_deleted) VALUES (2, 'BB002', 'Gazelle', 1, false);
INSERT INTO bike_brand (id, external_id, name, company_id, is_deleted) VALUES (3, 'BB003', 'Cowboy', 2, false);

-- Bike Engines
INSERT INTO bike_engine (id, external_id, name, is_deleted) VALUES (1, 'BE001', 'Bosch Performance Line 250W', false);
INSERT INTO bike_engine (id, external_id, name, is_deleted) VALUES (2, 'BE002', 'Shimano Steps E6100 250W', false);
INSERT INTO bike_engine (id, external_id, name, is_deleted) VALUES (3, 'BE003', 'No Engine', false);

-- Bike Models
INSERT INTO bike_model (id, external_id, name, bike_brand_id, bike_type_id, bike_engine_id, image_url, is_deleted)
VALUES (1, 'BM001', 'VanMoof S3', 1, 1, 1, 'https://example.com/vanmoof-s3.jpg', false);

INSERT INTO bike_model (id, external_id, name, bike_brand_id, bike_type_id, bike_engine_id, image_url, is_deleted)
VALUES (2, 'BM002', 'Gazelle Ultimate C380', 2, 1, 2, 'https://example.com/gazelle-c380.jpg', false);

INSERT INTO bike_model (id, external_id, name, bike_brand_id, bike_type_id, bike_engine_id, image_url, is_deleted)
VALUES (3, 'BM003', 'Cowboy 4', 3, 1, 1, 'https://example.com/cowboy-4.jpg', false);

INSERT INTO bike_model (id, external_id, name, bike_brand_id, bike_type_id, bike_engine_id, image_url, is_deleted)
VALUES (4, 'BM004', 'Gazelle Classic', 2, 2, 3, 'https://example.com/gazelle-classic.jpg', false);

-- ========================================
-- 6. BIKES (Products with discriminator BIKE)
-- ========================================
INSERT INTO product (id, external_id, product_type, is_b2b_rentable, code, frame_number, bike_status_id, bike_type_id, bike_model_id, hub_id, date_created, last_date_modified, created_by, last_modified_by, is_deleted)
VALUES (1, 'BIKE001', 'BIKE', false, 'BIKE001', 'FR001', 1, 1, 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false);

INSERT INTO product (id, external_id, product_type, is_b2b_rentable, code, frame_number, bike_status_id, bike_type_id, bike_model_id, hub_id, date_created, last_date_modified, created_by, last_modified_by, is_deleted)
VALUES (2, 'BIKE002', 'BIKE', true, 'BIKE002', 'FR002', 1, 1, 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false);

INSERT INTO product (id, external_id, product_type, is_b2b_rentable, code, frame_number, bike_status_id, bike_type_id, bike_model_id, hub_id, date_created, last_date_modified, created_by, last_modified_by, is_deleted)
VALUES (3, 'BIKE003', 'BIKE', false, 'BIKE003', 'FR003', 4, 1, 2, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false);

INSERT INTO product (id, external_id, product_type, is_b2b_rentable, code, frame_number, bike_status_id, bike_type_id, bike_model_id, hub_id, date_created, last_date_modified, created_by, last_modified_by, is_deleted)
VALUES (4, 'BIKE004', 'BIKE', true, 'BIKE004', 'FR004', 1, 1, 3, 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false);

INSERT INTO product (id, external_id, product_type, is_b2b_rentable, code, frame_number, bike_status_id, bike_type_id, bike_model_id, hub_id, date_created, last_date_modified, created_by, last_modified_by, is_deleted)
VALUES (5, 'BIKE005', 'BIKE', false, 'BIKE005', 'FR005', 2, 1, 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false);

INSERT INTO product (id, external_id, product_type, is_b2b_rentable, code, frame_number, bike_status_id, bike_type_id, bike_model_id, hub_id, date_created, last_date_modified, created_by, last_modified_by, is_deleted)
VALUES (6, 'BIKE006', 'BIKE', false, 'BIKE006', 'FR006', 6, 2, 4, 4, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false);

INSERT INTO product (id, external_id, product_type, is_b2b_rentable, code, frame_number, bike_status_id, bike_type_id, bike_model_id, hub_id, date_created, last_date_modified, created_by, last_modified_by, is_deleted)
VALUES (7, 'BIKE007', 'BIKE', true, 'BIKE007', 'FR007', 1, 1, 1, 5, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false);

INSERT INTO product (id, external_id, product_type, is_b2b_rentable, code, frame_number, bike_status_id, bike_type_id, bike_model_id, hub_id, date_created, last_date_modified, created_by, last_modified_by, is_deleted)
VALUES (8, 'BIKE008', 'BIKE', false, 'BIKE008', 'FR008', 3, 1, 2, 6, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false);

-- ========================================
-- 7. CHARGING STATIONS
-- ========================================

-- Charging Station Brands
INSERT INTO charging_station_brand (id, external_id, name, is_deleted) VALUES (1, 'CSB001', 'Tesla', false);
INSERT INTO charging_station_brand (id, external_id, name, is_deleted) VALUES (2, 'CSB002', 'ChargePoint', false);

-- Charging Station Models
INSERT INTO charging_station_model (id, external_id, name, charging_station_brand_id, is_deleted)
VALUES (1, 'CSM001', 'Wall Connector Gen 3', 1, false);
INSERT INTO charging_station_model (id, external_id, name, charging_station_brand_id, is_deleted)
VALUES (2, 'CSM002', 'CT4000 Series', 2, false);

-- Charging Stations (as Products)
INSERT INTO product (id, external_id, product_type, is_b2b_rentable, code, charging_station_status_id, charging_station_model_id, hub_id, date_created, last_date_modified, created_by, last_modified_by, is_deleted)
VALUES (100, 'CS001', 'CHARGING_STATION', false, 'CS001', 1, 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false);

INSERT INTO product (id, external_id, product_type, is_b2b_rentable, code, charging_station_status_id, charging_station_model_id, hub_id, date_created, last_date_modified, created_by, last_modified_by, is_deleted)
VALUES (101, 'CS002', 'CHARGING_STATION', false, 'CS002', 3, 2, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false);

-- ========================================
-- 8. PARTS
-- ========================================

-- Part Categories
INSERT INTO part_category (id, external_id, name, is_deleted) VALUES (1, 'PC001', 'Battery', false);
INSERT INTO part_category (id, external_id, name, is_deleted) VALUES (2, 'PC002', 'Tire', false);
INSERT INTO part_category (id, external_id, name, is_deleted) VALUES (3, 'PC003', 'Lock', false);

-- Part Brands
INSERT INTO part_brand (id, external_id, name, is_deleted) VALUES (1, 'PB001', 'Samsung', false);
INSERT INTO part_brand (id, external_id, name, is_deleted) VALUES (2, 'PB002', 'Schwalbe', false);

-- Parts (as Products)
INSERT INTO product (id, external_id, product_type, is_b2b_rentable, code, serial_number, part_category_id, part_brand_id, date_created, last_date_modified, created_by, last_modified_by, is_deleted)
VALUES (200, 'PART001', 'PART', false, 'PART001', 'SN001', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false);

INSERT INTO product (id, external_id, product_type, is_b2b_rentable, code, serial_number, part_category_id, part_brand_id, date_created, last_date_modified, created_by, last_modified_by, is_deleted)
VALUES (201, 'PART002', 'PART', false, 'PART002', 'SN002', 2, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false);

-- ========================================
-- 9. KEYS AND LOCKS
-- ========================================
INSERT INTO key_entity (id, external_id, code, hub_id, date_created, last_date_modified, created_by, last_modified_by, is_deleted)
VALUES (1, 'KEY001', 'KEY001', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false);

INSERT INTO key_entity (id, external_id, code, hub_id, date_created, last_date_modified, created_by, last_modified_by, is_deleted)
VALUES (2, 'KEY002', 'KEY002', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false);

INSERT INTO lock_entity (id, external_id, code, hub_id, date_created, last_date_modified, created_by, last_modified_by, is_deleted)
VALUES (1, 'LOCK001', 'LOCK001', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false);

INSERT INTO lock_entity (id, external_id, code, hub_id, date_created, last_date_modified, created_by, last_modified_by, is_deleted)
VALUES (2, 'LOCK002', 'LOCK002', 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false);

-- ========================================
-- 10. RENTAL PLANS
-- ========================================
INSERT INTO rental_plan (id, external_id, name, description, duration, rental_unit_id, price, company_id, date_created, last_date_modified, created_by, last_modified_by, is_deleted)
VALUES (1, 'RP001', 'Hourly Plan', 'Pay per hour', 1, 2, 5.00, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false);

INSERT INTO rental_plan (id, external_id, name, description, duration, rental_unit_id, price, company_id, date_created, last_date_modified, created_by, last_modified_by, is_deleted)
VALUES (2, 'RP002', 'Daily Plan', 'Pay per day', 1, 1, 25.00, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false);

INSERT INTO rental_plan (id, external_id, name, description, duration, rental_unit_id, price, company_id, date_created, last_date_modified, created_by, last_modified_by, is_deleted)
VALUES (3, 'RP003', 'Weekly Plan', 'Pay per week', 1, 3, 150.00, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false);

-- Bike Model Rental Plans
INSERT INTO bike_model_rental_plan (id, bike_model_id, rental_plan_id, is_deleted)
VALUES (1, 1, 1, false);

INSERT INTO bike_model_rental_plan (id, bike_model_id, rental_plan_id, is_deleted)
VALUES (2, 1, 2, false);

INSERT INTO bike_model_rental_plan (id, bike_model_id, rental_plan_id, is_deleted)
VALUES (3, 2, 2, false);

INSERT INTO bike_model_rental_plan (id, bike_model_id, rental_plan_id, is_deleted)
VALUES (4, 3, 3, false);

-- ========================================
-- 11. B2B SALES AND SUBSCRIPTIONS
-- ========================================

-- B2B Sales
INSERT INTO b2b_sale (id, external_id, name, description, company_id, b2b_sale_status_id, date_created, last_date_modified, created_by, last_modified_by, is_deleted)
VALUES (1, 'B2BS001', 'Corporate Fleet Sale', 'Fleet of 10 bikes for Corporate Inc', 1, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false);

INSERT INTO b2b_sale (id, external_id, name, description, company_id, b2b_sale_status_id, date_created, last_date_modified, created_by, last_modified_by, is_deleted)
VALUES (2, 'B2BS002', 'Hotel Partnership Sale', 'Bikes for hotel guests', 2, 4, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false);

-- B2B Sale Orders
INSERT INTO b2b_sale_order (id, external_id, b2b_sale_id, b2b_sale_order_status_id, total_amount, date_created, last_date_modified, created_by, last_modified_by, is_deleted)
VALUES (1, 'B2BSO001', 1, 2, 25000.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false);

-- B2B Sale Items
INSERT INTO b2b_sale_item (id, b2b_sale_id, product_id, quantity, price, total_price, is_deleted)
VALUES (1, 1, 2, 5, 500.00, 2500.00, false);

INSERT INTO b2b_sale_item (id, b2b_sale_id, product_id, quantity, price, total_price, is_deleted)
VALUES (2, 1, 4, 5, 500.00, 2500.00, false);

-- B2B Subscriptions
INSERT INTO b2b_subscription (id, external_id, name, description, company_id, b2b_subscription_status_id, start_date, end_date, date_created, last_date_modified, created_by, last_modified_by, is_deleted)
VALUES (1, 'B2BSUB001', 'Monthly Corporate Subscription', '30 bikes monthly subscription', 1, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '1' YEAR, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false);

-- B2B Subscription Items
INSERT INTO b2b_subscription_item (id, b2b_subscription_id, bike_model_id, quantity, monthly_price, is_deleted)
VALUES (1, 1, 1, 20, 3000.00, false);

INSERT INTO b2b_subscription_item (id, b2b_subscription_id, bike_model_id, quantity, monthly_price, is_deleted)
VALUES (2, 1, 2, 10, 1500.00, false);

-- B2B Subscription Orders
INSERT INTO b2b_subscription_order (id, external_id, b2b_subscription_id, b2b_subscription_order_status_id, total_amount, date_created, last_date_modified, created_by, last_modified_by, is_deleted)
VALUES (1, 'B2BSORD001', 1, 2, 4500.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false);

-- ========================================
-- 12. USER LOCATIONS
-- ========================================
INSERT INTO user_location (id, user_id, location_id, location_role_id, date_created, last_date_modified, created_by, last_modified_by, is_deleted)
VALUES (1, 1, 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false);

INSERT INTO user_location (id, user_id, location_id, location_role_id, date_created, last_date_modified, created_by, last_modified_by, is_deleted)
VALUES (2, 2, 1, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false);

INSERT INTO user_location (id, user_id, location_id, location_role_id, date_created, last_date_modified, created_by, last_modified_by, is_deleted)
VALUES (3, 3, 2, 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false);

-- ========================================
-- 13. RENTALS AND BIKE RENTALS
-- ========================================

-- Rentals
INSERT INTO rental (id, external_id, user_id, company_id, rental_status_id, date_created, last_date_modified, created_by, last_modified_by, is_deleted)
VALUES (1, 'RENT001', 1, 1, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false);

INSERT INTO rental (id, external_id, user_id, company_id, rental_status_id, date_created, last_date_modified, created_by, last_modified_by, is_deleted)
VALUES (2, 'RENT002', 2, 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false);

INSERT INTO rental (id, external_id, user_id, company_id, rental_status_id, date_created, last_date_modified, created_by, last_modified_by, is_deleted)
VALUES (3, 'RENT003', 3, 2, 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false);

-- Bike Rentals
INSERT INTO bike_rental (id, external_id, rental_id, bike_id, bike_rental_status_id, rental_plan_id, start_date, end_date, date_created, last_date_modified, created_by, last_modified_by, is_deleted)
VALUES (1, 'BR001', 1, 1, 1, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '1' DAY, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false);

INSERT INTO bike_rental (id, external_id, rental_id, bike_id, bike_rental_status_id, rental_plan_id, start_date, end_date, date_created, last_date_modified, created_by, last_modified_by, is_deleted)
VALUES (2, 'BR002', 3, 4, 2, 3, CURRENT_TIMESTAMP - INTERVAL '1' WEEK, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false);

-- ========================================
-- 14. BIKE RESERVATIONS
-- ========================================
INSERT INTO bike_reservation (id, external_id, user_id, bike_id, reservation_start, reservation_end, is_active, date_created, last_date_modified, created_by, last_modified_by, is_deleted)
VALUES (1, 'BRES001', 2, 6, CURRENT_TIMESTAMP + INTERVAL '1' DAY, CURRENT_TIMESTAMP + INTERVAL '2' DAY, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false);

INSERT INTO bike_reservation (id, external_id, user_id, bike_id, reservation_start, reservation_end, is_active, date_created, last_date_modified, created_by, last_modified_by, is_deleted)
VALUES (2, 'BRES002', 1, 2, CURRENT_TIMESTAMP + INTERVAL '3' DAY, CURRENT_TIMESTAMP + INTERVAL '5' DAY, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false);

-- ========================================
-- 15. RIDES
-- ========================================
INSERT INTO ride (id, external_id, bike_rental_id, ride_status_id, start_time, end_time, distance_km, date_created, last_date_modified, created_by, last_modified_by, is_deleted)
VALUES (1, 'RIDE001', 1, 2, CURRENT_TIMESTAMP - INTERVAL '2' HOUR, CURRENT_TIMESTAMP - INTERVAL '1' HOUR, 15.5, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false);

INSERT INTO ride (id, external_id, bike_rental_id, ride_status_id, start_time, end_time, distance_km, date_created, last_date_modified, created_by, last_modified_by, is_deleted)
VALUES (2, 'RIDE002', 1, 1, CURRENT_TIMESTAMP - INTERVAL '30' MINUTE, null, 5.2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false);

-- ========================================
-- 16. SERVICES AND SERVICE PRODUCTS
-- ========================================
INSERT INTO service (id, external_id, name, description, price, company_id, date_created, last_date_modified, created_by, last_modified_by, is_deleted)
VALUES (1, 'SRV001', 'Bike Maintenance', 'Standard bike maintenance service', 50.00, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false);

INSERT INTO service (id, external_id, name, description, price, company_id, date_created, last_date_modified, created_by, last_modified_by, is_deleted)
VALUES (2, 'SRV002', 'Battery Replacement', 'Replace bike battery', 150.00, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false);

-- Service Products (as Products)
INSERT INTO product (id, external_id, product_type, is_b2b_rentable, code, service_id, date_created, last_date_modified, created_by, last_modified_by, is_deleted)
VALUES (300, 'SP001', 'SERVICE_PRODUCT', false, 'SP001', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false);

INSERT INTO product (id, external_id, product_type, is_b2b_rentable, code, service_id, date_created, last_date_modified, created_by, last_modified_by, is_deleted)
VALUES (301, 'SP002', 'SERVICE_PRODUCT', false, 'SP002', 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false);

-- ========================================
-- 17. STOCK MOVEMENTS
-- ========================================
INSERT INTO stock_movement (id, external_id, product_id, from_hub_id, to_hub_id, quantity, movement_date, notes, date_created, last_date_modified, created_by, last_modified_by, is_deleted)
VALUES (1, 'SM001', 1, 1, 2, 1, CURRENT_TIMESTAMP, 'Transferred for maintenance', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false);

INSERT INTO stock_movement (id, external_id, product_id, from_hub_id, to_hub_id, quantity, movement_date, notes, date_created, last_date_modified, created_by, last_modified_by, is_deleted)
VALUES (2, 'SM002', 200, 1, 3, 5, CURRENT_TIMESTAMP, 'Parts replenishment', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false);

-- ========================================
-- 18. BIKE PARTS (Junction table)
-- ========================================
INSERT INTO bike_part (id, bike_id, part_id, installation_date, is_deleted)
VALUES (1, 1, 200, CURRENT_TIMESTAMP - INTERVAL '30' DAY, false);

INSERT INTO bike_part (id, bike_id, part_id, installation_date, is_deleted)
VALUES (2, 1, 201, CURRENT_TIMESTAMP - INTERVAL '30' DAY, false);

INSERT INTO bike_part (id, bike_id, part_id, installation_date, is_deleted)
VALUES (3, 2, 200, CURRENT_TIMESTAMP - INTERVAL '60' DAY, false);
