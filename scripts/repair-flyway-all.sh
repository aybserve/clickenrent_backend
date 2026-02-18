#!/bin/bash
# =====================================================================================================================
# REPAIR FLYWAY FOR ALL DATABASES
# =====================================================================================================================
# Purpose: Remove V100/V101 test data migrations from all service databases
# Usage: sudo bash repair-flyway-all.sh
# =====================================================================================================================

set -e

echo "=========================================="
echo "Repairing Flyway Test Data Migrations"
echo "=========================================="
echo ""

databases=(
    "clickenrent-auth"
    "clickenrent-rental"
    "clickenrent-payment"
    "clickenrent-support"
    "clickenrent-notification"
)

for db in "${databases[@]}"; do
    echo "----------------------------------------"
    echo "Database: $db"
    echo "----------------------------------------"
    
    # Check if database exists
    if sudo -u postgres psql -lqt | cut -d \| -f 1 | grep -qw "$db"; then
        echo "✓ Database found"
        
        # Show current test data migrations
        echo "Current test data migrations:"
        sudo -u postgres psql -d "$db" -c "
            SELECT version, description, success, checksum 
            FROM flyway_schema_history 
            WHERE version IN ('100', '101')
            ORDER BY version;
        " 2>/dev/null || echo "  (none found)"
        
        # Delete test data migrations
        echo "Deleting test data migration entries..."
        sudo -u postgres psql -d "$db" -c "
            DELETE FROM flyway_schema_history 
            WHERE version IN ('100', '101') 
            AND (description LIKE '%sample%' OR description LIKE '%Reset%');
        " 2>/dev/null
        
        # Show remaining migrations
        echo "Latest migrations after cleanup:"
        sudo -u postgres psql -d "$db" -c "
            SELECT version, description, success 
            FROM flyway_schema_history 
            ORDER BY installed_rank DESC 
            LIMIT 5;
        " 2>/dev/null
        
        echo "✓ $db repaired"
    else
        echo "⚠ Database not found, skipping"
    fi
    echo ""
done

echo "=========================================="
echo "Flyway repair complete!"
echo "=========================================="
echo ""
echo "Next steps:"
echo "1. Restart the failing pods:"
echo "   kubectl rollout restart deployment/auth-service -n clickenrent"
echo "   kubectl rollout restart deployment/rental-service -n clickenrent"
echo "   kubectl rollout restart deployment/payment-service -n clickenrent"
echo "   kubectl rollout restart deployment/support-service -n clickenrent"
echo "   kubectl rollout restart deployment/notification-service -n clickenrent"
echo ""
echo "2. Or wait for the GitHub Actions workflow to redeploy with git push"
echo ""
