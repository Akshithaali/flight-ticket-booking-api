package com.ebay.assessment.flightbooking.dto.response;

public class FlightResponse {

	private String flightNumber;
	private int capacity;
	private int availableSeats;

	public FlightResponse() {
	}

	public FlightResponse(String flightNumber, int capacity, int availableSeats) {

		this.flightNumber = flightNumber;
		this.capacity = capacity;
		this.availableSeats = availableSeats;
	}

	public String getFlightNumber() {
		return flightNumber;
	}

	public int getCapacity() {
		return capacity;
	}

	public int getAvailableSeats() {
		return availableSeats;
	}

	public void setFlightNumber(String flightNumber) {
		this.flightNumber = flightNumber;
	}

	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}

	public void setAvailableSeats(int availableSeats) {
		this.availableSeats = availableSeats;
	}
}