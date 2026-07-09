package com.ebay.assessment.flightbooking.repository.impl;

import com.ebay.assessment.flightbooking.domain.Booking;
import com.ebay.assessment.flightbooking.repository.BookingRepository;

import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryBookingRepository implements BookingRepository {

	private final Map<String, Booking> bookings = new ConcurrentHashMap<>();

	@Override
	public Booking save(Booking booking) {

		bookings.put(booking.bookingId(), booking);

		return booking;
	}

	@Override
	public boolean existsByFlightNumberAndPassengerName(String flightNumber, String passengerName) {

		return bookings.values().stream().anyMatch(booking -> booking.flightNumber().equals(flightNumber)
				&& booking.passengerName().equalsIgnoreCase(passengerName));
	}
}