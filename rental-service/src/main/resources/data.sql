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
-- Note: PostGIS extension is enabled here. 
-- The geom column, triggers, and indexes should be managed separately via migration scripts
-- or database management tools to avoid SQL parsing issues with Spring Boot's script executor.
-- =====================================================================================================================

-- Enable PostGIS extension for spatial operations
CREATE EXTENSION IF NOT EXISTS postgis;

-- =====================================================================================================================
-- SECTION 0.1: POSTGIS GEOMETRY COLUMN SETUP
-- =====================================================================================================================
-- CRITICAL: This section MUST run after Hibernate creates the coordinates table
-- The geom column enables spatial queries for the nearby bikes feature
-- CRITICAL DEPENDENCY: The queries in BikeRepository.java depend on this geom column.
-- If this script fails to execute, the /api/v1/bikes/nearby endpoint will return 500 errors.
-- =====================================================================================================================

-- Add geom column to coordinates table (Hibernate does not create this from the entity)
ALTER TABLE coordinates ADD COLUMN IF NOT EXISTS geom GEOGRAPHY(POINT, 4326);

-- Create spatial index on geometry column for fast proximity queries
-- This index is essential for performance with ST_DWithin queries
CREATE INDEX IF NOT EXISTS idx_coordinates_geom ON coordinates USING GIST (geom);

-- Trigger function to automatically update geometry from latitude/longitude
-- Uses single-quote syntax because Spring Boot ScriptUtils cannot parse $$ blocks correctly
CREATE OR REPLACE FUNCTION update_coordinates_geom() RETURNS TRIGGER AS
'BEGIN NEW.geom = ST_SetSRID(ST_MakePoint(NEW.longitude, NEW.latitude), 4326)::geography; RETURN NEW; END;'
LANGUAGE plpgsql;

-- Drop trigger if exists, then recreate (safe for repeated startup)
DROP TRIGGER IF EXISTS trg_coordinates_geom_update ON coordinates;
CREATE TRIGGER trg_coordinates_geom_update
    BEFORE INSERT OR UPDATE OF latitude, longitude ON coordinates
    FOR EACH ROW EXECUTE FUNCTION update_coordinates_geom();

-- Populate geom for any existing coordinates rows that have NULL geom
UPDATE coordinates SET geom = ST_SetSRID(ST_MakePoint(longitude, latitude), 4326)::geography
    WHERE geom IS NULL AND latitude IS NOT NULL AND longitude IS NOT NULL;

-- =====================================================================================================================
-- SECTION 0.5: SCHEMA MIGRATIONS (AUTO-APPLIED ON STARTUP)
-- =====================================================================================================================
-- Note: This section handles schema changes that need to be applied to existing databases.
-- Uses IF NOT EXISTS for safe idempotent operations that can run on every startup.
-- These run AFTER Hibernate creates/updates tables but BEFORE data inserts.
-- =====================================================================================================================

-- Migration: Add is_active column to location table (Added: 2026-01-26)
-- Purpose: Enable/disable locations without deleting them
ALTER TABLE location ADD COLUMN IF NOT EXISTS is_active BOOLEAN DEFAULT true;

-- =====================================================================================================================
-- SECTION 1: STATUS & LOOKUP TABLES (REQUIRED)
-- =====================================================================================================================

