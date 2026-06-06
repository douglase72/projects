package com.erdouglass.emdb.media.internal;

import java.util.List;
import java.util.Map;

import com.erdouglass.emdb.media.PersonCredit;
import com.erdouglass.emdb.media.person.Person;

/// Resolves the [Person] entities referenced by a batch of credits, creating
/// any that do not yet exist, so credit persistence can attach managed people
/// without each caller handling lookup-or-create itself.
public interface PersonResolver {

  /// Looks up or creates a [Person] for each supplied credit.
  ///
  /// @param credits the credits whose people should be resolved
  /// @return a map from TMDB id to the managed [Person] for that credit
  Map<Integer, Person> findOrCreate(List<PersonCredit> credits);
}
