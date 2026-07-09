package com.ebay.assessment.flightbooking.service.impl;

import com.ebay.assessment.flightbooking.domain.Flight;
import com.ebay.assessment.flightbooking.domain.exception.FlightAlreadyExistsException;
import com.ebay.assessment.flightbooking.dto.request.CreateFlightRequest;
import com.ebay.assessment.flightbooking.dto.response.FlightResponse;
import com.ebay.assessment.flightbooking.mapper.FlightMapper;
import com.ebay.assessment.flightbooking.repository.FlightRepository;
import com.ebay.assessment.flightbooking.service.FlightService;

import org.springframework.stereotype.Service;

@Service
public class FlightServiceImpl implements FlightService {

	private final FlightRepository flightRepository;
	private final FlightMapper flightMapper;

	public FlightServiceImpl(FlightRepository flightRepository, FlightMapper flightMapper) {

		this.flightRepository = flightRepository;
		this.flightMapper = flightMapper;
	}

	@Override
	public FlightResponse createFlight(CreateFlightRequest request) {

		String flightNumber = request.getFlightNumber().toUpperCase();

		flightRepository.findByFlightNumber(flightNumber).ifPresent(flight -> {
			throw new FlightAlreadyExistsException(flightNumber);
		});

		int capacity;
		try {
			capacity = Integer.parseInt(request.getCapacity().trim());
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("Capacity must be a valid number");
		}

		if (capacity < 1) {
			throw new IllegalArgumentException("Flight capacity must be greater than zero");
		}
		if (capacity > 1000) {
			throw new IllegalArgumentException("Capacity cannot exceed 1000");
		}

		Flight flight = new Flight(flightNumber, capacity);

		flightRepository.save(flight);

		return flightMapper.toResponse(flight);
	}
}