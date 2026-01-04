-- =====================================================================================================================
-- RENTAL SERVICE - DATABASE SCHEMA
-- =====================================================================================================================
-- Module: rental-service
-- Database: PostgreSQL
-- Description: Complete database schema for the bike rental and fleet management service.
--              Includes bikes, locations, rentals, B2B sales/subscriptions, and comprehensive fleet management.
-- 
-- Usage:
--   1. Create database: CREATE DATABASE clickenrent_rental;
--   2. Import schema: psql -U postgres -d clickenrent_rental -f rental-service.sql
--
-- Author: Vitaliy Shvetsov
-- Notes: Uses SINGLE_TABLE inheritance for Product (Bike, ChargingStation, Part, ServiceProduct)
-- =====================================================================================================================

-- Drop existing tables if they exist (in correct order to handle foreign key dependencies)
DROP TABLE IF EXISTS bike_part CASCADE;
DROP TABLE IF EXISTS stock_movement CASCADE;
DROP TABLE IF EXISTS service_product CASCADE;
DROP TABLE IF EXISTS service CASCADE;
DROP TABLE IF EXISTS ride CASCADE;
DROP TABLE IF EXISTS bike_reservation CASCADE;
DROP TABLE IF EXISTS bike_rental CASCADE;
DROP TABLE IF EXISTS rental CASCADE;
DROP TABLE IF EXISTS user_location CASCADE;
DROP TABLE IF EXISTS b2b_subscription_order_item CASCADE;
DROP TABLE IF EXISTS b2b_subscription_order CASCADE;
DROP TABLE IF EXISTS b2b_subscription_item CASCADE;
DROP TABLE IF EXISTS b2b_subscription CASCADE;
DROP TABLE IF EXISTS b2b_sale_order_item CASCADE;
DROP TABLE IF EXISTS b2b_sale_item CASCADE;
DROP TABLE IF EXISTS b2b_sale_order CASCADE;
DROP TABLE IF EXISTS b2b_sale CASCADE;
DROP TABLE IF EXISTS bike_model_rental_plan CASCADE;
DROP TABLE IF EXISTS rental_plan CASCADE;
DROP TABLE IF EXISTS key_entity CASCADE;
DROP TABLE IF EXISTS lock_entity CASCADE;
DROP TABLE IF EXISTS product CASCADE;
DROP TABLE IF EXISTS bike_model CASCADE;
DROP TABLE IF EXISTS bike_engine CASCADE;
DROP TABLE IF EXISTS bike_brand CASCADE;
DROP TABLE IF EXISTS charging_station_model CASCADE;
DROP TABLE IF EXISTS charging_station_brand CASCADE;
DROP TABLE IF EXISTS part_category CASCADE;
DROP TABLE IF EXISTS part_brand CASCADE;
DROP TABLE IF EXISTS hub_image CASCADE;
DROP TABLE IF EXISTS hub CASCADE;
DROP TABLE IF EXISTS location_image CASCADE;
DROP TABLE IF EXISTS location CASCADE;
DROP TABLE IF EXISTS coordinates CASCADE;
DROP TABLE IF EXISTS charging_station_status CASCADE;
DROP TABLE IF EXISTS b2b_subscription_order_status CASCADE;
DROP TABLE IF EXISTS b2b_subscription_status CASCADE;
DROP TABLE IF EXISTS b2b_sale_order_status CASCADE;
DROP TABLE IF EXISTS b2b_sale_status CASCADE;
DROP TABLE IF EXISTS location_role CASCADE;
DROP TABLE IF EXISTS ride_status CASCADE;
DROP TABLE IF EXISTS rental_unit CASCADE;
DROP TABLE IF EXISTS rental_status CASCADE;
DROP TABLE IF EXISTS bike_rental_status CASCADE;
DROP TABLE IF EXISTS bike_status CASCADE;
DROP TABLE IF EXISTS bike_type CASCADE;

-- =====================================================================================================================
-- SECTION 1: STATUS & LOOKUP TABLES
-- =====================================================================================================================
-- Reference data tables with fixed values

-- ---------------------------------------------------------------------------------------------------------------------
-- Table: bike_type
-- Description: Bike types (Electric bike, Non-electric bike)
-- ---------------------------------------------------------------------------------------------------------------------
CREATE TABLE bike_type (
    id                  BIGSERIAL PRIMARY KEY,
    name                VARCHAR(100) NOT NULL UNIQUE,
    
    CONSTRAINT chk_bike_type_name_not_empty CHECK (name <> '')
);

-- ---------------------------------------------------------------------------------------------------------------------
-- Table: bike_status
-- Description: Bike status values (Available, Broken, Disabled, In use, Paused, Reserved)
-- ---------------------------------------------------------------------------------------------------------------------
CREATE TABLE bike_status (
    id                  BIGSERIAL PRIMARY KEY,
    name                VARCHAR(50) NOT NULL UNIQUE,
    
    CONSTRAINT chk_bike_status_name_not_empty CHECK (name <> '')
);

-- ---------------------------------------------------------------------------------------------------------------------
-- Table: bike_rental_status
-- Description: Bike rental status values (Active, Completed, Cancelled)
-- ---------------------------------------------------------------------------------------------------------------------
CREATE TABLE bike_rental_status (
    id                  BIGSERIAL PRIMARY KEY,
    name                VARCHAR(50) NOT NULL UNIQUE,
    
    CONSTRAINT chk_bike_rental_status_name_not_empty CHECK (name <> '')
);

-- ---------------------------------------------------------------------------------------------------------------------
-- Table: rental_status
-- Description: Rental status values (Pending, Active, Completed, Cancelled)
-- ---------------------------------------------------------------------------------------------------------------------
CREATE TABLE rental_status (
    id                  BIGSERIAL PRIMARY KEY,
    name                VARCHAR(50) NOT NULL UNIQUE,
    
    CONSTRAINT chk_rental_status_name_not_empty CHECK (name <> '')
);

-- ---------------------------------------------------------------------------------------------------------------------
-- Table: rental_unit
-- Description: Rental time units (Day, Hour, Week, Month)
-- ---------------------------------------------------------------------------------------------------------------------
CREATE TABLE rental_unit (
    id                      BIGSERIAL PRIMARY KEY,
    name                    VARCHAR(50) NOT NULL UNIQUE,
    
    -- Audit fields
    date_created            TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_date_modified      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by              VARCHAR(255),
    last_modified_by        VARCHAR(255),
    is_deleted              BOOLEAN NOT NULL DEFAULT false,
    
    CONSTRAINT chk_rental_unit_name_not_empty CHECK (name <> '')
);

-- ---------------------------------------------------------------------------------------------------------------------
-- Table: ride_status
-- Description: Ride status values (Active, Finished, Paused)
-- ---------------------------------------------------------------------------------------------------------------------
CREATE TABLE ride_status (
    id                  BIGSERIAL PRIMARY KEY,
    name                VARCHAR(50) NOT NULL UNIQUE,
    
    CONSTRAINT chk_ride_status_name_not_empty CHECK (name <> '')
);

-- ---------------------------------------------------------------------------------------------------------------------
-- Table: location_role
-- Description: Location roles (Admin, Manager, Staff, Viewer)
-- ---------------------------------------------------------------------------------------------------------------------
CREATE TABLE location_role (
    id                  BIGSERIAL PRIMARY KEY,
    name                VARCHAR(50) NOT NULL UNIQUE,
    
    CONSTRAINT chk_location_role_name_not_empty CHECK (name <> '')
);

-- ---------------------------------------------------------------------------------------------------------------------
-- Table: b2b_sale_status
-- Description: B2B sale status values
-- ---------------------------------------------------------------------------------------------------------------------
CREATE TABLE b2b_sale_status (
    id                  BIGSERIAL PRIMARY KEY,
    name                VARCHAR(50) NOT NULL UNIQUE,
    
    CONSTRAINT chk_b2b_sale_status_name_not_empty CHECK (name <> '')
);

