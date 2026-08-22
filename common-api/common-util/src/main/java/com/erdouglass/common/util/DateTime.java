package com.erdouglass.common.util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.Locale;
import java.util.Objects;

public final class DateTime {
  public static final ZoneId DEFAULT_TIME_ZONE = ZoneId.of("UTC");
  
  private static final DateTimeFormatter MILLI = formatter(3);
  private static final DateTimeFormatter MICRO = formatter(6);
  private static final DateTimeFormatter NANO  = formatter(9);
  
  private final Instant utc;
  
  DateTime() {
    this.utc = Instant.now();
  }
  
  DateTime(Instant instant) {
    this.utc = Objects.requireNonNull(instant, "instant is required");
  }
  
  public DateTime addDays(long days) { return new DateTime(utc.plus(days, ChronoUnit.DAYS)); }
  public DateTime addWeeks(long weeks) { return new DateTime(utc.plus(Math.multiplyExact(weeks, 7), ChronoUnit.DAYS)); }
  public DateTime addMonths(long months) { return new DateTime(utc().plusMonths(months).toInstant()); }
  public DateTime addYears(long years) { return new DateTime(utc().plusYears(years).toInstant()); }
  
  public DateTime minusDays(long days) { return new DateTime(utc.minus(days, ChronoUnit.DAYS)); }
  public DateTime minusWeeks(long weeks) { return new DateTime(utc.minus(Math.multiplyExact(weeks, 7), ChronoUnit.DAYS)); }
  public DateTime minusMonths(long months) { return new DateTime(utc().minusMonths(months).toInstant()); }
  public DateTime minusYears(long years) { return new DateTime(utc().minusYears(years).toInstant()); }
  
  public boolean isAfter(DateTime that) {
    return utc.isAfter(that.utc);
  }

  public boolean isBefore(DateTime that) {
    return utc.isBefore(that.utc);
  }

  public LocalDate toLocalDate() {
    return utc.atZone(DEFAULT_TIME_ZONE).toLocalDate();
  }

  public LocalDateTime toLocalDateTime() {
    return LocalDateTime.ofInstant(utc, DEFAULT_TIME_ZONE);
  }

  public Instant toInstant() {
    return utc;
  }
  
  @Override
  public int hashCode() {
    return utc.hashCode();
  }
  
  @Override
  public boolean equals(Object o) {
    return o instanceof DateTime that && utc.equals(that.utc);
  }
  
  @Override 
  public String toString() { return utc.toString(); }
  public String toDateString() { return toLocalDate().toString(); }
  public String toMilliString() { return format(MILLI); }
  public String toMicroString() { return format(MICRO); }
  public String toNanoString()  { return format(NANO); }
  
  private String format(DateTimeFormatter dtf) {
    return dtf.format(utc);
  }
  
  private static DateTimeFormatter formatter(int fractionDigits) {
    return new DateTimeFormatterBuilder()
        .appendPattern("uuuu-MM-dd HH:mm:ss")
        .appendFraction(ChronoField.NANO_OF_SECOND, fractionDigits, fractionDigits, true)
        .toFormatter(Locale.ROOT)
        .withZone(DEFAULT_TIME_ZONE);
  }
  
  private OffsetDateTime utc() { return utc.atOffset(ZoneOffset.UTC); }
}
