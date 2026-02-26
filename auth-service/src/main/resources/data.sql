-- =====================================================================================================================
-- AUTH SERVICE - DATA INITIALIZATION (Fallback mode)
-- =====================================================================================================================
-- Module: auth-service
-- Database: PostgreSQL
-- Description: INSERT-only data initialization for when Flyway is disabled.
--              Activate by setting: FLYWAY_MIGRATE=false and SQL_INIT_MODE=always
--              Flyway is the source of truth for schema and data migrations.
--
-- Note: This file contains ONLY INSERT statements. No DDL (DROP/CREATE).
--       Schema is managed by Hibernate DDL (staging) or Flyway migrations (production).
--
-- Author: Vitaliy Shvetsov
-- =====================================================================================================================

-- =====================================================================================================================
-- SECTION 1: LOOKUP TABLE DATA (required for application to function)
-- =====================================================================================================================

-- Languages
INSERT INTO language (id, external_id, name) VALUES
(1, 'lang-ext-00001', 'English'),
(2, 'lang-ext-00002', 'German'),
(3, 'lang-ext-00003', 'French'),
(4, 'lang-ext-00004', 'Spanish'),
(5, 'lang-ext-00005', 'Italian')
ON CONFLICT (id) DO NOTHING;

-- Global Roles
INSERT INTO global_role (id, external_id, name) VALUES
(1, 'role-ext-00001', 'SUPERADMIN'),
(2, 'role-ext-00002', 'ADMIN'),
(3, 'role-ext-00003', 'B2B'),
(4, 'role-ext-00004', 'CUSTOMER'),
(5, 'role-ext-00005', 'DEV'),
(6, 'role-ext-00006', 'SYSTEM')
ON CONFLICT (id) DO NOTHING;

-- Company Types
INSERT INTO company_type (id, external_id, name) VALUES
(1, 'ctype-ext-00001', 'Hotel'),
(2, 'ctype-ext-00002', 'B&B'),
(3, 'ctype-ext-00003', 'Camping'),
(4, 'ctype-ext-00004', 'Vacation Rental'),
(5, 'ctype-ext-00005', 'Hostel')
ON CONFLICT (id) DO NOTHING;

-- Company Roles
INSERT INTO company_role (id, external_id, name) VALUES
(1, 'crole-ext-00001', 'Owner'),
(2, 'crole-ext-00002', 'Admin'),
(3, 'crole-ext-00003', 'Staff')
ON CONFLICT (id) DO NOTHING;

-- Countries
INSERT INTO country (id, external_id, name) VALUES
(1, 'country-ext-00001', 'Germany'),
(2, 'country-ext-00002', 'Austria'),
(3, 'country-ext-00003', 'Switzerland'),
(4, 'country-ext-00004', 'France'),
(5, 'country-ext-00005', 'Italy'),
(6, 'country-ext-00006', 'USA')
ON CONFLICT (id) DO NOTHING;

-- Reset sequences for lookup data
SELECT setval('language_id_seq', (SELECT COALESCE(MAX(id), 1) FROM language));
SELECT setval('global_role_id_seq', (SELECT COALESCE(MAX(id), 1) FROM global_role));
SELECT setval('company_type_id_seq', (SELECT COALESCE(MAX(id), 1) FROM company_type));
SELECT setval('company_role_id_seq', (SELECT COALESCE(MAX(id), 1) FROM company_role));
SELECT setval('country_id_seq', (SELECT COALESCE(MAX(id), 1) FROM country));

-- =====================================================================================================================
-- END OF DATA INITIALIZATION
-- =====================================================================================================================
