-- =====================================================================================================================
-- SUPPORT SERVICE - DATABASE SCHEMA
-- =====================================================================================================================
-- Module: support-service
-- Database: PostgreSQL
-- Description: Complete database schema for the customer support and feedback service.
--              Handles support requests, bike issues, error codes, feedback, and troubleshooting guides.
-- 
-- Usage:
--   1. Create database: CREATE DATABASE clickenrent_support;
--   2. Import schema: psql -U postgres -d clickenrent_support -f support-service.sql
--
-- Author: Vitaliy Shvetsov
-- =====================================================================================================================

-- Drop existing tables if they exist (in correct order to handle foreign key dependencies)
DROP TABLE IF EXISTS bike_inspection_item_photo CASCADE;
DROP TABLE IF EXISTS bike_inspection_item_bike_unit CASCADE;
DROP TABLE IF EXISTS bike_inspection_item_bike_issue CASCADE;
DROP TABLE IF EXISTS bike_inspection_item CASCADE;
DROP TABLE IF EXISTS bike_inspection CASCADE;
DROP TABLE IF EXISTS bike_inspection_item_status CASCADE;
DROP TABLE IF EXISTS bike_inspection_status CASCADE;
DROP TABLE IF EXISTS support_request_guide_item CASCADE;
DROP TABLE IF EXISTS support_request_bike_issue CASCADE;
DROP TABLE IF EXISTS bike_type_bike_issue CASCADE;
DROP TABLE IF EXISTS support_request CASCADE;
DROP TABLE IF EXISTS bike_rental_feedback CASCADE;
DROP TABLE IF EXISTS feedback CASCADE;
DROP TABLE IF EXISTS bike_issue CASCADE;
DROP TABLE IF EXISTS bike_unit CASCADE;
DROP TABLE IF EXISTS error_code CASCADE;
DROP TABLE IF EXISTS support_request_status CASCADE;
DROP TABLE IF EXISTS responsible_person CASCADE;

-- =====================================================================================================================
-- SECTION 1: LOOKUP & STATUS TABLES
-- =====================================================================================================================

-- ---------------------------------------------------------------------------------------------------------------------
-- Table: support_request_status
-- Description: Status values for support requests (OPEN, IN_PROGRESS, RESOLVED, CLOSED)
-- ---------------------------------------------------------------------------------------------------------------------
CREATE TABLE support_request_status (
    id                      BIGSERIAL PRIMARY KEY,
    name                    VARCHAR(100) NOT NULL UNIQUE,
    
    -- Audit fields
    date_created            TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_date_modified      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by              VARCHAR(255),
    last_modified_by        VARCHAR(255),
    is_deleted              BOOLEAN NOT NULL DEFAULT false,
    
    CONSTRAINT chk_support_request_status_name_not_empty CHECK (name <> '')
);

-- ---------------------------------------------------------------------------------------------------------------------
-- Table: responsible_person
-- Description: Personnel responsible for handling specific bike issues
-- ---------------------------------------------------------------------------------------------------------------------
CREATE TABLE responsible_person (
    id                      BIGSERIAL PRIMARY KEY,
    name                    VARCHAR(255) NOT NULL,
    
    -- Audit fields
    date_created            TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_date_modified      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by              VARCHAR(255),
    last_modified_by        VARCHAR(255),
    is_deleted              BOOLEAN NOT NULL DEFAULT false,
    
    CONSTRAINT chk_responsible_person_name_not_empty CHECK (name <> '')
);

-- ---------------------------------------------------------------------------------------------------------------------
-- Table: bike_inspection_status
-- Description: Status values for bike inspections (PENDING, IN_PROGRESS, COMPLETED, APPROVED)
-- ---------------------------------------------------------------------------------------------------------------------
CREATE TABLE bike_inspection_status (
    id                      BIGSERIAL PRIMARY KEY,
    name                    VARCHAR(100) NOT NULL UNIQUE,
    
    -- Audit fields
    date_created            TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_date_modified      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by              VARCHAR(255),
    last_modified_by        VARCHAR(255),
    is_deleted              BOOLEAN NOT NULL DEFAULT false,
    
    CONSTRAINT chk_bike_inspection_status_name_not_empty CHECK (name <> '')
);

-- ---------------------------------------------------------------------------------------------------------------------
-- Table: bike_inspection_item_status
-- Description: Status values for bike inspection items (OK, DAMAGED, NEEDS_REPAIR)
-- ---------------------------------------------------------------------------------------------------------------------
CREATE TABLE bike_inspection_item_status (
    id                      BIGSERIAL PRIMARY KEY,
    name                    VARCHAR(100) NOT NULL UNIQUE,
    
    -- Audit fields
    date_created            TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_date_modified      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by              VARCHAR(255),
    last_modified_by        VARCHAR(255),
    is_deleted              BOOLEAN NOT NULL DEFAULT false,
    
    CONSTRAINT chk_bike_inspection_item_status_name_not_empty CHECK (name <> '')
);

-- =====================================================================================================================
-- SECTION 2: ISSUE MANAGEMENT TABLES
-- =====================================================================================================================

