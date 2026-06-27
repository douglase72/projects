package com.erdouglass.emdb.ingest.core.person;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "people")
class Person {

  /// The primary key is a natural key as the TMDB Id set by the application.
  @Id
  private Integer id;
  
  @Column(name = "emdb_profile", unique = true)
  private UUID emdbProfile;
  
  @Column(name = "tmdb_profile", unique = true)
  private String tmdbProfile;
  
  Person(Integer id) {
    this.id = id;
  }
  
  public void setEmdbProfile(UUID emdbProfile) {
    this.emdbProfile = emdbProfile;
  }
   
  public UUID getEmdbProfile() {
    return emdbProfile;
  }
  
  public void setTmdbProfile(String tmdbProfile) {
    this.tmdbProfile = tmdbProfile;
  }
   
  public String getTmdbProfile() {
    return tmdbProfile;
  }  
}
