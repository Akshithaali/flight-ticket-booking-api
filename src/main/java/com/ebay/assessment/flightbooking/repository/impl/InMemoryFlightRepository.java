package com.ebay.assessment.flightbooking.repository.impl;

import com.ebay.assessment.flightbooking.domain.Flight;
import com.ebay.assessment.flightbooking.repository.FlightRepository;

import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryFlightRepository implements FlightRepository {

    private final Map<String, Flight> flights = new ConcurrentHashMap<>();

    @Override
    public Flight save(Flight flight) {

        flights.put(
                flight.getFlightNumber(),
                flight);

        return flight;
    }

    @Override
    public Optional<Flight> findByFlightNumber(String flightNumber) {

        return Optional.ofNullable(
                flights.get(flightNumber));
    }
}