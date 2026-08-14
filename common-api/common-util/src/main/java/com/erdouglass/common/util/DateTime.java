package com.erdouglass.common.util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.Locale;
import java.util.Objects;

public final class DateTime {
  public static final ZoneId DEFAULT_TIME_ZONE = ZoneId.of("UTC");
  
  private static final DateTimeFormatter MILLI = formatter(3);
  private static final DateTimeFormatter MICRO = formatter(6);
  private static final DateTimeFormatter NANO  = formatter(9);
  
  private final Instant instant;
  
  DateTime() {
    this.instant = Instant.now();
  }
  
  DateTime(Instant instant) {
    this.instant = Objects.requireNonNull(instant, "instant is required");
  }
  
  public boolean isAfter(DateTime that) {
    return instant.isAfter(that.instant);
  }

  public boolean isBefore(DateTime that) {
    return instant.isBefore(that.instant);
  }

  public LocalDate toLocalDate() {
    return instant.atZone(DEFAULT_TIME_ZONE).toLocalDate();
  }

  public LocalDateTime toLocalDateTime() {
    return LocalDateTime.ofInstant(instant, DEFAULT_TIME_ZONE);
  }

  public Instant toInstant() {
    return instant;
  }
  
  @Override
  public int hashCode() {
    return instant.hashCode();
  }
  
  @Override
  public boolean equals(Object o) {
    return o instanceof DateTime that && instant.equals(that.instant);
  }
  
  @Override 
  public String toString() { return format(NANO); }
  public String toDateString() { return toLocalDate().toString(); }
  public String toMilliString() { return format(MILLI); }
  public String toMicroString() { return format(MICRO); }
  public String toNanoString()  { return format(NANO); }
  
  private String format(DateTimeFormatter dtf) {
    return dtf.format(instant);
  }
  
  private static DateTimeFormatter formatter(int fractionDigits) {
    return new DateTimeFormatterBuilder()
        .appendPattern("uuuu-MM-dd HH:mm:ss")
        .appendFraction(ChronoField.NANO_OF_SECOND, fractionDigits, fractionDigits, true)
        .toFormatter(Locale.ROOT)
        .withZone(DEFAULT_TIME_ZONE);
  }
}
