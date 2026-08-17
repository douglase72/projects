package com.erdouglass.common.util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public final class DateTimeFactory {
  private static final DateTimeFormatter TIME = new DateTimeFormatterBuilder()
      .appendValue(ChronoField.HOUR_OF_DAY, 2)
      .appendLiteral(':')
      .appendValue(ChronoField.MINUTE_OF_HOUR, 2)
      .optionalStart()
        .appendLiteral(':')
        .appendValue(ChronoField.SECOND_OF_MINUTE, 2)
        .optionalStart()
          .appendFraction(ChronoField.NANO_OF_SECOND, 1, 9, true)
        .optionalEnd()
      .optionalEnd()
      .optionalStart().appendOffsetId().optionalEnd()
      .toFormatter(Locale.ROOT);
  
  private static final List<DateTimeFormatter> PARSERS = List.of(
      parser("uuuu-MM-dd"),
      parser("dd-MMM-uuuu"),
      parser("dd MMM uuuu"));

  private DateTimeFactory() {}
  
  public static DateTime now() {
    return new DateTime();
  }
  
  public static DateTime from(LocalDate date) {
    return from(date.atStartOfDay());
  }
  
  public static DateTime from(LocalDateTime dateTime) {
    return new DateTime(dateTime.atZone(DateTime.DEFAULT_TIME_ZONE).toInstant());
  } 
  
  public static DateTime from(Instant instant) {
    return new DateTime(instant);
  }
  
  public static DateTime from(long milliseconds) {
    return new DateTime(Instant.ofEpochMilli(milliseconds));
  }
  
  public static DateTime from(int year, int month, int dayOfMonth) {
    var zdt = ZonedDateTime.of(year, month, dayOfMonth, 0, 0, 0, 0, DateTime.DEFAULT_TIME_ZONE);
    return new DateTime(zdt.toInstant());
  }
  
  public static DateTime from(String dateTime) {
    Objects.requireNonNull(dateTime, "dateTime is required");
    for (DateTimeFormatter parser : PARSERS) {
      try {
        TemporalAccessor ta = parser.parseBest(
            dateTime, OffsetDateTime::from, LocalDateTime::from, LocalDate::from);
        return switch (ta) {
          case OffsetDateTime odt -> new DateTime(odt.toInstant());
          case LocalDateTime ldt -> new DateTime(ldt.atZone(DateTime.DEFAULT_TIME_ZONE).toInstant());
          case LocalDate ld -> new DateTime(ld.atStartOfDay(DateTime.DEFAULT_TIME_ZONE).toInstant());
          default -> throw new IllegalStateException("unexpected: " + ta.getClass());
        };
      } catch (DateTimeParseException e) {
        // try next pattern
      }
    }
    throw new IllegalArgumentException("Unparseable date/time: " + dateTime);
  }
  
  private static DateTimeFormatter parser(String datePattern) {
    return new DateTimeFormatterBuilder()
        .parseCaseInsensitive()
        .appendPattern(datePattern)
        .optionalStart().appendLiteral('T').append(TIME).optionalEnd()
        .optionalStart().appendLiteral(' ').append(TIME).optionalEnd()
        .toFormatter(Locale.ROOT)
        .withResolverStyle(ResolverStyle.STRICT);
  }
}
