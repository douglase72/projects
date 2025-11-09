package com.erdouglass.emdb.common.query;

import java.time.LocalDate;

import com.erdouglass.validation.DateRange;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record MovieDto(
		@NotNull @Positive Long id,
		@NotBlank @Size(max = ShowDto.NAME_MAX_LENGTH) String title,
		@DateRange(min = MIN_DATE, max = MAX_DATE) LocalDate releaseDate) {
	
	public static final String MIN_DATE = "1888-01-01";
	public static final String MAX_DATE = "2100-01-01";
	
}
