-- ========================================
-- Comprehensive Test Data for Support Service
-- ========================================

-- ========================================
-- 1. RESPONSIBLE PERSON
-- ========================================
INSERT INTO responsible_person (id, name, is_deleted, created_at, updated_at, created_by, last_modified_by)
VALUES (1, 'John Mechanic', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system');

INSERT INTO responsible_person (id, name, is_deleted, created_at, updated_at, created_by, last_modified_by)
VALUES (2, 'Sarah Electrician', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system');

INSERT INTO responsible_person (id, name, is_deleted, created_at, updated_at, created_by, last_modified_by)
VALUES (3, 'Mike Support Staff', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system');

-- ========================================
-- 2. SUPPORT REQUEST STATUS
-- ========================================
INSERT INTO support_request_status (id, name, is_deleted, created_at, updated_at, created_by, last_modified_by)
VALUES (1, 'OPEN', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system');

INSERT INTO support_request_status (id, name, is_deleted, created_at, updated_at, created_by, last_modified_by)
VALUES (2, 'IN_PROGRESS', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system');

INSERT INTO support_request_status (id, name, is_deleted, created_at, updated_at, created_by, last_modified_by)
VALUES (3, 'RESOLVED', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system');

INSERT INTO support_request_status (id, name, is_deleted, created_at, updated_at, created_by, last_modified_by)
VALUES (4, 'CLOSED', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system');

-- ========================================
-- 3. BIKE ISSUE (Hierarchical Structure)
-- ========================================
-- Root Issues
INSERT INTO bike_issue (id, external_id, name, description, parent_bike_issue_id, is_fixable_by_client, responsible_person_id, is_deleted, created_at, updated_at, created_by, last_modified_by)
VALUES (1, '550e8400-e29b-41d4-a716-446655440101', 'Battery Issues', 'Problems related to bike battery', NULL, false, 2, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system');

INSERT INTO bike_issue (id, external_id, name, description, parent_bike_issue_id, is_fixable_by_client, responsible_person_id, is_deleted, created_at, updated_at, created_by, last_modified_by)
VALUES (2, '550e8400-e29b-41d4-a716-446655440102', 'Brake Issues', 'Problems with bike braking system', NULL, false, 1, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system');

INSERT INTO bike_issue (id, external_id, name, description, parent_bike_issue_id, is_fixable_by_client, responsible_person_id, is_deleted, created_at, updated_at, created_by, last_modified_by)
VALUES (3, '550e8400-e29b-41d4-a716-446655440103', 'Motor Issues', 'Problems with electric motor', NULL, false, 1, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system');

-- Sub Issues (Battery)
INSERT INTO bike_issue (id, external_id, name, description, parent_bike_issue_id, is_fixable_by_client, responsible_person_id, is_deleted, created_at, updated_at, created_by, last_modified_by)
VALUES (4, '550e8400-e29b-41d4-a716-446655440104', 'Battery Dead', 'Battery completely discharged', 1, true, 2, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system');

INSERT INTO bike_issue (id, external_id, name, description, parent_bike_issue_id, is_fixable_by_client, responsible_person_id, is_deleted, created_at, updated_at, created_by, last_modified_by)
VALUES (5, '550e8400-e29b-41d4-a716-446655440105', 'Battery Connection Loose', 'Battery connector not properly attached', 1, true, 2, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system');

-- Sub Issues (Brakes)
INSERT INTO bike_issue (id, external_id, name, description, parent_bike_issue_id, is_fixable_by_client, responsible_person_id, is_deleted, created_at, updated_at, created_by, last_modified_by)
VALUES (6, '550e8400-e29b-41d4-a716-446655440106', 'Brake Pads Worn', 'Brake pads need replacement', 2, false, 1, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system');

INSERT INTO bike_issue (id, external_id, name, description, parent_bike_issue_id, is_fixable_by_client, responsible_person_id, is_deleted, created_at, updated_at, created_by, last_modified_by)
VALUES (7, '550e8400-e29b-41d4-a716-446655440107', 'Brake Cable Loose', 'Brake cable needs adjustment', 2, false, 1, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system');

-- Sub Issues (Motor)
INSERT INTO bike_issue (id, external_id, name, description, parent_bike_issue_id, is_fixable_by_client, responsible_person_id, is_deleted, created_at, updated_at, created_by, last_modified_by)
VALUES (8, '550e8400-e29b-41d4-a716-446655440108', 'Motor Not Starting', 'Electric motor fails to start', 3, false, 1, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system');

-- ========================================
-- 4. ERROR CODE
-- ========================================
INSERT INTO error_code (id, external_id, name, bike_engine_id, description, common_cause, diagnostic_steps, recommended_fix, notes, is_fixable_by_client, is_deleted, created_at, updated_at, created_by, last_modified_by)
VALUES (1, '550e8400-e29b-41d4-a716-446655440201', 'E001', 1, 'Battery Low Voltage', 'Battery discharged or faulty cell', 'Check battery voltage with multimeter', 'Charge or replace battery', 'Common error in cold weather', false, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system');

INSERT INTO error_code (id, external_id, name, bike_engine_id, description, common_cause, diagnostic_steps, recommended_fix, notes, is_fixable_by_client, is_deleted, created_at, updated_at, created_by, last_modified_by)
VALUES (2, '550e8400-e29b-41d4-a716-446655440202', 'E002', 1, 'Motor Controller Error', 'Faulty controller or wiring', 'Inspect controller connections', 'Replace motor controller', 'May require professional service', false, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system');

INSERT INTO error_code (id, external_id, name, bike_engine_id, description, common_cause, diagnostic_steps, recommended_fix, notes, is_fixable_by_client, is_deleted, created_at, updated_at, created_by, last_modified_by)
VALUES (3, '550e8400-e29b-41d4-a716-446655440203', 'E003', 2, 'Throttle Sensor Fault', 'Throttle sensor disconnected or damaged', 'Check throttle sensor connection', 'Reconnect or replace sensor', 'Client can check connection', true, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system');

INSERT INTO error_code (id, external_id, name, bike_engine_id, description, common_cause, diagnostic_steps, recommended_fix, notes, is_fixable_by_client, is_deleted, created_at, updated_at, created_by, last_modified_by)
VALUES (4, '550e8400-e29b-41d4-a716-446655440204', 'E004', 2, 'Brake Sensor Active', 'Brake lever engaged or sensor stuck', 'Check brake lever and sensor', 'Adjust or clean brake sensor', 'Usually easy fix', true, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system');

INSERT INTO error_code (id, external_id, name, bike_engine_id, description, common_cause, diagnostic_steps, recommended_fix, notes, is_fixable_by_client, is_deleted, created_at, updated_at, created_by, last_modified_by)
VALUES (5, '550e8400-e29b-41d4-a716-446655440205', 'E005', 3, 'Overheat Protection', 'Motor or controller overheating', 'Let bike cool down for 30 minutes', 'Avoid steep hills or heavy loads', 'Temporary error', true, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system');

-- ========================================
-- 5. FEEDBACK
-- ========================================
INSERT INTO feedback (id, external_id, user_id, rate, comment, date_time, is_deleted, created_at, updated_at, created_by, last_modified_by)
VALUES (1, '550e8400-e29b-41d4-a716-446655440301', 1, 5, 'Excellent service, very responsive support team!', CURRENT_TIMESTAMP, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system');

INSERT INTO feedback (id, external_id, user_id, rate, comment, date_time, is_deleted, created_at, updated_at, created_by, last_modified_by)
VALUES (2, '550e8400-e29b-41d4-a716-446655440302', 2, 4, 'Good overall experience, minor delay in response.', CURRENT_TIMESTAMP, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system');

INSERT INTO feedback (id, external_id, user_id, rate, comment, date_time, is_deleted, created_at, updated_at, created_by, last_modified_by)
VALUES (3, '550e8400-e29b-41d4-a716-446655440303', 3, 3, 'Average service, could be improved.', CURRENT_TIMESTAMP, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system');

INSERT INTO feedback (id, external_id, user_id, rate, comment, date_time, is_deleted, created_at, updated_at, created_by, last_modified_by)
VALUES (4, '550e8400-e29b-41d4-a716-446655440304', 1, 2, 'Not satisfied, took too long to resolve issue.', CURRENT_TIMESTAMP, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system');

-- ========================================
-- 6. BIKE RENTAL FEEDBACK
-- ========================================
INSERT INTO bike_rental_feedback (id, user_id, bike_rental_id, rate, comment, date_time, is_deleted, created_at, updated_at, created_by, last_modified_by)
VALUES (1, 1, 101, 5, 'Great bike, smooth ride!', CURRENT_TIMESTAMP, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system');

INSERT INTO bike_rental_feedback (id, user_id, bike_rental_id, rate, comment, date_time, is_deleted, created_at, updated_at, created_by, last_modified_by)
VALUES (2, 2, 102, 4, 'Good bike, but battery could last longer.', CURRENT_TIMESTAMP, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system');

INSERT INTO bike_rental_feedback (id, user_id, bike_rental_id, rate, comment, date_time, is_deleted, created_at, updated_at, created_by, last_modified_by)
VALUES (3, 3, 103, 3, 'Average experience, brakes were squeaky.', CURRENT_TIMESTAMP, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system');

INSERT INTO bike_rental_feedback (id, user_id, bike_rental_id, rate, comment, date_time, is_deleted, created_at, updated_at, created_by, last_modified_by)
VALUES (4, 1, 104, 2, 'Poor condition, motor had issues.', CURRENT_TIMESTAMP, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system');

-- ========================================
-- 7. SUPPORT REQUEST
-- ========================================
INSERT INTO support_request (id, external_id, user_id, bike_id, is_near_location, photo_url, error_code_id, support_request_status_id, is_deleted, created_at, updated_at, created_by, last_modified_by)
VALUES (1, '550e8400-e29b-41d4-a716-446655440401', 1, 201, true, 'https://example.com/photos/issue1.jpg', 1, 1, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system');

INSERT INTO support_request (id, external_id, user_id, bike_id, is_near_location, photo_url, error_code_id, support_request_status_id, is_deleted, created_at, updated_at, created_by, last_modified_by)
VALUES (2, '550e8400-e29b-41d4-a716-446655440402', 2, 202, false, NULL, 2, 2, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system');

INSERT INTO support_request (id, external_id, user_id, bike_id, is_near_location, photo_url, error_code_id, support_request_status_id, is_deleted, created_at, updated_at, created_by, last_modified_by)
VALUES (3, '550e8400-e29b-41d4-a716-446655440403', 3, 203, true, 'https://example.com/photos/issue3.jpg', NULL, 3, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system');

INSERT INTO support_request (id, external_id, user_id, bike_id, is_near_location, photo_url, error_code_id, support_request_status_id, is_deleted, created_at, updated_at, created_by, last_modified_by)
VALUES (4, '550e8400-e29b-41d4-a716-446655440404', 1, NULL, false, NULL, 3, 4, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system');

INSERT INTO support_request (id, external_id, user_id, bike_id, is_near_location, photo_url, error_code_id, support_request_status_id, is_deleted, created_at, updated_at, created_by, last_modified_by)
VALUES (5, '550e8400-e29b-41d4-a716-446655440405', 2, 204, true, 'https://example.com/photos/issue5.jpg', 4, 1, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system');

-- ========================================
-- 8. BIKE TYPE BIKE ISSUE (Junction Table)
-- ========================================
INSERT INTO bike_type_bike_issue (id, bike_type_id, bike_issue_id)
VALUES (1, 1, 1);

INSERT INTO bike_type_bike_issue (id, bike_type_id, bike_issue_id)
VALUES (2, 1, 2);

INSERT INTO bike_type_bike_issue (id, bike_type_id, bike_issue_id)
VALUES (3, 2, 1);

INSERT INTO bike_type_bike_issue (id, bike_type_id, bike_issue_id)
VALUES (4, 2, 3);

INSERT INTO bike_type_bike_issue (id, bike_type_id, bike_issue_id)
VALUES (5, 3, 2);

INSERT INTO bike_type_bike_issue (id, bike_type_id, bike_issue_id)
VALUES (6, 3, 3);

-- ========================================
-- 9. SUPPORT REQUEST BIKE ISSUE (Junction Table)
-- ========================================
INSERT INTO support_request_bike_issue (id, support_request_id, bike_issue_id)
VALUES (1, 1, 1);

INSERT INTO support_request_bike_issue (id, support_request_id, bike_issue_id)
VALUES (2, 1, 4);

INSERT INTO support_request_bike_issue (id, support_request_id, bike_issue_id)
VALUES (3, 2, 3);

INSERT INTO support_request_bike_issue (id, support_request_id, bike_issue_id)
VALUES (4, 2, 8);

INSERT INTO support_request_bike_issue (id, support_request_id, bike_issue_id)
VALUES (5, 3, 2);

INSERT INTO support_request_bike_issue (id, support_request_id, bike_issue_id)
VALUES (6, 4, 5);

INSERT INTO support_request_bike_issue (id, support_request_id, bike_issue_id)
VALUES (7, 5, 6);

-- ========================================
-- 10. SUPPORT REQUEST GUIDE ITEM
-- ========================================
INSERT INTO support_request_guide_item (id, item_index, description, bike_issue_id, support_request_status_id, is_deleted, created_at, updated_at, created_by, last_modified_by)
VALUES (1, 1, 'Check if battery is properly connected', 1, 1, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system');

INSERT INTO support_request_guide_item (id, item_index, description, bike_issue_id, support_request_status_id, is_deleted, created_at, updated_at, created_by, last_modified_by)
VALUES (2, 2, 'Verify battery charge level on display', 1, 1, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system');

INSERT INTO support_request_guide_item (id, item_index, description, bike_issue_id, support_request_status_id, is_deleted, created_at, updated_at, created_by, last_modified_by)
VALUES (3, 3, 'Try charging battery for at least 2 hours', 1, 2, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system');

INSERT INTO support_request_guide_item (id, item_index, description, bike_issue_id, support_request_status_id, is_deleted, created_at, updated_at, created_by, last_modified_by)
VALUES (4, 1, 'Test brake lever responsiveness', 2, 1, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system');

INSERT INTO support_request_guide_item (id, item_index, description, bike_issue_id, support_request_status_id, is_deleted, created_at, updated_at, created_by, last_modified_by)
VALUES (5, 2, 'Check brake pads for wear and alignment', 2, 1, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system');

INSERT INTO support_request_guide_item (id, item_index, description, bike_issue_id, support_request_status_id, is_deleted, created_at, updated_at, created_by, last_modified_by)
VALUES (6, 3, 'Adjust brake cable tension if needed', 2, 2, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system');

INSERT INTO support_request_guide_item (id, item_index, description, bike_issue_id, support_request_status_id, is_deleted, created_at, updated_at, created_by, last_modified_by)
VALUES (7, 1, 'Power cycle the bike (turn off and on)', 3, 1, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system');

INSERT INTO support_request_guide_item (id, item_index, description, bike_issue_id, support_request_status_id, is_deleted, created_at, updated_at, created_by, last_modified_by)
VALUES (8, 2, 'Check for error codes on display', 3, 1, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system');


