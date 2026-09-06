package com.erdouglass.emdb.media.kernel;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/// An aggregate rating from 0 to 10 inclusive, held to three decimal places.
///
/// `BigDecimal` rather than `double` because ratings are compared for equality
/// on every ingestion run, and binary floating point makes that comparison
/// unreliable — a value that round-trips through JSON as `7.3000000000000007`
/// would register as a change on every pass.
///
/// The value is rescaled to three places with `HALF_UP` *before* the range is
/// checked, so an input marginally above 10 rounds down into range rather than
/// being rejected. Rescaling also means `7.3` and `7.300` construct to equal
/// scores, which is what keeps the diff quiet.
///
/// @param value the rescaled rating: scale 3, between 0 and 10 inclusive
public record Score(BigDecimal value) {
  private static final BigDecimal MIN = BigDecimal.ZERO;
  private static final BigDecimal MAX = BigDecimal.TEN;
  private static final int SCALE = 3;
  
  public Score {
    Objects.requireNonNull(value, "score is required");
    value = value.setScale(SCALE, RoundingMode.HALF_UP);
    if (value.compareTo(MIN) < 0 || value.compareTo(MAX) > 0) {
        throw new IllegalArgumentException("score must be between 0 and 10, was %s"
            .formatted(value.toPlainString()));
    }
  }
  
  public static Score of(BigDecimal score) { return new Score(score); } 
}
