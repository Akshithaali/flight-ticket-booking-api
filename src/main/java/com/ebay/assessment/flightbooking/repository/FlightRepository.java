package com.ebay.assessment.flightbooking.repository;

import com.ebay.assessment.flightbooking.domain.Flight;

import java.util.Optional;

public interface FlightRepository {

    Flight save(Flight flight);

    Optional<Flight> findByFlightNumber(String flightNumber);
}
