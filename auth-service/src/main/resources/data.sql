-- =====================================================================================================================
-- AUTH SERVICE - DATABASE SCHEMA
-- =====================================================================================================================
-- Module: auth-service
-- Database: PostgreSQL
-- Description: Complete database schema for the authentication and authorization service.
--              Includes user management, company management, roles, addresses, and invitations.
-- 
-- Usage:
--   1. Create database: CREATE DATABASE clickenrent_auth;
--   2. Import schema: psql -U postgres -d clickenrent_auth -f auth-service.sql
--
-- Author: Vitaliy Shvetsov
-- =====================================================================================================================

-- Drop existing tables if they exist (in correct order to handle foreign key dependencies)
DROP TABLE IF EXISTS invitation CASCADE;
DROP TABLE IF EXISTS user_address CASCADE;
DROP TABLE IF EXISTS user_company CASCADE;
DROP TABLE IF EXISTS user_global_role CASCADE;
DROP TABLE IF EXISTS address CASCADE;
DROP TABLE IF EXISTS company CASCADE;
DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS country CASCADE;
DROP TABLE IF EXISTS company_role CASCADE;
DROP TABLE IF EXISTS company_type CASCADE;
DROP TABLE IF EXISTS global_role CASCADE;
DROP TABLE IF EXISTS language CASCADE;

-- =====================================================================================================================
-- SECTION 1: LOOKUP/REFERENCE TABLES
-- =====================================================================================================================
-- These tables contain relatively static reference data that other tables will reference.

-- ---------------------------------------------------------------------------------------------------------------------
-- Table: language
-- Description: Supported languages for user preferences and localization
-- ---------------------------------------------------------------------------------------------------------------------
CREATE TABLE language (
    id                  BIGSERIAL PRIMARY KEY,
    external_id         VARCHAR(100) UNIQUE,
    name                VARCHAR(50) NOT NULL UNIQUE,
    
    CONSTRAINT chk_language_name_not_empty CHECK (name <> '')
);

-- ---------------------------------------------------------------------------------------------------------------------
-- Table: global_role
-- Description: System-wide user roles (SUPERADMIN, ADMIN, B2B, CUSTOMER)
-- ---------------------------------------------------------------------------------------------------------------------
CREATE TABLE global_role (
    id                  BIGSERIAL PRIMARY KEY,
    external_id         VARCHAR(100) UNIQUE,
    name                VARCHAR(50) NOT NULL UNIQUE,
    
    CONSTRAINT chk_global_role_name_not_empty CHECK (name <> '')
);

-- ---------------------------------------------------------------------------------------------------------------------
-- Table: company_type
-- Description: Types of companies (Hotel, B&B, Camping, Vacation Rental, Hostel)
-- ---------------------------------------------------------------------------------------------------------------------
CREATE TABLE company_type (
    id                  BIGSERIAL PRIMARY KEY,
    external_id         VARCHAR(100) UNIQUE,
    name                VARCHAR(100) NOT NULL UNIQUE,
    
    CONSTRAINT chk_company_type_name_not_empty CHECK (name <> '')
);

-- ---------------------------------------------------------------------------------------------------------------------
-- Table: company_role
-- Description: Roles within a company (Owner, Admin, Staff)
-- ---------------------------------------------------------------------------------------------------------------------
CREATE TABLE company_role (
    id                  BIGSERIAL PRIMARY KEY,
    external_id         VARCHAR(100) UNIQUE,
    name                VARCHAR(50) NOT NULL UNIQUE,
    
    CONSTRAINT chk_company_role_name_not_empty CHECK (name <> '')
);

-- ---------------------------------------------------------------------------------------------------------------------
-- Table: country
-- Description: Country reference data for geographic information
-- ---------------------------------------------------------------------------------------------------------------------
CREATE TABLE country (
    id                  BIGSERIAL PRIMARY KEY,
    external_id         VARCHAR(100) UNIQUE,
    name                VARCHAR(100) NOT NULL UNIQUE,
    
    CONSTRAINT chk_country_name_not_empty CHECK (name <> '')
);

-- =====================================================================================================================
-- SECTION 2: CORE BUSINESS TABLES
-- =====================================================================================================================
-- Main business entities with full audit fields

