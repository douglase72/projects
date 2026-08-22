package com.erdouglass.emdb.media.person.domain;

import java.util.Objects;
import java.util.regex.Pattern;

import com.erdouglass.emdb.media.kernel.ValueObject;

/// The identifier clients use to address a person, of the form `pr_<n>`.
///
/// The prefix makes the id self-describing in URLs, logs and support tickets,
/// and means an id from another resource type fails fast instead of silently
/// addressing the wrong row. The numeric part is the database key, so this is a
/// presentation wrapper over the primary key rather than an independent
/// identifier — [#toLong()] converts back.
///
/// Because it is derived from a database-assigned key, a person has no public id
/// until it has been persisted.
///
/// The numeric part must be a positive integer with no leading zeros, so exactly
/// one string denotes any given person.
///
/// @param value the full prefixed id, never `null` and always matching
///        `pr_<n>`
public record PersonPublicId(String value) implements ValueObject<String> {
  private static final String PREFIX = "pr_";
  private static final Pattern SHAPE = Pattern.compile("^pr_[1-9]\\d*$");
  
  public PersonPublicId {
    Objects.requireNonNull(value, "person id must not be null");
    if (!SHAPE.matcher(value).matches()) {
      throw new IllegalArgumentException("person id must match pr_<n>, got: " + value);
    }    
  }
  
  /// Wraps an id that is already in prefixed form.
  ///
  /// Use this at the edge, on ids that arrived from a client.
  ///
  /// @param id the prefixed id, e.g. `pr_42`
  /// @return the validated id
  /// @throws IllegalArgumentException if `id` does not match `pr_<n>`
  public static PersonPublicId of(String id) {
    return new PersonPublicId(id);
  }
  
  /// Derives the public id from a database key.
  ///
  /// Use this on the way out of persistence, where the raw key is in hand.
  ///
  /// @param id the database key, must be positive
  /// @return the prefixed id
  /// @throws IllegalArgumentException if `id` is less than `1`
  public static PersonPublicId from(long id) {
    if (id < 1) {
      throw new IllegalArgumentException("id must be positive");
    }
    return new PersonPublicId(PREFIX + id);
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
