package com.ebay.assessment.flightbooking.service.impl;

import com.ebay.assessment.flightbooking.domain.Flight;
import com.ebay.assessment.flightbooking.domain.exception.FlightAlreadyExistsException;
import com.ebay.assessment.flightbooking.dto.request.CreateFlightRequest;
import com.ebay.assessment.flightbooking.dto.response.FlightResponse;
import com.ebay.assessment.flightbooking.mapper.FlightMapper;
import com.ebay.assessment.flightbooking.repository.FlightRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for FlightServiceImpl
 * Tests flight creation, validation, and capacity constraints.
 */
@ExtendWith(MockitoExtension.class)
class FlightServiceImplTest {

	@Mock
	private FlightRepository flightRepository;

	@Mock
	private FlightMapper flightMapper;

	@InjectMocks
	private FlightServiceImpl flightService;

	private CreateFlightRequest validRequest;

	@BeforeEach
	void setUp() {
		validRequest = new CreateFlightRequest();
		validRequest.setFlightNumber("FL001");
		validRequest.setCapacity("100");
	}

	@Test
	void testCreateFlightSuccessfully() {
		when(flightRepository.findByFlightNumber("FL001")).thenReturn(Optional.empty());

		FlightResponse response = new FlightResponse("FL001", 100, 100);
		when(flightMapper.toResponse(any(Flight.class))).thenReturn(response);

		FlightResponse result = flightService.createFlight(validRequest);

		assertNotNull(result);
		assertEquals("FL001", result.getFlightNumber());
		assertEquals(100, result.getCapacity());
		assertEquals(100, result.getAvailableSeats());

		verify(flightRepository, times(1)).save(any(Flight.class));
	}

	@Test
	void testCreateDuplicateFlightThrowsException() {
		Flight existingFlight = new Flight("FL001", 100);
		when(flightRepository.findByFlightNumber("FL001")).thenReturn(Optional.of(existingFlight));

		assertThrows(FlightAlreadyExistsException.class, () -> flightService.createFlight(validRequest));
		verify(flightRepository, never()).save(any(Flight.class));
	}

	@Test
	void testFlightNumberNormalization() {
		CreateFlightRequest request = new CreateFlightRequest();
		request.setFlightNumber("fl001");
		request.setCapacity("50");

		when(flightRepository.findByFlightNumber("FL001")).thenReturn(Optional.empty());

		FlightResponse response = new FlightResponse("FL001", 50, 50);
		when(flightMapper.toResponse(any(Flight.class))).thenReturn(response);

		FlightResponse result = flightService.createFlight(request);

		assertNotNull(result);
		assertEquals("FL001", result.getFlightNumber());
		verify(flightRepository, times(1)).save(any(Flight.class));
	}

	@ParameterizedTest
	@ValueSource(strings = { "0", "-1", "-100" })
	void testCapacityBelowMinimumThrowsException(String capacity) {
		CreateFlightRequest request = new CreateFlightRequest();
		request.setFlightNumber("FL001");
		request.setCapacity(capacity);

		when(flightRepository.findByFlightNumber("FL001")).thenReturn(Optional.empty());

		assertThrows(IllegalArgumentException.class, () -> flightService.createFlight(request));
		verify(flightRepository, never()).save(any(Flight.class));
	}

	@ParameterizedTest
	@ValueSource(strings = { "1001", "5000", "9999" })
	void testCapacityAboveMaximumThrowsException(String capacity) {
		CreateFlightRequest request = new CreateFlightRequest();
		request.setFlightNumber("FL001");
		request.setCapacity(capacity);

		when(flightRepository.findByFlightNumber("FL001")).thenReturn(Optional.empty());

		assertThrows(IllegalArgumentException.class, () -> flightService.createFlight(request));
		verify(flightRepository, never()).save(any(Flight.class));
	}

	@ParameterizedTest
	@ValueSource(strings = { "abc", "12.5", "1e10", "" })
	void testInvalidCapacityFormatThrowsException(String capacity) {
		CreateFlightRequest request = new CreateFlightRequest();
		request.setFlightNumber("FL001");
		request.setCapacity(capacity);

		when(flightRepository.findByFlightNumber("FL001")).thenReturn(Optional.empty());

		assertThrows(IllegalArgumentException.class, () -> flightService.createFlight(request));
		verify(flightRepository, never()).save(any(Flight.class));
	}

	@Test
	void testValidCapacityBoundaryMin() {
		CreateFlightRequest request = new CreateFlightRequest();
		request.setFlightNumber("FL001");
		request.setCapacity("1");

		when(flightRepository.findByFlightNumber("FL001")).thenReturn(Optional.empty());

		FlightResponse response = new FlightResponse("FL001", 1, 1);
		when(flightMapper.toResponse(any(Flight.class))).thenReturn(response);

		FlightResponse result = flightService.createFlight(request);

		assertNotNull(result);
		assertEquals(1, result.getCapacity());
		verify(flightRepository, times(1)).save(any(Flight.class));
	}

	@Test
	void testValidCapacityBoundaryMax() {
		CreateFlightRequest request = new CreateFlightRequest();
		request.setFlightNumber("FL002");
		request.setCapacity("1000");

		when(flightRepository.findByFlightNumber("FL002")).thenReturn(Optional.empty());

		FlightResponse response = new FlightResponse("FL002", 1000, 1000);
		when(flightMapper.toResponse(any(Flight.class))).thenReturn(response);

		FlightResponse result = flightService.createFlight(request);

		assertNotNull(result);
		assertEquals(1000, result.getCapacity());
		verify(flightRepository, times(1)).save(any(Flight.class));
	}

	@Test
	void testCapacityWithWhitespace() {
		CreateFlightRequest request = new CreateFlightRequest();
		request.setFlightNumber("FL001");
		request.setCapacity("  100  ");

		when(flightRepository.findByFlightNumber("FL001")).thenReturn(Optional.empty());

		FlightResponse response = new FlightResponse("FL001", 100, 100);
		when(flightMapper.toResponse(any(Flight.class))).thenReturn(response);

		FlightResponse result = flightService.createFlight(request);

		assertNotNull(result);
		assertEquals(100, result.getCapacity());
	}

	@Test
	void testNullFlightRepositoryThrowsException() {
		assertThrows(NullPointerException.class, () -> new FlightServiceImpl(null, flightMapper));
	}

	@Test
	void testNullFlightMapperThrowsException() {
		assertThrows(NullPointerException.class, () -> new FlightServiceImpl(flightRepository, null));
	}
}
