package com.ebay.assessment.flightbooking.controller;

import com.ebay.assessment.flightbooking.dto.request.CreateFlightRequest;
import com.ebay.assessment.flightbooking.dto.response.FlightResponse;
import com.ebay.assessment.flightbooking.service.FlightService;

import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for Flight Management
 * Handles flight creation and retrieval operations.
 */
@RestController
@RequestMapping("/api/v1/flights")
public class FlightController {

	private static final Logger logger = LoggerFactory.getLogger(FlightController.class);

	private final FlightService flightService;

	public FlightController(final FlightService flightService) {
		this.flightService = flightService;
	}

	/**
	 * Creates a new flight with specified capacity.
	 * 
	 * @param request the flight creation request containing flight number and capacity
	 * @return ResponseEntity with 201 Created status and flight details
	 */
	@PostMapping
	public ResponseEntity<FlightResponse> createFlight(@Valid @RequestBody final CreateFlightRequest request) {

		logger.info("Creating flight request received: flightNumber={}", request.getFlightNumber());

		FlightResponse response = flightService.createFlight(request);

		logger.info("Flight created successfully: flightNumber={}, capacity={}", response.getFlightNumber(),
				response.getCapacity());

		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}
}