package com.erdouglass.emdb.media.movie.adapter.out.persistence;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import com.erdouglass.emdb.media.kernel.LanguageCode;
import com.erdouglass.emdb.media.kernel.Overview;
import com.erdouglass.emdb.media.kernel.Score;
import com.erdouglass.emdb.media.kernel.Title;
import com.erdouglass.emdb.media.kernel.TmdbId;
import com.erdouglass.emdb.media.kernel.Version;
import com.erdouglass.emdb.media.movie.adapter.out.persistence.MovieCreditEntity.CreditType;
import com.erdouglass.emdb.media.movie.application.port.out.MovieCommandRepository;
import com.erdouglass.emdb.media.movie.domain.model.CastDetails;
import com.erdouglass.emdb.media.movie.domain.model.CrewDetails;
import com.erdouglass.emdb.media.movie.domain.model.Movie;
import com.erdouglass.emdb.media.movie.domain.model.MovieCredit;
import com.erdouglass.emdb.media.movie.domain.model.MovieDetails;
import com.erdouglass.emdb.media.movie.domain.model.MovieId;
import com.erdouglass.emdb.media.movie.domain.model.MoviePublicId;
import com.erdouglass.emdb.media.movie.domain.model.ReleaseDate;

@ApplicationScoped
class MovieCommandAdapter implements MovieCommandRepository {
  
  @Inject
  JakartaDataMovieCommandRepository repository;

  @Override
  public Movie insert(Movie movie) {
    var entity = repository.insert(toMovieEntity(movie)); 
    var credits = movie.credits().stream().map(c -> toMovieCreditEntity(c, entity)).toList();
    if (!credits.isEmpty()) {
      repository.insertCredits(credits);
    }
    return toMovie(entity);
  }

  @Override
  public void update(Movie movie) {
    var entity = repository.update(toMovieEntity(movie)); 
    var current = movie.credits().stream().map(c -> toMovieCreditEntity(c, entity)).toList();
    var currentIds = current.stream().map(MovieCreditEntity::getId).collect(Collectors.toSet());
    var storedIds = Set.copyOf(repository.findCreditIds(entity.getId()));
    
    var creditsToDelete = storedIds.stream().filter(id -> !currentIds.contains(id)).toList();
    if (!creditsToDelete.isEmpty()) {
      repository.deleteCredits(entity.getId(), creditsToDelete);
    }
    
    var creditsToUpdate = current.stream().filter(c -> storedIds.contains(c.getId())).toList();
    if (!creditsToUpdate.isEmpty()) {
      repository.updateCredits(creditsToUpdate);
    }
    
    var creditsToInsert = current.stream().filter(c -> !storedIds.contains(c.getId())).toList();
    if (!creditsToInsert.isEmpty()) {
      repository.insertCredits(creditsToInsert);
    }
  }

  @Override
  public Optional<Movie> findByTmdbId(TmdbId tmdbId) {
    return repository.findByTmdbIdId(tmdbId.value()).map(this::toMovie);
  }
  
  private MovieEntity toMovieEntity(Movie movie) {
    var entity = new MovieEntity();
    entity.setId(movie.publicId().map(MoviePublicId::toLong).orElse(null));
    entity.setSurrogateId(movie.id().value());
    entity.setTmdbId(movie.tmdbId().value());
    entity.setVersion(movie.version().value());
    entity.setTitle(movie.title().value());
    entity.setReleaseDate(movie.releaseDate().map(ReleaseDate::toLocalDate).orElse(null));
    entity.setScore(movie.score().map(Score::value).orElse(null));
    entity.setOriginalLanguage(movie.originalLanguage().map(LanguageCode::value).orElse(null));
    entity.setOverview(movie.overview().map(Overview::value).orElse(null));
    return entity;
  }
  
  private Movie toMovie(MovieEntity entity) {
    var id = MovieId.of(entity.getSurrogateId());
    var publicId = MoviePublicId.from(entity.getId());
    var tmdbId = TmdbId.of(entity.getTmdbId());
    var version = Version.of(entity.getVersion());
    var details = MovieDetails.builder()
        .title(Title.of(entity.getTitle()))
        .releaseDate(entity.getReleaseDate().map(ReleaseDate::from).orElse(null))
        .score(entity.getScore().map(Score::of).orElse(null))
        .originalLanguage(entity.getOriginalLanguage().map(LanguageCode::of).orElse(null))
        .overview(entity.getOverview().map(Overview::of).orElse(null))
        .build();
    return Movie.rehydrate(id, publicId, tmdbId, version, details, List.of());
  }
  
  private MovieCreditEntity toMovieCreditEntity(MovieCredit credit, MovieEntity movie) {
    var entity = new MovieCreditEntity();
    entity.setId(credit.id().value());
    entity.setTmdbId(credit.tmdbId().value());
    entity.setMovie(movie);
    entity.setPersonId(credit.personId().toLong());
    entity.setName(credit.name().value());
    switch (credit.details()) {
      case CastDetails c -> {
        entity.setCreditType(CreditType.CAST);
        entity.setRole(c.character().value());
        entity.setOrder(c.order().value());
      }
      case CrewDetails c -> {
        entity.setCreditType(CreditType.CREW);
        entity.setRole(c.job().value());
        entity.setDepartment(c.department().value());
      }
    }
    return entity;
  }
}
