package com.ebay.assessment.flightbooking.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CreateBookingRequest {

	@NotBlank(message = "Flight number is required")
	private String flightNumber;

	@NotBlank(message = "Passenger name is required")
    @Size(max = 100, message = "Passenger name cannot exceed 100 characters")
    private String passengerName;

    public CreateBookingRequest() {
    }

    public CreateBookingRequest(String passengerName) {
        this.passengerName = passengerName;
    }

    public String getPassengerName() {
        return passengerName;
    }

    public void setPassengerName(String passengerName) {
        this.passengerName = passengerName;
    }
	
    public String getFlightNumber() {
		return flightNumber;
	}

	public void setFlightNumber(String flightNumber) {
		this.flightNumber = flightNumber;
	}
}