-- ---------------------------------------------------------------------------------------------------------------------
-- 1.1 BIKE TYPE (HAS audit fields and external_id)
-- ---------------------------------------------------------------------------------------------------------------------
INSERT INTO bike_type (id, external_id, name, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, '550e8400-e29b-41d4-a716-446655440071', 'Electric', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(2, '550e8400-e29b-41d4-a716-446655440072', 'Non-electric', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- ---------------------------------------------------------------------------------------------------------------------
-- 1.2 BIKE STATUS (HAS audit fields and external_id)
-- ---------------------------------------------------------------------------------------------------------------------
INSERT INTO bike_status (id, external_id, name, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, '550e8400-e29b-41d4-a716-446655440081', 'Available', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(2, '550e8400-e29b-41d4-a716-446655440082', 'In use', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(3, '550e8400-e29b-41d4-a716-446655440083', 'Reserved', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(4, '550e8400-e29b-41d4-a716-446655440084', 'Broken', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(5, '550e8400-e29b-41d4-a716-446655440085', 'Disabled', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- ---------------------------------------------------------------------------------------------------------------------
-- 1.3 BIKE RENTAL STATUS (HAS audit fields and external_id)
-- ---------------------------------------------------------------------------------------------------------------------
INSERT INTO bike_rental_status (id, external_id, name, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, '550e8400-e29b-41d4-a716-446655440091', 'Active', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(2, '550e8400-e29b-41d4-a716-446655440092', 'Completed', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(3, '550e8400-e29b-41d4-a716-446655440093', 'Cancelled', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- ---------------------------------------------------------------------------------------------------------------------
-- 1.4 RENTAL STATUS (HAS audit fields and external_id)
-- ---------------------------------------------------------------------------------------------------------------------
INSERT INTO rental_status (id, external_id, name, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, '550e8400-e29b-41d4-a716-446655440061', 'Pending', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(2, '550e8400-e29b-41d4-a716-446655440062', 'Active', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(3, '550e8400-e29b-41d4-a716-446655440063', 'Completed', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(4, '550e8400-e29b-41d4-a716-446655440064', 'Cancelled', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- ---------------------------------------------------------------------------------------------------------------------
-- 1.5 RENTAL UNIT (HAS audit fields and external_id)
-- ---------------------------------------------------------------------------------------------------------------------
INSERT INTO rental_unit (id, external_id, name, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, '550e8400-e29b-41d4-a716-446655440051', 'Hour', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(2, '550e8400-e29b-41d4-a716-446655440052', 'Day', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(3, '550e8400-e29b-41d4-a716-446655440053', 'Week', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(4, '550e8400-e29b-41d4-a716-446655440054', 'Month', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- ---------------------------------------------------------------------------------------------------------------------
-- 1.6 RIDE STATUS (HAS audit fields and external_id)
-- ---------------------------------------------------------------------------------------------------------------------
INSERT INTO ride_status (id, external_id, name, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, '550e8400-e29b-41d4-a716-446655440141', 'Active', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(2, '550e8400-e29b-41d4-a716-446655440142', 'Finished', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(3, '550e8400-e29b-41d4-a716-446655440143', 'Paused', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- ---------------------------------------------------------------------------------------------------------------------
-- 1.7 LOCATION ROLE (HAS audit fields and external_id)
-- ---------------------------------------------------------------------------------------------------------------------
INSERT INTO location_role (id, external_id, name, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, '550e8400-e29b-41d4-a716-446655440131', 'Admin', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(2, '550e8400-e29b-41d4-a716-446655440132', 'Manager', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(3, '550e8400-e29b-41d4-a716-446655440133', 'Staff', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(4, '550e8400-e29b-41d4-a716-446655440134', 'Viewer', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- ---------------------------------------------------------------------------------------------------------------------
-- 1.8 B2B SALE STATUS (HAS audit fields and external_id)
-- ---------------------------------------------------------------------------------------------------------------------
INSERT INTO b2b_sale_status (id, external_id, name, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, '550e8400-e29b-41d4-a716-446655440151', 'Draft', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(2, '550e8400-e29b-41d4-a716-446655440152', 'Active', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(3, '550e8400-e29b-41d4-a716-446655440153', 'Completed', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(4, '550e8400-e29b-41d4-a716-446655440154', 'Cancelled', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- ---------------------------------------------------------------------------------------------------------------------
-- 1.9 B2B SALE ORDER STATUS (HAS audit fields and external_id)
-- ---------------------------------------------------------------------------------------------------------------------
INSERT INTO b2b_sale_order_status (id, external_id, name, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, '550e8400-e29b-41d4-a716-446655440161', 'Pending', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(2, '550e8400-e29b-41d4-a716-446655440162', 'Confirmed', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(3, '550e8400-e29b-41d4-a716-446655440163', 'Shipped', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(4, '550e8400-e29b-41d4-a716-446655440164', 'Delivered', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(5, '550e8400-e29b-41d4-a716-446655440165', 'Cancelled', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- ---------------------------------------------------------------------------------------------------------------------
-- 1.10 B2B SUBSCRIPTION STATUS (HAS audit fields and external_id)
-- ---------------------------------------------------------------------------------------------------------------------
INSERT INTO b2b_subscription_status (id, external_id, name, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, '550e8400-e29b-41d4-a716-446655440171', 'Active', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(2, '550e8400-e29b-41d4-a716-446655440172', 'Inactive', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(3, '550e8400-e29b-41d4-a716-446655440173', 'Suspended', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(4, '550e8400-e29b-41d4-a716-446655440174', 'Cancelled', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- ---------------------------------------------------------------------------------------------------------------------
-- 1.11 B2B SUBSCRIPTION ORDER STATUS (HAS audit fields and external_id)
-- ---------------------------------------------------------------------------------------------------------------------
INSERT INTO b2b_subscription_order_status (id, external_id, name, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, '550e8400-e29b-41d4-a716-446655440181', 'Pending', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(2, '550e8400-e29b-41d4-a716-446655440182', 'Confirmed', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(3, '550e8400-e29b-41d4-a716-446655440183', 'Fulfilled', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(4, '550e8400-e29b-41d4-a716-446655440184', 'Cancelled', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- ---------------------------------------------------------------------------------------------------------------------
-- 1.12 CHARGING STATION STATUS (HAS audit fields and external_id)
-- ---------------------------------------------------------------------------------------------------------------------
INSERT INTO charging_station_status (id, external_id, name, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, '550e8400-e29b-41d4-a716-446655440191', 'Available', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(2, '550e8400-e29b-41d4-a716-446655440192', 'In use', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(3, '550e8400-e29b-41d4-a716-446655440193', 'Broken', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(4, '550e8400-e29b-41d4-a716-446655440194', 'Disabled', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- ---------------------------------------------------------------------------------------------------------------------
-- 1.13 LOCK STATUS (HAS audit fields and external_id)
-- ---------------------------------------------------------------------------------------------------------------------
INSERT INTO lock_status (id, external_id, name, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, '550e8400-e29b-41d4-a716-446655440121', 'Locked', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(2, '550e8400-e29b-41d4-a716-446655440122', 'Unlocked', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(3, '550e8400-e29b-41d4-a716-446655440123', 'Unknown', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- ---------------------------------------------------------------------------------------------------------------------
-- 1.14 LOCK PROVIDER (HAS audit fields and external_id)
-- ---------------------------------------------------------------------------------------------------------------------
INSERT INTO lock_provider (id, external_id, name, api_endpoint, is_active, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, '550e8400-e29b-41d4-a716-446655440111', 'AXA', 'https://api.axa-locks.com', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(2, '550e8400-e29b-41d4-a716-446655440112', 'OMNI', 'https://api.omni-locks.com', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(3, '550e8400-e29b-41d4-a716-446655440113', 'Generic BLE', NULL, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- =====================================================================================================================
-- SECTION 2: BRANDS & MODELS
-- =====================================================================================================================

-- ---------------------------------------------------------------------------------------------------------------------
-- 2.1 BIKE BRANDS (NO audit fields)
-- ---------------------------------------------------------------------------------------------------------------------
INSERT INTO bike_brand (id, external_id, name, company_external_id, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, '550e8400-e29b-41d4-a716-446655440001', 'Trek', 'company-ext-001', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(2, '550e8400-e29b-41d4-a716-446655440002', 'Giant', 'company-ext-001', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(3, '550e8400-e29b-41d4-a716-446655440003', 'Specialized', 'company-ext-001', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(4, '550e8400-e29b-41d4-a716-446655440004', 'Cannondale', 'company-ext-001', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(5, '550e8400-e29b-41d4-a716-446655440005', 'Scott', 'company-ext-002', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- ---------------------------------------------------------------------------------------------------------------------
-- 2.2 BIKE ENGINES (NO audit fields)
-- ---------------------------------------------------------------------------------------------------------------------
INSERT INTO bike_engine (id, external_id, name, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, '550e8400-e29b-41d4-a716-446655440031', 'Bosch Performance CX', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(2, '550e8400-e29b-41d4-a716-446655440032', 'Shimano EP8', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(3, '550e8400-e29b-41d4-a716-446655440033', 'Yamaha PW-X3', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(4, '550e8400-e29b-41d4-a716-446655440034', 'Brose Drive S Mag', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- ---------------------------------------------------------------------------------------------------------------------
-- 2.3 BIKE MODELS (Extends Product - JOINED inheritance)
-- ---------------------------------------------------------------------------------------------------------------------
-- First insert into product table (parent) - Using IDs 1001-1005 for BikeModels
INSERT INTO product (id, external_id, product_type, is_b2b_rentable, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1001, '550e8400-e29b-41d4-a716-446655440301', 'BIKE_MODEL', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(1002, '550e8400-e29b-41d4-a716-446655440302', 'BIKE_MODEL', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(1003, '550e8400-e29b-41d4-a716-446655440303', 'BIKE_MODEL', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(1004, '550e8400-e29b-41d4-a716-446655440304', 'BIKE_MODEL', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(1005, '550e8400-e29b-41d4-a716-446655440305', 'BIKE_MODEL', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- Then insert into bike_model table (subclass)
INSERT INTO bike_model (id, name, bike_brand_id, bike_type_id, bike_engine_id, image_url, b2b_sale_price, b2b_subscription_price) VALUES
(1001, 'FX 3 Disc', 1, 2, NULL, 'https://images.example.com/bikes/trek-fx3.jpg', 800.00, 50.00),
(1002, 'Marlin 7', 1, 2, NULL, 'https://images.example.com/bikes/trek-marlin7.jpg', 950.00, 75.00),
(1003, 'Escape 3', 2, 2, NULL, 'https://images.example.com/bikes/giant-escape3.jpg', 600.00, 40.00),
(1004, 'Turbo Vado', 3, 1, 1, 'https://images.example.com/bikes/specialized-vado.jpg', 999.00, 99.00),
(1005, 'Rail 9.9', 1, 1, 1, 'https://images.example.com/bikes/trek-rail.jpg', 999.00, 99.00)
ON CONFLICT (id) DO NOTHING;

-- ---------------------------------------------------------------------------------------------------------------------
-- 2.4 PART BRANDS (NO audit fields, requires company_external_id)
-- ---------------------------------------------------------------------------------------------------------------------
INSERT INTO part_brand (id, external_id, name, company_external_id, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, '550e8400-e29b-41d4-a716-446655440011', 'Shimano', 'company-001', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(2, '550e8400-e29b-41d4-a716-446655440012', 'SRAM', 'company-001', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(3, '550e8400-e29b-41d4-a716-446655440013', 'Continental', 'company-001', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(4, '550e8400-e29b-41d4-a716-446655440014', 'Schwalbe', 'company-002', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(5, '550e8400-e29b-41d4-a716-446655440015', 'Bosch', 'company-002', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- ---------------------------------------------------------------------------------------------------------------------
-- 2.5 PART CATEGORIES (NO audit fields)
-- ---------------------------------------------------------------------------------------------------------------------
INSERT INTO part_category (id, external_id, name, parent_category_id, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, '550e8400-e29b-41d4-a716-446655440041', 'Battery', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(2, '550e8400-e29b-41d4-a716-446655440042', 'Tire', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(3, '550e8400-e29b-41d4-a716-446655440043', 'Lock', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(4, '550e8400-e29b-41d4-a716-446655440044', 'Saddle', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(5, '550e8400-e29b-41d4-a716-446655440045', 'Pedals', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- ---------------------------------------------------------------------------------------------------------------------
-- 2.6 CHARGING STATION BRANDS (NO audit fields, requires company_external_id)
-- ---------------------------------------------------------------------------------------------------------------------
INSERT INTO charging_station_brand (id, external_id, name, company_external_id, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, '550e8400-e29b-41d4-a716-446655440021', 'Bosch', 'company-001', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(2, '550e8400-e29b-41d4-a716-446655440022', 'Shimano STEPS', 'company-001', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(3, '550e8400-e29b-41d4-a716-446655440023', 'Yamaha', 'company-002', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- ---------------------------------------------------------------------------------------------------------------------
-- 2.7 CHARGING STATION MODELS (Extends Product - JOINED inheritance)
-- ---------------------------------------------------------------------------------------------------------------------
-- First insert into product table (parent) - Using IDs 2001-2003 for ChargingStationModels
INSERT INTO product (id, external_id, product_type, is_b2b_rentable, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(2001, '550e8400-e29b-41d4-a716-446655440051', 'CHARGING_STATION_MODEL', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(2002, '550e8400-e29b-41d4-a716-446655440052', 'CHARGING_STATION_MODEL', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(2003, '550e8400-e29b-41d4-a716-446655440053', 'CHARGING_STATION_MODEL', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- Then insert into charging_station_model table (subclass)
INSERT INTO charging_station_model (id, name, charging_station_brand_id, image_url, b2b_sale_price, b2b_subscription_price) VALUES
(2001, 'PowerPack 500', 1, 'https://images.example.com/chargers/bosch-500.jpg', 450.00, 30.00),
(2002, 'PowerTube 625', 1, 'https://images.example.com/chargers/bosch-625.jpg', 550.00, 35.00),
(2003, 'BT-E8036', 2, 'https://images.example.com/chargers/shimano-e8036.jpg', 480.00, 32.00)
ON CONFLICT (id) DO NOTHING;

-- =====================================================================================================================
-- SECTION 3: GEOGRAPHY & LOCATIONS
-- =====================================================================================================================

-- ---------------------------------------------------------------------------------------------------------------------
-- 3.1 COORDINATES (HAS audit fields and external_id)
-- ---------------------------------------------------------------------------------------------------------------------
INSERT INTO coordinates (id, external_id, latitude, longitude, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, '550e8400-e29b-41d4-a716-446655440501', 50.45010000, 30.52340000, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(2, '550e8400-e29b-41d4-a716-446655440502', 50.45470000, 30.52380000, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(3, '550e8400-e29b-41d4-a716-446655440503', 49.83970000, 24.02970000, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(4, '550e8400-e29b-41d4-a716-446655440504', 50.45020000, 30.52350000, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(5, '550e8400-e29b-41d4-a716-446655440505', 50.45480000, 30.52400000, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- ---------------------------------------------------------------------------------------------------------------------
-- 3.2 LOCATIONS (HAS audit fields)
-- ---------------------------------------------------------------------------------------------------------------------
INSERT INTO location (id, external_id, erp_partner_id, name, address, description, company_external_id, is_public, is_active, directions, coordinates_id, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, '550e8400-e29b-41d4-a716-446655440101', 'ERP-PARTNER-001', 'Downtown Bike Hub', '123 Main Street, Kyiv', 'Central bike rental location', 'company-ext-001', true, true, 'Near metro station', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(2, '550e8400-e29b-41d4-a716-446655440102', 'ERP-PARTNER-002', 'Park Side Station', '456 Park Avenue, Kyiv', 'Bike rental near park', 'company-ext-001', true, true, 'Next to park entrance', 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(3, '550e8400-e29b-41d4-a716-446655440103', 'ERP-PARTNER-003', 'City Center Rentals', '789 Central Square, Lviv', 'Main rental location', 'company-ext-002', true, true, 'Central square', 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
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
-- SECTION 5: PRODUCTS (JOINED Inheritance)
-- =====================================================================================================================

-- ---------------------------------------------------------------------------------------------------------------------
-- 5.1 BIKE PRODUCTS (Extends Product - JOINED inheritance)
-- ---------------------------------------------------------------------------------------------------------------------
-- First insert into product table (parent) - Using IDs 1-100 for Bikes
INSERT INTO product (id, external_id, product_type, is_b2b_rentable, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, '550e8400-e29b-41d4-a716-446655440401', 'BIKE', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(2, '550e8400-e29b-41d4-a716-446655440402', 'BIKE', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(3, '550e8400-e29b-41d4-a716-446655440403', 'BIKE', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- Then insert into bike table (subclass) - referencing bike_model_id 1001, 1001, 1002
INSERT INTO bike (id, code, qr_code_url, vat, is_vat_include, in_service_date, hub_id, coordinates_id, frame_number, bike_status_id, battery_level, lock_id, bike_type_id, currency_external_id, bike_model_id, revenue_share_percent) VALUES
(1, 'BIKE-001', 'https://qr.example.com/bike/001', 20.00, true, CURRENT_DATE - INTERVAL '6 months', 1, 1, 'TREK-FX3-001', 1, 85, 1, 2, 'currency-ext-eur', 1001, 10.00),
(2, 'BIKE-002', 'https://qr.example.com/bike/002', 20.00, true, CURRENT_DATE - INTERVAL '6 months', 1, 2, 'TREK-FX3-002', 1, 92, 2, 2, 'currency-ext-eur', 1001, 10.00),
(3, 'BIKE-003', 'https://qr.example.com/bike/003', 20.00, true, CURRENT_DATE - INTERVAL '4 months', 2, 3, 'TREK-M7-001', 1, 100, 3, 2, 'currency-ext-eur', 1002, 10.00)
ON CONFLICT (id) DO NOTHING;

-- ---------------------------------------------------------------------------------------------------------------------
-- 5.2 CHARGING STATION PRODUCTS (Extends Product - JOINED inheritance)
-- ---------------------------------------------------------------------------------------------------------------------
-- First insert into product table (parent) - Using IDs 101-200 for ChargingStations
INSERT INTO product (id, external_id, product_type, is_b2b_rentable, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(101, '550e8400-e29b-41d4-a716-446655440404', 'CHARGING_STATION', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(102, '550e8400-e29b-41d4-a716-446655440405', 'CHARGING_STATION', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- Then insert into charging_station table (subclass) - referencing charging_station_model_id 2001, 2002
INSERT INTO charging_station (id, code, qr_code_url, vat, is_vat_include, in_service_date, hub_id, coordinates_id, charging_station_status_id, charging_station_model_id, is_active) VALUES
(101, 'CS-001', 'https://qr.example.com/cs/001', 20.00, true, CURRENT_DATE, 1, 4, 1, 2001, true),
(102, 'CS-002', 'https://qr.example.com/cs/002', 20.00, true, CURRENT_DATE, 3, 5, 1, 2002, true)
ON CONFLICT (id) DO NOTHING;

-- ---------------------------------------------------------------------------------------------------------------------
-- 5.3 PART PRODUCTS (Extends Product - JOINED inheritance)
-- ---------------------------------------------------------------------------------------------------------------------
-- First insert into product table (parent) - Using IDs 201-300 for Parts
INSERT INTO product (id, external_id, product_type, is_b2b_rentable, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(201, '550e8400-e29b-41d4-a716-446655440406', 'PART', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(202, '550e8400-e29b-41d4-a716-446655440407', 'PART', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(203, '550e8400-e29b-41d4-a716-446655440408', 'PART', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- Then insert into part table (subclass)
INSERT INTO part (id, vat, is_vat_include, name, part_brand_id, image_url, part_category_id, hub_id, b2b_sale_price, quantity) VALUES
(201, 20.00, true, 'Bosch PowerPack 500', 5, 'https://images.example.com/parts/battery-500.jpg', 1, 1, 450.00, 5),
(202, 20.00, true, 'Continental Contact Plus', 3, 'https://images.example.com/parts/tire-contact.jpg', 2, 1, 35.00, 20),
(203, 20.00, true, 'Schwalbe Marathon', 4, 'https://images.example.com/parts/tire-marathon.jpg', 2, 2, 42.00, 15)
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
-- 6.1 SERVICES (HAS audit fields and external_id, only has name and b2b_subscription_price)
-- ---------------------------------------------------------------------------------------------------------------------
INSERT INTO service (id, external_id, name, b2b_subscription_price, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, '550e8400-e29b-41d4-a716-446655440701', 'Basic Tune-Up', 25.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(2, '550e8400-e29b-41d4-a716-446655440702', 'Full Service', 75.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(3, '550e8400-e29b-41d4-a716-446655440703', 'Tire Replacement', 30.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- ---------------------------------------------------------------------------------------------------------------------
-- 6.2 SERVICE PRODUCTS (Extends Product - JOINED inheritance)
-- ---------------------------------------------------------------------------------------------------------------------
-- First insert into product table (parent) - Using IDs 301-400 for ServiceProducts
INSERT INTO product (id, external_id, product_type, is_b2b_rentable, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(301, '550e8400-e29b-41d4-a716-446655440409', 'SERVICE_PRODUCT', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(302, '550e8400-e29b-41d4-a716-446655440410', 'SERVICE_PRODUCT', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- Then insert into service_product table (subclass)
INSERT INTO service_product (id, service_id, related_product_id) VALUES
(301, 1, NULL),
(302, 2, NULL)
ON CONFLICT (id) DO NOTHING;

-- =====================================================================================================================
-- SECTION 7: RENTAL PLANS & ASSOCIATIONS
-- =====================================================================================================================

-- ---------------------------------------------------------------------------------------------------------------------
-- 7.1 RENTAL PLANS (HAS audit fields and external_id)
-- ---------------------------------------------------------------------------------------------------------------------
INSERT INTO rental_plan (id, external_id, name, rental_unit_id, min_unit, max_unit, location_id, default_price, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, '550e8400-e29b-41d4-a716-446655440711', 'Hourly Plan', 1, 1, 24, 1, 5.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(2, '550e8400-e29b-41d4-a716-446655440712', 'Half Day', 1, 4, 8, 1, 15.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(3, '550e8400-e29b-41d4-a716-446655440713', 'Full Day', 2, 1, 7, 1, 25.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(4, '550e8400-e29b-41d4-a716-446655440714', 'Weekly Plan', 3, 1, 4, 2, 120.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- ---------------------------------------------------------------------------------------------------------------------
-- 7.2 BIKE MODEL RENTAL PLAN (HAS audit fields and external_id, needs price field)
-- ---------------------------------------------------------------------------------------------------------------------
INSERT INTO bike_model_rental_plan (id, external_id, bike_model_id, rental_plan_id, price, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, '550e8400-e29b-41d4-a716-446655440721', 1001, 1, 5.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(2, '550e8400-e29b-41d4-a716-446655440722', 1001, 2, 15.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(3, '550e8400-e29b-41d4-a716-446655440723', 1001, 3, 25.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(4, '550e8400-e29b-41d4-a716-446655440724', 1002, 1, 5.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(5, '550e8400-e29b-41d4-a716-446655440725', 1002, 3, 25.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- ---------------------------------------------------------------------------------------------------------------------
-- 7.3 USER LOCATION (HAS audit fields and external_id)
-- ---------------------------------------------------------------------------------------------------------------------
INSERT INTO user_location (id, external_id, user_external_id, location_id, location_role_id, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, '550e8400-e29b-41d4-a716-446655440901', 'usr-ext-00001', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(2, '550e8400-e29b-41d4-a716-446655440902', 'usr-ext-00002', 1, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(3, '550e8400-e29b-41d4-a716-446655440903', 'usr-ext-00003', 2, 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- =====================================================================================================================
-- SECTION 8: B2B SALES & SUBSCRIPTIONS
-- =====================================================================================================================

-- ---------------------------------------------------------------------------------------------------------------------
-- 8.1 B2B SALES (HAS audit fields)
-- ---------------------------------------------------------------------------------------------------------------------
INSERT INTO b2b_sale (id, external_id, location_id, b2b_sale_status_id, seller_company_external_id, buyer_company_external_id, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, '550e8400-e29b-41d4-a716-446655440801', 1, 2, 'company-ext-001', 'company-ext-003', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(2, '550e8400-e29b-41d4-a716-446655440802', 1, 3, 'company-ext-001', 'company-ext-004', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- ---------------------------------------------------------------------------------------------------------------------
-- 8.2 B2B SALE ORDERS (HAS audit fields)
-- ---------------------------------------------------------------------------------------------------------------------
INSERT INTO b2b_sale_order (id, external_id, seller_company_external_id, buyer_company_external_id, b2b_sale_order_status_id, location_id, b2b_sale_id, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, '550e8400-e29b-41d4-a716-446655440811', 'company-001', 'company-002', 4, 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(2, '550e8400-e29b-41d4-a716-446655440812', 'company-002', 'company-001', 3, 2, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- ---------------------------------------------------------------------------------------------------------------------
-- 8.3 B2B SALE PRODUCTS (NO audit fields)
-- ---------------------------------------------------------------------------------------------------------------------
-- Note: product_id references Bike products (IDs 1, 2, 3)
INSERT INTO b2b_sale_item (id, external_id, b2b_sale_id, product_id, price, quantity, total_price, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, '550e8400-e29b-41d4-a716-446655440821', 1, 1, 800.00, 5, 800.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(2, '550e8400-e29b-41d4-a716-446655440822', 1, 2, 800.00, 5, 800.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(3, '550e8400-e29b-41d4-a716-446655440823', 2, 3, 950.00, 8, 950.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- ---------------------------------------------------------------------------------------------------------------------
-- 8.4 B2B SALE ORDER PRODUCT MODELS (HAS audit fields, uses product_id not bike_model_id, price and total_price not unit_price)
-- ---------------------------------------------------------------------------------------------------------------------
-- Note: product_id references Bike products (IDs 1, 3)
INSERT INTO b2b_sale_order_item (id, external_id, b2b_sale_order_id, product_id, quantity, price, total_price, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
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
-- Note: product_id references Bike products (IDs 1, 2) and ChargingStation product (ID 101)
INSERT INTO b2b_subscription_item (id, external_id, b2b_subscription_id, product_id, start_date_time, end_date_time, price, total_price, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, '550e8400-e29b-41d4-a716-446655440861', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '12 months', 50.00, 600.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(2, '550e8400-e29b-41d4-a716-446655440862', 1, 101, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '12 months', 99.00, 1188.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(3, '550e8400-e29b-41d4-a716-446655440863', 2, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '24 months', 75.00, 1800.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- ---------------------------------------------------------------------------------------------------------------------
-- 8.7 B2B SUBSCRIPTION ORDERS (HAS audit fields)
-- ---------------------------------------------------------------------------------------------------------------------
INSERT INTO b2b_subscription_order (id, external_id, location_id, b2b_subscription_order_status_id, b2b_subscription_id, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, '550e8400-e29b-41d4-a716-446655440841', 1, 2, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(2, '550e8400-e29b-41d4-a716-446655440842', 2, 3, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- ---------------------------------------------------------------------------------------------------------------------
-- 8.8 B2B SUBSCRIPTION ORDER ITEMS (HAS audit fields, uses product_id not bike_model_id, price and total_price not unit_price)
-- ---------------------------------------------------------------------------------------------------------------------
-- Note: product_id references Bike products (IDs 1, 2)
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
-- Note: Removed redundant bike_external_id, location_external_id, rental_external_id fields
--       These can be accessed through relationships: bike.getExternalId(), location.getExternalId(), rental.getExternalId()
-- ---------------------------------------------------------------------------------------------------------------------
INSERT INTO bike_rental (id, external_id, bike_id, location_id, rental_id, start_date_time, end_date_time, rental_unit_id, bike_rental_status_id, is_revenue_share_paid, photo_url, price, total_price, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
-- Unpaid bike rentals from December 2025 (for payout testing)
(1, 'bike-rental-ext-00101', 1, 1, 1, '2025-12-05 10:00:00', '2025-12-05 18:00:00', 1, 2, false, NULL, 5.00, 40.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(2, 'bike-rental-ext-00102', 2, 1, 2, '2025-12-10 09:00:00', '2025-12-10 17:00:00', 1, 2, false, NULL, 5.00, 40.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(3, 'bike-rental-ext-00103', 3, 2, 2, '2025-12-15 14:00:00', '2025-12-15 20:00:00', 1, 2, false, NULL, 5.00, 30.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
-- Paid bike rental from November 2025 (should not appear in December payouts)
(4, 'bike-rental-ext-00104', 1, 1, 1, '2025-11-20 10:00:00', '2025-11-20 15:00:00', 1, 2, true, NULL, 5.00, 25.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
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
-- Note: product_id references Part products (IDs 202, 203)
INSERT INTO stock_movement (id, external_id, product_id, from_hub_id, to_hub_id, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, 'stock-mov-00101', 202, 1, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(2, 'stock-mov-00102', 203, 2, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- ---------------------------------------------------------------------------------------------------------------------
-- 10.2 BIKE PARTS - Table does not exist (no BikePart entity)
-- ---------------------------------------------------------------------------------------------------------------------
-- Note: There is no bike_part table. The relationship is handled through BikeModelPart entity.

-- ---------------------------------------------------------------------------------------------------------------------
-- 10.3 BIKE MODEL PARTS (HAS audit fields and external_id)
-- ---------------------------------------------------------------------------------------------------------------------
INSERT INTO bike_model_part (id, external_id, bike_model_id, part_id, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, '550e8400-e29b-41d4-a716-446655440731', 1001, 202, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(2, '550e8400-e29b-41d4-a716-446655440732', 1002, 203, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(3, '550e8400-e29b-41d4-a716-446655440733', 1003, 202, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
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
-- bike_model_id_seq removed - BikeModel now uses product_id_seq (JOINED inheritance)
SELECT setval('part_brand_id_seq', (SELECT COALESCE(MAX(id), 1) FROM part_brand));
SELECT setval('part_category_id_seq', (SELECT COALESCE(MAX(id), 1) FROM part_category));
SELECT setval('charging_station_brand_id_seq', (SELECT COALESCE(MAX(id), 1) FROM charging_station_brand));
-- charging_station_model_id_seq removed - ChargingStationModel now uses product_id_seq (JOINED inheritance)
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
SELECT setval('b2b_sale_item_id_seq', (SELECT COALESCE(MAX(id), 1) FROM b2b_sale_item));
SELECT setval('b2b_sale_order_item_id_seq', (SELECT COALESCE(MAX(id), 1) FROM b2b_sale_order_item));
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
-- SECTION 7: ROW LEVEL SECURITY (RLS) FOR MULTI-TENANT ISOLATION
-- =====================================================================================================================
-- Description: Adds PostgreSQL Row Level Security policies to enforce tenant (company) isolation at database level.
--              This is the 3rd layer of defense (after JWT claims and Hibernate filters).
--              RLS ensures that even direct SQL queries cannot access cross-tenant data.
-- 
-- Note: RLS policies use PostgreSQL session variables set by the application:
--       - app.is_superadmin: boolean flag for admin bypass
--       - app.company_external_ids: comma-separated list of company UUIDs user can access
-- =====================================================================================================================

-- Enable RLS on tenant-scoped tables
ALTER TABLE rental ENABLE ROW LEVEL SECURITY;
ALTER TABLE location ENABLE ROW LEVEL SECURITY;
ALTER TABLE b2b_sale ENABLE ROW LEVEL SECURITY;
ALTER TABLE b2b_subscription ENABLE ROW LEVEL SECURITY;
ALTER TABLE b2b_sale_order ENABLE ROW LEVEL SECURITY;
ALTER TABLE bike_brand ENABLE ROW LEVEL SECURITY;
ALTER TABLE charging_station_brand ENABLE ROW LEVEL SECURITY;
ALTER TABLE part_brand ENABLE ROW LEVEL SECURITY;

-- Force RLS even for table owner (important for security)
ALTER TABLE rental FORCE ROW LEVEL SECURITY;
ALTER TABLE location FORCE ROW LEVEL SECURITY;
ALTER TABLE b2b_sale FORCE ROW LEVEL SECURITY;
ALTER TABLE b2b_subscription FORCE ROW LEVEL SECURITY;
ALTER TABLE b2b_sale_order FORCE ROW LEVEL SECURITY;
ALTER TABLE bike_brand FORCE ROW LEVEL SECURITY;
ALTER TABLE charging_station_brand FORCE ROW LEVEL SECURITY;
ALTER TABLE part_brand FORCE ROW LEVEL SECURITY;

-- ---------------------------------------------------------------------------------------------------------------------
-- 7.1 RLS POLICY: rental table
-- ---------------------------------------------------------------------------------------------------------------------
-- Allows access if:
-- 1. User is superadmin (bypasses all filters), OR
-- 2. Rental belongs to one of user's companies
-- ---------------------------------------------------------------------------------------------------------------------
DROP POLICY IF EXISTS rental_tenant_isolation ON rental;
CREATE POLICY rental_tenant_isolation ON rental
    FOR ALL
    USING (
        -- Allow if user is superadmin
        COALESCE(current_setting('app.is_superadmin', true)::boolean, false) = true
        OR
        -- Allow if rental belongs to one of user's companies
        company_external_id = ANY(
            string_to_array(
                COALESCE(current_setting('app.company_external_ids', true), ''),
                ','
            )
        )
    );

-- ---------------------------------------------------------------------------------------------------------------------
-- 7.2 RLS POLICY: location table
-- ---------------------------------------------------------------------------------------------------------------------
DROP POLICY IF EXISTS location_tenant_isolation ON location;
CREATE POLICY location_tenant_isolation ON location
    FOR ALL
    USING (
        COALESCE(current_setting('app.is_superadmin', true)::boolean, false) = true
        OR
        company_external_id = ANY(
            string_to_array(
                COALESCE(current_setting('app.company_external_ids', true), ''),
                ','
            )
        )
    );

-- ---------------------------------------------------------------------------------------------------------------------
-- 7.3 RLS POLICY: b2b_sale table
-- ---------------------------------------------------------------------------------------------------------------------
-- Special case: User can see sale if they are seller OR buyer
-- ---------------------------------------------------------------------------------------------------------------------
DROP POLICY IF EXISTS b2b_sale_tenant_isolation ON b2b_sale;
CREATE POLICY b2b_sale_tenant_isolation ON b2b_sale
    FOR ALL
    USING (
        COALESCE(current_setting('app.is_superadmin', true)::boolean, false) = true
        OR
        seller_company_external_id = ANY(
            string_to_array(
                COALESCE(current_setting('app.company_external_ids', true), ''),
                ','
            )
        )
        OR
        buyer_company_external_id = ANY(
            string_to_array(
                COALESCE(current_setting('app.company_external_ids', true), ''),
                ','
            )
        )
    );

-- ---------------------------------------------------------------------------------------------------------------------
-- 7.4 RLS POLICY: b2b_subscription table
-- ---------------------------------------------------------------------------------------------------------------------
-- Access via location relationship
-- ---------------------------------------------------------------------------------------------------------------------
DROP POLICY IF EXISTS b2b_subscription_tenant_isolation ON b2b_subscription;
CREATE POLICY b2b_subscription_tenant_isolation ON b2b_subscription
    FOR ALL
    USING (
        COALESCE(current_setting('app.is_superadmin', true)::boolean, false) = true
        OR
        location_id IN (
            SELECT id FROM location 
            WHERE company_external_id = ANY(
                string_to_array(
                    COALESCE(current_setting('app.company_external_ids', true), ''),
                    ','
                )
            )
        )
    );

-- ---------------------------------------------------------------------------------------------------------------------
-- 7.5 RLS POLICY: b2b_sale_order table
-- ---------------------------------------------------------------------------------------------------------------------
-- Special case: User can see order if they are seller OR buyer
-- ---------------------------------------------------------------------------------------------------------------------
DROP POLICY IF EXISTS b2b_sale_order_tenant_isolation ON b2b_sale_order;
CREATE POLICY b2b_sale_order_tenant_isolation ON b2b_sale_order
    FOR ALL
    USING (
        COALESCE(current_setting('app.is_superadmin', true)::boolean, false) = true
        OR
        seller_company_external_id = ANY(
            string_to_array(
                COALESCE(current_setting('app.company_external_ids', true), ''),
                ','
            )
        )
        OR
        buyer_company_external_id = ANY(
            string_to_array(
                COALESCE(current_setting('app.company_external_ids', true), ''),
                ','
            )
        )
    );

-- ---------------------------------------------------------------------------------------------------------------------
-- 7.6 RLS POLICY: bike_brand table
-- ---------------------------------------------------------------------------------------------------------------------
DROP POLICY IF EXISTS bike_brand_tenant_isolation ON bike_brand;
CREATE POLICY bike_brand_tenant_isolation ON bike_brand
    FOR ALL
    USING (
        COALESCE(current_setting('app.is_superadmin', true)::boolean, false) = true
        OR
        company_external_id = ANY(
            string_to_array(
                COALESCE(current_setting('app.company_external_ids', true), ''),
                ','
            )
        )
    );

-- ---------------------------------------------------------------------------------------------------------------------
-- 7.7 RLS POLICY: charging_station_brand table
-- ---------------------------------------------------------------------------------------------------------------------
DROP POLICY IF EXISTS charging_station_brand_tenant_isolation ON charging_station_brand;
CREATE POLICY charging_station_brand_tenant_isolation ON charging_station_brand
    FOR ALL
    USING (
        COALESCE(current_setting('app.is_superadmin', true)::boolean, false) = true
        OR
        company_external_id = ANY(
            string_to_array(
                COALESCE(current_setting('app.company_external_ids', true), ''),
                ','
            )
        )
    );

-- ---------------------------------------------------------------------------------------------------------------------
-- 7.8 RLS POLICY: part_brand table
-- ---------------------------------------------------------------------------------------------------------------------
DROP POLICY IF EXISTS part_brand_tenant_isolation ON part_brand;
CREATE POLICY part_brand_tenant_isolation ON part_brand
    FOR ALL
    USING (
        COALESCE(current_setting('app.is_superadmin', true)::boolean, false) = true
        OR
        company_external_id = ANY(
            string_to_array(
                COALESCE(current_setting('app.company_external_ids', true), ''),
                ','
            )
        )
    );

-- ---------------------------------------------------------------------------------------------------------------------
-- 7.9 PERFORMANCE INDEXES for RLS
-- ---------------------------------------------------------------------------------------------------------------------
-- These indexes ensure RLS policies don't slow down queries
-- ---------------------------------------------------------------------------------------------------------------------
CREATE INDEX IF NOT EXISTS idx_rental_company_rls ON rental(company_external_id) WHERE is_deleted = false;
CREATE INDEX IF NOT EXISTS idx_location_company_rls ON location(company_external_id) WHERE is_deleted = false;
CREATE INDEX IF NOT EXISTS idx_b2b_sale_seller_rls ON b2b_sale(seller_company_external_id) WHERE is_deleted = false;
CREATE INDEX IF NOT EXISTS idx_b2b_sale_buyer_rls ON b2b_sale(buyer_company_external_id) WHERE is_deleted = false;
CREATE INDEX IF NOT EXISTS idx_b2b_sale_order_seller_rls ON b2b_sale_order(seller_company_external_id) WHERE is_deleted = false;
CREATE INDEX IF NOT EXISTS idx_b2b_sale_order_buyer_rls ON b2b_sale_order(buyer_company_external_id) WHERE is_deleted = false;
CREATE INDEX IF NOT EXISTS idx_bike_brand_company_rls ON bike_brand(company_external_id) WHERE is_deleted = false;
CREATE INDEX IF NOT EXISTS idx_charging_station_brand_company_rls ON charging_station_brand(company_external_id) WHERE is_deleted = false;
CREATE INDEX IF NOT EXISTS idx_part_brand_company_rls ON part_brand(company_external_id) WHERE is_deleted = false;

-- =====================================================================================================================
-- SECTION 8: AUDIT LOGGING
-- =====================================================================================================================

-- ---------------------------------------------------------------------------------------------------------------------
-- 8.1 AUDIT LOGS TABLE
-- ---------------------------------------------------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS audit_logs (
    id BIGSERIAL PRIMARY KEY,
    event_type VARCHAR(50) NOT NULL,
    user_external_id VARCHAR(100),
    company_external_ids TEXT,
    resource_type VARCHAR(100),
    resource_id VARCHAR(100),
    endpoint VARCHAR(500),
    http_method VARCHAR(10),
    client_ip VARCHAR(45),
    success BOOLEAN NOT NULL,
    error_message TEXT,
    metadata TEXT,
    timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ---------------------------------------------------------------------------------------------------------------------
-- 8.2 AUDIT LOGS INDEXES
-- ---------------------------------------------------------------------------------------------------------------------
CREATE INDEX IF NOT EXISTS idx_audit_logs_timestamp ON audit_logs(timestamp);
CREATE INDEX IF NOT EXISTS idx_audit_logs_user ON audit_logs(user_external_id);
CREATE INDEX IF NOT EXISTS idx_audit_logs_event_type ON audit_logs(event_type);
CREATE INDEX IF NOT EXISTS idx_audit_logs_success ON audit_logs(success);

-- =====================================================================================================================
-- END OF INITIALIZATION
-- =====================================================================================================================
