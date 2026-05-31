package com.erdouglass.emdb.media.domain.movie;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import org.jboss.logging.Logger;

import com.erdouglass.emdb.media.credit.CreditType;
import com.erdouglass.emdb.media.domain.MovieService;
import com.erdouglass.emdb.media.domain.internal.ImageService;
import com.erdouglass.emdb.media.domain.internal.PersonResolver;
import com.erdouglass.emdb.media.domain.person.Person;
import com.erdouglass.emdb.media.movie.MovieResponse;
import com.erdouglass.emdb.media.movie.SaveMovie;
import com.erdouglass.emdb.media.movie.SaveMovie.CastCredit;
import com.erdouglass.emdb.media.movie.SaveMovie.Credits;
import com.erdouglass.emdb.media.movie.SaveMovie.CrewCredit;
import com.erdouglass.emdb.media.person.PersonCredit;

@ApplicationScoped
class MovieServiceImpl implements MovieService {
  private static final Logger LOGGER = Logger.getLogger(MovieServiceImpl.class);
  
  @Inject
  MovieCreditRepository creditRepository;
  
  @Inject
  ImageService imageService;
  
  @Inject
  MovieMapper mapper;
  
  @Inject
  PersonResolver resolver;
  
  @Inject
  MovieRepository movieRepository;

  @Override
  @Transactional
  public MovieResponse save(final SaveMovie command) {
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
    LOGGER.infof("Saved: %s", movie);
    return mapper.toMovieResponse(movie);
  }
  
  private void saveCredits(Credits credits, Movie movie) {
    creditRepository.deleteByMovie(movie);
    var allCredits = Stream.concat(
        credits.cast().stream().map(c -> (PersonCredit) c), 
        credits.crew().stream().map(c -> (PersonCredit) c))
        .toList();
    var people = resolver.findOrCreate(allCredits);
    List<MovieCredit> creditsToInsert = new ArrayList<>();
    for (var credit : allCredits) {
      creditsToInsert.add(toMovieCredit(credit, movie, people.get(credit.tmdbId())));
    }
    
    if (!creditsToInsert.isEmpty()) {
      creditRepository.insertAll(creditsToInsert);
    }
  }
  
  private MovieCredit toMovieCredit(PersonCredit credit, Movie movie, Person person) {
    var movieCredit = new MovieCredit();
    movieCredit.setPerson(person);
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
      default -> throw new IllegalArgumentException("Invalid type: " + credit);
    }    
    return movieCredit;
  }
}