-- ---------------------------------------------------------------------------------------------------------------------
-- Table: b2b_sale_order_status
-- Description: B2B sale order status values
-- ---------------------------------------------------------------------------------------------------------------------
CREATE TABLE b2b_sale_order_status (
    id                  BIGSERIAL PRIMARY KEY,
    name                VARCHAR(50) NOT NULL UNIQUE,
    
    CONSTRAINT chk_b2b_sale_order_status_name_not_empty CHECK (name <> '')
);

-- ---------------------------------------------------------------------------------------------------------------------
-- Table: b2b_subscription_status
-- Description: B2B subscription status values
-- ---------------------------------------------------------------------------------------------------------------------
CREATE TABLE b2b_subscription_status (
    id                  BIGSERIAL PRIMARY KEY,
    name                VARCHAR(50) NOT NULL UNIQUE,
    
    CONSTRAINT chk_b2b_subscription_status_name_not_empty CHECK (name <> '')
);

-- ---------------------------------------------------------------------------------------------------------------------
-- Table: b2b_subscription_order_status
-- Description: B2B subscription order status values
-- ---------------------------------------------------------------------------------------------------------------------
CREATE TABLE b2b_subscription_order_status (
    id                  BIGSERIAL PRIMARY KEY,
    name                VARCHAR(50) NOT NULL UNIQUE,
    
    CONSTRAINT chk_b2b_subscription_order_status_name_not_empty CHECK (name <> '')
);

-- ---------------------------------------------------------------------------------------------------------------------
-- Table: charging_station_status
-- Description: Charging station status values
-- ---------------------------------------------------------------------------------------------------------------------
CREATE TABLE charging_station_status (
    id                  BIGSERIAL PRIMARY KEY,
    name                VARCHAR(50) NOT NULL UNIQUE,
    
    CONSTRAINT chk_charging_station_status_name_not_empty CHECK (name <> '')
);

-- =====================================================================================================================
-- SECTION 2: GEOGRAPHY & COORDINATES
-- =====================================================================================================================

-- ---------------------------------------------------------------------------------------------------------------------
-- Table: coordinates
-- Description: GPS coordinates shared by bikes, locations, hubs, rides, and charging stations
-- ---------------------------------------------------------------------------------------------------------------------
-- Enable PostGIS extension for spatial operations
CREATE EXTENSION IF NOT EXISTS postgis;

-- ---------------------------------------------------------------------------------------------------------------------
CREATE TABLE coordinates (
    id                      BIGSERIAL PRIMARY KEY,
    latitude                DECIMAL(10, 8) NOT NULL,
    longitude               DECIMAL(11, 8) NOT NULL,
    
    -- PostGIS geometry column for spatial indexing and queries
    -- Using geography type for accurate distance calculations on Earth's surface
    geom                    GEOGRAPHY(POINT, 4326),
    
    -- Audit fields
    date_created            TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_date_modified      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by              VARCHAR(255),
    last_modified_by        VARCHAR(255),
    is_deleted              BOOLEAN NOT NULL DEFAULT false
);

-- Create spatial index on geometry column for fast proximity queries
CREATE INDEX idx_coordinates_geom ON coordinates USING GIST (geom);

-- Trigger function to automatically update geometry from latitude/longitude
CREATE OR REPLACE FUNCTION update_coordinates_geom()
RETURNS TRIGGER AS $$
BEGIN
    NEW.geom = ST_SetSRID(ST_MakePoint(NEW.longitude, NEW.latitude), 4326)::geography;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Trigger to auto-update geometry on insert or update
CREATE TRIGGER trg_coordinates_geom_update
    BEFORE INSERT OR UPDATE OF latitude, longitude
    ON coordinates
    FOR EACH ROW
    EXECUTE FUNCTION update_coordinates_geom();

-- =====================================================================================================================
-- SECTION 3: LOCATIONS & HUBS
-- =====================================================================================================================

-- ---------------------------------------------------------------------------------------------------------------------
-- Table: location
-- Description: Rental locations with company ownership
-- ---------------------------------------------------------------------------------------------------------------------
CREATE TABLE location (
    id                      BIGSERIAL PRIMARY KEY,
    external_id             VARCHAR(100) UNIQUE,
    erp_partner_id          VARCHAR(100),
    name                    VARCHAR(255) NOT NULL,
    address                 VARCHAR(500),
    description             VARCHAR(1000),
    company_id              BIGINT NOT NULL,
    is_public               BOOLEAN NOT NULL DEFAULT true,
    directions              VARCHAR(1000),
    coordinates_id          BIGINT,
    
    -- Audit fields
    date_created            TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_date_modified      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by              VARCHAR(255),
    last_modified_by        VARCHAR(255),
    is_deleted              BOOLEAN NOT NULL DEFAULT false,
    
    CONSTRAINT fk_location_coordinates FOREIGN KEY (coordinates_id) 
        REFERENCES coordinates(id) ON DELETE SET NULL,
    CONSTRAINT chk_location_name_not_empty CHECK (name <> ''),
    CONSTRAINT chk_location_company_id CHECK (company_id > 0)
);

-- ---------------------------------------------------------------------------------------------------------------------
-- Table: location_image
-- Description: Images for locations
-- ---------------------------------------------------------------------------------------------------------------------
CREATE TABLE location_image (
    id                      BIGSERIAL PRIMARY KEY,
    external_id             VARCHAR(100) UNIQUE,
    location_id             BIGINT NOT NULL,
    image_url               VARCHAR(500) NOT NULL,
    
    -- Audit fields
    date_created            TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_date_modified      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by              VARCHAR(255),
    last_modified_by        VARCHAR(255),
    is_deleted              BOOLEAN NOT NULL DEFAULT false,
    
    CONSTRAINT fk_location_image_location FOREIGN KEY (location_id) 
        REFERENCES location(id) ON DELETE CASCADE
);

-- ---------------------------------------------------------------------------------------------------------------------
-- Table: hub
-- Description: Hubs within locations for organizing bikes and equipment
-- ---------------------------------------------------------------------------------------------------------------------
CREATE TABLE hub (
    id                      BIGSERIAL PRIMARY KEY,
    external_id             VARCHAR(100) UNIQUE,
    name                    VARCHAR(255) NOT NULL,
    location_id             BIGINT NOT NULL,
    directions              VARCHAR(1000),
    coordinates_id          BIGINT,
    
    -- Audit fields
    date_created            TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_date_modified      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by              VARCHAR(255),
    last_modified_by        VARCHAR(255),
    is_deleted              BOOLEAN NOT NULL DEFAULT false,
    
    CONSTRAINT fk_hub_location FOREIGN KEY (location_id) 
        REFERENCES location(id) ON DELETE CASCADE,
    CONSTRAINT fk_hub_coordinates FOREIGN KEY (coordinates_id) 
        REFERENCES coordinates(id) ON DELETE SET NULL,
    CONSTRAINT chk_hub_name_not_empty CHECK (name <> '')
);

-- ---------------------------------------------------------------------------------------------------------------------
-- Table: hub_image
-- Description: Images for hubs
-- ---------------------------------------------------------------------------------------------------------------------
CREATE TABLE hub_image (
    id                      BIGSERIAL PRIMARY KEY,
    external_id             VARCHAR(100) UNIQUE,
    hub_id                  BIGINT NOT NULL,
    image_url               VARCHAR(500) NOT NULL,
    
    -- Audit fields
    date_created            TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_date_modified      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by              VARCHAR(255),
    last_modified_by        VARCHAR(255),
    is_deleted              BOOLEAN NOT NULL DEFAULT false,
    
    CONSTRAINT fk_hub_image_hub FOREIGN KEY (hub_id) 
        REFERENCES hub(id) ON DELETE CASCADE
);

