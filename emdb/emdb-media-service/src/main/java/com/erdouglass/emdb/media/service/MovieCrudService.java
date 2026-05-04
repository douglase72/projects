package com.erdouglass.emdb.media.service;

import java.util.Objects;
import java.util.Optional;

import jakarta.data.page.Page;
import jakarta.data.page.PageRequest;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import com.erdouglass.emdb.media.annotation.Logged;
import com.erdouglass.emdb.media.api.command.SaveMovie;
import com.erdouglass.emdb.media.api.command.UpdateMovie;
import com.erdouglass.emdb.media.api.query.MovieQueryParameters;
import com.erdouglass.emdb.media.api.query.MovieView;
import com.erdouglass.emdb.media.entity.Movie;
import com.erdouglass.emdb.media.mapper.MovieMapper;
import com.erdouglass.emdb.media.query.SaveResult;
import com.erdouglass.emdb.media.query.SaveResult.SaveStatus;
import com.erdouglass.emdb.media.repository.MovieRepository;
import com.erdouglass.webservices.ResourceNotFoundException;

/// Business logic for movie operations.
///
/// Sits between the gRPC controller and the Jakarta Data repository,
/// applying validation, orchestrating upsert semantics, and translating
/// between command DTOs and JPA entities via [MovieMapper].
@ApplicationScoped
public class MovieCrudService {
  
  @Inject
  MovieMapper mapper;
  
  @Inject
  MovieRepository repository;
  
  /// Creates a movie if none exists for the given TMDB ID, otherwise
  /// updates the existing movie if the incoming command differs from
  /// the stored state.
  ///
  /// Returns a [SaveResult] with one of three statuses: `CREATED` for
  /// a new insert, `UPDATED` for a modification, or `UNCHANGED` if the
  /// command matched the existing state exactly and no write occurred.
  /// The unchanged case avoids unnecessary database writes and index
  /// churn during bulk imports from TMDB.
  ///
  /// @param command the movie data to persist
  /// @return the save result containing the status and the resulting entity
  @Transactional
  @Logged("Saved:")
  public SaveResult<Movie> save(@NotNull @Valid SaveMovie command) {
    var status = SaveStatus.UNCHANGED;
    var existingMovie = repository.findByTmdbId(command.tmdbId()).orElse(null);
    if (existingMovie == null) {
      existingMovie = repository.insert(mapper.toMovie(command));
      status = SaveStatus.CREATED;
    } else if (!isEqual(command, existingMovie)) {
      mapper.merge(command, existingMovie);
      repository.update(existingMovie);
      status = SaveStatus.UPDATED;
    }
    return SaveResult.of(status, existingMovie);
  }
  
  /// Retrieves a paginated list of [MovieView] projections.
  ///
  /// Uses `withoutTotal()` on the page request to skip the `COUNT(*)`
  /// query, since the browsing UI relies on `hasNext` rather than total
  /// page counts.
  ///
  /// @param parameters pagination and sorting options
  /// @return a page of movie projections  
  @Transactional
  @Logged(value = "Found:", subject = "movies")
  public Page<MovieView> findAll(@NotNull @Valid MovieQueryParameters parameters) {
    var pageRequest = PageRequest.ofPage(parameters.page(), parameters.size(), false);
    var results = repository.find(pageRequest);
    return results;
  }
  
  /// Retrieves a single movie by primary key.
  ///
  /// @param id the movie's primary key
  /// @param append optional comma-separated list of associations to include
  /// @return an [Optional] containing the movie if found, or empty if not  
  @Transactional
  @Logged("Found:")
  public Optional<Movie> findById(@NotNull @Positive Long id, String append) {
    var movie = repository.findById(id);
    return movie;
  }
  
  /// Retrieves a single movie by TMDB id.
  ///
  /// @param tmdbId the movie's TMDB id
  /// @param append optional comma-separated list of associations to include
  /// @return an [Optional] containing the movie if found, or empty if not  
  @Transactional
  @Logged("Found:")
  public Optional<Movie> findByTmdbId(@NotNull @Positive Integer tmdbId, String append) {
    return repository.findByTmdbId(tmdbId);
  }

  /// Updates an existing movie's mutable fields.
  ///
  /// @param id the movie's primary key
  /// @param command the fields to update
  /// @return the updated movie
  /// @throws ResourceNotFoundException if no movie exists with the given id  
  @Transactional
  @Logged("Updated:")
  public Movie update(Long id, UpdateMovie command) {
    var existingMovie = repository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("No movie found with id: " + id));
    mapper.merge(command, existingMovie);
    return repository.update(existingMovie);
  }

  /// Deletes a movie by primary key and returns the deleted entity.
  ///
  /// Fetching the movie before deletion allows callers to log or audit
  /// what was removed. Throws if the movie doesn't exist rather than
  /// silently succeeding on a no-op delete.
  ///
  /// @param id the movie's primary key
  /// @return the movie that was deleted
  /// @throws ResourceNotFoundException if no movie exists with the given id  
  @Transactional
  @Logged("Deleted:")
  public Movie delete(Long id) {
    var movie = repository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("No movie found with id: " + id));
    repository.deleteById(id);
    return movie;
  }

  /// Determines whether a save command matches the existing movie state.
  ///
  /// Used by [#save] to skip unnecessary updates when the incoming data
  /// is identical to what's already persisted. Compares only the fields
  /// that are modifiable via [SaveMovie] — `id`, `tmdbId`, and audit
  /// timestamps are not considered.  
  private boolean isEqual(SaveMovie command, Movie movie) {
    if (command == null || movie == null) return false;
    return Objects.equals(command.tmdbId(), movie.getTmdbId())
        && Objects.equals(command.title(), movie.getTitle())
        && Objects.equals(command.releaseDate(), movie.getReleaseDate())
        && Objects.equals(command.score(), movie.getScore())
        && Objects.equals(command.status(), movie.getStatus())
        && Objects.equals(command.runtime(), movie.getRuntime())
        && Objects.equals(command.budget(), movie.getBudget())
        && Objects.equals(command.backdrop() != null ? command.backdrop().name() : null, movie.getBackdrop())
        && Objects.equals(command.poster() != null ? command.poster().name() : null, movie.getPoster())
        && Objects.equals(command.homepage(), movie.getHomepage())
        && Objects.equals(command.originalLanguage(), movie.getOriginalLanguage())
        && Objects.equals(command.overview(), movie.getOverview());
  }
  
}