-- ---------------------------------------------------------------------------------------------------------------------
-- Table: error_code
-- Description: Diagnostic error codes linked to bike engines with troubleshooting information
-- ---------------------------------------------------------------------------------------------------------------------
CREATE TABLE error_code (
    id                      BIGSERIAL PRIMARY KEY,
    external_id             VARCHAR(100) UNIQUE,
    name                    VARCHAR(255) NOT NULL,
    bike_engine_external_id VARCHAR(100),
    description             VARCHAR(1000),
    common_cause            VARCHAR(1000),
    diagnostic_steps        VARCHAR(2000),
    recommended_fix         VARCHAR(1000),
    notes                   VARCHAR(2000),
    is_fixable_by_client    BOOLEAN NOT NULL DEFAULT false,
    
    -- Audit fields
    date_created            TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_date_modified      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by              VARCHAR(255),
    last_modified_by        VARCHAR(255),
    is_deleted              BOOLEAN NOT NULL DEFAULT false,
    
    CONSTRAINT chk_error_code_name_not_empty CHECK (name <> '')
);

-- ---------------------------------------------------------------------------------------------------------------------
-- Table: bike_issue
-- Description: Hierarchical bike issues with parent-child relationships
-- ---------------------------------------------------------------------------------------------------------------------
-- ---------------------------------------------------------------------------------------------------------------------
-- Table: bike_unit
-- Description: Bike unit/component reference data for tracking specific bike parts
-- ---------------------------------------------------------------------------------------------------------------------
CREATE TABLE bike_unit (
    id                      BIGSERIAL PRIMARY KEY,
    external_id             VARCHAR(100) UNIQUE,
    name                    VARCHAR(255) NOT NULL,
    company_external_id     VARCHAR(100) NOT NULL,
    
    -- Audit fields
    date_created            TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_date_modified      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by              VARCHAR(255),
    last_modified_by        VARCHAR(255),
    is_deleted              BOOLEAN NOT NULL DEFAULT false,
    
    CONSTRAINT chk_bike_unit_name_not_empty CHECK (name <> '')
);

-- ---------------------------------------------------------------------------------------------------------------------
-- Table: bike_issue
-- Description: Hierarchical bike issues with parent-child relationships
-- ---------------------------------------------------------------------------------------------------------------------
CREATE TABLE bike_issue (
    id                      BIGSERIAL PRIMARY KEY,
    external_id             VARCHAR(100) UNIQUE,
    erp_external_id         VARCHAR(100) UNIQUE,
    name                    VARCHAR(255) NOT NULL,
    description             VARCHAR(1000),
    parent_bike_issue_id    BIGINT,
    is_fixable_by_client    BOOLEAN NOT NULL DEFAULT false,
    responsible_person_id   BIGINT,
    bike_unit_id            BIGINT,
    
    -- Audit fields
    date_created            TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_date_modified      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by              VARCHAR(255),
    last_modified_by        VARCHAR(255),
    is_deleted              BOOLEAN NOT NULL DEFAULT false,
    
    CONSTRAINT fk_bike_issue_parent FOREIGN KEY (parent_bike_issue_id) 
        REFERENCES bike_issue(id) ON DELETE SET NULL,
    CONSTRAINT fk_bike_issue_responsible_person FOREIGN KEY (responsible_person_id) 
        REFERENCES responsible_person(id) ON DELETE SET NULL,
    CONSTRAINT fk_bike_issue_bike_unit FOREIGN KEY (bike_unit_id)
        REFERENCES bike_unit(id) ON DELETE SET NULL,
    CONSTRAINT chk_bike_issue_name_not_empty CHECK (name <> '')
);

-- =====================================================================================================================
-- SECTION 3: FEEDBACK TABLES
-- =====================================================================================================================

-- ---------------------------------------------------------------------------------------------------------------------
-- Table: feedback
-- Description: General user feedback with ratings (1-5) and comments
-- ---------------------------------------------------------------------------------------------------------------------
CREATE TABLE feedback (
    id                      BIGSERIAL PRIMARY KEY,
    external_id             VARCHAR(100) UNIQUE,
    user_external_id        VARCHAR(100),
    rate                    INTEGER NOT NULL,
    comment                 VARCHAR(2000),
    date_time               TIMESTAMP NOT NULL,
    
    -- Audit fields
    date_created            TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_date_modified      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by              VARCHAR(255),
    last_modified_by        VARCHAR(255),
    is_deleted              BOOLEAN NOT NULL DEFAULT false,
    
    CONSTRAINT chk_feedback_rate CHECK (rate >= 1 AND rate <= 5)
);

-- ---------------------------------------------------------------------------------------------------------------------
-- Table: bike_rental_feedback
-- Description: Feedback specific to bike rentals with ratings (1-5) and comments
-- ---------------------------------------------------------------------------------------------------------------------
CREATE TABLE bike_rental_feedback (
    id                      BIGSERIAL PRIMARY KEY,
    external_id             VARCHAR(100) UNIQUE,
    user_external_id        VARCHAR(100),
    bike_rental_external_id VARCHAR(100),
    rate                    INTEGER NOT NULL,
    comment                 VARCHAR(2000),
    date_time               TIMESTAMP NOT NULL,
    
    -- Audit fields
    date_created            TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_date_modified      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by              VARCHAR(255),
    last_modified_by        VARCHAR(255),
    is_deleted              BOOLEAN NOT NULL DEFAULT false,
    
    CONSTRAINT chk_bike_rental_feedback_rate CHECK (rate >= 1 AND rate <= 5)
);

