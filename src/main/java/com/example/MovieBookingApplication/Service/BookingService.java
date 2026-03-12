package com.example.MovieBookingApplication.Service;

import com.example.MovieBookingApplication.DTO.BookingDTO;
import com.example.MovieBookingApplication.Entity.Booking;
import com.example.MovieBookingApplication.Entity.BookingStatus;
import com.example.MovieBookingApplication.Entity.Show;
import com.example.MovieBookingApplication.Entity.User;
import com.example.MovieBookingApplication.Repository.BookingRepository;
import com.example.MovieBookingApplication.Repository.ShowRepository;
import com.example.MovieBookingApplication.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class BookingService {

	@Autowired
	private BookingRepository bookingRepository;

	@Autowired
	private ShowRepository showRepository;

	@Autowired
	private UserRepository userRepository;

	public Booking createBooking(BookingDTO bookingDTO){
		Show show = showRepository.findById(bookingDTO.getShowId())
				.orElseThrow(() -> new RuntimeException("Show not found"));
		if(!isSeatsAvailable(show.getId(), bookingDTO.getNumberOfSeats())){
			throw new RuntimeException("Not enough seats are available");
		}
		if(bookingDTO.getSeatNumbers().size() != bookingDTO.getNumberOfSeats()){
			throw new RuntimeException("seat Numbers and Number of Seats must be equal");
		}
		validateDuplicateSeats(show.getId(), bookingDTO.getSeatNumbers());

		User user = userRepository.findById(bookingDTO.getUserId())
				.orElseThrow(() -> new RuntimeException("User not found"));

		Booking booking = new Booking();
		booking.setUser(user);
		booking.setShow(show);
		booking.setNumberOfSeats(bookingDTO.getNumberOfSeats());
		booking.setSeatNumbers(bookingDTO.getSeatNumbers());
		booking.setPrice(calculateTotalAmount(show.getPrice(), bookingDTO.getNumberOfSeats()));
		booking.setBookingTime(LocalDateTime.now());
		booking.setBookingStatus(BookingStatus.PENDING);

		return bookingRepository.save(booking);
	}

	public List<Booking> getUserBooking(Long userid){
		return bookingRepository.findByUserId(userid);
	}

	public List<Booking> getShowBooking(Long showid){
		return bookingRepository.findByShowId(showid);
	}

	public Booking confirmBooking(Long bookingid){
		Booking booking = bookingRepository.findById(bookingid)
				.orElseThrow(() -> new RuntimeException("Booking not found"));
		if(booking.getBookingStatus() != BookingStatus.PENDING) {
			throw new RuntimeException("Booking is not in pending state");
		}
		booking.setBookingStatus(BookingStatus.CONFIRMED);
		return bookingRepository.save(booking);
	}

	// ✅ Admin cancel — no time restriction, can cancel anytime
	public Booking cancelBooking(Long bookingid){
		Booking booking = bookingRepository.findById(bookingid)
				.orElseThrow(() -> new RuntimeException("Booking not found"));

		if(booking.getBookingStatus() == BookingStatus.CANCELLED){
			throw new RuntimeException("Booking already been cancelled");
		}

		booking.setBookingStatus(BookingStatus.CANCELLED);
		return bookingRepository.save(booking);
	}

	// ✅ User cancel — enforces 2-hour deadline before show
	public Booking cancelBookingByUser(Long bookingid){
		Booking booking = bookingRepository.findById(bookingid)
				.orElseThrow(() -> new RuntimeException("Booking not found"));

		if(booking.getBookingStatus() == BookingStatus.CANCELLED){
			throw new RuntimeException("Booking already been cancelled");
		}

		LocalDateTime showTime = booking.getShow().getShowTime();
		LocalDateTime deadlineTime = showTime.minusHours(2);

		if(LocalDateTime.now().isAfter(deadlineTime)){
			throw new RuntimeException("Cannot cancel the booking — less than 2 hours before show");
		}

		booking.setBookingStatus(BookingStatus.CANCELLED);
		return bookingRepository.save(booking);
	}

	public List<Booking> getBookingByStatus(BookingStatus bookingStatus){
		return bookingRepository.findByBookingStatus(bookingStatus);
	}

	public boolean isSeatsAvailable(Long showid, Integer numberOfSeats){
		Show show = showRepository.findById(showid)
				.orElseThrow(() -> new RuntimeException("Show not found"));
		int bookedSeats = show.getBookings().stream()
				.filter(booking -> booking.getBookingStatus() != BookingStatus.CANCELLED)
				.mapToInt(Booking::getNumberOfSeats)
				.sum();
		return (show.getTheater().getTheaterCapacity() - bookedSeats) >= numberOfSeats;
	}

	public void validateDuplicateSeats(Long showId, List<String> seatNumbers){
		Show show = showRepository.findById(showId)
				.orElseThrow(() -> new RuntimeException("Show not found"));
		Set<String> occupiedSeats = show.getBookings().stream()
				.filter(b -> b.getBookingStatus() != BookingStatus.CANCELLED)
				.flatMap(b -> b.getSeatNumbers().stream())
				.collect(Collectors.toSet());

		List<String> duplicateSeats = seatNumbers.stream()
				.filter(occupiedSeats::contains)
				.collect(Collectors.toList());
		if(!duplicateSeats.isEmpty()){
			throw new RuntimeException("Seats are already Booked");
		}
	}

	public Double calculateTotalAmount(Double price, Integer numberOfSeats){
		return price * numberOfSeats;
	}
}