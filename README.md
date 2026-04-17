# Ecommerce Application

A microservice-based e-commerce backend built with Spring Boot 3.x and Java 21.

## Tech Stack

- **Framework**: Spring Boot 3.5.6
- **Language**: Java 21
- **Security**: Spring Security + JWT (jjwt 0.13.0)
- **Persistence**: Spring Data JPA
- **Database**: H2 (development)
- **Validation**: Spring Boot Starter Validation
- **Utilities**: Lombok
- **Build**: Maven

## Project Status

> Early development phase — services and features are being added incrementally.

## Modules / Services

> To be documented as services are added.

## Getting Started

### Prerequisites

- Java 21+
- Maven 3.8+

### Run Locally

```bash
mvn spring-boot:run
```

The application starts on `http://localhost:8080` by default.

## API Overview

### Users

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/users` | Retrieve all users |
| GET | `/api/users/{id}` | Retrieve a user by UUID |
| POST | `/api/users` | Create a new user |
| PUT | `/api/users/{id}` | Update an existing user |

#### User Request Body (`POST` / `PUT`)

```json
{
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@example.com",
  "phoneNumber": "9876543210",
  "userRole": "CUSTOMER"
}
```

> `userRole` accepted values: `CUSTOMER`, `ADMIN`

#### User Response Body

```json
{
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@example.com",
  "phoneNumber": "9876543210",
  "userRole": "CUSTOMER"
}
```