-- =====================================================================================================================
-- SECTION 4: SUPPORT REQUEST TABLES
-- =====================================================================================================================

-- ---------------------------------------------------------------------------------------------------------------------
-- Table: support_request
-- Description: Main customer support requests
-- ---------------------------------------------------------------------------------------------------------------------
CREATE TABLE support_request (
    id                          BIGSERIAL PRIMARY KEY,
    external_id                 VARCHAR(100) UNIQUE,
    user_external_id            VARCHAR(100),
    bike_external_id            VARCHAR(100),
    is_near_location            BOOLEAN NOT NULL DEFAULT false,
    photo_url                   VARCHAR(500),
    error_code_id               BIGINT,
    support_request_status_id   BIGINT NOT NULL,
    
    -- Audit fields
    date_created                TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_date_modified          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by                  VARCHAR(255),
    last_modified_by            VARCHAR(255),
    is_deleted                  BOOLEAN NOT NULL DEFAULT false,
    
    CONSTRAINT fk_support_request_error_code FOREIGN KEY (error_code_id) 
        REFERENCES error_code(id) ON DELETE SET NULL,
    CONSTRAINT fk_support_request_status FOREIGN KEY (support_request_status_id) 
        REFERENCES support_request_status(id) ON DELETE RESTRICT
);

-- ---------------------------------------------------------------------------------------------------------------------
-- Table: support_request_guide_item
-- Description: Step-by-step troubleshooting guide items
-- ---------------------------------------------------------------------------------------------------------------------
CREATE TABLE support_request_guide_item (
    id                          BIGSERIAL PRIMARY KEY,
    item_index                  INTEGER NOT NULL,
    description                 VARCHAR(2000) NOT NULL,
    bike_issue_id               BIGINT NOT NULL,
    support_request_status_id   BIGINT NOT NULL,
    
    -- Audit fields
    date_created                TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_date_modified          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by                  VARCHAR(255),
    last_modified_by            VARCHAR(255),
    is_deleted                  BOOLEAN NOT NULL DEFAULT false,
    
    CONSTRAINT fk_support_request_guide_item_bike_issue FOREIGN KEY (bike_issue_id) 
        REFERENCES bike_issue(id) ON DELETE CASCADE,
    CONSTRAINT fk_support_request_guide_item_status FOREIGN KEY (support_request_status_id) 
        REFERENCES support_request_status(id) ON DELETE RESTRICT,
    CONSTRAINT chk_support_request_guide_item_description_not_empty CHECK (description <> '')
);

-- =====================================================================================================================
-- SECTION 5: JUNCTION TABLES
-- =====================================================================================================================

-- ---------------------------------------------------------------------------------------------------------------------
-- Table: support_request_bike_issue
-- Description: Junction table linking support requests to multiple bike issues
-- ---------------------------------------------------------------------------------------------------------------------
CREATE TABLE support_request_bike_issue (
    id                      BIGSERIAL PRIMARY KEY,
    support_request_id      BIGINT NOT NULL,
    bike_issue_id           BIGINT NOT NULL,
    
    CONSTRAINT fk_support_request_bike_issue_request FOREIGN KEY (support_request_id) 
        REFERENCES support_request(id) ON DELETE CASCADE,
    CONSTRAINT fk_support_request_bike_issue_issue FOREIGN KEY (bike_issue_id) 
        REFERENCES bike_issue(id) ON DELETE CASCADE,
    CONSTRAINT uk_support_request_bike_issue UNIQUE (support_request_id, bike_issue_id)
);

-- ---------------------------------------------------------------------------------------------------------------------
-- Table: bike_type_bike_issue
-- Description: Junction table linking bike types to their common issues
-- ---------------------------------------------------------------------------------------------------------------------
CREATE TABLE bike_type_bike_issue (
    id                      BIGSERIAL PRIMARY KEY,
    bike_type_external_id   VARCHAR(100),
    bike_issue_id           BIGINT NOT NULL,
    
    CONSTRAINT fk_bike_type_bike_issue_issue FOREIGN KEY (bike_issue_id) 
        REFERENCES bike_issue(id) ON DELETE CASCADE,
    CONSTRAINT uk_bike_type_bike_issue UNIQUE (bike_type_external_id, bike_issue_id)
);

-- =====================================================================================================================
-- SECTION 6: BIKE INSPECTION TABLES
-- =====================================================================================================================

-- ---------------------------------------------------------------------------------------------------------------------
-- Table: bike_inspection
-- Description: Main bike inspections performed by users
-- ---------------------------------------------------------------------------------------------------------------------
CREATE TABLE bike_inspection (
    id                          BIGSERIAL PRIMARY KEY,
    external_id                 VARCHAR(100) UNIQUE,
    user_external_id            VARCHAR(100),
    company_external_id         VARCHAR(100) NOT NULL,
    comment                     VARCHAR(2000),
    bike_inspection_status_id   BIGINT NOT NULL,
    
    -- Audit fields
    date_created                TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_date_modified          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by                  VARCHAR(255),
    last_modified_by            VARCHAR(255),
    is_deleted                  BOOLEAN NOT NULL DEFAULT false,
    
    CONSTRAINT fk_bike_inspection_status FOREIGN KEY (bike_inspection_status_id) 
        REFERENCES bike_inspection_status(id) ON DELETE RESTRICT
);

