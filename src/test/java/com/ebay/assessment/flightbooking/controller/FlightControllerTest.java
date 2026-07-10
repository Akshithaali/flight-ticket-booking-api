package com.ebay.assessment.flightbooking.controller;

import com.ebay.assessment.flightbooking.dto.request.CreateFlightRequest;
import com.ebay.assessment.flightbooking.dto.response.FlightResponse;
import com.ebay.assessment.flightbooking.service.FlightService;

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
 * Unit tests for FlightController
 * Tests flight creation endpoint and HTTP responses.
 */
@ExtendWith(MockitoExtension.class)
class FlightControllerTest {

	@Mock
	private FlightService flightService;

	@InjectMocks
	private FlightController flightController;

	@Test
	void testCreateFlightReturnsCreatedStatus() {
		CreateFlightRequest request = new CreateFlightRequest("FL001", "100");
		FlightResponse response = new FlightResponse("FL001", 100, 100);

		when(flightService.createFlight(any(CreateFlightRequest.class))).thenReturn(response);

		ResponseEntity<FlightResponse> result = flightController.createFlight(request);

		assertEquals(HttpStatus.CREATED, result.getStatusCode());
		assertNotNull(result.getBody());
		assertEquals("FL001", result.getBody().getFlightNumber());
		assertEquals(100, result.getBody().getCapacity());

		verify(flightService, times(1)).createFlight(any(CreateFlightRequest.class));
	}

	@Test
	void testCreateFlightWithDifferentCapacities() {
		CreateFlightRequest request = new CreateFlightRequest("FL002", "500");
		FlightResponse response = new FlightResponse("FL002", 500, 500);

		when(flightService.createFlight(any(CreateFlightRequest.class))).thenReturn(response);

		ResponseEntity<FlightResponse> result = flightController.createFlight(request);

		assertEquals(HttpStatus.CREATED, result.getStatusCode());
		assertEquals(500, result.getBody().getCapacity());
	}
}
