package com.erdouglass.emdb.common.query;

/// A non-instantiable utility class that centralizes validation constants for the
/// application.
///
/// ## Purpose
/// This class defines constraints for DTO fields and database columns, such as
/// string lengths, to ensure data integrity and consistency across the entire
/// application. All members are `public static final`.
public final class ShowDto {

/// The maximum length for names, such as a movie `title` or person `name`.
public static final int NAME_MAX_LENGTH = 140;

/// The maximum length for a movie or TV show `overview`.
public static final int OVERVIEW_MAX_LENGTH = 1024;

/// The maximum length for an image path string, like `backdrop` or `poster`.
public static final int POSTER_MAX_LENGTH = 37;

/// The minimum length for an image path string, like `backdrop` or `poster`.
public static final int POSTER_MIN_LENGTH = 31;

/// The maximum length for a production `status` string (e.g., "Released").
public static final int STATUS_MAX_LENGTH = 16;

/// The maximum length for a movie's `tagline`.
public static final int TAGLINE_MAX_LENGTH = 150;

private ShowDto() { }

}
