package com.erdouglass.emdb.media.movie;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class Movie {
  
  private Long id;
  
  @NotNull
  private LocalDate releaseDate;
  
  @NotBlank
  private String title;
    
  public Movie() {
    
  }
  
  public void setId(final Long id) {
    this.id = id;
  }
  
  public Long getId() {
    return id;
  }
  
  public void setReleaseDate(final LocalDate releaseDate) {
    this.releaseDate = releaseDate;
  }
  
  public LocalDate getReleaseDate() {
    return releaseDate;
  }
  
  public void setTitle(final String title) {
    this.title = title;
  }
  
  public String getTitle() {
    return title;
  }
  
  @Override
  public String toString() {
    return "Movie[id=" + id
        + ", title=" + title
        + ", releaseDate=" + releaseDate
        + "]";
  }
}
