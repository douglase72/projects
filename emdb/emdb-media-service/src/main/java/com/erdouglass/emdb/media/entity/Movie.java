package com.erdouglass.emdb.media.entity;

import java.time.LocalDate;

public class Movie {
  
  private Long id;
    
  private LocalDate releaseDate;
  
  private String title;
  
  private Integer tmdbId;
  
  public Movie() {
    id = 1L;
  }
  
  public Long getId() {
    return id;
  }
  
  public void setReleaseDate(LocalDate releaseDate) {
    this.releaseDate = releaseDate;
  }

  public LocalDate getReleaseDate() {
    return releaseDate;
  }
  
  public void setTitle(String title) {
    this.title = title;
  }
  
  public String getTitle() {
    return title;
  }
  
  public void setTmdbId(Integer tmdbId) {
    this.tmdbId = tmdbId;
  }
  
  public Integer getTmdbId() {
    return tmdbId;
  }
  
  @Override
  public String toString() {
    return "Movie[id=" + id
        + ", tmdbId=" + tmdbId
        + ", title=" + title
        + ", relaseDate=" + releaseDate
        + "]";
  }

}
