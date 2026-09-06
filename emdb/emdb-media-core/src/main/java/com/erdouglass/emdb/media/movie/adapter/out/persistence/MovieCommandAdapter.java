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
import com.erdouglass.emdb.media.movie.domain.model.MovieId;
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
  public Optional<Movie> findByPublicId(PublicId publicId) {
    return repository.findByPublicId(publicId.value()).map(this::toMovie);
  }

  @Override
  public Optional<Movie> findByTmdbId(TmdbId tmdbId) {
    return repository.findByTmdbId(tmdbId.value()).map(this::toMovie);
  }
  
  private MovieEntity toMovieEntity(Movie movie) {
    var entity = new MovieEntity();
    entity.setId(movie.publicId().map(PublicId::value).orElse(null));
    entity.setAggregateId(movie.id().value());
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
    var command = RehydrateMovie.builder()
        .id(MovieId.of(entity.getAggregateId()))
        .publicId(PublicId.of(entity.getId()))
        .tmdbId(TmdbId.of(entity.getTmdbId()))
        .version(Version.of(entity.getVersion()))
        .title(Title.of(entity.getTitle()))
        .releaseDate(entity.getReleaseDate().map(ReleaseDate::from).orElse(null))
        .score(entity.getScore().map(Score::of).orElse(null))
        .originalLanguage(entity.getOriginalLanguage().map(LanguageCode::of).orElse(null))
        .overview(entity.getOverview().map(Overview::of).orElse(null))        
        .build();
    return Movie.rehydrate(command);
  }
}
