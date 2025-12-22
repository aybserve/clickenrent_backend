-- =====================================================
-- H2-Compatible Test Data for Auth Service
-- Password for all users: Test123!
-- Bcrypt hash: $2a$10$xn3LI/AjqicFYZFruSwve.FGW6a7Rr1m3.1kpn.5F5HJTU.E/aXGC
-- =====================================================

-- =====================================================
-- 1. LOOKUP TABLES (Foundation Data)
-- =====================================================

-- Languages (using MERGE for H2 compatibility)
MERGE INTO language (id, name) KEY(id) VALUES
(1, 'English'),
(2, 'German'),
(3, 'French'),
(4, 'Spanish'),
(5, 'Italian');

-- Global Roles
MERGE INTO global_role (id, name) KEY(id) VALUES
(1, 'SUPERADMIN'),
(2, 'ADMIN'),
(3, 'B2B'),
(4, 'CUSTOMER');

-- Company Types
MERGE INTO company_type (id, name) KEY(id) VALUES
(1, 'Hotel'),
(2, 'B&B'),
(3, 'Camping'),
(4, 'Vacation Rental'),
(5, 'Hostel');

-- Company Roles
MERGE INTO company_role (id, name) KEY(id) VALUES
(1, 'Owner'),
(2, 'Admin'),
(3, 'Staff');

-- Countries
MERGE INTO country (id, name) KEY(id) VALUES
(1, 'Germany'),
(2, 'Austria'),
(3, 'Switzerland'),
(4, 'France'),
(5, 'Italy'),
(6, 'USA');

-- Cities
MERGE INTO city (id, name, country_id) KEY(id) VALUES
(1, 'Berlin', 1),
(2, 'Munich', 1),
(3, 'Hamburg', 1),
(4, 'Vienna', 2),
(5, 'Salzburg', 2),
(6, 'Zurich', 3),
(7, 'Geneva', 3),
(8, 'Paris', 4),
(9, 'Lyon', 4),
(10, 'Rome', 5),
(11, 'Milan', 5),
(12, 'New York', 6),
(13, 'Los Angeles', 6);

-- =====================================================
-- 2. USERS
-- Password: Test123!
-- =====================================================

MERGE INTO users (id, external_id, user_name, email, password, first_name, last_name, phone, city, address, zipcode, language_id, is_active, is_email_verified, is_accepted_terms, is_accepted_privacy_policy, date_created, last_date_modified, created_by, last_modified_by, is_deleted) KEY(id) VALUES
-- SUPERADMIN
(1, 'usr-ext-00001', 'superadmin', 'superadmin@clickenrent.com', '$2a$10$59i5SxRWkbcxt2rfTyjJ2.dZrlXFchPqyw1p56D/Ltp6jvYGVh2YK', 'Super', 'Admin', '+49-30-12345001', 'Berlin', 'Admin Street 1', '10115', 1, true, true, true, true, NOW(), NOW(), 'system', 'system', false),

-- ADMIN Users
(2, 'usr-ext-00002', 'admin_john', 'john.admin@clickenrent.com', '$2a$10$xn3LI/AjqicFYZFruSwve.FGW6a7Rr1m3.1kpn.5F5HJTU.E/aXGC', 'John', 'Administrator', '+49-89-12345002', 'Munich', 'Admin Plaza 5', '80331', 1, true, true, true, true, NOW(), NOW(), 'system', 'system', false),
(3, 'usr-ext-00003', 'admin_sarah', 'sarah.admin@clickenrent.com', '$2a$10$xn3LI/AjqicFYZFruSwve.FGW6a7Rr1m3.1kpn.5F5HJTU.E/aXGC', 'Sarah', 'Manager', '+41-44-12345003', 'Zurich', 'Management Ave 12', '8001', 2, true, true, true, true, NOW(), NOW(), 'system', 'system', false),

