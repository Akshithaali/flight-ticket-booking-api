package com.ebay.assessment.flightbooking.service;

import com.ebay.assessment.flightbooking.dto.request.CreateFlightRequest;
import com.ebay.assessment.flightbooking.dto.response.FlightResponse;

/**
 * Service interface for Flight operations.
 * Defines business logic operations for flight management.
 */
public interface FlightService {

	/**
	 * Creates a new flight with the specified details.
	 * 
	 * @param request the flight creation request containing flight number and capacity
	 * @return FlightResponse containing the created flight details
	 * @throws com.ebay.assessment.flightbooking.domain.exception.FlightAlreadyExistsException if flight already exists
	 * @throws IllegalArgumentException if capacity is invalid
	 */
	FlightResponse createFlight(CreateFlightRequest request);
}