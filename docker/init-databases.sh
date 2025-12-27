#!/bin/bash
set -e

# This script creates multiple databases in PostgreSQL
# It reads the POSTGRES_MULTIPLE_DATABASES environment variable
# and creates each database listed (comma-separated)

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-EOSQL
    CREATE DATABASE "clickenrent-auth";
    CREATE DATABASE "clickenrent-rental";
    CREATE DATABASE "clickenrent-payment";
    CREATE DATABASE "clickenrent-notification";
    CREATE DATABASE "clickenrent-support";
EOSQL

echo "Multiple databases created successfully!"

