package com.erdouglass.common.validation;

import java.time.Instant;
import java.time.format.DateTimeParseException;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DateRangeValidator implements ConstraintValidator<DateRange, Instant> {
  private Instant minDate;
  private Instant maxDate;

  @Override
  public void initialize(DateRange constraintAnnotation) {
    try {
      minDate = Instant.parse(constraintAnnotation.min());
      maxDate = Instant.parse(constraintAnnotation.max());
    } catch (DateTimeParseException e) {
      throw new IllegalArgumentException("Invalid date format. Use 'yyyy-MM-dd'", e);
    }
  }

  @Override
  public boolean isValid(Instant value, ConstraintValidatorContext context) {
    if (value == null) {
      return true;
    }
    boolean isAfterMin = !value.isBefore(minDate);
    boolean isBeforeMax = !value.isAfter(maxDate);
    return isAfterMin && isBeforeMax;
  }
}
