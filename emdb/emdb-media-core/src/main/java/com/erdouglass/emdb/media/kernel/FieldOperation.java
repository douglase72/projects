package com.erdouglass.emdb.media.kernel;

/// How a single field changed between two revisions.
///
/// Deliberately smaller than the vocabulary of the write that produced it: a
/// title being created writes [#ADDED] rows, not "created" rows, so a field's
/// history reads the same regardless of which endpoint caused the change.
///
/// Persisted by name, so renaming a constant invalidates existing audit rows.
public enum FieldOperation {
  ADDED, 
  UPDATED, 
  DELETED;
}
