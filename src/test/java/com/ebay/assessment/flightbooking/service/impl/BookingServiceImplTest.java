package com.ebay.assessment.flightbooking.service.impl;

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
import com.ebay.assessment.flightbooking.util.BookingIdGenerator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for BookingServiceImpl
 * Tests booking creation, validation, and concurrency scenarios.
 */
@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

	@Mock
	private FlightRepository flightRepository;

	@Mock
	private BookingRepository bookingRepository;

	@Mock
	private BookingMapper bookingMapper;

	@Mock
	private BookingIdGenerator bookingIdGenerator;

	@InjectMocks
	private BookingServiceImpl bookingService;

	private CreateBookingRequest validRequest;
	private Flight flight;

	@BeforeEach
	void setUp() {
		validRequest = new CreateBookingRequest();
		validRequest.setFlightNumber("FL001");
		validRequest.setPassengerName("John Doe");

		flight = new Flight("FL001", 100);
	}

	@Test
	void testCreateBookingSuccessfully() {
		when(flightRepository.findByFlightNumber("FL001")).thenReturn(Optional.of(flight));
		when(bookingRepository.existsByFlightNumberAndPassengerName("FL001", "John Doe")).thenReturn(false);
		when(bookingIdGenerator.generateBookingId()).thenReturn("BK-20260710-00001");

		Booking booking = new Booking("BK-20260710-00001", "FL001", "John Doe", LocalDateTime.now());
		BookingResponse response = new BookingResponse("BK-20260710-00001", "FL001", "John Doe");
		when(bookingMapper.toResponse(any(Booking.class))).thenReturn(response);

		BookingResponse result = bookingService.createBooking(validRequest);

		assertNotNull(result);
		assertEquals("BK-20260710-00001", result.getBookingId());
		assertEquals("FL001", result.getFlightNumber());
		assertEquals("John Doe", result.getPassengerName());

		verify(flightRepository, times(1)).findByFlightNumber("FL001");
		verify(bookingRepository, times(1)).save(any(Booking.class));
	}

	@Test
	void testBookingNonExistentFlightThrowsException() {
		when(flightRepository.findByFlightNumber("INVALID")).thenReturn(Optional.empty());

		CreateBookingRequest invalidRequest = new CreateBookingRequest();
		invalidRequest.setFlightNumber("INVALID");
		invalidRequest.setPassengerName("John Doe");

		assertThrows(FlightNotFoundException.class, () -> bookingService.createBooking(invalidRequest));
		verify(bookingRepository, never()).save(any(Booking.class));
	}

	@Test
	void testDuplicateBookingThrowsException() {
		when(flightRepository.findByFlightNumber("FL001")).thenReturn(Optional.of(flight));
		when(bookingRepository.existsByFlightNumberAndPassengerName("FL001", "John Doe")).thenReturn(true);

		assertThrows(DuplicateBookingException.class, () -> bookingService.createBooking(validRequest));
		verify(bookingRepository, never()).save(any(Booking.class));
	}

	@Test
	void testFullyBookedFlightThrowsException() {
		Flight fullyBooked = new Flight("FL002", 1);
		fullyBooked.reserveSeat();

		when(flightRepository.findByFlightNumber("FL002")).thenReturn(Optional.of(fullyBooked));
		when(bookingRepository.existsByFlightNumberAndPassengerName("FL002", "Jane Doe")).thenReturn(false);

		CreateBookingRequest request = new CreateBookingRequest();
		request.setFlightNumber("FL002");
		request.setPassengerName("Jane Doe");

		assertThrows(FlightFullyBookedException.class, () -> bookingService.createBooking(request));
		verify(bookingRepository, never()).save(any(Booking.class));
	}

	@Test
	void testConcurrentBookingsPreventOverbooking() throws InterruptedException {
		int capacity = 10;
		int threadCount = 20;
		Flight limitedFlight = new Flight("FL003", capacity);

		when(flightRepository.findByFlightNumber("FL003")).thenReturn(Optional.of(limitedFlight));
		when(bookingRepository.existsByFlightNumberAndPassengerName(anyString(), anyString())).thenReturn(false);
		when(bookingIdGenerator.generateBookingId()).thenReturn("BK-ID");

		BookingResponse response = new BookingResponse("BK-ID", "FL003", "Passenger");
		when(bookingMapper.toResponse(any(Booking.class))).thenReturn(response);

		ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
		CountDownLatch latch = new CountDownLatch(threadCount);
		AtomicInteger successCount = new AtomicInteger(0);
		AtomicInteger failureCount = new AtomicInteger(0);

		for (int i = 0; i < threadCount; i++) {
			int passengerId = i;
			executorService.submit(() -> {
				try {
					CreateBookingRequest request = new CreateBookingRequest();
					request.setFlightNumber("FL003");
					request.setPassengerName("Passenger " + passengerId);

					bookingService.createBooking(request);
					successCount.incrementAndGet();
				} catch (FlightFullyBookedException e) {
					failureCount.incrementAndGet();
				} finally {
					latch.countDown();
				}
			});
		}

		latch.await();
		executorService.shutdown();

		assertEquals(capacity, successCount.get(), "Only " + capacity + " bookings should succeed");
		assertEquals(threadCount - capacity, failureCount.get(), "Remaining bookings should fail");
		assertEquals(capacity, limitedFlight.getBookedSeats(), "Flight should have exactly " + capacity + " booked seats");
	}

	@Test
	void testNullFlightRepositoryThrowsException() {
		assertThrows(NullPointerException.class, () -> new BookingServiceImpl(null, bookingRepository,
				bookingMapper, bookingIdGenerator));
	}

	@Test
	void testNullBookingRepositoryThrowsException() {
		assertThrows(NullPointerException.class, () -> new BookingServiceImpl(flightRepository, null,
				bookingMapper, bookingIdGenerator));
	}

	@Test
	void testBookingWithLowerCaseFlightNumberNormalized() {
		CreateBookingRequest request = new CreateBookingRequest();
		request.setFlightNumber("fl001");
		request.setPassengerName("John Doe");

		// Repository contains flight with uppercase number
		when(flightRepository.findByFlightNumber("FL001")).thenReturn(Optional.of(flight));
		when(bookingRepository.existsByFlightNumberAndPassengerName("FL001", "John Doe")).thenReturn(false);
		when(bookingIdGenerator.generateBookingId()).thenReturn("BK-20260710-00001");

		Booking booking = new Booking("BK-20260710-00001", "FL001", "John Doe", LocalDateTime.now());
		BookingResponse response = new BookingResponse("BK-20260710-00001", "FL001", "John Doe");
		when(bookingMapper.toResponse(any(Booking.class))).thenReturn(response);

		BookingResponse result = bookingService.createBooking(request);

		assertNotNull(result);
		assertEquals("FL001", result.getFlightNumber());
		verify(flightRepository, times(1)).findByFlightNumber("FL001");
	}

	@Test
	void testBookingWithMixedCaseFlightNumberNormalized() {
		CreateBookingRequest request = new CreateBookingRequest();
		request.setFlightNumber("Fl001");
		request.setPassengerName("Jane Doe");

		when(flightRepository.findByFlightNumber("FL001")).thenReturn(Optional.of(flight));
		when(bookingRepository.existsByFlightNumberAndPassengerName("FL001", "Jane Doe")).thenReturn(false);
		when(bookingIdGenerator.generateBookingId()).thenReturn("BK-20260710-00002");

		Booking booking = new Booking("BK-20260710-00002", "FL001", "Jane Doe", LocalDateTime.now());
		BookingResponse response = new BookingResponse("BK-20260710-00002", "FL001", "Jane Doe");
		when(bookingMapper.toResponse(any(Booking.class))).thenReturn(response);

		BookingResponse result = bookingService.createBooking(request);

		assertNotNull(result);
		assertEquals("FL001", result.getFlightNumber());
		verify(flightRepository, times(1)).findByFlightNumber("FL001");
	}
}
