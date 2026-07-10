package com.ebay.assessment.flightbooking.util;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Utility component for generating unique, thread-safe, user-friendly booking IDs.
 * 
 * Booking ID format: BK-YYYYMMDD-XXXXX
 * Example: BK-20260710-00001
 * 
 * This ensures:
 * - Readability and professional appearance
 * - Temporal context (date of booking)
 * - Sequential uniqueness within each day
 * - Thread-safety through AtomicLong
 */
@Component
public class BookingIdGenerator {

	private final AtomicLong dailySequence = new AtomicLong(0);
	private volatile LocalDate lastSequenceDate;

	/**
	 * Generates a unique booking ID in format: BK-YYYYMMDD-XXXXX
	 * 
	 * @return a unique, formatted booking ID
	 */
	public String generateBookingId() {
		LocalDate today = LocalDate.now();
		
		// Reset sequence counter if day has changed
		synchronized (this) {
			if (lastSequenceDate == null || !lastSequenceDate.equals(today)) {
				dailySequence.set(0);
				lastSequenceDate = today;
			}
		}
		
		long sequence = dailySequence.incrementAndGet();
		String dateStr = today.format(DateTimeFormatter.BASIC_ISO_DATE);
		
		return String.format("BK-%s-%05d", dateStr, sequence);
	}
}
