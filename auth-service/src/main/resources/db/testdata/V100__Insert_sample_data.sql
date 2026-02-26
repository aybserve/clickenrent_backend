-- =====================================================================================================================
-- AUTH SERVICE - SAMPLE DATA (Flyway Testdata V100)
-- =====================================================================================================================
-- Module: auth-service
-- Database: PostgreSQL
-- Description: Insert sample/test data for development and testing.
--              All inserts use ON CONFLICT to ensure idempotency.
--              All user passwords are: Test123!
--              Bcrypt hash: $2a$10$59i5SxRWkbcxt2rfTyjJ2.dZrlXFchPqyw1p56D/Ltp6jvYGVh2YK
-- 
-- Author: Vitaliy Shvetsov
-- =====================================================================================================================

-- =====================================================================================================================
-- DISABLE FORCE RLS TEMPORARILY FOR DATA INSERTION
-- =====================================================================================================================
-- The tables have FORCE ROW LEVEL SECURITY which blocks even BYPASSRLS users.
-- We temporarily disable FORCE to allow test data insertion, then re-enable it.
-- =====================================================================================================================

ALTER TABLE company ENABLE ROW LEVEL SECURITY;  -- Change from FORCE to ENABLE
ALTER TABLE user_company ENABLE ROW LEVEL SECURITY;  -- Change from FORCE to ENABLE

-- =====================================================================================================================
-- SECTION 1: USERS
-- =====================================================================================================================

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

-- =====================================================================================================================
-- SECTION 2: USER GLOBAL ROLES
-- =====================================================================================================================

