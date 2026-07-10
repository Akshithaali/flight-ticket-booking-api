package com.ebay.assessment.flightbooking.service.impl;

import com.ebay.assessment.flightbooking.domain.Flight;
import com.ebay.assessment.flightbooking.domain.exception.FlightAlreadyExistsException;
import com.ebay.assessment.flightbooking.dto.request.CreateFlightRequest;
import com.ebay.assessment.flightbooking.dto.response.FlightResponse;
import com.ebay.assessment.flightbooking.mapper.FlightMapper;
import com.ebay.assessment.flightbooking.repository.FlightRepository;
import com.ebay.assessment.flightbooking.service.FlightService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * Service implementation for Flight operations.
 * Handles flight creation with validation and duplicate prevention.
 */
@Service
public class FlightServiceImpl implements FlightService {

	private static final Logger logger = LoggerFactory.getLogger(FlightServiceImpl.class);
	private static final int MIN_CAPACITY = 1;
	private static final int MAX_CAPACITY = 1000;

	private final FlightRepository flightRepository;
	private final FlightMapper flightMapper;

	public FlightServiceImpl(FlightRepository flightRepository, FlightMapper flightMapper) {

		this.flightRepository = Objects.requireNonNull(flightRepository, "FlightRepository cannot be null");
		this.flightMapper = Objects.requireNonNull(flightMapper, "FlightMapper cannot be null");
	}

	/**
	 * Creates a new flight with the specified flight number and capacity.
	 * 
	 * @param request the flight creation request
	 * @return FlightResponse with flight details
	 * @throws FlightAlreadyExistsException if flight already exists
	 * @throws IllegalArgumentException if capacity is invalid
	 */
	@Override
	public FlightResponse createFlight(CreateFlightRequest request) {

		logger.debug("Creating flight with request: {}", request.getFlightNumber());

		String flightNumber = request.getFlightNumber().toUpperCase();

		flightRepository.findByFlightNumber(flightNumber).ifPresent(flight -> {
			logger.warn("Attempt to create duplicate flight: {}", flightNumber);
			throw new FlightAlreadyExistsException(flightNumber);
		});

		int capacity;
		try {
			capacity = Integer.parseInt(request.getCapacity().trim());
		} catch (NumberFormatException e) {
			logger.error("Invalid capacity format for flight {}: {}", flightNumber, request.getCapacity(), e);
			throw new IllegalArgumentException("Capacity must be a valid number", e);
		}

		if (capacity < MIN_CAPACITY) {
			logger.warn("Flight {} capacity below minimum: {}", flightNumber, capacity);
			throw new IllegalArgumentException("Flight capacity must be greater than zero");
		}

		if (capacity > MAX_CAPACITY) {
			logger.warn("Flight {} capacity exceeds maximum: {}", flightNumber, capacity);
			throw new IllegalArgumentException("Capacity cannot exceed 1000");
		}

		Flight flight = new Flight(flightNumber, capacity);
		flightRepository.save(flight);

		logger.info("Flight created successfully: flightNumber={}, capacity={}", flightNumber, capacity);

		return flightMapper.toResponse(flight);
	}
}