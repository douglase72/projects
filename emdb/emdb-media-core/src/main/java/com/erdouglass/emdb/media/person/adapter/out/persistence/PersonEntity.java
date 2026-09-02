package com.erdouglass.emdb.media.person.adapter.out.persistence;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Version;

import com.erdouglass.emdb.media.kernel.Name;
import com.erdouglass.emdb.media.person.domain.model.Biography;
import com.erdouglass.emdb.media.person.domain.model.Gender;

@Entity
@Table(
    name = "person",
    uniqueConstraints = {
      @UniqueConstraint(name = "uq_person_tmdb_id", columnNames = { "tmdb_id" }),
    }
  )
class PersonEntity {

  @Column(length = Biography.MAX_LENGTH)
  private String biography;
  
  @Column(name = "birth_date")
  private LocalDate birthDate;
  
  @Column(name = "death_date")
  private LocalDate deathDate;
  
  @Enumerated(EnumType.STRING)
  @Column(length = Gender.MAX_LENGTH)
  private Gender gender;
  
  @Id
  private UUID id;
  
  @Column(nullable = false, length = Name.MAX_LENGTH)
  private String name;
  
  @Column(name = "tmdb_id", nullable = false, updatable = false)
  private Integer tmdbId;
  
  /// One version on the root guards the whole aggregate. The adapter updates 
  /// the root on every aggregate save, so any concurrent change to any part of 
  /// the aggregate is detected here.
  @Version
  private Long version;
  
  PersonEntity() {}
  
  public void setBiography(String biography) { this.biography = biography; }
  public Optional<String> getBiography() { return Optional.ofNullable(biography); }
  
  public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }
  public Optional<LocalDate> getBirthDate() { return Optional.ofNullable(birthDate); }
  
  public void setDeathDate(LocalDate deathDate) { this.deathDate = deathDate; }
  public Optional<LocalDate> getDeathDate() { return Optional.ofNullable(deathDate); }
  
  public void setGender(Gender gender) { this.gender = gender; }
  public Optional<Gender> getGender() { return Optional.ofNullable(gender); }
  
  public void setId(UUID id) { this.id = id; }
  public UUID getId() { return id; }
  
  public void setName(String name) { this.name = name; }
  public String getName() { return name; }
  
  public void setTmdbId(Integer tmdbId) { this.tmdbId = tmdbId; }
  public Integer getTmdbId() { return tmdbId; }
  
  public void setVersion(long version) { this.version = version; }
  public Long getVersion() { return version; }
}
