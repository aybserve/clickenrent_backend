-- =====================================================================================================================
-- SUPPORT SERVICE - REFERENCE DATA v1.0 (Flyway Migration)
-- =====================================================================================================================
-- Module: support-service
-- Database: PostgreSQL
-- Version: 1.0
-- Description: Required lookup data and reference data for support service
--
-- Note: Sample/test data is loaded via db/testdata/ when staging or dev profile is active.
-- 
-- Author: Vitaliy Shvetsov
-- =====================================================================================================================

-- =====================================================================================================================
-- SECTION 1: REQUIRED LOOKUP DATA
-- =====================================================================================================================
-- This data is REQUIRED for the application to function properly.

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
(1, '550e8400-e29b-41d4-a716-446655440001', 'System Company', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(2, '550e8400-e29b-41d4-a716-446655440002', 'B2B Partner', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- ---------------------------------------------------------------------------------------------------------------------
-- 1.3 BIKE INSPECTION STATUS
-- ---------------------------------------------------------------------------------------------------------------------
INSERT INTO bike_inspection_status (id, external_id, name, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, '550e8400-e29b-41d4-a716-446655440020', 'PENDING', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(2, '550e8400-e29b-41d4-a716-446655440021', 'IN_PROGRESS', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(3, '550e8400-e29b-41d4-a716-446655440022', 'COMPLETED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(4, '550e8400-e29b-41d4-a716-446655440023', 'APPROVED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- ---------------------------------------------------------------------------------------------------------------------
-- 1.4 BIKE INSPECTION ITEM STATUS
-- ---------------------------------------------------------------------------------------------------------------------
INSERT INTO bike_inspection_item_status (id, external_id, name, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, '550e8400-e29b-41d4-a716-446655440030', 'OK', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(2, '550e8400-e29b-41d4-a716-446655440031', 'DAMAGED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(3, '550e8400-e29b-41d4-a716-446655440032', 'NEEDS_REPAIR', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(4, '550e8400-e29b-41d4-a716-446655440033', 'MISSING', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- =====================================================================================================================
-- SECTION 2: REFERENCE DATA (RECOMMENDED)
-- =====================================================================================================================
-- This data provides common bike issues and error codes.
-- Recommended for production but can be customized based on your bike fleet.

-- ---------------------------------------------------------------------------------------------------------------------
-- 2.1 BIKE UNIT
-- ---------------------------------------------------------------------------------------------------------------------
INSERT INTO bike_unit (id, external_id, name, company_external_id, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, '550e8400-e29b-41d4-a716-446655440001', 'Front Wheel', 'company-ext-001', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(2, '550e8400-e29b-41d4-a716-446655440002', 'Rear Wheel', 'company-ext-001', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(3, '550e8400-e29b-41d4-a716-446655440003', 'Battery Pack', 'company-ext-001', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(4, '550e8400-e29b-41d4-a716-446655440004', 'Motor Assembly', 'company-ext-002', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(5, '550e8400-e29b-41d4-a716-446655440005', 'Brake System', 'company-ext-002', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- ---------------------------------------------------------------------------------------------------------------------
-- 2.2 BIKE ISSUE (Hierarchical Structure)
-- ---------------------------------------------------------------------------------------------------------------------
-- Root Issues
INSERT INTO bike_issue (id, external_id, erp_external_id, name, description, parent_bike_issue_id, is_fixable_by_client, responsible_person_id, bike_unit_id, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, '550e8400-e29b-41d4-a716-446655440101', 'ERP-ISSUE-001', 'Battery Issues', 'Problems related to bike battery', NULL, false, 2, 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(2, '550e8400-e29b-41d4-a716-446655440102', 'ERP-ISSUE-002', 'Brake Issues', 'Problems with bike braking system', NULL, false, 1, 5, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(3, '550e8400-e29b-41d4-a716-446655440103', 'ERP-ISSUE-003', 'Motor Issues', 'Problems with electric motor', NULL, false, 1, 4, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(4, '550e8400-e29b-41d4-a716-446655440104', NULL, 'Tire Issues', 'Problems with tires and wheels', NULL, false, 1, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(5, '550e8400-e29b-41d4-a716-446655440105', NULL, 'Display Issues', 'Problems with bike display or controls', NULL, false, 2, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- Sub Issues (Battery)
INSERT INTO bike_issue (id, external_id, erp_external_id, name, description, parent_bike_issue_id, is_fixable_by_client, responsible_person_id, bike_unit_id, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(6, '550e8400-e29b-41d4-a716-446655440106', NULL, 'Battery Dead', 'Battery completely discharged', 1, true, 2, 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(7, '550e8400-e29b-41d4-a716-446655440107', NULL, 'Battery Connection Loose', 'Battery connector not properly attached', 1, true, 2, 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(8, '550e8400-e29b-41d4-a716-446655440108', NULL, 'Battery Not Charging', 'Battery fails to charge', 1, false, 2, 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- Sub Issues (Brakes)
INSERT INTO bike_issue (id, external_id, erp_external_id, name, description, parent_bike_issue_id, is_fixable_by_client, responsible_person_id, bike_unit_id, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(9, '550e8400-e29b-41d4-a716-446655440109', NULL, 'Brake Pads Worn', 'Brake pads need replacement', 2, false, 1, 5, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(10, '550e8400-e29b-41d4-a716-446655440110', NULL, 'Brake Cable Loose', 'Brake cable needs adjustment', 2, false, 1, 5, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(11, '550e8400-e29b-41d4-a716-446655440111', NULL, 'Brake Squeaking', 'Brakes making noise', 2, false, 1, 5, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- Sub Issues (Motor)
INSERT INTO bike_issue (id, external_id, erp_external_id, name, description, parent_bike_issue_id, is_fixable_by_client, responsible_person_id, bike_unit_id, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(12, '550e8400-e29b-41d4-a716-446655440112', NULL, 'Motor Not Starting', 'Electric motor fails to start', 3, false, 1, 4, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(13, '550e8400-e29b-41d4-a716-446655440113', NULL, 'Motor Overheating', 'Motor running too hot', 3, false, 1, 4, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(14, '550e8400-e29b-41d4-a716-446655440114', NULL, 'Motor Making Noise', 'Unusual sounds from motor', 3, false, 1, 4, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- Sub Issues (Tire)
INSERT INTO bike_issue (id, external_id, erp_external_id, name, description, parent_bike_issue_id, is_fixable_by_client, responsible_person_id, bike_unit_id, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(15, '550e8400-e29b-41d4-a716-446655440115', NULL, 'Flat Tire', 'Tire has lost air pressure', 4, true, 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(16, '550e8400-e29b-41d4-a716-446655440116', NULL, 'Tire Worn', 'Tire tread is worn out', 4, false, 1, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- Sub Issues (Display)
INSERT INTO bike_issue (id, external_id, erp_external_id, name, description, parent_bike_issue_id, is_fixable_by_client, responsible_person_id, bike_unit_id, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(17, '550e8400-e29b-41d4-a716-446655440117', NULL, 'Display Not Working', 'Display screen is blank or not responding', 5, false, 2, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(18, '550e8400-e29b-41d4-a716-446655440118', NULL, 'Display Error Message', 'Display showing error code', 5, false, 2, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- ---------------------------------------------------------------------------------------------------------------------
-- 2.3 ERROR CODE
-- ---------------------------------------------------------------------------------------------------------------------
-- Note: bike_engine_external_id has been removed. Use bike_engine_error_code junction table for associations.
INSERT INTO error_code (id, external_id, name, description, common_cause, diagnostic_steps, recommended_fix, notes, is_fixable_by_client, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, '550e8400-e29b-41d4-a716-446655440201', 'E001', 'Battery Low Voltage', 'Battery discharged or faulty cell', 'Check battery voltage with multimeter', 'Charge or replace battery', 'Common error in cold weather', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(2, '550e8400-e29b-41d4-a716-446655440202', 'E002', 'Motor Controller Error', 'Faulty controller or wiring', 'Inspect controller connections', 'Replace motor controller', 'May require professional service', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(3, '550e8400-e29b-41d4-a716-446655440203', 'E003', 'Throttle Sensor Fault', 'Throttle sensor disconnected or damaged', 'Check throttle sensor connection', 'Reconnect or replace sensor', 'Client can check connection', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(4, '550e8400-e29b-41d4-a716-446655440204', 'E004', 'Brake Sensor Active', 'Brake lever engaged or sensor stuck', 'Check brake lever and sensor', 'Adjust or clean brake sensor', 'Usually easy fix', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(5, '550e8400-e29b-41d4-a716-446655440205', 'E005', 'Overheat Protection', 'Motor or controller overheating', 'Let bike cool down for 30 minutes', 'Avoid steep hills or heavy loads', 'Temporary error', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(6, '550e8400-e29b-41d4-a716-446655440206', 'E006', 'Communication Error', 'Loss of communication between components', 'Check all cable connections', 'Reconnect or replace cables', 'Check for loose connections', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(7, '550e8400-e29b-41d4-a716-446655440207', 'E007', 'Speed Sensor Error', 'Speed sensor not detecting wheel rotation', 'Check sensor alignment and magnet position', 'Adjust sensor or replace if damaged', 'Affects speed display and motor assist', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(8, '550e8400-e29b-41d4-a716-446655440208', 'E008', 'Torque Sensor Error', 'Pedal assist sensor malfunction', 'Check torque sensor connections', 'Recalibrate or replace sensor', 'Affects pedal assist functionality', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- Reset sequences for reference data
SELECT setval('support_request_status_id_seq', (SELECT COALESCE(MAX(id), 1) FROM support_request_status));
SELECT setval('responsible_person_id_seq', (SELECT COALESCE(MAX(id), 1) FROM responsible_person));
SELECT setval('bike_inspection_status_id_seq', (SELECT COALESCE(MAX(id), 1) FROM bike_inspection_status));
SELECT setval('bike_inspection_item_status_id_seq', (SELECT COALESCE(MAX(id), 1) FROM bike_inspection_item_status));
SELECT setval('bike_unit_id_seq', (SELECT COALESCE(MAX(id), 1) FROM bike_unit));
SELECT setval('bike_issue_id_seq', (SELECT COALESCE(MAX(id), 1) FROM bike_issue));
SELECT setval('error_code_id_seq', (SELECT COALESCE(MAX(id), 1) FROM error_code));

-- =====================================================================================================================
-- END OF REFERENCE DATA
-- =====================================================================================================================