-- B2B Users (Hotel/Company Owners/Managers)
(4, 'usr-ext-00004', 'hotelowner_max', 'max.mueller@grandhotel.de', '$2a$10$xn3LI/AjqicFYZFruSwve.FGW6a7Rr1m3.1kpn.5F5HJTU.E/aXGC', 'Max', 'Mueller', '+49-30-55512001', 'Berlin', 'Friedrichstrasse 100', '10117', 2, true, true, true, true, NOW(), NOW(), 'system', 'system', false),
(5, 'usr-ext-00005', 'hotelowner_anna', 'anna.schmidt@alpineresort.at', '$2a$10$xn3LI/AjqicFYZFruSwve.FGW6a7Rr1m3.1kpn.5F5HJTU.E/aXGC', 'Anna', 'Schmidt', '+43-662-88800001', 'Salzburg', 'Getreidegasse 25', '5020', 2, true, true, true, true, NOW(), NOW(), 'system', 'system', false),
(6, 'usr-ext-00006', 'campingowner_pierre', 'pierre.dubois@camping-provence.fr', '$2a$10$xn3LI/AjqicFYZFruSwve.FGW6a7Rr1m3.1kpn.5F5HJTU.E/aXGC', 'Pierre', 'Dubois', '+33-4-90-123456', 'Lyon', 'Route de Camping 15', '69001', 3, true, true, true, true, NOW(), NOW(), 'system', 'system', false),

-- CUSTOMER Users
(7, 'usr-ext-00007', 'customer_tom', 'tom.wilson@email.com', '$2a$10$xn3LI/AjqicFYZFruSwve.FGW6a7Rr1m3.1kpn.5F5HJTU.E/aXGC', 'Tom', 'Wilson', '+1-212-555-0101', 'New York', '123 Broadway', '10001', 1, true, true, true, true, NOW(), NOW(), 'system', 'system', false),
(8, 'usr-ext-00008', 'customer_emma', 'emma.johnson@email.com', '$2a$10$xn3LI/AjqicFYZFruSwve.FGW6a7Rr1m3.1kpn.5F5HJTU.E/aXGC', 'Emma', 'Johnson', '+49-40-555-0102', 'Hamburg', 'Reeperbahn 50', '20359', 2, true, true, true, true, NOW(), NOW(), 'system', 'system', false),
(9, 'usr-ext-00009', 'customer_luca', 'luca.rossi@email.it', '$2a$10$xn3LI/AjqicFYZFruSwve.FGW6a7Rr1m3.1kpn.5F5HJTU.E/aXGC', 'Luca', 'Rossi', '+39-06-555-0103', 'Rome', 'Via del Corso 120', '00186', 5, true, true, true, true, NOW(), NOW(), 'system', 'system', false),
(10, 'usr-ext-00010', 'customer_marie', 'marie.blanc@email.fr', '$2a$10$xn3LI/AjqicFYZFruSwve.FGW6a7Rr1m3.1kpn.5F5HJTU.E/aXGC', 'Marie', 'Blanc', '+33-1-555-0104', 'Paris', 'Champs-Élysées 88', '75008', 3, true, true, true, true, NOW(), NOW(), 'system', 'system', false),
(11, 'usr-ext-00011', 'customer_hans', 'hans.weber@email.de', '$2a$10$xn3LI/AjqicFYZFruSwve.FGW6a7Rr1m3.1kpn.5F5HJTU.E/aXGC', 'Hans', 'Weber', '+49-89-555-0105', 'Munich', 'Marienplatz 8', '80331', 2, true, true, true, true, NOW(), NOW(), 'system', 'system', false),
(12, 'usr-ext-00012', 'customer_sophia', 'sophia.martin@email.com', '$2a$10$xn3LI/AjqicFYZFruSwve.FGW6a7Rr1m3.1kpn.5F5HJTU.E/aXGC', 'Sophia', 'Martin', '+41-22-555-0106', 'Geneva', 'Rue du Rhône 42', '1204', 1, true, true, true, true, NOW(), NOW(), 'system', 'system', false),
(13, 'usr-ext-00013', 'customer_oliver', 'oliver.brown@email.com', '$2a$10$xn3LI/AjqicFYZFruSwve.FGW6a7Rr1m3.1kpn.5F5HJTU.E/aXGC', 'Oliver', 'Brown', '+1-310-555-0107', 'Los Angeles', 'Sunset Blvd 500', '90028', 1, true, false, true, true, NOW(), NOW(), 'system', 'system', false);

