package com.ebay.assessment.flightbooking.service;

import com.ebay.assessment.flightbooking.dto.request.CreateBookingRequest;
import com.ebay.assessment.flightbooking.dto.response.BookingResponse;

public interface BookingService {
    
    BookingResponse createBooking(
            CreateBookingRequest request);
}