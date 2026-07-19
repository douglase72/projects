package com.erdouglass.emdb.media.domain.shared;

import java.text.Normalizer;

import com.erdouglass.emdb.media.MediaConstants;

public record Title(String value) {
  
  public Title {
    if (value == null) {
      throw new IllegalArgumentException("title must not be null");
    }
    value = Normalizer.normalize(value.strip(), Normalizer.Form.NFC);
    if (value.isBlank()) {
      throw new IllegalArgumentException("title must not be blank");
    }
    if (value.length() > MediaConstants.TITLE_MAX_LENGTH) {
      throw new IllegalArgumentException(
          "title must not exceed %d characters".formatted(MediaConstants.TITLE_MAX_LENGTH));
    }
  }
  
  public static Title of(String title) {
    return new Title(title);
  }
  
  @Override
  public String toString() {
    return value.toString();
  }
}