-- =====================================================
-- 3. USER GLOBAL ROLES
-- =====================================================

MERGE INTO user_global_role (id, user_id, global_role_id, date_created, last_date_modified, created_by, last_modified_by, is_deleted) KEY(id) VALUES
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
(13, 13, 4, NOW(), NOW(), 'system', 'system', false);

-- =====================================================
-- 4. COMPANIES
-- =====================================================

MERGE INTO company (id, external_id, name, description, website, logo, erp_partner_id, company_type_id, date_created, last_date_modified, created_by, last_modified_by, is_deleted) KEY(id) VALUES
(1, 'comp-ext-00001', 'Grand Hotel Berlin', 'Luxury 5-star hotel in the heart of Berlin with exceptional service and amenities', 'https://grandhotelberlin.de', 'https://cdn.example.com/logos/grand-hotel-berlin.png', 'ERP-HTL-001', 1, NOW(), NOW(), 'system', 'system', false),
(2, 'comp-ext-00002', 'Alpine Resort Salzburg', 'Traditional Austrian resort with stunning mountain views and spa facilities', 'https://alpineresort.at', 'https://cdn.example.com/logos/alpine-resort.png', 'ERP-HTL-002', 1, NOW(), NOW(), 'system', 'system', false),
(3, 'comp-ext-00003', 'Camping Provence', 'Family-friendly camping site in beautiful Provence countryside', 'https://camping-provence.fr', 'https://cdn.example.com/logos/camping-provence.png', 'ERP-CMP-001', 3, NOW(), NOW(), 'system', 'system', false),
(4, 'comp-ext-00004', 'Cozy B&B Vienna', 'Charming bed and breakfast in historic Vienna district', 'https://cozybnbvienna.at', 'https://cdn.example.com/logos/cozy-bnb.png', 'ERP-BNB-001', 2, NOW(), NOW(), 'system', 'system', false),
(5, 'comp-ext-00005', 'City Hostel Munich', 'Modern hostel with great location for backpackers and budget travelers', 'https://cityhostelmunich.de', 'https://cdn.example.com/logos/city-hostel.png', 'ERP-HST-001', 5, NOW(), NOW(), 'system', 'system', false),
(6, 'comp-ext-00006', 'Lake View Vacation Rentals', 'Premium vacation rentals with stunning lake views in Switzerland', 'https://lakeviewrentals.ch', 'https://cdn.example.com/logos/lakeview.png', 'ERP-VRC-001', 4, NOW(), NOW(), 'system', 'system', false);

-- =====================================================
-- 5. USER COMPANY RELATIONSHIPS
-- =====================================================

MERGE INTO user_company (id, user_id, company_id, company_role_id, date_created, last_date_modified, created_by, last_modified_by, is_deleted) KEY(id) VALUES
-- Max Mueller owns Grand Hotel Berlin
(1, 4, 1, 1, NOW(), NOW(), 'system', 'system', false),

-- Anna Schmidt owns Alpine Resort Salzburg
(2, 5, 2, 1, NOW(), NOW(), 'system', 'system', false),

-- Pierre Dubois owns Camping Provence
(3, 6, 3, 1, NOW(), NOW(), 'system', 'system', false),

-- Max Mueller is also admin at City Hostel Munich (multi-company owner)
(4, 4, 5, 2, NOW(), NOW(), 'system', 'system', false);

-- =====================================================
-- 6. ADDRESSES
-- =====================================================

