package com.erdouglass.emdb.common;

import java.time.LocalDate;

public abstract class AbstractPersonBuilder<T> {
  protected String biography;
  protected LocalDate birthDate;
  protected String birthPlace;
  protected LocalDate deathDate;
  protected Gender gender;
  protected String homepage;
  protected String name;
  protected Integer tmdbId;
  
  public T biography(String biography) {
    this.biography = biography;
    return self();
  }
  
  public T birthDate(LocalDate birthDate) {
    this.birthDate = birthDate;
    return self();
  }
  
  public T birthPlace(String birthPlace) {
    this.birthPlace = birthPlace;
    return self();
  }
  
  public T deathDate(LocalDate deathDate) {
    this.deathDate = deathDate;
    return self();
  }
  
  public T gender(Gender gender) {
    this.gender = gender;
    return self();
  }
  
  public T homepage(String homepage) {
    this.homepage = homepage;
    return self();
  } 
  
  public T name(String name) {
    this.name = name;
    return self();
  }
  
  public T tmdbId(Integer tmdbId) {
    this.tmdbId = tmdbId;
    return self();
  }
  
  protected abstract T self();

}