-- ---------------------------------------------------------------------------------------------------------------------
-- Table: users
-- Description: User accounts with profile information, credentials, and preferences
-- Audit Fields: date_created, last_date_modified, created_by, last_modified_by, is_deleted
-- Soft Delete: Supports soft deletion via is_deleted flag
-- ---------------------------------------------------------------------------------------------------------------------
CREATE TABLE users (
    id                          BIGSERIAL PRIMARY KEY,
    external_id                 VARCHAR(100) UNIQUE,
    user_name                   VARCHAR(100) NOT NULL UNIQUE,
    email                       VARCHAR(255) NOT NULL UNIQUE,
    password                    VARCHAR(255),
    first_name                  VARCHAR(100),
    last_name                   VARCHAR(100),
    phone                       VARCHAR(20),
    image_url                   VARCHAR(500),
    language_id                 BIGINT,
    is_active                   BOOLEAN NOT NULL DEFAULT true,
    is_email_verified           BOOLEAN DEFAULT false,
    is_accepted_terms           BOOLEAN DEFAULT false,
    is_accepted_privacy_policy  BOOLEAN DEFAULT false,
    provider_id                 VARCHAR(50),
    provider_user_id            VARCHAR(255),
    
    -- Audit fields
    date_created                TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_date_modified          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by                  VARCHAR(255),
    last_modified_by            VARCHAR(255),
    is_deleted                  BOOLEAN NOT NULL DEFAULT false,
    
    CONSTRAINT fk_users_language FOREIGN KEY (language_id) 
        REFERENCES language(id) ON DELETE SET NULL,
    CONSTRAINT chk_users_username_not_empty CHECK (user_name <> ''),
    CONSTRAINT chk_users_email_not_empty CHECK (email <> '')
);

-- ---------------------------------------------------------------------------------------------------------------------
-- Table: company
-- Description: Company/organization information (hotels, B&Bs, camping sites, etc.)
-- Audit Fields: date_created, last_date_modified, created_by, last_modified_by, is_deleted
-- Soft Delete: Supports soft deletion via is_deleted flag
-- ---------------------------------------------------------------------------------------------------------------------
CREATE TABLE company (
    id                          BIGSERIAL PRIMARY KEY,
    external_id                 VARCHAR(100) UNIQUE,
    name                        VARCHAR(255) NOT NULL,
    description                 VARCHAR(1000),
    website                     VARCHAR(255),
    logo                        VARCHAR(500),
    erp_partner_id              VARCHAR(100),
    company_type_id             BIGINT,
    
    -- Audit fields
    date_created                TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_date_modified          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by                  VARCHAR(255),
    last_modified_by            VARCHAR(255),
    is_deleted                  BOOLEAN NOT NULL DEFAULT false,
    
    CONSTRAINT fk_company_type FOREIGN KEY (company_type_id) 
        REFERENCES company_type(id) ON DELETE SET NULL,
    CONSTRAINT chk_company_name_not_empty CHECK (name <> '')
);

-- ---------------------------------------------------------------------------------------------------------------------
-- Table: address
-- Description: Physical addresses with street, postcode, city, and country information
-- Audit Fields: date_created, last_date_modified, created_by, last_modified_by, is_deleted
-- Soft Delete: Supports soft deletion via is_deleted flag
-- ---------------------------------------------------------------------------------------------------------------------
CREATE TABLE address (
    id                          BIGSERIAL PRIMARY KEY,
    external_id                 VARCHAR(100) UNIQUE,
    city                        VARCHAR(100) NOT NULL,
    country_id                  BIGINT NOT NULL,
    street                      VARCHAR(255),
    postcode                    VARCHAR(20),
    
    -- Audit fields
    date_created                TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_date_modified          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by                  VARCHAR(255),
    last_modified_by            VARCHAR(255),
    is_deleted                  BOOLEAN NOT NULL DEFAULT false,
    
    CONSTRAINT fk_address_country FOREIGN KEY (country_id) 
        REFERENCES country(id) ON DELETE RESTRICT,
    CONSTRAINT chk_address_city_not_empty CHECK (city <> '')
);

-- ---------------------------------------------------------------------------------------------------------------------
-- Table: invitation
-- Description: User invitation system for company onboarding
-- Audit Fields: date_created, last_date_modified, created_by, last_modified_by, is_deleted
-- Soft Delete: Supports soft deletion via is_deleted flag
-- Status Values: PENDING, ACCEPTED, EXPIRED, CANCELLED
-- ---------------------------------------------------------------------------------------------------------------------
CREATE TABLE invitation (
    id                          BIGSERIAL PRIMARY KEY,
    email                       VARCHAR(255) NOT NULL,
    token                       VARCHAR(255) NOT NULL UNIQUE,
    invited_by_user_id          BIGINT NOT NULL,
    company_id                  BIGINT NOT NULL,
    status                      VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    expires_at                  TIMESTAMP NOT NULL,
    accepted_at                 TIMESTAMP,
    
    -- Audit fields
    date_created                TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_date_modified          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by                  VARCHAR(255),
    last_modified_by            VARCHAR(255),
    is_deleted                  BOOLEAN NOT NULL DEFAULT false,
    
    CONSTRAINT fk_invitation_user FOREIGN KEY (invited_by_user_id) 
        REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_invitation_company FOREIGN KEY (company_id) 
        REFERENCES company(id) ON DELETE CASCADE,
    CONSTRAINT chk_invitation_email_not_empty CHECK (email <> ''),
    CONSTRAINT chk_invitation_token_not_empty CHECK (token <> ''),
    CONSTRAINT chk_invitation_status CHECK (status IN ('PENDING', 'ACCEPTED', 'EXPIRED', 'CANCELLED'))
);

