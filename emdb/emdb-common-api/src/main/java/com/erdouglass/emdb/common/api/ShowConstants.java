package com.erdouglass.emdb.common.api;

/// A utility class containing validation boundaries and hard constraints
/// related to movies and television shows.
///
/// This class cannot be instantiated.
public final class ShowConstants {

  /// The maximum valid date for a show.
  public static final String MAX_DATE = "2100-01-01";

  /// The minimum valid release date for a movie.
  public static final String MOVIE_MIN_DATE = "1888-01-01";

  /// The maximum length for show titles.
  public static final int TITLE_MAX_LENGTH = 140;

  /// The maximum length for a movie or TV show `overview`.
  public static final int OVERVIEW_MAX_LENGTH = 1024;

  /// The maximum length for an image path string, like `backdrop` or `poster`.
  public static final int POSTER_MAX_LENGTH = 80;

  /// The minimum length for an image path string, like `backdrop` or `poster`.
  public static final int POSTER_MIN_LENGTH = 1;

  /// The maximum length for a persons role.
  public static final int ROLE_MAX_LENGTH = 100;

  /// The minimum valid air date for a series.
  public static final String SERIES_MIN_DATE = "1928-09-10";

  /// The maximum length for the type of series.
  public static final int SERIES_TYPE_MAX_LENGTH = 11;

  /// The maximum length for a production `status` string (e.g., "Released").
  public static final int STATUS_MAX_LENGTH = 16;

  /// The maximum length for a movie's `tagline`.
  public static final int TAGLINE_MAX_LENGTH = 150;

  private ShowConstants() {
  }

}
