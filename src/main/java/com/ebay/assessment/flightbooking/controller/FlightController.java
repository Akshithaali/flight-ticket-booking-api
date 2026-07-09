package com.ebay.assessment.flightbooking.controller;

import com.ebay.assessment.flightbooking.dto.request.CreateFlightRequest;
import com.ebay.assessment.flightbooking.dto.response.FlightResponse;
import com.ebay.assessment.flightbooking.service.FlightService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/flights")
public class FlightController {

	private final FlightService flightService;

	public FlightController(final FlightService flightService) {
		this.flightService = flightService;
	}

	@PostMapping
	public ResponseEntity<FlightResponse> createFlight(@Valid @RequestBody final CreateFlightRequest request) {

		FlightResponse response = flightService.createFlight(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}
}