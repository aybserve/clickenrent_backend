-- =====================================================================================================================
-- ANALYTICS SERVICE - REFERENCE DATA v1.0 (Flyway Migration)
-- =====================================================================================================================
-- Module: analytics-service
-- Database: PostgreSQL
-- Version: 1.0
-- Description: Reference data for analytics service
--
-- Note: Analytics service has no required reference/lookup data.
--       All data is generated from ETL pipelines processing actual rental data.
--       Sample/test data is loaded via db/testdata/ when staging or dev profile is active.
-- 
-- Author: Vitaliy Shvetsov
-- =====================================================================================================================

-- No reference data required for analytics service
-- All metrics are computed from source systems (rental-service, auth-service, support-service)

SELECT 'Analytics Service: No reference data needed. Schema ready for ETL pipeline data.' AS status;
