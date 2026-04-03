# Finance Dashboard — Backend API

A role-based finance data processing and access control backend built with **Spring Boot**, **Spring Security (JWT)**, and **MySQL**.

---

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Language | Java 17 |
| Framework | Spring Boot 3 |
| Security | Spring Security + JWT (JJWT) |
| Database | MySQL |
| ORM | Spring Data JPA / Hibernate |
| Build Tool | Maven |
| API Style | REST |

---

## Project Structure

```
src/main/java/com/zorvyn/finance_dashboard/
├── configuration/        # Security config, JWT provider & validator
├── controller/           # AuthController, TransactionController, UserController
├── dto/                  # Response DTOs (TransactionDto, UserDto, DashboardSummaryDto)
├── entity/               # JPA entities (User, Transaction)
├── enums/                # Role (VIEWER, ANALYST, ADMIN), TransactionType (INCOME, EXPENSE)
├── exception/            # GlobalExceptionHandler, custom exceptions
├── repository/           # Spring Data JPA repositories
├── request/              # Request body models with validation
├── response/             # AuthResponse, ApiResponse
└── service/              # Business logic interfaces + implementations
```

---

## Setup & Running Locally

### Prerequisites
- Java 17+
- MySQL running locally
- Maven

### 1. Create the database
```sql
CREATE DATABASE finance_db;
```

### 2. Configure `application.properties`
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/finance_db
spring.datasource.username=root
spring.datasource.password=root
spring.jpa.hibernate.ddl-auto=update
server.port=8081
```

### 3. Run the application
```bash
./mvnw spring-boot:run
```

The server starts at `http://localhost:8081`

---

## Roles & Access Control

| Role | Permissions |
|------|------------|
| `VIEWER` | View transactions, view own profile |
| `ANALYST` | View transactions + access dashboard summary |
| `ADMIN` | Full access — create, update, delete transactions + manage users |

Access control is enforced using **Spring Security `@PreAuthorize`** annotations on each endpoint, combined with JWT-based stateless authentication.

---

## API Reference

### Auth — `/api/auth`

#### POST `/api/auth/signup` — Register user
```json
{
  "username": "john_doe",
  "email": "john@gmail.com",
  "password": "secret123",
  "role": "ADMIN"
}
```
Response `201 Created`:
```json
{
  "email": "john@gmail.com",
  "role": "ADMIN",
  "token": "eyJhbGciOiJIUzM4N...",
  "tokenType": "Bearer",
  "username": "john_doe"
}
```

#### POST `/api/auth/login` — Login
```json
{
  "email": "john@gmail.com",
  "password": "secret123"
}
```
Response `200 OK`:
```json
{
  "email": "john@gmail.com",
  "role": "ADMIN",
  "token": "eyJhbGciOiJIUzM4N...",
  "tokenType": "Bearer",
  "username": "john_doe"
}
```

> All protected endpoints require: `Authorization: Bearer <token>`

---

### Transactions — `/api/transaction`

| Method | Endpoint | Role Required | Description |
|--------|----------|--------------|-------------|
| `POST` | `/api/transaction/create` | ADMIN | Create a transaction |
| `GET` | `/api/transaction/{id}` | VIEWER, ANALYST, ADMIN | Get transaction by ID |
| `POST` | `/api/transaction/update/{id}` | ADMIN | Update a transaction |
| `DELETE` | `/api/transaction/{id}` | ADMIN | Delete a transaction |
| `GET` | `/api/transaction` | VIEWER, ANALYST, ADMIN | Get all (with optional filters) |
| `GET` | `/api/transaction/paged` | VIEWER, ANALYST, ADMIN | Paginated transactions |
| `GET` | `/api/transaction/recent` | VIEWER, ANALYST, ADMIN | Recent transactions |
| `GET` | `/api/transaction/dashboard/summary` | ANALYST, ADMIN | Dashboard summary |

#### Create Transaction — Request Body
```json
{
  "amount": 50000.00,
  "type": "INCOME",
  "category": "Salary",
  "date": "2026-04-01",
  "description": "Monthly salary credit"
}
```

#### Filter Query Parameters
```
GET /api/transaction?type=EXPENSE
GET /api/transaction?category=Salary
GET /api/transaction?startDate=2026-04-01&endDate=2026-04-30
GET /api/transaction?type=INCOME&startDate=2026-04-01&endDate=2026-04-30
```

#### Pagination
```
GET /api/transaction/paged?page=0&size=5&sortBy=amount&direction=desc
```

