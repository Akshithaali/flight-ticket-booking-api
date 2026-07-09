package com.ebay.assessment.flightbooking.domain;

import java.util.concurrent.atomic.AtomicInteger;

public class Flight {

    private final String flightNumber;
    private final int capacity;
    private final AtomicInteger bookedSeats;

    public Flight(String flightNumber, int capacity) {
        this.flightNumber = flightNumber;
        this.capacity = capacity;
        this.bookedSeats = new AtomicInteger(0);
    }

    public String getFlightNumber() {
        return flightNumber;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getBookedSeats() {
        return bookedSeats.get();
    }

    public int getAvailableSeats() {
        return capacity - bookedSeats.get();
    }

    public boolean hasAvailableSeats() {
        return bookedSeats.get() < capacity;
    }

    public void reserveSeat() {
        if (!hasAvailableSeats()) {
            throw new IllegalStateException(
                    "No seats available for flight " + flightNumber);
        }

        bookedSeats.incrementAndGet();
    }
}