-- ---------------------------------------------------------------------------------------------------------------------
-- Table: bike_inspection_item
-- Description: Individual items within a bike inspection
-- ---------------------------------------------------------------------------------------------------------------------
CREATE TABLE bike_inspection_item (
    id                                  BIGSERIAL PRIMARY KEY,
    external_id                         VARCHAR(100) UNIQUE,
    bike_inspection_id                  BIGINT NOT NULL,
    bike_external_id                    VARCHAR(100),
    company_external_id                 VARCHAR(100) NOT NULL,
    comment                             VARCHAR(2000),
    bike_inspection_item_status_id      BIGINT NOT NULL,
    error_code_id                       BIGINT,
    
    -- Audit fields
    date_created                        TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_date_modified                  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by                          VARCHAR(255),
    last_modified_by                    VARCHAR(255),
    is_deleted                          BOOLEAN NOT NULL DEFAULT false,
    
    CONSTRAINT fk_bike_inspection_item_inspection FOREIGN KEY (bike_inspection_id) 
        REFERENCES bike_inspection(id) ON DELETE CASCADE,
    CONSTRAINT fk_bike_inspection_item_status FOREIGN KEY (bike_inspection_item_status_id) 
        REFERENCES bike_inspection_item_status(id) ON DELETE RESTRICT,
    CONSTRAINT fk_bike_inspection_item_error_code FOREIGN KEY (error_code_id) 
        REFERENCES error_code(id) ON DELETE SET NULL
);

-- ---------------------------------------------------------------------------------------------------------------------
-- Table: bike_inspection_item_photo
-- Description: Photos attached to bike inspection items
-- ---------------------------------------------------------------------------------------------------------------------
CREATE TABLE bike_inspection_item_photo (
    id                          BIGSERIAL PRIMARY KEY,
    bike_inspection_item_id     BIGINT NOT NULL,
    photo_url                   VARCHAR(500),
    company_external_id         VARCHAR(100) NOT NULL,
    
    -- Audit fields
    date_created                TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_date_modified          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by                  VARCHAR(255),
    last_modified_by            VARCHAR(255),
    is_deleted                  BOOLEAN NOT NULL DEFAULT false,
    
    CONSTRAINT fk_bike_inspection_item_photo_item FOREIGN KEY (bike_inspection_item_id) 
        REFERENCES bike_inspection_item(id) ON DELETE CASCADE
);

-- ---------------------------------------------------------------------------------------------------------------------
-- Table: bike_inspection_item_bike_issue
-- Description: Junction table linking bike inspection items to bike issues
-- ---------------------------------------------------------------------------------------------------------------------
CREATE TABLE bike_inspection_item_bike_issue (
    id                          BIGSERIAL PRIMARY KEY,
    external_id                 VARCHAR(100) UNIQUE,
    bike_inspection_item_id     BIGINT NOT NULL,
    bike_issue_id               BIGINT NOT NULL,
    company_external_id         VARCHAR(100) NOT NULL,
    
    -- Audit fields
    date_created                TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_date_modified          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by                  VARCHAR(255),
    last_modified_by            VARCHAR(255),
    is_deleted                  BOOLEAN NOT NULL DEFAULT false,
    
    CONSTRAINT fk_bike_inspection_item_bike_issue_item FOREIGN KEY (bike_inspection_item_id) 
        REFERENCES bike_inspection_item(id) ON DELETE CASCADE,
    CONSTRAINT fk_bike_inspection_item_bike_issue_issue FOREIGN KEY (bike_issue_id) 
        REFERENCES bike_issue(id) ON DELETE CASCADE,
    CONSTRAINT uk_bike_inspection_item_bike_issue UNIQUE (bike_inspection_item_id, bike_issue_id)
);

-- ---------------------------------------------------------------------------------------------------------------------
-- Table: bike_inspection_item_bike_unit
-- Description: Junction table linking bike inspection items to bike units with problem tracking
-- ---------------------------------------------------------------------------------------------------------------------
CREATE TABLE bike_inspection_item_bike_unit (
    id                          BIGSERIAL PRIMARY KEY,
    external_id                 VARCHAR(100) UNIQUE,
    bike_inspection_item_id     BIGINT NOT NULL,
    bike_unit_id                BIGINT NOT NULL,
    has_problem                 BOOLEAN NOT NULL DEFAULT false,
    company_external_id         VARCHAR(100) NOT NULL,
    
    -- Audit fields
    date_created                TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_date_modified          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by                  VARCHAR(255),
    last_modified_by            VARCHAR(255),
    is_deleted                  BOOLEAN NOT NULL DEFAULT false,
    
    CONSTRAINT fk_bike_inspection_item_bike_unit_item FOREIGN KEY (bike_inspection_item_id) 
        REFERENCES bike_inspection_item(id) ON DELETE CASCADE,
    CONSTRAINT fk_bike_inspection_item_bike_unit_unit FOREIGN KEY (bike_unit_id) 
        REFERENCES bike_unit(id) ON DELETE CASCADE,
    CONSTRAINT uk_bike_inspection_item_bike_unit UNIQUE (bike_inspection_item_id, bike_unit_id)
);

