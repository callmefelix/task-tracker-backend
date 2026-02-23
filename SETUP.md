# Setup Guide

Complete setup instructions for the Task Tracker Backend.

## Table of Contents

1. [Prerequisites](#prerequisites)
2. [Environment Setup](#environment-setup)
3. [Database Setup](#database-setup)
4. [Application Configuration](#application-configuration)
5. [Running the Application](#running-the-application)
6. [Verification](#verification)
7. [Troubleshooting](#troubleshooting)

## Prerequisites

### Required Software

- **Java Development Kit (JDK) 17 or higher**
  - Download from [Oracle](https://www.oracle.com/java/technologies/downloads/) or [OpenJDK](https://adoptium.net/)
  - Verify: `java -version`

- **MySQL 8.0 or higher**
  - Download from [MySQL Downloads](https://dev.mysql.com/downloads/)
  - Or use Docker: `docker run -d -p 3306:3306 -e MYSQL_ROOT_PASSWORD=root mysql:8.0`

- **Gradle 8.10+ (optional)**
  - The project includes Gradle Wrapper, so you don't need to install Gradle separately
  - Verify: `./gradlew --version`

### Optional Software

- **Docker Desktop** - For containerized deployment
- **IntelliJ IDEA** - Recommended IDE for Kotlin development
- **Postman** or **curl** - For API testing

## Environment Setup

### 1. Clone the Repository

```bash
git clone <repository-url>
cd task-tracker-backend
```

### 2. Set Java Version

Ensure JDK 17 is your active version:

```bash
# Check current version
java -version

# On Linux/Mac with SDKMAN
sdk use java 17.0.x-tem

# On Windows, set JAVA_HOME environment variable
# System Properties -> Environment Variables -> JAVA_HOME = C:\Program Files\Java\jdk-17
```

### 3. Configure IDE (Optional)

#### IntelliJ IDEA

1. Open project in IntelliJ IDEA
2. File → Project Structure → Project SDK: Select JDK 17
3. File → Settings → Build, Execution, Deployment → Build Tools → Gradle
   - Gradle JVM: Select JDK 17
4. Install Kotlin plugin if not already installed

#### VS Code

1. Install Java Extension Pack
2. Install Kotlin extension
3. Set java.home in settings.json

## Database Setup

### Option 1: Local MySQL Installation

1. **Install MySQL 8.0**
   - Download and install from [mysql.com](https://dev.mysql.com/downloads/)
   - During installation, set root password

2. **Create Database and User**

   ```sql
   # Connect to MySQL as root
   mysql -u root -p

   # Create database
   CREATE DATABASE tasktracker_db;

   # Create user with password
   CREATE USER 'taskuser'@'localhost' IDENTIFIED BY 'taskpass';

   # Grant privileges
   GRANT ALL PRIVILEGES ON tasktracker_db.* TO 'taskuser'@'localhost';

   # Apply changes
   FLUSH PRIVILEGES;

   # Verify
   USE tasktracker_db;
   SHOW TABLES;  # Should be empty initially

   # Exit
   EXIT;
   ```

3. **Test Connection**

   ```bash
   mysql -u taskuser -p tasktracker_db
   # Enter password: taskpass
   ```

### Option 2: Docker MySQL

```bash
# Start MySQL container
docker run -d \
  --name tasktracker-mysql \
  -p 3306:3306 \
  -e MYSQL_ROOT_PASSWORD=rootpassword \
  -e MYSQL_DATABASE=tasktracker_db \
  -e MYSQL_USER=taskuser \
  -e MYSQL_PASSWORD=taskpass \
  mysql:8.0

# Verify container is running
docker ps | grep tasktracker-mysql

# Connect to MySQL
docker exec -it tasktracker-mysql mysql -u taskuser -p
# Enter password: taskpass
```

### Option 3: Docker Compose (Full Stack)

```bash
# Starts MySQL, backend, and frontend
docker-compose up -d

# Database will be automatically created and configured
```

## Application Configuration

### 1. Review application.properties

The file `src/main/resources/application.properties` contains all configuration:

```properties
# Database connection (default for local MySQL)
spring.datasource.url=jdbc:mysql://localhost:3306/tasktracker_db
spring.datasource.username=taskuser
spring.datasource.password=taskpass

# For Docker, these are overridden by environment variables
spring.datasource.url=${DB_URL:jdbc:mysql://localhost:3306/tasktracker_db}
spring.datasource.username=${DB_USERNAME:taskuser}
spring.datasource.password=${DB_PASSWORD:taskpass}
```

### 2. Environment Variables (Optional)

For different environments, set these environment variables:

```bash
# Linux/Mac
export DB_URL="jdbc:mysql://localhost:3306/tasktracker_db"
export DB_USERNAME="taskuser"
export DB_PASSWORD="taskpass"

# Windows PowerShell
$env:DB_URL = "jdbc:mysql://localhost:3306/tasktracker_db"
$env:DB_USERNAME = "taskuser"
$env:DB_PASSWORD = "taskpass"

# Windows CMD
set DB_URL=jdbc:mysql://localhost:3306/tasktracker_db
set DB_USERNAME=taskuser
set DB_PASSWORD=taskpass
```

### 3. Create .env file (For Docker)

```bash
cp .env.example .env
# Edit .env with your values
```

## Running the Application

### Method 1: Using Gradle Wrapper (Local Development)

```bash
# Clean and build
./gradlew clean build

# Run application
./gradlew bootRun

# Or on Windows
gradlew.bat bootRun
```

The application will start on http://localhost:8080

### Method 2: Running JAR file

```bash
# Build JAR
./gradlew bootJar

# Run JAR
java -jar build/libs/task-tracker-backend-0.0.1-SNAPSHOT.jar

# With custom profile
java -jar build/libs/task-tracker-backend-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev
```

### Method 3: Docker

```bash
# Build Docker image
docker build -t task-tracker-backend .

# Run container
docker run -p 8080:8080 \
  -e DB_URL="jdbc:mysql://host.docker.internal:3306/tasktracker_db" \
  -e DB_USERNAME="taskuser" \
  -e DB_PASSWORD="taskpass" \
  task-tracker-backend
```

### Method 4: Docker Compose (Recommended)

```bash
# Start all services (MySQL + Backend + Frontend)
docker-compose up -d

# View logs
docker-compose logs -f backend

# Stop services
docker-compose down
```

## Verification

### 1. Check Application Startup

Look for this in the console output:

```
Started TaskTrackerBackendApplicationKt in X.XXX seconds
```

### 2. Test Health Endpoint

```bash
curl http://localhost:8080/actuator/health
# Expected: {"status":"UP"}
```

### 3. Test Authentication

```bash
# Register a new user
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"testpass123"}'

# Login
curl -X POST http://localhost:8080/api/auth/token \
  -H "Content-Type: application/json" \
  -d '{"username":"user","password":"password"}'

# Expected: {"access_token":"eyJ..."}
```

### 4. Test Task API

```bash
# Get token first
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/token \
  -H "Content-Type: application/json" \
  -d '{"username":"user","password":"password"}' \
  | jq -r '.access_token')

# Create a task
curl -X POST http://localhost:8080/api/tasks \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"title":"Test Task","description":"Testing API","status":"TODO"}'

# Get all tasks
curl -X GET http://localhost:8080/api/tasks \
  -H "Authorization: Bearer $TOKEN"
```

### 5. Verify Database

```bash
# Connect to database
mysql -u taskuser -p tasktracker_db

# Check tables
SHOW TABLES;
# Expected: tasks, users

# Check default users
SELECT username, role FROM users;
# Expected: user, admin

# Check tasks
SELECT title, status FROM tasks;
```

## Troubleshooting

### Issue: Port 8080 already in use

**Solution:**

```bash
# Find process using port 8080
# Linux/Mac
lsof -i :8080

# Windows
netstat -ano | findstr :8080

# Kill the process or change port in application.properties
server.port=8081
```

### Issue: Cannot connect to MySQL

**Error:** `Communications link failure`

**Solutions:**

1. **Check MySQL is running**
   ```bash
   # Linux
   sudo systemctl status mysql

   # Mac
   brew services list

   # Windows
   services.msc  # Look for MySQL
   ```

2. **Verify MySQL port**
   ```bash
   mysql -u root -p -e "SHOW VARIABLES LIKE 'port';"
   ```

3. **Check MySQL is accepting connections**
   ```bash
   # Should see 0.0.0.0:3306 or :::3306
   netstat -an | grep 3306
   ```

4. **Test connection**
   ```bash
   mysql -u taskuser -p -h localhost tasktracker_db
   ```

### Issue: Access denied for user

**Error:** `Access denied for user 'taskuser'@'localhost'`

**Solution:**

```sql
# Connect as root
mysql -u root -p

# Check user exists
SELECT user, host FROM mysql.user WHERE user='taskuser';

# If not exists, create user
CREATE USER 'taskuser'@'localhost' IDENTIFIED BY 'taskpass';
GRANT ALL PRIVILEGES ON tasktracker_db.* TO 'taskuser'@'localhost';
FLUSH PRIVILEGES;

# If using Docker, might need wildcard host
CREATE USER 'taskuser'@'%' IDENTIFIED BY 'taskpass';
GRANT ALL PRIVILEGES ON tasktracker_db.* TO 'taskuser'@'%';
FLUSH PRIVILEGES;
```

### Issue: Database not found

**Error:** `Unknown database 'tasktracker_db'`

**Solution:**

```sql
# Connect as root
mysql -u root -p

# Create database
CREATE DATABASE tasktracker_db;

# Verify
SHOW DATABASES;
```

### Issue: Gradle build fails

**Error:** `Could not resolve dependencies`

**Solutions:**

1. **Clean Gradle cache**
   ```bash
   ./gradlew clean --refresh-dependencies
   ```

2. **Delete Gradle cache manually**
   ```bash
   rm -rf ~/.gradle/caches/
   ./gradlew build
   ```

3. **Check internet connection** (Gradle needs to download dependencies)

4. **Use Gradle wrapper version**
   ```bash
   ./gradlew wrapper --gradle-version=8.10.2
   ```

### Issue: Out of memory during build

**Error:** `java.lang.OutOfMemoryError`

**Solution:**

Create/edit `gradle.properties`:

```properties
org.gradle.jvmargs=-Xmx2048m -XX:MaxMetaspaceSize=512m
```

### Issue: Tables not created

**Solution:**

Check Hibernate is configured correctly in application.properties:

```properties
spring.jpa.hibernate.ddl-auto=update
```

Run application, then check database:

```sql
USE tasktracker_db;
SHOW TABLES;
```

### Issue: CORS errors in browser

**Solution:**

WebConfig.kt already configures CORS. If still seeing errors:

1. Check frontend URL matches allowed origins
2. Clear browser cache
3. Check browser console for actual error
4. Verify backend is running

### Issue: JWT token expired

**Error:** `401 Unauthorized`

**Solution:**

1. Login again to get new token (tokens expire after 1 hour)
2. Or increase token expiry in AuthController.kt:
   ```kotlin
   .expiresAt(now.plus(24, ChronoUnit.HOURS)) // 24 hours
   ```

## Next Steps

After successful setup:

1. ✅ Test all API endpoints with Postman or curl
2. ✅ Start the frontend application
3. ✅ Create sample tasks through the UI
4. ✅ Review the [README.md](README.md) for API documentation
5. ✅ Check [DOCKER_SETUP.md](DOCKER_SETUP.md) for Docker deployment

## Additional Resources

- [Spring Boot Documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [Kotlin Documentation](https://kotlinlang.org/docs/home.html)
- [MySQL Documentation](https://dev.mysql.com/doc/)
- [Gradle User Guide](https://docs.gradle.org/current/userguide/userguide.html)
- [Docker Documentation](https://docs.docker.com/)

## Support

If you encounter issues not covered here:

1. Check application logs in console output
2. Check MySQL error logs
3. Review [README.md](README.md) for API documentation
4. Create an issue in the repository with:
   - Error message
   - Steps to reproduce
   - Environment details (OS, Java version, MySQL version)
