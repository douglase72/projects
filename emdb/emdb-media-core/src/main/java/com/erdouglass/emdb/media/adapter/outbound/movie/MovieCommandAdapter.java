package com.erdouglass.emdb.media.adapter.outbound.movie;

import java.util.Optional;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import com.erdouglass.emdb.media.TmdbId;
import com.erdouglass.emdb.media.application.port.outbound.movie.MovieCommandRepository;
import com.erdouglass.emdb.media.domain.movie.Movie;
import com.erdouglass.emdb.media.domain.movie.MovieDetails;
import com.erdouglass.emdb.media.domain.movie.MovieId;
import com.erdouglass.emdb.media.domain.movie.MoviePublicId;
import com.erdouglass.emdb.media.domain.movie.ReleaseDate;
import com.erdouglass.emdb.media.domain.movie.Title;
import com.erdouglass.emdb.media.domain.shared.LanguageCode;
import com.erdouglass.emdb.media.domain.shared.Overview;
import com.erdouglass.emdb.media.domain.shared.Score;
import com.erdouglass.emdb.media.domain.shared.Version;

@ApplicationScoped
class MovieCommandAdapter implements MovieCommandRepository {
  
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
  public void deleteByPublicId(MoviePublicId publicId) {
    repository.deleteById(publicId.toLong());
  }
  
  @Override
  public Optional<Movie> findByPublicId(MoviePublicId publicId) {
    return repository.findById(publicId.toLong()).map(this::toMovie);
  }

  @Override
  public Optional<Movie> findByTmdbId(TmdbId tmdbId) {
    return repository.findByTmdbId(tmdbId.value()).map(this::toMovie);
  }
  
  private MovieEntity toMovieEntity(Movie movie) {
    var entity = new MovieEntity();
    entity.setId(movie.publicId().map(MoviePublicId::toLong).orElse(null));
    entity.setSurrogateId(movie.id().value());
    entity.setTmdbId(movie.tmdbId().value());
    entity.setVersion(movie.version().map(Version::value).orElse(0L));
    entity.setLocked(movie.isLocked());
    entity.setTitle(movie.details().title().value());
    entity.setReleaseDate(movie.details().releaseDate().map(ReleaseDate::toLocalDate).orElse(null));
    entity.setScore(movie.details().score().map(Score::value).orElse(null));
    entity.setOriginalLanguage(movie.details().originalLanguage().map(LanguageCode::value).orElse(null));
    entity.setOverview(movie.details().overview().map(Overview::value).orElse(null));
    return entity;
  }
  
  private Movie toMovie(MovieEntity entity) {
    var id = MovieId.of(entity.getSurrogateId());
    var publicId = MoviePublicId.from(entity.getId());
    var tmdbId = TmdbId.of(entity.getTmdbId());
    var details = MovieDetails.builder()
        .title(Title.of(entity.getTitle()))
        .releaseDate(entity.getReleaseDate().map(ReleaseDate::from).orElse(null))
        .score(entity.getScore().map(Score::of).orElse(null))
        .originalLanguage(entity.getOriginalLanguage().map(LanguageCode::of).orElse(null))
        .overview(entity.getOverview().map(Overview::of).orElse(null))
        .build();
    return Movie.rehydrate(id, publicId, tmdbId, entity.getLocked(), details, Version.of(entity.getVersion()));
  }
}
