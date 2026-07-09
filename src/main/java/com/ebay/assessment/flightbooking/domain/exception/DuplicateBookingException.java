package com.ebay.assessment.flightbooking.domain.exception;

public class DuplicateBookingException extends RuntimeException {

	public DuplicateBookingException(String passengerName, String flightNumber) {

		super("Passenger '" + passengerName + "' already has a booking for flight " + flightNumber);
	}
}