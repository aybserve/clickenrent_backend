-- Test Data for Auth Service
-- This script creates global roles, company types, and test companies

-- ========================================
-- 1. INSERT GLOBAL ROLES
-- ========================================
INSERT INTO global_role (id, name) VALUES
    (1, 'SuperAdmin'),
    (2, 'Admin'),
    (3, 'B2B'),
    (4, 'Customer')
ON CONFLICT (name) DO NOTHING;

-- ========================================
-- 2. INSERT COMPANY TYPES
-- ========================================
INSERT INTO company_type (id, name) VALUES
    (1, 'Hotel'),
    (2, 'B&B'),
    (3, 'Hostel'),
    (4, 'Apartment'),
    (5, 'Resort')
ON CONFLICT (name) DO NOTHING;

-- ========================================
-- 3. INSERT TEST COMPANIES
-- ========================================
INSERT INTO company (id, external_id, name, description, website, logo, company_type_id) VALUES
    (1, 'comp-001', 'Grand Hotel Plaza', 'Luxury 5-star hotel in city center', 'https://grandhotelplaza.com', 'https://example.com/logos/grand-hotel.png', 1),
    (2, 'comp-002', 'Cozy B&B', 'Family-run bed and breakfast', 'https://cozybnb.com', 'https://example.com/logos/cozy-bnb.png', 2),
    (3, 'comp-003', 'Beach Resort Paradise', 'Beautiful beachfront resort with all amenities', 'https://beachresort.com', 'https://example.com/logos/beach-resort.png', 5),
    (4, 'comp-004', 'City Hostel', 'Budget-friendly hostel for travelers', 'https://cityhostel.com', 'https://example.com/logos/city-hostel.png', 3),
    (5, 'comp-005', 'Mountain View Apartments', 'Modern apartments with stunning views', 'https://mountainview.com', 'https://example.com/logos/mountain-view.png', 4)
ON CONFLICT (external_id) DO NOTHING;

-- ========================================
-- 4. INSERT COMPANY ROLES
-- ========================================
INSERT INTO company_role (id, name) VALUES
    (1, 'Owner'),
    (2, 'Admin'),
    (3, 'Manager'),
    (4, 'Staff'),
    (5, 'Viewer')
ON CONFLICT (name) DO NOTHING;

-- ========================================
-- 5. ASSIGN ADMIN ROLE TO EXISTING USER (if any)
-- ========================================
-- Uncomment and update the user_id after creating a test user
-- INSERT INTO user_global_role (user_id, global_role_id) 
-- SELECT u.id, 2 FROM users u WHERE u.user_name = 'your_username'
-- ON CONFLICT DO NOTHING;

-- ========================================
-- 6. RESET SEQUENCES (if needed)
-- ========================================
SELECT setval('global_role_id_seq', (SELECT MAX(id) FROM global_role));
SELECT setval('company_type_id_seq', (SELECT MAX(id) FROM company_type));
SELECT setval('company_id_seq', (SELECT MAX(id) FROM company));
SELECT setval('company_role_id_seq', (SELECT MAX(id) FROM company_role));

-- ========================================
-- Verification Queries
-- ========================================
-- SELECT * FROM global_role;
-- SELECT * FROM company_type;
-- SELECT * FROM company;
-- SELECT * FROM company_role;


