-- =====================================================================================================================
-- SUPPORT SERVICE - ADDITIONAL REFERENCE DATA (Flyway Migration V5)
-- =====================================================================================================================
-- Module: support-service
-- Database: PostgreSQL
-- Description: Insert additional reference data not covered by V4 (bike_engine_error_code junction table).
--              Depends on V4 which inserts error_code records.
--              All inserts use ON CONFLICT to ensure idempotency.
-- 
-- Author: Vitaliy Shvetsov
-- =====================================================================================================================

-- Bike Engine Error Code (Junction Table - Many-to-Many)
-- Links error codes to bike engines (one error code can be associated with multiple bike engines)
INSERT INTO bike_engine_error_code (id, external_id, bike_engine_external_id, error_code_id, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, '550e8400-e29b-41d4-a716-446655440211', 'bike-engine-ext-001', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(2, '550e8400-e29b-41d4-a716-446655440212', 'bike-engine-ext-001', 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(3, '550e8400-e29b-41d4-a716-446655440213', 'bike-engine-ext-001', 6, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(4, '550e8400-e29b-41d4-a716-446655440214', 'bike-engine-ext-002', 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(5, '550e8400-e29b-41d4-a716-446655440215', 'bike-engine-ext-002', 4, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(6, '550e8400-e29b-41d4-a716-446655440216', 'bike-engine-ext-002', 7, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(7, '550e8400-e29b-41d4-a716-446655440217', 'bike-engine-ext-003', 5, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(8, '550e8400-e29b-41d4-a716-446655440218', 'bike-engine-ext-003', 8, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
-- Example: E001 can also occur on bike-engine-ext-002 (demonstrates many-to-many)
(9, '550e8400-e29b-41d4-a716-446655440219', 'bike-engine-ext-002', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
-- Example: E002 can also occur on bike-engine-ext-003 (demonstrates many-to-many)
(10, '550e8400-e29b-41d4-a716-446655440220', 'bike-engine-ext-003', 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (id) DO NOTHING;

-- =====================================================================================================================
-- END OF ADDITIONAL REFERENCE DATA
-- =====================================================================================================================
