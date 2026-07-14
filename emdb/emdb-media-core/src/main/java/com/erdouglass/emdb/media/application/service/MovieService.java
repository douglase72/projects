package com.erdouglass.emdb.media.application.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import org.jboss.logging.Logger;

import com.erdouglass.common.graphql.ResourceNotFoundException;
import com.erdouglass.emdb.media.PersonCredit;
import com.erdouglass.emdb.media.SaveMovie;
import com.erdouglass.emdb.media.SaveMovieUseCase;
import com.erdouglass.emdb.media.SaveResult;
import com.erdouglass.emdb.media.SaveResult.Status;
import com.erdouglass.emdb.media.application.port.inbound.movie.DeleteMovieUseCase;
import com.erdouglass.emdb.media.application.port.inbound.movie.MovieView;
import com.erdouglass.emdb.media.application.port.inbound.movie.MovieView.MovieCredits;
import com.erdouglass.emdb.media.application.port.inbound.movie.QueryMovieUseCase;
import com.erdouglass.emdb.media.application.port.inbound.movie.UpdateMovie;
import com.erdouglass.emdb.media.application.port.inbound.movie.UpdateMovieUseCase;
import com.erdouglass.emdb.media.domain.movie.Movie;
import com.erdouglass.emdb.media.domain.movie.MovieCredit;
import com.erdouglass.emdb.media.domain.movie.MovieRepository;
import com.erdouglass.emdb.media.domain.shared.Credit.CreditType;

@ApplicationScoped
class MovieService implements SaveMovieUseCase, UpdateMovieUseCase, DeleteMovieUseCase, 
    QueryMovieUseCase {
  private static final Logger LOGGER = Logger.getLogger(MovieService.class);
  
  @Inject
  ImageService imageService;
  
  @Inject
  MovieMapper mapper;
  
  @Inject
  PersonResolver personResolver;
  
  @Inject
  MovieRepository repository;
  
  @Override
  @Transactional
  public SaveResult save(SaveMovie command) {
    SaveResult result;
    Movie movie;
    var existing = repository.findByExternalId(command.externalId()).orElse(null);
    if (existing == null) {
      imageService.save(command.backdrop());
      imageService.save(command.poster());
      movie = repository.insert(mapper.toMovie(command));
      result = new SaveResult(movie.getId(), Status.CREATED);
    } else {
      var backdrop = imageService.update(existing.getBackdrop(), command.backdrop());
      var poster = imageService.update(existing.getPoster(), command.poster());
      var cmd = SaveMovie.builder(command)
          .backdrop(backdrop.image())
          .poster(poster.image())
          .build();
      mapper.merge(cmd, existing);
      movie = repository.update(existing);      
      backdrop.toDelete().ifPresent(imageService::delete);
      poster.toDelete().ifPresent(imageService::delete);
      result = new SaveResult(movie.getId(), Status.UPDATED);
    }
    saveCredits(movie, command.credits());
    LOGGER.infof("Saved: %s", movie);
    return result;
  }

  @Override
  public MovieView findById(Long id) {
    return repository.findById(id)
        .map(mapper::toMovieView)
        .orElseThrow(() -> new ResourceNotFoundException("No movie found with id: " + id));        
  }

  @Override
  public MovieView update(Long id, UpdateMovie command) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void deleteById(Long id) {
    throw new UnsupportedOperationException();
  }

  @Override
  public MovieCredits findCreditsByMovieId(Long id) {
    return mapper.toCredits(repository.findCreditsByMovieId(id));
  }
  
  private void saveCredits(Movie movie, SaveMovie.Credits credits) {
    repository.deleteCreditsByMovieId(movie.getId());
    var allCredits = Stream.concat(
        credits.cast().stream().map(c -> (PersonCredit) c), 
        credits.crew().stream().map(c -> (PersonCredit) c))
        .toList();
    var people = personResolver.findOrCreate(allCredits);
    
    List<MovieCredit> creditsToInsert = new ArrayList<>();
    for (var credit : credits.cast()) {
      var movieCredit = new MovieCredit(credit.creditId());
      movieCredit.setType(CreditType.CAST);
      movieCredit.setMovieId(movie.getId());
      movieCredit.setPersonId(people.get(credit.externalId()));
      movieCredit.setRole(credit.character());
      movieCredit.setOrder(credit.order());
      creditsToInsert.add(movieCredit); 
    }
    
    for (var credit : credits.crew()) {
      var movieCredit = new MovieCredit(credit.creditId());
      movieCredit.setType(CreditType.CREW);
      movieCredit.setMovieId(movie.getId());
      movieCredit.setPersonId(people.get(credit.externalId()));
      movieCredit.setRole(credit.job());
      creditsToInsert.add(movieCredit);      
    }
    
    if (!creditsToInsert.isEmpty()) {
      repository.insertCredits(creditsToInsert);
    }    
  }
}
