package com.example.MovieBookingApplication.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class Theater {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String theaterName;
	private String theaterLocation;
	private Integer theaterCapacity;
	private Integer  theaterScreenType;

	@OneToMany(mappedBy = "theater", fetch = FetchType.LAZY,
	cascade = CascadeType.ALL,
	orphanRemoval = true)
	@JsonIgnore
	private List<Show> show;
}
