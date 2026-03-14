package com.erdouglass.emdb.media.entity;

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

import com.erdouglass.emdb.common.Gender;
import com.erdouglass.emdb.common.PersonConstants;
import com.erdouglass.validation.DateRange;

/// Represents a Person (Actor, Director, Crew) in the media database.
///
/// Uniqueness is determined by the `tmdbId` (The Movie Database ID).
@Entity
@Table(name = "People")
@SequenceGenerator(
  name = SequenceEntity.SEQUENCE_GENERATOR, 
  sequenceName = "person_sequence", 
  initialValue = 1, 
  allocationSize = 50)
public class Person extends SequenceEntity {
  
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
  
  @Enumerated(EnumType.STRING)
  @Column(length = PersonConstants.GENDER_MAX_LENGTH)
  private Gender gender;
  
  @NotBlank
  @Size(max = PersonConstants.NAME_MAX_LENGTH)
  private String name;
  
  @Column(unique = true)
  private UUID profile;
  
  @NotNull
  @Column(name = "tmdb_id", unique = true, updatable = false)
  private Integer tmdbId;  
  
  public Person() {
    
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
  
  public void setTmdbId(Integer tmdbId) {
    this.tmdbId = tmdbId;
  }
  
  public Integer getTmdbId() {
    return tmdbId;
  }
  
  @Override
  public String toString() {
    return "Person[" 
    + "tmdbId=" + getTmdbId() 
    + ", name=" + getName() 
    + ", birthDate=" + getBirthDate()
    + ", deathDate=" + getDeathDate() 
    + ", gender=" + getGender()
    + ", profile=" + getProfile()
    + ", birthPlace=" + getBirthPlace()
    + "]";
  } 
  
}
