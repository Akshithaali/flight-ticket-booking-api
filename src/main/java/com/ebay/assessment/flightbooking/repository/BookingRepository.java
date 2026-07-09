package com.ebay.assessment.flightbooking.repository;

import com.ebay.assessment.flightbooking.domain.Booking;

public interface BookingRepository {

    Booking save(Booking booking);
    
    boolean existsByFlightNumberAndPassengerName(
            String flightNumber,
            String passengerName);
}