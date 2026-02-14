-- =====================================================================================================================
-- SUPPORT SERVICE - SAMPLE DATA (Flyway Testdata V100)
-- =====================================================================================================================
-- Module: support-service
-- Database: PostgreSQL
-- Description: Insert sample/test data for development and testing.
--              All inserts use ON CONFLICT to ensure idempotency.
--              Only loaded when 'staging' or 'dev' profile is active.
-- 
-- Author: Vitaliy Shvetsov
-- =====================================================================================================================

-- Feedback
INSERT INTO feedback (id, external_id, user_external_id, rate, comment, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, '550e8400-e29b-41d4-a716-446655440301', 'usr-ext-00007', 5, 'Excellent service, very responsive support team!', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(2, '550e8400-e29b-41d4-a716-446655440302', 'usr-ext-00008', 4, 'Good overall experience, minor delay in response.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(3, '550e8400-e29b-41d4-a716-446655440303', 'usr-ext-00009', 3, 'Average service, could be improved.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(4, '550e8400-e29b-41d4-a716-446655440304', 'usr-ext-00007', 2, 'Not satisfied, took too long to resolve issue.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- Bike Rental Feedback
INSERT INTO bike_rental_feedback (id, external_id, user_external_id, bike_rental_external_id, rate, comment, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, '550e8400-e29b-41d4-a716-446655440501', 'usr-ext-00007', 'bike-rental-ext-00101', 5, 'Great bike, smooth ride!', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(2, '550e8400-e29b-41d4-a716-446655440502', 'usr-ext-00008', 'bike-rental-ext-00102', 4, 'Good bike, but battery could last longer.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(3, '550e8400-e29b-41d4-a716-446655440503', 'usr-ext-00009', 'bike-rental-ext-00103', 3, 'Average experience, brakes were squeaky.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(4, '550e8400-e29b-41d4-a716-446655440504', 'usr-ext-00007', 'bike-rental-ext-00104', 2, 'Poor condition, motor had issues.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- Support Request
INSERT INTO support_request (id, external_id, user_external_id, bike_external_id, company_external_id, is_near_location, photo_url, error_code_id, support_request_status_id, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, '550e8400-e29b-41d4-a716-446655440401', 'usr-ext-00007', 'bike-ext-00201', 'company-ext-001', true, 'https://example.com/photos/issue1.jpg', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(2, '550e8400-e29b-41d4-a716-446655440402', 'usr-ext-00008', 'bike-ext-00202', 'company-ext-001', false, NULL, 2, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(3, '550e8400-e29b-41d4-a716-446655440403', 'usr-ext-00009', 'bike-ext-00203', 'company-ext-002', true, 'https://example.com/photos/issue3.jpg', NULL, 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(4, '550e8400-e29b-41d4-a716-446655440404', 'usr-ext-00007', NULL, 'company-ext-001', false, NULL, 3, 4, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(5, '550e8400-e29b-41d4-a716-446655440405', 'usr-ext-00008', 'bike-ext-00204', 'company-ext-001', true, 'https://example.com/photos/issue5.jpg', 4, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- Bike Type Bike Issue (Junction Table)
INSERT INTO bike_type_bike_issue (id, external_id, bike_type_external_id, bike_issue_id, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, '550e8400-e29b-41d4-a716-446655440601', 'bike-type-ext-001', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(2, '550e8400-e29b-41d4-a716-446655440602', 'bike-type-ext-001', 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(3, '550e8400-e29b-41d4-a716-446655440603', 'bike-type-ext-001', 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(4, '550e8400-e29b-41d4-a716-446655440604', 'bike-type-ext-002', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(5, '550e8400-e29b-41d4-a716-446655440605', 'bike-type-ext-002', 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(6, '550e8400-e29b-41d4-a716-446655440606', 'bike-type-ext-002', 4, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(7, '550e8400-e29b-41d4-a716-446655440607', 'bike-type-ext-003', 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(8, '550e8400-e29b-41d4-a716-446655440608', 'bike-type-ext-003', 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(9, '550e8400-e29b-41d4-a716-446655440609', 'bike-type-ext-003', 5, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- Support Request Bike Issue (Junction Table)
INSERT INTO support_request_bike_issue (id, external_id, support_request_id, bike_issue_id, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, '550e8400-e29b-41d4-a716-446655440701', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(2, '550e8400-e29b-41d4-a716-446655440702', 1, 6, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(3, '550e8400-e29b-41d4-a716-446655440703', 2, 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(4, '550e8400-e29b-41d4-a716-446655440704', 2, 12, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(5, '550e8400-e29b-41d4-a716-446655440705', 3, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(6, '550e8400-e29b-41d4-a716-446655440706', 4, 7, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(7, '550e8400-e29b-41d4-a716-446655440707', 5, 9, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- Support Request Guide Items
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

-- Bike Inspection
INSERT INTO bike_inspection (id, external_id, user_external_id, company_external_id, comment, bike_inspection_status_id, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, '550e8400-e29b-41d4-a716-446655440901', 'usr-ext-00007', 'company-ext-001', 'Pre-rental inspection completed successfully', 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(2, '550e8400-e29b-41d4-a716-446655440902', 'usr-ext-00008', 'company-ext-001', 'Post-rental inspection - minor issues found', 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(3, '550e8400-e29b-41d4-a716-446655440903', 'usr-ext-00009', 'company-ext-002', 'Routine maintenance inspection in progress', 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(4, '550e8400-e29b-41d4-a716-446655440904', 'usr-ext-00007', 'company-ext-001', 'New bike inspection pending', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- Bike Inspection Items
INSERT INTO bike_inspection_item (id, external_id, bike_inspection_id, bike_external_id, company_external_id, comment, bike_inspection_item_status_id, error_code_id, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, '550e8400-e29b-41d4-a716-446655441001', 1, 'bike-ext-00201', 'company-ext-001', 'Battery level excellent - 100%', 1, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(2, '550e8400-e29b-41d4-a716-446655441002', 1, 'bike-ext-00201', 'company-ext-001', 'Brakes working properly, no issues', 1, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(3, '550e8400-e29b-41d4-a716-446655441003', 1, 'bike-ext-00201', 'company-ext-001', 'Tires in good condition', 1, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(4, '550e8400-e29b-41d4-a716-446655441004', 2, 'bike-ext-00202', 'company-ext-001', 'Minor scratch on frame - cosmetic only', 2, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(5, '550e8400-e29b-41d4-a716-446655441005', 2, 'bike-ext-00202', 'company-ext-001', 'Brake pads worn - needs replacement', 3, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(6, '550e8400-e29b-41d4-a716-446655441006', 3, 'bike-ext-00203', 'company-ext-002', 'Tire pressure low - needs air', 3, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(7, '550e8400-e29b-41d4-a716-446655441007', 3, 'bike-ext-00203', 'company-ext-002', 'Chain needs lubrication - Display showing E001', 3, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(8, '550e8400-e29b-41d4-a716-446655441008', 4, 'bike-ext-00204', 'company-ext-001', 'Awaiting inspection', 1, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- Bike Inspection Item Photos
INSERT INTO bike_inspection_item_photo (id, external_id, bike_inspection_item_id, photo_url, company_external_id, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, '550e8400-e29b-41d4-a716-446655441201', 4, 'https://example.com/photos/inspection/scratch-frame-001.jpg', 'company-ext-001', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(2, '550e8400-e29b-41d4-a716-446655441202', 4, 'https://example.com/photos/inspection/scratch-frame-002.jpg', 'company-ext-001', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(3, '550e8400-e29b-41d4-a716-446655441203', 5, 'https://example.com/photos/inspection/brake-pads-worn-001.jpg', 'company-ext-001', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(4, '550e8400-e29b-41d4-a716-446655441204', 5, 'https://example.com/photos/inspection/brake-pads-worn-002.jpg', 'company-ext-001', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(5, '550e8400-e29b-41d4-a716-446655441205', 6, 'https://example.com/photos/inspection/tire-low-pressure-001.jpg', 'company-ext-002', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(6, '550e8400-e29b-41d4-a716-446655441206', 7, 'https://example.com/photos/inspection/chain-dry-001.jpg', 'company-ext-002', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- Bike Inspection Item Bike Issue (Junction Table)
INSERT INTO bike_inspection_item_bike_issue (id, external_id, bike_inspection_item_id, bike_issue_id, company_external_id, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, '550e8400-e29b-41d4-a716-446655441101', 5, 9, 'company-ext-001', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(2, '550e8400-e29b-41d4-a716-446655441102', 5, 2, 'company-ext-001', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(3, '550e8400-e29b-41d4-a716-446655441103', 6, 15, 'company-ext-002', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(4, '550e8400-e29b-41d4-a716-446655441104', 6, 4, 'company-ext-002', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- Bike Inspection Item Bike Unit (Junction Table)
INSERT INTO bike_inspection_item_bike_unit (id, external_id, bike_inspection_item_id, bike_unit_id, has_problem, company_external_id, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, '550e8400-e29b-41d4-a716-446655441301', 5, 5, true, 'company-ext-001', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(2, '550e8400-e29b-41d4-a716-446655441302', 6, 1, true, 'company-ext-002', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(3, '550e8400-e29b-41d4-a716-446655441303', 7, 3, false, 'company-ext-002', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- =====================================================================================================================
-- END OF SAMPLE DATA
-- =====================================================================================================================
