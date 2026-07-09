package com.ebay.assessment.flightbooking.domain.exception;

public class FlightNotFoundException extends RuntimeException {

    public FlightNotFoundException(String flightNumber) {
        super("Flight not found: " + flightNumber);
    }
}