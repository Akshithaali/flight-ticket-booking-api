package com.ebay.assessment.flightbooking.dto.response;

public class BookingResponse {

	private String bookingId;
	private String flightNumber;
	private String passengerName;

	public BookingResponse() {
	}

	public BookingResponse(String bookingId, String flightNumber, String passengerName) {

		this.bookingId = bookingId;
		this.flightNumber = flightNumber;
		this.passengerName = passengerName;
	}

	public String getBookingId() {
		return bookingId;
	}

	public String getFlightNumber() {
		return flightNumber;
	}

	public String getPassengerName() {
		return passengerName;
	}

	public void setBookingId(String bookingId) {
		this.bookingId = bookingId;
	}

	public void setFlightNumber(String flightNumber) {
		this.flightNumber = flightNumber;
	}

	public void setPassengerName(String passengerName) {
		this.passengerName = passengerName;
	}
}