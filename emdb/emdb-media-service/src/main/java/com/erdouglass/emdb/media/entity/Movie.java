package com.erdouglass.emdb.media.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.PositiveOrZero;

import com.erdouglass.emdb.common.ShowConstants;
import com.erdouglass.validation.DateRange;

/// Represents a theatrical movie entity within the application.
///
/// Extends the [Show] base class with movie-specific attributes such as
/// production budget, box office revenue, theatrical release date, and runtime.
@Entity
@Table(name = "Movies")
@SequenceGenerator(
    name = BasicEntity.SEQUENCE_GENERATOR, 
    sequenceName = "movie_sequence", 
    initialValue = 1, 
    allocationSize = 1)
public class Movie extends Show {

  /// The production budget of the movie, in dollars. Must be zero or positive.
  @PositiveOrZero
  private Integer budget;

  /// The theatrical release date of the movie.
  @Column(name = "release_date")
  @DateRange(min = ShowConstants.MOVIE_MIN_DATE, max = ShowConstants.MAX_DATE)
  private LocalDate releaseDate;

  /// The runtime of the movie, in minutes. Must be zero or positive.
  @PositiveOrZero
  private Integer runtime;

  /// The box office revenue of the movie, in dollars. Must be zero or positive.
  @PositiveOrZero
  private Integer revenue;

  public Movie() {

  }

  public void setBudget(Integer budget) {
    this.budget = budget;
  }

  public Integer getBudget() {
    return budget;
  }

  public void setReleaseDate(LocalDate releaseDate) {
    this.releaseDate = releaseDate;
  }

  public LocalDate getReleaseDate() {
    return releaseDate;
  }

  public void setRevenue(Integer revenue) {
    this.revenue = revenue;
  }

  public Integer getRevenue() {
    return revenue;
  }

  public void setRuntime(Integer runtime) {
    this.runtime = runtime;
  }

  public Integer getRuntime() {
    return runtime;
  }

  @Override
  public String toString() {
    return "Movie[id=" + getId() 
      + ", tmdbId=" + getTmdbId() 
      + ", title=" + getTitle() 
      + ", releaseDate=" + getReleaseDate()
      + ", created=" + getCreated() 
      + ", modified=" + getModified() 
      + "]";
  }

}