INSERT INTO user_global_role (id, external_id, user_id, global_role_id, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
-- SUPERADMIN
(1, 'ugr-ext-00001', 1, 1, NOW(), NOW(), 'system', 'system', false),
-- ADMIN
(2, 'ugr-ext-00002', 2, 2, NOW(), NOW(), 'system', 'system', false),
(3, 'ugr-ext-00003', 3, 2, NOW(), NOW(), 'system', 'system', false),
-- B2B
(4, 'ugr-ext-00004', 4, 3, NOW(), NOW(), 'system', 'system', false),
(5, 'ugr-ext-00005', 5, 3, NOW(), NOW(), 'system', 'system', false),
(6, 'ugr-ext-00006', 6, 3, NOW(), NOW(), 'system', 'system', false),
-- CUSTOMER
(7, 'ugr-ext-00007', 7, 4, NOW(), NOW(), 'system', 'system', false),
(8, 'ugr-ext-00008', 8, 4, NOW(), NOW(), 'system', 'system', false),
(9, 'ugr-ext-00009', 9, 4, NOW(), NOW(), 'system', 'system', false),
(10, 'ugr-ext-00010', 10, 4, NOW(), NOW(), 'system', 'system', false),
(11, 'ugr-ext-00011', 11, 4, NOW(), NOW(), 'system', 'system', false),
(12, 'ugr-ext-00012', 12, 4, NOW(), NOW(), 'system', 'system', false),
(13, 'ugr-ext-00013', 13, 4, NOW(), NOW(), 'system', 'system', false),
-- SYSTEM (Service Accounts)
(14, 'ugr-ext-00014', 14, 6, NOW(), NOW(), 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- =====================================================================================================================
-- SECTION 3: COMPANIES
-- =====================================================================================================================

INSERT INTO company (id, external_id, name, description, website, logo, erp_partner_id, company_type_id, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, 'comp-ext-00001', 'Grand Hotel Berlin', 'Luxury 5-star hotel in the heart of Berlin with exceptional service and amenities', 'https://grandhotelberlin.de', 'https://cdn.example.com/logos/grand-hotel-berlin.png', 'ERP-HTL-001', 1, NOW(), NOW(), 'system', 'system', false),
(2, 'comp-ext-00002', 'Alpine Resort Salzburg', 'Traditional Austrian resort with stunning mountain views and spa facilities', 'https://alpineresort.at', 'https://cdn.example.com/logos/alpine-resort.png', 'ERP-HTL-002', 1, NOW(), NOW(), 'system', 'system', false),
(3, 'comp-ext-00003', 'Camping Provence', 'Family-friendly camping site in beautiful Provence countryside', 'https://camping-provence.fr', 'https://cdn.example.com/logos/camping-provence.png', 'ERP-CMP-001', 3, NOW(), NOW(), 'system', 'system', false),
(4, 'comp-ext-00004', 'Cozy B&B Vienna', 'Charming bed and breakfast in historic Vienna district', 'https://cozybnbvienna.at', 'https://cdn.example.com/logos/cozy-bnb.png', 'ERP-BNB-001', 2, NOW(), NOW(), 'system', 'system', false),
(5, 'comp-ext-00005', 'City Hostel Munich', 'Modern hostel with great location for backpackers and budget travelers', 'https://cityhostelmunich.de', 'https://cdn.example.com/logos/city-hostel.png', 'ERP-HST-001', 5, NOW(), NOW(), 'system', 'system', false),
(6, 'comp-ext-00006', 'Lake View Vacation Rentals', 'Premium vacation rentals with stunning lake views in Switzerland', 'https://lakeviewrentals.ch', 'https://cdn.example.com/logos/lakeview.png', 'ERP-VRC-001', 4, NOW(), NOW(), 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- =====================================================================================================================
-- SECTION 4: USER COMPANY RELATIONSHIPS
-- =====================================================================================================================

INSERT INTO user_company (id, external_id, user_id, company_id, company_role_id, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, 'uc-ext-00001', 4, 1, 1, NOW(), NOW(), 'system', 'system', false),
(2, 'uc-ext-00002', 5, 2, 1, NOW(), NOW(), 'system', 'system', false),
(3, 'uc-ext-00003', 6, 3, 1, NOW(), NOW(), 'system', 'system', false),
(4, 'uc-ext-00004', 4, 5, 2, NOW(), NOW(), 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- =====================================================================================================================
-- SECTION 5: ADDRESSES
-- =====================================================================================================================

INSERT INTO address (id, external_id, city, country_id, street, postcode, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, 'addr-ext-00001', 'Berlin', 1, 'Friedrichstrasse 100', '10117', NOW(), NOW(), 'system', 'system', false),
(2, 'addr-ext-00002', 'Munich', 1, 'Marienplatz 8', '80331', NOW(), NOW(), 'system', 'system', false),
(3, 'addr-ext-00003', 'Hamburg', 1, 'Reeperbahn 50', '20359', NOW(), NOW(), 'system', 'system', false),
(4, 'addr-ext-00004', 'Vienna', 2, 'Kaerntner Strasse 15', '1010', NOW(), NOW(), 'system', 'system', false),
(5, 'addr-ext-00005', 'Salzburg', 2, 'Getreidegasse 25', '5020', NOW(), NOW(), 'system', 'system', false),
(6, 'addr-ext-00006', 'Zurich', 3, 'Bahnhofstrasse 45', '8001', NOW(), NOW(), 'system', 'system', false),
(7, 'addr-ext-00007', 'Geneva', 3, 'Rue du Rhone 42', '1204', NOW(), NOW(), 'system', 'system', false),
(8, 'addr-ext-00008', 'Paris', 4, 'Champs-Elysees 88', '75008', NOW(), NOW(), 'system', 'system', false),
(9, 'addr-ext-00009', 'Lyon', 4, 'Route de Camping 15', '69001', NOW(), NOW(), 'system', 'system', false),
(10, 'addr-ext-00010', 'Rome', 5, 'Via del Corso 120', '00186', NOW(), NOW(), 'system', 'system', false),
(11, 'addr-ext-00011', 'Milan', 5, 'Via Montenapoleone 10', '20121', NOW(), NOW(), 'system', 'system', false),
(12, 'addr-ext-00012', 'New York', 6, '123 Broadway', '10001', NOW(), NOW(), 'system', 'system', false),
(13, 'addr-ext-00013', 'Los Angeles', 6, 'Sunset Blvd 500', '90028', NOW(), NOW(), 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- =====================================================================================================================
-- SECTION 6: USER ADDRESS RELATIONSHIPS
-- =====================================================================================================================

INSERT INTO user_address (id, external_id, user_id, address_id, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, 'ua-ext-00001', 4, 1, NOW(), NOW(), 'system', 'system', false),
(2, 'ua-ext-00002', 5, 5, NOW(), NOW(), 'system', 'system', false),
(3, 'ua-ext-00003', 6, 9, NOW(), NOW(), 'system', 'system', false),
(4, 'ua-ext-00004', 7, 12, NOW(), NOW(), 'system', 'system', false),
(5, 'ua-ext-00005', 8, 3, NOW(), NOW(), 'system', 'system', false),
(6, 'ua-ext-00006', 9, 10, NOW(), NOW(), 'system', 'system', false),
(7, 'ua-ext-00007', 10, 8, NOW(), NOW(), 'system', 'system', false),
(8, 'ua-ext-00008', 11, 2, NOW(), NOW(), 'system', 'system', false),
(9, 'ua-ext-00009', 12, 7, NOW(), NOW(), 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- =====================================================================================================================
-- SECTION 7: INVITATIONS
-- =====================================================================================================================

INSERT INTO invitation (id, external_id, email, token, invited_by_user_id, company_id, status, expires_at, accepted_at, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, 'inv-ext-00001', 'newstaff@example.com', 'inv-token-pending-001', 4, 1, 'PENDING', NOW() + INTERVAL '7 days', NULL, NOW(), NOW(), 'hotelowner_max', 'hotelowner_max', false),
(2, 'inv-ext-00002', 'manager@alpine.com', 'inv-token-pending-002', 5, 2, 'PENDING', NOW() + INTERVAL '14 days', NULL, NOW(), NOW(), 'hotelowner_anna', 'hotelowner_anna', false),
(3, 'inv-ext-00003', 'staff@camping.com', 'inv-token-accepted-001', 6, 3, 'ACCEPTED', NOW() + INTERVAL '30 days', NOW() - INTERVAL '2 days', NOW() - INTERVAL '5 days', NOW(), 'campingowner_pierre', 'campingowner_pierre', false),
(4, 'inv-ext-00004', 'oldstaff@example.com', 'inv-token-expired-001', 4, 1, 'PENDING', NOW() - INTERVAL '5 days', NULL, NOW() - INTERVAL '12 days', NOW(), 'hotelowner_max', 'hotelowner_max', false),
(5, 'inv-ext-00005', 'cancelled@example.com', 'inv-token-cancelled-001', 5, 2, 'CANCELLED', NOW() + INTERVAL '7 days', NULL, NOW() - INTERVAL '3 days', NOW(), 'hotelowner_anna', 'hotelowner_anna', false)
ON CONFLICT (id) DO NOTHING;

-- =====================================================================================================================
-- SECTION 8: USER PREFERENCES
-- =====================================================================================================================

INSERT INTO user_preferences (id, external_id, user_id, navigation_order, theme, language_id, timezone, date_format, time_format, currency_external_id, email_notifications, push_notifications, sms_notifications, notification_frequency, items_per_page, dashboard_layout, table_preferences, default_filters, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, 'upref-ext-00001', 1, '{"superadmin": ["dashboard", "companies", "locations", "bikes", "users", "bikeRentals", "analytics", "push-notifications", "legal-documents"]}'::jsonb, 'SYSTEM', 1, 'UTC', 'YYYY-MM-DD', 'TWENTY_FOUR_HOUR', '550e8400-e29b-41d4-a716-446655440021', true, true, false, 'IMMEDIATE', 20, '{}'::jsonb, '{}'::jsonb, '{}'::jsonb, NOW(), NOW(), 'system', 'system', false),
(2, 'upref-ext-00002', 2, '{"admin": ["dashboard", "users", "locations", "bikes", "bikeRentals", "analytics"]}'::jsonb, 'LIGHT', 1, 'Europe/Berlin', 'DD/MM/YYYY', 'TWENTY_FOUR_HOUR', '550e8400-e29b-41d4-a716-446655440022', true, true, false, 'IMMEDIATE', 20, '{}'::jsonb, '{}'::jsonb, '{}'::jsonb, NOW(), NOW(), 'system', 'system', false),
(3, 'upref-ext-00003', 3, '{"admin": ["dashboard", "users", "bikeRentals", "analytics", "locations", "bikes"]}'::jsonb, 'DARK', 2, 'Europe/Zurich', 'DD.MM.YYYY', 'TWENTY_FOUR_HOUR', '550e8400-e29b-41d4-a716-446655440022', true, true, false, 'DAILY', 25, '{}'::jsonb, '{}'::jsonb, '{}'::jsonb, NOW(), NOW(), 'system', 'system', false),
(4, 'upref-ext-00004', 4, '{"b2b": ["dashboard", "bikes", "bikeRentals", "analytics"]}'::jsonb, 'LIGHT', 2, 'Europe/Berlin', 'DD.MM.YYYY', 'TWENTY_FOUR_HOUR', '550e8400-e29b-41d4-a716-446655440022', true, true, true, 'IMMEDIATE', 20, '{}'::jsonb, '{}'::jsonb, '{}'::jsonb, NOW(), NOW(), 'system', 'system', false),
(5, 'upref-ext-00005', 5, '{"b2b": ["bikes", "bikeRentals", "analytics", "dashboard"]}'::jsonb, 'SYSTEM', 2, 'Europe/Vienna', 'DD.MM.YYYY', 'TWENTY_FOUR_HOUR', '550e8400-e29b-41d4-a716-446655440022', true, true, false, 'IMMEDIATE', 20, '{}'::jsonb, '{}'::jsonb, '{}'::jsonb, NOW(), NOW(), 'system', 'system', false),
(6, 'upref-ext-00006', 6, '{"b2b": ["dashboard", "bikes", "bikeRentals", "analytics"]}'::jsonb, 'LIGHT', 3, 'Europe/Paris', 'DD/MM/YYYY', 'TWENTY_FOUR_HOUR', '550e8400-e29b-41d4-a716-446655440022', true, false, false, 'DAILY', 30, '{}'::jsonb, '{}'::jsonb, '{}'::jsonb, NOW(), NOW(), 'system', 'system', false),
(7, 'upref-ext-00007', 7, '{"customer": ["rentals", "bikes", "profile", "payment-methods"]}'::jsonb, 'DARK', 1, 'America/New_York', 'MM/DD/YYYY', 'TWELVE_HOUR', '550e8400-e29b-41d4-a716-446655440021', true, true, false, 'IMMEDIATE', 20, '{}'::jsonb, '{}'::jsonb, '{}'::jsonb, NOW(), NOW(), 'system', 'system', false),
(8, 'upref-ext-00008', 8, '{"customer": ["rentals", "bikes", "profile", "payment-methods"]}'::jsonb, 'LIGHT', 2, 'Europe/Berlin', 'DD.MM.YYYY', 'TWENTY_FOUR_HOUR', '550e8400-e29b-41d4-a716-446655440022', true, true, false, 'IMMEDIATE', 20, '{}'::jsonb, '{}'::jsonb, '{}'::jsonb, NOW(), NOW(), 'system', 'system', false),
(9, 'upref-ext-00009', 9, '{"customer": ["bikes", "rentals", "profile", "payment-methods"]}'::jsonb, 'SYSTEM', 5, 'Europe/Rome', 'DD/MM/YYYY', 'TWENTY_FOUR_HOUR', '550e8400-e29b-41d4-a716-446655440022', true, true, false, 'DAILY', 20, '{}'::jsonb, '{}'::jsonb, '{}'::jsonb, NOW(), NOW(), 'system', 'system', false),
(10, 'upref-ext-00010', 10, '{"customer": ["rentals", "bikes", "profile", "payment-methods"]}'::jsonb, 'LIGHT', 3, 'Europe/Paris', 'DD/MM/YYYY', 'TWENTY_FOUR_HOUR', '550e8400-e29b-41d4-a716-446655440022', true, false, false, 'IMMEDIATE', 20, '{}'::jsonb, '{}'::jsonb, '{}'::jsonb, NOW(), NOW(), 'system', 'system', false),
(11, 'upref-ext-00011', 11, '{"customer": ["rentals", "bikes", "profile", "payment-methods"]}'::jsonb, 'SYSTEM', 2, 'Europe/Berlin', 'DD.MM.YYYY', 'TWENTY_FOUR_HOUR', '550e8400-e29b-41d4-a716-446655440022', true, true, false, 'IMMEDIATE', 20, '{}'::jsonb, '{}'::jsonb, '{}'::jsonb, NOW(), NOW(), 'system', 'system', false),
(12, 'upref-ext-00012', 12, '{"customer": ["rentals", "bikes", "profile", "payment-methods"]}'::jsonb, 'DARK', 1, 'Europe/Zurich', 'DD.MM.YYYY', 'TWENTY_FOUR_HOUR', '550e8400-e29b-41d4-a716-446655440022', true, true, false, 'IMMEDIATE', 25, '{}'::jsonb, '{}'::jsonb, '{}'::jsonb, NOW(), NOW(), 'system', 'system', false),
(13, 'upref-ext-00013', 13, '{"customer": ["rentals", "bikes", "profile", "payment-methods"]}'::jsonb, 'LIGHT', 1, 'America/Los_Angeles', 'MM/DD/YYYY', 'TWELVE_HOUR', '550e8400-e29b-41d4-a716-446655440021', false, false, false, 'WEEKLY', 20, '{}'::jsonb, '{}'::jsonb, '{}'::jsonb, NOW(), NOW(), 'system', 'system', false),
(14, 'upref-ext-00014', 14, '{"system": ["status", "health", "metrics"]}'::jsonb, 'SYSTEM', 1, 'UTC', 'YYYY-MM-DD', 'TWENTY_FOUR_HOUR', '550e8400-e29b-41d4-a716-446655440021', false, false, false, 'IMMEDIATE', 20, '{}'::jsonb, '{}'::jsonb, '{}'::jsonb, NOW(), NOW(), 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- =====================================================================================================================
-- RE-ENABLE FORCE RLS AFTER DATA INSERTION
-- =====================================================================================================================
-- Restore FORCE ROW LEVEL SECURITY to ensure policies are enforced for all users including superusers.
-- =====================================================================================================================

ALTER TABLE company FORCE ROW LEVEL SECURITY;
ALTER TABLE user_company FORCE ROW LEVEL SECURITY;

-- =====================================================================================================================
-- END OF SAMPLE DATA
-- =====================================================================================================================