-- =====================================================================================================================
-- SECTION 3: JUNCTION/RELATIONSHIP TABLES
-- =====================================================================================================================
-- Join tables for many-to-many relationships between core entities

-- ---------------------------------------------------------------------------------------------------------------------
-- Table: user_global_role
-- Description: Links users to their global system roles (many-to-many)
-- Audit Fields: date_created, last_date_modified, created_by, last_modified_by, is_deleted
-- Soft Delete: Supports soft deletion via is_deleted flag
-- ---------------------------------------------------------------------------------------------------------------------
CREATE TABLE user_global_role (
    id                          BIGSERIAL PRIMARY KEY,
    external_id                 VARCHAR(100) UNIQUE,
    user_id                     BIGINT NOT NULL,
    global_role_id              BIGINT NOT NULL,
    
    -- Audit fields
    date_created                TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_date_modified          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by                  VARCHAR(255),
    last_modified_by            VARCHAR(255),
    is_deleted                  BOOLEAN NOT NULL DEFAULT false,
    
    CONSTRAINT fk_user_global_role_user FOREIGN KEY (user_id) 
        REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_user_global_role_role FOREIGN KEY (global_role_id) 
        REFERENCES global_role(id) ON DELETE CASCADE,
    CONSTRAINT uk_user_global_role UNIQUE (user_id, global_role_id)
);

-- ---------------------------------------------------------------------------------------------------------------------
-- Table: user_company
-- Description: Links users to companies with specific company roles (many-to-many)
-- Audit Fields: date_created, last_date_modified, created_by, last_modified_by, is_deleted
-- Soft Delete: Supports soft deletion via is_deleted flag
-- ---------------------------------------------------------------------------------------------------------------------
CREATE TABLE user_company (
    id                          BIGSERIAL PRIMARY KEY,
    external_id                 VARCHAR(100) UNIQUE,
    user_id                     BIGINT NOT NULL,
    company_id                  BIGINT NOT NULL,
    company_role_id             BIGINT NOT NULL,
    
    -- Audit fields
    date_created                TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_date_modified          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by                  VARCHAR(255),
    last_modified_by            VARCHAR(255),
    is_deleted                  BOOLEAN NOT NULL DEFAULT false,
    
    CONSTRAINT fk_user_company_user FOREIGN KEY (user_id) 
        REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_user_company_company FOREIGN KEY (company_id) 
        REFERENCES company(id) ON DELETE CASCADE,
    CONSTRAINT fk_user_company_role FOREIGN KEY (company_role_id) 
        REFERENCES company_role(id) ON DELETE RESTRICT,
    CONSTRAINT uk_user_company UNIQUE (user_id, company_id)
);

-- ---------------------------------------------------------------------------------------------------------------------
-- Table: user_address
-- Description: Links users to addresses (many-to-many)
-- Audit Fields: date_created, last_date_modified, created_by, last_modified_by, is_deleted
-- Soft Delete: Supports soft deletion via is_deleted flag
-- ---------------------------------------------------------------------------------------------------------------------
CREATE TABLE user_address (
    id                          BIGSERIAL PRIMARY KEY,
    external_id                 VARCHAR(100) UNIQUE,
    user_id                     BIGINT NOT NULL,
    address_id                  BIGINT NOT NULL,
    
    -- Audit fields
    date_created                TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_date_modified          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by                  VARCHAR(255),
    last_modified_by            VARCHAR(255),
    is_deleted                  BOOLEAN NOT NULL DEFAULT false,
    
    CONSTRAINT fk_user_address_user FOREIGN KEY (user_id) 
        REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_user_address_address FOREIGN KEY (address_id) 
        REFERENCES address(id) ON DELETE CASCADE,
    CONSTRAINT uk_user_address UNIQUE (user_id, address_id)
);

