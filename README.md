# Banking Management System

A secure RESTful banking application built with Java, Spring Boot, Spring Security, Spring Data JPA and MySQL.

## Features
- User registration and JWT login
- Role-based authorization with USER and ADMIN roles
- Bank account creation and account lookup
- Deposit and withdrawal operations
- Transactional fund transfer between accounts
- Transaction history with pagination
- Input validation
- Global exception handling
- JUnit + Mockito service-layer tests
- MySQL persistence using Spring Data JPA/Hibernate

## Tech Stack
Java 21 | Spring Boot 3.5.6 | Spring Security | JWT (JJWT) | Spring Data JPA | Hibernate | MySQL | Maven | JUnit 5 | Mockito | Lombok

## Project Structure
```
src/main/java/com/bank
├── config          Security configuration
├── controller      REST controllers
├── dto             Request/response DTOs
├── entity          JPA entities and enums
├── exception       Custom exceptions + global handler
├── repository      Spring Data repositories
├── security        JWT filter/service + UserDetailsService
└── service         Business logic and transactions
```

## Database Setup
1. Install MySQL and start the server.
2. Create the database:
```sql
CREATE DATABASE banking_db;
```
3. Open `src/main/resources/application.properties` and change `spring.datasource.username` and `spring.datasource.password` if required.
4. The application uses `spring.jpa.hibernate.ddl-auto=update`, so tables are created/updated automatically.

## Run
```bash
mvn clean install
mvn spring-boot:run
```
Or import the project into Eclipse/STS as an existing Maven project and run `BankingManagementSystemApplication`.

## API Flow
### 1. Register
`POST /api/auth/register`
```json
{
  "username": "ketan",
  "password": "password123"
}
```

### 2. Login
`POST /api/auth/login`
```json
{
  "username": "ketan",
  "password": "password123"
}
```
Copy the returned JWT token and send it on protected requests:
`Authorization: Bearer <token>`

### 3. Create account
`POST /api/accounts`
```json
{
  "accountHolderName": "Ketan Tabade",
  "accountNumber": "1234567890"
}
```

### 4. Deposit
`POST /api/accounts/1234567890/deposit`
```json
{"amount": 5000.00}
```

### 5. Withdraw
`POST /api/accounts/1234567890/withdraw`
```json
{"amount": 1000.00}
```

### 6. Transfer
`POST /api/accounts/1234567890/transfer`
```json
{
  "toAccountNumber": "9876543210",
  "amount": 500.00
}
```

### 7. Transaction history
`GET /api/accounts/1234567890/transactions?page=0&size=10`

## Security Notes
This repository contains demo configuration only. Before deploying anywhere, replace the JWT secret and database credentials with environment variables or a secrets manager.

## Interview Explanation
The application follows Controller → Service → Repository architecture. JWT is used for stateless authentication. Spring Security validates the token before protected endpoints are reached. The service layer contains banking rules such as insufficient-balance checks. Fund transfers are marked `@Transactional`, so debit, credit and transaction-history inserts are handled as one database transaction.
