package com.ebay.assessment.flightbooking.controller;

import com.ebay.assessment.flightbooking.dto.request.CreateBookingRequest;
import com.ebay.assessment.flightbooking.dto.response.BookingResponse;
import com.ebay.assessment.flightbooking.service.BookingService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/flights")
public class BookingController {

	private final BookingService bookingService;

	public BookingController(final BookingService bookingService) {
		this.bookingService = bookingService;
	}

	@PostMapping("/bookings")
	public ResponseEntity<BookingResponse> createBooking(@Valid @RequestBody final CreateBookingRequest request) {

		BookingResponse response = bookingService.createBooking(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}
}