-- =====================================================================================================================
-- SECTION 4: INDEXES
-- =====================================================================================================================
-- Indexes for improved query performance based on entity annotations

-- Users table indexes
CREATE INDEX idx_user_external_id ON users(external_id);
CREATE INDEX idx_user_email ON users(email);
CREATE INDEX idx_user_username ON users(user_name);
CREATE INDEX idx_user_provider ON users(provider_id, provider_user_id);

-- Company table indexes
CREATE INDEX idx_company_external_id ON company(external_id);

-- Language table indexes
CREATE INDEX idx_language_external_id ON language(external_id);

-- Global role table indexes
CREATE INDEX idx_global_role_external_id ON global_role(external_id);

-- Company type table indexes
CREATE INDEX idx_company_type_external_id ON company_type(external_id);

-- Company role table indexes
CREATE INDEX idx_company_role_external_id ON company_role(external_id);

-- Country table indexes
CREATE INDEX idx_country_external_id ON country(external_id);
CREATE INDEX idx_country_name ON country(name);

-- Address table indexes
CREATE INDEX idx_address_external_id ON address(external_id);
CREATE INDEX idx_address_country ON address(country_id);

-- User global role table indexes
CREATE INDEX idx_user_global_role_external_id ON user_global_role(external_id);

-- User company table indexes
CREATE INDEX idx_user_company_external_id ON user_company(external_id);

-- User address table indexes
CREATE INDEX idx_user_address_external_id ON user_address(external_id);

-- Invitation table indexes
CREATE INDEX idx_invitation_token ON invitation(token);
CREATE INDEX idx_invitation_email ON invitation(email);
CREATE INDEX idx_invitation_status ON invitation(status);

-- =====================================================================================================================
-- SECTION 5: TEST/MOCKUP DATA (OPTIONAL)
-- =====================================================================================================================
-- This section contains sample data for testing and development purposes.
-- Comment out or remove this section before deploying to production if you don't want test data.
-- All user passwords are: Test123
-- Bcrypt hash: $2a$10$59i5SxRWkbcxt2rfTyjJ2.dZrlXFchPqyw1p56D/Ltp6jvYGVh2YK

-- ---------------------------------------------------------------------------------------------------------------------
-- 5.1 LOOKUP TABLE DATA
-- ---------------------------------------------------------------------------------------------------------------------

-- Languages
INSERT INTO language (id, name) VALUES
(1, 'English'),
(2, 'German'),
(3, 'French'),
(4, 'Spanish'),
(5, 'Italian')
ON CONFLICT (id) DO NOTHING;

-- Global Roles
INSERT INTO global_role (id, name) VALUES
(1, 'SUPERADMIN'),
(2, 'ADMIN'),
(3, 'B2B'),
(4, 'CUSTOMER'),
(5, 'DEV'),
(6, 'SYSTEM')
ON CONFLICT (id) DO NOTHING;

-- Company Types
INSERT INTO company_type (id, name) VALUES
(1, 'Hotel'),
(2, 'B&B'),
(3, 'Camping'),
(4, 'Vacation Rental'),
(5, 'Hostel')
ON CONFLICT (id) DO NOTHING;

-- Company Roles
INSERT INTO company_role (id, name) VALUES
(1, 'Owner'),
(2, 'Admin'),
(3, 'Staff')
ON CONFLICT (id) DO NOTHING;

-- Countries
INSERT INTO country (id, name) VALUES
(1, 'Germany'),
(2, 'Austria'),
(3, 'Switzerland'),
(4, 'France'),
(5, 'Italy'),
(6, 'USA')
ON CONFLICT (id) DO NOTHING;

-- ---------------------------------------------------------------------------------------------------------------------
-- 5.2 USERS DATA
-- Password for all users: Test123!
-- ---------------------------------------------------------------------------------------------------------------------

