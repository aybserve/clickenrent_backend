-- =====================================================================================================================
-- RENTAL SERVICE - SEQUENCE RESET (Flyway Testdata V101)
-- =====================================================================================================================
-- Module: rental-service
-- Database: PostgreSQL
-- Description: Reset sequences to correct values after inserting sample data.
--              Safe version: checks if sequence and table exist before resetting.
-- 
-- Author: Vitaliy Shvetsov
-- =====================================================================================================================

DO $$
DECLARE
    sequences TEXT[] := ARRAY[
        'bike_type_id_seq:bike_type',
        'bike_status_id_seq:bike_status',
        'bike_rental_status_id_seq:bike_rental_status',
        'rental_status_id_seq:rental_status',
        'rental_unit_id_seq:rental_unit',
        'ride_status_id_seq:ride_status',
        'location_role_id_seq:location_role',
        'b2b_sale_status_id_seq:b2b_sale_status',
        'b2b_sale_order_status_id_seq:b2b_sale_order_status',
        'b2b_subscription_status_id_seq:b2b_subscription_status',
        'b2b_subscription_order_status_id_seq:b2b_subscription_order_status',
        'charging_station_status_id_seq:charging_station_status',
        'lock_status_id_seq:lock_status',
        'lock_provider_id_seq:lock_provider',
        'bike_brand_id_seq:bike_brand',
        'bike_engine_id_seq:bike_engine',
        'part_brand_id_seq:part_brand',
        'part_category_id_seq:part_category',
        'charging_station_brand_id_seq:charging_station_brand',
        'coordinates_id_seq:coordinates',
        'location_id_seq:location',
        'location_image_id_seq:location_image',
        'hub_id_seq:hub',
        'hub_image_id_seq:hub_image',
        'lock_entity_id_seq:lock_entity',
        'key_entity_id_seq:key_entity',
        'product_id_seq:product',
        'service_id_seq:service',
        'rental_plan_id_seq:rental_plan',
        'bike_model_rental_plan_id_seq:bike_model_rental_plan',
        'user_location_id_seq:user_location',
        'b2b_sale_id_seq:b2b_sale',
        'b2b_sale_order_id_seq:b2b_sale_order',
        'b2b_sale_item_id_seq:b2b_sale_item',
        'b2b_sale_order_item_id_seq:b2b_sale_order_item',
        'b2b_subscription_id_seq:b2b_subscription',
        'b2b_subscription_item_id_seq:b2b_subscription_item',
        'b2b_subscription_order_id_seq:b2b_subscription_order',
        'b2b_subscription_order_item_id_seq:b2b_subscription_order_item',
        'rental_id_seq:rental',
        'bike_rental_id_seq:bike_rental',
        'bike_reservation_id_seq:bike_reservation',
        'ride_id_seq:ride',
        'stock_movement_id_seq:stock_movement',
        'bike_model_part_id_seq:bike_model_part'
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
