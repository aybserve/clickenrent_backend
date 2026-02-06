#!/bin/bash

# Test Elasticsearch connection with authentication
# This script helps verify Elasticsearch is running and credentials work

echo "🔍 Testing Elasticsearch Connection..."
echo ""

# Load password from .env file
if [ -f .env ]; then
    export $(cat .env | grep ELASTIC_PASSWORD | xargs)
    echo "✅ Loaded password from .env file"
else
    echo "❌ Error: .env file not found"
    exit 1
fi

echo ""
echo "Testing connection to http://localhost:9200"
echo ""

# Test without authentication (should fail)
echo "1️⃣ Test WITHOUT authentication (should fail):"
curl -s http://localhost:9200 | head -5
echo ""
echo ""

# Test with authentication (should succeed)
echo "2️⃣ Test WITH authentication (should succeed):"
curl -s -u elastic:${ELASTIC_PASSWORD} http://localhost:9200
echo ""
echo ""

# Check cluster health
echo "3️⃣ Cluster Health:"
curl -s -u elastic:${ELASTIC_PASSWORD} http://localhost:9200/_cluster/health | python3 -m json.tool 2>/dev/null || curl -s -u elastic:${ELASTIC_PASSWORD} http://localhost:9200/_cluster/health
echo ""
echo ""

# List indices
echo "4️⃣ Existing Indices:"
curl -s -u elastic:${ELASTIC_PASSWORD} http://localhost:9200/_cat/indices?v
echo ""

echo "✅ Test complete!"
