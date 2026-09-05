package com.erdouglass.emdb.media.person.application.port.in;

import java.util.Map;

import com.erdouglass.emdb.media.api.TmdbId;
import com.erdouglass.emdb.media.kernel.AggregateId;

public interface ResolvePersonUseCase {

  /// Guarantees a Person exists for every reference, creating stubs for unknown people, and
  /// returns their ids keyed by TmdbId. Joins the caller's transaction; each new stub's
  /// PersonStubCreated is in the outbox when this returns.
  Map<TmdbId, AggregateId> resolve(ResolvePersonCommand command);
}
