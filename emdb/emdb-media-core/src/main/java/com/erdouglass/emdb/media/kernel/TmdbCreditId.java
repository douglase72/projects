package com.erdouglass.emdb.media.kernel;

import java.util.Objects;

public record TmdbCreditId(String value) {

  public TmdbCreditId {
    Objects.requireNonNull(value);
    if (!value.matches("[0-9a-f]{24}")) 
      throw new IllegalArgumentException("invalid credit id: " + value);
  }
  
  public static TmdbCreditId of(String creditId) {
    return new TmdbCreditId(creditId);
  }
}
