package com.erdouglass.emdb.media.domain.person;

import java.time.LocalDate;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

import com.erdouglass.common.validation.DateRange;
import com.erdouglass.emdb.common.Configuration;
import com.erdouglass.emdb.media.domain.internal.Media;
import com.erdouglass.emdb.media.person.Gender;
import com.erdouglass.emdb.media.person.PersonConstants;
import com.erdouglass.emdb.media.show.ShowConstants;

@Entity
@Table(name = "People")
@SequenceGenerator(
    name = Media.SEQUENCE_GENERATOR, 
    sequenceName = "person_sequence", 
    initialValue = 1, 
    allocationSize = 50)
public class Person extends Media {

  @Size(max = PersonConstants.BIOGRAPHY_MAX_LENGTH)
  private String biography;
  
  @Past
  @Column(name = "birth_date")
  @DateRange(min = PersonConstants.MIN_DATE, max = PersonConstants.MAX_DATE)
  private LocalDate birthDate;
  
  @Column(name = "birth_place")
  @Size(max = PersonConstants.BIRTH_PLACE_MAX_LENGTH)
  private String birthPlace;
  
  @Past
  @Column(name = "death_date")
  @DateRange(min = PersonConstants.MIN_DATE, max = PersonConstants.MAX_DATE)
  private LocalDate deathDate;
  
  @NotNull 
  @Enumerated(EnumType.STRING)
  @Column(length = PersonConstants.GENDER_MAX_LENGTH)
  private Gender gender;
  
  @Size(max = Configuration.URL_MAX_LENGTH)
  private String homepage;
  
  @NotBlank
  @Size(max = PersonConstants.NAME_MAX_LENGTH)
  private String name;
  
  @Column(unique = true)
  private UUID profile;
  
  @Column(name="tmdb_profile", unique = true)
  @Size(min = ShowConstants.POSTER_MIN_LENGTH, max = ShowConstants.POSTER_MAX_LENGTH)
  private String tmdbProfile;
  
  /// Default constructor required by JPA.
  Person() {}
  
  public Person(final int tmdbId) {
    super(tmdbId);
  }
    
  public void setBiography(String biography) {
    this.biography = biography;
  }

  public String getBiography() {
    return biography;
  }
  
  public void setBirthDate(LocalDate birthDate) {
    this.birthDate = birthDate;
  }
  
  public LocalDate getBirthDate() {
    return birthDate;
  }
  
  public void setBirthPlace(String birthPlace) {
    this.birthPlace = birthPlace;
  }
  
  public String getBirthPlace() {
    return birthPlace;
  }
  
  public void setDeathDate(LocalDate deathDate) {
    this.deathDate = deathDate;
  }

  public LocalDate getDeathDate() {
    return deathDate;
  }
  
  public void setGender(Gender gender) {
    this.gender = gender;
  }

  public Gender getGender() {
    return gender;
  }
  
  public void setHomepage(String homepage) {
    this.homepage = homepage;
  }

  public String getHomepage() {
    return homepage;
  }
  
  public void setName(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }
  
  public void setProfile(UUID profile) {
    this.profile = profile;
  }

  public UUID getProfile() {
    return profile;
  }
  
  public void setTmdbProfile(String tmdbProfile) {
    this.tmdbProfile = tmdbProfile;
  }
  
  public String getTmdbProfile() {
    return tmdbProfile;
  } 
  
  @Override
  public String toString() {
    return "Person[id=" + getId()
    + ", tmdbId=" + getTmdbId() 
    + ", name=" + getName() 
    + ", birthDate=" + getBirthDate()
    + ", deathDate=" + getDeathDate() 
    + ", gender=" + getGender()
    + ", profile=" + getProfile()
    + "]";
  }
}
