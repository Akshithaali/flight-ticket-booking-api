package com.ebay.assessment.flightbooking.mapper;

import com.ebay.assessment.flightbooking.domain.Booking;
import com.ebay.assessment.flightbooking.dto.response.BookingResponse;

import org.springframework.stereotype.Component;

@Component
public class BookingMapper {

	public BookingResponse toResponse(Booking booking) {

		return new BookingResponse(booking.bookingId(), booking.flightNumber(), booking.passengerName());
	}
}