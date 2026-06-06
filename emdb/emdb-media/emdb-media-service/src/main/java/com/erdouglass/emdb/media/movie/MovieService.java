package com.erdouglass.emdb.media.movie;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import com.erdouglass.emdb.media.PersonMovieCredit;
import com.erdouglass.emdb.media.command.SaveMovie;
import com.erdouglass.emdb.media.command.SaveMovie.CastCredit;
import com.erdouglass.emdb.media.command.SaveMovie.Credits;
import com.erdouglass.emdb.media.command.SaveMovie.CrewCredit;
import com.erdouglass.emdb.media.credit.CreditType;
import com.erdouglass.emdb.media.image.ImageService;
import com.erdouglass.emdb.media.internal.PersonResolver;
import com.erdouglass.emdb.media.logging.Log;

/// Application service that orchestrates persistence of [Movie] aggregates,
/// including their poster/backdrop images and cast & crew credits. Reconciles
/// each [SaveMovie] command against existing records, inserting a new movie or
/// merging an update, and cleaning up any images it replaces.
@ApplicationScoped
class MovieService {
  
  @Inject
  MovieCreditRepository creditRepository;
  
  @Inject
  ImageService imageService;
  
  @Inject
  MovieMapper mapper;
  
  @Inject
  MovieRepository movieRepository;
  
  @Inject
  PersonResolver resolver;

  /// Persists a movie from the command, creating it when no movie with the same
  /// TMDB id exists and updating the existing one otherwise. Poster and backdrop
  /// images are saved or replaced as needed, and the full cast & crew is rebuilt
  /// from the command.
  ///
  /// @param command the movie data to persist
  /// @return the saved movie, with generated identifiers and credits populated
  @Log
  @Transactional
  public Movie save(final SaveMovie command) {
    Movie movie;
    var existing = movieRepository.findByTmdbId(command.tmdbId()).orElse(null);
    if (existing == null) {
      var backdrop = imageService.save(command.backdrop());
      var poster = imageService.save(command.poster());
      movie = movieRepository.insert(mapper.toMovie(command, backdrop, poster));
    } else {
      var backdrop = imageService
          .update(existing.getTmdbBackdrop(), existing.getBackdrop(), command.backdrop());
      var poster = imageService
          .update(existing.getTmdbPoster(), existing.getPoster(), command.poster());
      var cmd = SaveMovie.builder(command)
          .backdrop(backdrop.image())
          .poster(poster.image())
          .build();
      mapper.merge(cmd, existing);
      movie = movieRepository.update(existing);
      backdrop.toDelete().ifPresent(imageService::delete);
      poster.toDelete().ifPresent(imageService::delete);
    }
    saveCredits(command.credits(), movie);
    return movie;
  }
  
  /// Replaces all credits for the movie: existing credits are deleted, the
  /// referenced people are resolved or created, and a fresh [MovieCredit] is
  /// inserted for each cast and crew entry.
  private void saveCredits(Credits credits, Movie movie) {
    creditRepository.deleteByMovie(movie);
    var allCredits = Stream.concat(
        credits.cast().stream().map(c -> (PersonMovieCredit) c), 
        credits.crew().stream().map(c -> (PersonMovieCredit) c))
        .toList();
    var people = resolver.findOrCreate(new ArrayList<>(allCredits));
    List<MovieCredit> creditsToInsert = new ArrayList<>();
    for (var credit : allCredits) {
      var movieCredit = new MovieCredit(credit.creditId());
      movieCredit.setPerson(people.get(credit.tmdbId()));
      movieCredit.setMovie(movie);
      switch (credit) {
        case CastCredit cast -> {
          movieCredit.setType(CreditType.CAST);
          movieCredit.setRole(cast.character());
          movieCredit.setOrder(cast.order());
        }
        case CrewCredit crew -> {
          movieCredit.setType(CreditType.CREW);
          movieCredit.setRole(crew.job());
        }
        default -> throw new IllegalArgumentException("Invalid credit: " + credit);
      }
      creditsToInsert.add(movieCredit);      
    }
    
    if (!creditsToInsert.isEmpty()) {
      movie.setCredits(creditRepository.insertAll(creditsToInsert));
    }    
  }
}
