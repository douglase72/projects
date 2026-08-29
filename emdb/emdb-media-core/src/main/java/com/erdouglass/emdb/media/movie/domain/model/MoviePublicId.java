package com.erdouglass.emdb.media.movie.domain.model;

import java.util.Objects;
import java.util.regex.Pattern;

import com.erdouglass.emdb.media.kernel.ValueObject;

/// The identifier clients use to address a title, of the form `mv_<n>`.
///
/// The prefix makes the id self-describing in URLs, logs and support tickets,
/// and means an id from another resource type fails fast instead of silently
/// addressing the wrong row. The numeric part is the database key, so this is a
/// presentation wrapper over the primary key rather than an independent
/// identifier — [#toLong()] converts back.
///
/// Because it is derived from a database-assigned key, a movie has no public id
/// until it has been persisted.
///
/// The numeric part must be a positive integer with no leading zeros, so exactly
/// one string denotes any given title.
///
/// @param value the full prefixed id, never `null` and always matching
///        `mv_<n>`
public record MoviePublicId(String value) implements ValueObject<String> {
  private static final String PREFIX = "mv_";
  private static final Pattern SHAPE = Pattern.compile("^mv_[1-9]\\d*$");
  
  public MoviePublicId {
    Objects.requireNonNull(value, "movie id must not be null");
    if (!SHAPE.matcher(value).matches()) {
      throw new IllegalArgumentException("movie id must match mv_<n>, got: " + value);
    }    
  }
  
  /// Wraps an id that is already in prefixed form.
  ///
  /// Use this at the edge, on ids that arrived from a client.
  ///
  /// @param id the prefixed id, e.g. `mv_42`
  /// @return the validated id
  /// @throws IllegalArgumentException if `id` does not match `mv_<n>`
  public static MoviePublicId of(String id) {
    return new MoviePublicId(id);
  }
  
  /// Derives the public id from a database key.
  ///
  /// Use this on the way out of persistence, where the raw key is in hand.
  ///
  /// @param id the database key, must be positive
  /// @return the prefixed id
  /// @throws IllegalArgumentException if `id` is less than `1`
  public static MoviePublicId from(long id) {
    if (id < 1) {
      throw new IllegalArgumentException("id must be positive");
    }
    return new MoviePublicId(PREFIX + id);
  }
  
  /// Strips the prefix to recover the database key.
  ///
  /// Use this on the way into persistence. The conversion cannot fail: the shape
  /// enforced at construction guarantees a parsable positive numeric part.
  ///
  /// @return the underlying database key
  public Long toLong() {
    return Long.parseLong(value.substring(PREFIX.length()));
  }
}
