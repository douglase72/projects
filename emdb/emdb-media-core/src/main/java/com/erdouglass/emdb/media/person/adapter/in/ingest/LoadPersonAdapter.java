package com.erdouglass.emdb.media.person.adapter.in.ingest;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import com.erdouglass.emdb.media.api.LoadPersonCommand;
import com.erdouglass.emdb.media.api.LoadPersonUseCase;
import com.erdouglass.emdb.media.kernel.Name;
import com.erdouglass.emdb.media.person.application.port.in.SavePersonCommand;
import com.erdouglass.emdb.media.person.application.port.in.SavePersonUseCase;
import com.erdouglass.emdb.media.person.domain.model.Biography;
import com.erdouglass.emdb.media.person.domain.model.BirthDate;
import com.erdouglass.emdb.media.person.domain.model.DeathDate;
import com.erdouglass.emdb.media.person.domain.model.Gender;
import com.erdouglass.emdb.media.person.domain.model.PersonDetails;

@ApplicationScoped
class LoadPersonAdapter implements LoadPersonUseCase {
  
  @Inject
  SavePersonUseCase saveUseCase;

  @Override
  public void load(LoadPersonCommand command) {
    var details = PersonDetails.builder()
        .name(Name.of(command.name()))
        .birthDate(command.birthDate() != null ? BirthDate.of(command.birthDate()) : null)
        .deathDate(command.deathDate() != null ? DeathDate.of(command.deathDate()) : null)
        .gender(command.gender() != null ? Gender.from(command.gender()) : null)
        .biography(command.biography() != null ? Biography.of(command.biography()) : null)
        .build();
    saveUseCase.save(SavePersonCommand.of(command.tmdbId(), details));
  }
}
