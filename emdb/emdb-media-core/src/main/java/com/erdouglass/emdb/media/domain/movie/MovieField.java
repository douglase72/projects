package com.erdouglass.emdb.media.domain.movie;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import com.erdouglass.emdb.media.movie.MovieDetails;

public enum MovieField {
  TITLE(MovieDetails::title),
  RELEASE_DATE(MovieDetails::releaseDate),
  SCORE(MovieDetails::score),
  ORIGINAL_LANGUAGE(MovieDetails::originalLanguage);
  
  private final Function<MovieDetails, Object> extractor;

  MovieField(Function<MovieDetails, Object> extractor) {
    this.extractor = extractor;
  } 
  
  Object valueIn(MovieDetails details) {
    return details == null ? null : extractor.apply(details);
  }
  
  /**
   * Field-by-field diff between two snapshots; either side may be null, meaning "absent".
   *   diff(null, d) -> ADDED for every populated field    (creation)
   *   diff(a, b)    -> ADDED / UPDATED / DELETED per field that differs
   *   diff(d, null) -> DELETED for every populated field  (removal)
   * Row order follows declaration order — deterministic audit output.
   */
  static List<FieldChange> diff(MovieDetails oldDetails, MovieDetails newDetails) {
    List<FieldChange> changes = new ArrayList<>();
    for (MovieField field : values()) {
      Object oldValue = field.valueIn(oldDetails);
      Object newValue = field.valueIn(newDetails);
      if (Objects.equals(oldValue, newValue)) continue;
      if (oldValue == null) changes.add(FieldChange.added(field, newValue));
      else if (newValue == null) changes.add(FieldChange.deleted(field, oldValue));
      else changes.add(FieldChange.updated(field, oldValue, newValue));
    }
    return List.copyOf(changes);
  }  
}
