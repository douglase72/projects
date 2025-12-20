package com.erdouglass.emdb.media.entity;

import java.time.LocalDate;
import java.util.Optional;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.PositiveOrZero;

import com.erdouglass.emdb.common.ShowConstants;
import com.erdouglass.emdb.common.ShowStatus;
import com.erdouglass.validation.DateRange;

/// A concrete entity representing a specific movie (film).
///
/// This class extends the generic {@link Show} to include film-specific
/// financial and temporal data, such as `budget`, `revenue`, and `runtime`.
///
/// It maps to the "Movies" table and uses the shared sequence generator
/// defined in {@link BasicEntity}.
@Entity
@Table(name = "Movies")
@SequenceGenerator(
    name = BasicEntity.SEQUENCE_GENERATOR, 
    sequenceName = "movie_sequence", 
    initialValue = 1, 
    allocationSize = 1)
public class Movie extends Show {

  /// The production budget of the movie, in dollars.
  /// Must be zero or positive.
  @PositiveOrZero
  private Integer budget;

  /// The theatrical release date of the movie.
  @Column(name = "release_date")
  @DateRange(min = ShowConstants.MOVIE_MIN_DATE, max = ShowConstants.MAX_DATE)
  private LocalDate releaseDate;

  /// The runtime of the movie, in minutes.
  /// Must be zero or positive.
  @PositiveOrZero
  private Integer runtime;

  /// The box office revenue of the movie, in dollars.
  /// Must be zero or positive.
  @PositiveOrZero
  private Integer revenue;

  Movie() {

  }

  public Movie(Integer tmdbId, String name, ShowStatus status) {
    super(tmdbId, name, status);
  }

  public void budget(Integer budget) {
    this.budget = budget;
  }

  public Optional<Integer> budget() {
    return Optional.ofNullable(budget);
  }

  public void releaseDate(LocalDate releaseDate) {
    this.releaseDate = releaseDate;
  }

  public Optional<LocalDate> releaseDate() {
    return Optional.ofNullable(releaseDate);
  }

  public void revenue(Integer revenue) {
    this.revenue = revenue;
  }

  public Optional<Integer> revenue() {
    return Optional.ofNullable(revenue);
  }

  public void runtime(Integer runtime) {
    this.runtime = runtime;
  }

  public Optional<Integer> runtime() {
    return Optional.ofNullable(runtime);
  }

  @Override
  public String toString() {
    return "Movie[id=" + id() 
        + ", tmdbId=" + tmdbId() 
        + ", name=" + name() 
        + ", releaseDate=" + releaseDate
        + ", status=" + status() 
        + ", created=" + created() 
        + ", modified=" + modified() + "]";
  }

}