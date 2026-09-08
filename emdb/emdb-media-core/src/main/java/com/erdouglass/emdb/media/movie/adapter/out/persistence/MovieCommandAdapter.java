package com.erdouglass.emdb.media.movie.adapter.out.persistence;

import java.util.Optional;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import com.erdouglass.emdb.media.api.TmdbId;
import com.erdouglass.emdb.media.kernel.LanguageCode;
import com.erdouglass.emdb.media.kernel.Overview;
import com.erdouglass.emdb.media.kernel.PublicId;
import com.erdouglass.emdb.media.kernel.Score;
import com.erdouglass.emdb.media.kernel.Title;
import com.erdouglass.emdb.media.kernel.Version;
import com.erdouglass.emdb.media.movie.application.port.out.MovieCommandRepository;
import com.erdouglass.emdb.media.movie.domain.model.Movie;
import com.erdouglass.emdb.media.movie.domain.model.MovieDetails;
import com.erdouglass.emdb.media.movie.domain.model.ReleaseDate;

@ApplicationScoped
class MovieCommandAdapterimplements implements MovieCommandRepository {
  
  @Inject
  JakartaDataMovieCommandRepository repository;

  @Override
  public Movie insert(Movie movie) {
    return toMovie(repository.insert(toMovieEntity(movie)));
  }

  @Override
  public Movie update(Movie movie) {
    return toMovie(repository.update(toMovieEntity(movie)));
  }
  
  @Override
  public Optional<Movie> findById(PublicId id) {
    return repository.findById(id.value()).map(this::toMovie);
  }

  @Override
  public Optional<Movie> findByTmdbId(TmdbId tmdbId) {
    return repository.findByTmdbId(tmdbId.value()).map(this::toMovie);
  }
  
  private MovieEntity toMovieEntity(Movie movie) {
    var entity = new MovieEntity();
    entity.setId(movie.id().value());
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
    var id = PublicId.of(entity.getId());
    var tmdbId = TmdbId.of(entity.getTmdbId());
    var version = Version.of(entity.getVersion());
    var details = MovieDetails.builder()
        .title(Title.of(entity.getTitle()))
        .releaseDate(entity.getReleaseDate().map(ReleaseDate::from).orElse(null))
        .score(entity.getScore().map(Score::of).orElse(null))
        .originalLanguage(entity.getOriginalLanguage().map(LanguageCode::of).orElse(null))
        .overview(entity.getOverview().map(Overview::of).orElse(null))           
        .build();
    return Movie.rehydrate(id, tmdbId, version, details);
  }
}
