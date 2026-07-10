package com.erdouglass.emdb.media.application.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import org.jboss.logging.Logger;

import com.erdouglass.emdb.media.MediaFacade;
import com.erdouglass.emdb.media.PersonCredit;
import com.erdouglass.emdb.media.SaveMovie;
import com.erdouglass.emdb.media.SavePerson;
import com.erdouglass.emdb.media.SaveResult;
import com.erdouglass.emdb.media.SaveResult.Status;
import com.erdouglass.emdb.media.SaveSeries;
import com.erdouglass.emdb.media.domain.movie.Movie;
import com.erdouglass.emdb.media.domain.movie.MovieCredit;
import com.erdouglass.emdb.media.domain.movie.MovieRepository;
import com.erdouglass.emdb.media.domain.person.Person;
import com.erdouglass.emdb.media.domain.person.PersonRepository;
import com.erdouglass.emdb.media.domain.series.Series;
import com.erdouglass.emdb.media.domain.series.SeriesRepository;
import com.erdouglass.emdb.media.domain.shared.Credit.CreditType;

@ApplicationScoped
class MediaHandler implements MediaFacade {
  private static final Logger LOGGER = Logger.getLogger(MediaHandler.class);
  
  @Inject
  ImageService imageService;
  
  @Inject
  MovieMapper movieMapper;
  
  @Inject
  PersonMapper personMapper;
  
  @Inject
  SeriesMapper seriesMapper;
  
  @Inject
  MovieRepository movieRepository;
  
  @Inject
  PersonRepository personRepository;
  
  @Inject
  SeriesRepository seriesRepository;
  
  @Inject
  PersonResolver personResolver;

  @Override
  @Transactional
  public SaveResult saveMovie(SaveMovie command) {
    SaveResult result;
    Movie movie;
    var existing = movieRepository.findByExternalId(command.externalId()).orElse(null);
    if (existing == null) {
      imageService.save(command.backdrop());
      imageService.save(command.poster());
      movie = movieRepository.insert(movieMapper.toMovie(command));
      result = new SaveResult(movie.getId(), Status.CREATED);
    } else {
      var backdrop = imageService.update(existing.getBackdrop(), command.backdrop());
      var poster = imageService.update(existing.getPoster(), command.poster());
      var cmd = SaveMovie.builder(command)
          .backdrop(backdrop.image())
          .poster(poster.image())
          .build();
      movieMapper.merge(cmd, existing);
      movie = movieRepository.update(existing);      
      backdrop.toDelete().ifPresent(imageService::delete);
      poster.toDelete().ifPresent(imageService::delete);
      result = new SaveResult(movie.getId(), Status.UPDATED);
    }
    saveMovieCredits(movie, command.credits());
    LOGGER.infof("Saved: %s", movie);
    return result;
  }

  @Override
  @Transactional
  public SaveResult savePerson(SavePerson command) {
    SaveResult result;
    Person person;
    var existing = personRepository.findByExternalId(command.externalId()).orElse(null); 
    if (existing == null) {
      imageService.save(command.profile());
      person = personRepository.insert(personMapper.toPerson(command));
      result = new SaveResult(person.getId(), Status.CREATED);
    } else {
      var profile = imageService.update(existing.getProfile(), command.profile());
      var cmd = SavePerson.builder(command)
          .profile(profile.image())
          .build();
      personMapper.merge(cmd, existing);
      person = personRepository.update(existing);
      profile.toDelete().ifPresent(imageService::delete);
      result = new SaveResult(person.getId(), Status.UPDATED);
    }
    LOGGER.infof("Saved: %s", person);
    return result;
  }

  @Override
  @Transactional
  public SaveResult saveSeries(SaveSeries command) {
    SaveResult result;
    Series series;
    var existing = seriesRepository.findByExternalId(command.externalId()).orElse(null); 
    if (existing == null) {
      imageService.save(command.backdrop());
      imageService.save(command.poster());
      series = seriesRepository.insert(seriesMapper.toSeries(command));
      result = new SaveResult(series.getId(), Status.CREATED);
    } else {
      var backdrop = imageService.update(existing.getBackdrop(), command.backdrop());
      var poster = imageService.update(existing.getPoster(), command.poster());
      var cmd = SaveSeries.builder(command)
          .backdrop(backdrop.image())
          .poster(poster.image())
          .build();
      seriesMapper.merge(cmd, existing);
      series = seriesRepository.update(existing);      
      backdrop.toDelete().ifPresent(imageService::delete);
      poster.toDelete().ifPresent(imageService::delete); 
      result = new SaveResult(series.getId(), Status.UPDATED);
    }
    LOGGER.infof("Saved: %s", series);
    return result;
  }
  
  private void saveMovieCredits(Movie movie, SaveMovie.Credits credits) {
    movieRepository.deleteCreditsByMovieId(movie.getId());
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
      movieRepository.insertCredits(creditsToInsert);
    }    
  }
}
