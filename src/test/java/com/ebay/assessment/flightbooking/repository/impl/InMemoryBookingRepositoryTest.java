package com.ebay.assessment.flightbooking.repository.impl;

import com.ebay.assessment.flightbooking.domain.Booking;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for InMemoryBookingRepository
 * Tests booking storage and duplicate detection.
 */
class InMemoryBookingRepositoryTest {

	private InMemoryBookingRepository repository;

	@BeforeEach
	void setUp() {
		repository = new InMemoryBookingRepository();
	}

	@Test
	void testSaveAndRetrieveBooking() {
		Booking booking = new Booking("BK-001", "FL001", "John Doe", LocalDateTime.now());

		Booking savedBooking = repository.save(booking);

		assertNotNull(savedBooking);
		assertEquals("BK-001", savedBooking.bookingId());
		assertEquals("FL001", savedBooking.flightNumber());
		assertEquals("John Doe", savedBooking.passengerName());
	}

	@Test
	void testExistsByFlightNumberAndPassengerNameTrue() {
		Booking booking = new Booking("BK-001", "FL001", "John Doe", LocalDateTime.now());
		repository.save(booking);

		boolean exists = repository.existsByFlightNumberAndPassengerName("FL001", "John Doe");

		assertTrue(exists);
	}

	@Test
	void testExistsByFlightNumberAndPassengerNameFalse() {
		boolean exists = repository.existsByFlightNumberAndPassengerName("FL001", "Jane Doe");

		assertFalse(exists);
	}

	@Test
	void testExistsByFlightNumberAndPassengerNameCaseInsensitive() {
		Booking booking = new Booking("BK-001", "FL001", "John Doe", LocalDateTime.now());
		repository.save(booking);

		boolean existsLower = repository.existsByFlightNumberAndPassengerName("FL001", "john doe");
		boolean existsUpper = repository.existsByFlightNumberAndPassengerName("FL001", "JOHN DOE");
		boolean existsMixed = repository.existsByFlightNumberAndPassengerName("FL001", "JoHn DoE");

		assertTrue(existsLower);
		assertTrue(existsUpper);
		assertTrue(existsMixed);
	}

	@Test
	void testMultipleBookingsSaved() {
		Booking booking1 = new Booking("BK-001", "FL001", "John Doe", LocalDateTime.now());
		Booking booking2 = new Booking("BK-002", "FL001", "Jane Smith", LocalDateTime.now());
		Booking booking3 = new Booking("BK-003", "FL002", "Bob Johnson", LocalDateTime.now());

		repository.save(booking1);
		repository.save(booking2);
		repository.save(booking3);

		assertTrue(repository.existsByFlightNumberAndPassengerName("FL001", "John Doe"));
		assertTrue(repository.existsByFlightNumberAndPassengerName("FL001", "Jane Smith"));
		assertTrue(repository.existsByFlightNumberAndPassengerName("FL002", "Bob Johnson"));
	}

	@Test
	void testDifferentFlightsSameName() {
		Booking booking1 = new Booking("BK-001", "FL001", "John Doe", LocalDateTime.now());
		Booking booking2 = new Booking("BK-002", "FL002", "John Doe", LocalDateTime.now());

		repository.save(booking1);
		repository.save(booking2);

		assertTrue(repository.existsByFlightNumberAndPassengerName("FL001", "John Doe"));
		assertTrue(repository.existsByFlightNumberAndPassengerName("FL002", "John Doe"));
	}

	@Test
	void testNonExistentCombination() {
		Booking booking = new Booking("BK-001", "FL001", "John Doe", LocalDateTime.now());
		repository.save(booking);

		boolean exists = repository.existsByFlightNumberAndPassengerName("FL002", "John Doe");

		assertFalse(exists);
	}
}
