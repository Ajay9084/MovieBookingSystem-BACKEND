package com.example.MovieBookingApplication.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
public class Booking {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
    private Integer numberOfSeats;
	private Double price;
	private LocalDateTime bookingTime;
	private BookingStatus bookingStatus;

	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "booking_seat_numbers")
	private List<String> seatNumbers;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "user_id", nullable = false)
	@JsonIgnore
	private User user;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "show_id", nullable = false)
	private Show show;
}
