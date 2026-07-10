package com.ebay.assessment.flightbooking.mapper;

import com.ebay.assessment.flightbooking.domain.Flight;
import com.ebay.assessment.flightbooking.dto.response.FlightResponse;

import org.springframework.stereotype.Component;

@Component
public class FlightMapper {

	public FlightResponse toResponse(Flight flight) {

		return new FlightResponse(flight.getFlightNumber(), flight.getCapacity(), flight.getAvailableSeats());
	}
}