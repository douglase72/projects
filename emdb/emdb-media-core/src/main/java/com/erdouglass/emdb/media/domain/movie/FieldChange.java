package com.erdouglass.emdb.media.domain.movie;

import java.util.Objects;

import com.erdouglass.emdb.media.domain.shared.FieldOperation;

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
  
  public static FieldChange added(MovieField field, Object newValue) {
    return new FieldChange(field, null, str(newValue), FieldOperation.ADDED);
  }

  public static FieldChange updated(MovieField field, Object oldValue, Object newValue) {
    return new FieldChange(field, str(oldValue), str(newValue), FieldOperation.UPDATED);
  }

  public static FieldChange deleted(MovieField field, Object oldValue) {
    return new FieldChange(field, str(oldValue), null, FieldOperation.DELETED);
  }

  private static String str(Object value) { return value == null ? null : value.toString(); }

  private static void require(boolean condition, String message) {
    if (!condition) throw new IllegalArgumentException(message);
  }  
}