-- =====================================================================================================================
-- SECTION 4: BRANDS & MODELS
-- =====================================================================================================================

-- ---------------------------------------------------------------------------------------------------------------------
-- Table: bike_brand
-- Description: Bike manufacturers/brands
-- ---------------------------------------------------------------------------------------------------------------------
CREATE TABLE bike_brand (
    id                      BIGSERIAL PRIMARY KEY,
    external_id             VARCHAR(100) UNIQUE,
    name                    VARCHAR(255) NOT NULL,
    company_id              BIGINT,
    
    -- Audit fields
    date_created            TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_date_modified      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by              VARCHAR(255),
    last_modified_by        VARCHAR(255),
    is_deleted              BOOLEAN NOT NULL DEFAULT false,
    
    CONSTRAINT chk_bike_brand_name_not_empty CHECK (name <> '')
);

-- ---------------------------------------------------------------------------------------------------------------------
-- Table: bike_engine
-- Description: Bike electric motor specifications
-- ---------------------------------------------------------------------------------------------------------------------
CREATE TABLE bike_engine (
    id                      BIGSERIAL PRIMARY KEY,
    external_id             VARCHAR(100) UNIQUE,
    name                    VARCHAR(255) NOT NULL,
    
    -- Audit fields
    date_created            TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_date_modified      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by              VARCHAR(255),
    last_modified_by        VARCHAR(255),
    is_deleted              BOOLEAN NOT NULL DEFAULT false,
    
    CONSTRAINT chk_bike_engine_name_not_empty CHECK (name <> '')
);

-- ---------------------------------------------------------------------------------------------------------------------
-- Table: bike_model
-- Description: Bike models with brand and specifications
-- ---------------------------------------------------------------------------------------------------------------------
CREATE TABLE bike_model (
    id                      BIGSERIAL PRIMARY KEY,
    external_id             VARCHAR(100) UNIQUE,
    name                    VARCHAR(255) NOT NULL,
    bike_brand_id           BIGINT NOT NULL,
    bike_type_id            BIGINT NOT NULL,
    bike_engine_id          BIGINT,
    image_url               VARCHAR(500),
    
    -- Audit fields
    date_created            TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_date_modified      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by              VARCHAR(255),
    last_modified_by        VARCHAR(255),
    is_deleted              BOOLEAN NOT NULL DEFAULT false,
    
    CONSTRAINT fk_bike_model_brand FOREIGN KEY (bike_brand_id) 
        REFERENCES bike_brand(id) ON DELETE RESTRICT,
    CONSTRAINT fk_bike_model_type FOREIGN KEY (bike_type_id) 
        REFERENCES bike_type(id) ON DELETE RESTRICT,
    CONSTRAINT fk_bike_model_engine FOREIGN KEY (bike_engine_id) 
        REFERENCES bike_engine(id) ON DELETE SET NULL,
    CONSTRAINT chk_bike_model_name_not_empty CHECK (name <> '')
);

-- ---------------------------------------------------------------------------------------------------------------------
-- Table: charging_station_brand
-- Description: Charging station manufacturers
-- ---------------------------------------------------------------------------------------------------------------------
CREATE TABLE charging_station_brand (
    id                      BIGSERIAL PRIMARY KEY,
    external_id             VARCHAR(100) UNIQUE,
    name                    VARCHAR(255) NOT NULL,
    
    -- Audit fields
    date_created            TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_date_modified      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by              VARCHAR(255),
    last_modified_by        VARCHAR(255),
    is_deleted              BOOLEAN NOT NULL DEFAULT false,
    
    CONSTRAINT chk_charging_station_brand_name_not_empty CHECK (name <> '')
);

-- ---------------------------------------------------------------------------------------------------------------------
-- Table: charging_station_model
-- Description: Charging station models
-- ---------------------------------------------------------------------------------------------------------------------
CREATE TABLE charging_station_model (
    id                              BIGSERIAL PRIMARY KEY,
    external_id                     VARCHAR(100) UNIQUE,
    name                            VARCHAR(255) NOT NULL,
    charging_station_brand_id       BIGINT NOT NULL,
    
    -- Audit fields
    date_created                    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_date_modified              TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by                      VARCHAR(255),
    last_modified_by                VARCHAR(255),
    is_deleted                      BOOLEAN NOT NULL DEFAULT false,
    
    CONSTRAINT fk_charging_station_model_brand FOREIGN KEY (charging_station_brand_id) 
        REFERENCES charging_station_brand(id) ON DELETE RESTRICT,
    CONSTRAINT chk_charging_station_model_name_not_empty CHECK (name <> '')
);

-- ---------------------------------------------------------------------------------------------------------------------
-- Table: part_brand
-- Description: Part manufacturers
-- ---------------------------------------------------------------------------------------------------------------------
CREATE TABLE part_brand (
    id                      BIGSERIAL PRIMARY KEY,
    external_id             VARCHAR(100) UNIQUE,
    name                    VARCHAR(255) NOT NULL,
    
    -- Audit fields
    date_created            TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_date_modified      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by              VARCHAR(255),
    last_modified_by        VARCHAR(255),
    is_deleted              BOOLEAN NOT NULL DEFAULT false,
    
    CONSTRAINT chk_part_brand_name_not_empty CHECK (name <> '')
);

-- ---------------------------------------------------------------------------------------------------------------------
-- Table: part_category
-- Description: Part categories (Battery, Tire, Lock, etc.)
-- ---------------------------------------------------------------------------------------------------------------------
CREATE TABLE part_category (
    id                      BIGSERIAL PRIMARY KEY,
    external_id             VARCHAR(100) UNIQUE,
    name                    VARCHAR(255) NOT NULL,
    
    -- Audit fields
    date_created            TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_date_modified      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by              VARCHAR(255),
    last_modified_by        VARCHAR(255),
    is_deleted              BOOLEAN NOT NULL DEFAULT false,
    
    CONSTRAINT chk_part_category_name_not_empty CHECK (name <> '')
);

-- =====================================================================================================================
-- SECTION 5: PRODUCT TABLE (SINGLE_TABLE Inheritance)
-- =====================================================================================================================
-- One table for all product types: Bike, ChargingStation, Part, ServiceProduct
-- Uses discriminator column 'product_type' to distinguish between types

