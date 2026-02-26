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

# Find psql binary (works with Postgres.app and Homebrew)
PSQL=""
if command -v psql &> /dev/null; then
    PSQL="psql"
elif [ -f "/Applications/Postgres.app/Contents/Versions/17/bin/psql" ]; then
    PSQL="/Applications/Postgres.app/Contents/Versions/17/bin/psql"
elif [ -f "/Applications/Postgres.app/Contents/Versions/16/bin/psql" ]; then
    PSQL="/Applications/Postgres.app/Contents/Versions/16/bin/psql"
else
    echo "❌ psql not found. Please install PostgreSQL or Postgres.app"
    exit 1
fi

echo "Using psql: $PSQL"
echo ""

# Get current user (for Postgres.app, the database user is your macOS user)
DB_USER="${USER}"

echo "Database user: $DB_USER"
echo ""

# Local databases
databases=(
    "clickenrent-auth"
    "clickenrent-rental"
    "clickenrent-payment"
    "clickenrent-support"
    "clickenrent-notification"
)

# First, grant BYPASSRLS to current user
echo "Granting BYPASSRLS to $DB_USER..."
$PSQL -d postgres -c "ALTER USER $DB_USER BYPASSRLS;" 2>/dev/null || echo "⚠ Could not grant BYPASSRLS (user might not exist or already has it)"

echo ""

# Clean up each database
for db in "${databases[@]}"; do
    echo "----------------------------------------"
    echo "Database: $db"
    echo "----------------------------------------"
    
    # Check if database exists
    if $PSQL -d postgres -lqt 2>/dev/null | cut -d \| -f 1 | grep -qw "$db"; then
        echo "✓ Database found"
        
        # Show current test data migrations
        echo "Current test data migrations:"
        $PSQL -d "$db" -c "
            SELECT version, description, success 
            FROM flyway_schema_history 
            WHERE version IN ('100', '101')
            ORDER BY version;
        " 2>/dev/null || echo "  (no migrations found or table doesn't exist)"
        
        # Delete test data migrations
        echo "Deleting V100/V101 migration entries..."
        $PSQL -d "$db" -c "
            DELETE FROM flyway_schema_history 
            WHERE version IN ('100', '101');
        " 2>/dev/null || echo "  (table doesn't exist or already clean)"
        
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
