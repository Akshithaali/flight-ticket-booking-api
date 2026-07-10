package com.ebay.assessment.flightbooking.domain;

import java.time.LocalDateTime;

public record Booking(String bookingId, String flightNumber, String passengerName, LocalDateTime bookedAt) {
}