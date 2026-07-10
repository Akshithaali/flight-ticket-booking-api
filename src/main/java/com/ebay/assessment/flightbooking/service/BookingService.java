package com.ebay.assessment.flightbooking.service;

import com.ebay.assessment.flightbooking.dto.request.CreateBookingRequest;
import com.ebay.assessment.flightbooking.dto.response.BookingResponse;

/**
 * Service interface for Booking operations.
 * Defines business logic operations for flight bookings with overbooking prevention.
 */
public interface BookingService {

	/**
	 * Creates a new booking for a passenger on a specified flight.
	 * Ensures thread-safe overbooking prevention and duplicate booking prevention.
	 * 
	 * @param request the booking creation request containing flight number and passenger name
	 * @return BookingResponse containing the created booking details
	 * @throws com.ebay.assessment.flightbooking.domain.exception.FlightNotFoundException if flight does not exist
	 * @throws com.ebay.assessment.flightbooking.domain.exception.FlightFullyBookedException if flight has no available seats
	 * @throws com.ebay.assessment.flightbooking.domain.exception.DuplicateBookingException if passenger already booked on this flight
	 */
	BookingResponse createBooking(CreateBookingRequest request);
}