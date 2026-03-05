package com.erdouglass.validation;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DateRangeValidator implements ConstraintValidator<DateRange, LocalDate> {
	private LocalDate minDate;
	private LocalDate maxDate;

	@Override
	public void initialize(DateRange constraintAnnotation) {
		try {
			minDate = LocalDate.parse(constraintAnnotation.min());
			maxDate = LocalDate.parse(constraintAnnotation.max());
		} catch (DateTimeParseException e) {
			throw new IllegalArgumentException("Invalid date format for @DateRange annotation. Use 'yyyy-MM-dd'", e);
		}
	}

	@Override
	public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
		if (value == null) {
			return true;
		}

		boolean isAfterMin = !value.isBefore(minDate);
		boolean isBeforeMax = !value.isAfter(maxDate);
		return isAfterMin && isBeforeMax;
	}
}
