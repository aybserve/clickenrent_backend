-- =====================================================================================================================
-- RENTAL SERVICE - DATABASE INITIALIZATION
-- =====================================================================================================================
-- Module: rental-service
-- Database: PostgreSQL
-- Description: Production database initialization for the bike/equipment rental and fleet management service.
--              Contains required lookup data and minimal sample data for all tables.
-- 
-- Usage:
--   This file is automatically executed by Spring Boot on application startup when:
--   - spring.jpa.hibernate.ddl-auto is set to 'create', 'create-drop', or 'update'
--   - spring.jpa.properties.hibernate.hbm2ddl.import_files=/data.sql
--
-- Note: For production deployment, review and customize sample data as needed.
--
-- Author: Vitaliy Shvetsov
-- =====================================================================================================================

-- =====================================================================================================================
-- SECTION 0: POSTGIS SETUP (REQUIRED FOR LOCATION FEATURES)
-- =====================================================================================================================
-- Note: Using standard semicolon (;) as statement separator
-- =====================================================================================================================

-- Enable PostGIS extension for spatial operations
CREATE EXTENSION IF NOT EXISTS postgis;

-- Add geometry column to coordinates table (if not exists)
-- Note: This runs after Hibernate creates the table
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'coordinates' AND column_name = 'geom'
    ) THEN
        ALTER TABLE coordinates ADD COLUMN geom GEOGRAPHY(POINT, 4326);
    END IF;
END $$;

-- Create spatial index on geometry column (if not exists)
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_indexes 
        WHERE tablename = 'coordinates' AND indexname = 'idx_coordinates_geom'
    ) THEN
        CREATE INDEX idx_coordinates_geom ON coordinates USING GIST (geom);
    END IF;
END $$;

-- Create trigger function to automatically update geometry from latitude/longitude
CREATE OR REPLACE FUNCTION update_coordinates_geom()
RETURNS TRIGGER AS $$
BEGIN
    NEW.geom = ST_SetSRID(ST_MakePoint(NEW.longitude, NEW.latitude), 4326)::geography;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Create trigger (drop first if exists to avoid errors)
DROP TRIGGER IF EXISTS trg_coordinates_geom_update ON coordinates;

CREATE TRIGGER trg_coordinates_geom_update
    BEFORE INSERT OR UPDATE OF latitude, longitude
    ON coordinates
    FOR EACH ROW
    EXECUTE FUNCTION update_coordinates_geom();

-- Update existing coordinates to populate geometry column
UPDATE coordinates SET geom = ST_SetSRID(ST_MakePoint(longitude, latitude), 4326)::geography WHERE geom IS NULL;

-- =====================================================================================================================
-- SECTION 1: STATUS & LOOKUP TABLES (REQUIRED)
-- =====================================================================================================================

-- ---------------------------------------------------------------------------------------------------------------------
-- 1.1 BIKE TYPE (NO audit fields)
-- ---------------------------------------------------------------------------------------------------------------------
INSERT INTO bike_type (id, name) VALUES
(1, 'Electric'),
(2, 'Non-electric')
ON CONFLICT (id) DO NOTHING;

-- ---------------------------------------------------------------------------------------------------------------------
-- 1.2 BIKE STATUS (NO audit fields)
-- ---------------------------------------------------------------------------------------------------------------------
INSERT INTO bike_status (id, name) VALUES
(1, 'Available'),
(2, 'In use'),
(3, 'Reserved'),
(4, 'Broken'),
(5, 'Disabled')
ON CONFLICT (id) DO NOTHING;

-- ---------------------------------------------------------------------------------------------------------------------
-- 1.3 BIKE RENTAL STATUS (NO audit fields)
-- ---------------------------------------------------------------------------------------------------------------------
INSERT INTO bike_rental_status (id, name) VALUES
(1, 'Active'),
(2, 'Completed'),
(3, 'Cancelled')
ON CONFLICT (id) DO NOTHING;

-- ---------------------------------------------------------------------------------------------------------------------
-- 1.4 RENTAL STATUS (NO audit fields)
-- ---------------------------------------------------------------------------------------------------------------------
INSERT INTO rental_status (id, name) VALUES
(1, 'Pending'),
(2, 'Active'),
(3, 'Completed'),
(4, 'Cancelled')
ON CONFLICT (id) DO NOTHING;

