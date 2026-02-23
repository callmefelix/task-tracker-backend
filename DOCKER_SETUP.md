# Docker Setup Guide

This guide explains how to run the Personal Task Tracker application using Docker and Docker Compose.

## Prerequisites

- Docker Engine 20.10+ installed
- Docker Compose 1.29+ installed
- At least 2GB of free RAM
- Ports 80, 3306, and 8080 available

## Quick Start

### 1. Clone the repositories (if not already done)

```bash
# Backend
git clone <backend-repo-url>
cd task-tracker-backend

# Frontend (in a separate directory)
git clone <frontend-repo-url>
```

**Note:** Ensure both repositories are in the same parent directory:
```
parent-directory/
├── task-tracker-backend/
└── task-tracker-frontend/
```

### 2. Start all services

From the `task-tracker-backend` directory:

```bash
docker-compose up -d
```

This will:
- Pull/build required Docker images
- Start MySQL database
- Start the Kotlin backend
- Start the React frontend with Nginx

### 3. Wait for services to be ready

The first startup may take 2-3 minutes. Check the status:

```bash
docker-compose ps
```

All services should show as "healthy" or "running".

### 4. Access the application

- **Frontend:** http://localhost
- **Backend API:** http://localhost:8080/api
- **Health Check:** http://localhost:8080/actuator/health

### 5. Default credentials

The application comes with pre-configured users:

- **Username:** `user` / **Password:** `password` (Regular user)
- **Username:** `admin` / **Password:** `admin` (Admin user)

You can also register new users via the registration page.

## Managing the Application

### View logs

```bash
# All services
docker-compose logs -f

# Specific service
docker-compose logs -f backend
docker-compose logs -f frontend
docker-compose logs -f mysql
```

### Stop the application

```bash
docker-compose down
```

### Stop and remove volumes (deletes all data)

```bash
docker-compose down -v
```

### Rebuild after code changes

```bash
# Rebuild and restart
docker-compose up -d --build

# Rebuild specific service
docker-compose up -d --build backend
```

### Restart a service

```bash
docker-compose restart backend
docker-compose restart frontend
```

## Architecture

```
┌─────────────────┐
│   Frontend      │
│   (React)       │
│   Port: 80      │
└────────┬────────┘
         │
         │ HTTP
         │
┌────────▼────────┐
│   Backend       │
│   (Kotlin)      │
│   Port: 8080    │
└────────┬────────┘
         │
         │ JDBC
         │
┌────────▼────────┐
│   MySQL         │
│   Database      │
│   Port: 3306    │
└─────────────────┘
```

## Services Overview

### MySQL Database
- **Image:** mysql:8.0
- **Port:** 3306
- **Database:** tasktracker_db
- **Data persistence:** Via Docker volume `mysql_data`

### Backend (Kotlin Spring Boot)
- **Built from:** task-tracker-backend/Dockerfile
- **Port:** 8080
- **Health check:** /actuator/health
- **Depends on:** MySQL

### Frontend (React + Nginx)
- **Built from:** task-tracker-frontend/Dockerfile
- **Port:** 80
- **Serves:** Static React build
- **Depends on:** Backend

## Configuration

### Environment Variables

Copy `.env.example` to `.env` and customize if needed:

```bash
cp .env.example .env
```

Key variables:
- `MYSQL_ROOT_PASSWORD`: MySQL root password
- `MYSQL_DATABASE`: Database name
- `MYSQL_USER`: Database user
- `MYSQL_PASSWORD`: Database password
- `VITE_API_BASE_URL`: Frontend API endpoint

### Custom Ports

Edit `docker-compose.yml` to change ports:

```yaml
ports:
  - "8081:8080"  # Change host port (left side)
```

## Troubleshooting

### Services won't start

1. Check if ports are already in use:
```bash
# Windows
netstat -ano | findstr :80
netstat -ano | findstr :8080
netstat -ano | findstr :3306

# Linux/Mac
lsof -i :80
lsof -i :8080
lsof -i :3306
```

2. View service logs:
```bash
docker-compose logs backend
```

### Backend can't connect to MySQL

Wait for MySQL to be fully ready:
```bash
docker-compose logs mysql | grep "ready for connections"
```

Then restart the backend:
```bash
docker-compose restart backend
```

### Frontend shows connection error

1. Check backend is running:
```bash
curl http://localhost:8080/actuator/health
```

2. Verify API URL in frontend build
3. Check browser console for CORS errors

### Database connection errors

1. Verify MySQL is healthy:
```bash
docker-compose ps mysql
```

2. Check MySQL logs:
```bash
docker-compose logs mysql
```

3. Test connection manually:
```bash
docker-compose exec mysql mysql -utaskuser -ptaskpass tasktracker_db
```

### Clean slate restart

Remove everything and start fresh:
```bash
docker-compose down -v
docker system prune -f
docker-compose up -d --build
```

## Development vs Production

This docker-compose setup is suitable for:
- ✅ Local development
- ✅ Testing
- ✅ Demo purposes

For production, consider:
- Using Docker secrets for passwords
- Separate docker-compose files for dev/prod
- External MySQL instance (managed service)
- HTTPS/SSL certificates
- Container orchestration (Kubernetes)
- Horizontal scaling
- Monitoring and logging solutions

## Data Persistence

Data is persisted in the `mysql_data` Docker volume. To backup:

```bash
# Backup
docker-compose exec mysql mysqldump -utaskuser -ptaskpass tasktracker_db > backup.sql

# Restore
docker-compose exec -T mysql mysql -utaskuser -ptaskpass tasktracker_db < backup.sql
```

## Health Checks

All services have health checks:

```bash
# Check backend health
curl http://localhost:8080/actuator/health

# Check frontend health
curl http://localhost/health

# Check MySQL health
docker-compose exec mysql mysqladmin ping -h localhost -u root -prootpassword
```

## Resource Usage

Typical resource usage:
- **MySQL:** ~200MB RAM
- **Backend:** ~300-500MB RAM
- **Frontend:** ~10MB RAM
- **Total:** ~600-800MB RAM

## Support

For issues or questions:
1. Check service logs: `docker-compose logs <service>`
2. Verify all prerequisites are met
3. Ensure correct directory structure
4. Check firewall/antivirus settings
