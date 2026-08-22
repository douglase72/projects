package com.erdouglass.emdb.media.person.domain;

import java.time.LocalDate;
import java.util.Objects;

import com.erdouglass.common.util.DateTime;
import com.erdouglass.common.util.DateTimeFactory;
import com.erdouglass.emdb.media.kernel.ValueObject;

public record BirthDate(DateTime value) implements ValueObject<DateTime> {
  public static final DateTime MIN = DateTimeFactory.from(1800, 1, 1);
  
  public BirthDate {
    Objects.requireNonNull(value, "birth date must not be null");
    if (value.isBefore(MIN)) {
      throw new IllegalArgumentException(
          "birth date must not be earlier than %s".formatted(MIN));
    }
    DateTime latest = DateTimeFactory.now().addDays(1);
    if (value.isAfter(latest)) {
      throw new IllegalArgumentException(
          "birth date must not be in the future, was %s".formatted(value));
    }
  }
  
  public static BirthDate of(DateTime birthDate) {
    return new BirthDate(birthDate);
  }
  
  public static BirthDate from(LocalDate birthDate) {
    return new BirthDate(DateTimeFactory.from(birthDate));
  }
  
  public static BirthDate from(String birthDate) {
    return new BirthDate(DateTimeFactory.from(birthDate));
  } 
  
  public LocalDate toLocalDate() {
    return value.toLocalDate();
  }
}
