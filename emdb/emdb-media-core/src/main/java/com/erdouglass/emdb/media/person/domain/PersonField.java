package com.erdouglass.emdb.media.person.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

import com.erdouglass.emdb.media.kernel.ValueObject;

public enum PersonField {
  NAME(PersonDetails::name),
  BIRTH_DATE(PersonDetails::birthDate),
  DEATH_DATE(PersonDetails::deathDate),
  GENDER(PersonDetails::gender),
  BIOGRAPHY(PersonDetails::biography);
  
  private final Function<PersonDetails, Object> extractor;

  PersonField(Function<PersonDetails, Object> extractor) {
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
  static List<PersonFieldChange> diff(PersonDetails oldDetails, PersonDetails newDetails) {
    List<PersonFieldChange> changes = new ArrayList<>();
    for (PersonField field : values()) {
      Object oldValue = field.valueIn(oldDetails);
      Object newValue = field.valueIn(newDetails);
      if (Objects.equals(oldValue, newValue)) continue;
      if (oldValue == null) changes.add(PersonFieldChange.added(field, newValue));
      else if (newValue == null) changes.add(PersonFieldChange.deleted(field, oldValue));
      else changes.add(PersonFieldChange.updated(field, oldValue, newValue));
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
  Object valueIn(PersonDetails details) {
    if (details == null) {
      return null;
    }
    Object value = extractor.apply(details);
    value = value instanceof Optional<?> optional ? optional.orElse(null) : value;
    return value instanceof ValueObject<?> vo ? vo.value() : value;
  }
}
