package com.erdouglass.emdb.media.person.domain.model;

import java.util.Objects;
import java.util.Optional;

import com.erdouglass.emdb.media.kernel.Name;
import com.erdouglass.emdb.media.kernel.TmdbId;
import com.erdouglass.emdb.media.kernel.Version;

/// Person aggregate.
///
/// A person carries three identifiers, each with a different lifetime and a
/// different audience:
///
/// * [PersonId] — surrogate id assigned by the application when the aggregate is
///   created. This is the Java identity used by [#equals(Object)] and
///   [#hashCode()], and it is never exposed to the public.
/// * [PersonPublicId] — public-facing id derived from the key the database
///   assigns on first insert. Absent until the aggregate has been persisted.
/// * [TmdbId] — natural id supplied by the upstream catalogue, present from
///   creation and never reassigned.
public final class Person {
  private final PersonId id;
  private final TmdbId tmdbId;
  private final Version version;
  
  private PersonDetails details;
  
  private Person(
      PersonId id, 
      TmdbId tmdbId, 
      Version version, 
      PersonDetails details) {
    this.id = Objects.requireNonNull(id, "id is required");
    this.tmdbId = Objects.requireNonNull(tmdbId, "TMDB id is required");
    this.version = Objects.requireNonNull(version, "version is required");
    this.details = Objects.requireNonNull(details, "details are required");
  }
  
  public static Person create(TmdbId tmdbId, PersonDetails details) {
    Person person = new Person(PersonId.newId(), tmdbId, Version.of(0L), details);
    return person;
  }
  
  public static Person rehydrate(
      PersonId id, 
      TmdbId tmdbId, 
      Version version, 
      PersonDetails details) {
    return new Person(id, tmdbId, version, details);
  }
  
  public void update(PersonDetails details) {
    this.details = Objects.requireNonNull(details, "details are required");
  }
  
  public PersonId id() { return id; } 
  public TmdbId tmdbId() { return tmdbId; }
  public Version version() { return version; }
  public Name name() { return details.name(); }
  public Optional<BirthDate> birthDate() { return Optional.ofNullable(details.birthDate()); }
  public Optional<DeathDate> deathDate() { return Optional.ofNullable(details.deathDate()); }
  public Optional<Gender> gender() { return Optional.ofNullable(details.gender()); }
  public Optional<Biography> biography() { return Optional.ofNullable(details.biography()); }
  
  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
  
  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Person other = (Person) obj;
    return Objects.equals(id, other.id);
  }
  
  @Override
  public String toString() {
    return getClass().getSimpleName() + "[id=" + id.value()
        + ", tmdbId=" + tmdbId.value()
        + ", version=" + version.value()
        + ", name=" + name().value()
        + ", birthDate=" + birthDate().map(BirthDate::toLocalDate).orElse(null)
        + ", deathDate=" + deathDate().map(DeathDate::toLocalDate).orElse(null) 
        + ", gender=" + gender().orElse(null)
        + "]";
  }
}