INSERT INTO users (id, external_id, user_name, email, password, first_name, last_name, phone, language_id, is_active, is_email_verified, is_accepted_terms, is_accepted_privacy_policy, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
-- SUPERADMIN
(1, 'usr-ext-00001', 'superadmin', 'superadmin@clickenrent.com', '$2a$10$59i5SxRWkbcxt2rfTyjJ2.dZrlXFchPqyw1p56D/Ltp6jvYGVh2YK', 'Super', 'Admin', '+49-30-12345001', 1, true, true, true, true, NOW(), NOW(), 'system', 'system', false),

-- ADMIN Users
(2, 'usr-ext-00002', 'admin_john', 'john.admin@clickenrent.com', '$2a$10$59i5SxRWkbcxt2rfTyjJ2.dZrlXFchPqyw1p56D/Ltp6jvYGVh2YK', 'John', 'Administrator', '+49-89-12345002', 1, true, true, true, true, NOW(), NOW(), 'system', 'system', false),
(3, 'usr-ext-00003', 'admin_sarah', 'sarah.admin@clickenrent.com', '$2a$10$59i5SxRWkbcxt2rfTyjJ2.dZrlXFchPqyw1p56D/Ltp6jvYGVh2YK', 'Sarah', 'Manager', '+41-44-12345003', 2, true, true, true, true, NOW(), NOW(), 'system', 'system', false),

-- B2B Users (Hotel/Company Owners/Managers)
(4, 'usr-ext-00004', 'hotelowner_max', 'max.mueller@grandhotel.de', '$2a$10$59i5SxRWkbcxt2rfTyjJ2.dZrlXFchPqyw1p56D/Ltp6jvYGVh2YK', 'Max', 'Mueller', '+49-30-55512001', 2, true, true, true, true, NOW(), NOW(), 'system', 'system', false),
(5, 'usr-ext-00005', 'hotelowner_anna', 'anna.schmidt@alpineresort.at', '$2a$10$59i5SxRWkbcxt2rfTyjJ2.dZrlXFchPqyw1p56D/Ltp6jvYGVh2YK', 'Anna', 'Schmidt', '+43-662-88800001', 2, true, true, true, true, NOW(), NOW(), 'system', 'system', false),
(6, 'usr-ext-00006', 'campingowner_pierre', 'pierre.dubois@camping-provence.fr', '$2a$10$59i5SxRWkbcxt2rfTyjJ2.dZrlXFchPqyw1p56D/Ltp6jvYGVh2YK', 'Pierre', 'Dubois', '+33-4-90-123456', 3, true, true, true, true, NOW(), NOW(), 'system', 'system', false),

-- CUSTOMER Users
(7, 'usr-ext-00007', 'customer_tom', 'tom.wilson@email.com', '$2a$10$59i5SxRWkbcxt2rfTyjJ2.dZrlXFchPqyw1p56D/Ltp6jvYGVh2YK', 'Tom', 'Wilson', '+1-212-555-0101', 1, true, true, true, true, NOW(), NOW(), 'system', 'system', false),
(8, 'usr-ext-00008', 'customer_emma', 'emma.johnson@email.com', '$2a$10$59i5SxRWkbcxt2rfTyjJ2.dZrlXFchPqyw1p56D/Ltp6jvYGVh2YK', 'Emma', 'Johnson', '+49-40-555-0102', 2, true, true, true, true, NOW(), NOW(), 'system', 'system', false),
(9, 'usr-ext-00009', 'customer_luca', 'luca.rossi@email.it', '$2a$10$59i5SxRWkbcxt2rfTyjJ2.dZrlXFchPqyw1p56D/Ltp6jvYGVh2YK', 'Luca', 'Rossi', '+39-06-555-0103', 5, true, true, true, true, NOW(), NOW(), 'system', 'system', false),
(10, 'usr-ext-00010', 'customer_marie', 'marie.blanc@email.fr', '$2a$10$59i5SxRWkbcxt2rfTyjJ2.dZrlXFchPqyw1p56D/Ltp6jvYGVh2YK', 'Marie', 'Blanc', '+33-1-555-0104', 3, true, true, true, true, NOW(), NOW(), 'system', 'system', false),
(11, 'usr-ext-00011', 'customer_hans', 'hans.weber@email.de', '$2a$10$59i5SxRWkbcxt2rfTyjJ2.dZrlXFchPqyw1p56D/Ltp6jvYGVh2YK', 'Hans', 'Weber', '+49-89-555-0105', 2, true, true, true, true, NOW(), NOW(), 'system', 'system', false),
(12, 'usr-ext-00012', 'customer_sophia', 'sophia.martin@email.com', '$2a$10$59i5SxRWkbcxt2rfTyjJ2.dZrlXFchPqyw1p56D/Ltp6jvYGVh2YK', 'Sophia', 'Martin', '+41-22-555-0106', 1, true, true, true, true, NOW(), NOW(), 'system', 'system', false),
(13, 'usr-ext-00013', 'customer_oliver', 'oliver.brown@email.com', '$2a$10$59i5SxRWkbcxt2rfTyjJ2.dZrlXFchPqyw1p56D/Ltp6jvYGVh2YK', 'Oliver', 'Brown', '+1-310-555-0107', 1, true, false, true, true, NOW(), NOW(), 'system', 'system', false),

-- SYSTEM Service Account (for inter-service communication)
(14, 'usr-ext-service-payment', 'service_payment', 'service.payment@clickenrent.internal', '$2a$10$59i5SxRWkbcxt2rfTyjJ2.dZrlXFchPqyw1p56D/Ltp6jvYGVh2YK', 'Payment', 'Service', '+00-00-00000000', 1, true, true, true, true, NOW(), NOW(), 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- ---------------------------------------------------------------------------------------------------------------------
-- 5.3 USER GLOBAL ROLES
-- ---------------------------------------------------------------------------------------------------------------------

INSERT INTO user_global_role (id, user_id, global_role_id, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
-- SUPERADMIN
(1, 1, 1, NOW(), NOW(), 'system', 'system', false),

-- ADMIN
(2, 2, 2, NOW(), NOW(), 'system', 'system', false),
(3, 3, 2, NOW(), NOW(), 'system', 'system', false),

-- B2B
(4, 4, 3, NOW(), NOW(), 'system', 'system', false),
(5, 5, 3, NOW(), NOW(), 'system', 'system', false),
(6, 6, 3, NOW(), NOW(), 'system', 'system', false),

-- CUSTOMER
(7, 7, 4, NOW(), NOW(), 'system', 'system', false),
(8, 8, 4, NOW(), NOW(), 'system', 'system', false),
(9, 9, 4, NOW(), NOW(), 'system', 'system', false),
(10, 10, 4, NOW(), NOW(), 'system', 'system', false),
(11, 11, 4, NOW(), NOW(), 'system', 'system', false),
(12, 12, 4, NOW(), NOW(), 'system', 'system', false),
(13, 13, 4, NOW(), NOW(), 'system', 'system', false),

-- SYSTEM (Service Accounts)
(14, 14, 6, NOW(), NOW(), 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- ---------------------------------------------------------------------------------------------------------------------
-- 5.4 COMPANIES
-- ---------------------------------------------------------------------------------------------------------------------

INSERT INTO company (id, external_id, name, description, website, logo, erp_partner_id, company_type_id, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, 'comp-ext-00001', 'Grand Hotel Berlin', 'Luxury 5-star hotel in the heart of Berlin with exceptional service and amenities', 'https://grandhotelberlin.de', 'https://cdn.example.com/logos/grand-hotel-berlin.png', 'ERP-HTL-001', 1, NOW(), NOW(), 'system', 'system', false),
(2, 'comp-ext-00002', 'Alpine Resort Salzburg', 'Traditional Austrian resort with stunning mountain views and spa facilities', 'https://alpineresort.at', 'https://cdn.example.com/logos/alpine-resort.png', 'ERP-HTL-002', 1, NOW(), NOW(), 'system', 'system', false),
(3, 'comp-ext-00003', 'Camping Provence', 'Family-friendly camping site in beautiful Provence countryside', 'https://camping-provence.fr', 'https://cdn.example.com/logos/camping-provence.png', 'ERP-CMP-001', 3, NOW(), NOW(), 'system', 'system', false),
(4, 'comp-ext-00004', 'Cozy B&B Vienna', 'Charming bed and breakfast in historic Vienna district', 'https://cozybnbvienna.at', 'https://cdn.example.com/logos/cozy-bnb.png', 'ERP-BNB-001', 2, NOW(), NOW(), 'system', 'system', false),
(5, 'comp-ext-00005', 'City Hostel Munich', 'Modern hostel with great location for backpackers and budget travelers', 'https://cityhostelmunich.de', 'https://cdn.example.com/logos/city-hostel.png', 'ERP-HST-001', 5, NOW(), NOW(), 'system', 'system', false),
(6, 'comp-ext-00006', 'Lake View Vacation Rentals', 'Premium vacation rentals with stunning lake views in Switzerland', 'https://lakeviewrentals.ch', 'https://cdn.example.com/logos/lakeview.png', 'ERP-VRC-001', 4, NOW(), NOW(), 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- ---------------------------------------------------------------------------------------------------------------------
-- 5.5 USER COMPANY RELATIONSHIPS
-- ---------------------------------------------------------------------------------------------------------------------

INSERT INTO user_company (id, user_id, company_id, company_role_id, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
-- Max Mueller owns Grand Hotel Berlin
(1, 4, 1, 1, NOW(), NOW(), 'system', 'system', false),

-- Anna Schmidt owns Alpine Resort Salzburg
(2, 5, 2, 1, NOW(), NOW(), 'system', 'system', false),

-- Pierre Dubois owns Camping Provence
(3, 6, 3, 1, NOW(), NOW(), 'system', 'system', false),

-- Max Mueller is also admin at City Hostel Munich (multi-company owner)
(4, 4, 5, 2, NOW(), NOW(), 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- ---------------------------------------------------------------------------------------------------------------------
-- 5.6 ADDRESSES
-- ---------------------------------------------------------------------------------------------------------------------

INSERT INTO address (id, city, country_id, street, postcode, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, 'Berlin', 1, 'Friedrichstrasse 100', '10117', NOW(), NOW(), 'system', 'system', false),
(2, 'Munich', 1, 'Marienplatz 8', '80331', NOW(), NOW(), 'system', 'system', false),
(3, 'Hamburg', 1, 'Reeperbahn 50', '20359', NOW(), NOW(), 'system', 'system', false),
(4, 'Vienna', 2, 'Kärntner Strasse 15', '1010', NOW(), NOW(), 'system', 'system', false),
(5, 'Salzburg', 2, 'Getreidegasse 25', '5020', NOW(), NOW(), 'system', 'system', false),
(6, 'Zurich', 3, 'Bahnhofstrasse 45', '8001', NOW(), NOW(), 'system', 'system', false),
(7, 'Geneva', 3, 'Rue du Rhône 42', '1204', NOW(), NOW(), 'system', 'system', false),
(8, 'Paris', 4, 'Champs-Élysées 88', '75008', NOW(), NOW(), 'system', 'system', false),
(9, 'Lyon', 4, 'Route de Camping 15', '69001', NOW(), NOW(), 'system', 'system', false),
(10, 'Rome', 5, 'Via del Corso 120', '00186', NOW(), NOW(), 'system', 'system', false),
(11, 'Milan', 5, 'Via Montenapoleone 10', '20121', NOW(), NOW(), 'system', 'system', false),
(12, 'New York', 6, '123 Broadway', '10001', NOW(), NOW(), 'system', 'system', false),
(13, 'Los Angeles', 6, 'Sunset Blvd 500', '90028', NOW(), NOW(), 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- ---------------------------------------------------------------------------------------------------------------------
-- 5.7 USER ADDRESS RELATIONSHIPS
-- ---------------------------------------------------------------------------------------------------------------------

INSERT INTO user_address (id, user_id, address_id, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, 4, 1, NOW(), NOW(), 'system', 'system', false),  -- Max -> Berlin address
(2, 5, 5, NOW(), NOW(), 'system', 'system', false),  -- Anna -> Salzburg address
(3, 6, 9, NOW(), NOW(), 'system', 'system', false),  -- Pierre -> Lyon address
(4, 7, 12, NOW(), NOW(), 'system', 'system', false), -- Tom -> New York address
(5, 8, 3, NOW(), NOW(), 'system', 'system', false),  -- Emma -> Hamburg address
(6, 9, 10, NOW(), NOW(), 'system', 'system', false), -- Luca -> Rome address
(7, 10, 8, NOW(), NOW(), 'system', 'system', false), -- Marie -> Paris address
(8, 11, 2, NOW(), NOW(), 'system', 'system', false), -- Hans -> Munich address
(9, 12, 7, NOW(), NOW(), 'system', 'system', false)  -- Sophia -> Geneva address
ON CONFLICT (id) DO NOTHING;

-- ---------------------------------------------------------------------------------------------------------------------
-- 5.8 INVITATIONS
-- ---------------------------------------------------------------------------------------------------------------------

INSERT INTO invitation (id, email, token, invited_by_user_id, company_id, status, expires_at, accepted_at, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
-- Pending invitation
(1, 'newstaff@example.com', 'inv-token-pending-001', 4, 1, 'PENDING', NOW() + INTERVAL '7 days', NULL, NOW(), NOW(), 'hotelowner_max', 'hotelowner_max', false),

-- Another pending invitation
(2, 'manager@alpine.com', 'inv-token-pending-002', 5, 2, 'PENDING', NOW() + INTERVAL '14 days', NULL, NOW(), NOW(), 'hotelowner_anna', 'hotelowner_anna', false),

-- Accepted invitation
(3, 'staff@camping.com', 'inv-token-accepted-001', 6, 3, 'ACCEPTED', NOW() + INTERVAL '30 days', NOW() - INTERVAL '2 days', NOW() - INTERVAL '5 days', NOW(), 'campingowner_pierre', 'campingowner_pierre', false),

-- Expired invitation
(4, 'oldstaff@example.com', 'inv-token-expired-001', 4, 1, 'PENDING', NOW() - INTERVAL '5 days', NULL, NOW() - INTERVAL '12 days', NOW(), 'hotelowner_max', 'hotelowner_max', false),

-- Cancelled invitation
(5, 'cancelled@example.com', 'inv-token-cancelled-001', 5, 2, 'CANCELLED', NOW() + INTERVAL '7 days', NULL, NOW() - INTERVAL '3 days', NOW(), 'hotelowner_anna', 'hotelowner_anna', false)
ON CONFLICT (id) DO NOTHING;

-- =====================================================================================================================
-- SECTION 6: ROW LEVEL SECURITY
-- =====================================================================================================================
-- PostgreSQL RLS policies for database-level tenant isolation
-- This is the final layer of defense-in-depth security

-- Enable RLS on user_company table
ALTER TABLE user_company ENABLE ROW LEVEL SECURITY;
ALTER TABLE user_company FORCE ROW LEVEL SECURITY;

-- Policy: Allow superadmins to see all user-company associations,
-- B2B users see only their company's associations
CREATE POLICY user_company_tenant_isolation ON user_company
USING (
    current_setting('app.is_superadmin', true)::boolean = true
    OR EXISTS (
        SELECT 1 FROM company c 
        WHERE c.id = user_company.company_id 
        AND c.external_id IN (
            SELECT unnest(string_to_array(current_setting('app.company_external_ids', true), ','))
        )
    )
);

-- Enable RLS on company table
ALTER TABLE company ENABLE ROW LEVEL SECURITY;
ALTER TABLE company FORCE ROW LEVEL SECURITY;

-- Policy: Allow superadmins to see all companies,
-- B2B users see only their companies
CREATE POLICY company_tenant_isolation ON company
USING (
    current_setting('app.is_superadmin', true)::boolean = true
    OR external_id IN (
        SELECT unnest(string_to_array(current_setting('app.company_external_ids', true), ','))
    )
);

-- Create indexes for RLS performance
CREATE INDEX IF NOT EXISTS idx_user_company_company_external_id ON user_company(company_id);
CREATE INDEX IF NOT EXISTS idx_company_external_id_rls ON company(external_id);

-- =====================================================================================================================
-- SECTION 7: SEQUENCE RESET
-- =====================================================================================================================
-- Reset sequences to the correct values after inserting test data
-- This ensures that new records will have IDs starting after the test data IDs

SELECT setval('language_id_seq', (SELECT COALESCE(MAX(id), 1) FROM language));
SELECT setval('global_role_id_seq', (SELECT COALESCE(MAX(id), 1) FROM global_role));
SELECT setval('company_type_id_seq', (SELECT COALESCE(MAX(id), 1) FROM company_type));
SELECT setval('company_role_id_seq', (SELECT COALESCE(MAX(id), 1) FROM company_role));
SELECT setval('country_id_seq', (SELECT COALESCE(MAX(id), 1) FROM country));
SELECT setval('users_id_seq', (SELECT COALESCE(MAX(id), 1) FROM users));
SELECT setval('user_global_role_id_seq', (SELECT COALESCE(MAX(id), 1) FROM user_global_role));
SELECT setval('company_id_seq', (SELECT COALESCE(MAX(id), 1) FROM company));
SELECT setval('user_company_id_seq', (SELECT COALESCE(MAX(id), 1) FROM user_company));
SELECT setval('address_id_seq', (SELECT COALESCE(MAX(id), 1) FROM address));
SELECT setval('user_address_id_seq', (SELECT COALESCE(MAX(id), 1) FROM user_address));
SELECT setval('invitation_id_seq', (SELECT COALESCE(MAX(id), 1) FROM invitation));

-- =====================================================================================================================
-- SECTION 9: AUDIT LOGGING
-- =====================================================================================================================

-- ---------------------------------------------------------------------------------------------------------------------
-- 9.1 AUDIT LOGS TABLE
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
-- 9.2 AUDIT LOGS INDEXES
-- ---------------------------------------------------------------------------------------------------------------------
CREATE INDEX IF NOT EXISTS idx_audit_logs_timestamp ON audit_logs(timestamp);
CREATE INDEX IF NOT EXISTS idx_audit_logs_user ON audit_logs(user_external_id);
CREATE INDEX IF NOT EXISTS idx_audit_logs_event_type ON audit_logs(event_type);
CREATE INDEX IF NOT EXISTS idx_audit_logs_success ON audit_logs(success);

-- =====================================================================================================================
-- END OF SCHEMA
-- =====================================================================================================================
-- Schema created successfully!
-- Total tables: 13
-- Test users: 13 (password: Test123!)
-- Test companies: 6
-- =====================================================================================================================