-- ---------------------------------------------------------------------------------------------------------------------
-- Table: product
-- Description: Base table for all products using SINGLE_TABLE inheritance
-- Discriminator: product_type (BIKE, CHARGING_STATION, PART, SERVICE_PRODUCT)
-- Includes all columns from subclasses (nullable for non-applicable types)
-- ---------------------------------------------------------------------------------------------------------------------
CREATE TABLE product (
    id                              BIGSERIAL PRIMARY KEY,
    external_id                     VARCHAR(100) UNIQUE,
    product_type                    VARCHAR(50) NOT NULL,
    is_b2b_rentable                 BOOLEAN NOT NULL DEFAULT false,
    
    -- Common bike/charging station fields
    code                            VARCHAR(50) UNIQUE,
    qr_code_url                     VARCHAR(500),
    vat                             DECIMAL(5, 2),
    is_vat_include                  BOOLEAN,
    in_service_date                 DATE,
    coordinates_id                  BIGINT,
    hub_id                          BIGINT,
    
    -- Bike-specific fields
    frame_number                    VARCHAR(100),
    bike_status_id                  BIGINT,
    battery_level                   INTEGER NOT NULL DEFAULT 0,
    lock_id                         BIGINT,
    bike_type_id                    BIGINT,
    currency_id                     BIGINT,
    bike_model_id                   BIGINT,
    revenue_share_percent           DECIMAL(5, 2),
    
    -- Charging station-specific fields
    charging_station_status_id      BIGINT,
    charging_station_model_id       BIGINT,
    is_active                       BOOLEAN,
    
    -- Part-specific fields
    name                            VARCHAR(100),
    part_brand_id                   BIGINT,
    image_url                       VARCHAR(500),
    part_category_id                BIGINT,
    b2b_sale_price                  DECIMAL(5, 2),
    quantity                        INTEGER,
    serial_number                   VARCHAR(100),
    
    -- Service product-specific fields
    service_id                      BIGINT,
    
    -- Audit fields
    date_created                    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_date_modified              TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by                      VARCHAR(255),
    last_modified_by                VARCHAR(255),
    is_deleted                      BOOLEAN NOT NULL DEFAULT false,
    
    CONSTRAINT fk_product_coordinates FOREIGN KEY (coordinates_id) 
        REFERENCES coordinates(id) ON DELETE SET NULL,
    CONSTRAINT fk_product_hub FOREIGN KEY (hub_id) 
        REFERENCES hub(id) ON DELETE SET NULL,
    CONSTRAINT fk_product_bike_status FOREIGN KEY (bike_status_id) 
        REFERENCES bike_status(id) ON DELETE SET NULL,
    CONSTRAINT fk_product_bike_type FOREIGN KEY (bike_type_id) 
        REFERENCES bike_type(id) ON DELETE SET NULL,
    CONSTRAINT fk_product_bike_model FOREIGN KEY (bike_model_id) 
        REFERENCES bike_model(id) ON DELETE SET NULL,
    CONSTRAINT fk_product_charging_station_status FOREIGN KEY (charging_station_status_id) 
        REFERENCES charging_station_status(id) ON DELETE SET NULL,
    CONSTRAINT fk_product_charging_station_model FOREIGN KEY (charging_station_model_id) 
        REFERENCES charging_station_model(id) ON DELETE SET NULL,
    CONSTRAINT fk_product_part_brand FOREIGN KEY (part_brand_id) 
        REFERENCES part_brand(id) ON DELETE SET NULL,
    CONSTRAINT fk_product_part_category FOREIGN KEY (part_category_id) 
        REFERENCES part_category(id) ON DELETE SET NULL,
    CONSTRAINT chk_product_type CHECK (product_type IN ('BIKE', 'CHARGING_STATION', 'PART', 'SERVICE_PRODUCT'))
);

-- =====================================================================================================================
-- SECTION 6: HARDWARE (LOCKS & KEYS)
-- =====================================================================================================================

-- ---------------------------------------------------------------------------------------------------------------------
-- Table: lock_entity
-- Description: Electronic locks for bikes
-- ---------------------------------------------------------------------------------------------------------------------
CREATE TABLE lock_entity (
    id                      BIGSERIAL PRIMARY KEY,
    external_id             VARCHAR(100) UNIQUE,
    mac_address             VARCHAR(17) NOT NULL UNIQUE,
    
    -- Audit fields
    date_created            TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_date_modified      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by              VARCHAR(255),
    last_modified_by        VARCHAR(255),
    is_deleted              BOOLEAN NOT NULL DEFAULT false,
    
    CONSTRAINT chk_lock_mac_address_not_empty CHECK (mac_address <> '')
);

-- ---------------------------------------------------------------------------------------------------------------------
-- Table: key_entity
-- Description: Keys for locks (multiple keys per lock)
-- ---------------------------------------------------------------------------------------------------------------------
CREATE TABLE key_entity (
    id                      BIGSERIAL PRIMARY KEY,
    external_id             VARCHAR(100) UNIQUE,
    lock_id                 BIGINT NOT NULL,
    
    -- Audit fields
    date_created            TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_date_modified      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by              VARCHAR(255),
    last_modified_by        VARCHAR(255),
    is_deleted              BOOLEAN NOT NULL DEFAULT false,
    
    CONSTRAINT fk_key_lock FOREIGN KEY (lock_id) 
        REFERENCES lock_entity(id) ON DELETE CASCADE
);

-- Add lock foreign key to product table
ALTER TABLE product ADD CONSTRAINT fk_product_lock FOREIGN KEY (lock_id) 
    REFERENCES lock_entity(id) ON DELETE SET NULL;

-- =====================================================================================================================
-- SECTION 7: RENTAL PLANS
-- =====================================================================================================================

-- ---------------------------------------------------------------------------------------------------------------------
-- Table: rental_plan
-- Description: Rental pricing plans
-- ---------------------------------------------------------------------------------------------------------------------
CREATE TABLE rental_plan (
    id                      BIGSERIAL PRIMARY KEY,
    external_id             VARCHAR(100) UNIQUE,
    name                    VARCHAR(255) NOT NULL,
    description             VARCHAR(1000),
    duration                INTEGER NOT NULL,
    rental_unit_id          BIGINT NOT NULL,
    price                   DECIMAL(10, 2) NOT NULL,
    company_id              BIGINT NOT NULL,
    
    -- Audit fields
    date_created            TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_date_modified      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by              VARCHAR(255),
    last_modified_by        VARCHAR(255),
    is_deleted              BOOLEAN NOT NULL DEFAULT false,
    
    CONSTRAINT fk_rental_plan_unit FOREIGN KEY (rental_unit_id) 
        REFERENCES rental_unit(id) ON DELETE RESTRICT,
    CONSTRAINT chk_rental_plan_name_not_empty CHECK (name <> ''),
    CONSTRAINT chk_rental_plan_duration CHECK (duration > 0),
    CONSTRAINT chk_rental_plan_price CHECK (price >= 0),
    CONSTRAINT chk_rental_plan_company_id CHECK (company_id > 0)
);

-- ---------------------------------------------------------------------------------------------------------------------
-- Table: bike_model_rental_plan
-- Description: Links bike models to rental plans
-- ---------------------------------------------------------------------------------------------------------------------
CREATE TABLE bike_model_rental_plan (
    id                      BIGSERIAL PRIMARY KEY,
    bike_model_id           BIGINT NOT NULL,
    rental_plan_id          BIGINT NOT NULL,
    is_deleted              BOOLEAN NOT NULL DEFAULT false,
    
    CONSTRAINT fk_bike_model_rental_plan_model FOREIGN KEY (bike_model_id) 
        REFERENCES bike_model(id) ON DELETE CASCADE,
    CONSTRAINT fk_bike_model_rental_plan_plan FOREIGN KEY (rental_plan_id) 
        REFERENCES rental_plan(id) ON DELETE CASCADE,
    CONSTRAINT uk_bike_model_rental_plan UNIQUE (bike_model_id, rental_plan_id)
);

-- =====================================================================================================================
-- SECTION 8: B2B SALES & SUBSCRIPTIONS
-- =====================================================================================================================

-- ---------------------------------------------------------------------------------------------------------------------
-- Table: b2b_sale
-- Description: B2B bike sales
-- ---------------------------------------------------------------------------------------------------------------------
CREATE TABLE b2b_sale (
    id                      BIGSERIAL PRIMARY KEY,
    external_id             VARCHAR(100) UNIQUE,
    name                    VARCHAR(255) NOT NULL,
    description             VARCHAR(1000),
    company_id              BIGINT NOT NULL,
    b2b_sale_status_id      BIGINT NOT NULL,
    
    -- Audit fields
    date_created            TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_date_modified      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by              VARCHAR(255),
    last_modified_by        VARCHAR(255),
    is_deleted              BOOLEAN NOT NULL DEFAULT false,
    
    CONSTRAINT fk_b2b_sale_status FOREIGN KEY (b2b_sale_status_id) 
        REFERENCES b2b_sale_status(id) ON DELETE RESTRICT,
    CONSTRAINT chk_b2b_sale_name_not_empty CHECK (name <> ''),
    CONSTRAINT chk_b2b_sale_company_id CHECK (company_id > 0)
);

