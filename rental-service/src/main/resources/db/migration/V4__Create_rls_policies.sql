-- =====================================================================================================================
-- RENTAL SERVICE - ROW LEVEL SECURITY POLICIES (Flyway Migration V4)
-- =====================================================================================================================
-- Module: rental-service
-- Database: PostgreSQL
-- Description: Create RLS policies for multi-tenant isolation on all tenant-scoped tables.
--              Uses DROP POLICY IF EXISTS + CREATE POLICY for idempotency.
-- 
-- Author: Vitaliy Shvetsov
-- =====================================================================================================================

-- Enable RLS on tenant-scoped tables
ALTER TABLE rental ENABLE ROW LEVEL SECURITY;
ALTER TABLE location ENABLE ROW LEVEL SECURITY;
ALTER TABLE b2b_sale ENABLE ROW LEVEL SECURITY;
ALTER TABLE b2b_subscription ENABLE ROW LEVEL SECURITY;
ALTER TABLE b2b_sale_order ENABLE ROW LEVEL SECURITY;
ALTER TABLE bike_brand ENABLE ROW LEVEL SECURITY;
ALTER TABLE charging_station_brand ENABLE ROW LEVEL SECURITY;
ALTER TABLE part_brand ENABLE ROW LEVEL SECURITY;

-- Force RLS even for table owner (important for security)
ALTER TABLE rental FORCE ROW LEVEL SECURITY;
ALTER TABLE location FORCE ROW LEVEL SECURITY;
ALTER TABLE b2b_sale FORCE ROW LEVEL SECURITY;
ALTER TABLE b2b_subscription FORCE ROW LEVEL SECURITY;
ALTER TABLE b2b_sale_order FORCE ROW LEVEL SECURITY;
ALTER TABLE bike_brand FORCE ROW LEVEL SECURITY;
ALTER TABLE charging_station_brand FORCE ROW LEVEL SECURITY;
ALTER TABLE part_brand FORCE ROW LEVEL SECURITY;

-- RLS POLICY: rental table
DROP POLICY IF EXISTS rental_tenant_isolation ON rental;
CREATE POLICY rental_tenant_isolation ON rental
    FOR ALL
    USING (
        COALESCE(current_setting('app.is_superadmin', true)::boolean, false) = true
        OR
        company_external_id = ANY(
            string_to_array(
                COALESCE(current_setting('app.company_external_ids', true), ''),
                ','
            )
        )
    );

-- RLS POLICY: location table
DROP POLICY IF EXISTS location_tenant_isolation ON location;
CREATE POLICY location_tenant_isolation ON location
    FOR ALL
    USING (
        COALESCE(current_setting('app.is_superadmin', true)::boolean, false) = true
        OR
        company_external_id = ANY(
            string_to_array(
                COALESCE(current_setting('app.company_external_ids', true), ''),
                ','
            )
        )
    );

-- RLS POLICY: b2b_sale table (seller OR buyer)
DROP POLICY IF EXISTS b2b_sale_tenant_isolation ON b2b_sale;
CREATE POLICY b2b_sale_tenant_isolation ON b2b_sale
    FOR ALL
    USING (
        COALESCE(current_setting('app.is_superadmin', true)::boolean, false) = true
        OR
        seller_company_external_id = ANY(
            string_to_array(
                COALESCE(current_setting('app.company_external_ids', true), ''),
                ','
            )
        )
        OR
        buyer_company_external_id = ANY(
            string_to_array(
                COALESCE(current_setting('app.company_external_ids', true), ''),
                ','
            )
        )
    );

-- RLS POLICY: b2b_subscription table (via location)
DROP POLICY IF EXISTS b2b_subscription_tenant_isolation ON b2b_subscription;
CREATE POLICY b2b_subscription_tenant_isolation ON b2b_subscription
    FOR ALL
    USING (
        COALESCE(current_setting('app.is_superadmin', true)::boolean, false) = true
        OR
        location_id IN (
            SELECT id FROM location 
            WHERE company_external_id = ANY(
                string_to_array(
                    COALESCE(current_setting('app.company_external_ids', true), ''),
                    ','
                )
            )
        )
    );

