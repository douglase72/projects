package com.erdouglass.emdb.media.movie.adapter.out.person;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import com.erdouglass.emdb.media.kernel.AggregateId;
import com.erdouglass.emdb.media.kernel.TmdbId;
import com.erdouglass.emdb.media.movie.application.port.out.PersonCredit;
import com.erdouglass.emdb.media.movie.application.port.out.ResolvePeople;
import com.erdouglass.emdb.media.person.application.port.in.ResolvePersonCommand;
import com.erdouglass.emdb.media.person.application.port.in.ResolvePersonCommand.Reference;
import com.erdouglass.emdb.media.person.application.port.in.ResolvePersonUseCase;

@ApplicationScoped
class ResolvePeopleAdapter implements ResolvePeople {
  
  @Inject
  ResolvePersonUseCase resolver;

  @Override
  public Map<TmdbId, AggregateId> resolve(Set<PersonCredit> people) {
    var command = ResolvePersonCommand.of(people.stream()
        .map(c -> Reference.of(c.tmdbId(), c.name()))
        .collect(Collectors.toSet()));
    return resolver.resolve(command);
  }
}
