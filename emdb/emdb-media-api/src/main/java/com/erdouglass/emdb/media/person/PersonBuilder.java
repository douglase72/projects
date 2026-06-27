package com.erdouglass.emdb.media.person;

import java.time.LocalDate;

import com.erdouglass.emdb.media.image.Image;

public abstract class PersonBuilder<T> {
  protected String biography;
  protected LocalDate birthDate;
  protected String birthPlace;
  protected LocalDate deathDate;
  protected Gender gender;
  protected String homepage;
  protected String name;
  protected Image profile;
  
  public T biography(final String biography) {
    this.biography = biography;
    return self();
  }
  
  public T birthDate(final LocalDate birthDate) {
    this.birthDate = birthDate;
    return self();
  }
  
  public T birthPlace(final String birthPlace) {
    this.birthPlace = birthPlace;
    return self();
  }
  
  public T deathDate(LocalDate deathDate) {
    this.deathDate = deathDate;
    return self();
  }
  
  public T gender(final Gender gender) {
    this.gender = gender;
    return self();
  }
  
  public T homepage(final String homepage) {
    this.homepage = homepage;
    return self();
  } 
  
  public T name(final String name) {
    this.name = name;
    return self();
  }
  
  public T profile(final Image profile) {
    this.profile = profile;
    return self();
  }
  
  protected abstract T self();
}
