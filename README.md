# Task Tracker Backend

A production-grade RESTful API for personal task management built with Kotlin and Spring Boot.

## 📋 Table of Contents

- [Features](#features)
- [Technology Stack](#technology-stack)
- [Prerequisites](#prerequisites)
- [Quick Start](#quick-start)
- [API Documentation](#api-documentation)
- [Configuration](#configuration)
- [Database Schema](#database-schema)
- [Development](#development)
- [Docker Deployment](#docker-deployment)
- [Architecture](#architecture)
- [Future Improvements](#future-improvements)

## ✨ Features

### Core Functionality
- ✅ **Task Management**: Create, read, update, and delete tasks
- ✅ **User Authentication**: JWT-based authentication with secure password encryption
- ✅ **User Registration**: Self-service user registration
- ✅ **Task Filtering**: Filter tasks by status and search by title/description
- ✅ **User Isolation**: Each user can only access their own tasks
- ✅ **RESTful API**: Clean, consistent API design

### Technical Features
- ✅ **JWT Authentication**: Stateless authentication with 1-hour token expiry
- ✅ **Password Encryption**: BCrypt password hashing
- ✅ **Database Persistence**: MySQL with JPA/Hibernate
- ✅ **Health Checks**: Actuator endpoints for monitoring
- ✅ **CORS Support**: Configured for frontend integration
- ✅ **Docker Support**: Containerized with docker-compose
- ✅ **Input Validation**: Request validation with detailed error messages
- ✅ **Global Exception Handling**: Consistent error responses

## 🛠️ Technology Stack

- **Language**: Kotlin 1.9.25
- **Framework**: Spring Boot 3.4.1
  - Spring Data JPA
  - Spring Security
  - Spring Web
  - Spring Validation
  - Spring Actuator
- **Database**: MySQL 8.0
- **Authentication**: JWT (OAuth2 Resource Server)
- **Build Tool**: Gradle 8.10.2
- **JDK**: 17
- **Container**: Docker

## 📦 Prerequisites

### For Local Development
- JDK 17 or higher
- MySQL 8.0 or higher
- Gradle 8.10+ (or use included wrapper)

### For Docker Deployment
- Docker Engine 20.10+
- Docker Compose 1.29+

## 🚀 Quick Start

### Option 1: Docker (Recommended)

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd task-tracker-backend
   ```

2. **Start all services**
   ```bash
   docker-compose up -d
   ```

3. **Access the API**
   - API Base URL: http://localhost:8080/api
   - Health Check: http://localhost:8080/actuator/health

### Option 2: Local Development

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd task-tracker-backend
   ```

2. **Set up MySQL database**
   ```sql
   CREATE DATABASE tasktracker_db;
   CREATE USER 'taskuser'@'localhost' IDENTIFIED BY 'taskpass';
   GRANT ALL PRIVILEGES ON tasktracker_db.* TO 'taskuser'@'localhost';
   FLUSH PRIVILEGES;
   ```

3. **Configure application properties** (optional)

   Edit `src/main/resources/application.properties`:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/tasktracker_db
   spring.datasource.username=taskuser
   spring.datasource.password=taskpass
   ```

4. **Build and run**
   ```bash
   ./gradlew bootRun
   ```

5. **API will be available at**: http://localhost:8080

## 📚 API Documentation

### Authentication Endpoints

#### Register User
```http
POST /api/auth/register
Content-Type: application/json

{
  "username": "john",
  "password": "password123"
}

Response: 201 Created
{
  "message": "User registered successfully"
}
```

#### Login
```http
POST /api/auth/token
Content-Type: application/json

{
  "username": "user",
  "password": "password"
}

Response: 200 OK
{
  "access_token": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

### Task Endpoints

All task endpoints require authentication. Include the JWT token in the Authorization header:
```
Authorization: Bearer <access_token>
```

#### Get All Tasks
```http
GET /api/tasks
GET /api/tasks?status=TODO
GET /api/tasks?search=meeting

Response: 200 OK
[
  {
    "id": "uuid",
    "title": "Complete project",
    "description": "Finish the backend API",
    "status": "TODO",
    "createdAt": "2026-02-23T10:00:00Z",
    "updatedAt": "2026-02-23T10:00:00Z"
  }
]
```

#### Get Task by ID
```http
GET /api/tasks/{id}

Response: 200 OK
{
  "id": "uuid",
  "title": "Complete project",
  "description": "Finish the backend API",
  "status": "TODO",
  "createdAt": "2026-02-23T10:00:00Z",
  "updatedAt": "2026-02-23T10:00:00Z"
}
```

#### Create Task
```http
POST /api/tasks
Content-Type: application/json

{
  "title": "New task",
  "description": "Task description",
  "status": "TODO"
}

Response: 201 Created
{
  "id": "uuid",
  "title": "New task",
  "description": "Task description",
  "status": "TODO",
  "createdAt": "2026-02-23T10:00:00Z",
  "updatedAt": "2026-02-23T10:00:00Z"
}
```

#### Update Task
```http
PUT /api/tasks/{id}
Content-Type: application/json

{
  "title": "Updated title",
  "description": "Updated description",
  "status": "IN_PROGRESS"
}

Response: 200 OK
{
  "id": "uuid",
  "title": "Updated title",
  "description": "Updated description",
  "status": "IN_PROGRESS",
  "createdAt": "2026-02-23T10:00:00Z",
  "updatedAt": "2026-02-23T10:30:00Z"
}
```

#### Delete Task
```http
DELETE /api/tasks/{id}

Response: 204 No Content
```

### Task Status Values
- `TODO` - Task not started
- `IN_PROGRESS` - Task in progress
- `DONE` - Task completed

### Error Responses

```json
{
  "status": 400,
  "message": "Validation failed",
  "timestamp": "2026-02-23T10:00:00Z",
  "path": "/api/tasks"
}
```

Common HTTP Status Codes:
- `200` - Success
- `201` - Created
- `204` - No Content (successful deletion)
- `400` - Bad Request (validation error)
- `401` - Unauthorized (missing or invalid token)
- `403` - Forbidden
- `404` - Not Found
- `500` - Internal Server Error

## ⚙️ Configuration

### Environment Variables

The application supports environment variables for Docker deployment:

| Variable | Default | Description |
|----------|---------|-------------|
| `DB_URL` | `jdbc:mysql://localhost:3306/tasktracker_db` | Database connection URL |
| `DB_USERNAME` | `user` | Database username |
| `DB_PASSWORD` | `password` | Database password |

### Application Properties

Key configuration in `application.properties`:

```properties
# Server
server.port=8080

# Database
spring.datasource.url=jdbc:mysql://localhost:3306/tasktracker_db
spring.datasource.username=taskuser
spring.datasource.password=taskpass

# JPA/Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# Actuator
management.endpoints.web.exposure.include=health
```

### Default Users

The application creates two default users on startup:

| Username | Password | Role |
|----------|----------|------|
| `user` | `password` | USER |
| `admin` | `admin` | ADMIN |

## 🗄️ Database Schema

### Users Table
```sql
CREATE TABLE users (
  id BINARY(16) PRIMARY KEY,
  username VARCHAR(255) UNIQUE NOT NULL,
  password VARCHAR(255) NOT NULL,
  role VARCHAR(50) NOT NULL,
  created_at TIMESTAMP NOT NULL,
  updated_at TIMESTAMP NOT NULL
);
```

### Tasks Table
```sql
CREATE TABLE tasks (
  id BINARY(16) PRIMARY KEY,
  title VARCHAR(255) NOT NULL,
  description TEXT,
  status VARCHAR(50) NOT NULL,
  created_at TIMESTAMP NOT NULL,
  updated_at TIMESTAMP NOT NULL,
  user_id BINARY(16) NOT NULL,
  FOREIGN KEY (user_id) REFERENCES users(id)
);
```

## 💻 Development

### Build the Project
```bash
./gradlew build
```

### Run Tests
```bash
./gradlew test
```

### Clean Build
```bash
./gradlew clean build
```

### Run Locally
```bash
./gradlew bootRun
```

### Check Code Quality
```bash
./gradlew check
```

### Generate JAR
```bash
./gradlew bootJar
```

The executable JAR will be in `build/libs/task-tracker-backend-0.0.1-SNAPSHOT.jar`

## 🐳 Docker Deployment

### Using Docker Compose (Recommended)

The project includes a complete `docker-compose.yml` file that sets up:
- MySQL database
- Backend API
- Frontend application (optional)

```bash
# Start all services
docker-compose up -d

# View logs
docker-compose logs -f

# Stop services
docker-compose down

# Rebuild and restart
docker-compose up -d --build
```

See [DOCKER_SETUP.md](DOCKER_SETUP.md) for detailed Docker instructions.

### Manual Docker Build

```bash
# Build image
docker build -t task-tracker-backend .

# Run container
docker run -p 8080:8080 \
  -e DB_URL="jdbc:mysql://host.docker.internal:3306/tasktracker_db" \
  -e DB_USERNAME="taskuser" \
  -e DB_PASSWORD="taskpass" \
  task-tracker-backend
```

## 🏗️ Architecture

### Project Structure
```
src/
├── main/
│   ├── kotlin/
│   │   └── com/
│   │       └── tasktracker/
│   │           ├── config/          # Configuration classes
│   │           │   ├── DataInitializer.kt
│   │           │   ├── SecurityConfig.kt
│   │           │   └── WebConfig.kt
│   │           ├── controller/      # REST controllers
│   │           │   ├── AuthController.kt
│   │           │   └── TaskController.kt
│   │           ├── domain/          # Entity classes
│   │           │   ├── Task.kt
│   │           │   ├── TaskStatus.kt
│   │           │   └── User.kt
│   │           ├── dto/             # Data transfer objects
│   │           │   ├── CreateTaskRequest.kt
│   │           │   ├── LoginRequest.kt
│   │           │   ├── RegisterRequest.kt
│   │           │   ├── TaskFilterRequest.kt
│   │           │   ├── TaskResponse.kt
│   │           │   └── UpdateTaskRequest.kt
│   │           ├── exception/       # Exception handling
│   │           │   ├── GlobalExceptionHandler.kt
│   │           │   └── ResourceNotFoundException.kt
│   │           ├── repository/      # Data access layer
│   │           │   ├── TaskRepository.kt
│   │           │   └── UserRepository.kt
│   │           └── service/         # Business logic
│   │               ├── CustomUserDetailsService.kt
│   │               ├── TaskService.kt
│   │               └── UserService.kt
│   └── resources/
│       └── application.properties
└── test/
    └── kotlin/
        └── com/
            └── tasktracker/
                └── TaskTrackerBackendApplicationTests.kt
```

### Architecture Layers

1. **Controller Layer**: REST endpoints and request/response handling
2. **Service Layer**: Business logic and transaction management
3. **Repository Layer**: Database operations using Spring Data JPA
4. **Domain Layer**: Entity models and business objects
5. **DTO Layer**: Data transfer objects for API contracts
6. **Configuration Layer**: Security, CORS, and application setup

### Security Architecture

- **JWT-based authentication**: Stateless token authentication
- **BCrypt password encryption**: Secure password storage
- **User isolation**: Tasks are scoped to the authenticated user
- **CORS configuration**: Supports frontend integration
- **Health check endpoint**: Public endpoint for monitoring

## 📈 Future Improvements

### High Priority
- [ ] **Database Migration**: Use Flyway or Liquibase for version control
- [ ] **Pagination**: Add pagination support for task lists
- [ ] **Unit Tests**: Add comprehensive unit and integration tests
- [ ] **API Documentation**: Add Swagger/OpenAPI documentation
- [ ] **Logging**: Enhanced logging with correlation IDs

### Medium Priority
- [ ] **Task Categories/Tags**: Organize tasks with categories
- [ ] **Task Priority**: Add priority levels to tasks
- [ ] **Due Dates**: Add due date functionality
- [ ] **Task Sharing**: Share tasks between users
- [ ] **Task Comments**: Add comments to tasks
- [ ] **File Attachments**: Attach files to tasks

### Low Priority (Production Deployment)
- [ ] **Load Balancing**: Distribute traffic across instances
- [ ] **Caching**: Redis for session and query caching
- [ ] **Monitoring**: Prometheus + Grafana for metrics
- [ ] **Rate Limiting**: API rate limiting
- [ ] **Database Read Replicas**: Separate read/write databases
- [ ] **Message Queue**: Async processing for heavy operations

## 🤝 Contributing

This is a take-home assignment project. For actual contributions:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📄 License

This project is created as a technical assessment.

## 📞 Support

For questions or issues, please create an issue in the repository.

---

**Built with ❤️ using Kotlin and Spring Boot**
