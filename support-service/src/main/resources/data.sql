-- =====================================================================================================================
-- SUPPORT SERVICE - DATABASE INITIALIZATION
-- =====================================================================================================================
-- Module: support-service
-- Database: PostgreSQL
-- Description: Production database initialization for the customer support and feedback service.
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
-- 1.1 SUPPORT REQUEST STATUS
-- ---------------------------------------------------------------------------------------------------------------------
INSERT INTO support_request_status (id, external_id, name, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, '550e8400-e29b-41d4-a716-446655440010', 'OPEN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(2, '550e8400-e29b-41d4-a716-446655440011', 'IN_PROGRESS', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(3, '550e8400-e29b-41d4-a716-446655440012', 'RESOLVED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(4, '550e8400-e29b-41d4-a716-446655440013', 'CLOSED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- ---------------------------------------------------------------------------------------------------------------------
-- 1.2 RESPONSIBLE PERSON
-- ---------------------------------------------------------------------------------------------------------------------
INSERT INTO responsible_person (id, external_id, name, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, '550e8400-e29b-41d4-a716-446655440001', 'Mechanical Team', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(2, '550e8400-e29b-41d4-a716-446655440002', 'Electrical Team', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(3, '550e8400-e29b-41d4-a716-446655440003', 'Support Staff', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- =====================================================================================================================
-- SECTION 2: REFERENCE DATA (RECOMMENDED)
-- =====================================================================================================================
-- This data provides common bike issues and error codes.
-- Recommended for production but can be customized based on your bike fleet.

-- ---------------------------------------------------------------------------------------------------------------------
-- 2.1 BIKE ISSUE (Hierarchical Structure)
-- ---------------------------------------------------------------------------------------------------------------------
-- Root Issues
INSERT INTO bike_issue (id, external_id, name, description, parent_bike_issue_id, is_fixable_by_client, responsible_person_id, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, '550e8400-e29b-41d4-a716-446655440101', 'Battery Issues', 'Problems related to bike battery', NULL, false, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(2, '550e8400-e29b-41d4-a716-446655440102', 'Brake Issues', 'Problems with bike braking system', NULL, false, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(3, '550e8400-e29b-41d4-a716-446655440103', 'Motor Issues', 'Problems with electric motor', NULL, false, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(4, '550e8400-e29b-41d4-a716-446655440104', 'Tire Issues', 'Problems with tires and wheels', NULL, false, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(5, '550e8400-e29b-41d4-a716-446655440105', 'Display Issues', 'Problems with bike display or controls', NULL, false, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- Sub Issues (Battery)
INSERT INTO bike_issue (id, external_id, name, description, parent_bike_issue_id, is_fixable_by_client, responsible_person_id, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(6, '550e8400-e29b-41d4-a716-446655440106', 'Battery Dead', 'Battery completely discharged', 1, true, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(7, '550e8400-e29b-41d4-a716-446655440107', 'Battery Connection Loose', 'Battery connector not properly attached', 1, true, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(8, '550e8400-e29b-41d4-a716-446655440108', 'Battery Not Charging', 'Battery fails to charge', 1, false, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- Sub Issues (Brakes)
INSERT INTO bike_issue (id, external_id, name, description, parent_bike_issue_id, is_fixable_by_client, responsible_person_id, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(9, '550e8400-e29b-41d4-a716-446655440109', 'Brake Pads Worn', 'Brake pads need replacement', 2, false, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(10, '550e8400-e29b-41d4-a716-446655440110', 'Brake Cable Loose', 'Brake cable needs adjustment', 2, false, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(11, '550e8400-e29b-41d4-a716-446655440111', 'Brake Squeaking', 'Brakes making noise', 2, false, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- Sub Issues (Motor)
INSERT INTO bike_issue (id, external_id, name, description, parent_bike_issue_id, is_fixable_by_client, responsible_person_id, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(12, '550e8400-e29b-41d4-a716-446655440112', 'Motor Not Starting', 'Electric motor fails to start', 3, false, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(13, '550e8400-e29b-41d4-a716-446655440113', 'Motor Overheating', 'Motor running too hot', 3, false, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(14, '550e8400-e29b-41d4-a716-446655440114', 'Motor Making Noise', 'Unusual sounds from motor', 3, false, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- Sub Issues (Tire)
INSERT INTO bike_issue (id, external_id, name, description, parent_bike_issue_id, is_fixable_by_client, responsible_person_id, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(15, '550e8400-e29b-41d4-a716-446655440115', 'Flat Tire', 'Tire has lost air pressure', 4, true, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(16, '550e8400-e29b-41d4-a716-446655440116', 'Tire Worn', 'Tire tread is worn out', 4, false, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- Sub Issues (Display)
INSERT INTO bike_issue (id, external_id, name, description, parent_bike_issue_id, is_fixable_by_client, responsible_person_id, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(17, '550e8400-e29b-41d4-a716-446655440117', 'Display Not Working', 'Display screen is blank or not responding', 5, false, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(18, '550e8400-e29b-41d4-a716-446655440118', 'Display Error Message', 'Display showing error code', 5, false, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- ---------------------------------------------------------------------------------------------------------------------
-- 2.2 ERROR CODE
-- ---------------------------------------------------------------------------------------------------------------------
INSERT INTO error_code (id, external_id, name, bike_engine_external_id, description, common_cause, diagnostic_steps, recommended_fix, notes, is_fixable_by_client, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, '550e8400-e29b-41d4-a716-446655440201', 'E001', 'bike-engine-ext-001', 'Battery Low Voltage', 'Battery discharged or faulty cell', 'Check battery voltage with multimeter', 'Charge or replace battery', 'Common error in cold weather', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(2, '550e8400-e29b-41d4-a716-446655440202', 'E002', 'bike-engine-ext-001', 'Motor Controller Error', 'Faulty controller or wiring', 'Inspect controller connections', 'Replace motor controller', 'May require professional service', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(3, '550e8400-e29b-41d4-a716-446655440203', 'E003', 'bike-engine-ext-002', 'Throttle Sensor Fault', 'Throttle sensor disconnected or damaged', 'Check throttle sensor connection', 'Reconnect or replace sensor', 'Client can check connection', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(4, '550e8400-e29b-41d4-a716-446655440204', 'E004', 'bike-engine-ext-002', 'Brake Sensor Active', 'Brake lever engaged or sensor stuck', 'Check brake lever and sensor', 'Adjust or clean brake sensor', 'Usually easy fix', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(5, '550e8400-e29b-41d4-a716-446655440205', 'E005', 'bike-engine-ext-003', 'Overheat Protection', 'Motor or controller overheating', 'Let bike cool down for 30 minutes', 'Avoid steep hills or heavy loads', 'Temporary error', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(6, '550e8400-e29b-41d4-a716-446655440206', 'E006', 'bike-engine-ext-001', 'Communication Error', 'Loss of communication between components', 'Check all cable connections', 'Reconnect or replace cables', 'Check for loose connections', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(7, '550e8400-e29b-41d4-a716-446655440207', 'E007', 'bike-engine-ext-002', 'Speed Sensor Error', 'Speed sensor not detecting wheel rotation', 'Check sensor alignment and magnet position', 'Adjust sensor or replace if damaged', 'Affects speed display and motor assist', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(8, '550e8400-e29b-41d4-a716-446655440208', 'E008', 'bike-engine-ext-003', 'Torque Sensor Error', 'Pedal assist sensor malfunction', 'Check torque sensor connections', 'Recalibrate or replace sensor', 'Affects pedal assist functionality', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- =====================================================================================================================
-- SECTION 3: SAMPLE/TEST DATA (OPTIONAL)
-- =====================================================================================================================
-- This section contains sample data for testing and development purposes.
-- COMMENT OUT or REMOVE this entire section before deploying to production.
-- Sample data references external IDs from other services (auth-service, rental-service).

-- ---------------------------------------------------------------------------------------------------------------------
-- 3.1 FEEDBACK (Sample data - references users from auth-service)
-- ---------------------------------------------------------------------------------------------------------------------
INSERT INTO feedback (id, external_id, user_external_id, rate, comment, date_time, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, '550e8400-e29b-41d4-a716-446655440301', 'usr-ext-00007', 5, 'Excellent service, very responsive support team!', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(2, '550e8400-e29b-41d4-a716-446655440302', 'usr-ext-00008', 4, 'Good overall experience, minor delay in response.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(3, '550e8400-e29b-41d4-a716-446655440303', 'usr-ext-00009', 3, 'Average service, could be improved.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(4, '550e8400-e29b-41d4-a716-446655440304', 'usr-ext-00007', 2, 'Not satisfied, took too long to resolve issue.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- ---------------------------------------------------------------------------------------------------------------------
-- 3.2 BIKE RENTAL FEEDBACK (Sample data - references users and bike rentals from other services)
-- ---------------------------------------------------------------------------------------------------------------------
INSERT INTO bike_rental_feedback (id, external_id, user_external_id, bike_rental_external_id, rate, comment, date_time, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, '550e8400-e29b-41d4-a716-446655440501', 'usr-ext-00007', 'bike-rental-ext-00101', 5, 'Great bike, smooth ride!', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(2, '550e8400-e29b-41d4-a716-446655440502', 'usr-ext-00008', 'bike-rental-ext-00102', 4, 'Good bike, but battery could last longer.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(3, '550e8400-e29b-41d4-a716-446655440503', 'usr-ext-00009', 'bike-rental-ext-00103', 3, 'Average experience, brakes were squeaky.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(4, '550e8400-e29b-41d4-a716-446655440504', 'usr-ext-00007', 'bike-rental-ext-00104', 2, 'Poor condition, motor had issues.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- ---------------------------------------------------------------------------------------------------------------------
-- 3.3 SUPPORT REQUEST (Sample data - references users and bikes from other services)
-- ---------------------------------------------------------------------------------------------------------------------
INSERT INTO support_request (id, external_id, user_external_id, bike_external_id, is_near_location, photo_url, error_code_id, support_request_status_id, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, '550e8400-e29b-41d4-a716-446655440401', 'usr-ext-00007', 'bike-ext-00201', true, 'https://example.com/photos/issue1.jpg', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(2, '550e8400-e29b-41d4-a716-446655440402', 'usr-ext-00008', 'bike-ext-00202', false, NULL, 2, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(3, '550e8400-e29b-41d4-a716-446655440403', 'usr-ext-00009', 'bike-ext-00203', true, 'https://example.com/photos/issue3.jpg', NULL, 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(4, '550e8400-e29b-41d4-a716-446655440404', 'usr-ext-00007', NULL, false, NULL, 3, 4, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(5, '550e8400-e29b-41d4-a716-446655440405', 'usr-ext-00008', 'bike-ext-00204', true, 'https://example.com/photos/issue5.jpg', 4, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- ---------------------------------------------------------------------------------------------------------------------
-- 3.4 BIKE TYPE BIKE ISSUE (Junction Table - references bike types from rental-service)
-- ---------------------------------------------------------------------------------------------------------------------
INSERT INTO bike_type_bike_issue (id, external_id, bike_type_external_id, bike_issue_id) VALUES
(1, '550e8400-e29b-41d4-a716-446655440601', 'bike-type-ext-001', 1),
(2, '550e8400-e29b-41d4-a716-446655440602', 'bike-type-ext-001', 2),
(3, '550e8400-e29b-41d4-a716-446655440603', 'bike-type-ext-001', 3),
(4, '550e8400-e29b-41d4-a716-446655440604', 'bike-type-ext-002', 1),
(5, '550e8400-e29b-41d4-a716-446655440605', 'bike-type-ext-002', 3),
(6, '550e8400-e29b-41d4-a716-446655440606', 'bike-type-ext-002', 4),
(7, '550e8400-e29b-41d4-a716-446655440607', 'bike-type-ext-003', 2),
(8, '550e8400-e29b-41d4-a716-446655440608', 'bike-type-ext-003', 3),
(9, '550e8400-e29b-41d4-a716-446655440609', 'bike-type-ext-003', 5)
ON CONFLICT (id) DO NOTHING;

-- ---------------------------------------------------------------------------------------------------------------------
-- 3.5 SUPPORT REQUEST BIKE ISSUE (Junction Table)
-- ---------------------------------------------------------------------------------------------------------------------
INSERT INTO support_request_bike_issue (id, external_id, support_request_id, bike_issue_id) VALUES
(1, '550e8400-e29b-41d4-a716-446655440701', 1, 1),
(2, '550e8400-e29b-41d4-a716-446655440702', 1, 6),
(3, '550e8400-e29b-41d4-a716-446655440703', 2, 3),
(4, '550e8400-e29b-41d4-a716-446655440704', 2, 12),
(5, '550e8400-e29b-41d4-a716-446655440705', 3, 2),
(6, '550e8400-e29b-41d4-a716-446655440706', 4, 7),
(7, '550e8400-e29b-41d4-a716-446655440707', 5, 9)
ON CONFLICT (id) DO NOTHING;

-- ---------------------------------------------------------------------------------------------------------------------
-- 3.6 SUPPORT REQUEST GUIDE ITEM (Sample troubleshooting guides)
-- ---------------------------------------------------------------------------------------------------------------------
INSERT INTO support_request_guide_item (id, external_id, item_index, description, bike_issue_id, support_request_status_id, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, '550e8400-e29b-41d4-a716-446655440801', 1, 'Check if battery is properly connected to the bike frame', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(2, '550e8400-e29b-41d4-a716-446655440802', 2, 'Verify battery charge level on the display panel', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(3, '550e8400-e29b-41d4-a716-446655440803', 3, 'Try charging battery for at least 2 hours using the provided charger', 1, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(4, '550e8400-e29b-41d4-a716-446655440804', 4, 'If battery still not working, contact support for replacement', 1, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(5, '550e8400-e29b-41d4-a716-446655440805', 1, 'Test brake lever responsiveness - should engage smoothly', 2, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(6, '550e8400-e29b-41d4-a716-446655440806', 2, 'Check brake pads for wear and proper alignment with rim/disc', 2, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(7, '550e8400-e29b-41d4-a716-446655440807', 3, 'Adjust brake cable tension if brakes feel loose', 2, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(8, '550e8400-e29b-41d4-a716-446655440808', 4, 'If brakes still not working properly, bring bike to service center', 2, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(9, '550e8400-e29b-41d4-a716-446655440809', 1, 'Power cycle the bike - turn off completely and wait 30 seconds', 3, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(10, '550e8400-e29b-41d4-a716-446655440810', 2, 'Check for error codes displayed on the bike screen', 3, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(11, '550e8400-e29b-41d4-a716-446655440811', 3, 'Ensure battery is fully charged and properly connected', 3, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(12, '550e8400-e29b-41d4-a716-446655440812', 4, 'If motor still not starting, contact technical support immediately', 3, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- =====================================================================================================================
-- SECTION 4: SEQUENCE RESET
-- =====================================================================================================================
-- Reset sequences to the correct values after inserting data.
-- This ensures that new records will have IDs starting after the existing data IDs.

SELECT setval('responsible_person_id_seq', (SELECT COALESCE(MAX(id), 1) FROM responsible_person));
SELECT setval('support_request_status_id_seq', (SELECT COALESCE(MAX(id), 1) FROM support_request_status));
SELECT setval('error_code_id_seq', (SELECT COALESCE(MAX(id), 1) FROM error_code));
SELECT setval('bike_issue_id_seq', (SELECT COALESCE(MAX(id), 1) FROM bike_issue));
SELECT setval('feedback_id_seq', (SELECT COALESCE(MAX(id), 1) FROM feedback));
SELECT setval('bike_rental_feedback_id_seq', (SELECT COALESCE(MAX(id), 1) FROM bike_rental_feedback));
SELECT setval('support_request_id_seq', (SELECT COALESCE(MAX(id), 1) FROM support_request));
SELECT setval('support_request_guide_item_id_seq', (SELECT COALESCE(MAX(id), 1) FROM support_request_guide_item));
SELECT setval('support_request_bike_issue_id_seq', (SELECT COALESCE(MAX(id), 1) FROM support_request_bike_issue));
SELECT setval('bike_type_bike_issue_id_seq', (SELECT COALESCE(MAX(id), 1) FROM bike_type_bike_issue));

-- =====================================================================================================================
-- END OF INITIALIZATION
-- =====================================================================================================================
-- Database initialization completed successfully!
-- 
-- Summary:
-- - Required lookup tables: support_request_status, responsible_person
-- - Reference data: bike_issue (18 issues), error_code (8 codes)
-- - Sample data: feedback (4), bike_rental_feedback (4), support_request (5)
-- 
-- Cross-Service References:
-- - User External IDs: Referenced from auth-service (usr-ext-XXXXX)
-- - Bike External IDs: Referenced from rental-service (bike-ext-XXXXX)
-- - Bike Rental External IDs: Referenced from rental-service (bike-rental-ext-XXXXX)
-- - Bike Type External IDs: Referenced from rental-service (bike-type-ext-XXXXX)
-- - Bike Engine External IDs: Referenced from rental-service (bike-engine-ext-XXXXX)
-- =====================================================================================================================

-- =====================================================================================================================
-- SECTION 4: MULTI-TENANT SCHEMA UPDATES
-- =====================================================================================================================
-- Add company_external_id column to support_request for multi-tenant isolation

ALTER TABLE support_request ADD COLUMN IF NOT EXISTS company_external_id VARCHAR(100);
CREATE INDEX IF NOT EXISTS idx_support_request_company ON support_request(company_external_id);

-- =====================================================================================================================
-- SECTION 5: ROW LEVEL SECURITY (RLS) FOR MULTI-TENANT ISOLATION
-- =====================================================================================================================
-- PostgreSQL RLS policies for database-level tenant isolation

-- Enable RLS on support_request table
ALTER TABLE support_request ENABLE ROW LEVEL SECURITY;
ALTER TABLE support_request FORCE ROW LEVEL SECURITY;

-- Policy: Allow superadmins to see all support requests,
-- B2B users see only their company's support requests
CREATE POLICY support_request_tenant_isolation ON support_request
USING (
    current_setting('app.is_superadmin', true)::boolean = true
    OR company_external_id IN (
        SELECT unnest(string_to_array(current_setting('app.company_external_ids', true), ','))
    )
);

-- Create index for RLS performance
CREATE INDEX IF NOT EXISTS idx_support_request_company_rls ON support_request(company_external_id) WHERE is_deleted = false;

-- =====================================================================================================================
-- END OF INITIALIZATION
-- =====================================================================================================================





