#!/bin/bash

# Start local Docker services (Elasticsearch, Kibana, Redis)
# This script starts the required services for local development

echo "🚀 Starting local Docker services..."
echo ""

cd docker-services/

# Check if .env file exists
if [ ! -f .env ]; then
    echo "❌ Error: .env file not found in docker-services/"
    echo "   Please copy .env.example to .env and configure it"
    exit 1
fi

# Check if Docker is running
if ! docker info > /dev/null 2>&1; then
    echo "❌ Error: Docker is not running"
    echo "   Please start Docker Desktop and try again"
    exit 1
fi

# Pull latest images
echo "📥 Pulling latest Docker images..."
docker compose pull

# Start services
echo "🔄 Starting services..."
docker compose up -d

# Wait for Elasticsearch to be healthy
echo ""
echo "⏳ Waiting for Elasticsearch to be healthy (this may take 30-60 seconds)..."
echo "   You can check logs with: docker compose logs -f elasticsearch"
echo ""

sleep 5

# Check if services are running
echo "📊 Service Status:"
docker compose ps

echo ""
echo "✅ Services started successfully!"
echo ""
echo "📋 Connection Details:"
echo "   Elasticsearch: http://localhost:9200"
echo "   Kibana:        http://localhost:5601"
echo "   Redis:         localhost:6379"
echo ""
echo "   Username: elastic"
echo "   Password: (check .env file for ELASTIC_PASSWORD)"
echo ""
echo "💡 Useful commands:"
echo "   - View logs:        cd docker-services && docker compose logs -f"
echo "   - Stop services:    cd docker-services && docker compose down"
echo "   - Restart services: cd docker-services && docker compose restart"
echo ""
