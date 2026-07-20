package com.erdouglass.emdb.media.domain.movie;

import java.time.LocalDate;

/// Capability interface: the shape of an edit the [Movie] aggregate is
/// willing to consume, declared *by the domain* so the dependency arrow
/// points inward — adapters implement it, the domain never learns who.
///
/// Raw types by necessity, not preference: implementors are wire-bound
/// records whose accessors must double as these methods, so value-object
/// construction happens inside [Movie#merge(UpdateMovie)].
///
/// `version()` is the snapshot this edit was composed against — the claim
/// the optimistic check verifies — never a field to copy blindly.
public interface UpdateMovie {
  
  Long version();
  String title();
  LocalDate releaseDate();
  String originalLanguage();
}
