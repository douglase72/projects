package com.erdouglass.emdb.media.movie.domain.model;

import java.time.LocalDate;
import java.util.Objects;

import com.erdouglass.common.util.DateTime;
import com.erdouglass.common.util.DateTimeFactory;
import com.erdouglass.emdb.media.kernel.ValueObject;

/// The date a title was first released.
///
/// Bounded to a plausible window rather than left open: dates before [#MIN] fall
/// outside the history of cinema and dates after [#MAX] are almost always a data
/// entry or parsing error, and both are cheaper to reject at the edge than to
/// discover later in a sorted listing.
///
/// Wraps the application's `DateTime` abstraction rather than `LocalDate`
/// directly; [#toLocalDate()] is the escape hatch for callers that need the JDK
/// type.
///
/// @param value the release instant, never `null`, within [`MIN`][#MIN] and
///        [`MAX`][#MAX] inclusive
public record ReleaseDate(DateTime value) implements ValueObject<DateTime> {
  public static final DateTime MIN = DateTimeFactory.from(1874, 1, 1);
  public static final DateTime MAX = DateTimeFactory.from(2100, 1, 1);

  public ReleaseDate {
    Objects.requireNonNull(value, "release date must not be null");
    if (value.isBefore(MIN) || value.isAfter(MAX)) {
      throw new IllegalArgumentException(
          "release date must be between %s and %s".formatted(MIN, MAX));
    } 
  }
  
  public static ReleaseDate of(DateTime releaseDate) {
    return new ReleaseDate(releaseDate);
  }
  
  public static ReleaseDate from(LocalDate releaseDate) {
    return new ReleaseDate(DateTimeFactory.from(releaseDate));
  }
  
  public static ReleaseDate from(String releaseDate) {
    return new ReleaseDate(DateTimeFactory.from(releaseDate));
  }
  
  public LocalDate toLocalDate() {
    return value.toLocalDate();
  }
}
