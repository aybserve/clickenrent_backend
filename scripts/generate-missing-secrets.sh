#!/bin/bash

# =====================================================
# Generate Missing GitHub Secrets
# =====================================================
# This script generates placeholder values for missing secrets
# Copy the output and add to GitHub Secrets

echo "=========================================="
echo "MISSING GITHUB SECRETS - PLACEHOLDER VALUES"
echo "=========================================="
echo ""
echo "Add these to: https://github.com/YOUR_USERNAME/YOUR_REPO/settings/secrets/actions"
echo ""

# Generate LOCK_ENCRYPTION_KEY (32 characters for AES-256)
LOCK_KEY=$(openssl rand -hex 16)
echo "LOCK_ENCRYPTION_KEY:"
echo "$LOCK_KEY"
echo ""

# Generate MULTISAFEPAY_WEBHOOK_SECRET
WEBHOOK_SECRET=$(openssl rand -base64 32)
echo "MULTISAFEPAY_WEBHOOK_SECRET:"
echo "$WEBHOOK_SECRET"
echo ""

# Placeholder OAuth values (won't work for real login, but services will start)
echo "GOOGLE_CLIENT_ID:"
echo "placeholder-google-client-id.apps.googleusercontent.com"
echo ""

echo "GOOGLE_CLIENT_SECRET:"
echo "placeholder-google-client-secret"
echo ""

echo "APPLE_TEAM_ID:"
echo "PLACEHOLDER123"
echo ""

echo "APPLE_CLIENT_ID:"
echo "com.clickenrent.placeholder"
echo ""

echo "APPLE_KEY_ID:"
echo "PLACEHOLDER"
echo ""

echo "APPLE_PRIVATE_KEY:"
echo "-----BEGIN PRIVATE KEY-----
MIGHAgEAMBMGByqGSM49AgEGCCqGSM49AwEHBG0wawIBAQQgPlaceholder12345678
placeholder12345678placeholder12345678==
-----END PRIVATE KEY-----"
echo ""

echo "AZURE_STORAGE_CONNECTION_STRING:"
echo "DefaultEndpointsProtocol=https;AccountName=placeholder;AccountKey=cGxhY2Vob2xkZXI=;EndpointSuffix=core.windows.net"
echo ""

echo "=========================================="
echo "NOTE: These are placeholder values."
echo "Services will start but OAuth login won't work."
echo "Update with real values when you have them."
echo "=========================================="