-- ---------------------------------------------------------------------------------------------------------------------
-- 1.5 RENTAL UNIT (HAS audit fields)
-- ---------------------------------------------------------------------------------------------------------------------
INSERT INTO rental_unit (id, name, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, 'Hour', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(2, 'Day', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(3, 'Week', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(4, 'Month', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- ---------------------------------------------------------------------------------------------------------------------
-- 1.6 RIDE STATUS (NO audit fields)
-- ---------------------------------------------------------------------------------------------------------------------
INSERT INTO ride_status (id, name) VALUES
(1, 'Active'),
(2, 'Finished'),
(3, 'Paused')
ON CONFLICT (id) DO NOTHING;

-- ---------------------------------------------------------------------------------------------------------------------
-- 1.7 LOCATION ROLE (NO audit fields)
-- ---------------------------------------------------------------------------------------------------------------------
INSERT INTO location_role (id, name) VALUES
(1, 'Admin'),
(2, 'Manager'),
(3, 'Staff'),
(4, 'Viewer')
ON CONFLICT (id) DO NOTHING;

-- ---------------------------------------------------------------------------------------------------------------------
-- 1.8 B2B SALE STATUS (NO audit fields)
-- ---------------------------------------------------------------------------------------------------------------------
INSERT INTO b2b_sale_status (id, name) VALUES
(1, 'Draft'),
(2, 'Active'),
(3, 'Completed'),
(4, 'Cancelled')
ON CONFLICT (id) DO NOTHING;

-- ---------------------------------------------------------------------------------------------------------------------
-- 1.9 B2B SALE ORDER STATUS (NO audit fields)
-- ---------------------------------------------------------------------------------------------------------------------
INSERT INTO b2b_sale_order_status (id, name) VALUES
(1, 'Pending'),
(2, 'Confirmed'),
(3, 'Shipped'),
(4, 'Delivered'),
(5, 'Cancelled')
ON CONFLICT (id) DO NOTHING;

-- ---------------------------------------------------------------------------------------------------------------------
-- 1.10 B2B SUBSCRIPTION STATUS (NO audit fields)
-- ---------------------------------------------------------------------------------------------------------------------
INSERT INTO b2b_subscription_status (id, name) VALUES
(1, 'Active'),
(2, 'Inactive'),
(3, 'Suspended'),
(4, 'Cancelled')
ON CONFLICT (id) DO NOTHING;

-- ---------------------------------------------------------------------------------------------------------------------
-- 1.11 B2B SUBSCRIPTION ORDER STATUS (NO audit fields)
-- ---------------------------------------------------------------------------------------------------------------------
INSERT INTO b2b_subscription_order_status (id, name) VALUES
(1, 'Pending'),
(2, 'Confirmed'),
(3, 'Fulfilled'),
(4, 'Cancelled')
ON CONFLICT (id) DO NOTHING;

-- ---------------------------------------------------------------------------------------------------------------------
-- 1.12 CHARGING STATION STATUS (NO audit fields)
-- ---------------------------------------------------------------------------------------------------------------------
INSERT INTO charging_station_status (id, name) VALUES
(1, 'Available'),
(2, 'In use'),
(3, 'Broken'),
(4, 'Disabled')
ON CONFLICT (id) DO NOTHING;

-- ---------------------------------------------------------------------------------------------------------------------
-- 1.13 LOCK STATUS (NO audit fields)
-- ---------------------------------------------------------------------------------------------------------------------
INSERT INTO lock_status (id, name) VALUES
(1, 'Locked'),
(2, 'Unlocked'),
(3, 'Unknown')
ON CONFLICT (id) DO NOTHING;

-- ---------------------------------------------------------------------------------------------------------------------
-- 1.14 LOCK PROVIDER (NO audit fields)
-- ---------------------------------------------------------------------------------------------------------------------
INSERT INTO lock_provider (id, name, api_endpoint, is_active) VALUES
(1, 'AXA', 'https://api.axa-locks.com', true),
(2, 'OMNI', 'https://api.omni-locks.com', true),
(3, 'Generic BLE', NULL, true)
ON CONFLICT (id) DO NOTHING;

-- =====================================================================================================================
-- SECTION 2: BRANDS & MODELS
-- =====================================================================================================================

-- ---------------------------------------------------------------------------------------------------------------------
-- 2.1 BIKE BRANDS (NO audit fields)
-- ---------------------------------------------------------------------------------------------------------------------
INSERT INTO bike_brand (id, external_id, name, company_external_id) VALUES
(1, '550e8400-e29b-41d4-a716-446655440001', 'Trek', 'company-ext-001'),
(2, '550e8400-e29b-41d4-a716-446655440002', 'Giant', 'company-ext-001'),
(3, '550e8400-e29b-41d4-a716-446655440003', 'Specialized', 'company-ext-001'),
(4, '550e8400-e29b-41d4-a716-446655440004', 'Cannondale', 'company-ext-001'),
(5, '550e8400-e29b-41d4-a716-446655440005', 'Scott', 'company-ext-002')
ON CONFLICT (id) DO NOTHING;

-- ---------------------------------------------------------------------------------------------------------------------
-- 2.2 BIKE ENGINES (NO audit fields)
-- ---------------------------------------------------------------------------------------------------------------------
INSERT INTO bike_engine (id, external_id, name) VALUES
(1, '550e8400-e29b-41d4-a716-446655440031', 'Bosch Performance CX'),
(2, '550e8400-e29b-41d4-a716-446655440032', 'Shimano EP8'),
(3, '550e8400-e29b-41d4-a716-446655440033', 'Yamaha PW-X3'),
(4, '550e8400-e29b-41d4-a716-446655440034', 'Brose Drive S Mag')
ON CONFLICT (id) DO NOTHING;

-- ---------------------------------------------------------------------------------------------------------------------
-- 2.3 BIKE MODELS (HAS audit fields)
-- ---------------------------------------------------------------------------------------------------------------------
INSERT INTO bike_model (id, external_id, name, bike_brand_id, bike_type_id, bike_engine_id, image_url, b2b_sale_price, b2b_subscription_price, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, '550e8400-e29b-41d4-a716-446655440301', 'FX 3 Disc', 1, 2, NULL, 'https://images.example.com/bikes/trek-fx3.jpg', 800.00, 50.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(2, '550e8400-e29b-41d4-a716-446655440302', 'Marlin 7', 1, 2, NULL, 'https://images.example.com/bikes/trek-marlin7.jpg', 950.00, 75.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(3, '550e8400-e29b-41d4-a716-446655440303', 'Escape 3', 2, 2, NULL, 'https://images.example.com/bikes/giant-escape3.jpg', 600.00, 40.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(4, '550e8400-e29b-41d4-a716-446655440304', 'Turbo Vado', 3, 1, 1, 'https://images.example.com/bikes/specialized-vado.jpg', 999.00, 99.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(5, '550e8400-e29b-41d4-a716-446655440305', 'Rail 9.9', 1, 1, 1, 'https://images.example.com/bikes/trek-rail.jpg', 999.00, 99.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- ---------------------------------------------------------------------------------------------------------------------
-- 2.4 PART BRANDS (NO audit fields, requires company_external_id)
-- ---------------------------------------------------------------------------------------------------------------------
INSERT INTO part_brand (id, external_id, name, company_external_id) VALUES
(1, '550e8400-e29b-41d4-a716-446655440011', 'Shimano', 'company-001'),
(2, '550e8400-e29b-41d4-a716-446655440012', 'SRAM', 'company-001'),
(3, '550e8400-e29b-41d4-a716-446655440013', 'Continental', 'company-001'),
(4, '550e8400-e29b-41d4-a716-446655440014', 'Schwalbe', 'company-002'),
(5, '550e8400-e29b-41d4-a716-446655440015', 'Bosch', 'company-002')
ON CONFLICT (id) DO NOTHING;

-- ---------------------------------------------------------------------------------------------------------------------
-- 2.5 PART CATEGORIES (NO audit fields)
-- ---------------------------------------------------------------------------------------------------------------------
INSERT INTO part_category (id, external_id, name, parent_category_id) VALUES
(1, '550e8400-e29b-41d4-a716-446655440041', 'Battery', NULL),
(2, '550e8400-e29b-41d4-a716-446655440042', 'Tire', NULL),
(3, '550e8400-e29b-41d4-a716-446655440043', 'Lock', NULL),
(4, '550e8400-e29b-41d4-a716-446655440044', 'Saddle', NULL),
(5, '550e8400-e29b-41d4-a716-446655440045', 'Pedals', NULL)
ON CONFLICT (id) DO NOTHING;

-- ---------------------------------------------------------------------------------------------------------------------
-- 2.6 CHARGING STATION BRANDS (NO audit fields, requires company_external_id)
-- ---------------------------------------------------------------------------------------------------------------------
INSERT INTO charging_station_brand (id, external_id, name, company_external_id) VALUES
(1, '550e8400-e29b-41d4-a716-446655440021', 'Bosch', 'company-001'),
(2, '550e8400-e29b-41d4-a716-446655440022', 'Shimano STEPS', 'company-001'),
(3, '550e8400-e29b-41d4-a716-446655440023', 'Yamaha', 'company-002')
ON CONFLICT (id) DO NOTHING;

-- ---------------------------------------------------------------------------------------------------------------------
-- 2.7 CHARGING STATION MODELS (HAS audit fields, requires b2b_sale_price and b2b_subscription_price)
-- ---------------------------------------------------------------------------------------------------------------------
INSERT INTO charging_station_model (id, external_id, name, charging_station_brand_id, image_url, b2b_sale_price, b2b_subscription_price, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, '550e8400-e29b-41d4-a716-446655440051', 'PowerPack 500', 1, 'https://images.example.com/chargers/bosch-500.jpg', 450.00, 30.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(2, '550e8400-e29b-41d4-a716-446655440052', 'PowerTube 625', 1, 'https://images.example.com/chargers/bosch-625.jpg', 550.00, 35.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(3, '550e8400-e29b-41d4-a716-446655440053', 'BT-E8036', 2, 'https://images.example.com/chargers/shimano-e8036.jpg', 480.00, 32.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- =====================================================================================================================
-- SECTION 3: GEOGRAPHY & LOCATIONS
-- =====================================================================================================================

-- ---------------------------------------------------------------------------------------------------------------------
-- 3.1 COORDINATES (HAS audit fields)
-- ---------------------------------------------------------------------------------------------------------------------
INSERT INTO coordinates (id, latitude, longitude, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, 50.45010000, 30.52340000, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(2, 50.45470000, 30.52380000, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(3, 49.83970000, 24.02970000, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(4, 50.45020000, 30.52350000, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(5, 50.45480000, 30.52400000, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- ---------------------------------------------------------------------------------------------------------------------
-- 3.2 LOCATIONS (HAS audit fields)
-- ---------------------------------------------------------------------------------------------------------------------
INSERT INTO location (id, external_id, erp_partner_id, name, address, description, company_external_id, is_public, directions, coordinates_id, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, '550e8400-e29b-41d4-a716-446655440101', 'ERP-PARTNER-001', 'Downtown Bike Hub', '123 Main Street, Kyiv', 'Central bike rental location', 'company-ext-001', true, 'Near metro station', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(2, '550e8400-e29b-41d4-a716-446655440102', 'ERP-PARTNER-002', 'Park Side Station', '456 Park Avenue, Kyiv', 'Bike rental near park', 'company-ext-001', true, 'Next to park entrance', 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(3, '550e8400-e29b-41d4-a716-446655440103', 'ERP-PARTNER-003', 'City Center Rentals', '789 Central Square, Lviv', 'Main rental location', 'company-ext-002', true, 'Central square', 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- ---------------------------------------------------------------------------------------------------------------------
-- 3.3 LOCATION IMAGES (HAS audit fields)
-- ---------------------------------------------------------------------------------------------------------------------
INSERT INTO location_image (id, external_id, location_id, image_url, sort_order, is_thumbnail, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, '550e8400-e29b-41d4-a716-446655440111', 1, 'https://images.example.com/locations/downtown-1.jpg', 1, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(2, '550e8400-e29b-41d4-a716-446655440112', 2, 'https://images.example.com/locations/parkside-1.jpg', 1, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- ---------------------------------------------------------------------------------------------------------------------
-- 3.4 HUBS (HAS audit fields)
-- ---------------------------------------------------------------------------------------------------------------------
INSERT INTO hub (id, external_id, name, location_id, directions, coordinates_id, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, '550e8400-e29b-41d4-a716-446655440201', 'Hub A - Main', 1, 'Main entrance', 4, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(2, '550e8400-e29b-41d4-a716-446655440202', 'Hub B - North', 1, 'North side', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(3, '550e8400-e29b-41d4-a716-446655440203', 'Hub A - Park', 2, 'Park gate', 5, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- ---------------------------------------------------------------------------------------------------------------------
-- 3.5 HUB IMAGES (HAS audit fields)
-- ---------------------------------------------------------------------------------------------------------------------
INSERT INTO hub_image (id, external_id, hub_id, image_url, sort_order, is_thumbnail, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, '550e8400-e29b-41d4-a716-446655440211', 1, 'https://images.example.com/hubs/hub-a-1.jpg', 1, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(2, '550e8400-e29b-41d4-a716-446655440212', 3, 'https://images.example.com/hubs/hub-park-1.jpg', 1, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- =====================================================================================================================
-- SECTION 4: HARDWARE (LOCKS & KEYS)
-- =====================================================================================================================

-- ---------------------------------------------------------------------------------------------------------------------
-- 4.1 LOCK ENTITIES (HAS audit fields)
-- ---------------------------------------------------------------------------------------------------------------------
INSERT INTO lock_entity (id, external_id, mac_address, lock_status_id, lock_provider_id, battery_level, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, '550e8400-e29b-41d4-a716-446655440601', 'AA:BB:CC:DD:EE:01', 1, 1, 95, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(2, '550e8400-e29b-41d4-a716-446655440602', 'AA:BB:CC:DD:EE:02', 1, 1, 87, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(3, '550e8400-e29b-41d4-a716-446655440603', 'AA:BB:CC:DD:EE:03', 1, 2, 92, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- ---------------------------------------------------------------------------------------------------------------------
-- 4.2 KEY ENTITIES (HAS audit fields)
-- ---------------------------------------------------------------------------------------------------------------------
INSERT INTO key_entity (id, external_id, lock_id, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, '550e8400-e29b-41d4-a716-446655440611', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(2, '550e8400-e29b-41d4-a716-446655440612', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(3, '550e8400-e29b-41d4-a716-446655440613', 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(4, '550e8400-e29b-41d4-a716-446655440614', 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- =====================================================================================================================
-- SECTION 5: PRODUCTS (SINGLE_TABLE Inheritance)
-- =====================================================================================================================

-- ---------------------------------------------------------------------------------------------------------------------
-- 5.1 BIKE PRODUCTS (Product extends BaseAuditEntity)
-- Fields: code, qr_code_url, vat, is_vat_include, in_service_date, hub_id, coordinates_id,
--         frame_number, bike_status_id, battery_level, lock_id, bike_type_id, 
--         currency_external_id, bike_model_id, revenue_share_percent
-- Note: Due to SINGLE_TABLE inheritance, Part's required fields must be provided: name, b2b_sale_price, part_brand_id, part_category_id
-- ---------------------------------------------------------------------------------------------------------------------
INSERT INTO product (id, external_id, product_type, is_b2b_rentable, code, qr_code_url, vat, is_vat_include, in_service_date, hub_id, coordinates_id, frame_number, bike_status_id, battery_level, lock_id, bike_type_id, currency_external_id, bike_model_id, revenue_share_percent, name, b2b_sale_price, part_brand_id, part_category_id, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, '550e8400-e29b-41d4-a716-446655440401', 'BIKE', true, 'BIKE-001', 'https://qr.example.com/bike/001', 20.00, true, CURRENT_DATE - INTERVAL '6 months', 1, NULL, 'TREK-FX3-001', 1, 85, 1, 2, 'currency-ext-eur', 1, 10.00, 'Trek FX 3 Disc #001', 0.00, 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(2, '550e8400-e29b-41d4-a716-446655440402', 'BIKE', true, 'BIKE-002', 'https://qr.example.com/bike/002', 20.00, true, CURRENT_DATE - INTERVAL '6 months', 1, NULL, 'TREK-FX3-002', 1, 92, 2, 2, 'currency-ext-eur', 1, 10.00, 'Trek FX 3 Disc #002', 0.00, 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(3, '550e8400-e29b-41d4-a716-446655440403', 'BIKE', true, 'BIKE-003', 'https://qr.example.com/bike/003', 20.00, true, CURRENT_DATE - INTERVAL '4 months', 2, NULL, 'TREK-M7-001', 1, 100, 3, 2, 'currency-ext-eur', 2, 10.00, 'Trek Marlin 7 #001', 0.00, 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- ---------------------------------------------------------------------------------------------------------------------
-- 5.2 CHARGING STATION PRODUCTS
-- Fields: code, qr_code_url, vat, is_vat_include, in_service_date, hub_id, coordinates_id,
--         charging_station_status_id, charging_station_model_id, is_active
-- Note: Due to SINGLE_TABLE inheritance, Part's required fields must be provided: name, b2b_sale_price, part_brand_id, part_category_id
--       Also need to provide battery_level with default 0 for non-bike products
-- ---------------------------------------------------------------------------------------------------------------------
INSERT INTO product (id, external_id, product_type, is_b2b_rentable, code, qr_code_url, vat, is_vat_include, in_service_date, hub_id, coordinates_id, charging_station_status_id, charging_station_model_id, is_active, battery_level, name, b2b_sale_price, part_brand_id, part_category_id, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(4, '550e8400-e29b-41d4-a716-446655440404', 'CHARGING_STATION', false, 'CS-001', 'https://qr.example.com/cs/001', 20.00, true, CURRENT_DATE, 1, 4, 1, 1, true, 0, 'Charging Station #001', 0.00, 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(5, '550e8400-e29b-41d4-a716-446655440405', 'CHARGING_STATION', false, 'CS-002', 'https://qr.example.com/cs/002', 20.00, true, CURRENT_DATE, 3, 5, 1, 2, true, 0, 'Charging Station #002', 0.00, 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- ---------------------------------------------------------------------------------------------------------------------
-- 5.3 PART PRODUCTS
-- Fields: vat, is_vat_include, name, part_brand_id, image_url, part_category_id, hub_id, b2b_sale_price, quantity
-- Note: Due to SINGLE_TABLE inheritance, Bike's required field 'code' must be provided
--       Also need to provide battery_level with default 0 for non-bike products
-- ---------------------------------------------------------------------------------------------------------------------
INSERT INTO product (id, external_id, product_type, is_b2b_rentable, code, vat, is_vat_include, battery_level, name, part_brand_id, image_url, part_category_id, hub_id, b2b_sale_price, quantity, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(6, '550e8400-e29b-41d4-a716-446655440406', 'PART', false, 'PART-001', 20.00, true, 0, 'Bosch PowerPack 500', 5, 'https://images.example.com/parts/battery-500.jpg', 1, 1, 450.00, 5, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(7, '550e8400-e29b-41d4-a716-446655440407', 'PART', false, 'PART-002', 20.00, true, 0, 'Continental Contact Plus', 3, 'https://images.example.com/parts/tire-contact.jpg', 2, 1, 35.00, 20, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(8, '550e8400-e29b-41d4-a716-446655440408', 'PART', false, 'PART-003', 20.00, true, 0, 'Schwalbe Marathon', 4, 'https://images.example.com/parts/tire-marathon.jpg', 2, 2, 42.00, 15, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- ---------------------------------------------------------------------------------------------------------------------
-- 5.4 SERVICE PRODUCTS
-- Fields: service_id, product_id
-- Note: ServiceProduct entity does NOT have name field
-- ---------------------------------------------------------------------------------------------------------------------
-- We'll insert services first, then service products

-- =====================================================================================================================
-- SECTION 6: SERVICES
-- =====================================================================================================================

-- ---------------------------------------------------------------------------------------------------------------------
-- 6.1 SERVICES (HAS audit fields, only has name and b2b_subscription_price)
-- ---------------------------------------------------------------------------------------------------------------------
INSERT INTO service (id, name, b2b_subscription_price, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, 'Basic Tune-Up', 25.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(2, 'Full Service', 75.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(3, 'Tire Replacement', 30.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- ---------------------------------------------------------------------------------------------------------------------
-- 6.2 SERVICE PRODUCTS (service_id field)
-- Note: Due to SINGLE_TABLE inheritance, must provide ALL required fields from ALL subclasses
--       Also need to provide battery_level with default 0 for non-bike products
-- ---------------------------------------------------------------------------------------------------------------------
INSERT INTO product (id, external_id, product_type, is_b2b_rentable, code, battery_level, name, b2b_sale_price, part_brand_id, part_category_id, service_id, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(9, '550e8400-e29b-41d4-a716-446655440409', 'SERVICE_PRODUCT', false, 'SVC-001', 0, 'Basic Maintenance Service', 0.00, 1, 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(10, '550e8400-e29b-41d4-a716-446655440410', 'SERVICE_PRODUCT', false, 'SVC-002', 0, 'Full Tune-Up Service', 0.00, 1, 1, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- =====================================================================================================================
-- SECTION 7: RENTAL PLANS & ASSOCIATIONS
-- =====================================================================================================================

-- ---------------------------------------------------------------------------------------------------------------------
-- 7.1 RENTAL PLANS (NO audit fields)
-- ---------------------------------------------------------------------------------------------------------------------
INSERT INTO rental_plan (id, name, rental_unit_id, min_unit, max_unit, location_id, default_price) VALUES
(1, 'Hourly Plan', 1, 1, 24, 1, 5.00),
(2, 'Half Day', 1, 4, 8, 1, 15.00),
(3, 'Full Day', 2, 1, 7, 1, 25.00),
(4, 'Weekly Plan', 3, 1, 4, 2, 120.00)
ON CONFLICT (id) DO NOTHING;

-- ---------------------------------------------------------------------------------------------------------------------
-- 7.2 BIKE MODEL RENTAL PLAN (HAS audit fields, needs price field)
-- ---------------------------------------------------------------------------------------------------------------------
INSERT INTO bike_model_rental_plan (id, bike_model_id, rental_plan_id, price, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, 1, 1, 5.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(2, 1, 2, 15.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(3, 1, 3, 25.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(4, 2, 1, 5.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(5, 2, 3, 25.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- ---------------------------------------------------------------------------------------------------------------------
-- 7.3 USER LOCATION (NO audit fields)
-- ---------------------------------------------------------------------------------------------------------------------
INSERT INTO user_location (id, external_id, user_external_id, location_id, location_role_id) VALUES
(1, '550e8400-e29b-41d4-a716-446655440901', 'usr-ext-00001', 1, 1),
(2, '550e8400-e29b-41d4-a716-446655440902', 'usr-ext-00002', 1, 2),
(3, '550e8400-e29b-41d4-a716-446655440903', 'usr-ext-00003', 2, 3)
ON CONFLICT (id) DO NOTHING;

-- =====================================================================================================================
-- SECTION 8: B2B SALES & SUBSCRIPTIONS
-- =====================================================================================================================

-- ---------------------------------------------------------------------------------------------------------------------
-- 8.1 B2B SALES (HAS audit fields)
-- ---------------------------------------------------------------------------------------------------------------------
INSERT INTO b2b_sale (id, external_id, location_id, b2b_sale_status_id, date_time, seller_company_external_id, buyer_company_external_id, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, '550e8400-e29b-41d4-a716-446655440801', 1, 2, CURRENT_TIMESTAMP - INTERVAL '30 days', 'company-ext-001', 'company-ext-003', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(2, '550e8400-e29b-41d4-a716-446655440802', 1, 3, CURRENT_TIMESTAMP - INTERVAL '60 days', 'company-ext-001', 'company-ext-004', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- ---------------------------------------------------------------------------------------------------------------------
-- 8.2 B2B SALE ORDERS (HAS audit fields)
-- ---------------------------------------------------------------------------------------------------------------------
INSERT INTO b2b_sale_order (id, external_id, seller_company_external_id, buyer_company_external_id, b2b_sale_order_status_id, location_id, b2b_sale_id, date_time, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, '550e8400-e29b-41d4-a716-446655440811', 'company-001', 'company-002', 4, 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(2, '550e8400-e29b-41d4-a716-446655440812', 'company-002', 'company-001', 3, 2, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- ---------------------------------------------------------------------------------------------------------------------
-- 8.3 B2B SALE PRODUCTS (NO audit fields)
-- ---------------------------------------------------------------------------------------------------------------------
INSERT INTO b2b_sale_product (id, external_id, b2b_sale_id, product_id, price, quantity, total_price) VALUES
(1, '550e8400-e29b-41d4-a716-446655440821', 1, 1, 800.00, 5, 800.00),
(2, '550e8400-e29b-41d4-a716-446655440822', 1, 2, 800.00, 5, 800.00),
(3, '550e8400-e29b-41d4-a716-446655440823', 2, 3, 950.00, 8, 950.00)
ON CONFLICT (id) DO NOTHING;

-- ---------------------------------------------------------------------------------------------------------------------
-- 8.4 B2B SALE ORDER PRODUCT MODELS (HAS audit fields, uses product_id not bike_model_id, price and total_price not unit_price)
-- ---------------------------------------------------------------------------------------------------------------------
INSERT INTO b2b_sale_order_product_model (id, external_id, b2b_sale_order_id, product_id, quantity, price, total_price, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, '550e8400-e29b-41d4-a716-446655440851', 1, 1, 5, 800.00, 4000.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(2, '550e8400-e29b-41d4-a716-446655440852', 2, 3, 10, 950.00, 9500.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- ---------------------------------------------------------------------------------------------------------------------
-- 8.5 B2B SUBSCRIPTIONS (HAS audit fields)
-- ---------------------------------------------------------------------------------------------------------------------
INSERT INTO b2b_subscription (id, external_id, location_id, end_date_time, b2b_subscription_status_id, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, '550e8400-e29b-41d4-a716-446655440831', 1, CURRENT_TIMESTAMP + INTERVAL '12 months', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(2, '550e8400-e29b-41d4-a716-446655440832', 2, CURRENT_TIMESTAMP + INTERVAL '24 months', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- ---------------------------------------------------------------------------------------------------------------------
-- 8.6 B2B SUBSCRIPTION ITEMS (NO audit fields, uses product_id, start_date_time, end_date_time, price, total_price)
-- ---------------------------------------------------------------------------------------------------------------------
INSERT INTO b2b_subscription_item (id, external_id, b2b_subscription_id, product_id, start_date_time, end_date_time, price, total_price) VALUES
(1, '550e8400-e29b-41d4-a716-446655440861', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '12 months', 50.00, 600.00),
(2, '550e8400-e29b-41d4-a716-446655440862', 1, 4, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '12 months', 99.00, 1188.00),
(3, '550e8400-e29b-41d4-a716-446655440863', 2, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '24 months', 75.00, 1800.00)
ON CONFLICT (id) DO NOTHING;

-- ---------------------------------------------------------------------------------------------------------------------
-- 8.7 B2B SUBSCRIPTION ORDERS (HAS audit fields)
-- ---------------------------------------------------------------------------------------------------------------------
INSERT INTO b2b_subscription_order (id, external_id, location_id, date_time, b2b_subscription_order_status_id, b2b_subscription_id, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, '550e8400-e29b-41d4-a716-446655440841', 1, CURRENT_TIMESTAMP, 2, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(2, '550e8400-e29b-41d4-a716-446655440842', 2, CURRENT_TIMESTAMP, 3, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- ---------------------------------------------------------------------------------------------------------------------
-- 8.8 B2B SUBSCRIPTION ORDER ITEMS (HAS audit fields, uses product_id not bike_model_id, price and total_price not unit_price)
-- ---------------------------------------------------------------------------------------------------------------------
INSERT INTO b2b_subscription_order_item (id, external_id, b2b_subscription_order_id, product_id, quantity, price, total_price, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, '550e8400-e29b-41d4-a716-446655440871', 1, 1, 10, 50.00, 500.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(2, '550e8400-e29b-41d4-a716-446655440872', 2, 2, 20, 75.00, 1500.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- =====================================================================================================================
-- SECTION 9: RENTAL OPERATIONS
-- =====================================================================================================================

-- ---------------------------------------------------------------------------------------------------------------------
-- 9.1 RENTALS (HAS audit fields)
-- ---------------------------------------------------------------------------------------------------------------------
INSERT INTO rental (id, external_id, user_external_id, company_external_id, rental_status_id, erp_rental_order_id, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, 'rental-ext-00101', 'usr-ext-00007', 'company-ext-001', 3, 'ERP-RENT-001', CURRENT_TIMESTAMP - INTERVAL '2 days', CURRENT_TIMESTAMP, 'system', 'system', false),
(2, 'rental-ext-00102', 'usr-ext-00008', 'company-ext-001', 3, 'ERP-RENT-002', CURRENT_TIMESTAMP - INTERVAL '5 days', CURRENT_TIMESTAMP, 'system', 'system', false),
(3, 'rental-ext-00103', 'usr-ext-00009', 'company-ext-002', 2, 'ERP-RENT-003', CURRENT_TIMESTAMP - INTERVAL '1 hour', CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- ---------------------------------------------------------------------------------------------------------------------
-- 9.2 BIKE RENTALS (HAS audit fields)
-- ---------------------------------------------------------------------------------------------------------------------
INSERT INTO bike_rental (id, external_id, bike_id, bike_external_id, location_id, location_external_id, rental_id, rental_external_id, start_date_time, end_date_time, rental_unit_id, bike_rental_status_id, is_revenue_share_paid, photo_url, price, total_price, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, 'bike-rental-ext-00101', 1, '550e8400-e29b-41d4-a716-446655440401', 1, '550e8400-e29b-41d4-a716-446655440101', 1, 'rental-ext-00101', CURRENT_TIMESTAMP - INTERVAL '2 days', CURRENT_TIMESTAMP - INTERVAL '1 day', 2, 2, true, NULL, 25.00, 25.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(2, 'bike-rental-ext-00102', 2, '550e8400-e29b-41d4-a716-446655440402', 1, '550e8400-e29b-41d4-a716-446655440101', 2, 'rental-ext-00102', CURRENT_TIMESTAMP - INTERVAL '5 days', CURRENT_TIMESTAMP - INTERVAL '1 day', 1, 2, true, NULL, 5.00, 20.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- ---------------------------------------------------------------------------------------------------------------------
-- 9.3 BIKE RESERVATIONS (HAS audit fields)
-- ---------------------------------------------------------------------------------------------------------------------
INSERT INTO bike_reservation (id, external_id, user_external_id, bike_id, start_date_time, end_date_time, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, 'reservation-ext-00101', 'usr-ext-00010', 3, CURRENT_TIMESTAMP + INTERVAL '2 hours', CURRENT_TIMESTAMP + INTERVAL '6 hours', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(2, 'reservation-ext-00102', 'usr-ext-00011', 1, CURRENT_TIMESTAMP + INTERVAL '1 day', CURRENT_TIMESTAMP + INTERVAL '2 days', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- ---------------------------------------------------------------------------------------------------------------------
-- 9.4 RIDES (HAS audit fields)
-- NOTE: Updated to use start_coordinates_id and end_coordinates_id instead of coordinates_id
-- ---------------------------------------------------------------------------------------------------------------------
INSERT INTO ride (id, external_id, bike_rental_id, start_date_time, end_date_time, start_location_id, end_location_id, start_coordinates_id, end_coordinates_id, ride_status_id, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, 'ride-ext-00101', 1, CURRENT_TIMESTAMP - INTERVAL '2 days', CURRENT_TIMESTAMP - INTERVAL '2 days' + INTERVAL '45 minutes', 1, 2, 1, 2, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(2, 'ride-ext-00102', 2, CURRENT_TIMESTAMP - INTERVAL '5 days', CURRENT_TIMESTAMP - INTERVAL '5 days' + INTERVAL '30 minutes', 2, 1, 2, 1, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- =====================================================================================================================
-- SECTION 10: INVENTORY & MAINTENANCE
-- =====================================================================================================================

-- ---------------------------------------------------------------------------------------------------------------------
-- 10.1 STOCK MOVEMENTS (HAS audit fields)
-- ---------------------------------------------------------------------------------------------------------------------
INSERT INTO stock_movement (id, external_id, product_id, from_hub_id, to_hub_id, date_time, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, 'stock-mov-00101', 7, 1, 2, CURRENT_TIMESTAMP - INTERVAL '3 days', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(2, 'stock-mov-00102', 8, 2, 1, CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- ---------------------------------------------------------------------------------------------------------------------
-- 10.2 BIKE PARTS - Table does not exist (no BikePart entity)
-- ---------------------------------------------------------------------------------------------------------------------
-- Note: There is no bike_part table. The relationship is handled through BikeModelPart entity.

-- ---------------------------------------------------------------------------------------------------------------------
-- 10.3 BIKE MODEL PARTS (HAS audit fields)
-- ---------------------------------------------------------------------------------------------------------------------
INSERT INTO bike_model_part (id, bike_model_id, part_id, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, 1, 7, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(2, 2, 8, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(3, 3, 7, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- =====================================================================================================================
-- SECTION 11: SEQUENCE RESET
-- =====================================================================================================================

SELECT setval('bike_type_id_seq', (SELECT COALESCE(MAX(id), 1) FROM bike_type));
SELECT setval('bike_status_id_seq', (SELECT COALESCE(MAX(id), 1) FROM bike_status));
SELECT setval('bike_rental_status_id_seq', (SELECT COALESCE(MAX(id), 1) FROM bike_rental_status));
SELECT setval('rental_status_id_seq', (SELECT COALESCE(MAX(id), 1) FROM rental_status));
SELECT setval('rental_unit_id_seq', (SELECT COALESCE(MAX(id), 1) FROM rental_unit));
SELECT setval('ride_status_id_seq', (SELECT COALESCE(MAX(id), 1) FROM ride_status));
SELECT setval('location_role_id_seq', (SELECT COALESCE(MAX(id), 1) FROM location_role));
SELECT setval('b2b_sale_status_id_seq', (SELECT COALESCE(MAX(id), 1) FROM b2b_sale_status));
SELECT setval('b2b_sale_order_status_id_seq', (SELECT COALESCE(MAX(id), 1) FROM b2b_sale_order_status));
SELECT setval('b2b_subscription_status_id_seq', (SELECT COALESCE(MAX(id), 1) FROM b2b_subscription_status));
SELECT setval('b2b_subscription_order_status_id_seq', (SELECT COALESCE(MAX(id), 1) FROM b2b_subscription_order_status));
SELECT setval('charging_station_status_id_seq', (SELECT COALESCE(MAX(id), 1) FROM charging_station_status));
SELECT setval('lock_status_id_seq', (SELECT COALESCE(MAX(id), 1) FROM lock_status));
SELECT setval('lock_provider_id_seq', (SELECT COALESCE(MAX(id), 1) FROM lock_provider));
SELECT setval('bike_brand_id_seq', (SELECT COALESCE(MAX(id), 1) FROM bike_brand));
SELECT setval('bike_engine_id_seq', (SELECT COALESCE(MAX(id), 1) FROM bike_engine));
SELECT setval('bike_model_id_seq', (SELECT COALESCE(MAX(id), 1) FROM bike_model));
SELECT setval('part_brand_id_seq', (SELECT COALESCE(MAX(id), 1) FROM part_brand));
SELECT setval('part_category_id_seq', (SELECT COALESCE(MAX(id), 1) FROM part_category));
SELECT setval('charging_station_brand_id_seq', (SELECT COALESCE(MAX(id), 1) FROM charging_station_brand));
SELECT setval('charging_station_model_id_seq', (SELECT COALESCE(MAX(id), 1) FROM charging_station_model));
SELECT setval('coordinates_id_seq', (SELECT COALESCE(MAX(id), 1) FROM coordinates));
SELECT setval('location_id_seq', (SELECT COALESCE(MAX(id), 1) FROM location));
SELECT setval('location_image_id_seq', (SELECT COALESCE(MAX(id), 1) FROM location_image));
SELECT setval('hub_id_seq', (SELECT COALESCE(MAX(id), 1) FROM hub));
SELECT setval('hub_image_id_seq', (SELECT COALESCE(MAX(id), 1) FROM hub_image));
SELECT setval('lock_entity_id_seq', (SELECT COALESCE(MAX(id), 1) FROM lock_entity));
SELECT setval('key_entity_id_seq', (SELECT COALESCE(MAX(id), 1) FROM key_entity));
SELECT setval('product_id_seq', (SELECT COALESCE(MAX(id), 1) FROM product));
SELECT setval('service_id_seq', (SELECT COALESCE(MAX(id), 1) FROM service));
SELECT setval('rental_plan_id_seq', (SELECT COALESCE(MAX(id), 1) FROM rental_plan));
SELECT setval('bike_model_rental_plan_id_seq', (SELECT COALESCE(MAX(id), 1) FROM bike_model_rental_plan));
SELECT setval('user_location_id_seq', (SELECT COALESCE(MAX(id), 1) FROM user_location));
SELECT setval('b2b_sale_id_seq', (SELECT COALESCE(MAX(id), 1) FROM b2b_sale));
SELECT setval('b2b_sale_order_id_seq', (SELECT COALESCE(MAX(id), 1) FROM b2b_sale_order));
SELECT setval('b2b_sale_product_id_seq', (SELECT COALESCE(MAX(id), 1) FROM b2b_sale_product));
SELECT setval('b2b_sale_order_product_model_id_seq', (SELECT COALESCE(MAX(id), 1) FROM b2b_sale_order_product_model));
SELECT setval('b2b_subscription_id_seq', (SELECT COALESCE(MAX(id), 1) FROM b2b_subscription));
SELECT setval('b2b_subscription_item_id_seq', (SELECT COALESCE(MAX(id), 1) FROM b2b_subscription_item));
SELECT setval('b2b_subscription_order_id_seq', (SELECT COALESCE(MAX(id), 1) FROM b2b_subscription_order));
SELECT setval('b2b_subscription_order_item_id_seq', (SELECT COALESCE(MAX(id), 1) FROM b2b_subscription_order_item));
SELECT setval('rental_id_seq', (SELECT COALESCE(MAX(id), 1) FROM rental));
SELECT setval('bike_rental_id_seq', (SELECT COALESCE(MAX(id), 1) FROM bike_rental));
SELECT setval('bike_reservation_id_seq', (SELECT COALESCE(MAX(id), 1) FROM bike_reservation));
SELECT setval('ride_id_seq', (SELECT COALESCE(MAX(id), 1) FROM ride));
SELECT setval('stock_movement_id_seq', (SELECT COALESCE(MAX(id), 1) FROM stock_movement));
-- bike_part table does not exist
SELECT setval('bike_model_part_id_seq', (SELECT COALESCE(MAX(id), 1) FROM bike_model_part));

-- =====================================================================================================================
-- END OF INITIALIZATION
-- =====================================================================================================================
