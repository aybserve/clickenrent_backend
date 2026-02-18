-- =====================================================================================================================
-- RENTAL SERVICE - SAMPLE DATA (Flyway Testdata V100)
-- =====================================================================================================================
-- Module: rental-service
-- Database: PostgreSQL
-- Description: Insert sample/test data for brands, models, locations, bikes, rentals, B2B, etc.
--              All inserts use ON CONFLICT to ensure idempotency.
-- 
-- Author: Vitaliy Shvetsov
-- =====================================================================================================================

-- =====================================================================================================================
-- DISABLE FORCE RLS TEMPORARILY FOR DATA INSERTION
-- =====================================================================================================================
ALTER TABLE rental ENABLE ROW LEVEL SECURITY;
ALTER TABLE location ENABLE ROW LEVEL SECURITY;
ALTER TABLE b2b_sale ENABLE ROW LEVEL SECURITY;
ALTER TABLE b2b_subscription ENABLE ROW LEVEL SECURITY;
ALTER TABLE b2b_sale_order ENABLE ROW LEVEL SECURITY;
ALTER TABLE bike_brand ENABLE ROW LEVEL SECURITY;
ALTER TABLE charging_station_brand ENABLE ROW LEVEL SECURITY;
ALTER TABLE part_brand ENABLE ROW LEVEL SECURITY;

-- =====================================================================================================================
-- SECTION 1: BRANDS & MODELS
-- =====================================================================================================================

