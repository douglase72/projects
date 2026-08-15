package com.erdouglass.emdb.media;

import java.math.BigDecimal;
import java.util.Optional;

public interface UpsertMovieCommand {
  
  String title();
  Optional<String> releaseDate();
  Optional<BigDecimal> score();
  Optional<String> originalLanguage();
  Optional<String> overview();
}
