package com.example.MovieBookingApplication.Service;

import com.example.MovieBookingApplication.DTO.ShowDTO;
import com.example.MovieBookingApplication.Entity.Booking;
import com.example.MovieBookingApplication.Entity.BookingStatus;
import com.example.MovieBookingApplication.Entity.Movie;
import com.example.MovieBookingApplication.Entity.Show;
import com.example.MovieBookingApplication.Entity.Theater;
import com.example.MovieBookingApplication.Repository.BookingRepository;
import com.example.MovieBookingApplication.Repository.MovieRepository;
import com.example.MovieBookingApplication.Repository.ShowRepository;
import com.example.MovieBookingApplication.Repository.TheaterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ShowService {

	@Autowired
	private ShowRepository showRepository;

	@Autowired
	private BookingRepository bookingRepository;

	@Autowired
	private MovieRepository movieRepository;

	@Autowired
	private TheaterRepository theaterRepository;

	public Show createShow(ShowDTO showDTO) {
		Movie movie = movieRepository.findById(showDTO.getMovieId())
				.orElseThrow(() -> new RuntimeException("No Movie Found"));

		Theater theater = theaterRepository.findById(showDTO.getTheaterId())
				.orElseThrow(() -> new RuntimeException("No Theater Found for id " + showDTO.getTheaterId()));

		Show show = new Show();
		show.setShowTime(showDTO.getShowTime());
		show.setPrice(showDTO.getPrice());
		show.setMovie(movie);
		show.setTheater(theater);
		return showRepository.save(show);
	}

	public List<Show> getAllShows() {
		return showRepository.findAll();
	}

	public List<Show> getShowByMovie(Long movieid) {
		Optional<List<Show>> showListBox = showRepository.findByMovieId(movieid);
		if (showListBox.isPresent()) {
			return showListBox.get();
		} else throw new RuntimeException("No shows available for the movie");
	}

	public List<Show> getShowByTheater(Long theaterid) {
		Optional<List<Show>> showListBox = showRepository.findByTheaterId(theaterid);
		if (showListBox.isPresent()) {
			return showListBox.get();
		} else throw new RuntimeException("No shows available for the Theater");
	}

	public Show updateShow(Long id, ShowDTO showDTO) {
		Show show = showRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("No Show available for the id " + id));

		Movie movie = movieRepository.findById(showDTO.getMovieId())
				.orElseThrow(() -> new RuntimeException("No Movie Found"));

		Theater theater = theaterRepository.findById(showDTO.getTheaterId())
				.orElseThrow(() -> new RuntimeException("No Theater Found for id " + showDTO.getTheaterId()));

		show.setShowTime(showDTO.getShowTime());
		show.setPrice(showDTO.getPrice());
		show.setMovie(movie);
		show.setTheater(theater);
		return showRepository.save(show);
	}

	public void deleteShow(Long id) {
		Show show = showRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("No Show available for the id " + id));

		List<Booking> activeBookings = show.getBookings()
				.stream()
				.filter(b -> b.getBookingStatus() != BookingStatus.CANCELLED)
				.toList();

		if (!activeBookings.isEmpty()) {
			throw new RuntimeException(
					"Cannot delete show — it has " + activeBookings.size() + " active booking(s). Cancel them first."
			);
		}

		// 1. delete cancelled bookings
		bookingRepository.deleteByShowId(id);

		// 2. delete show
		showRepository.deleteById(id);
	}
}