MERGE INTO address (id, city_id, street, postcode, date_created, last_date_modified, created_by, last_modified_by, is_deleted) KEY(id) VALUES
(1, 1, 'Friedrichstrasse 100', '10117', NOW(), NOW(), 'system', 'system', false),
(2, 2, 'Marienplatz 8', '80331', NOW(), NOW(), 'system', 'system', false),
(3, 3, 'Reeperbahn 50', '20359', NOW(), NOW(), 'system', 'system', false),
(4, 4, 'Kärntner Strasse 15', '1010', NOW(), NOW(), 'system', 'system', false),
(5, 5, 'Getreidegasse 25', '5020', NOW(), NOW(), 'system', 'system', false),
(6, 6, 'Bahnhofstrasse 45', '8001', NOW(), NOW(), 'system', 'system', false),
(7, 7, 'Rue du Rhône 42', '1204', NOW(), NOW(), 'system', 'system', false),
(8, 8, 'Champs-Élysées 88', '75008', NOW(), NOW(), 'system', 'system', false),
(9, 9, 'Route de Camping 15', '69001', NOW(), NOW(), 'system', 'system', false),
(10, 10, 'Via del Corso 120', '00186', NOW(), NOW(), 'system', 'system', false),
(11, 11, 'Via Montenapoleone 10', '20121', NOW(), NOW(), 'system', 'system', false),
(12, 12, '123 Broadway', '10001', NOW(), NOW(), 'system', 'system', false),
(13, 13, 'Sunset Blvd 500', '90028', NOW(), NOW(), 'system', 'system', false);

-- =====================================================
-- 7. USER ADDRESS RELATIONSHIPS
-- =====================================================

MERGE INTO user_address (id, user_id, address_id, date_created, last_date_modified, created_by, last_modified_by, is_deleted) KEY(id) VALUES
(1, 4, 1, NOW(), NOW(), 'system', 'system', false),  -- Max -> Berlin address
(2, 5, 5, NOW(), NOW(), 'system', 'system', false),  -- Anna -> Salzburg address
(3, 6, 9, NOW(), NOW(), 'system', 'system', false),  -- Pierre -> Lyon address
(4, 7, 12, NOW(), NOW(), 'system', 'system', false), -- Tom -> New York address
(5, 8, 3, NOW(), NOW(), 'system', 'system', false),  -- Emma -> Hamburg address
(6, 9, 10, NOW(), NOW(), 'system', 'system', false), -- Luca -> Rome address
(7, 10, 8, NOW(), NOW(), 'system', 'system', false), -- Marie -> Paris address
(8, 11, 2, NOW(), NOW(), 'system', 'system', false), -- Hans -> Munich address
(9, 12, 7, NOW(), NOW(), 'system', 'system', false);  -- Sophia -> Geneva address

-- =====================================================
-- 8. INVITATIONS
-- Note: H2 doesn't support INTERVAL, using DATEADD instead
-- =====================================================

MERGE INTO invitation (id, email, token, invited_by_user_id, company_id, status, expires_at, accepted_at, date_created, last_date_modified, created_by, last_modified_by, is_deleted) KEY(id) VALUES
-- Pending invitation
(1, 'newstaff@example.com', 'inv-token-pending-001', 4, 1, 'PENDING', DATEADD('DAY', 7, NOW()), NULL, NOW(), NOW(), 'hotelowner_max', 'hotelowner_max', false),

-- Another pending invitation
(2, 'manager@alpine.com', 'inv-token-pending-002', 5, 2, 'PENDING', DATEADD('DAY', 14, NOW()), NULL, NOW(), NOW(), 'hotelowner_anna', 'hotelowner_anna', false),

-- Accepted invitation
(3, 'staff@camping.com', 'inv-token-accepted-001', 6, 3, 'ACCEPTED', DATEADD('DAY', 30, NOW()), DATEADD('DAY', -2, NOW()), DATEADD('DAY', -5, NOW()), NOW(), 'campingowner_pierre', 'campingowner_pierre', false),

-- Expired invitation
(4, 'oldstaff@example.com', 'inv-token-expired-001', 4, 1, 'PENDING', DATEADD('DAY', -5, NOW()), NULL, DATEADD('DAY', -12, NOW()), NOW(), 'hotelowner_max', 'hotelowner_max', false),

-- Cancelled invitation
(5, 'cancelled@example.com', 'inv-token-cancelled-001', 5, 2, 'CANCELLED', DATEADD('DAY', 7, NOW()), NULL, DATEADD('DAY', -3, NOW()), NOW(), 'hotelowner_anna', 'hotelowner_anna', false);

-- Note: H2 auto-increments sequences, no manual reset needed like PostgreSQL's setval




