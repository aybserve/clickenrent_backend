-- =====================================================================================================================
-- RENTAL SERVICE - DATABASE INITIALIZATION
-- =====================================================================================================================
-- Module: rental-service
-- Database: PostgreSQL
-- Description: Production database initialization for the bike/equipment rental and fleet management service.
--              Contains required lookup data and optional reference/test data.
-- 
-- Usage:
--   This file is automatically executed by Spring Boot on application startup when:
--   - spring.jpa.hibernate.ddl-auto is set to 'create', 'create-drop', or 'update'
--   - spring.sql.init.mode is set to 'always' (default is 'embedded')
--
-- Note: For production deployment, ensure only required lookup data is uncommented.
--       Test/sample data should be commented out or removed.
--
-- Author: Vitaliy Shvetsov
-- =====================================================================================================================

-- =====================================================================================================================
-- SECTION 1: REQUIRED LOOKUP DATA
-- =====================================================================================================================
-- This data is REQUIRED for the application to function properly.
-- Do NOT comment out or remove this section.

-- ---------------------------------------------------------------------------------------------------------------------
-- 1.1 BIKE BRANDS (Sample lookup data - can be customized per deployment)
-- ---------------------------------------------------------------------------------------------------------------------
-- Note: These are common bike brands. Add/remove based on your business needs.
INSERT INTO bike_brands (id, external_id, company_external_id, name, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, '550e8400-e29b-41d4-a716-446655440001', 'company-ext-001', 'Trek', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(2, '550e8400-e29b-41d4-a716-446655440002', 'company-ext-001', 'Giant', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(3, '550e8400-e29b-41d4-a716-446655440003', 'company-ext-001', 'Specialized', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(4, '550e8400-e29b-41d4-a716-446655440004', 'company-ext-001', 'Cannondale', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(5, '550e8400-e29b-41d4-a716-446655440005', 'company-ext-001', 'Scott', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(6, '550e8400-e29b-41d4-a716-446655440006', 'company-ext-001', 'Merida', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(7, '550e8400-e29b-41d4-a716-446655440007', 'company-ext-001', 'Cube', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(8, '550e8400-e29b-41d4-a716-446655440008', 'company-ext-001', 'Bianchi', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- ---------------------------------------------------------------------------------------------------------------------
-- 1.2 PART BRANDS (Sample lookup data - can be customized per deployment)
-- ---------------------------------------------------------------------------------------------------------------------
-- Note: These are common bike part/component brands. Add/remove based on your business needs.
INSERT INTO part_brands (id, external_id, company_external_id, name, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, '550e8400-e29b-41d4-a716-446655440011', 'company-ext-001', 'Shimano', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(2, '550e8400-e29b-41d4-a716-446655440012', 'company-ext-001', 'SRAM', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(3, '550e8400-e29b-41d4-a716-446655440013', 'company-ext-001', 'Campagnolo', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(4, '550e8400-e29b-41d4-a716-446655440014', 'company-ext-001', 'Continental', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(5, '550e8400-e29b-41d4-a716-446655440015', 'company-ext-001', 'Michelin', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(6, '550e8400-e29b-41d4-a716-446655440016', 'company-ext-001', 'Schwalbe', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(7, '550e8400-e29b-41d4-a716-446655440017', 'company-ext-001', 'RockShox', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(8, '550e8400-e29b-41d4-a716-446655440018', 'company-ext-001', 'Fox', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- ---------------------------------------------------------------------------------------------------------------------
-- 1.3 CHARGING STATION BRANDS (Sample lookup data - for e-bikes)
-- ---------------------------------------------------------------------------------------------------------------------
-- Note: These are common e-bike charging station brands. Add/remove based on your business needs.
INSERT INTO charging_station_brands (id, external_id, company_external_id, name, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, '550e8400-e29b-41d4-a716-446655440021', 'company-ext-001', 'Bosch', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(2, '550e8400-e29b-41d4-a716-446655440022', 'company-ext-001', 'Shimano STEPS', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(3, '550e8400-e29b-41d4-a716-446655440023', 'company-ext-001', 'Yamaha', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(4, '550e8400-e29b-41d4-a716-446655440024', 'company-ext-001', 'Brose', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(5, '550e8400-e29b-41d4-a716-446655440025', 'company-ext-001', 'Panasonic', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- =====================================================================================================================
-- SECTION 2: SAMPLE/TEST DATA (OPTIONAL)
-- =====================================================================================================================
-- This section contains sample data for testing and development purposes.
-- COMMENT OUT or REMOVE this entire section before deploying to production.
-- Sample data references external IDs from other services (auth-service, payment-service).

-- ---------------------------------------------------------------------------------------------------------------------
-- 2.1 LOCATIONS (Sample data - references companies from auth-service)
-- ---------------------------------------------------------------------------------------------------------------------
INSERT INTO locations (id, external_id, company_external_id, name, address, city, country, postal_code, latitude, longitude, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, '550e8400-e29b-41d4-a716-446655440101', 'company-ext-001', 'Downtown Bike Hub', '123 Main Street', 'Kyiv', 'Ukraine', '01001', 50.4501, 30.5234, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(2, '550e8400-e29b-41d4-a716-446655440102', 'company-ext-001', 'Park Side Station', '456 Park Avenue', 'Kyiv', 'Ukraine', '01002', 50.4547, 30.5238, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(3, '550e8400-e29b-41d4-a716-446655440103', 'company-ext-002', 'City Center Rentals', '789 Central Square', 'Lviv', 'Ukraine', '79000', 49.8397, 24.0297, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- ---------------------------------------------------------------------------------------------------------------------
-- 2.2 HUBS (Sample data - bike storage/parking hubs)
-- ---------------------------------------------------------------------------------------------------------------------
INSERT INTO hubs (id, external_id, location_id, name, capacity, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, '550e8400-e29b-41d4-a716-446655440201', 1, 'Hub A - Main Entrance', 20, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(2, '550e8400-e29b-41d4-a716-446655440202', 1, 'Hub B - North Side', 15, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(3, '550e8400-e29b-41d4-a716-446655440203', 2, 'Hub A - Park Entrance', 25, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(4, '550e8400-e29b-41d4-a716-446655440204', 3, 'Hub A - Central', 30, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- ---------------------------------------------------------------------------------------------------------------------
-- 2.3 BIKE MODELS (Sample data)
-- ---------------------------------------------------------------------------------------------------------------------
INSERT INTO bike_models (id, external_id, bike_brand_id, name, type, description, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, '550e8400-e29b-41d4-a716-446655440301', 1, 'FX 3 Disc', 'HYBRID', 'Versatile hybrid bike with disc brakes', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(2, '550e8400-e29b-41d4-a716-446655440302', 1, 'Marlin 7', 'MOUNTAIN', 'Trail-ready mountain bike', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(3, '550e8400-e29b-41d4-a716-446655440303', 2, 'Escape 3', 'CITY', 'Comfortable city commuter', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(4, '550e8400-e29b-41d4-a716-446655440304', 3, 'Turbo Vado', 'ELECTRIC', 'Premium electric bike', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- ---------------------------------------------------------------------------------------------------------------------
-- 2.4 BIKES (Sample data - actual bike inventory)
-- ---------------------------------------------------------------------------------------------------------------------
INSERT INTO bikes (id, external_id, bike_model_id, hub_id, serial_number, status, purchase_date, purchase_price, currency_external_id, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, '550e8400-e29b-41d4-a716-446655440401', 1, 1, 'TREK-FX3-001', 'AVAILABLE', CURRENT_DATE - INTERVAL '6 months', 800.00, '550e8400-e29b-41d4-a716-446655440022', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(2, '550e8400-e29b-41d4-a716-446655440402', 1, 1, 'TREK-FX3-002', 'AVAILABLE', CURRENT_DATE - INTERVAL '6 months', 800.00, '550e8400-e29b-41d4-a716-446655440022', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(3, '550e8400-e29b-41d4-a716-446655440403', 2, 2, 'TREK-M7-001', 'AVAILABLE', CURRENT_DATE - INTERVAL '4 months', 1200.00, '550e8400-e29b-41d4-a716-446655440022', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(4, '550e8400-e29b-41d4-a716-446655440404', 3, 3, 'GIANT-ESC3-001', 'AVAILABLE', CURRENT_DATE - INTERVAL '3 months', 600.00, '550e8400-e29b-41d4-a716-446655440022', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(5, '550e8400-e29b-41d4-a716-446655440405', 4, 1, 'SPEC-VADO-001', 'AVAILABLE', CURRENT_DATE - INTERVAL '2 months', 3500.00, '550e8400-e29b-41d4-a716-446655440022', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- ---------------------------------------------------------------------------------------------------------------------
-- 2.5 RENTAL PLANS (Sample data - pricing plans)
-- ---------------------------------------------------------------------------------------------------------------------
INSERT INTO rental_plans (id, external_id, location_id, name, description, duration_minutes, price, currency_external_id, is_active, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, '550e8400-e29b-41d4-a716-446655440501', 1, 'Hourly Plan', 'Pay per hour', 60, 5.00, '550e8400-e29b-41d4-a716-446655440022', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(2, '550e8400-e29b-41d4-a716-446655440502', 1, 'Half Day', '4 hours rental', 240, 15.00, '550e8400-e29b-41d4-a716-446655440022', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(3, '550e8400-e29b-41d4-a716-446655440503', 1, 'Full Day', '24 hours rental', 1440, 25.00, '550e8400-e29b-41d4-a716-446655440022', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(4, '550e8400-e29b-41d4-a716-446655440504', 2, 'Hourly Plan', 'Pay per hour', 60, 5.00, '550e8400-e29b-41d4-a716-446655440022', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(5, '550e8400-e29b-41d4-a716-446655440505', 3, 'Full Day', '24 hours rental', 1440, 20.00, '550e8400-e29b-41d4-a716-446655440022', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- ---------------------------------------------------------------------------------------------------------------------
-- 2.6 RENTALS (Sample data - references users from auth-service)
-- ---------------------------------------------------------------------------------------------------------------------
INSERT INTO rentals (id, external_id, user_external_id, company_external_id, start_date_time, end_date_time, total_price, currency_external_id, status, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, 'rental-ext-00101', 'usr-ext-00007', 'company-ext-001', CURRENT_TIMESTAMP - INTERVAL '2 hours', CURRENT_TIMESTAMP - INTERVAL '1 hour', 5.00, '550e8400-e29b-41d4-a716-446655440022', 'COMPLETED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(2, 'rental-ext-00102', 'usr-ext-00008', 'company-ext-001', CURRENT_TIMESTAMP - INTERVAL '5 hours', CURRENT_TIMESTAMP - INTERVAL '1 hour', 15.00, '550e8400-e29b-41d4-a716-446655440022', 'COMPLETED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(3, 'rental-ext-00103', 'usr-ext-00009', 'company-ext-002', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '2 hours', 20.00, '550e8400-e29b-41d4-a716-446655440022', 'COMPLETED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- ---------------------------------------------------------------------------------------------------------------------
-- 2.7 BIKE RENTALS (Sample data - links rentals to specific bikes)
-- ---------------------------------------------------------------------------------------------------------------------
INSERT INTO bike_rentals (id, external_id, rental_id, bike_id, start_hub_id, end_hub_id, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, 'bike-rental-ext-00101', 1, 1, 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(2, 'bike-rental-ext-00102', 2, 2, 1, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(3, 'bike-rental-ext-00103', 3, 4, 3, 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- =====================================================================================================================
-- SECTION 3: SEQUENCE RESET
-- =====================================================================================================================
-- Reset sequences to the correct values after inserting data.
-- This ensures that new records will have IDs starting after the existing data IDs.

SELECT setval('bike_brands_id_seq', (SELECT COALESCE(MAX(id), 1) FROM bike_brands));
SELECT setval('part_brands_id_seq', (SELECT COALESCE(MAX(id), 1) FROM part_brands));
SELECT setval('charging_station_brands_id_seq', (SELECT COALESCE(MAX(id), 1) FROM charging_station_brands));
SELECT setval('locations_id_seq', (SELECT COALESCE(MAX(id), 1) FROM locations));
SELECT setval('hubs_id_seq', (SELECT COALESCE(MAX(id), 1) FROM hubs));
SELECT setval('bike_models_id_seq', (SELECT COALESCE(MAX(id), 1) FROM bike_models));
SELECT setval('bikes_id_seq', (SELECT COALESCE(MAX(id), 1) FROM bikes));
SELECT setval('rental_plans_id_seq', (SELECT COALESCE(MAX(id), 1) FROM rental_plans));
SELECT setval('rentals_id_seq', (SELECT COALESCE(MAX(id), 1) FROM rentals));
SELECT setval('bike_rentals_id_seq', (SELECT COALESCE(MAX(id), 1) FROM bike_rentals));

-- =====================================================================================================================
-- END OF INITIALIZATION
-- =====================================================================================================================
-- Database initialization completed successfully!
-- 
-- Summary:
-- - Required lookup tables: bike_brands (8), part_brands (8), charging_station_brands (5)
-- - Sample data: locations (3), hubs (4), bike_models (4), bikes (5)
-- - Sample data: rental_plans (5), rentals (3), bike_rentals (3)
-- 
-- Cross-Service References:
-- - User External IDs: Referenced from auth-service (usr-ext-XXXXX)
-- - Company External IDs: Referenced from auth-service (company-ext-XXXXX)
-- - Currency External IDs: Referenced from payment-service (550e8400-e29b-41d4-a716-446655440022 = EUR)
-- 
-- Note: The sample data section (SECTION 2) should be commented out or removed in production.
--       Only the lookup data in SECTION 1 is required for the application to function.
-- =====================================================================================================================