-- ---------------------------------------------------------------------------------------------------------------------
-- Table: b2b_sale_order
-- Description: Orders for B2B sales
-- ---------------------------------------------------------------------------------------------------------------------
CREATE TABLE b2b_sale_order (
    id                          BIGSERIAL PRIMARY KEY,
    external_id                 VARCHAR(100) UNIQUE,
    b2b_sale_id                 BIGINT NOT NULL,
    b2b_sale_order_status_id    BIGINT NOT NULL,
    total_amount                DECIMAL(10, 2) NOT NULL,
    
    -- Audit fields
    date_created                TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_date_modified          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by                  VARCHAR(255),
    last_modified_by            VARCHAR(255),
    is_deleted                  BOOLEAN NOT NULL DEFAULT false,
    
    CONSTRAINT fk_b2b_sale_order_sale FOREIGN KEY (b2b_sale_id) 
        REFERENCES b2b_sale(id) ON DELETE CASCADE,
    CONSTRAINT fk_b2b_sale_order_status FOREIGN KEY (b2b_sale_order_status_id) 
        REFERENCES b2b_sale_order_status(id) ON DELETE RESTRICT,
    CONSTRAINT chk_b2b_sale_order_total_amount CHECK (total_amount >= 0)
);

-- ---------------------------------------------------------------------------------------------------------------------
-- Table: b2b_sale_item
-- Description: Items in B2B sales
-- ---------------------------------------------------------------------------------------------------------------------
CREATE TABLE b2b_sale_item (
    id                      BIGSERIAL PRIMARY KEY,
    external_id             VARCHAR(100) UNIQUE,
    b2b_sale_id             BIGINT NOT NULL,
    product_id              BIGINT NOT NULL,
    quantity                INTEGER NOT NULL,
    price                   DECIMAL(10, 2) NOT NULL,
    total_price             DECIMAL(10, 2) NOT NULL,
    date_created            TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_date_modified      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by              VARCHAR(255),
    last_modified_by        VARCHAR(255),
    is_deleted              BOOLEAN NOT NULL DEFAULT false,
    
    CONSTRAINT fk_b2b_sale_item_sale FOREIGN KEY (b2b_sale_id) 
        REFERENCES b2b_sale(id) ON DELETE CASCADE,
    CONSTRAINT fk_b2b_sale_item_product FOREIGN KEY (product_id) 
        REFERENCES product(id) ON DELETE RESTRICT,
    CONSTRAINT chk_b2b_sale_item_quantity CHECK (quantity > 0),
    CONSTRAINT chk_b2b_sale_item_price CHECK (price >= 0)
);

CREATE INDEX idx_b2b_sale_item_external_id ON b2b_sale_item(external_id);
CREATE INDEX idx_b2b_sale_item_product_id ON b2b_sale_item(product_id);

-- ---------------------------------------------------------------------------------------------------------------------
-- Table: b2b_sale_order_item
-- Description: Items in B2B sale orders
-- ---------------------------------------------------------------------------------------------------------------------
CREATE TABLE b2b_sale_order_item (
    id                      BIGSERIAL PRIMARY KEY,
    external_id             VARCHAR(100) UNIQUE,
    b2b_sale_order_id       BIGINT NOT NULL,
    product_id              BIGINT NOT NULL,
    quantity                INTEGER NOT NULL,
    price                   DECIMAL(10, 2) NOT NULL,
    total_price             DECIMAL(10, 2) NOT NULL,
    is_deleted              BOOLEAN NOT NULL DEFAULT false,
    
    CONSTRAINT fk_b2b_sale_order_item_order FOREIGN KEY (b2b_sale_order_id) 
        REFERENCES b2b_sale_order(id) ON DELETE CASCADE,
    CONSTRAINT fk_b2b_sale_order_item_product FOREIGN KEY (product_id) 
        REFERENCES product(id) ON DELETE RESTRICT,
    CONSTRAINT chk_b2b_sale_order_item_quantity CHECK (quantity > 0),
    CONSTRAINT chk_b2b_sale_order_item_price CHECK (price >= 0)
);

CREATE INDEX idx_b2b_sale_order_item_external_id ON b2b_sale_order_item(external_id);

-- ---------------------------------------------------------------------------------------------------------------------
-- Table: b2b_subscription
-- Description: B2B subscription plans
-- ---------------------------------------------------------------------------------------------------------------------
CREATE TABLE b2b_subscription (
    id                              BIGSERIAL PRIMARY KEY,
    external_id                     VARCHAR(100) UNIQUE,
    name                            VARCHAR(255) NOT NULL,
    description                     VARCHAR(1000),
    company_id                      BIGINT NOT NULL,
    b2b_subscription_status_id      BIGINT NOT NULL,
    start_date                      TIMESTAMP NOT NULL,
    end_date                        TIMESTAMP,
    
    -- Audit fields
    date_created                    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_date_modified              TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by                      VARCHAR(255),
    last_modified_by                VARCHAR(255),
    is_deleted                      BOOLEAN NOT NULL DEFAULT false,
    
    CONSTRAINT fk_b2b_subscription_status FOREIGN KEY (b2b_subscription_status_id) 
        REFERENCES b2b_subscription_status(id) ON DELETE RESTRICT,
    CONSTRAINT chk_b2b_subscription_name_not_empty CHECK (name <> ''),
    CONSTRAINT chk_b2b_subscription_company_id CHECK (company_id > 0)
);

-- ---------------------------------------------------------------------------------------------------------------------
-- Table: b2b_subscription_item
-- Description: Items in subscriptions
-- ---------------------------------------------------------------------------------------------------------------------
CREATE TABLE b2b_subscription_item (
    id                      BIGSERIAL PRIMARY KEY,
    b2b_subscription_id     BIGINT NOT NULL,
    bike_model_id           BIGINT NOT NULL,
    quantity                INTEGER NOT NULL,
    monthly_price           DECIMAL(10, 2) NOT NULL,
    is_deleted              BOOLEAN NOT NULL DEFAULT false,
    
    CONSTRAINT fk_b2b_subscription_item_subscription FOREIGN KEY (b2b_subscription_id) 
        REFERENCES b2b_subscription(id) ON DELETE CASCADE,
    CONSTRAINT fk_b2b_subscription_item_model FOREIGN KEY (bike_model_id) 
        REFERENCES bike_model(id) ON DELETE RESTRICT,
    CONSTRAINT chk_b2b_subscription_item_quantity CHECK (quantity > 0),
    CONSTRAINT chk_b2b_subscription_item_monthly_price CHECK (monthly_price >= 0)
);

-- ---------------------------------------------------------------------------------------------------------------------
-- Table: b2b_subscription_order
-- Description: Subscription orders
-- ---------------------------------------------------------------------------------------------------------------------
CREATE TABLE b2b_subscription_order (
    id                                      BIGSERIAL PRIMARY KEY,
    external_id                             VARCHAR(100) UNIQUE,
    b2b_subscription_id                     BIGINT NOT NULL,
    b2b_subscription_order_status_id        BIGINT NOT NULL,
    total_amount                            DECIMAL(10, 2) NOT NULL,
    
    -- Audit fields
    date_created                            TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_date_modified                      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by                              VARCHAR(255),
    last_modified_by                        VARCHAR(255),
    is_deleted                              BOOLEAN NOT NULL DEFAULT false,
    
    CONSTRAINT fk_b2b_subscription_order_subscription FOREIGN KEY (b2b_subscription_id) 
        REFERENCES b2b_subscription(id) ON DELETE CASCADE,
    CONSTRAINT fk_b2b_subscription_order_status FOREIGN KEY (b2b_subscription_order_status_id) 
        REFERENCES b2b_subscription_order_status(id) ON DELETE RESTRICT,
    CONSTRAINT chk_b2b_subscription_order_total_amount CHECK (total_amount >= 0)
);

