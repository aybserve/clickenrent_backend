-- =====================================================================================================================
-- ANALYTICS SERVICE - SAMPLE DATA (Flyway Testdata V100)
-- =====================================================================================================================
-- Module: analytics-service
-- Database: PostgreSQL
-- Description: Insert sample analytics data for development and testing.
--              All inserts use ON CONFLICT to ensure idempotency.
--              Only loaded when 'staging' or 'dev' profile is active.
-- 
-- Author: Vitaliy Shvetsov
-- =====================================================================================================================

-- Analytics Daily Summary (last 7 days)
INSERT INTO analytics_daily_summary (
    external_id, company_external_id, summary_date,
    new_customers, active_customers, total_customers,
    total_bike_rentals, completed_bike_rentals, cancelled_bike_rentals,
    total_bike_rental_duration_minutes, average_bike_rental_duration_minutes,
    total_revenue_cents, total_refunds_cents, average_bike_rental_revenue_cents,
    total_bikes, available_bikes, in_use_bikes, maintenance_bikes,
    total_locations, active_locations,
    new_tickets, resolved_tickets, open_tickets,
    is_deleted, date_created, last_date_modified
) VALUES
('daily-summary-today', 'company-ext-001', CURRENT_DATE, 
 2, 15, 150, 12, 11, 1, 720, 60, 12000, 0, 1000, 
 25, 18, 5, 2, 3, 3, 1, 2, 3, false, NOW(), NOW()),
('daily-summary-1d-ago', 'company-ext-001', CURRENT_DATE - INTERVAL '1 day',
 3, 18, 148, 15, 14, 1, 900, 60, 15000, 500, 1000,
 25, 17, 6, 2, 3, 3, 2, 3, 2, false, NOW(), NOW()),
('daily-summary-2d-ago', 'company-ext-001', CURRENT_DATE - INTERVAL '2 days',
 1, 16, 145, 14, 13, 1, 840, 60, 14000, 0, 1000,
 25, 19, 4, 2, 3, 3, 1, 1, 4, false, NOW(), NOW()),
('daily-summary-3d-ago', 'company-ext-001', CURRENT_DATE - INTERVAL '3 days',
 2, 17, 144, 13, 12, 1, 780, 60, 13000, 0, 1000,
 25, 18, 5, 2, 3, 3, 0, 2, 2, false, NOW(), NOW()),
('daily-summary-4d-ago', 'company-ext-001', CURRENT_DATE - INTERVAL '4 days',
 1, 14, 142, 11, 10, 1, 660, 60, 11000, 0, 1000,
 25, 20, 3, 2, 3, 3, 1, 1, 2, false, NOW(), NOW()),
('daily-summary-5d-ago', 'company-ext-001', CURRENT_DATE - INTERVAL '5 days',
 2, 16, 141, 16, 15, 1, 960, 60, 16000, 0, 1000,
 25, 17, 6, 2, 3, 3, 2, 3, 1, false, NOW(), NOW()),
('daily-summary-6d-ago', 'company-ext-001', CURRENT_DATE - INTERVAL '6 days',
 1, 15, 139, 14, 13, 1, 840, 60, 14000, 0, 1000,
 25, 18, 5, 2, 3, 3, 1, 2, 2, false, NOW(), NOW())
ON CONFLICT (external_id) DO NOTHING;

-- Analytics Hourly Metrics (sample hours)
INSERT INTO analytics_hourly_metrics (
    external_id, company_external_id, metric_hour,
    bike_rentals_started, bike_rentals_completed, bike_rental_revenue_cents,
    active_customers, new_registrations,
    is_deleted, date_created, last_date_modified
) VALUES
('hourly-metrics-08h', 'company-ext-001', 
 DATE_TRUNC('hour', CURRENT_TIMESTAMP - INTERVAL '16 hours'),
 3, 3, 3000, 5, 1, false, NOW(), NOW()),
('hourly-metrics-12h', 'company-ext-001',
 DATE_TRUNC('hour', CURRENT_TIMESTAMP - INTERVAL '12 hours'),
 5, 5, 5000, 8, 0, false, NOW(), NOW()),
