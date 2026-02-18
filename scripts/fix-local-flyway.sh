#!/bin/bash
# =====================================================================================================================
# FIX LOCAL FLYWAY - Clean up test data migrations for local development
# =====================================================================================================================
# Purpose: Remove V100/V101 entries from local databases and grant BYPASSRLS
# Usage: bash scripts/fix-local-flyway.sh
# =====================================================================================================================

set -e

echo "=========================================="
echo "Fix Local Flyway Test Data Migrations"
echo "=========================================="
echo ""

# Local databases
databases=(
    "clickenrent-auth"
    "clickenrent-rental"
    "clickenrent-payment"
    "clickenrent-support"
    "clickenrent-notification"
)

# First, grant BYPASSRLS to postgres user
echo "Granting BYPASSRLS to postgres user..."
psql -U postgres -d postgres -c "ALTER USER postgres BYPASSRLS;" 2>/dev/null || \
sudo -u postgres psql -d postgres -c "ALTER USER postgres BYPASSRLS;"

echo "✓ BYPASSRLS granted"
echo ""

# Clean up each database
for db in "${databases[@]}"; do
    echo "----------------------------------------"
    echo "Database: $db"
    echo "----------------------------------------"
    
    # Check if database exists
    if psql -U postgres -lqt 2>/dev/null | cut -d \| -f 1 | grep -qw "$db" || \
       sudo -u postgres psql -lqt 2>/dev/null | cut -d \| -f 1 | grep -qw "$db"; then
        echo "✓ Database found"
        
        # Show current test data migrations
        echo "Current test data migrations:"
        psql -U postgres -d "$db" -c "
            SELECT version, description, success 
            FROM flyway_schema_history 
            WHERE version IN ('100', '101')
            ORDER BY version;
        " 2>/dev/null || sudo -u postgres psql -d "$db" -c "
            SELECT version, description, success 
            FROM flyway_schema_history 
            WHERE version IN ('100', '101')
            ORDER BY version;
        " 2>/dev/null || echo "  (no migrations found)"
        
        # Delete test data migrations
        echo "Deleting V100/V101 migration entries..."
        psql -U postgres -d "$db" -c "
            DELETE FROM flyway_schema_history 
            WHERE version IN ('100', '101');
        " 2>/dev/null || sudo -u postgres psql -d "$db" -c "
            DELETE FROM flyway_schema_history 
            WHERE version IN ('100', '101');
        "
        
        echo "✓ $db cleaned"
    else
        echo "⚠ Database not found, skipping"
    fi
    echo ""
done

echo "=========================================="
echo "Local Flyway repair complete!"
echo "=========================================="
echo ""
echo "You can now run your services from IntelliJ."
echo "Flyway will apply the updated V100/V101 migrations."
echo ""
