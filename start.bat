@echo off
REM Personal Task Tracker - Quick Start Script (Windows)

echo ==================================
echo Personal Task Tracker - Quick Start
echo ==================================
echo.

REM Check if Docker is installed
docker --version >nul 2>&1
if errorlevel 1 (
    echo X Docker is not installed. Please install Docker Desktop first.
    pause
    exit /b 1
)

REM Check if Docker Compose is installed
docker-compose --version >nul 2>&1
if errorlevel 1 (
    echo X Docker Compose is not installed. Please install Docker Compose first.
    pause
    exit /b 1
)

REM Check if frontend directory exists
if not exist "..\task-tracker-frontend" (
    echo X Frontend directory not found.
    echo Please ensure task-tracker-frontend is in the same parent directory.
    pause
    exit /b 1
)

echo √ Prerequisites check passed
echo.

REM Check if .env exists, if not create from .env.example
if not exist ".env" (
    if exist ".env.example" (
        echo Creating .env file from .env.example...
        copy .env.example .env >nul
        echo √ .env file created
        echo.
    )
)

REM Start services
echo Starting services...
echo.
docker-compose up -d

echo.
echo Waiting for services to be ready...
echo This may take 1-2 minutes on first run...
echo.

REM Wait for services (simplified for Windows)
timeout /t 30 /nobreak >nul

echo.
echo ==================================
echo Service Status:
echo ==================================
docker-compose ps
echo.

REM Display access information
echo ==================================
echo Access Information:
echo ==================================
echo Frontend:     http://localhost
echo Backend API:  http://localhost:8080/api
echo Health Check: http://localhost:8080/actuator/health
echo.
echo Default Credentials:
echo   Username: user
echo   Password: password
echo.
echo ==================================
echo Useful Commands:
echo ==================================
echo   View logs:           docker-compose logs -f
echo   Stop services:       docker-compose down
echo   Restart services:    docker-compose restart
echo   Rebuild:             docker-compose up -d --build
echo ==================================
echo.
echo Application is ready! Open http://localhost in your browser.
echo.
pause
