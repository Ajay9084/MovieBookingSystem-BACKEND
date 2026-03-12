package com.example.MovieBookingApplication.Repository;

import com.example.MovieBookingApplication.Entity.Show;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ShowRepository extends JpaRepository<Show, Long> {

	Optional<List<Show>> findByMovieId(Long movieid);
	Optional<List<Show>> findByTheaterId(Long theaterid);

	@Transactional
	void deleteByMovieId(Long movieId);

	@Transactional
	void deleteByTheaterId(Long theaterId);

}
