package com.example.MovieBookingApplication.DTO;

import com.example.MovieBookingApplication.Entity.BookingStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class BookingDTO {
	private Integer numberOfSeats;
	private Double price;
	private LocalDateTime bookingTime;
	private BookingStatus bookingStatus;
	private List<String> seatNumbers;
	private Long userId;
	private Long showId;
}