-- ---------------------------------------------------------------------------------------------------------------------
-- Table: b2b_subscription_order_item
-- Description: Items in subscription orders
-- ---------------------------------------------------------------------------------------------------------------------
CREATE TABLE b2b_subscription_order_item (
    id                              BIGSERIAL PRIMARY KEY,
    b2b_subscription_order_id       BIGINT NOT NULL,
    bike_model_id                   BIGINT NOT NULL,
    quantity                        INTEGER NOT NULL,
    unit_price                      DECIMAL(10, 2) NOT NULL,
    is_deleted                      BOOLEAN NOT NULL DEFAULT false,
    
    CONSTRAINT fk_b2b_subscription_order_item_order FOREIGN KEY (b2b_subscription_order_id) 
        REFERENCES b2b_subscription_order(id) ON DELETE CASCADE,
    CONSTRAINT fk_b2b_subscription_order_item_model FOREIGN KEY (bike_model_id) 
        REFERENCES bike_model(id) ON DELETE RESTRICT,
    CONSTRAINT chk_b2b_subscription_order_item_quantity CHECK (quantity > 0),
    CONSTRAINT chk_b2b_subscription_order_item_unit_price CHECK (unit_price >= 0)
);

-- =====================================================================================================================
-- SECTION 9: USER LOCATIONS
-- =====================================================================================================================

-- ---------------------------------------------------------------------------------------------------------------------
-- Table: user_location
-- Description: Links users to locations with roles
-- ---------------------------------------------------------------------------------------------------------------------
CREATE TABLE user_location (
    id                      BIGSERIAL PRIMARY KEY,
    user_id                 BIGINT NOT NULL,
    location_id             BIGINT NOT NULL,
    location_role_id        BIGINT NOT NULL,
    
    -- Audit fields
    date_created            TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_date_modified      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by              VARCHAR(255),
    last_modified_by        VARCHAR(255),
    is_deleted              BOOLEAN NOT NULL DEFAULT false,
    
    CONSTRAINT fk_user_location_location FOREIGN KEY (location_id) 
        REFERENCES location(id) ON DELETE CASCADE,
    CONSTRAINT fk_user_location_role FOREIGN KEY (location_role_id) 
        REFERENCES location_role(id) ON DELETE RESTRICT,
    CONSTRAINT chk_user_location_user_id CHECK (user_id > 0),
    CONSTRAINT uk_user_location UNIQUE (user_id, location_id)
);

-- =====================================================================================================================
-- SECTION 10: RENTALS
-- =====================================================================================================================

-- ---------------------------------------------------------------------------------------------------------------------
-- Table: rental
-- Description: Main rental orders
-- ---------------------------------------------------------------------------------------------------------------------
CREATE TABLE rental (
    id                      BIGSERIAL PRIMARY KEY,
    external_id             VARCHAR(100) UNIQUE,
    user_id                 BIGINT NOT NULL,
    company_id              BIGINT NOT NULL,
    rental_status_id        BIGINT NOT NULL,
    erp_rental_order_id     VARCHAR(100),
    
    -- Audit fields
    date_created            TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_date_modified      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by              VARCHAR(255),
    last_modified_by        VARCHAR(255),
    is_deleted              BOOLEAN NOT NULL DEFAULT false,
    
    CONSTRAINT fk_rental_status FOREIGN KEY (rental_status_id) 
        REFERENCES rental_status(id) ON DELETE RESTRICT,
    CONSTRAINT chk_rental_user_id CHECK (user_id > 0),
    CONSTRAINT chk_rental_company_id CHECK (company_id > 0)
);

-- ---------------------------------------------------------------------------------------------------------------------
-- Table: bike_rental
-- Description: Individual bike rentals within a rental order
-- ---------------------------------------------------------------------------------------------------------------------
CREATE TABLE bike_rental (
    id                          BIGSERIAL PRIMARY KEY,
    external_id                 VARCHAR(100) UNIQUE,
    bike_id                     BIGINT NOT NULL,
    location_id                 BIGINT NOT NULL,
    rental_id                   BIGINT NOT NULL,
    start_date_time             TIMESTAMP NOT NULL,
    end_date_time               TIMESTAMP,
    rental_unit_id              BIGINT,
    bike_rental_status_id       BIGINT,
    is_revenue_share_paid       BOOLEAN NOT NULL DEFAULT false,
    is_b2b_rentable             BOOLEAN NOT NULL DEFAULT false,
    photo_url                   VARCHAR(500),
    price                       DECIMAL(10, 2),
    total_price                 DECIMAL(10, 2) NOT NULL,
    
    -- Audit fields
    date_created                TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_date_modified          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by                  VARCHAR(255),
    last_modified_by            VARCHAR(255),
    is_deleted                  BOOLEAN NOT NULL DEFAULT false,
    
    CONSTRAINT fk_bike_rental_bike FOREIGN KEY (bike_id) 
        REFERENCES product(id) ON DELETE RESTRICT,
    CONSTRAINT fk_bike_rental_location FOREIGN KEY (location_id) 
        REFERENCES location(id) ON DELETE RESTRICT,
    CONSTRAINT fk_bike_rental_rental FOREIGN KEY (rental_id) 
        REFERENCES rental(id) ON DELETE CASCADE,
    CONSTRAINT fk_bike_rental_unit FOREIGN KEY (rental_unit_id) 
        REFERENCES rental_unit(id) ON DELETE SET NULL,
    CONSTRAINT fk_bike_rental_status FOREIGN KEY (bike_rental_status_id) 
        REFERENCES bike_rental_status(id) ON DELETE SET NULL,
    CONSTRAINT chk_bike_rental_price CHECK (price >= 0 OR price IS NULL),
    CONSTRAINT chk_bike_rental_total_price CHECK (total_price >= 0)
);

-- ---------------------------------------------------------------------------------------------------------------------
-- Table: bike_reservation
-- Description: Bike reservations
-- ---------------------------------------------------------------------------------------------------------------------
CREATE TABLE bike_reservation (
    id                      BIGSERIAL PRIMARY KEY,
    external_id             VARCHAR(100) UNIQUE,
    user_id                 BIGINT NOT NULL,
    bike_id                 BIGINT NOT NULL,
    reservation_start       TIMESTAMP NOT NULL,
    reservation_end         TIMESTAMP NOT NULL,
    is_active               BOOLEAN NOT NULL DEFAULT true,
    
    -- Audit fields
    date_created            TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_date_modified      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by              VARCHAR(255),
    last_modified_by        VARCHAR(255),
    is_deleted              BOOLEAN NOT NULL DEFAULT false,
    
    CONSTRAINT fk_bike_reservation_bike FOREIGN KEY (bike_id) 
        REFERENCES product(id) ON DELETE CASCADE,
    CONSTRAINT chk_bike_reservation_user_id CHECK (user_id > 0)
);

-- =====================================================================================================================
-- SECTION 11: RIDES
-- =====================================================================================================================

-- ---------------------------------------------------------------------------------------------------------------------
-- Table: ride
-- Description: Individual rides during a bike rental
-- ---------------------------------------------------------------------------------------------------------------------
CREATE TABLE ride (
    id                      BIGSERIAL PRIMARY KEY,
    external_id             VARCHAR(100) UNIQUE,
    bike_rental_id          BIGINT NOT NULL,
    ride_status_id          BIGINT NOT NULL,
    start_time              TIMESTAMP NOT NULL,
    end_time                TIMESTAMP,
    distance_km             DECIMAL(10, 2),
    start_coordinates_id    BIGINT,
    end_coordinates_id      BIGINT,
    
    -- Audit fields
    date_created            TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_date_modified      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by              VARCHAR(255),
    last_modified_by        VARCHAR(255),
    is_deleted              BOOLEAN NOT NULL DEFAULT false,
    
    CONSTRAINT fk_ride_bike_rental FOREIGN KEY (bike_rental_id) 
        REFERENCES bike_rental(id) ON DELETE CASCADE,
    CONSTRAINT fk_ride_status FOREIGN KEY (ride_status_id) 
        REFERENCES ride_status(id) ON DELETE RESTRICT,
    CONSTRAINT fk_ride_start_coordinates FOREIGN KEY (start_coordinates_id) 
        REFERENCES coordinates(id) ON DELETE SET NULL,
    CONSTRAINT fk_ride_end_coordinates FOREIGN KEY (end_coordinates_id) 
        REFERENCES coordinates(id) ON DELETE SET NULL,
    CONSTRAINT chk_ride_distance CHECK (distance_km >= 0 OR distance_km IS NULL)
);

