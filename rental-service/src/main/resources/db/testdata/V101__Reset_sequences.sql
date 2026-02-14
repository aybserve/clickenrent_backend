-- =====================================================================================================================
-- RENTAL SERVICE - SEQUENCE RESET (Flyway Testdata V101)
-- =====================================================================================================================
-- Module: rental-service
-- Database: PostgreSQL
-- Description: Reset sequences to correct values after inserting sample data.
-- 
-- Author: Vitaliy Shvetsov
-- =====================================================================================================================

SELECT setval('bike_type_id_seq', (SELECT COALESCE(MAX(id), 1) FROM bike_type));
SELECT setval('bike_status_id_seq', (SELECT COALESCE(MAX(id), 1) FROM bike_status));
SELECT setval('bike_rental_status_id_seq', (SELECT COALESCE(MAX(id), 1) FROM bike_rental_status));
SELECT setval('rental_status_id_seq', (SELECT COALESCE(MAX(id), 1) FROM rental_status));
SELECT setval('rental_unit_id_seq', (SELECT COALESCE(MAX(id), 1) FROM rental_unit));
SELECT setval('ride_status_id_seq', (SELECT COALESCE(MAX(id), 1) FROM ride_status));
SELECT setval('location_role_id_seq', (SELECT COALESCE(MAX(id), 1) FROM location_role));
SELECT setval('b2b_sale_status_id_seq', (SELECT COALESCE(MAX(id), 1) FROM b2b_sale_status));
SELECT setval('b2b_sale_order_status_id_seq', (SELECT COALESCE(MAX(id), 1) FROM b2b_sale_order_status));
SELECT setval('b2b_subscription_status_id_seq', (SELECT COALESCE(MAX(id), 1) FROM b2b_subscription_status));
SELECT setval('b2b_subscription_order_status_id_seq', (SELECT COALESCE(MAX(id), 1) FROM b2b_subscription_order_status));
SELECT setval('charging_station_status_id_seq', (SELECT COALESCE(MAX(id), 1) FROM charging_station_status));
SELECT setval('lock_status_id_seq', (SELECT COALESCE(MAX(id), 1) FROM lock_status));
SELECT setval('lock_provider_id_seq', (SELECT COALESCE(MAX(id), 1) FROM lock_provider));
SELECT setval('bike_brand_id_seq', (SELECT COALESCE(MAX(id), 1) FROM bike_brand));
SELECT setval('bike_engine_id_seq', (SELECT COALESCE(MAX(id), 1) FROM bike_engine));
SELECT setval('part_brand_id_seq', (SELECT COALESCE(MAX(id), 1) FROM part_brand));
SELECT setval('part_category_id_seq', (SELECT COALESCE(MAX(id), 1) FROM part_category));
SELECT setval('charging_station_brand_id_seq', (SELECT COALESCE(MAX(id), 1) FROM charging_station_brand));
SELECT setval('coordinates_id_seq', (SELECT COALESCE(MAX(id), 1) FROM coordinates));
SELECT setval('location_id_seq', (SELECT COALESCE(MAX(id), 1) FROM location));
SELECT setval('location_image_id_seq', (SELECT COALESCE(MAX(id), 1) FROM location_image));
SELECT setval('hub_id_seq', (SELECT COALESCE(MAX(id), 1) FROM hub));
SELECT setval('hub_image_id_seq', (SELECT COALESCE(MAX(id), 1) FROM hub_image));
SELECT setval('lock_entity_id_seq', (SELECT COALESCE(MAX(id), 1) FROM lock_entity));
SELECT setval('key_entity_id_seq', (SELECT COALESCE(MAX(id), 1) FROM key_entity));
SELECT setval('product_id_seq', (SELECT COALESCE(MAX(id), 1) FROM product));
SELECT setval('service_id_seq', (SELECT COALESCE(MAX(id), 1) FROM service));
SELECT setval('rental_plan_id_seq', (SELECT COALESCE(MAX(id), 1) FROM rental_plan));
SELECT setval('bike_model_rental_plan_id_seq', (SELECT COALESCE(MAX(id), 1) FROM bike_model_rental_plan));
SELECT setval('user_location_id_seq', (SELECT COALESCE(MAX(id), 1) FROM user_location));
SELECT setval('b2b_sale_id_seq', (SELECT COALESCE(MAX(id), 1) FROM b2b_sale));
SELECT setval('b2b_sale_order_id_seq', (SELECT COALESCE(MAX(id), 1) FROM b2b_sale_order));
SELECT setval('b2b_sale_item_id_seq', (SELECT COALESCE(MAX(id), 1) FROM b2b_sale_item));
SELECT setval('b2b_sale_order_item_id_seq', (SELECT COALESCE(MAX(id), 1) FROM b2b_sale_order_item));
SELECT setval('b2b_subscription_id_seq', (SELECT COALESCE(MAX(id), 1) FROM b2b_subscription));
SELECT setval('b2b_subscription_item_id_seq', (SELECT COALESCE(MAX(id), 1) FROM b2b_subscription_item));
SELECT setval('b2b_subscription_order_id_seq', (SELECT COALESCE(MAX(id), 1) FROM b2b_subscription_order));
SELECT setval('b2b_subscription_order_item_id_seq', (SELECT COALESCE(MAX(id), 1) FROM b2b_subscription_order_item));
SELECT setval('rental_id_seq', (SELECT COALESCE(MAX(id), 1) FROM rental));
SELECT setval('bike_rental_id_seq', (SELECT COALESCE(MAX(id), 1) FROM bike_rental));
SELECT setval('bike_reservation_id_seq', (SELECT COALESCE(MAX(id), 1) FROM bike_reservation));
SELECT setval('ride_id_seq', (SELECT COALESCE(MAX(id), 1) FROM ride));
SELECT setval('stock_movement_id_seq', (SELECT COALESCE(MAX(id), 1) FROM stock_movement));
SELECT setval('bike_model_part_id_seq', (SELECT COALESCE(MAX(id), 1) FROM bike_model_part));

-- =====================================================================================================================
-- END OF SEQUENCE RESET
-- =====================================================================================================================
