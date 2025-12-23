package com.erdouglass.emdb.media.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

import com.erdouglass.emdb.common.CreditType;
import com.erdouglass.emdb.common.Gender;
import com.erdouglass.emdb.common.PersonConstants;
import com.erdouglass.validation.DateRange;

@Entity
@Table(name = "People")
@SequenceGenerator(
    name = BasicEntity.SEQUENCE_GENERATOR, 
    sequenceName = "person_sequence", 
    initialValue = 1,
    allocationSize = 50)
public class Person extends BasicEntity<Integer> {
  private static final int GENDER_MAX_LENGTH  = 10;
  
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
  
  @NotNull
  @Enumerated(EnumType.STRING)
  @Column(length = GENDER_MAX_LENGTH)
  private Gender gender;
  
  @NotBlank
  @Size(max = PersonConstants.NAME_MAX_LENGTH)
  private String name;
  
  @Size(min = PersonConstants.PROFILE_MIN_LENGTH, max = PersonConstants.PROFILE_MAX_LENGTH)
  private String profile;
  
  Person() {
    
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

  public Optional<LocalDate> birthDate() {
    return Optional.ofNullable(birthDate);
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
  
  public void deathDate(LocalDate deathDate) {
    this.deathDate = deathDate;
  }

  public Optional<LocalDate> deathDate() {
    return Optional.ofNullable(deathDate);
  }
  
  public void gender(Gender gender) {
    this.gender = gender;
  }
  
  public Gender gender() {
    return gender;
  }
  
  public void name(String name) {
    this.name = name;
  }

  public String name() {
    return name;
  }
  
  public void profile(String profile) {
    this.profile = profile;
  }

  public Optional<String> profile() {
    return Optional.ofNullable(profile);
  }
  
  @Override
  public String toString() {
    return "Person[id=" + id()
        + ", tmdbId=" + tmdbId() 
        + ", name=" + name()
        + ", birthDate=" + birthDate().orElse(null)
        + ", gender=" + gender() 
        + ", created=" + created() 
        + ", modified=" + modified() 
        + "]";
  }

}