('hourly-metrics-18h', 'company-ext-001',
 DATE_TRUNC('hour', CURRENT_TIMESTAMP - INTERVAL '6 hours'),
 4, 3, 3000, 6, 1, false, NOW(), NOW()),
('hourly-metrics-now', 'company-ext-001',
 DATE_TRUNC('hour', CURRENT_TIMESTAMP),
 2, 2, 2000, 4, 0, false, NOW(), NOW())
ON CONFLICT (external_id) DO NOTHING;

-- Analytics Bike Metrics (sample bikes)
INSERT INTO analytics_bike_metrics (
    external_id, company_external_id, metric_date, bike_external_id, bike_code,
    total_bike_rentals, total_duration_minutes, bike_rental_revenue_cents,
    available_hours, in_use_hours, maintenance_hours,
    is_deleted, date_created, last_date_modified
) VALUES
('bike-metrics-bike1-today', 'company-ext-001', CURRENT_DATE, 'BIKE-001', 'BIKE-001',
 8, 480, 8000, 16.0, 8.0, 0.0, false, NOW(), NOW()),
('bike-metrics-bike1-yesterday', 'company-ext-001', CURRENT_DATE - INTERVAL '1 day', 'BIKE-001', 'BIKE-001',
 9, 540, 9000, 15.0, 9.0, 0.0, false, NOW(), NOW()),
('bike-metrics-bike2-today', 'company-ext-001', CURRENT_DATE, 'BIKE-002', 'BIKE-002',
 4, 240, 4000, 20.0, 4.0, 0.0, false, NOW(), NOW()),
('bike-metrics-bike2-yesterday', 'company-ext-001', CURRENT_DATE - INTERVAL '1 day', 'BIKE-002', 'BIKE-002',
 6, 360, 6000, 18.0, 6.0, 0.0, false, NOW(), NOW()),
('bike-metrics-bike3-today', 'company-ext-001', CURRENT_DATE, 'BIKE-003', 'BIKE-003',
 0, 0, 0, 21.0, 0.0, 3.0, false, NOW(), NOW()),
('bike-metrics-bike3-yesterday', 'company-ext-001', CURRENT_DATE - INTERVAL '1 day', 'BIKE-003', 'BIKE-003',
 5, 300, 5000, 19.0, 5.0, 0.0, false, NOW(), NOW())
ON CONFLICT (external_id) DO NOTHING;

-- Analytics Location Metrics (sample locations)
INSERT INTO analytics_location_metrics (
    external_id, company_external_id, metric_date, location_external_id,
    total_pickups, total_dropoffs, unique_customers, bike_rental_revenue_cents,
    average_bikes_available,
    is_deleted, date_created, last_date_modified
) VALUES
('location-metrics-loc1-today', 'company-ext-001', CURRENT_DATE, '550e8400-e29b-41d4-a716-446655440101',
 15, 18, 12, 15000, 8.0, false, NOW(), NOW()),
('location-metrics-loc1-yesterday', 'company-ext-001', CURRENT_DATE - INTERVAL '1 day', '550e8400-e29b-41d4-a716-446655440101',
 18, 16, 15, 18000, 7.0, false, NOW(), NOW()),
('location-metrics-loc2-today', 'company-ext-001', CURRENT_DATE, '550e8400-e29b-41d4-a716-446655440102',
 8, 9, 7, 8000, 12.0, false, NOW(), NOW()),
('location-metrics-loc2-yesterday', 'company-ext-001', CURRENT_DATE - INTERVAL '1 day', '550e8400-e29b-41d4-a716-446655440102',
 10, 8, 8, 10000, 11.0, false, NOW(), NOW()),
('location-metrics-loc3-today', 'company-ext-002', CURRENT_DATE, '550e8400-e29b-41d4-a716-446655440103',
 5, 4, 5, 5000, 15.0, false, NOW(), NOW()),
('location-metrics-loc3-yesterday', 'company-ext-002', CURRENT_DATE - INTERVAL '1 day', '550e8400-e29b-41d4-a716-446655440103',
 12, 13, 10, 12000, 9.0, false, NOW(), NOW())
ON CONFLICT (external_id) DO NOTHING;

-- =====================================================================================================================
-- END OF SAMPLE DATA
-- =====================================================================================================================
