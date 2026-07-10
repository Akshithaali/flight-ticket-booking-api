package com.ebay.assessment.flightbooking.service.impl;

import java.time.LocalDateTime;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import com.ebay.assessment.flightbooking.util.BookingIdGenerator;

/**
 * Service implementation for Booking operations.
 * Handles booking creation with thread-safe overbooking prevention.
 */
@Service
public class BookingServiceImpl implements BookingService {

	private static final Logger logger = LoggerFactory.getLogger(BookingServiceImpl.class);

	private final FlightRepository flightRepository;
	private final BookingRepository bookingRepository;
	private final BookingMapper bookingMapper;
	private final BookingIdGenerator bookingIdGenerator;

	public BookingServiceImpl(FlightRepository flightRepository, BookingRepository bookingRepository,
			BookingMapper bookingMapper, BookingIdGenerator bookingIdGenerator) {

		this.flightRepository = Objects.requireNonNull(flightRepository, "FlightRepository cannot be null");
		this.bookingRepository = Objects.requireNonNull(bookingRepository, "BookingRepository cannot be null");
		this.bookingMapper = Objects.requireNonNull(bookingMapper, "BookingMapper cannot be null");
		this.bookingIdGenerator = Objects.requireNonNull(bookingIdGenerator, "BookingIdGenerator cannot be null");
	}

	/**
	 * Creates a new booking for a passenger on a specified flight.
	 * Ensures thread-safe overbooking prevention and duplicate booking prevention.
	 * 
	 * @param request the booking creation request
	 * @return BookingResponse with booking details
	 * @throws FlightNotFoundException if flight does not exist
	 * @throws DuplicateBookingException if passenger already booked on this flight
	 * @throws FlightFullyBookedException if flight has no available seats
	 */
	@Override
	public BookingResponse createBooking(CreateBookingRequest request) {

		String flightNumber = request.getFlightNumber().toUpperCase();
		logger.debug("Processing booking request: flightNumber={}, passengerName={}", flightNumber,
				request.getPassengerName());

		Flight flight = flightRepository.findByFlightNumber(flightNumber)
				.orElseThrow(() -> {
					logger.warn("Flight not found for booking: {}", flightNumber);
					return new FlightNotFoundException(flightNumber);
				});

		/*
		 * Prevent overbooking by making availability check + seat reservation atomic.
		 * Synchronize on flight object to ensure thread-safe seat reservation.
		 */
		synchronized (flight) {

			if (bookingRepository.existsByFlightNumberAndPassengerName(flight.getFlightNumber(),
					request.getPassengerName())) {

				logger.warn("Duplicate booking attempt: passengerName={}, flightNumber={}", request.getPassengerName(),
						flight.getFlightNumber());
				throw new DuplicateBookingException(request.getPassengerName(), flight.getFlightNumber());
			}

			if (!flight.hasAvailableSeats()) {
				logger.warn("Flight fully booked, booking rejected: flightNumber={}", flight.getFlightNumber());
				throw new FlightFullyBookedException(flight.getFlightNumber());
			}

			flight.reserveSeat();

			String bookingId = bookingIdGenerator.generateBookingId();
			Booking booking = new Booking(bookingId, flight.getFlightNumber(), request.getPassengerName(),
					LocalDateTime.now());

			bookingRepository.save(booking);

			logger.info("Booking created successfully: bookingId={}, flightNumber={}, passengerName={}, availableSeats={}",
					bookingId, flight.getFlightNumber(), request.getPassengerName(), flight.getAvailableSeats());

			return bookingMapper.toResponse(booking);
		}
	}
}