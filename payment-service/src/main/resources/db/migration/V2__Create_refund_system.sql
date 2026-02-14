-- =====================================================================================================================
-- PAYMENT SERVICE - REFUND SYSTEM (Flyway Migration V2)
-- =====================================================================================================================
-- Module: payment-service
-- Database: PostgreSQL
-- Description: Insert refund statuses and reasons, create RLS policy for refunds table.
--              Uses DROP POLICY IF EXISTS + CREATE POLICY for idempotency.
-- 
-- Author: Vitaliy Shvetsov
-- =====================================================================================================================

-- Refund Status
INSERT INTO refund_statuses (id, external_id, code, name, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, '650e8400-e29b-41d4-a716-446655440001', 'PENDING', 'Pending', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(2, '650e8400-e29b-41d4-a716-446655440002', 'PROCESSING', 'Processing', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(3, '650e8400-e29b-41d4-a716-446655440003', 'SUCCEEDED', 'Succeeded', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(4, '650e8400-e29b-41d4-a716-446655440004', 'FAILED', 'Failed', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(5, '650e8400-e29b-41d4-a716-446655440005', 'CANCELED', 'Canceled', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (code) DO NOTHING;

-- Refund Reason
INSERT INTO refund_reasons (id, external_id, code, name, date_created, last_date_modified, created_by, last_modified_by, is_deleted) VALUES
(1, '650e8400-e29b-41d4-a716-446655440011', 'CUSTOMER_REQUEST', 'Customer Request', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(2, '650e8400-e29b-41d4-a716-446655440012', 'DUPLICATE_CHARGE', 'Duplicate Charge', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(3, '650e8400-e29b-41d4-a716-446655440013', 'FRAUDULENT', 'Fraudulent Transaction', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(4, '650e8400-e29b-41d4-a716-446655440014', 'PRODUCT_NOT_AVAILABLE', 'Product/Service Not Available', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(5, '650e8400-e29b-41d4-a716-446655440015', 'SERVICE_UNSATISFACTORY', 'Service Unsatisfactory', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false),
(6, '650e8400-e29b-41d4-a716-446655440016', 'OTHER', 'Other', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false)
ON CONFLICT (code) DO NOTHING;

-- RLS for refunds table
ALTER TABLE refunds ENABLE ROW LEVEL SECURITY;
ALTER TABLE refunds FORCE ROW LEVEL SECURITY;

DROP POLICY IF EXISTS refunds_tenant_isolation ON refunds;
CREATE POLICY refunds_tenant_isolation ON refunds
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
        OR
        company_external_id IS NULL
    );

CREATE INDEX IF NOT EXISTS idx_refunds_company_rls ON refunds(company_external_id) WHERE is_deleted = false;

-- =====================================================================================================================
-- END OF REFUND SYSTEM
-- =====================================================================================================================
