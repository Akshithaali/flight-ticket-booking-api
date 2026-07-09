package com.ebay.assessment.flightbooking.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CreateFlightRequest {

	@NotBlank(message = "Flight number is required")
	@Size(max = 20, message = "Flight number cannot exceed 20 characters")
	private String flightNumber;

	@NotBlank(message = "Capacity is required")
	private String capacity;

	public CreateFlightRequest() {
	}

	public CreateFlightRequest(String flightNumber, String capacity) {
		this.flightNumber = flightNumber;
		this.capacity = capacity;
	}

	public String getFlightNumber() {
		return flightNumber;
	}

	public void setFlightNumber(String flightNumber) {
		this.flightNumber = flightNumber;
	}

	public String getCapacity() {
		return capacity;
	}

	public void setCapacity(String capacity) {
		this.capacity = capacity;
	}
}