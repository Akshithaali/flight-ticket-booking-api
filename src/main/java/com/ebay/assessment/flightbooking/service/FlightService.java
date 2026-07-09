package com.ebay.assessment.flightbooking.service;

import com.ebay.assessment.flightbooking.dto.request.CreateFlightRequest;
import com.ebay.assessment.flightbooking.dto.response.FlightResponse;

public interface FlightService {

    FlightResponse createFlight(CreateFlightRequest request);
}