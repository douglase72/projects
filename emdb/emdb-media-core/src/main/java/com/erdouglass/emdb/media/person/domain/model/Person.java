package com.erdouglass.emdb.media.person.domain.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.erdouglass.emdb.media.api.TmdbId;
import com.erdouglass.emdb.media.kernel.AggregateRoot;
import com.erdouglass.emdb.media.kernel.PublicId;
import com.erdouglass.emdb.media.kernel.Version;
import com.erdouglass.emdb.media.person.domain.event.PersonCreated;
import com.erdouglass.emdb.media.person.domain.event.PersonEvent;
import com.erdouglass.emdb.media.person.domain.event.PersonUpdated;

public final class Person extends AggregateRoot {
  private PersonDetails details;
  private final List<PersonEvent> domainEvents = new ArrayList<>();
  
  private Person(PublicId id, TmdbId tmdbId, Version version, PersonDetails details) {
    super(id, tmdbId, version);
    this.details = Objects.requireNonNull(details, "details are required");
  }
  
  public static Person create(TmdbId tmdbId, PersonDetails details) {
    var person = new Person(PublicId.newId(), tmdbId, Version.of(0L), details);
    person.raise(PersonCreated.of(person.id(), person.tmdbId(), person.name()));
    return person;
  }
  
  public static Person rehydrate(PublicId id, TmdbId tmdbId, Version version, PersonDetails details) {
    return new Person(id, tmdbId, version, details);
  }
  
  public void update(PersonDetails details) {
    this.details = details;
    raise(PersonUpdated.of(id(), tmdbId(), name()));
  }
  
  public List<PersonEvent> pullEvents() {
    var events = List.copyOf(domainEvents);
    domainEvents.clear();
    return events;
  }
  
  public Name name() { return details.name(); }
  public Optional<BirthDate> birthDate() { return Optional.ofNullable(details.birthDate()); }
  public Optional<DeathDate> deathDate() { return Optional.ofNullable(details.deathDate()); }
  public Optional<Gender> gender() { return Optional.ofNullable(details.gender()); }
  public Optional<Biography> biography() { return Optional.ofNullable(details.biography()); }
  
  @Override
  public String toString() {
    return getClass().getSimpleName() + "[id=" + id().value()
        + ", tmdbId=" + tmdbId().value()
        + ", version=" + version().value()
        + ", name=" + name().value()
        + ", birthDate=" + birthDate().map(BirthDate::toLocalDate).orElse(null)
        + "]";
  }
  
  private void raise(PersonEvent event) {
    domainEvents.add(event);
  }
}
