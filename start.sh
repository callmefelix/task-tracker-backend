#!/bin/bash

# Personal Task Tracker - Quick Start Script

set -e

echo "=================================="
echo "Personal Task Tracker - Quick Start"
echo "=================================="
echo ""

# Check if Docker is installed
if ! command -v docker &> /dev/null; then
    echo "❌ Docker is not installed. Please install Docker first."
    exit 1
fi

# Check if Docker Compose is installed
if ! command -v docker-compose &> /dev/null; then
    echo "❌ Docker Compose is not installed. Please install Docker Compose first."
    exit 1
fi

# Check if frontend directory exists
if [ ! -d "../task-tracker-frontend" ]; then
    echo "❌ Frontend directory not found."
    echo "Please ensure task-tracker-frontend is in the same parent directory."
    exit 1
fi

echo "✅ Prerequisites check passed"
echo ""

# Check if .env exists, if not create from .env.example
if [ ! -f ".env" ] && [ -f ".env.example" ]; then
    echo "📝 Creating .env file from .env.example..."
    cp .env.example .env
    echo "✅ .env file created"
    echo ""
fi

# Start services
echo "🚀 Starting services..."
echo ""
docker-compose up -d

echo ""
echo "⏳ Waiting for services to be ready..."
echo "   This may take 1-2 minutes on first run..."
echo ""

# Wait for services to be healthy
max_attempts=60
attempt=0

while [ $attempt -lt $max_attempts ]; do
    if docker-compose ps | grep -q "healthy"; then
        backend_health=$(docker-compose ps backend | grep -c "healthy" || echo "0")
        mysql_health=$(docker-compose ps mysql | grep -c "healthy" || echo "0")

        if [ "$backend_health" -eq "1" ] && [ "$mysql_health" -eq "1" ]; then
            echo ""
            echo "✅ All services are healthy!"
            break
        fi
    fi

    attempt=$((attempt + 1))
    echo -n "."
    sleep 2
done

if [ $attempt -eq $max_attempts ]; then
    echo ""
    echo "⚠️  Services took longer than expected to start."
    echo "Check logs with: docker-compose logs"
    echo ""
fi

# Display service status
echo ""
echo "=================================="
echo "Service Status:"
echo "=================================="
docker-compose ps
echo ""

# Display access information
echo "=================================="
echo "Access Information:"
echo "=================================="
echo "📱 Frontend:     http://localhost"
echo "🔧 Backend API:  http://localhost:8080/api"
echo "❤️  Health Check: http://localhost:8080/actuator/health"
echo ""
echo "Default Credentials:"
echo "  Username: user"
echo "  Password: password"
echo ""
echo "=================================="
echo "Useful Commands:"
echo "=================================="
echo "  View logs:           docker-compose logs -f"
echo "  Stop services:       docker-compose down"
echo "  Restart services:    docker-compose restart"
echo "  Rebuild:             docker-compose up -d --build"
echo "=================================="
echo ""
echo "✨ Application is ready! Open http://localhost in your browser."
