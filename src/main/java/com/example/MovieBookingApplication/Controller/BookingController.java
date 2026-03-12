package com.example.MovieBookingApplication.Controller;

import com.example.MovieBookingApplication.DTO.BookingDTO;
import com.example.MovieBookingApplication.Entity.Booking;
import com.example.MovieBookingApplication.Entity.BookingStatus;
import com.example.MovieBookingApplication.Entity.User;
import com.example.MovieBookingApplication.Repository.UserRepository;
import com.example.MovieBookingApplication.Service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/booking")
public class BookingController {

	@Autowired
	private BookingService bookingService;

	@Autowired
	private UserRepository userRepository; // ✅ added

	@PostMapping("/createbooking")
	public ResponseEntity<Booking> createBooking(
			@RequestBody BookingDTO bookingDTO,
			@AuthenticationPrincipal UserDetails userDetails) { // ✅ get user from JWT

		// ✅ always set userId from token — never rely on frontend
		User user = userRepository.findByUsername(userDetails.getUsername())
				.orElseThrow(() -> new RuntimeException("User not found"));
		bookingDTO.setUserId(user.getId());

		return ResponseEntity.ok(bookingService.createBooking(bookingDTO));
	}


	@GetMapping("/getuserbookings/{id}")
	public ResponseEntity<List<Booking>> getUserBooking(@PathVariable Long id) {
		return ResponseEntity.ok(bookingService.getUserBooking(id));
	}

	@GetMapping("/getshowbooking/{id}")
	public ResponseEntity<List<Booking>> getShowBooking(@PathVariable Long id) {
		return ResponseEntity.ok(bookingService.getShowBooking(id));
	}

	@PutMapping("/confirm/{id}")
	public ResponseEntity<Booking> confirmBooking(@PathVariable Long id) {
		return ResponseEntity.ok(bookingService.confirmBooking(id));
	}

	@PutMapping("/cancel/{id}")
	public ResponseEntity<Booking> cancelBooking(@PathVariable Long id) {
		return ResponseEntity.ok(bookingService.cancelBooking(id));
	}

	@GetMapping("/getbookingbystatus/{bookingStatus}")
	public ResponseEntity<List<Booking>> getBookingByStatus(@PathVariable BookingStatus bookingStatus) {
		return ResponseEntity.ok(bookingService.getBookingByStatus(bookingStatus));
	}
}