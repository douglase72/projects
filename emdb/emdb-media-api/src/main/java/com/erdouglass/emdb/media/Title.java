package com.erdouglass.emdb.media;

import java.text.Normalizer;

public record Title(String value) {
  public static final int MAX_LENGTH = 140;
  
  public Title {
    if (value == null) {
      throw new IllegalArgumentException("title must not be null");
    }
    value = Normalizer.normalize(value.strip(), Normalizer.Form.NFC);
    if (value.isBlank()) {
      throw new IllegalArgumentException("title must not be blank");
    }
    if (value.length() > MAX_LENGTH) {
      throw new IllegalArgumentException(
          "title must not exceed %d characters".formatted(MAX_LENGTH));
    }
  }
  
  public static Title of(String title) {
    return new Title(title);
  }
  
  @Override
  public String toString() {
    return value;
  }
}
