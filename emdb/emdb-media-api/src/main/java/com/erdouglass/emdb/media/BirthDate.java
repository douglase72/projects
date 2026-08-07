package com.erdouglass.emdb.media;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeParseException;
import java.util.Objects;
import java.util.Optional;

public record BirthDate(LocalDate value) {
  public static final LocalDate MIN = LocalDate.of(1700, 1, 1);
  
  public BirthDate {
    Objects.requireNonNull(value, "birth date must not be null");
    LocalDate latest = LocalDate.now(ZoneOffset.MAX);
    if (value.isBefore(MIN) || value.isAfter(latest)) {
      throw new IllegalArgumentException(
          "birth date must be between %s and %s".formatted(MIN, latest));
    } 
  }
  
  public static BirthDate of(LocalDate birthDate) {
    return new BirthDate(birthDate);
  }
  
  public static Optional<BirthDate> from(String birthDate) {
    try {
      return Optional.ofNullable(birthDate)
          .filter(bd -> !bd.isBlank())
          .map(bd -> new BirthDate(LocalDate.parse(bd)));      
    } catch (DateTimeParseException e) {
      return Optional.empty();
    }
  }
}
