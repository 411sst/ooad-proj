# Movie Ticket Booking System

A comprehensive web-based movie ticket booking system built with Spring Boot, implementing 8 design patterns and 8 design principles.

## Team
- **Shrish** - Seat Selection & Real-time Updates (State + Observer Pattern)
- **Vaishnav** - Payment Processing & F&B Integration (Decorator + Facade Pattern)
- **Saffiya** - Movie Catalog & Smart Recommendations (Abstract Factory + Singleton Pattern)
- **Rushad** - Theater Management & Dynamic Pricing (Chain of Responsibility + Strategy Pattern)

## Tech Stack
- **Backend**: Java 17, Spring Boot 3.2.2, Spring Security, Spring Data JPA, WebSocket (STOMP)
- **Frontend**: Thymeleaf, Bootstrap 5, Chart.js, SockJS + STOMP.js
- **Database**: PostgreSQL (prod) / H2 (dev)
- **Build**: Maven

## Quick Start

### Prerequisites
- Java 17+
- Maven 3.8+
- (Optional) Docker for PostgreSQL

### Run with H2 (Development)
```bash
mvn spring-boot:run
```
App starts at http://localhost:8080

### Run with PostgreSQL (Production)
```bash
docker-compose up -d
mvn spring-boot:run -Dspring.profiles.active=prod
```

### Demo Accounts
| Role     | Email                 | Password     |
|----------|-----------------------|--------------|
| Customer | shrish@example.com    | Password123! |
| Customer | vaishnav@example.com  | Password123! |
| Customer | saffiya@example.com   | Password123! |
| Manager  | manager@example.com   | Password123! |
| Admin    | admin@example.com     | Password123! |

## API Endpoints

### Authentication
- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - Login and get JWT token
- `POST /api/auth/refresh` - Refresh JWT token
- `GET /api/auth/me` - Get current user profile

### H2 Console (Dev only)
- URL: http://localhost:8080/h2-console
- JDBC URL: `jdbc:h2:mem:moviebooking_db`
- Username: `sa`, Password: (empty)

## Design Patterns
1. **State Pattern** - Booking lifecycle management
2. **Observer Pattern** - Real-time seat availability via WebSocket
3. **Decorator Pattern** - F&B add-ons to bookings
4. **Facade Pattern** - Unified payment gateway interface
5. **Abstract Factory** - Theater type configurations
6. **Singleton** - Database connection pool manager
7. **Chain of Responsibility** - Booking validation pipeline
8. **Strategy Pattern** - Dynamic pricing algorithms

## Design Principles
1. Single Responsibility Principle (SRP)
2. Information Expert (GRASP)
3. Open-Closed Principle
4. Polymorphism (GRASP)
5. High Cohesion (GRASP)
6. Creator (GRASP)
7. Low Coupling (GRASP)
8. Controller (GRASP)

## Project Structure
```
src/main/java/com/moviebooking/
├── config/          # Security, WebSocket, Data initialization
├── controller/      # REST and MVC controllers
├── dto/             # Data transfer objects
├── entity/          # JPA entities and enums
├── exception/       # Custom exceptions and global handler
├── patterns/        # Design pattern implementations
├── repository/      # Spring Data JPA repositories
├── security/        # JWT authentication
├── service/         # Business logic
└── util/            # Utility classes
```
