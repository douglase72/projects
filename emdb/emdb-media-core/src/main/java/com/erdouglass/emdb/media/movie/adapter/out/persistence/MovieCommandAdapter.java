package com.erdouglass.emdb.media.movie.adapter.out.persistence;

import java.util.Optional;

import jakarta.data.exceptions.OptimisticLockingFailureException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import com.erdouglass.emdb.media.kernel.LanguageCode;
import com.erdouglass.emdb.media.kernel.Overview;
import com.erdouglass.emdb.media.kernel.Score;
import com.erdouglass.emdb.media.kernel.SourceId;
import com.erdouglass.emdb.media.kernel.Title;
import com.erdouglass.emdb.media.kernel.Version;
import com.erdouglass.emdb.media.movie.adapter.out.persistence.MovieCreditEntity.CreditType;
import com.erdouglass.emdb.media.movie.application.port.out.MovieCommandRepository;
import com.erdouglass.emdb.media.movie.domain.CastCredit;
import com.erdouglass.emdb.media.movie.domain.CrewCredit;
import com.erdouglass.emdb.media.movie.domain.Movie;
import com.erdouglass.emdb.media.movie.domain.MovieCredit;
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
  public Movie add(Movie movie) {
    var entity = repository.insert(toMovieEntity(movie));
    var credits = movie.credits().stream().map(c -> toMovieCreditEntity(c, entity)).toList();
    if (!credits.isEmpty()) {
      repository.insertCredits(credits);
    }
    return toMovie(entity);
  }

  /// Writes a modified title, checking the optimistic-locking version.
  ///
  /// @param movie the aggregate to persist, carrying the version it was read at
  /// @return the persisted aggregate with the incremented version
  /// @throws OptimisticLockingFailureException if the stored version has moved
  ///         on since the aggregate was loaded
  @Override
  public Movie update(Movie movie) {
    return toMovie(repository.update(toMovieEntity(movie)));
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
    //repository.deleteById(publicId.toLong());
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
  private MovieEntity toMovieEntity(Movie movie) {
    var details = movie.details();
    var entity = new MovieEntity();
    entity.setId(movie.publicId().map(MoviePublicId::toLong).orElse(null));
    entity.setSurrogateId(movie.id().value());
    entity.setSource(movie.sourceId().provider());
    entity.setSourceId(movie.sourceId().id());
    entity.setVersion(movie.version().value());
    entity.setTitle(movie.details().title().value());
    entity.setReleaseDate(details.releaseDate() != null ? details.releaseDate().toLocalDate() : null);
    entity.setScore(details.score() != null ? details.score().value() : null);
    entity.setOriginalLanguage(details.originalLanguage() != null ? details.originalLanguage().value() : null);
    entity.setOverview(details.overview() != null ? details.overview().value() : null);
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
    var details = MovieDetails.builder()
        .title(Title.of(entity.getTitle()))
        .releaseDate(entity.getReleaseDate().map(ReleaseDate::from).orElse(null))
        .score(entity.getScore().map(Score::of).orElse(null))
        .originalLanguage(entity.getOriginalLanguage().map(LanguageCode::of).orElse(null))
        .overview(entity.getOverview().map(Overview::of).orElse(null))
        .build();
    return Movie.rehydrate(id, publicId, sourceId, details, Version.of(entity.getVersion()));
  }
  
  private MovieCreditEntity toMovieCreditEntity(MovieCredit credit, MovieEntity movie) {
    var entity = new MovieCreditEntity();
    entity.id(credit.id().value());
    entity.movie(movie);
    entity.source(credit.sourceId().provider());
    entity.sourceId(credit.sourceId().id());
    entity.personId(credit.personId());
    entity.name(credit.name().value());
    switch (credit) {
      case CastCredit c -> {
        entity.creditType(CreditType.CAST);
        entity.role(c.character().value());
        entity.order(c.order().value());
      }
      case CrewCredit c -> {
        entity.creditType(CreditType.CREW);
        entity.role(c.job().value());
        entity.department(c.department().value());
      }
    }
    return entity;
  }
}
