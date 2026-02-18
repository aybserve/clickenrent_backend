-- =====================================================================================================================
-- SUPPORT SERVICE - SEQUENCE RESET (Flyway Testdata V101)
-- =====================================================================================================================
-- Module: support-service
-- Database: PostgreSQL
-- Description: Reset sequences to correct values after inserting sample data.
--              Safe version: checks if sequence and table exist before resetting.
-- 
-- Author: Vitaliy Shvetsov
-- =====================================================================================================================

DO $$
DECLARE
    sequences TEXT[] := ARRAY[
        'responsible_person_id_seq:responsible_person',
        'support_request_status_id_seq:support_request_status',
        'bike_inspection_status_id_seq:bike_inspection_status',
        'bike_inspection_item_status_id_seq:bike_inspection_item_status',
        'error_code_id_seq:error_code',
        'bike_engine_error_code_id_seq:bike_engine_error_code',
        'bike_unit_id_seq:bike_unit',
        'bike_issue_id_seq:bike_issue',
        'feedback_id_seq:feedback',
        'bike_rental_feedback_id_seq:bike_rental_feedback',
        'support_request_id_seq:support_request',
        'support_request_guide_item_id_seq:support_request_guide_item',
        'support_request_bike_issue_id_seq:support_request_bike_issue',
        'bike_type_bike_issue_id_seq:bike_type_bike_issue',
        'bike_inspection_id_seq:bike_inspection',
        'bike_inspection_item_id_seq:bike_inspection_item',
        'bike_inspection_item_photo_id_seq:bike_inspection_item_photo',
        'bike_inspection_item_bike_issue_id_seq:bike_inspection_item_bike_issue',
        'bike_inspection_item_bike_unit_id_seq:bike_inspection_item_bike_unit'
    ];
    seq_info TEXT[];
    seq_name TEXT;
    table_name TEXT;
    max_id BIGINT;
BEGIN
    FOREACH seq_info IN ARRAY sequences
    LOOP
        seq_name := split_part(seq_info, ':', 1);
        table_name := split_part(seq_info, ':', 2);
        
        IF EXISTS (SELECT 1 FROM pg_class WHERE relkind = 'S' AND relname = seq_name)
           AND EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = table_name) THEN
            EXECUTE format('SELECT COALESCE(MAX(id), 0) FROM %I', table_name) INTO max_id;
            PERFORM setval(seq_name, GREATEST(max_id, 1));
        END IF;
    END LOOP;
END $$;

-- =====================================================================================================================
-- END OF SEQUENCE RESET
-- =====================================================================================================================
