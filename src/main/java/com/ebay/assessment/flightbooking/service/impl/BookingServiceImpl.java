package com.ebay.assessment.flightbooking.service.impl;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Service;

import com.ebay.assessment.flightbooking.domain.Booking;
import com.ebay.assessment.flightbooking.domain.Flight;
import com.ebay.assessment.flightbooking.domain.exception.DuplicateBookingException;
import com.ebay.assessment.flightbooking.domain.exception.FlightFullyBookedException;
import com.ebay.assessment.flightbooking.domain.exception.FlightNotFoundException;
import com.ebay.assessment.flightbooking.dto.request.CreateBookingRequest;
import com.ebay.assessment.flightbooking.dto.response.BookingResponse;
import com.ebay.assessment.flightbooking.mapper.BookingMapper;
import com.ebay.assessment.flightbooking.repository.BookingRepository;
import com.ebay.assessment.flightbooking.repository.FlightRepository;
import com.ebay.assessment.flightbooking.service.BookingService;

@Service
public class BookingServiceImpl implements BookingService {

    private final FlightRepository flightRepository;
    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;

    private final AtomicLong bookingSequence = new AtomicLong(1000);

    public BookingServiceImpl(
            FlightRepository flightRepository,
            BookingRepository bookingRepository,
            BookingMapper bookingMapper) {

        this.flightRepository = flightRepository;
        this.bookingRepository = bookingRepository;
        this.bookingMapper = bookingMapper;
    }

    @Override
    public BookingResponse createBooking(CreateBookingRequest request) {

        Flight flight = flightRepository.findByFlightNumber(request.getFlightNumber())
                .orElseThrow(() -> new FlightNotFoundException(request.getFlightNumber()));

        /*
         * Prevent overbooking by making availability check + seat reservation atomic.
         */
        synchronized (flight) {

            if (bookingRepository.existsByFlightNumberAndPassengerName(
                    flight.getFlightNumber(),
                    request.getPassengerName())) {

                throw new DuplicateBookingException(
                        request.getPassengerName(),
                        flight.getFlightNumber());
            }

            if (!flight.hasAvailableSeats()) {
                throw new FlightFullyBookedException(
                        flight.getFlightNumber());
            }

            flight.reserveSeat();

            Booking booking = new Booking(
                    String.valueOf(bookingSequence.incrementAndGet()),
                    flight.getFlightNumber(),
                    request.getPassengerName(),
                    LocalDateTime.now()
            );

            bookingRepository.save(booking);

            return bookingMapper.toResponse(booking);
        }
    }
}