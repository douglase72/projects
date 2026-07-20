package com.erdouglass.emdb.media.domain.shared;

import com.erdouglass.emdb.media.MediaType;

/// URL-facing identity: a [MediaType] discriminator plus a database-assigned
/// sequence number, rendered `mv_42`. The only identifier that leaves the
/// system.
public record PublicId(MediaType type, Long value) {
  
  public PublicId {
    if (type == null) {
      throw new IllegalArgumentException("media type must not be null");
    }
    if (value == null || value < 1) {
      throw new IllegalArgumentException("invalid id");
    }
  }
  
  public static PublicId of(MediaType type, Long value) {
    return new PublicId(type, value);
  }
  
  public static PublicId from(String id) {
    int i = id.indexOf('_');
    if (i < 0) throw new IllegalArgumentException("invalid id: " + id);
    var type = MediaType.from(id.substring(0, i));
    return new PublicId(type, Long.parseLong(id.substring(i + 1)));
  }

  @Override
  public String toString() {
    return type.toString() + "_" + value;
  }
}
