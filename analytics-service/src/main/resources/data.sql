-- =====================================================================================================================
-- ANALYTICS SERVICE - DATA INITIALIZATION (Fallback mode)
-- =====================================================================================================================
-- Module: analytics-service
-- Database: PostgreSQL
-- Description: INSERT-only data initialization for when Flyway is disabled.
--              Activate by setting: FLYWAY_MIGRATE=false and SQL_INIT_MODE=always
--              Flyway is the source of truth for schema and data migrations.
--
-- Note: Analytics service has no required reference/lookup data.
--       All data is generated from ETL pipelines processing actual rental data.
--       Sample/test data is managed by Flyway via db/testdata/ (staging/dev profiles).
-- 
-- Author: Vitaliy Shvetsov
-- =====================================================================================================================

SELECT 'Analytics Service: No reference data needed. Schema ready for ETL pipeline data.' AS status;
