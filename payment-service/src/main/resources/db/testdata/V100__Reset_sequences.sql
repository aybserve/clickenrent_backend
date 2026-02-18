-- =====================================================================================================================
-- PAYMENT SERVICE - SEQUENCE RESET (Flyway Testdata V100)
-- =====================================================================================================================
-- Module: payment-service
-- Database: PostgreSQL
-- Description: Reset sequences to correct values after inserting data.
--              Safe version: checks if sequence and table exist before resetting.
-- 
-- Author: Vitaliy Shvetsov
-- =====================================================================================================================

-- =====================================================================================================================
-- DISABLE FORCE RLS TEMPORARILY
-- =====================================================================================================================
ALTER TABLE b2b_revenue_share_payouts ENABLE ROW LEVEL SECURITY;
ALTER TABLE financial_transactions ENABLE ROW LEVEL SECURITY;
ALTER TABLE location_bank_accounts ENABLE ROW LEVEL SECURITY;
ALTER TABLE refunds ENABLE ROW LEVEL SECURITY;

DO $$
DECLARE
    sequences TEXT[] := ARRAY[
        'payment_statuses_id_seq:payment_statuses',
        'payment_methods_id_seq:payment_methods',
        'currencies_id_seq:currencies',
        'service_providers_id_seq:service_providers',
        'user_payment_profiles_id_seq:user_payment_profiles',
        'user_payment_methods_id_seq:user_payment_methods',
        'financial_transactions_id_seq:financial_transactions',
        'rental_fin_transactions_id_seq:rental_fin_transactions',
        'b2b_sale_fin_transactions_id_seq:b2b_sale_fin_transactions',
        'b2b_subscription_fin_transactions_id_seq:b2b_subscription_fin_transactions',
        'b2b_revenue_share_payouts_id_seq:b2b_revenue_share_payouts',
        'b2b_revenue_share_payout_items_id_seq:b2b_revenue_share_payout_items',
        'payout_fin_transactions_id_seq:payout_fin_transactions',
        'location_bank_accounts_id_seq:location_bank_accounts',
        'refund_statuses_id_seq:refund_statuses',
        'refund_reasons_id_seq:refund_reasons',
        'refunds_id_seq:refunds'
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
-- RE-ENABLE FORCE RLS
-- =====================================================================================================================
ALTER TABLE b2b_revenue_share_payouts FORCE ROW LEVEL SECURITY;
ALTER TABLE financial_transactions FORCE ROW LEVEL SECURITY;
ALTER TABLE location_bank_accounts FORCE ROW LEVEL SECURITY;
ALTER TABLE refunds FORCE ROW LEVEL SECURITY;

-- =====================================================================================================================
-- END OF SEQUENCE RESET
-- =====================================================================================================================