-- =====================================================================================================================
-- SECTION 7: INDEXES
-- =====================================================================================================================
-- Indexes for improved query performance

-- Error Code indexes
CREATE INDEX idx_error_code_external_id ON error_code(external_id);
CREATE INDEX idx_error_code_bike_engine_ext_id ON error_code(bike_engine_external_id);

-- Bike Unit indexes
CREATE INDEX idx_bike_unit_external_id ON bike_unit(external_id);
CREATE INDEX idx_bike_unit_company ON bike_unit(company_external_id);

-- Bike Issue indexes
CREATE INDEX idx_bike_issue_external_id ON bike_issue(external_id);
CREATE INDEX idx_bike_issue_erp_external_id ON bike_issue(erp_external_id);
CREATE INDEX idx_bike_issue_parent ON bike_issue(parent_bike_issue_id);
CREATE INDEX idx_bike_issue_responsible_person ON bike_issue(responsible_person_id);
CREATE INDEX idx_bike_issue_bike_unit ON bike_issue(bike_unit_id);

-- Feedback indexes
CREATE INDEX idx_feedback_external_id ON feedback(external_id);
CREATE INDEX idx_feedback_user_external_id ON feedback(user_external_id);

-- Bike Rental Feedback indexes
CREATE INDEX idx_bike_rental_feedback_external_id ON bike_rental_feedback(external_id);
CREATE INDEX idx_bike_rental_feedback_user_external_id ON bike_rental_feedback(user_external_id);
CREATE INDEX idx_bike_rental_feedback_bike_rental_ext_id ON bike_rental_feedback(bike_rental_external_id);

-- Support Request indexes
CREATE INDEX idx_support_request_external_id ON support_request(external_id);
CREATE INDEX idx_support_request_user_external_id ON support_request(user_external_id);
CREATE INDEX idx_support_request_bike_external_id ON support_request(bike_external_id);
CREATE INDEX idx_support_request_error_code ON support_request(error_code_id);
CREATE INDEX idx_support_request_status ON support_request(support_request_status_id);

-- Support Request Guide Item indexes
CREATE INDEX idx_support_request_guide_item_bike_issue ON support_request_guide_item(bike_issue_id);
CREATE INDEX idx_support_request_guide_item_status ON support_request_guide_item(support_request_status_id);

-- Junction table indexes
CREATE INDEX idx_support_request_bike_issue_request ON support_request_bike_issue(support_request_id);
CREATE INDEX idx_support_request_bike_issue_issue ON support_request_bike_issue(bike_issue_id);
CREATE INDEX idx_bike_type_bike_issue_bike_type_ext_id ON bike_type_bike_issue(bike_type_external_id);
CREATE INDEX idx_bike_type_bike_issue_bike_issue ON bike_type_bike_issue(bike_issue_id);

-- Bike Inspection indexes
CREATE INDEX idx_bike_inspection_external_id ON bike_inspection(external_id);
CREATE INDEX idx_bike_inspection_user_external_id ON bike_inspection(user_external_id);
CREATE INDEX idx_bike_inspection_company ON bike_inspection(company_external_id);
CREATE INDEX idx_bike_inspection_status ON bike_inspection(bike_inspection_status_id);

-- Bike Inspection Item indexes
CREATE INDEX idx_bike_inspection_item_external_id ON bike_inspection_item(external_id);
CREATE INDEX idx_bike_inspection_item_inspection ON bike_inspection_item(bike_inspection_id);
CREATE INDEX idx_bike_inspection_item_bike_external_id ON bike_inspection_item(bike_external_id);
CREATE INDEX idx_bike_inspection_item_company ON bike_inspection_item(company_external_id);
CREATE INDEX idx_bike_inspection_item_status ON bike_inspection_item(bike_inspection_item_status_id);
CREATE INDEX idx_bike_inspection_item_error_code ON bike_inspection_item(error_code_id);

-- Bike Inspection Item Photo indexes
CREATE INDEX idx_bike_inspection_item_photo_item ON bike_inspection_item_photo(bike_inspection_item_id);
CREATE INDEX idx_bike_inspection_item_photo_company ON bike_inspection_item_photo(company_external_id);

-- Bike Inspection Item Bike Issue indexes
CREATE INDEX idx_bike_inspection_item_bike_issue_external_id ON bike_inspection_item_bike_issue(external_id);
CREATE INDEX idx_bike_inspection_item_bike_issue_item ON bike_inspection_item_bike_issue(bike_inspection_item_id);
CREATE INDEX idx_bike_inspection_item_bike_issue_issue ON bike_inspection_item_bike_issue(bike_issue_id);
CREATE INDEX idx_bike_inspection_item_bike_issue_company ON bike_inspection_item_bike_issue(company_external_id);

-- Bike Inspection Item Bike Unit indexes
CREATE INDEX idx_bike_inspection_item_bike_unit_external_id ON bike_inspection_item_bike_unit(external_id);
CREATE INDEX idx_bike_inspection_item_bike_unit_item ON bike_inspection_item_bike_unit(bike_inspection_item_id);
CREATE INDEX idx_bike_inspection_item_bike_unit_unit ON bike_inspection_item_bike_unit(bike_unit_id);
CREATE INDEX idx_bike_inspection_item_bike_unit_company ON bike_inspection_item_bike_unit(company_external_id);

