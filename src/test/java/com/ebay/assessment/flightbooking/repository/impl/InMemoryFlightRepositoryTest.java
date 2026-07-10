package com.ebay.assessment.flightbooking.repository.impl;

import com.ebay.assessment.flightbooking.domain.Flight;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for InMemoryFlightRepository
 * Tests flight storage and retrieval operations.
 */
class InMemoryFlightRepositoryTest {

	private InMemoryFlightRepository repository;

	@BeforeEach
	void setUp() {
		repository = new InMemoryFlightRepository();
	}

	@Test
	void testSaveAndRetrieveFlight() {
		Flight flight = new Flight("FL001", 100);

		Flight savedFlight = repository.save(flight);

		assertNotNull(savedFlight);
		assertEquals("FL001", savedFlight.getFlightNumber());
		assertEquals(100, savedFlight.getCapacity());
	}

	@Test
	void testFindByFlightNumberReturnsOptional() {
		Flight flight = new Flight("FL001", 100);
		repository.save(flight);

		Optional<Flight> retrieved = repository.findByFlightNumber("FL001");

		assertTrue(retrieved.isPresent());
		assertEquals("FL001", retrieved.get().getFlightNumber());
	}

	@Test
	void testFindByNonExistentFlightNumberReturnsEmpty() {
		Optional<Flight> retrieved = repository.findByFlightNumber("NONEXISTENT");

		assertTrue(retrieved.isEmpty());
	}

	@Test
	void testMultipleFlightsSaved() {
		Flight flight1 = new Flight("FL001", 100);
		Flight flight2 = new Flight("FL002", 200);

		repository.save(flight1);
		repository.save(flight2);

		Optional<Flight> retrieved1 = repository.findByFlightNumber("FL001");
		Optional<Flight> retrieved2 = repository.findByFlightNumber("FL002");

		assertTrue(retrieved1.isPresent());
		assertTrue(retrieved2.isPresent());
		assertEquals(100, retrieved1.get().getCapacity());
		assertEquals(200, retrieved2.get().getCapacity());
	}

	@Test
	void testUpdateExistingFlight() {
		Flight flight = new Flight("FL001", 100);
		repository.save(flight);

		flight.reserveSeat();

		Optional<Flight> retrieved = repository.findByFlightNumber("FL001");

		assertTrue(retrieved.isPresent());
		assertEquals(99, retrieved.get().getAvailableSeats());
	}

	@Test
	void testFlightNumberCaseSensitivity() {
		Flight flight = new Flight("FL001", 100);
		repository.save(flight);

		Optional<Flight> upperCase = repository.findByFlightNumber("FL001");
		Optional<Flight> lowerCase = repository.findByFlightNumber("fl001");

		assertTrue(upperCase.isPresent());
		assertTrue(lowerCase.isEmpty());
	}
}
