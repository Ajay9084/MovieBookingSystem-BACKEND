package com.example.MovieBookingApplication.Service;

import com.example.MovieBookingApplication.DTO.TheaterDTO;
import com.example.MovieBookingApplication.Entity.Show;
import com.example.MovieBookingApplication.Entity.Theater;
import com.example.MovieBookingApplication.Repository.BookingRepository;
import com.example.MovieBookingApplication.Repository.ShowRepository;
import com.example.MovieBookingApplication.Repository.TheaterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class TheaterService {

	@Autowired
	private TheaterRepository theaterRepository;

	@Autowired
	private BookingRepository bookingRepository;

	@Autowired
	private ShowRepository showRepository;

	public Theater addTheater(TheaterDTO theaterDTO) {
		Theater theater = new Theater();
		theater.setTheaterName(theaterDTO.getTheaterName());
		theater.setTheaterLocation(theaterDTO.getTheaterLocation());
		theater.setTheaterCapacity(theaterDTO.getTheaterCapacity());
		theater.setTheaterScreenType(theaterDTO.getTheaterScreenType());
		return theaterRepository.save(theater);
	}

	// ✅ ADDED
	public List<Theater> getAllTheaters() {
		return theaterRepository.findAll();
	}

	public List<Theater> getTheaterByLocation(String location) {
		Optional<List<Theater>> listOfTheaterBox = theaterRepository.findByTheaterLocation(location);
		if (listOfTheaterBox.isPresent()) {
			return listOfTheaterBox.get();
		} else throw new RuntimeException("No theaters found for location: " + location);
	}

	public Theater updateTheater(Long id, TheaterDTO theaterDTO) {
		Theater theater = theaterRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("No theater found for id: " + id));
		theater.setTheaterName(theaterDTO.getTheaterName());
		theater.setTheaterLocation(theaterDTO.getTheaterLocation());
		theater.setTheaterCapacity(theaterDTO.getTheaterCapacity());
		theater.setTheaterScreenType(theaterDTO.getTheaterScreenType());
		return theaterRepository.save(theater);
	}

	public void deleteTheater(Long id) {
		List<Show> shows = showRepository.findByTheaterId(id)
				.orElse(Collections.emptyList());

		// 1. delete all bookings for each show
		shows.forEach(s -> bookingRepository.deleteByShowId(s.getId()));

		// 2. delete all shows
		showRepository.deleteByTheaterId(id);

		// 3. delete theater
		theaterRepository.deleteById(id);
	}
}