-- =====================================================================================================================
-- SECTION 12: SERVICES & MAINTENANCE
-- =====================================================================================================================

-- ---------------------------------------------------------------------------------------------------------------------
-- Table: service
-- Description: Maintenance and repair services
-- ---------------------------------------------------------------------------------------------------------------------
CREATE TABLE service (
    id                      BIGSERIAL PRIMARY KEY,
    external_id             VARCHAR(100) UNIQUE,
    name                    VARCHAR(255) NOT NULL,
    description             VARCHAR(1000),
    price                   DECIMAL(10, 2) NOT NULL,
    company_id              BIGINT NOT NULL,
    
    -- Audit fields
    date_created            TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_date_modified      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by              VARCHAR(255),
    last_modified_by        VARCHAR(255),
    is_deleted              BOOLEAN NOT NULL DEFAULT false,
    
    CONSTRAINT chk_service_name_not_empty CHECK (name <> ''),
    CONSTRAINT chk_service_price CHECK (price >= 0),
    CONSTRAINT chk_service_company_id CHECK (company_id > 0)
);

-- Add service foreign key to product table
ALTER TABLE product ADD CONSTRAINT fk_product_service FOREIGN KEY (service_id) 
    REFERENCES service(id) ON DELETE SET NULL;

-- =====================================================================================================================
-- SECTION 13: INVENTORY & STOCK MANAGEMENT
-- =====================================================================================================================

-- ---------------------------------------------------------------------------------------------------------------------
-- Table: stock_movement
-- Description: Track product movements between hubs
-- ---------------------------------------------------------------------------------------------------------------------
CREATE TABLE stock_movement (
    id                      BIGSERIAL PRIMARY KEY,
    external_id             VARCHAR(100) UNIQUE,
    product_id              BIGINT NOT NULL,
    from_hub_id             BIGINT,
    to_hub_id               BIGINT,
    quantity                INTEGER NOT NULL,
    movement_date           TIMESTAMP NOT NULL,
    notes                   VARCHAR(1000),
    
    -- Audit fields
    date_created            TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_date_modified      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by              VARCHAR(255),
    last_modified_by        VARCHAR(255),
    is_deleted              BOOLEAN NOT NULL DEFAULT false,
    
    CONSTRAINT fk_stock_movement_product FOREIGN KEY (product_id) 
        REFERENCES product(id) ON DELETE CASCADE,
    CONSTRAINT fk_stock_movement_from_hub FOREIGN KEY (from_hub_id) 
        REFERENCES hub(id) ON DELETE SET NULL,
    CONSTRAINT fk_stock_movement_to_hub FOREIGN KEY (to_hub_id) 
        REFERENCES hub(id) ON DELETE SET NULL,
    CONSTRAINT chk_stock_movement_quantity CHECK (quantity > 0)
);

-- ---------------------------------------------------------------------------------------------------------------------
-- Table: bike_part
-- Description: Links bikes to their installed parts
-- ---------------------------------------------------------------------------------------------------------------------
CREATE TABLE bike_part (
    id                      BIGSERIAL PRIMARY KEY,
    bike_id                 BIGINT NOT NULL,
    part_id                 BIGINT NOT NULL,
    installation_date       TIMESTAMP NOT NULL,
    is_deleted              BOOLEAN NOT NULL DEFAULT false,
    
    CONSTRAINT fk_bike_part_bike FOREIGN KEY (bike_id) 
        REFERENCES product(id) ON DELETE CASCADE,
    CONSTRAINT fk_bike_part_part FOREIGN KEY (part_id) 
        REFERENCES product(id) ON DELETE CASCADE
);

-- =====================================================================================================================
-- SECTION 14: INDEXES
-- =====================================================================================================================
-- Indexes for improved query performance

-- Product table indexes (inheritance table)
CREATE INDEX idx_product_external_id ON product(external_id);
CREATE INDEX idx_product_type ON product(product_type);
CREATE INDEX idx_product_code ON product(code);
CREATE INDEX idx_product_hub_id ON product(hub_id);
CREATE INDEX idx_product_bike_status_id ON product(bike_status_id);
CREATE INDEX idx_product_bike_model_id ON product(bike_model_id);

-- Location indexes
CREATE INDEX idx_location_external_id ON location(external_id);
CREATE INDEX idx_location_company_id ON location(company_id);
CREATE INDEX idx_location_erp_partner_id ON location(erp_partner_id);

-- Hub indexes
CREATE INDEX idx_hub_external_id ON hub(external_id);
CREATE INDEX idx_hub_location_id ON hub(location_id);

-- Lock & Key indexes
CREATE INDEX idx_lock_external_id ON lock_entity(external_id);
CREATE INDEX idx_lock_mac_address ON lock_entity(mac_address);
CREATE INDEX idx_key_external_id ON key_entity(external_id);
CREATE INDEX idx_key_lock_id ON key_entity(lock_id);

-- Rental indexes
CREATE INDEX idx_rental_external_id ON rental(external_id);
CREATE INDEX idx_rental_user_id ON rental(user_id);
CREATE INDEX idx_rental_company_id ON rental(company_id);
CREATE INDEX idx_rental_erp_order_id ON rental(erp_rental_order_id);
CREATE INDEX idx_rental_status_id ON rental(rental_status_id);

-- Bike Rental indexes
CREATE INDEX idx_bike_rental_external_id ON bike_rental(external_id);
CREATE INDEX idx_bike_rental_bike_id ON bike_rental(bike_id);
CREATE INDEX idx_bike_rental_rental_id ON bike_rental(rental_id);
CREATE INDEX idx_bike_rental_location_id ON bike_rental(location_id);

-- Ride indexes
CREATE INDEX idx_ride_external_id ON ride(external_id);
CREATE INDEX idx_ride_bike_rental_id ON ride(bike_rental_id);
CREATE INDEX idx_ride_status_id ON ride(ride_status_id);

-- B2B Sale indexes
CREATE INDEX idx_b2b_sale_external_id ON b2b_sale(external_id);
CREATE INDEX idx_b2b_sale_company_id ON b2b_sale(company_id);

-- B2B Subscription indexes
CREATE INDEX idx_b2b_subscription_external_id ON b2b_subscription(external_id);
CREATE INDEX idx_b2b_subscription_company_id ON b2b_subscription(company_id);

-- User Location indexes
CREATE INDEX idx_user_location_user_id ON user_location(user_id);
CREATE INDEX idx_user_location_location_id ON user_location(location_id);

-- Bike Model indexes
CREATE INDEX idx_bike_model_external_id ON bike_model(external_id);
CREATE INDEX idx_bike_model_brand_id ON bike_model(bike_brand_id);

-- Rental Plan indexes
CREATE INDEX idx_rental_plan_external_id ON rental_plan(external_id);
CREATE INDEX idx_rental_plan_company_id ON rental_plan(company_id);

-- Service indexes
CREATE INDEX idx_service_external_id ON service(external_id);
CREATE INDEX idx_service_company_id ON service(company_id);

