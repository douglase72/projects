package com.erdouglass.emdb.media.movie.adapter.out.persistence;

import java.util.Optional;

import jakarta.data.exceptions.OptimisticLockingFailureException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import com.erdouglass.emdb.media.SourceId;
import com.erdouglass.emdb.media.kernel.LanguageCode;
import com.erdouglass.emdb.media.kernel.Overview;
import com.erdouglass.emdb.media.kernel.Score;
import com.erdouglass.emdb.media.kernel.Title;
import com.erdouglass.emdb.media.kernel.Version;
import com.erdouglass.emdb.media.movie.application.port.out.MovieCommandRepository;
import com.erdouglass.emdb.media.movie.domain.Movie;
import com.erdouglass.emdb.media.movie.domain.MovieDetails;
import com.erdouglass.emdb.media.movie.domain.MovieId;
import com.erdouglass.emdb.media.movie.domain.MoviePublicId;
import com.erdouglass.emdb.media.movie.domain.ReleaseDate;

/// Translates between the movie aggregate and its persistent row.
///
/// The seam that keeps JPA out of the domain: everything above this class works
/// in aggregates and value objects, everything below in entities and column
/// types. Validation runs in both directions as a side effect of that
/// translation, since rebuilding value objects re-applies their invariants — a
/// row that violates them fails loudly on load rather than propagating.
///
/// Holds no transaction of its own; callers supply one.
@ApplicationScoped
class MovieCommandAdapter implements MovieCommandRepository {
  private static final long INITIAL_VERSION = 0L;
  
  @Inject
  JakartaDataMovieCommandRepository repository;

  /// Writes a new title and returns it with its database-assigned identity.
  ///
  /// The returned aggregate is not the one passed in: it carries the public id
  /// and version the database supplied, which the argument could not have had.
  /// Callers must use the return value for anything that needs those, including
  /// audit attribution.
  ///
  /// @param movie the unpersisted aggregate
  /// @return the persisted aggregate, now reporting a public id and version
  @Override
  public Movie insert(Movie movie) {
    return toMovie(repository.insert(toMovieEntity(movie, INITIAL_VERSION)));
  }

  /// Writes a modified title, checking the optimistic-locking version.
  ///
  /// @param movie the aggregate to persist, carrying the version it was read at
  /// @return the persisted aggregate with the incremented version
  /// @throws OptimisticLockingFailureException if the stored version has moved
  ///         on since the aggregate was loaded
  @Override
  public Movie update(Movie movie) {
    var version = movie.version().map(Version::value)
        .orElseThrow(() -> new IllegalArgumentException("invalid version"));
    return toMovie(repository.update(toMovieEntity(movie, version)));
  }
  
  /// Removes the title with the given catalogue id.
  ///
  /// Deleting an id that no longer exists is not reported as an error here; the
  /// service checks existence before calling, so it can produce a `404` and can
  /// close out the audit trail first.
  ///
  /// @param publicId the catalogue id of the title to remove
  @Override
  public void deleteByPublicId(MoviePublicId publicId) {
    repository.deleteById(publicId.toLong());
  }
  
  /// Loads a title by its catalogue id.
  ///
  /// @param publicId the catalogue id
  /// @return the rehydrated aggregate, or empty if none carries that id
  /// @throws IllegalArgumentException if a stored value no longer satisfies its
  ///         domain invariants
  @Override
  public Optional<Movie> findByPublicId(MoviePublicId publicId) {
    return repository.findById(publicId.toLong()).map(this::toMovie);
  }
  
  /// Loads a title by its natural id, the lookup that turns ingestion into an
  /// upsert.
  ///
  /// @param sourceId the natural id from the upstream catalogue
  /// @return the rehydrated aggregate, or empty if the title is new
  /// @throws IllegalArgumentException if a stored value no longer satisfies its
  ///         domain invariants
  @Override
  public Optional<Movie> findBySourceId(SourceId sourceId) {
    return repository.findBySourceId(sourceId.provider(), sourceId.id()).map(this::toMovie);
  }
  
  /// Flattens an aggregate into a row.
  ///
  /// @param movie the aggregate to flatten
  /// @return the row to write
  private MovieEntity toMovieEntity(Movie movie, long version) {
    var entity = new MovieEntity();
    entity.setId(movie.publicId().map(MoviePublicId::toLong).orElse(null));
    entity.setSurrogateId(movie.id().value());
    entity.setSource(movie.sourceId().provider());
    entity.setSourceId(movie.sourceId().id());
    entity.setVersion(version);
    entity.setTitle(movie.details().title().value());
    entity.setReleaseDate(movie.details().releaseDate().map(ReleaseDate::toLocalDate).orElse(null));
    entity.setScore(movie.details().score().map(Score::value).orElse(null));
    entity.setOriginalLanguage(movie.details().originalLanguage().map(LanguageCode::value).orElse(null));
    entity.setOverview(movie.details().overview().map(Overview::value).orElse(null));
    return entity;
  }
  
  /// Rebuilds an aggregate from a row.
  ///
  /// Goes through `Movie.rehydrate` rather than the creation factory, so a
  /// locked title can be loaded and its stored identity is preserved rather than
  /// regenerated. Every column passes back through its value object, so an
  /// invalid row surfaces here.
  ///
  /// @param entity the row to rebuild from
  /// @return the aggregate
  /// @throws IllegalArgumentException if a stored value violates a domain
  ///         invariant, for example a score outside 0 to 10
  private Movie toMovie(MovieEntity entity) {
    var id = MovieId.of(entity.getSurrogateId());
    var publicId = MoviePublicId.from(entity.getId());
    var sourceId = SourceId.of(entity.getSource(), entity.getSourceId());
    var details = new MovieDetails(
        Title.of(entity.getTitle()),
        entity.getReleaseDate().map(ReleaseDate::from),
        entity.getScore().map(Score::of),
        entity.getOriginalLanguage().map(LanguageCode::of),
        entity.getOverview().map(Overview::of));
    return Movie.rehydrate(id, publicId, sourceId, details, Version.of(entity.getVersion()));
  }
}
