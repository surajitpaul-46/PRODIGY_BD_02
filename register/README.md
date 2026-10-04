# Task 2: Register User API with MySQL Database Persistence

<p align="center">
  <img src="https://img.shields.io/badge/Java-21-orange?style=for-the-badge&logo=openjdk" alt="Java 21" />
  <img src="https://img.shields.io/badge/Spring_Boot-3.4.3-brightgreen?style=for-the-badge&logo=springboot" alt="Spring Boot 3.4.3" />
  <img src="https://img.shields.io/badge/MySQL-8.0-4479A1?style=for-the-badge&logo=mysql" alt="MySQL" />
  <img src="https://img.shields.io/badge/Spring_Data_JPA-Hibernate-59666C?style=for-the-badge&logo=hibernate" alt="JPA" />
  <img src="https://img.shields.io/badge/HikariCP-Connection_Pool-005C8A?style=for-the-badge" alt="HikariCP" />
</p>

Task 2 upgrades the previous REST API from in-memory storage to a persistent **MySQL Relational Database** using **Spring Data JPA (Hibernate)**, **HikariCP Connection Pooling**, **SQL Database Migrations**, and **Environment Variables (`.env`)**.

---

## 📌 Task 2 Features & Enhancements

- **Relational Database Integration**: Integrated MySQL using Spring Data JPA (`JpaRepository`) and Hibernate ORM.
- **HikariCP Connection Pooling**: Configured high-performance connection pool management (`maximum-pool-size=10`, `minimum-idle=5`).
- **Environment Variable Configuration**: Integrated `.env` file loading for sensitive database credentials.
- **Database Schema Migration**: Included `V1__create_users_table.sql` migration script for table schema setup.
- **Hibernate Automatic Schema DDL**: `spring.jpa.hibernate.ddl-auto=update` and `spring.jpa.show-sql=true`.

---

## ⚙️ Environment Variables Configuration (`.env`)

```env
SPRING_APPLICATION_NAME=register
SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3307/register
SPRING_DATASOURCE_USERNAME=your_db_username
SPRING_DATASOURCE_PASSWORD=your_db_password
SPRING_JPA_HIBERNATE_DDL_AUTO=update
SPRING_JPA_SHOW_SQL=true
```

---

## 📁 Application Properties (`application.properties`)

```properties
spring.application.name=${SPRING_APPLICATION_NAME:register}

# MySQL Datasource Configuration
spring.datasource.url=${SPRING_DATASOURCE_URL:jdbc:mysql://localhost:3307/register}
spring.datasource.username=${your_db_username}
spring.datasource.password=${your_db_password}
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# HikariCP Connection Pooling
spring.datasource.hikari.maximum-pool-size=10
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.idle-timeout=30000
spring.datasource.hikari.connection-timeout=20000
spring.datasource.hikari.pool-name=RegisterHikariPool

# JPA & Hibernate Settings
spring.jpa.hibernate.ddl-auto=${SPRING_JPA_HIBERNATE_DDL_AUTO:update}
spring.jpa.show-sql=${SPRING_JPA_SHOW_SQL:true}
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect
```

---

## 🗄️ Database Table Schema (`users`)

```sql
CREATE TABLE IF NOT EXISTS users (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    age INT NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

---

## 🚀 REST API Specification

Base URL: `http://localhost:8080/api/users`

| Method | Endpoint | Description | Status Code |
|--------|----------|-------------|-------------|
| `POST` | `/api/users` | Create user in MySQL | `201 Created` / `400 Bad Request` |
| `GET` | `/api/users` | Retrieve all users from MySQL | `200 OK` |
| `GET` | `/api/users/{id}` | Get user by UUID | `200 OK` / `404 Not Found` |
| `PUT` | `/api/users/{id}` | Update user attributes in MySQL | `200 OK` / `404 Not Found` / `400 Bad Request` |
| `DELETE` | `/api/users/{id}` | Delete user from MySQL | `204 No Content` / `404 Not Found` |

---

## 🧪 Testing & Execution

```bash
# Run unit & integration tests
.\gradlew.bat test

# Run application server (Port 8080)
.\gradlew.bat bootRun
```
