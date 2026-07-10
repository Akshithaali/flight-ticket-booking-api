package com.ebay.assessment.flightbooking.controller;

import com.ebay.assessment.flightbooking.dto.request.CreateBookingRequest;
import com.ebay.assessment.flightbooking.dto.response.BookingResponse;
import com.ebay.assessment.flightbooking.service.BookingService;

import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for Booking Management
 * Handles flight booking operations.
 */
@RestController
@RequestMapping("/api/v1/flights")
public class BookingController {

	private static final Logger logger = LoggerFactory.getLogger(BookingController.class);

	private final BookingService bookingService;

	public BookingController(final BookingService bookingService) {
		this.bookingService = bookingService;
	}

	/**
	 * Creates a new booking for a passenger on a specified flight.
	 * 
	 * @param request the booking creation request containing flight number and passenger name
	 * @return ResponseEntity with 201 Created status and booking details
	 */
	@PostMapping("/bookings")
	public ResponseEntity<BookingResponse> createBooking(@Valid @RequestBody final CreateBookingRequest request) {

		logger.info("Booking request received: flightNumber={}, passengerName={}", request.getFlightNumber(),
				request.getPassengerName());

		BookingResponse response = bookingService.createBooking(request);

		logger.info("Booking created successfully: bookingId={}, flightNumber={}, passengerName={}", 
				response.getBookingId(), response.getFlightNumber(), response.getPassengerName());

		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}
}