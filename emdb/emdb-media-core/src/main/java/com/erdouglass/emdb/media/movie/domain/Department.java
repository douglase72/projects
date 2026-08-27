package com.erdouglass.emdb.media.movie.domain;

import java.text.Normalizer;
import java.util.Objects;

public record Department(String value) {

  public Department {
    Objects.requireNonNull(value, "department must not be null");
    value = Normalizer.normalize(value.strip(), Normalizer.Form.NFC);
    if (value.isBlank()) {
      throw new IllegalArgumentException("department must not be blank");
    }
  }
  
  public static Department of(String department) {
    return new Department(department);
  }
}
