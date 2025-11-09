package com.erdouglass.emdb.media.service;

import java.time.LocalDate;

import org.jboss.logging.Logger;

import com.erdouglass.emdb.common.command.MovieCreateCommand;
import com.erdouglass.emdb.common.query.MovieDto;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@ApplicationScoped
public class MovieService {
	private static final Logger LOGGER = Logger.getLogger(MovieService.class);
	
	public MovieDto create(@NotNull @Valid MovieCreateCommand request) {
		var movie = new MovieDto(1L, "Austin Powers in Goldmember", LocalDate.parse("2002-02-26"));
		LOGGER.infof("Created: %s", movie);
		return movie;
	}
	
	public MovieDto findById(@NotNull @Positive Long id) {
  	var movie = new MovieDto(1L, "Austin Powers in Goldmember", LocalDate.parse("2002-02-26"));
  	LOGGER.infof("Found: %s", movie);
  	return movie;		
	}
	
}
