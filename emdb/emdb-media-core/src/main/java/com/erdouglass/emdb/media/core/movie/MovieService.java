package com.erdouglass.emdb.media.core.movie;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import com.erdouglass.common.graphql.ResourceNotFoundException;
import com.erdouglass.emdb.media.core.ImageService;
import com.erdouglass.emdb.media.core.credit.CreditType;
import com.erdouglass.emdb.media.core.logging.Log;
import com.erdouglass.emdb.media.core.person.PersonResolver;
import com.erdouglass.emdb.media.movie.MovieCommandService;
import com.erdouglass.emdb.media.movie.MovieDto;
import com.erdouglass.emdb.media.movie.MovieDto.MovieCredits;
import com.erdouglass.emdb.media.movie.MovieQueryService;
import com.erdouglass.emdb.media.movie.SaveMovie;
import com.erdouglass.emdb.media.movie.SaveMovie.Credits;
import com.erdouglass.emdb.media.movie.UpdateMovie;
import com.erdouglass.emdb.media.person.PersonCredit;

@ApplicationScoped
class MovieService implements MovieCommandService, MovieQueryService {
  
  @Inject
  MovieCreditRepository creditRepository;
  
  @Inject
  ImageService imageService;
  
  @Inject
  MovieMapper mapper;
  
  @Inject
  PersonResolver personResolver;
  
  @Inject
  MovieRepository repository;
  
  /// Upsert the [Movie] in the given command.
  /// 
  /// This method is idempotent, creating a movie if one does not already exist 
  /// by the given TMDB ID or updating the existing one.
  @Override
  @Log("Saved:")
  @Transactional
  public MovieDto save(SaveMovie command) {
    Movie movie;
    var existing = repository.findByTmdbId(command.tmdbId()).orElse(null);
    if (existing == null) {
      imageService.save(command.backdrop());
      imageService.save(command.poster());
      movie = repository.insert(mapper.toMovie(command));
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
    }
    saveCredits(command.credits(), movie);
    return mapper.toMovieDto(movie);
  }
  
  @Override
  @Log("Found:")
  @Transactional
  public MovieDto findById(Long id) {
    return repository.findById(id)
        .map(mapper::toMovieView)
        .orElseThrow(() -> new ResourceNotFoundException("No movie found with id: " + id));     
  }
  
  @Override
  @Transactional
  public MovieCredits findCreditsByMovieId(Long id) {
    return mapper.toCredits(creditRepository.findByMovieId(id));
  }

  @Override
  @Transactional
  public MovieDto update(UpdateMovie command) {
    throw new UnsupportedOperationException();
  }

  @Override
  @Transactional
  public void delete(Long id) {
    throw new UnsupportedOperationException();
  }
  
  private void saveCredits(Credits credits, Movie movie) {
    creditRepository.deleteByMovie(movie);
    var allCredits = Stream.concat(
        credits.cast().stream().map(c -> (PersonCredit) c), 
        credits.crew().stream().map(c -> (PersonCredit) c))
        .toList();
    var people = personResolver.findOrCreate(allCredits);
    List<MovieCredit> creditsToInsert = new ArrayList<>();
    for (var credit : credits.cast()) {
      var movieCredit = new MovieCredit(credit.creditId());
      movieCredit.setType(CreditType.CAST);
      movieCredit.setMovie(movie);
      movieCredit.setPerson(people.get(credit.tmdbId()));
      movieCredit.setRole(credit.character());
      movieCredit.setOrder(credit.order());
      creditsToInsert.add(movieCredit); 
    }
    
    for (var credit : credits.crew()) {
      var movieCredit = new MovieCredit(credit.creditId());
      movieCredit.setType(CreditType.CREW);
      movieCredit.setMovie(movie);
      movieCredit.setPerson(people.get(credit.tmdbId()));
      movieCredit.setRole(credit.job());
      creditsToInsert.add(movieCredit); 
    }
    
    if (!creditsToInsert.isEmpty()) {
      movie.setCredits(creditRepository.insertAll(creditsToInsert));
    }
  }
}
