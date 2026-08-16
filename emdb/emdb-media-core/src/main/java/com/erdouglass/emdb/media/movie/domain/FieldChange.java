package com.erdouglass.emdb.media.movie.domain;

import java.util.Objects;

import com.erdouglass.emdb.media.kernel.FieldOperation;

/// One row of the audit trail: a single field of a single title moving from one
/// value to another.
///
/// Values are held as strings rather than as their domain types so that a row is
/// readable long after the field's type has changed, and so that heterogeneous
/// fields can share one table. Conversion is by `toString()`, which makes rows
/// suitable for display and diffing but *not* for reconstructing the aggregate.
///
/// The operation and the two values are kept consistent, and construction fails
/// if they disagree:
///
/// | Operation | `oldValue` | `newValue` |
/// |-----------|------------|------------|
/// | `ADDED`   | absent     | present    |
/// | `UPDATED` | present    | present    |
/// | `DELETED` | present    | absent     |
///
/// Prefer the [#added][#added(MovieField, Object)],
/// [#updated][#updated(MovieField, Object, Object)] and
/// [#deleted][#deleted(MovieField, Object)] factories over the canonical
/// constructor; they pick the operation for you and cannot produce an
/// inconsistent row.
///
/// @param field which field of the title changed
/// @param oldValue the value before the change, `null` when the field had none
/// @param newValue the value after the change, `null` when the field has none
/// @param operation how the field changed
public record FieldChange(MovieField field, String oldValue, String newValue, FieldOperation operation) {

  public FieldChange {
    Objects.requireNonNull(field, "field");
    Objects.requireNonNull(operation, "operation");
    switch (operation) {
        case ADDED   -> require(oldValue == null && newValue != null, "ADDED: no old value, a new value");
        case UPDATED -> require(oldValue != null && newValue != null, "UPDATED: both values present");
        case DELETED -> require(oldValue != null && newValue == null, "DELETED: an old value, no new value");
    }
  }
  
  /// Records a field gaining a value it did not have.
  ///
  /// @param field the field that gained a value
  /// @param newValue the value it gained, rendered with `toString()`
  /// @return the audit row
  /// @throws IllegalArgumentException if `newValue` is `null`, since that would
  ///         describe a change that did not happen
  public static FieldChange added(MovieField field, Object newValue) {
    return new FieldChange(field, null, str(newValue), FieldOperation.ADDED);
  }

  /// Records a field moving from one value to another.
  ///
  /// The caller is responsible for having established that the two values
  /// actually differ; this method does not check.
  ///
  /// @param field the field that changed
  /// @param oldValue the value before, rendered with `toString()`
  /// @param newValue the value after, rendered with `toString()`
  /// @return the audit row
  /// @throws IllegalArgumentException if either value is `null`
  public static FieldChange updated(MovieField field, Object oldValue, Object newValue) {
    return new FieldChange(field, str(oldValue), str(newValue), FieldOperation.UPDATED);
  }

  /// Records a field losing the value it had.
  ///
  /// @param field the field that was cleared
  /// @param oldValue the value it held, rendered with `toString()`
  /// @return the audit row
  /// @throws IllegalArgumentException if `oldValue` is `null`
  public static FieldChange deleted(MovieField field, Object oldValue) {
    return new FieldChange(field, str(oldValue), null, FieldOperation.DELETED);
  }

  private static String str(Object value) { return value == null ? null : value.toString(); }

  private static void require(boolean condition, String message) {
    if (!condition) throw new IllegalArgumentException(message);
  }  
}
