# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Commands

```bash
# Compile
mvn clean compile

# Run tests
mvn test

# Run single test class
mvn test -Dtest=ClassNameTest

# Package
mvn clean package -DskipTests

# Run application
mvn spring-boot:run

# Or run JAR
java -jar target/edu-online-1.0.0.jar
```

## Architecture Overview

**Pattern**: Layered Spring Boot application with modular domain packages

```
com.edu/
├── EduOnlineApplication.java      # Entry point
├── common/                         # Shared components
│   ├── config/                    # WebConfig, FilterConfig, FileConfig
│   ├── aspect/                    # OperationLogAspect (AOP logging)
│   ├── enums/                     # ResultCode, UserRole
│   ├── exception/                 # BusinessException, GlobalExceptionHandler
│   ├── result/                    # Result (unified response wrapper)
│   └── utils/                     # JwtUtils
├── security/                       # JwtAuthenticationFilter
└── modules/                        # Domain modules (isolated)
    ├── auth/                       # Authentication (login/register/logout)
    ├── user/                       # User management
    ├── course/                     # Course + Chapter + Lesson
    ├── learning/                   # Enrollment + Progress tracking
    ├── exercise/                   # Practice exercises + submissions
    ├── exam/                       # Online exams + records
    ├── discussion/                 # Forum posts + replies
    ├── review/                     # Course reviews + ratings
    ├── file/                       # File upload (local + Aliyun OSS)
    └── system/                     # RBAC + Operation logs
```

## Module Structure (Standard Pattern)

Each domain module follows:
```
module/
├── controller/  - REST endpoints (@RestController)
├── service/     - Business logic interface
├── service/impl - Service implementation
├── entity/      - JPA entities
└── mapper/      - MyBatis-Plus mappers
```

## Key Technologies

- **ORM**: MyBatis-Plus with global config in `application.yml`
- **Auth**: JWT with 7-day expiry, filtered via `JwtAuthenticationFilter`
- **Table naming**: `tb_` prefix (configured in MyBatis-Plus global-config)
- **Soft delete**: `deleted` field (0=active, 1=deleted)
- **Response format**: `com.edu.common.result.Result` wrapper for all API responses

## Database

- MySQL 8.0, schema defined in `edu_online.sql`
- 17 tables covering users, courses, learning, exercises, exams, discussions, reviews
- Foreign keys enforce referential integrity

## Configuration Priority

`application.yml` is the main config. Key sections:
- `server.port`: 8080
- `spring.datasource`: MySQL connection
- `spring.redis`: Redis (optional caching)
- `jwt`: Token settings
- `oss`: Aliyun OSS credentials
- `mybatis-plus`: ORM + logging

## Testing

Tests located in `src/test/java`. Use standard Spring Boot test annotations.
