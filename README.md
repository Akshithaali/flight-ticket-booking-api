# Flight Booking Application

## Overview

The **Flight Booking Application** is a Spring Boot REST API for managing flights and passenger bookings. It provides endpoints to create flights and book seats while enforcing business rules such as duplicate prevention, flight validation, and safe concurrent seat reservations.

The application uses an **in-memory data store** and follows a clean layered architecture with validation, centralized exception handling, and thread-safe booking operations.

---

# Features

## Flight Management

* Create flights with unique flight numbers.
* Validate flight capacity.
* Prevent duplicate flight creation.
* Store flight numbers in uppercase for consistency.

## Booking Management

* Create bookings for existing flights.
* Generate unique booking IDs.
* Prevent duplicate bookings for the same passenger on the same flight.
* Reject bookings for non-existent flights.
* Reject bookings when a flight has reached full capacity.

---

# Technology Stack

* Java
* Spring Boot
* Spring Web
* Maven
* ConcurrentHashMap (In-Memory Storage)
* AtomicInteger (Thread Safety)

---

# Project Architecture

The project follows a layered architecture to maintain separation of concerns.

```text
Controller
    │
    ▼
Service
    │
    ▼
Repository
    │
    ▼
In-Memory Storage
```

### Package Structure

```text
src/main/java
├── controller          # REST endpoints
├── service             # Business logic
├── repository          # Data access abstraction
├── repository/impl     # In-memory implementations
├── domain              # Business models
├── dto                 # Request/Response DTOs
├── mapper              # DTO ↔ Domain mapping
├── exception           # Custom exceptions & global handler
└── util                # Utility classes
```

---

# Concurrency & Thread Safety

One of the core requirements of this application is preventing **overbooking** during concurrent booking requests.

## Atomic Seat Counter

Each `Flight` maintains the number of booked seats using:

```java
private final AtomicInteger bookedSeats = new AtomicInteger(0);
```

Using `AtomicInteger` ensures that seat count updates are thread-safe.

## Synchronized Booking

Booking operations are synchronized on the corresponding flight object:

```java
synchronized (flight) {
    // validate seat availability
    // reserve seat
    // create booking
}
```

This guarantees that:

* Seat availability is checked atomically.
* Seat reservation happens safely.
* Race conditions are prevented.
* Flight capacity cannot be exceeded even under concurrent requests.

---

# Business Rules

## Flight Rules

* Flight number must be unique.
* Flight number is stored in uppercase.
* Capacity must be greater than `0`.
* Capacity must not exceed `1000`.
* Capacity must be numeric.

## Booking Rules

* Flight must exist before a booking can be created.
* A passenger can book the same flight only once.
* Passenger name comparison is case-insensitive.
* Bookings are rejected when no seats are available.

---

# API Endpoints

| Method | Endpoint                   | Description      |
| ------ | -------------------------- | ---------------- |
| POST   | `/api/v1/flights`          | Create a flight  |
| POST   | `/api/v1/flights/bookings` | Create a booking |

---

# Example Requests

## Create Flight

**POST** `/api/v1/flights`

```json
{
  "flightNumber": "AI101",
  "capacity": 150
}
```

### Success Response

```json
{
  "flightNumber": "AI101",
  "capacity": 150
}
```

---

## Create Booking

**POST** `/api/v1/flights/bookings`

```json
{
  "flightNumber": "AI101",
  "passengerName": "John Doe"
}
```

### Success Response

```json
{
    "bookingId": "BK-20260710-00006",
    "flightNumber": "AI101",
    "passengerName": "John Doe"
}
```

---

# Error Handling

The application provides centralized exception handling with meaningful error responses.

Examples include:

* Flight already exists
* Flight not found
* Flight is fully booked
* Duplicate booking
* Invalid flight capacity
* Validation failures

---

# Limitations

* Uses `ConcurrentHashMap` as an in-memory data store.
* Data is lost when the application restarts.
* No database integration.
* No booking cancellation functionality.
* No authentication or authorization.
* No persistence layer.

---

# Running the Application

## Prerequisites

* Java 17+ (or your project's required version)
* Maven

## Build

```bash
mvn clean install
```

## Run

```bash
mvn spring-boot:run
```

The application will start on:

```
http://localhost:8080
```

---

# Future Enhancements

* Database integration (PostgreSQL/MySQL)
* Booking cancellation
* Flight search APIs
* Authentication & authorization
* Seat selection
* Swagger/OpenAPI documentation
* Docker support
* Unit and integration tests

---

# Design Highlights

* Clean layered architecture
* Separation of concerns
* Thread-safe booking implementation
* Centralized exception handling
* Request validation
* DTO-based API design
* In-memory repository abstraction for easy migration to a database in the future
