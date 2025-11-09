package com.erdouglass.emdb.common.command;

import java.time.LocalDate;

import com.erdouglass.emdb.common.query.MovieDto;
import com.erdouglass.emdb.common.query.ShowDto;
import com.erdouglass.validation.DateRange;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record MovieCreateCommand(
		@NotBlank @Size(max = ShowDto.NAME_MAX_LENGTH) String title,
		@DateRange(min = MovieDto.MIN_DATE, max = MovieDto.MAX_DATE) LocalDate releaseDate) {

}
