package com.erdouglass.emdb.media.domain.movie;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.PositiveOrZero;

import com.erdouglass.common.validation.DateRange;
import com.erdouglass.emdb.media.api.ShowConstants;
import com.erdouglass.emdb.media.domain.Media;
import com.erdouglass.emdb.media.domain.Show;

/// A theatrical film entity. Extends [Show] with movie-specific fields
/// (budget, revenue, runtime, release date) and binds the inherited sequence
/// generator to the `movie_sequence` database sequence.
@Entity
@Table(
    name = "Movies",
    uniqueConstraints = @UniqueConstraint(
      name = "uk_movies_title_release_date",
      columnNames = { "title", "release_date" }
    )
  )
@SequenceGenerator(
  name = Media.SEQUENCE_GENERATOR, 
  sequenceName = "movie_sequence", 
  initialValue = 1, 
  allocationSize = 1)
class Movie extends Show {
  
  @PositiveOrZero
  private Integer budget;
    
  @Column(name = "release_date")
  @DateRange(min = ShowConstants.MOVIE_MIN_DATE, max = ShowConstants.MAX_DATE)
  private LocalDate releaseDate;
  
  @PositiveOrZero
  private Integer revenue;
  
  @PositiveOrZero
  private Integer runtime;
  
  /// Default constructor required by JPA.
  Movie() {}
  
  public void setBudget(final Integer budget) {
    this.budget = budget;
  }

  public Integer getBudget() {
    return budget;
  }  
  
  public void setReleaseDate(final LocalDate releaseDate) {
    this.releaseDate = releaseDate;
  }
  
  public LocalDate getReleaseDate() {
    return releaseDate;
  }
  
  public void setRevenue(final Integer revenue) {
    this.revenue = revenue;
  }

  public Integer getRevenue() {
    return revenue;
  }

  public void setRuntime(final Integer runtime) {
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
        + ", poster=" + getPoster()
        + "]";
  }
}
