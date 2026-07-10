package com.ebay.assessment.flightbooking.controller;

import com.ebay.assessment.flightbooking.dto.request.CreateBookingRequest;
import com.ebay.assessment.flightbooking.dto.response.BookingResponse;
import com.ebay.assessment.flightbooking.service.BookingService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for BookingController
 * Tests booking creation endpoint and HTTP responses.
 */
@ExtendWith(MockitoExtension.class)
class BookingControllerTest {

	@Mock
	private BookingService bookingService;

	@InjectMocks
	private BookingController bookingController;

	@Test
	void testCreateBookingReturnsCreatedStatus() {
		CreateBookingRequest request = new CreateBookingRequest();
		request.setFlightNumber("FL001");
		request.setPassengerName("John Doe");

		BookingResponse response = new BookingResponse("BK-20260710-00001", "FL001", "John Doe");

		when(bookingService.createBooking(any(CreateBookingRequest.class))).thenReturn(response);

		ResponseEntity<BookingResponse> result = bookingController.createBooking(request);

		assertEquals(HttpStatus.CREATED, result.getStatusCode());
		assertNotNull(result.getBody());
		assertEquals("BK-20260710-00001", result.getBody().getBookingId());
		assertEquals("FL001", result.getBody().getFlightNumber());
		assertEquals("John Doe", result.getBody().getPassengerName());

		verify(bookingService, times(1)).createBooking(any(CreateBookingRequest.class));
	}

	@Test
	void testCreateBookingWithDifferentPassengers() {
		CreateBookingRequest request = new CreateBookingRequest();
		request.setFlightNumber("FL002");
		request.setPassengerName("Jane Smith");

		BookingResponse response = new BookingResponse("BK-20260710-00002", "FL002", "Jane Smith");

		when(bookingService.createBooking(any(CreateBookingRequest.class))).thenReturn(response);

		ResponseEntity<BookingResponse> result = bookingController.createBooking(request);

		assertEquals(HttpStatus.CREATED, result.getStatusCode());
		assertEquals("Jane Smith", result.getBody().getPassengerName());
	}
}
