## Prompts Used to develop the Application

## Iteration 1

Prompt:
Design and build a small Flight Ticket Booking REST API in Spring Boot. 
I had created a package com.ebay.assessment.flightbooking".

The requirements are:

1. A flight can be created with a flight number (string) and capacity (int).
2. A passenger can book a ticket by providing the flight number (string ) and passenger name (string).
3. Assume the client already knows the flight number, so flight search functionality is not needed.
4. The application should use in-memory storage only. No database is needed.
5. Overbooking should never be allowed.
6. No authentication or authorization is required.
7. No APIs are needed to retrieve bookings. Creating bookings is enough.

I'd like the project to be structured in a way that is clean, maintainable and easy to extend if more requirements are added later.

Please come up with a good project structure and implementation approach. I prefer to keep API contracts separate from domain models, use request/response DTOs, have dedicated domain objects for Flight and Booking and keep business logic out of the controllers.

Use repository interfaces with in-memory implementations backed by ConcurrentHashMap. Also think about exception handling, validation, mapping between models and DTOs and thread safety for the booking flow.

Start by designing the overall structure and then generate the initial implementation with the main APIs, domain models, repositories, services, DTOs and exception handling. The goal is not just to make it work, but to have a codebase that looks like something that could be discussed confidently during a code review.

Should be separated like layers, 
com.ebay.assessment.flightbooking.controller (BookingController and FlightController)
com.ebay.assessment.flightbooking.dto (request and response)
com.ebay.assessment.flightbooking.service (interface and implementation)
com.ebay.assessment.flightbooking.repository (interface and implementation)
com.ebay.assessment.flightbooking.domain ( flight , booking and domain exceptions)

Notes :
Add mapper classes for DTO/domain conversion.
Ensure thread safety during booking operations.
Use modern Java features where appropriate, such as ConcurrentHashMap, AtomicInteger, Optional, Streams, immutable collections, and constructor-based dependency injection.

Implement the complete end-to-end flow for:

POST /api/v1/flights
POST /api/v1/flights/bookings

Generate all required classes and wire the application together so it is runnable and follows clean coding practices."



## Iteration 2


The custom exceptions in the package 'com.ebay.assessment.flightbooking.domain.exception' are being thrown correctly for scenarios such as flight not found, flight already exists, flight fully booked, and duplicate booking.

However, while testing the APIs, these exceptions are resulting in 500 Internal Server Error responses because there is currently no global exception handling mechanism. The current 'GlobalExceptionHandler' class is empty.

Modify GlobalExceptionHandler.java to implement centralized exception handling using Spring Boot. Handle each custom exception and return appropriate HTTP status codes and meaningful error messages.
handle the following scenarios:


For the API POST /api/v1/flights, 


1. Flight already exists → 409 Conflict
2. Flight number is null, empty, or blank → 400 Bad Request
3. Capacity is null → 400 Bad Request
4. Capacity is below the minimum allowed value or exceeds the maximum allowed value → 400 Bad Request
5. Capacity must be a valid number → 400 Bad Request



For the API 'POST /api/v1/flights/bookings', 

1. Flight not found → 404 Not Found
2. Flight fully booked → 409 Conflict
3. Duplicate booking for the same passenger on the same flight → 409 Conflict
4. Flight number is null, empty, or blank → 400 Bad Request
5. Passenger name is null, empty, or blank → 400 Bad Request


Any request validation failure → 400 Bad Request

Use '@RestControllerAdvice' to implement global exception handling. Create dedicated @ExceptionHandler methods for all custom exceptions. Also handle validation-related exceptions such as MethodArgumentNotValidException, ConstraintViolationException, and IllegalArgumentException.

Return a consistent error response structure containing timestamp, status, error, message, and path. Include the request URI in the response and provide clear, user-friendly error messages.

Add a generic fallback handler for unexpected exceptions that returns a 500 Internal Server Error response with the message "An unexpected error occurred".


## Iteration 3

Currently, the booking ID is generated sequentially using an 'AtomicLong' initialized to 1000 within BookingServiceImpl.java.

Refactor this implementation by moving the booking ID generation logic out of the service layer into a dedicated utility class. Generate more realistic and user-friendly booking IDs instead of returning a plain sequential number.

Update BookingServiceImpl to use the new utility class through dependency injection and remove the existing AtomicLong sequence from the service. Ensure the solution remains thread-safe, generates unique booking IDs, and follows Spring Boot and clean code best practices.

## Iteration 3

Identified that flight numbers are currently case-sensitive when creating a booking. For example, if a flight is created as "AI101", a booking request using "ai101" does not work.

Update the application so that flight numbers are treated the same regardless of letter case during booking. Ensure booking creation, flight lookup, and duplicate booking checks work correctly whether the flight number is entered in uppercase, lowercase, or mixed case. Add or update test cases to cover this scenario.


## Iteration 4

Analyze the application completely. This application is for flight creation and ticket booking, where bookings are made using a known flight number and no flight search functionality is required.

Extract the requirements, constraints, and expected behaviors from the test details provided below and verify that the implementation satisfies them.

Specifically verify that:

1. Flight data and bookings are stored in memory only.
2. Overbooking is prevented, including under concurrent booking requests.
3. No unnecessary features have been implemented beyond the assignment scope (authentication, authorization, rate limiting, destination logic, booking retrieval APIs, etc.).
4. REST endpoints use appropriate HTTP methods, status codes, request validation, and exception handling.

Assess whether the application meets production-ready Java and Spring Boot standards. Review the overall design, code quality, validation, exception handling, concurrency handling, API design, maintainability, logging, and test coverage.

Implement improvements where appropriate, including adding or improving logging, increasing automated test coverage, strengthening validation, improving error handling, refactoring code where beneficial, and addressing any production-readiness concerns that are within the scope of the assignment. Do not add features that violate the stated constraints.

Provide a concise summary of:

* Verified requirements and constraints
* Improvements made
* Any remaining issues or limitations
* Optional enhancements that are outside the assignment scope


## Iteration 5

Modify the readme file. Cross-check the implementation and ensure the README accurately reflects the actual behavior of the application rather than making assumptions. Include all important scenarios, validations and expected outcomes so that the README can serve as both project documentation and a testing/reference guide for reviewers.