-- Bike Brands
INSERT INTO bike_brand (id, external_id, name, company_external_id, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, '550e8400-e29b-41d4-a716-446655440001', 'Trek', 'company-ext-001', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(2, '550e8400-e29b-41d4-a716-446655440002', 'Giant', 'company-ext-001', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(3, '550e8400-e29b-41d4-a716-446655440003', 'Specialized', 'company-ext-001', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(4, '550e8400-e29b-41d4-a716-446655440004', 'Cannondale', 'company-ext-001', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(5, '550e8400-e29b-41d4-a716-446655440005', 'Scott', 'company-ext-002', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- Bike Engines
INSERT INTO bike_engine (id, external_id, name, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, '550e8400-e29b-41d4-a716-446655440031', 'Bosch Performance CX', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(2, '550e8400-e29b-41d4-a716-446655440032', 'Shimano EP8', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(3, '550e8400-e29b-41d4-a716-446655440033', 'Yamaha PW-X3', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(4, '550e8400-e29b-41d4-a716-446655440034', 'Brose Drive S Mag', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- Bike Models (Product parent + BikeModel subclass - JOINED inheritance)
INSERT INTO product (id, external_id, product_type, is_b2b_rentable, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1001, '550e8400-e29b-41d4-a716-446655440301', 'BIKE_MODEL', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(1002, '550e8400-e29b-41d4-a716-446655440302', 'BIKE_MODEL', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(1003, '550e8400-e29b-41d4-a716-446655440303', 'BIKE_MODEL', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(1004, '550e8400-e29b-41d4-a716-446655440304', 'BIKE_MODEL', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(1005, '550e8400-e29b-41d4-a716-446655440305', 'BIKE_MODEL', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

INSERT INTO bike_model (id, name, bike_brand_id, bike_type_id, bike_engine_id, image_url, b2b_sale_price, b2b_subscription_price) VALUES
(1001, 'FX 3 Disc', 1, 2, NULL, 'https://images.example.com/bikes/trek-fx3.jpg', 800.00, 50.00),
(1002, 'Marlin 7', 1, 2, NULL, 'https://images.example.com/bikes/trek-marlin7.jpg', 950.00, 75.00),
(1003, 'Escape 3', 2, 2, NULL, 'https://images.example.com/bikes/giant-escape3.jpg', 600.00, 40.00),
(1004, 'Turbo Vado', 3, 1, 1, 'https://images.example.com/bikes/specialized-vado.jpg', 999.00, 99.00),
(1005, 'Rail 9.9', 1, 1, 1, 'https://images.example.com/bikes/trek-rail.jpg', 999.00, 99.00)
ON CONFLICT (id) DO NOTHING;

-- Part Brands
INSERT INTO part_brand (id, external_id, name, company_external_id, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, '550e8400-e29b-41d4-a716-446655440011', 'Shimano', 'company-001', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(2, '550e8400-e29b-41d4-a716-446655440012', 'SRAM', 'company-001', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(3, '550e8400-e29b-41d4-a716-446655440013', 'Continental', 'company-001', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(4, '550e8400-e29b-41d4-a716-446655440014', 'Schwalbe', 'company-002', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(5, '550e8400-e29b-41d4-a716-446655440015', 'Bosch', 'company-002', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- Part Categories
INSERT INTO part_category (id, external_id, name, parent_category_id, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, '550e8400-e29b-41d4-a716-446655440041', 'Battery', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(2, '550e8400-e29b-41d4-a716-446655440042', 'Tire', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(3, '550e8400-e29b-41d4-a716-446655440043', 'Lock', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(4, '550e8400-e29b-41d4-a716-446655440044', 'Saddle', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(5, '550e8400-e29b-41d4-a716-446655440045', 'Pedals', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- Charging Station Brands
INSERT INTO charging_station_brand (id, external_id, name, company_external_id, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, '550e8400-e29b-41d4-a716-446655440021', 'Bosch', 'company-001', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(2, '550e8400-e29b-41d4-a716-446655440022', 'Shimano STEPS', 'company-001', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(3, '550e8400-e29b-41d4-a716-446655440023', 'Yamaha', 'company-002', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- Charging Station Models (Product parent + ChargingStationModel subclass)
INSERT INTO product (id, external_id, product_type, is_b2b_rentable, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(2001, '550e8400-e29b-41d4-a716-446655440051', 'CHARGING_STATION_MODEL', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(2002, '550e8400-e29b-41d4-a716-446655440052', 'CHARGING_STATION_MODEL', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(2003, '550e8400-e29b-41d4-a716-446655440053', 'CHARGING_STATION_MODEL', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

INSERT INTO charging_station_model (id, name, charging_station_brand_id, image_url, b2b_sale_price, b2b_subscription_price) VALUES
(2001, 'PowerPack 500', 1, 'https://images.example.com/chargers/bosch-500.jpg', 450.00, 30.00),
(2002, 'PowerTube 625', 1, 'https://images.example.com/chargers/bosch-625.jpg', 550.00, 35.00),
(2003, 'BT-E8036', 2, 'https://images.example.com/chargers/shimano-e8036.jpg', 480.00, 32.00)
ON CONFLICT (id) DO NOTHING;

-- =====================================================================================================================
-- SECTION 2: GEOGRAPHY & LOCATIONS
-- =====================================================================================================================

INSERT INTO coordinates (id, external_id, latitude, longitude, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, '550e8400-e29b-41d4-a716-446655440501', 50.45010000, 30.52340000, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(2, '550e8400-e29b-41d4-a716-446655440502', 50.45470000, 30.52380000, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(3, '550e8400-e29b-41d4-a716-446655440503', 49.83970000, 24.02970000, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(4, '550e8400-e29b-41d4-a716-446655440504', 50.45020000, 30.52350000, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(5, '550e8400-e29b-41d4-a716-446655440505', 50.45480000, 30.52400000, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

INSERT INTO location (id, external_id, erp_partner_id, name, address, description, company_external_id, is_public, is_active, directions, coordinates_id, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, '550e8400-e29b-41d4-a716-446655440101', 'ERP-PARTNER-001', 'Downtown Bike Hub', '123 Main Street, Kyiv', 'Central bike rental location', 'company-ext-001', true, true, 'Near metro station', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(2, '550e8400-e29b-41d4-a716-446655440102', 'ERP-PARTNER-002', 'Park Side Station', '456 Park Avenue, Kyiv', 'Bike rental near park', 'company-ext-001', true, true, 'Next to park entrance', 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(3, '550e8400-e29b-41d4-a716-446655440103', 'ERP-PARTNER-003', 'City Center Rentals', '789 Central Square, Lviv', 'Main rental location', 'company-ext-002', true, true, 'Central square', 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

INSERT INTO location_image (id, external_id, location_id, image_url, sort_order, is_thumbnail, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, '550e8400-e29b-41d4-a716-446655440111', 1, 'https://images.example.com/locations/downtown-1.jpg', 1, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(2, '550e8400-e29b-41d4-a716-446655440112', 2, 'https://images.example.com/locations/parkside-1.jpg', 1, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

INSERT INTO hub (id, external_id, name, location_id, directions, coordinates_id, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, '550e8400-e29b-41d4-a716-446655440201', 'Hub A - Main', 1, 'Main entrance', 4, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(2, '550e8400-e29b-41d4-a716-446655440202', 'Hub B - North', 1, 'North side', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(3, '550e8400-e29b-41d4-a716-446655440203', 'Hub A - Park', 2, 'Park gate', 5, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

INSERT INTO hub_image (id, external_id, hub_id, image_url, sort_order, is_thumbnail, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, '550e8400-e29b-41d4-a716-446655440211', 1, 'https://images.example.com/hubs/hub-a-1.jpg', 1, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(2, '550e8400-e29b-41d4-a716-446655440212', 3, 'https://images.example.com/hubs/hub-park-1.jpg', 1, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- =====================================================================================================================
-- SECTION 3: HARDWARE (LOCKS & KEYS)
-- =====================================================================================================================

INSERT INTO lock_entity (id, external_id, mac_address, lock_status_id, lock_provider_id, battery_level, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, '550e8400-e29b-41d4-a716-446655440601', 'AA:BB:CC:DD:EE:01', 1, 1, 95, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(2, '550e8400-e29b-41d4-a716-446655440602', 'AA:BB:CC:DD:EE:02', 1, 1, 87, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(3, '550e8400-e29b-41d4-a716-446655440603', 'AA:BB:CC:DD:EE:03', 1, 2, 92, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

INSERT INTO key_entity (id, external_id, lock_id, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, '550e8400-e29b-41d4-a716-446655440611', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(2, '550e8400-e29b-41d4-a716-446655440612', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(3, '550e8400-e29b-41d4-a716-446655440613', 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(4, '550e8400-e29b-41d4-a716-446655440614', 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- =====================================================================================================================
-- SECTION 4: PRODUCTS (JOINED Inheritance)
-- =====================================================================================================================

-- Bike Products
INSERT INTO product (id, external_id, product_type, is_b2b_rentable, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, '550e8400-e29b-41d4-a716-446655440401', 'BIKE', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(2, '550e8400-e29b-41d4-a716-446655440402', 'BIKE', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(3, '550e8400-e29b-41d4-a716-446655440403', 'BIKE', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

INSERT INTO bike (id, code, qr_code_url, vat, is_vat_include, in_service_date, hub_id, coordinates_id, frame_number, bike_status_id, battery_level, lock_id, bike_type_id, currency_external_id, bike_model_id, revenue_share_percent) VALUES
(1, 'BIKE-001', 'https://qr.example.com/bike/001', 20.00, true, CURRENT_DATE - INTERVAL '6 months', 1, 1, 'TREK-FX3-001', 1, 85, 1, 2, 'currency-ext-eur', 1001, 10.00),
(2, 'BIKE-002', 'https://qr.example.com/bike/002', 20.00, true, CURRENT_DATE - INTERVAL '6 months', 1, 2, 'TREK-FX3-002', 1, 92, 2, 2, 'currency-ext-eur', 1001, 10.00),
(3, 'BIKE-003', 'https://qr.example.com/bike/003', 20.00, true, CURRENT_DATE - INTERVAL '4 months', 2, 3, 'TREK-M7-001', 1, 100, 3, 2, 'currency-ext-eur', 1002, 10.00)
ON CONFLICT (id) DO NOTHING;

-- Charging Station Products
INSERT INTO product (id, external_id, product_type, is_b2b_rentable, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(101, '550e8400-e29b-41d4-a716-446655440404', 'CHARGING_STATION', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(102, '550e8400-e29b-41d4-a716-446655440405', 'CHARGING_STATION', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

INSERT INTO charging_station (id, code, qr_code_url, vat, is_vat_include, in_service_date, hub_id, coordinates_id, charging_station_status_id, charging_station_model_id, is_active) VALUES
(101, 'CS-001', 'https://qr.example.com/cs/001', 20.00, true, CURRENT_DATE, 1, 4, 1, 2001, true),
(102, 'CS-002', 'https://qr.example.com/cs/002', 20.00, true, CURRENT_DATE, 3, 5, 1, 2002, true)
ON CONFLICT (id) DO NOTHING;

-- Part Products
INSERT INTO product (id, external_id, product_type, is_b2b_rentable, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(201, '550e8400-e29b-41d4-a716-446655440406', 'PART', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(202, '550e8400-e29b-41d4-a716-446655440407', 'PART', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(203, '550e8400-e29b-41d4-a716-446655440408', 'PART', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

INSERT INTO part (id, vat, is_vat_include, name, part_brand_id, image_url, part_category_id, hub_id, b2b_sale_price, quantity) VALUES
(201, 20.00, true, 'Bosch PowerPack 500', 5, 'https://images.example.com/parts/battery-500.jpg', 1, 1, 450.00, 5),
(202, 20.00, true, 'Continental Contact Plus', 3, 'https://images.example.com/parts/tire-contact.jpg', 2, 1, 35.00, 20),
(203, 20.00, true, 'Schwalbe Marathon', 4, 'https://images.example.com/parts/tire-marathon.jpg', 2, 2, 42.00, 15)
ON CONFLICT (id) DO NOTHING;

-- =====================================================================================================================
-- SECTION 5: SERVICES
-- =====================================================================================================================

INSERT INTO service (id, external_id, name, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, '550e8400-e29b-41d4-a716-446655440701', 'Basic Tune-Up', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(2, '550e8400-e29b-41d4-a716-446655440702', 'Full Service', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(3, '550e8400-e29b-41d4-a716-446655440703', 'Tire Replacement', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- Service Products
INSERT INTO product (id, external_id, product_type, is_b2b_rentable, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(301, '550e8400-e29b-41d4-a716-446655440409', 'SERVICE_PRODUCT', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(302, '550e8400-e29b-41d4-a716-446655440410', 'SERVICE_PRODUCT', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

INSERT INTO service_product (id, service_id, related_product_id, b2b_subscription_price) VALUES
(301, 1, NULL, 25.00),
(302, 2, NULL, 75.00)
ON CONFLICT (id) DO NOTHING;

-- =====================================================================================================================
-- SECTION 6: RENTAL PLANS & ASSOCIATIONS
-- =====================================================================================================================

INSERT INTO rental_plan (id, external_id, name, rental_unit_id, min_unit, max_unit, location_id, default_price, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, '550e8400-e29b-41d4-a716-446655440711', 'Hourly Plan', 1, 1, 24, 1, 5.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(2, '550e8400-e29b-41d4-a716-446655440712', 'Half Day', 1, 4, 8, 1, 15.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(3, '550e8400-e29b-41d4-a716-446655440713', 'Full Day', 2, 1, 7, 1, 25.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(4, '550e8400-e29b-41d4-a716-446655440714', 'Weekly Plan', 3, 1, 4, 2, 120.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

INSERT INTO bike_model_rental_plan (id, external_id, bike_model_id, rental_plan_id, price, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, '550e8400-e29b-41d4-a716-446655440721', 1001, 1, 5.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(2, '550e8400-e29b-41d4-a716-446655440722', 1001, 2, 15.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(3, '550e8400-e29b-41d4-a716-446655440723', 1001, 3, 25.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(4, '550e8400-e29b-41d4-a716-446655440724', 1002, 1, 5.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(5, '550e8400-e29b-41d4-a716-446655440725', 1002, 3, 25.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

INSERT INTO user_location (id, external_id, user_external_id, location_id, location_role_id, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, '550e8400-e29b-41d4-a716-446655440901', 'usr-ext-00001', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(2, '550e8400-e29b-41d4-a716-446655440902', 'usr-ext-00002', 1, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(3, '550e8400-e29b-41d4-a716-446655440903', 'usr-ext-00003', 2, 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- =====================================================================================================================
-- SECTION 7: B2B SALES & SUBSCRIPTIONS
-- =====================================================================================================================

INSERT INTO b2b_sale (id, external_id, location_id, b2b_sale_status_id, seller_company_external_id, buyer_company_external_id, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, '550e8400-e29b-41d4-a716-446655440801', 1, 2, 'company-ext-001', 'company-ext-003', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(2, '550e8400-e29b-41d4-a716-446655440802', 1, 3, 'company-ext-001', 'company-ext-004', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

INSERT INTO b2b_sale_order (id, external_id, seller_company_external_id, buyer_company_external_id, b2b_sale_order_status_id, location_id, b2b_sale_id, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, '550e8400-e29b-41d4-a716-446655440811', 'company-001', 'company-002', 4, 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(2, '550e8400-e29b-41d4-a716-446655440812', 'company-002', 'company-001', 3, 2, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

INSERT INTO b2b_sale_item (id, external_id, b2b_sale_id, product_id, price, quantity, total_price, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, '550e8400-e29b-41d4-a716-446655440821', 1, 1, 800.00, 5, 800.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(2, '550e8400-e29b-41d4-a716-446655440822', 1, 2, 800.00, 5, 800.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(3, '550e8400-e29b-41d4-a716-446655440823', 2, 3, 950.00, 8, 950.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

INSERT INTO b2b_sale_order_item (id, external_id, b2b_sale_order_id, product_id, quantity, price, total_price, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, '550e8400-e29b-41d4-a716-446655440851', 1, 1, 5, 800.00, 4000.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(2, '550e8400-e29b-41d4-a716-446655440852', 2, 3, 10, 950.00, 9500.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

INSERT INTO b2b_subscription (id, external_id, location_id, end_date_time, b2b_subscription_status_id, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, '550e8400-e29b-41d4-a716-446655440831', 1, CURRENT_TIMESTAMP + INTERVAL '12 months', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(2, '550e8400-e29b-41d4-a716-446655440832', 2, CURRENT_TIMESTAMP + INTERVAL '24 months', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

INSERT INTO b2b_subscription_item (id, external_id, b2b_subscription_id, product_id, start_date_time, end_date_time, price, total_price, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, '550e8400-e29b-41d4-a716-446655440861', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '12 months', 50.00, 600.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(2, '550e8400-e29b-41d4-a716-446655440862', 1, 101, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '12 months', 99.00, 1188.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(3, '550e8400-e29b-41d4-a716-446655440863', 2, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '24 months', 75.00, 1800.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

INSERT INTO b2b_subscription_order (id, external_id, location_id, b2b_subscription_order_status_id, b2b_subscription_id, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, '550e8400-e29b-41d4-a716-446655440841', 1, 2, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(2, '550e8400-e29b-41d4-a716-446655440842', 2, 3, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

INSERT INTO b2b_subscription_order_item (id, external_id, b2b_subscription_order_id, product_id, quantity, price, total_price, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, '550e8400-e29b-41d4-a716-446655440871', 1, 1, 10, 50.00, 500.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(2, '550e8400-e29b-41d4-a716-446655440872', 2, 2, 20, 75.00, 1500.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- =====================================================================================================================
-- SECTION 8: RENTAL OPERATIONS
-- =====================================================================================================================

INSERT INTO rental (id, external_id, user_external_id, company_external_id, rental_status_id, erp_rental_order_id, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, 'rental-ext-00101', 'usr-ext-00007', 'company-ext-001', 3, 'ERP-RENT-001', CURRENT_TIMESTAMP - INTERVAL '2 days', CURRENT_TIMESTAMP, 'system', 'system', false),
(2, 'rental-ext-00102', 'usr-ext-00008', 'company-ext-001', 3, 'ERP-RENT-002', CURRENT_TIMESTAMP - INTERVAL '5 days', CURRENT_TIMESTAMP, 'system', 'system', false),
(3, 'rental-ext-00103', 'usr-ext-00009', 'company-ext-002', 2, 'ERP-RENT-003', CURRENT_TIMESTAMP - INTERVAL '1 hour', CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

INSERT INTO bike_rental (id, external_id, bike_id, location_id, rental_id, start_date_time, end_date_time, rental_unit_id, bike_rental_status_id, is_revenue_share_paid, photo_url, price, total_price, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, 'bike-rental-ext-00101', 1, 1, 1, '2025-12-05 10:00:00', '2025-12-05 18:00:00', 1, 2, false, NULL, 5.00, 40.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(2, 'bike-rental-ext-00102', 2, 1, 2, '2025-12-10 09:00:00', '2025-12-10 17:00:00', 1, 2, false, NULL, 5.00, 40.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(3, 'bike-rental-ext-00103', 3, 2, 2, '2025-12-15 14:00:00', '2025-12-15 20:00:00', 1, 2, false, NULL, 5.00, 30.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(4, 'bike-rental-ext-00104', 1, 1, 1, '2025-11-20 10:00:00', '2025-11-20 15:00:00', 1, 2, true, NULL, 5.00, 25.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

INSERT INTO bike_reservation (id, external_id, user_external_id, bike_id, start_date_time, end_date_time, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, 'reservation-ext-00101', 'usr-ext-00010', 3, CURRENT_TIMESTAMP + INTERVAL '2 hours', CURRENT_TIMESTAMP + INTERVAL '6 hours', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(2, 'reservation-ext-00102', 'usr-ext-00011', 1, CURRENT_TIMESTAMP + INTERVAL '1 day', CURRENT_TIMESTAMP + INTERVAL '2 days', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

INSERT INTO ride (id, external_id, bike_rental_id, start_date_time, end_date_time, start_location_id, end_location_id, start_coordinates_id, end_coordinates_id, ride_status_id, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, 'ride-ext-00101', 1, CURRENT_TIMESTAMP - INTERVAL '2 days', CURRENT_TIMESTAMP - INTERVAL '2 days' + INTERVAL '45 minutes', 1, 2, 1, 2, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(2, 'ride-ext-00102', 2, CURRENT_TIMESTAMP - INTERVAL '5 days', CURRENT_TIMESTAMP - INTERVAL '5 days' + INTERVAL '30 minutes', 2, 1, 2, 1, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- =====================================================================================================================
-- SECTION 9: INVENTORY & MAINTENANCE
-- =====================================================================================================================

INSERT INTO stock_movement (id, external_id, product_id, from_hub_id, to_hub_id, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, 'stock-mov-00101', 202, 1, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(2, 'stock-mov-00102', 203, 2, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

INSERT INTO bike_model_part (id, external_id, bike_model_id, part_id, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, '550e8400-e29b-41d4-a716-446655440731', 1001, 202, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(2, '550e8400-e29b-41d4-a716-446655440732', 1002, 203, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(3, '550e8400-e29b-41d4-a716-446655440733', 1003, 202, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- =====================================================================================================================
-- RE-ENABLE FORCE RLS AFTER DATA INSERTION
-- =====================================================================================================================
ALTER TABLE rental FORCE ROW LEVEL SECURITY;
ALTER TABLE location FORCE ROW LEVEL SECURITY;
ALTER TABLE b2b_sale FORCE ROW LEVEL SECURITY;
ALTER TABLE b2b_subscription FORCE ROW LEVEL SECURITY;
ALTER TABLE b2b_sale_order FORCE ROW LEVEL SECURITY;
ALTER TABLE bike_brand FORCE ROW LEVEL SECURITY;
ALTER TABLE charging_station_brand FORCE ROW LEVEL SECURITY;
ALTER TABLE part_brand FORCE ROW LEVEL SECURITY;

-- =====================================================================================================================
-- END OF SAMPLE DATA
-- =====================================================================================================================
