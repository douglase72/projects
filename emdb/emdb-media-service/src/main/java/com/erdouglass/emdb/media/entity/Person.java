package com.erdouglass.emdb.media.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

import com.erdouglass.emdb.common.CreditType;
import com.erdouglass.emdb.common.Gender;
import com.erdouglass.emdb.common.PersonConstants;
import com.erdouglass.emdb.common.ShowConstants;
import com.erdouglass.validation.DateRange;

/// Represents a Person (Actor, Director, Crew) in the media database.
///
/// Uniqueness is determined by the `tmdbId` (The Movie Database ID).
@Entity
@Table(name = "People")
@SequenceGenerator(
    name = BasicEntity.SEQUENCE_GENERATOR, 
    sequenceName = "person_sequence", 
    initialValue = 1, 
    allocationSize = 50)
public class Person extends BasicEntity<Integer> {

  @Size(max = PersonConstants.BIOGRAPHY_MAX_LENGTH)
  private String biography;

  @Past
  @Column(name = "birth_date")
  @DateRange(min = PersonConstants.MIN_DATE, max = PersonConstants.MAX_DATE)
  private LocalDate birthDate;

  @Size(max = PersonConstants.BIRTH_PLACE_MAX_LENGTH)
  @Column(name = "birth_place")
  private String birthPlace;
  
  /// The credits collection in a person is a bidirectional association 
  /// specified by the mappedBy field which maps the {@link Person#id} primary
  /// key to the foreign key in the Credits table.
  @OneToMany(mappedBy = _Credit.PERSON)
  private List<Credit> credits = new ArrayList<>();  

  @Past
  @Column(name = "death_date")
  @DateRange(min = PersonConstants.MIN_DATE, max = PersonConstants.MAX_DATE)
  private LocalDate deathDate;

  @Enumerated(EnumType.STRING)
  @Column(length = PersonConstants.GENDER_MAX_LENGTH)
  private Gender gender;

  @NotBlank
  @Size(max = PersonConstants.NAME_MAX_LENGTH)
  private String name;

  @Column(unique = true)
  private UUID profile;
  
  @Column(name="tmdb_profile")
  @Size(min = ShowConstants.POSTER_MIN_LENGTH, max = ShowConstants.POSTER_MAX_LENGTH)
  private String tmdbProfile;

  Person() {

  }
  
  public Person(String name) {
    this.name = name;
  }

  public Person(Integer tmdbId, String name) {
    super(tmdbId);
    this.name = name;
  }

  public void biography(String biography) {
    this.biography = biography;
  }

  public Optional<String> biography() {
    return Optional.ofNullable(biography);
  }

  public Optional<String> birthPlace() {
    return Optional.ofNullable(birthPlace);
  }

  public void birthPlace(String birthPlace) {
    this.birthPlace = birthPlace;
  }

  public void birthDate(LocalDate birthDate) {
    this.birthDate = birthDate;
  }
  
  public List<Credit> cast() {
    return credits.stream().filter(c -> c.type() == CreditType.CAST).toList();
  }
  
  public void credits(List<Credit> credits) {
    this.credits = new ArrayList<>(credits);
  }
  
  public List<Credit> credits() {
    return List.copyOf(credits);
  }
  
  public List<Credit> crew() {
    return credits.stream().filter(c -> c.type() == CreditType.CREW).toList();
  }  

  public Optional<LocalDate> birthDate() {
    return Optional.ofNullable(birthDate);
  }

  public void deathDate(LocalDate deathDate) {
    this.deathDate = deathDate;
  }

  public Optional<LocalDate> deathDate() {
    return Optional.ofNullable(deathDate);
  }

  public void gender(Gender gender) {
    this.gender = gender;
  }

  public Optional<Gender> gender() {
    return Optional.ofNullable(gender);
  }

  public void name(String name) {
    this.name = name;
  }

  public String name() {
    return name;
  }

  public void profile(UUID profile) {
    this.profile = profile;
  }

  public Optional<UUID> profile() {
    return Optional.ofNullable(profile);
  }
  
  public void tmdbProfile(String tmdbProfile) {
    this.tmdbProfile = tmdbProfile;
  }
  
  public Optional<String> tmdbProfile() {
    return Optional.ofNullable(tmdbProfile);
  }

  /// Checks if the business data of this person matches another person.
  ///
  /// @param other The person to compare against.
  /// @return `true` if all mutable fields are effectively equal;
  ///         `false` otherwise.
  public boolean isEqualTo(Person other) {
    return Objects.equals(name, other.name)
        && Objects.equals(birthDate, other.birthDate) 
        && Objects.equals(deathDate, other.deathDate)
        && Objects.equals(gender, other.gender) 
        && Objects.equals(birthPlace, other.birthPlace)
        && Objects.equals(profile, other.profile) 
        && Objects.equals(tmdbProfile, other.tmdbProfile)
        && Objects.equals(biography, other.biography);
  }

  @Override
  public String toString() {
    return "Person[id=" + id() 
    + ", tmdbId=" + tmdbId() 
    + ", name=" + name() 
    + ", birthDate=" + birthDate().orElse(null)
    + ", created=" + created() 
    + ", modified=" + modified() + "]";
  }

}