-- =====================================================================================================================
-- SECTION 15: TEST/MOCKUP DATA (OPTIONAL)
-- =====================================================================================================================
-- This section contains sample data for testing and development purposes.
-- Comment out or remove this section before deploying to production if you don't want test data.

-- ---------------------------------------------------------------------------------------------------------------------
-- 15.1 STATUS TABLES DATA
-- ---------------------------------------------------------------------------------------------------------------------

INSERT INTO bike_type (id, name) VALUES
(1, 'Electric bike'),
(2, 'Non-electric bike')
ON CONFLICT (id) DO NOTHING;

INSERT INTO bike_status (id, name) VALUES
(1, 'Available'),
(2, 'Broken'),
(3, 'Disabled'),
(4, 'In use'),
(5, 'Paused'),
(6, 'Reserved')
ON CONFLICT (id) DO NOTHING;

INSERT INTO bike_rental_status (id, name) VALUES
(1, 'Active'),
(2, 'Completed'),
(3, 'Cancelled')
ON CONFLICT (id) DO NOTHING;

INSERT INTO rental_status (id, name) VALUES
(1, 'Pending'),
(2, 'Active'),
(3, 'Completed'),
(4, 'Cancelled')
ON CONFLICT (id) DO NOTHING;

INSERT INTO rental_unit (id, name, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, 'Day', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(2, 'Hour', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(3, 'Week', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(4, 'Month', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

INSERT INTO ride_status (id, name) VALUES
(1, 'Active'),
(2, 'Finished'),
(3, 'Paused')
ON CONFLICT (id) DO NOTHING;

INSERT INTO location_role (id, name) VALUES
(1, 'Admin'),
(2, 'Manager'),
(3, 'Staff'),
(4, 'Viewer')
ON CONFLICT (id) DO NOTHING;

INSERT INTO b2b_sale_status (id, name) VALUES
(1, 'Request'),
(2, 'Ordered'),
(3, 'Pending'),
(4, 'Payment'),
(5, 'Delivered'),
(6, 'Cancelled')
ON CONFLICT (id) DO NOTHING;

INSERT INTO b2b_sale_order_status (id, name) VALUES
(1, 'Pending'),
(2, 'Processing'),
(3, 'Shipped'),
(4, 'Delivered'),
(5, 'Cancelled')
ON CONFLICT (id) DO NOTHING;

INSERT INTO b2b_subscription_status (id, name) VALUES
(1, 'Pending'),
(2, 'Active'),
(3, 'Cancelled'),
(4, 'Expired')
ON CONFLICT (id) DO NOTHING;

INSERT INTO b2b_subscription_order_status (id, name) VALUES
(1, 'Pending'),
(2, 'Active'),
(3, 'Completed'),
(4, 'Cancelled')
ON CONFLICT (id) DO NOTHING;

INSERT INTO charging_station_status (id, name) VALUES
(1, 'Idle'),
(2, 'Pre charging'),
(3, 'Charging'),
(4, 'Fully charged'),
(5, 'Error'),
(6, 'Offline')
ON CONFLICT (id) DO NOTHING;

-- ---------------------------------------------------------------------------------------------------------------------
-- 15.2 COORDINATES
-- ---------------------------------------------------------------------------------------------------------------------
INSERT INTO coordinates (id, latitude, longitude, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, 52.370216, 4.895168, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(2, 51.507351, -0.127758, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(3, 48.856614, 2.352222, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(4, 52.520008, 13.404954, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(5, 41.902782, 12.496366, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- ---------------------------------------------------------------------------------------------------------------------
-- 15.3 LOCATIONS
-- ---------------------------------------------------------------------------------------------------------------------
INSERT INTO location (id, external_id, name, address, company_id, is_public, description, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, 'LOC001', 'Amsterdam Central', 'Stationsplein 1, 1012 AB Amsterdam', 1, true, 'Main location in Amsterdam', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(2, 'LOC002', 'Amsterdam West', 'Westermarkt 20, 1016 DK Amsterdam', 1, true, 'West district location', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(3, 'LOC003', 'London Bridge', 'London Bridge Street, London SE1 9SG', 2, true, 'London main station', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- ---------------------------------------------------------------------------------------------------------------------
-- 15.4 HUBS
-- ---------------------------------------------------------------------------------------------------------------------
INSERT INTO hub (id, external_id, name, location_id, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, 'HUB001', 'Main', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(2, 'HUB002', 'South', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(3, 'HUB003', 'Main', 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- ---------------------------------------------------------------------------------------------------------------------
-- 15.5 BRANDS, ENGINES, AND MODELS
-- ---------------------------------------------------------------------------------------------------------------------
INSERT INTO bike_brand (id, external_id, name, company_id, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, 'BB001', 'VanMoof', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(2, 'BB002', 'Gazelle', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

INSERT INTO bike_engine (id, external_id, name, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, 'BE001', 'Bosch Performance Line 250W', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(2, 'BE002', 'Shimano Steps E6100 250W', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

INSERT INTO bike_model (id, external_id, name, bike_brand_id, bike_type_id, bike_engine_id, image_url, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, 'BM001', 'VanMoof S3', 1, 1, 1, 'https://example.com/vanmoof-s3.jpg', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(2, 'BM002', 'Gazelle Ultimate C380', 2, 1, 2, 'https://example.com/gazelle-c380.jpg', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- ---------------------------------------------------------------------------------------------------------------------
-- 15.6 PRODUCTS (BIKES using SINGLE_TABLE inheritance)
-- ---------------------------------------------------------------------------------------------------------------------
INSERT INTO product (id, external_id, product_type, is_b2b_rentable, code, frame_number, bike_status_id, bike_type_id, bike_model_id, hub_id, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, 'BIKE001', 'BIKE', false, 'BIKE001', 'FR001', 1, 1, 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(2, 'BIKE002', 'BIKE', true, 'BIKE002', 'FR002', 1, 1, 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(3, 'BIKE003', 'BIKE', false, 'BIKE003', 'FR003', 4, 1, 2, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- ---------------------------------------------------------------------------------------------------------------------
-- 15.7 RENTAL PLANS
-- ---------------------------------------------------------------------------------------------------------------------
INSERT INTO rental_plan (id, external_id, name, description, duration, rental_unit_id, price, company_id, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, 'RP001', 'Hourly Plan', 'Pay per hour', 1, 2, 5.00, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(2, 'RP002', 'Daily Plan', 'Pay per day', 1, 1, 25.00, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- =====================================================================================================================
-- SECTION 16: SEQUENCE RESET
-- =====================================================================================================================
-- Reset sequences to the correct values after inserting test data

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
SELECT setval('coordinates_id_seq', (SELECT COALESCE(MAX(id), 1) FROM coordinates));
SELECT setval('location_id_seq', (SELECT COALESCE(MAX(id), 1) FROM location));
SELECT setval('hub_id_seq', (SELECT COALESCE(MAX(id), 1) FROM hub));
SELECT setval('bike_brand_id_seq', (SELECT COALESCE(MAX(id), 1) FROM bike_brand));
SELECT setval('bike_engine_id_seq', (SELECT COALESCE(MAX(id), 1) FROM bike_engine));
SELECT setval('bike_model_id_seq', (SELECT COALESCE(MAX(id), 1) FROM bike_model));
SELECT setval('product_id_seq', (SELECT COALESCE(MAX(id), 1) FROM product));
SELECT setval('rental_plan_id_seq', (SELECT COALESCE(MAX(id), 1) FROM rental_plan));

-- =====================================================================================================================
-- END OF SCHEMA
-- =====================================================================================================================
-- Schema created successfully!
-- Total tables: 51
-- Product inheritance: SINGLE_TABLE strategy with discriminator column
-- Test data: Sample locations, hubs, bikes, and rental plans
-- =====================================================================================================================








