package com.example.MovieBookingApplication.DTO;

import lombok.Data;

import java.time.LocalDate;

@Data
public class MovieDTO {
	private String name;
	private String description;
	private String genre;
	private String language;
	private Integer duration;
	private LocalDate releaseDate;
}
