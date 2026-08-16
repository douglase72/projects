package com.erdouglass.emdb.media.movie.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

import com.erdouglass.emdb.media.kernel.ValueObject;

/// The auditable fields of a title, each paired with a way to read it out of a
/// [MovieDetails] snapshot.
///
/// Pairing the name with the accessor is what keeps the diff honest: adding a
/// field to [MovieDetails] and forgetting to add it here means the field simply
/// never appears in the audit trail, and the compiler cannot catch that. Add the
/// constant in the same commit as the component.
///
/// Constants are persisted by name, so renaming one invalidates existing audit
/// rows. Declaration order is also the order in which changes are emitted, which
/// is what makes audit output deterministic and reviewable — insert new
/// constants deliberately rather than alphabetically.
public enum MovieField {
  TITLE(MovieDetails::title),
  RELEASE_DATE(MovieDetails::releaseDate),
  SCORE(MovieDetails::score),
  ORIGINAL_LANGUAGE(MovieDetails::originalLanguage),
  OVERVIEW(MovieDetails::overview);
  
  private final Function<MovieDetails, Object> extractor;

  MovieField(Function<MovieDetails, Object> extractor) {
    this.extractor = extractor;
  }
  
  /// Diffs two snapshots field by field.
  ///
  /// Either side may be `null`, meaning "absent", which gives three uses from
  /// one method:
  ///
  /// * `diff(null, d)` — every populated field as an addition (creation)
  /// * `diff(a, b)` — an addition, update or removal per field that differs
  /// * `diff(d, null)` — every populated field as a removal (deletion)
  ///
  /// Fields that compare equal are skipped, so an empty result means the two
  /// snapshots are equivalent. Rows follow declaration order.
  ///
  /// @param oldDetails the earlier snapshot, or `null` for none
  /// @param newDetails the later snapshot, or `null` for none
  /// @return an immutable list of changes, empty when nothing differs
  /// @implNote values are compared and stringified exactly as the components are
  ///           declared, so optional components are handled as `Optional`
  ///           instances rather than as their unwrapped contents
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
  
  /// Reads this field out of a snapshot, treating a missing snapshot as a
  /// missing value.
  ///
  /// Optional components are unwrapped, so an absent field reads as `null` and
  /// not as `Optional.empty`. That is what lets [#diff] recognise a field being
  /// cleared as a removal rather than as an update to a sentinel, and it is why
  /// the return type is `Object` rather than the component type.
  ///
  /// The `null` tolerance on `details` lets [#diff] express creation and
  /// deletion as diffs against nothing, rather than needing separate code paths.
  ///
  /// @param details the snapshot to read, or `null` for "no snapshot"
  /// @return this field's value, or `null` when the snapshot is `null` or the
  ///         field is unpopulated
  Object valueIn(MovieDetails details) {
    if (details == null) {
      return null;
    }
    Object value = extractor.apply(details);
    value = value instanceof Optional<?> optional ? optional.orElse(null) : value;
    return value instanceof ValueObject<?> vo ? vo.value() : value;
  }
}