-- =====================================================================================================================
-- SECTION 8: TEST/MOCKUP DATA (OPTIONAL)
-- =====================================================================================================================
-- This section contains sample data for testing and development purposes.
-- Comment out or remove this section before deploying to production if you don't want test data.

-- ---------------------------------------------------------------------------------------------------------------------
-- 8.1 RESPONSIBLE PERSON
-- ---------------------------------------------------------------------------------------------------------------------
INSERT INTO responsible_person (id, name, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, 'John Mechanic', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(2, 'Sarah Electrician', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(3, 'Mike Support Staff', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- ---------------------------------------------------------------------------------------------------------------------
-- 8.2 SUPPORT REQUEST STATUS
-- ---------------------------------------------------------------------------------------------------------------------
INSERT INTO support_request_status (id, name, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, 'OPEN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(2, 'IN_PROGRESS', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(3, 'RESOLVED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(4, 'CLOSED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- ---------------------------------------------------------------------------------------------------------------------
-- 8.3 BIKE INSPECTION STATUS
-- ---------------------------------------------------------------------------------------------------------------------
INSERT INTO bike_inspection_status (id, name, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, 'PENDING', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(2, 'IN_PROGRESS', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(3, 'COMPLETED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(4, 'APPROVED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- ---------------------------------------------------------------------------------------------------------------------
-- 8.4 BIKE INSPECTION ITEM STATUS
-- ---------------------------------------------------------------------------------------------------------------------
INSERT INTO bike_inspection_item_status (id, name, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, 'OK', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(2, 'DAMAGED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(3, 'NEEDS_REPAIR', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(4, 'MISSING', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- ---------------------------------------------------------------------------------------------------------------------
-- 8.5 BIKE ISSUE (Hierarchical Structure)
-- ---------------------------------------------------------------------------------------------------------------------
-- Root Issues
INSERT INTO bike_issue (id, external_id, name, description, parent_bike_issue_id, is_fixable_by_client, responsible_person_id, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, '550e8400-e29b-41d4-a716-446655440101', 'Battery Issues', 'Problems related to bike battery', NULL, false, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(2, '550e8400-e29b-41d4-a716-446655440102', 'Brake Issues', 'Problems with bike braking system', NULL, false, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(3, '550e8400-e29b-41d4-a716-446655440103', 'Motor Issues', 'Problems with electric motor', NULL, false, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- Sub Issues (Battery)
INSERT INTO bike_issue (id, external_id, name, description, parent_bike_issue_id, is_fixable_by_client, responsible_person_id, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(4, '550e8400-e29b-41d4-a716-446655440104', 'Battery Dead', 'Battery completely discharged', 1, true, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(5, '550e8400-e29b-41d4-a716-446655440105', 'Battery Connection Loose', 'Battery connector not properly attached', 1, true, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- Sub Issues (Brakes)
INSERT INTO bike_issue (id, external_id, name, description, parent_bike_issue_id, is_fixable_by_client, responsible_person_id, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(6, '550e8400-e29b-41d4-a716-446655440106', 'Brake Pads Worn', 'Brake pads need replacement', 2, false, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(7, '550e8400-e29b-41d4-a716-446655440107', 'Brake Cable Loose', 'Brake cable needs adjustment', 2, false, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- Sub Issues (Motor)
INSERT INTO bike_issue (id, external_id, name, description, parent_bike_issue_id, is_fixable_by_client, responsible_person_id, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(8, '550e8400-e29b-41d4-a716-446655440108', 'Motor Not Starting', 'Electric motor fails to start', 3, false, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- ---------------------------------------------------------------------------------------------------------------------
-- 8.6 ERROR CODE
-- ---------------------------------------------------------------------------------------------------------------------
INSERT INTO error_code (id, external_id, name, bike_engine_external_id, description, common_cause, diagnostic_steps, recommended_fix, notes, is_fixable_by_client, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, '550e8400-e29b-41d4-a716-446655440201', 'E001', 'bike-engine-ext-001', 'Battery Low Voltage', 'Battery discharged or faulty cell', 'Check battery voltage with multimeter', 'Charge or replace battery', 'Common error in cold weather', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(2, '550e8400-e29b-41d4-a716-446655440202', 'E002', 'bike-engine-ext-001', 'Motor Controller Error', 'Faulty controller or wiring', 'Inspect controller connections', 'Replace motor controller', 'May require professional service', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(3, '550e8400-e29b-41d4-a716-446655440203', 'E003', 'bike-engine-ext-002', 'Throttle Sensor Fault', 'Throttle sensor disconnected or damaged', 'Check throttle sensor connection', 'Reconnect or replace sensor', 'Client can check connection', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(4, '550e8400-e29b-41d4-a716-446655440204', 'E004', 'bike-engine-ext-002', 'Brake Sensor Active', 'Brake lever engaged or sensor stuck', 'Check brake lever and sensor', 'Adjust or clean brake sensor', 'Usually easy fix', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(5, '550e8400-e29b-41d4-a716-446655440205', 'E005', 'bike-engine-ext-003', 'Overheat Protection', 'Motor or controller overheating', 'Let bike cool down for 30 minutes', 'Avoid steep hills or heavy loads', 'Temporary error', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- ---------------------------------------------------------------------------------------------------------------------
-- 8.7 FEEDBACK
-- ---------------------------------------------------------------------------------------------------------------------
INSERT INTO feedback (id, external_id, user_external_id, rate, comment, date_time, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, '550e8400-e29b-41d4-a716-446655440301', 'usr-ext-00007', 5, 'Excellent service, very responsive support team!', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(2, '550e8400-e29b-41d4-a716-446655440302', 'usr-ext-00008', 4, 'Good overall experience, minor delay in response.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(3, '550e8400-e29b-41d4-a716-446655440303', 'usr-ext-00009', 3, 'Average service, could be improved.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(4, '550e8400-e29b-41d4-a716-446655440304', 'usr-ext-00007', 2, 'Not satisfied, took too long to resolve issue.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- ---------------------------------------------------------------------------------------------------------------------
-- 8.8 BIKE RENTAL FEEDBACK
-- ---------------------------------------------------------------------------------------------------------------------
INSERT INTO bike_rental_feedback (id, external_id, user_external_id, bike_rental_external_id, rate, comment, date_time, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, '550e8400-e29b-41d4-a716-446655440501', 'usr-ext-00007', 'bike-rental-ext-00101', 5, 'Great bike, smooth ride!', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(2, '550e8400-e29b-41d4-a716-446655440502', 'usr-ext-00008', 'bike-rental-ext-00102', 4, 'Good bike, but battery could last longer.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(3, '550e8400-e29b-41d4-a716-446655440503', 'usr-ext-00009', 'bike-rental-ext-00103', 3, 'Average experience, brakes were squeaky.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(4, '550e8400-e29b-41d4-a716-446655440504', 'usr-ext-00007', 'bike-rental-ext-00104', 2, 'Poor condition, motor had issues.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- ---------------------------------------------------------------------------------------------------------------------
-- 8.9 SUPPORT REQUEST
-- ---------------------------------------------------------------------------------------------------------------------
INSERT INTO support_request (id, external_id, user_external_id, bike_external_id, is_near_location, photo_url, error_code_id, support_request_status_id, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, '550e8400-e29b-41d4-a716-446655440401', 'usr-ext-00007', 'bike-ext-00201', true, 'https://example.com/photos/issue1.jpg', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(2, '550e8400-e29b-41d4-a716-446655440402', 'usr-ext-00008', 'bike-ext-00202', false, NULL, 2, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(3, '550e8400-e29b-41d4-a716-446655440403', 'usr-ext-00009', 'bike-ext-00203', true, 'https://example.com/photos/issue3.jpg', NULL, 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(4, '550e8400-e29b-41d4-a716-446655440404', 'usr-ext-00007', NULL, false, NULL, 3, 4, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(5, '550e8400-e29b-41d4-a716-446655440405', 'usr-ext-00008', 'bike-ext-00204', true, 'https://example.com/photos/issue5.jpg', 4, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- ---------------------------------------------------------------------------------------------------------------------
-- 8.10 BIKE TYPE BIKE ISSUE (Junction Table)
-- ---------------------------------------------------------------------------------------------------------------------
INSERT INTO bike_type_bike_issue (id, bike_type_external_id, bike_issue_id) VALUES
(1, 'bike-type-ext-001', 1),
(2, 'bike-type-ext-001', 2),
(3, 'bike-type-ext-002', 1),
(4, 'bike-type-ext-002', 3),
(5, 'bike-type-ext-003', 2),
(6, 'bike-type-ext-003', 3)
ON CONFLICT (id) DO NOTHING;

-- ---------------------------------------------------------------------------------------------------------------------
-- 8.11 SUPPORT REQUEST BIKE ISSUE (Junction Table)
-- ---------------------------------------------------------------------------------------------------------------------
INSERT INTO support_request_bike_issue (id, support_request_id, bike_issue_id) VALUES
(1, 1, 1),
(2, 1, 4),
(3, 2, 3),
(4, 2, 8),
(5, 3, 2),
(6, 4, 5),
(7, 5, 6)
ON CONFLICT (id) DO NOTHING;

-- ---------------------------------------------------------------------------------------------------------------------
-- 8.12 SUPPORT REQUEST GUIDE ITEM
-- ---------------------------------------------------------------------------------------------------------------------
INSERT INTO support_request_guide_item (id, item_index, description, bike_issue_id, support_request_status_id, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, 1, 'Check if battery is properly connected', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(2, 2, 'Verify battery charge level on display', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(3, 3, 'Try charging battery for at least 2 hours', 1, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(4, 1, 'Test brake lever responsiveness', 2, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(5, 2, 'Check brake pads for wear and alignment', 2, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(6, 3, 'Adjust brake cable tension if needed', 2, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(7, 1, 'Power cycle the bike (turn off and on)', 3, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(8, 2, 'Check for error codes on display', 3, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- ---------------------------------------------------------------------------------------------------------------------
-- 8.13 BIKE INSPECTION
-- ---------------------------------------------------------------------------------------------------------------------
INSERT INTO bike_inspection (id, external_id, user_external_id, company_external_id, comment, bike_inspection_status_id, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, '550e8400-e29b-41d4-a716-446655440601', 'usr-ext-00007', 'company-ext-001', 'Pre-rental inspection completed', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(2, '550e8400-e29b-41d4-a716-446655440602', 'usr-ext-00008', 'company-ext-001', 'Post-rental inspection - minor issues found', 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(3, '550e8400-e29b-41d4-a716-446655440603', 'usr-ext-00009', 'company-ext-002', 'Routine maintenance inspection', 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- ---------------------------------------------------------------------------------------------------------------------
-- 8.14 BIKE INSPECTION ITEM
-- ---------------------------------------------------------------------------------------------------------------------
INSERT INTO bike_inspection_item (id, external_id, bike_inspection_id, bike_external_id, company_external_id, comment, bike_inspection_item_status_id, error_code_id, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, '550e8400-e29b-41d4-a716-446655440701', 1, 'bike-ext-00201', 'company-ext-001', 'Battery level good', 1, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(2, '550e8400-e29b-41d4-a716-446655440702', 1, 'bike-ext-00201', 'company-ext-001', 'Brakes working properly', 1, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(3, '550e8400-e29b-41d4-a716-446655440703', 2, 'bike-ext-00202', 'company-ext-001', 'Minor scratch on frame', 2, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(4, '550e8400-e29b-41d4-a716-446655440704', 2, 'bike-ext-00202', 'company-ext-001', 'Brake pads worn', 3, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(5, '550e8400-e29b-41d4-a716-446655440705', 3, 'bike-ext-00203', 'company-ext-002', 'Tire pressure low - Display showing E001', 3, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- ---------------------------------------------------------------------------------------------------------------------
-- 8.15 BIKE INSPECTION ITEM PHOTO
-- ---------------------------------------------------------------------------------------------------------------------
INSERT INTO bike_inspection_item_photo (id, bike_inspection_item_id, photo_url, company_external_id, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, 3, 'https://example.com/photos/inspection/scratch-001.jpg', 'company-ext-001', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(2, 3, 'https://example.com/photos/inspection/scratch-002.jpg', 'company-ext-001', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(3, 4, 'https://example.com/photos/inspection/brake-pads-001.jpg', 'company-ext-001', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(4, 5, 'https://example.com/photos/inspection/tire-001.jpg', 'company-ext-002', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- ---------------------------------------------------------------------------------------------------------------------
-- 8.16 BIKE INSPECTION ITEM BIKE ISSUE
-- ---------------------------------------------------------------------------------------------------------------------
INSERT INTO bike_inspection_item_bike_issue (id, external_id, bike_inspection_item_id, bike_issue_id, company_external_id, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, '550e8400-e29b-41d4-a716-446655440801', 4, 6, 'company-ext-001', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(2, '550e8400-e29b-41d4-a716-446655440802', 4, 2, 'company-ext-001', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- =====================================================================================================================
-- SECTION 9: SEQUENCE RESET
-- =====================================================================================================================
-- Reset sequences to the correct values after inserting test data

SELECT setval('responsible_person_id_seq', (SELECT COALESCE(MAX(id), 1) FROM responsible_person));
SELECT setval('support_request_status_id_seq', (SELECT COALESCE(MAX(id), 1) FROM support_request_status));
SELECT setval('bike_inspection_status_id_seq', (SELECT COALESCE(MAX(id), 1) FROM bike_inspection_status));
SELECT setval('bike_inspection_item_status_id_seq', (SELECT COALESCE(MAX(id), 1) FROM bike_inspection_item_status));
SELECT setval('error_code_id_seq', (SELECT COALESCE(MAX(id), 1) FROM error_code));
SELECT setval('bike_issue_id_seq', (SELECT COALESCE(MAX(id), 1) FROM bike_issue));
SELECT setval('feedback_id_seq', (SELECT COALESCE(MAX(id), 1) FROM feedback));
SELECT setval('bike_rental_feedback_id_seq', (SELECT COALESCE(MAX(id), 1) FROM bike_rental_feedback));
SELECT setval('support_request_id_seq', (SELECT COALESCE(MAX(id), 1) FROM support_request));
SELECT setval('support_request_guide_item_id_seq', (SELECT COALESCE(MAX(id), 1) FROM support_request_guide_item));
SELECT setval('support_request_bike_issue_id_seq', (SELECT COALESCE(MAX(id), 1) FROM support_request_bike_issue));
SELECT setval('bike_type_bike_issue_id_seq', (SELECT COALESCE(MAX(id), 1) FROM bike_type_bike_issue));
SELECT setval('bike_inspection_id_seq', (SELECT COALESCE(MAX(id), 1) FROM bike_inspection));
SELECT setval('bike_inspection_item_id_seq', (SELECT COALESCE(MAX(id), 1) FROM bike_inspection_item));
SELECT setval('bike_inspection_item_photo_id_seq', (SELECT COALESCE(MAX(id), 1) FROM bike_inspection_item_photo));
SELECT setval('bike_inspection_item_bike_issue_id_seq', (SELECT COALESCE(MAX(id), 1) FROM bike_inspection_item_bike_issue));

-- =====================================================================================================================
-- END OF SCHEMA
-- =====================================================================================================================
-- Schema created successfully!
-- Total tables: 16
-- Hierarchical structure: BikeIssue with parent-child relationships
-- Tenant isolation: BikeInspection and related entities are company-scoped
-- Test data: Comprehensive support requests, issues, feedback, troubleshooting guides, and bike inspections
-- =====================================================================================================================




