package com.erdouglass.emdb.media.service;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.jboss.logging.Logger;

import com.erdouglass.emdb.common.MediaType;
import com.erdouglass.emdb.common.comand.SaveSeries;
import com.erdouglass.emdb.common.comand.UpdateSeries;
import com.erdouglass.emdb.common.event.IngestStatusChanged;
import com.erdouglass.emdb.common.event.IngestStatusChanged.IngestSource;
import com.erdouglass.emdb.common.event.IngestStatusChanged.IngestStatus;
import com.erdouglass.emdb.media.entity.Series;
import com.erdouglass.emdb.media.mapper.SeriesMapper;
import com.erdouglass.emdb.media.repository.SeriesRepository;
import com.erdouglass.emdb.scraper.service.TmdbSeriesScraper;
import com.erdouglass.webservices.ResourceNotFoundException;

import io.smallrye.reactive.messaging.rabbitmq.OutgoingRabbitMQMetadata;

@ApplicationScoped
public class SeriesService extends MediaService {
  private static final Logger LOGGER = Logger.getLogger(SeriesService.class);
  private static final String ROUTE_KEY = "series.invalid";
  
  @Inject
  @Channel("series-dlq-out")
  Emitter<SaveSeries> dlqEmitter;
  
  @Inject
  SeriesMapper mapper;
  
  @Inject
  TmdbSeriesScraper scraper;
  
  @Inject
  SeriesRepository repository;
  
  @Override
  public void ingest(@NotNull @Positive Integer tmdbId, @NotNull UUID jobId) {
    var existingSeries = findByTmdbId(tmdbId);
    var command = existingSeries
        .map(mapper::toSaveSeries)
        .orElseGet(() -> SaveSeries.builder().tmdbId(tmdbId).build());
    var saveCommand = scraper.scrape(command, jobId);
    
    try {
      validate(saveCommand);
      var series = save(saveCommand);
      LOGGER.infof("Saved: %s", series);
      existingSeries.ifPresent(s -> {
        if (!Objects.equals(s.tmdbBackdrop().orElse(null), series.tmdbBackdrop().orElse(null))) {
          s.backdrop().ifPresent(imageService::delete);
        }
        if (!Objects.equals(s.tmdbPoster().orElse(null), series.tmdbPoster().orElse(null))) {
          s.poster().ifPresent(imageService::delete);
        }      
      });
      statusService.send(IngestStatusChanged.builder()
          .id(jobId)
          .status(IngestStatus.COMPLETED)
          .tmdbId(tmdbId)
          .source(IngestSource.MEDIA)
          .type(MediaType.SERIES)
          .name(series.title())
          .emdbId(series.id())
          .build());
    }  catch (Exception e) {
      dlqEmitter.send(Message.of(saveCommand)
          .addMetadata(OutgoingRabbitMQMetadata.builder()
          .withRoutingKey(ROUTE_KEY)
          .build()));
      throw new RuntimeException(e);
    }
  }  
  
  @Transactional
  public Series save(SaveSeries command) {
    var series = mapper.toSeries(command);
    repository.findByTmdbId(series.tmdbId()).ifPresent(s -> series.id(s.id()));
    var savedSeries = repository.save(series);
    return savedSeries;     
  }
  
  @Transactional
  public Series findById(@NotNull @Positive Long id, String append) {
    return repository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("No series found with id: " + id));
  }
  
  @Transactional
  Optional<Series> findByTmdbId(@NotNull @Positive Integer id) {
    return repository.findByTmdbId(id);
  }
  
  @Transactional
  public Series update(Long id, UpdateSeries command) {
    long start = System.nanoTime();
    var existingSeries = findById(id, null);
    var newSeries = mapper.toSeries(command);
    newSeries.id(existingSeries.id());
    newSeries.tmdbId(existingSeries.tmdbId());
    var updatedSeries = repository.update(newSeries);
    var et = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
    LOGGER.infof("Updated %s in %d ms", updatedSeries, et);    
    return updatedSeries;
  }
  
  @Transactional
  public void deleteById(Long id) {
    long start = System.nanoTime();
    var series = findById(id, "credits");
    series.backdrop().ifPresent(imageService::delete);
    series.poster().ifPresent(imageService::delete);
    repository.deleteById(id);
    var et = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
    LOGGER.infof("Deleted %s in %d ms", series, et);    
  }

}
