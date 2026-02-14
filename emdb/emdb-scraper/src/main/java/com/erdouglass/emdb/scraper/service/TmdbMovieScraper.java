package com.erdouglass.emdb.scraper.service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import com.erdouglass.emdb.common.CreditType;
import com.erdouglass.emdb.common.MediaType;
import com.erdouglass.emdb.common.comand.SaveMovie;
import com.erdouglass.emdb.common.comand.SaveMovieCredit;
import com.erdouglass.emdb.common.comand.SavePerson;
import com.erdouglass.emdb.common.event.IngestStatusChanged;
import com.erdouglass.emdb.common.event.IngestStatusChanged.IngestSource;
import com.erdouglass.emdb.common.event.IngestStatusChanged.IngestStatus;
import com.erdouglass.emdb.common.service.IngestStatusService;
import com.erdouglass.emdb.scraper.client.TmdbMovieClient;
import com.erdouglass.emdb.scraper.query.TmdbMovieDto;
import com.erdouglass.emdb.scraper.query.TmdbMovieDto.CastCredit;
import com.erdouglass.emdb.scraper.query.TmdbMovieDto.CrewCredit;

import io.micrometer.core.annotation.Timed;

@ApplicationScoped
public class TmdbMovieScraper extends TmdbScraper {
  private static final Logger LOGGER = Logger.getLogger(TmdbMovieScraper.class);
  private static final String CREDITS = "credits";
  
  @Inject
  @RestClient
  TmdbMovieClient client;
  
  @Inject
  IngestStatusService statusService;  
  
  @Timed(
      value = "emdb.scrape.duration", 
      extraTags = {"media", "movie"}
  )
  public SaveMovie scrape(@NotNull @Valid SaveMovie command, @NotBlank String jobId) {
    var start = System.nanoTime();
    var tmdbMovie = client.findById(command.tmdbId(), CREDITS);
    var ids = Stream.concat(
        tmdbMovie.credits().cast().stream().limit(castLimit).map(CastCredit::id), 
        tmdbMovie.credits().crew().stream().limit(crewLimit).map(CrewCredit::id))
        .distinct()
        .toList();
    var existingPeople = command.credits().stream()
        .map(SaveMovieCredit::person)
        .collect(Collectors.toMap(SavePerson::tmdbId, Function.identity()));
    var people = findPeople(ids, existingPeople);
    var credits = createCredits(tmdbMovie, people); 
    
    // Create the command to save the movie.
    var cmd = SaveMovie.builder()
        .tmdbId(tmdbMovie.id())
        .title(tmdbMovie.title())
        .releaseDate(tmdbMovie.release_date())
        .score(tmdbMovie.vote_average())
        .status(tmdbMovie.status())
        .runtime(tmdbMovie.runtime())
        .budget(tmdbMovie.budget())
        .revenue(tmdbMovie.revenue())
        .homepage(tmdbMovie.homepage())
        .originalLanguage(tmdbMovie.original_language())
        .backdrop(command.backdrop())
        .tmdbBackdrop(command.tmdbBackdrop())
        .poster(command.poster())
        .tmdbPoster(command.tmdbPoster())
        .tagline(tmdbMovie.tagline())
        .overview(tmdbMovie.overview())
        .credits(credits);
    if (!Objects.equals(tmdbMovie.backdrop_path(), command.tmdbBackdrop())) {
      cmd.backdrop(imageService.save(tmdbMovie.backdrop_path()))
        .tmdbBackdrop(tmdbMovie.backdrop_path());
    }
    if (!Objects.equals(tmdbMovie.poster_path(), command.tmdbPoster())) {
      cmd.poster(imageService.save(tmdbMovie.poster_path()))
        .tmdbPoster(tmdbMovie.poster_path());
    }
    var et = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
    LOGGER.infof("Ingest Job %s for TMDB movie %d extracted in %d ms", jobId, command.tmdbId(), et); 
    statusService.send(IngestStatusChanged.builder()
        .id(jobId)
        .status(IngestStatus.EXTRACTED)
        .tmdbId(command.tmdbId())
        .source(IngestSource.MEDIA)
        .type(MediaType.MOVIE)
        .name(tmdbMovie.title())
        .build());
    return cmd.build();
  }
  
  private List<SaveMovieCredit> createCredits(TmdbMovieDto movie, Map<Integer, SavePerson> people) {
    var cast = movie.credits().cast().stream()
        .limit(castLimit)
        .map(c -> SaveMovieCredit.builder()
            .type(CreditType.CAST)
            .role(c.character())
            .person(people.get(c.id()))
            .order(c.order())
            .build());
    var crew = movie.credits().crew().stream()
        .limit(crewLimit)
        .map(c -> SaveMovieCredit.builder()
            .type(CreditType.CREW)
            .role(c.job())
            .person(people.get(c.id()))
            .build());
    var credits = Stream.concat(cast, crew).toList();
    LOGGER.info(String.format("Found %d credits in TMDB movie %d", credits.size(), movie.id()));
    return credits;
  }

}
