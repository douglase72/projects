package com.erdouglass.emdb.ingest.core.movie;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "Tmdb_Movies")
class Movie {

  /// The primary key is a natural key as the TMDB Id set by the application.
  @Id
  private Integer id;
  
  @Column(name = "emdb_backdrop", unique = true)
  private UUID emdbBackdrop;
  
  @Column(name = "emdb_poster", unique = true)
  private UUID emdbPoster;
  
  @Column(name = "tmdb_backdrop", unique = true)
  private String tmdbBackdrop;
  
  @Column(name = "tmdb_poster", unique = true)
  private String tmdbPoster;
  
  Movie(Integer id) {
    this.id = id;
  }
  
  public void setEmdbBackdrop(UUID emdbBackdrop) {
    this.emdbBackdrop = emdbBackdrop;
  }
   
  public UUID getEmdbBackdrop() {
    return emdbBackdrop;
  }
  
  public void setEmdbPoster(UUID emdbPoster) {
    this.emdbPoster = emdbPoster;
  }
   
  public UUID getEmdbPoster() {
    return emdbPoster;
  } 
  
  public void setTmdbBackdrop(String tmdbBackdrop) {
    this.tmdbBackdrop = tmdbBackdrop;
  }
   
  public String getTmdbBackdrop() {
    return tmdbBackdrop;
  }
  
  public void setTmdbPoster(String tmdbPoster) {
    this.tmdbPoster = tmdbPoster;
  }
   
  public String getTmdbPoster() {
    return tmdbPoster;
  } 
}
