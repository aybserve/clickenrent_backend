#!/bin/bash
# =====================================================================================================================
# GRANT BYPASSRLS PERMISSION - Execution Script
# =====================================================================================================================
# Purpose: Grant BYPASSRLS to postgres user to allow Flyway migrations to bypass RLS policies
# Usage: 
#   For remote database:  bash scripts/grant-bypassrls.sh
#   For local database:   sudo -u postgres psql -f scripts/grant-bypassrls.sql
# =====================================================================================================================

set -e

echo "=========================================="
echo "Grant BYPASSRLS to Flyway User"
echo "=========================================="
echo ""

# Database connection details (from k8s configmap)
DB_HOST="cnr.aybserve.com"
DB_PORT="5432"
DB_USER="postgres"

# Check if password is provided
if [ -z "$PGPASSWORD" ]; then
    echo "⚠️  Database password not set in environment"
    echo ""
    echo "Please set the PGPASSWORD environment variable:"
    echo "  export PGPASSWORD='your-postgres-password'"
    echo ""
    echo "Or get it from Kubernetes:"
    echo "  export PGPASSWORD=\$(kubectl get secret app-secrets -n clickenrent -o jsonpath='{.data.db-password}' | base64 -d)"
    echo ""
    exit 1
fi

echo "Connecting to $DB_HOST:$DB_PORT as $DB_USER..."
echo ""

# Check current status
echo "Current BYPASSRLS status:"
psql -h "$DB_HOST" -p "$DB_PORT" -U "$DB_USER" -d postgres -c "
    SELECT rolname, rolbypassrls 
    FROM pg_roles 
    WHERE rolname = '$DB_USER';
"

echo ""
echo "Granting BYPASSRLS permission..."

# Grant BYPASSRLS
psql -h "$DB_HOST" -p "$DB_PORT" -U "$DB_USER" -d postgres -c "ALTER USER $DB_USER BYPASSRLS;"

echo ""
echo "✓ BYPASSRLS granted successfully!"
echo ""

# Verify
echo "Verifying the change:"
psql -h "$DB_HOST" -p "$DB_PORT" -U "$DB_USER" -d postgres -c "
    SELECT rolname, rolbypassrls 
    FROM pg_roles 
    WHERE rolname = '$DB_USER';
"

echo ""
echo "=========================================="
echo "Grant Complete!"
echo "=========================================="
echo ""
echo "Next steps:"
echo "1. Restart the pods to trigger Flyway migrations:"
echo "   kubectl rollout restart deployment/auth-service -n clickenrent"
echo "   kubectl rollout restart deployment/rental-service -n clickenrent"
echo "   kubectl rollout restart deployment/payment-service -n clickenrent"
echo "   kubectl rollout restart deployment/support-service -n clickenrent"
echo "   kubectl rollout restart deployment/notification-service -n clickenrent"
echo ""
echo "2. Monitor the deployment:"
echo "   kubectl get pods -n clickenrent -w"
echo ""
echo "3. Check auth-service logs for successful Flyway migration:"
echo "   kubectl logs -n clickenrent -l app=auth-service -f | grep -i flyway"
echo ""
