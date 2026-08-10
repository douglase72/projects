package com.erdouglass.emdb.media.domain.shared;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public record Score(BigDecimal value) {
  private static final BigDecimal MIN = BigDecimal.ZERO;
  private static final BigDecimal MAX = BigDecimal.TEN;
  private static final int SCALE = 3;
  
  public Score {
    Objects.requireNonNull(value, "score is required");
    value = value.setScale(SCALE, RoundingMode.HALF_UP);
    if (value.compareTo(MIN) < 0 || value.compareTo(MAX) > 0) {
        throw new IllegalArgumentException(
                "score must be between 0 and 10, was %s".formatted(value.toPlainString()));
    }
  }
  
  public static Score of(BigDecimal score) {
    return new Score(score);
  } 
}
