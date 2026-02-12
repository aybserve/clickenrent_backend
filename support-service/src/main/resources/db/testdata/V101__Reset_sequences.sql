-- =====================================================================================================================
-- SUPPORT SERVICE - SEQUENCE RESET (Flyway Testdata V101)
-- =====================================================================================================================
-- Module: support-service
-- Database: PostgreSQL
-- Description: Reset sequences to correct values after inserting sample data.
--              Only loaded when 'staging' or 'dev' profile is active.
-- 
-- Author: Vitaliy Shvetsov
-- =====================================================================================================================

SELECT setval('responsible_person_id_seq', (SELECT COALESCE(MAX(id), 1) FROM responsible_person));
SELECT setval('support_request_status_id_seq', (SELECT COALESCE(MAX(id), 1) FROM support_request_status));
SELECT setval('bike_inspection_status_id_seq', (SELECT COALESCE(MAX(id), 1) FROM bike_inspection_status));
SELECT setval('bike_inspection_item_status_id_seq', (SELECT COALESCE(MAX(id), 1) FROM bike_inspection_item_status));
SELECT setval('error_code_id_seq', (SELECT COALESCE(MAX(id), 1) FROM error_code));
SELECT setval('bike_engine_error_code_id_seq', (SELECT COALESCE(MAX(id), 1) FROM bike_engine_error_code));
SELECT setval('bike_unit_id_seq', (SELECT COALESCE(MAX(id), 1) FROM bike_unit));
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
SELECT setval('bike_inspection_item_bike_unit_id_seq', (SELECT COALESCE(MAX(id), 1) FROM bike_inspection_item_bike_unit));

-- =====================================================================================================================
-- END OF SEQUENCE RESET
-- =====================================================================================================================
