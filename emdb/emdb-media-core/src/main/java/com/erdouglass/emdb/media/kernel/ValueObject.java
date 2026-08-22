package com.erdouglass.emdb.media.kernel;

/// A single-value wrapper: a type whose entire state is one underlying value.
///
/// Implemented by the record wrappers that give primitives meaning — [Score],
/// [Version], `Title`, `TmdbId` and the rest. Its purpose is to give code at the
/// edges one way to reach the underlying value without knowing which wrapper it
/// holds. Audit rendering is the motivating case: a change row needs `7.500`,
/// not `Score[value=7.500]`, and without this interface the only generic way to
/// get there is `toString()`, which bakes the Java type name into stored history.
///
/// Implementing costs nothing beyond the declaration — the record component
/// accessor already satisfies the method, so no body is needed:
///
/// ```
/// public record Score(BigDecimal value) implements ValueObject<BigDecimal> { }
/// ```
///
/// This is narrower than "value object" in the DDD sense. `MovieDetails` is an
/// immutable value too, but it has five components and no single underlying
/// value, so it does not implement this. Only wrappers do.
///
/// Implementations are expected to be immutable and to validate in their
/// constructor, so [#value()] can never return something the type would have
/// rejected.
///
/// @param <T> the type of the wrapped value
public interface ValueObject<T> {

  /// {@return the wrapped value, never `null`}
  ///
  /// The value is the normalised, validated form — [Title] returns NFC-normalised
  /// text and [Score] returns a rescaled decimal, not whatever was passed in.
  T value();
}
