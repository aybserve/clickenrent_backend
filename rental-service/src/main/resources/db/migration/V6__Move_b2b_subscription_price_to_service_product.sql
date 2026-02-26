-- =====================================================================================================================
-- RENTAL SERVICE - MOVE B2B SUBSCRIPTION PRICE TO SERVICE PRODUCT (Flyway Migration V6)
-- =====================================================================================================================
-- Module: rental-service
-- Database: PostgreSQL
-- Description: Move b2b_subscription_price field from service table to service_product table.
--              This refactoring better aligns the pricing with the product structure.
--              
--              NOTE: This migration is idempotent and handles the case where Hibernate's ddl-auto=update
--              may have already dropped the column from service table before this migration runs.
-- 
-- Author: Vitaliy Shvetsov
-- =====================================================================================================================

-- Step 1: Add b2b_subscription_price column to service_product table
ALTER TABLE service_product 
ADD COLUMN IF NOT EXISTS b2b_subscription_price NUMERIC(10,2);

-- Step 2: Migrate data from service to service_product (only if source column still exists)
-- Check if the column exists in service table before attempting data migration
DO $$
BEGIN
    IF EXISTS (
        SELECT 1 
        FROM information_schema.columns 
        WHERE table_name = 'service' 
        AND column_name = 'b2b_subscription_price'
    ) THEN
        -- Copy the b2b_subscription_price from the related service record
        UPDATE service_product sp
        SET b2b_subscription_price = s.b2b_subscription_price
        FROM service s
        WHERE sp.service_id = s.id
        AND sp.b2b_subscription_price IS NULL;
        
        RAISE NOTICE 'Successfully migrated b2b_subscription_price data from service to service_product';
    ELSE
        RAISE NOTICE 'Column b2b_subscription_price does not exist in service table - skipping data migration';
    END IF;
END $$;

-- Step 3: Drop the column from service table (if it still exists)
ALTER TABLE service 
DROP COLUMN IF EXISTS b2b_subscription_price;

-- =====================================================================================================================
-- END OF MIGRATION
-- =====================================================================================================================