#### Dashboard Summary Response
```json
{
  "totalIncome": 50000.00,
  "totalExpenses": 2600.00,
  "netBalance": 47400.00,
  "categoryTotals": {
    "Salary": 50000.00,
    "Marketing": 1800.00,
    "Office Supplies": 800.00
  },
  "recentTransactions": [ ... ],
  "monthlyIncome": { "2026-04": 50000.00 },
  "monthlyExpenses": { "2026-04": 2600.00 }
}
```

---

### Users — `/api/users`

| Method | Endpoint | Role Required | Description |
|--------|----------|--------------|-------------|
| `GET` | `/api/users/all` | ADMIN | Get all users |
| `GET` | `/api/users/{id}` | VIEWER, ANALYST, ADMIN | Get user by ID |
| `PUT` | `/api/users/{id}/status` | ADMIN | Update user role |
| `PUT` | `/api/users/{id}` | VIEWER, ANALYST, ADMIN | Update own profile |
| `DELETE` | `/api/users/{id}` | ADMIN | Delete user |

#### Update Role — Request Body
```json
{ "role": "ANALYST" }
```

---

## Data Models

### User
| Field | Type | Notes |
|-------|------|-------|
| id | Long | Auto-generated |
| username | String | Unique, 3–50 chars |
| email | String | Unique, valid email |
| password | String | BCrypt encoded |
| role | Enum | VIEWER / ANALYST / ADMIN |
| active | boolean | Default true |
| createdAt | LocalDateTime | Auto-set |
| updatedAt | LocalDateTime | Auto on update |

### Transaction
| Field | Type | Notes |
|-------|------|-------|
| id | Long | Auto-generated |
| amount | BigDecimal | > 0, max 13 integer digits |
| type | Enum | INCOME / EXPENSE |
| category | String | Max 100 chars |
| date | LocalDate | ISO format |
| description | String | Optional, max 500 chars |
| deleted | boolean | Soft delete flag |
| createdBy | User | FK to User |
| createdAt | LocalDateTime | Auto-set |
| updatedAt | LocalDateTime | Auto on update |

---

## Security Design

- **Stateless JWT authentication** — no sessions
- Token is signed using HMAC-SHA and contains `email` + `authorities` claims
- `JwtTokenValidator` (OncePerRequestFilter) validates token on every request and sets the `SecurityContext`
- Passwords are hashed with **BCrypt**
- Public routes: `/api/auth/signup`, `/api/auth/login`
- All other `/api/**` routes require a valid JWT

---

## Validation & Error Handling

All request bodies are validated using **Jakarta Bean Validation** (`@NotNull`, `@NotBlank`, `@Email`, `@Size`, `@DecimalMin`).

A `GlobalExceptionHandler` handles:

| Exception | HTTP Status |
|-----------|------------|
| Validation failure | `400 Bad Request` |
| Unauthorized / bad token | `401 Unauthorized` |
| Insufficient role | `403 Forbidden` |
| Resource not found | `404 Not Found` |
| Duplicate email/username | `409 Conflict` |
| Unexpected errors | `500 Internal Server Error` |

---

## Optional Features Implemented

- ✅ JWT Authentication (stateless, role-embedded tokens)
- ✅ Pagination with sorting (`/api/transaction/paged`)
- ✅ Filtering by type, category, and date range
- ✅ Soft delete on transactions (`deleted` flag)
- ✅ Dashboard summary with category totals and monthly trends
- ✅ Global exception handling with meaningful error responses
- ✅ Role-based access control via `@PreAuthorize`

---

## Assumptions Made

1. A user registers with a role assigned at signup — no separate role assignment flow is required at creation time.
2. Login uses **email** as the identifier since `loadUserByUsername` is implemented using email lookup.
3. Soft delete is used for transactions — deleted records are excluded from all queries but remain in the database.
4. The `createdBy` field on a transaction is set from the authenticated user's JWT principal at the time of creation.
5. Dashboard summary aggregates all non-deleted transactions regardless of date range unless otherwise filtered.

---

## Test Credentials (for evaluation)

| Role | Email | Password |
|------|-------|----------|
| ADMIN | nisha@gmail.com | secret123 |
| VIEWER | aditya@gmail.com | secret123 |
| VIEWER | john@gmail.com | secret123 |

---

## Author

**Aditya Chavhan**
adityamchavhan24@gmail.com
Backend Developer Intern — Zorvyn FinTech Pvt. Ltd.
