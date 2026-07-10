package com.ebay.assessment.flightbooking.domain;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Domain object representing a Flight.
 * Manages flight information and seat reservation with thread-safe operations.
 */
public class Flight {

	private final String flightNumber;
	private final int capacity;
	private final AtomicInteger bookedSeats;

	/**
	 * Constructs a Flight with specified flight number and capacity.
	 * 
	 * @param flightNumber the unique flight identifier
	 * @param capacity the total number of available seats
	 */
	public Flight(String flightNumber, int capacity) {
		this.flightNumber = flightNumber;
		this.capacity = capacity;
		this.bookedSeats = new AtomicInteger(0);
	}

	/**
	 * Gets the flight number.
	 * 
	 * @return the flight number
	 */
	public String getFlightNumber() {
		return flightNumber;
	}

	/**
	 * Gets the total capacity of the flight.
	 * 
	 * @return the total capacity
	 */
	public int getCapacity() {
		return capacity;
	}

	/**
	 * Gets the number of booked seats.
	 * 
	 * @return the number of booked seats
	 */
	public int getBookedSeats() {
		return bookedSeats.get();
	}

	/**
	 * Gets the number of available seats.
	 * 
	 * @return the number of available seats
	 */
	public int getAvailableSeats() {
		return capacity - bookedSeats.get();
	}

	/**
	 * Checks if the flight has available seats for booking.
	 * 
	 * @return true if seats are available, false otherwise
	 */
	public boolean hasAvailableSeats() {
		return bookedSeats.get() < capacity;
	}

	/**
	 * Reserves a seat on the flight. Throws exception if no seats available.
	 * This operation is thread-safe through AtomicInteger.
	 * 
	 * @throws IllegalStateException if no seats available for the flight
	 */
	public void reserveSeat() {
		if (!hasAvailableSeats()) {
			throw new IllegalStateException("No seats available for flight " + flightNumber);
		}

		bookedSeats.incrementAndGet();
	}

	/**
	 * String representation of the Flight.
	 * 
	 * @return string representation with flight details
	 */
	@Override
	public String toString() {
		return "Flight{" + "flightNumber='" + flightNumber + '\'' + ", capacity=" + capacity + ", bookedSeats="
				+ bookedSeats.get() + ", availableSeats=" + getAvailableSeats() + '}';
	}
}