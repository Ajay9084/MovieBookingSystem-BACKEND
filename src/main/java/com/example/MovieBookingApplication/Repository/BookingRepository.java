package com.example.MovieBookingApplication.Repository;

import com.example.MovieBookingApplication.Entity.Booking;
import com.example.MovieBookingApplication.Entity.BookingStatus;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking,Long> {
List<Booking> findByUserId(Long userId);
List<Booking> findByShowId(Long showId);

@Transactional
void deleteByShowId(Long showId);

List<Booking> findByBookingStatus(BookingStatus bookingStatus);



}
