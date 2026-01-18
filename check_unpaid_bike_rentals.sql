-- =====================================================================================================================
-- CHECK UNPAID BIKE RENTALS
-- =====================================================================================================================
-- Run this on the clickenrent_rental database to see all bike rentals and their payout status
-- Usage: psql -U postgres -d clickenrent_rental -f check_unpaid_bike_rentals.sql

-- 1. Check all bike rentals with their payout status
SELECT 
    br.id,
    br.external_id,
    br.start_date_time,
    br.end_date_time,
    br.total_price,
    br.currency,
    br.is_revenue_share_paid,
    brs.code as status,
    b.is_b2b_rentable,
    b.revenue_share_percent,
    l.name as location_name
FROM bike_rentals br
LEFT JOIN bike_rental_status brs ON br.bike_rental_status_id = brs.id
LEFT JOIN bikes b ON br.bike_id = b.id
LEFT JOIN hubs h ON b.hub_id = h.id
LEFT JOIN locations l ON h.location_id = l.id
ORDER BY br.start_date_time DESC
LIMIT 20;

-- 2. Check specifically for unpaid bike rentals from December 2025
SELECT 
    br.id,
    br.external_id,
    br.start_date_time,
    br.end_date_time,
    br.total_price,
    br.currency,
    br.is_revenue_share_paid,
    brs.code as status,
    b.is_b2b_rentable,
    b.revenue_share_percent,
    l.name as location_name
FROM bike_rentals br
LEFT JOIN bike_rental_status brs ON br.bike_rental_status_id = brs.id
LEFT JOIN bikes b ON br.bike_id = b.id
LEFT JOIN hubs h ON b.hub_id = h.id
LEFT JOIN locations l ON h.location_id = l.id
WHERE br.is_revenue_share_paid = false
  AND br.start_date_time >= '2025-12-01 00:00:00'
  AND br.start_date_time <= '2025-12-31 23:59:59'
ORDER BY br.start_date_time DESC;

-- 3. Count unpaid bike rentals by month
SELECT 
    TO_CHAR(br.start_date_time, 'YYYY-MM') as month,
    COUNT(*) as unpaid_rentals,
    SUM(br.total_price) as total_amount
FROM bike_rentals br
WHERE br.is_revenue_share_paid = false
GROUP BY TO_CHAR(br.start_date_time, 'YYYY-MM')
ORDER BY month DESC;

-- 4. Check if any bikes are configured for B2B revenue sharing
SELECT 
    b.id,
    b.external_id,
    b.name,
    b.is_b2b_rentable,
    b.revenue_share_percent,
    l.name as location_name
FROM bikes b
LEFT JOIN hubs h ON b.hub_id = h.id
LEFT JOIN locations l ON h.location_id = l.id
WHERE b.is_b2b_rentable = true
ORDER BY b.id
LIMIT 10;
