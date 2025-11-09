package com.erdouglass.emdb.media.entity;

import java.time.LocalDate;
import java.util.Optional;

import org.hibernate.annotations.SoftDelete;

import com.erdouglass.emdb.common.query.MovieDto;
import com.erdouglass.emdb.common.query.ShowStatus;
import com.erdouglass.validation.DateRange;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.PositiveOrZero;

/// Represents the concrete `Movie` entity.
///
/// This class extends the `Show` mapped superclass and inherits all its fields
/// (like `title`, `overview`, etc.) as well as the `BasicEntity` fields
/// (like `id`, `created`, `modified`, `tmdbId`).
///
/// ## Key Features
///
/// * **Movie-Specific Data**: Adds fields not present in other show types,
///     such as `budget`, `releaseDate`, `runtime`, and `revenue`.
/// * **Soft Deletion**: This entity uses `@SoftDelete`, meaning records are
///     marked as deleted rather than being physically removed from the database.
/// * **Sequence**: Configures the `movie_sequence` for its primary key generation.
/// * **Null Safety**: Like its `Show` superclass, it uses `Optional`-wrapping
///     getters (e.g., `runtime()`) for all nullable fields.
///
/// @see com.erdouglass.emdb.media.entity.Show
/// @see com.erdouglass.emdb.media.entity.BasicEntity
@Entity
@SoftDelete
@Table(
    name = "Movies",
    uniqueConstraints = @UniqueConstraint(columnNames = {"name", "release_date"})
)
@SequenceGenerator(
    name = BasicEntity.SEQUENCE_GENERATOR, 
    sequenceName = "movie_sequence", 
    initialValue = 1, 
    allocationSize = 1)
public class Movie extends Show {
	
	@PositiveOrZero 
	private Integer budget;

	@Column(name = "release_date")
	@DateRange(min = MovieDto.MIN_DATE, max = MovieDto.MAX_DATE) 
	private LocalDate releaseDate;
	
	@PositiveOrZero 
	private Integer runtime;
	
	@PositiveOrZero 
	private Integer revenue;
	
	Movie() {
		
	}
	
	public Movie(String name, Integer tmdbId, ShowStatus status) {
		super(name, tmdbId, status);
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
      + ", name=" + name()
      + ", releaseDate=" + releaseDate
      + ", tmdbId=" + tmdbId()
      + ", status=" + status()
      + ", runtime=" + runtime
      + ", created=" + created()
      + ", modified=" + modified()
      + "]";
  }
	
}