-- RLS POLICY: b2b_sale_order table (seller OR buyer)
DROP POLICY IF EXISTS b2b_sale_order_tenant_isolation ON b2b_sale_order;
CREATE POLICY b2b_sale_order_tenant_isolation ON b2b_sale_order
    FOR ALL
    USING (
        COALESCE(current_setting('app.is_superadmin', true)::boolean, false) = true
        OR
        seller_company_external_id = ANY(
            string_to_array(
                COALESCE(current_setting('app.company_external_ids', true), ''),
                ','
            )
        )
        OR
        buyer_company_external_id = ANY(
            string_to_array(
                COALESCE(current_setting('app.company_external_ids', true), ''),
                ','
            )
        )
    );

-- RLS POLICY: bike_brand table
DROP POLICY IF EXISTS bike_brand_tenant_isolation ON bike_brand;
CREATE POLICY bike_brand_tenant_isolation ON bike_brand
    FOR ALL
    USING (
        COALESCE(current_setting('app.is_superadmin', true)::boolean, false) = true
        OR
        company_external_id = ANY(
            string_to_array(
                COALESCE(current_setting('app.company_external_ids', true), ''),
                ','
            )
        )
    );

-- RLS POLICY: charging_station_brand table
DROP POLICY IF EXISTS charging_station_brand_tenant_isolation ON charging_station_brand;
CREATE POLICY charging_station_brand_tenant_isolation ON charging_station_brand
    FOR ALL
    USING (
        COALESCE(current_setting('app.is_superadmin', true)::boolean, false) = true
        OR
        company_external_id = ANY(
            string_to_array(
                COALESCE(current_setting('app.company_external_ids', true), ''),
                ','
            )
        )
    );

-- RLS POLICY: part_brand table
DROP POLICY IF EXISTS part_brand_tenant_isolation ON part_brand;
CREATE POLICY part_brand_tenant_isolation ON part_brand
    FOR ALL
    USING (
        COALESCE(current_setting('app.is_superadmin', true)::boolean, false) = true
        OR
        company_external_id = ANY(
            string_to_array(
                COALESCE(current_setting('app.company_external_ids', true), ''),
                ','
            )
        )
    );

-- Performance indexes for RLS
CREATE INDEX IF NOT EXISTS idx_rental_company_rls ON rental(company_external_id) WHERE is_deleted = false;
CREATE INDEX IF NOT EXISTS idx_location_company_rls ON location(company_external_id) WHERE is_deleted = false;
CREATE INDEX IF NOT EXISTS idx_b2b_sale_seller_rls ON b2b_sale(seller_company_external_id) WHERE is_deleted = false;
CREATE INDEX IF NOT EXISTS idx_b2b_sale_buyer_rls ON b2b_sale(buyer_company_external_id) WHERE is_deleted = false;
CREATE INDEX IF NOT EXISTS idx_b2b_sale_order_seller_rls ON b2b_sale_order(seller_company_external_id) WHERE is_deleted = false;
CREATE INDEX IF NOT EXISTS idx_b2b_sale_order_buyer_rls ON b2b_sale_order(buyer_company_external_id) WHERE is_deleted = false;
CREATE INDEX IF NOT EXISTS idx_bike_brand_company_rls ON bike_brand(company_external_id) WHERE is_deleted = false;
CREATE INDEX IF NOT EXISTS idx_charging_station_brand_company_rls ON charging_station_brand(company_external_id) WHERE is_deleted = false;
CREATE INDEX IF NOT EXISTS idx_part_brand_company_rls ON part_brand(company_external_id) WHERE is_deleted = false;

-- =====================================================================================================================
-- END OF RLS POLICIES
-- =====================================================================================================================
