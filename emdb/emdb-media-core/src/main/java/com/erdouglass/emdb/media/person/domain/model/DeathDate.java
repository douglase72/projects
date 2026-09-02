package com.erdouglass.emdb.media.person.domain.model;

import java.time.LocalDate;
import java.util.Objects;

import com.erdouglass.common.util.DateTime;
import com.erdouglass.common.util.DateTimeFactory;

public record DeathDate(DateTime value) {
  public static final DateTime MIN = BirthDate.MIN;
  
  public DeathDate {
    Objects.requireNonNull(value, "death date must not be null");
    if (value.isBefore(MIN)) {
      throw new IllegalArgumentException(
          "death date must not be earlier than %s".formatted(MIN));
    }
    DateTime latest = DateTimeFactory.now().addDays(1);
    if (value.isAfter(latest)) {
      throw new IllegalArgumentException(
          "death date must not be in the future, was %s".formatted(value));
    }
  }
  
  public static DeathDate of(DateTime birthDate) {
    return new DeathDate(birthDate);
  }
  
  public static DeathDate from(LocalDate birthDate) {
    return new DeathDate(DateTimeFactory.from(birthDate));
  }
  
  public static DeathDate from(String birthDate) {
    return new DeathDate(DateTimeFactory.from(birthDate));
  } 
  
  public LocalDate toLocalDate() {
    return value.toLocalDate();
  }
}
