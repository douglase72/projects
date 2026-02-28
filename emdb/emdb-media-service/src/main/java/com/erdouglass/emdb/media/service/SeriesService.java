package com.erdouglass.emdb.media.service;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.control.ActivateRequestContext;
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
import com.erdouglass.emdb.common.comand.SaveSeries.Credits;
import com.erdouglass.emdb.common.comand.UpdateSeries;
import com.erdouglass.emdb.common.comand.UpdateSeriesCredit;
import com.erdouglass.emdb.common.event.IngestStatusChanged;
import com.erdouglass.emdb.common.event.IngestStatusChanged.IngestSource;
import com.erdouglass.emdb.common.event.IngestStatusChanged.IngestStatus;
import com.erdouglass.emdb.common.query.SeriesDto;
import com.erdouglass.emdb.media.entity.Movie_;
import com.erdouglass.emdb.media.entity.Person;
import com.erdouglass.emdb.media.entity.Role;
import com.erdouglass.emdb.media.entity.Series;
import com.erdouglass.emdb.media.entity.SeriesCredit;
import com.erdouglass.emdb.media.mapper.SeriesCreditMapper;
import com.erdouglass.emdb.media.mapper.SeriesMapper;
import com.erdouglass.emdb.media.repository.RoleRepository;
import com.erdouglass.emdb.media.repository.SeriesCreditRepository;
import com.erdouglass.emdb.media.repository.SeriesRepository;
import com.erdouglass.emdb.scraper.service.TmdbSeriesScraper;
import com.erdouglass.webservices.ResourceNotFoundException;

import io.quarkus.narayana.jta.QuarkusTransaction;
import io.smallrye.reactive.messaging.rabbitmq.OutgoingRabbitMQMetadata;

@ApplicationScoped
public class SeriesService extends MediaService {
  private static final Logger LOGGER = Logger.getLogger(SeriesService.class);
  private static final String ROUTE_KEY = "series.invalid";
  
  @Inject
  SeriesCreditMapper creditMapper;
  
  @Inject
  @Channel("series-dlq-out")
  Emitter<SaveSeries> dlqEmitter;
  
  @Inject
  SeriesMapper mapper;
  
  @Inject
  PersonService personService;  
  
  @Inject
  TmdbSeriesScraper scraper;
  
  @Inject
  SeriesCreditRepository creditRepository;
  
  @Inject
  SeriesRepository repository;
  
  @Inject
  RoleRepository roleRepositoy;
  
  @Override
  @ActivateRequestContext
  public Duration ingest(@NotNull @Positive Integer tmdbId, @NotNull UUID jobId) {
    var start = Instant.now();
    var existingSeries = repository.findByTmdbId(tmdbId);
    var command = existingSeries
        .map(mapper::toSaveSeries)
        .orElseGet(() -> SaveSeries.builder().tmdbId(tmdbId).build());
    var saveCommand = scraper.scrape(command, jobId);
    
    try {
      validate(saveCommand);
      var series = QuarkusTransaction.requiringNew().call(() -> saveSeries(saveCommand));
      existingSeries.ifPresent(s -> deleteShowImages(s, series));
      deletePeopleImages(saveCommand.people(), command.people());
      var et = Duration.between(start, Instant.now());
      var msg = String.format("Ingest Job for TMDB %s %d completed in %d ms", 
          MediaType.SERIES, series.tmdbId(), et.toMillis());
      LOGGER.info(msg);         
      statusService.send(IngestStatusChanged.builder()
          .id(jobId)
          .status(IngestStatus.COMPLETED)
          .tmdbId(tmdbId)
          .source(IngestSource.MEDIA)
          .type(MediaType.SERIES)
          .name(series.title())
          .emdbId(series.id())
          .message(msg)
          .build());
      return et;
    }  catch (Exception e) {
      dlqEmitter.send(Message.of(saveCommand)
          .addMetadata(OutgoingRabbitMQMetadata.builder()
          .withRoutingKey(ROUTE_KEY)
          .build()));
      throw new RuntimeException(e);
    }
  }  
  
  @Transactional
  public SeriesDto save(SaveSeries command) {
    long start = System.nanoTime();
    var series = saveSeries(command);
    var et = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
    LOGGER.infof("Saved %s in %d ms", series, et);
    return mapper.toSeriesDto(series);     
  }
  
  @Transactional
  public SeriesDto findById(Long id, String append) {
    long start = System.nanoTime();
    var series = findSeriesById(id, append);
    var et = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
    LOGGER.infof("Found %s in %d ms", series, et);
    return mapper.toSeriesDto(series); 
  }
  
  @Transactional
  public SeriesDto update(Long id, UpdateSeries command) {
    long start = System.nanoTime();
    var existingSeries = findSeriesById(id, null);
    var newSeries = mapper.toSeries(command);
    newSeries.id(existingSeries.id());
    newSeries.tmdbId(existingSeries.tmdbId());
    var updatedSeries = repository.update(newSeries);
    var et = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
    LOGGER.infof("Updated %s in %d ms", updatedSeries, et);    
    return mapper.toSeriesDto(updatedSeries); 
  }
  
  @Transactional
  public void deleteById(Long id) {
    long start = System.nanoTime();
    var series = findSeriesById(id, "credits");
    series.backdrop().ifPresent(imageService::delete);
    series.poster().ifPresent(imageService::delete);
    roleRepositoy.deleteIn(series.credits().stream()
        .flatMap(c -> c.roles().stream())
        .map(Role::id).toList());
    creditRepository.deleteAll(series.credits());
    repository.deleteById(id);
    var et = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
    LOGGER.infof("Deleted %s in %d ms", series, et);    
  }
  
  @Transactional
  public void updateCredit(UUID id, UpdateSeriesCredit command) {
    var credit = creditRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("No credit found with id: " + id));
    credit.totalEpisodes(command.totalEpisodes());
    credit.order(command.order());
    creditRepository.update(credit);
  }
  
  private Series saveSeries(SaveSeries command) {
    var series = mapper.toSeries(command);
    repository.findByTmdbId(series.tmdbId()).ifPresent(s -> series.id(s.id()));
    var savedSeries = repository.save(series);
    var savedPeople = personService.saveAll(command.people()).stream()
        .collect(Collectors.toMap(s -> s.person().tmdbId(), s -> s.person()));
    saveCredits(savedSeries, savedPeople, command.credits());
    return savedSeries;
  }
  
  private Series findSeriesById(Long id, String append) {
    var series = repository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("No series found with id: " + id));    
    series.credits(List.of());
    if (append != null && append.contains(Movie_.CREDITS)) {
      series.credits(creditRepository.findBySeriesId(id));
    }
    return series;
  }
  
  private void saveCredits(Series series, Map<Integer, Person> people, Credits credits) {
    List<SeriesCredit> creditsToInsert = new ArrayList<>();
    List<SeriesCredit> creditsToUpdate = new ArrayList<>();
    List<Role> rolesToInsert = new ArrayList<>();
    List<UUID> rolesToDelete = new ArrayList<>();
    var existingCredits = creditRepository.findBySeriesId(series.id()).stream()
        .collect(Collectors.toMap(
            c -> c.person().tmdbId() + "-" + c.type(), 
            Function.identity(), 
            (existing, _) -> existing));
    
    List<SeriesCredit> mappedCredits = new ArrayList<>(); 
    for (var cmd : credits.cast()) {
      var person = Optional.ofNullable(people.get(cmd.person().tmdbId()))
          .orElseThrow(() -> new ResourceNotFoundException("Person not found: " + cmd.person().tmdbId())); 
      mappedCredits.add(creditMapper.toSeriesCredit(cmd, series, person));      
    }
    for (var cmd : credits.crew()) {
      var person = Optional.ofNullable(people.get(cmd.person().tmdbId()))
          .orElseThrow(() -> new ResourceNotFoundException("Person not found: " + cmd.person().tmdbId())); 
      mappedCredits.add(creditMapper.toSeriesCredit(cmd, series, person));      
    } 
    
    for (var newCredit : mappedCredits) {
      String key = newCredit.person().tmdbId() + "-" + newCredit.type();
      var existingCredit = existingCredits.remove(key);
      if (existingCredit == null) {
        creditsToInsert.add(newCredit);
        rolesToInsert.addAll(newCredit.roles());
      } else if (!existingCredit.isEqualTo(newCredit)) {
        existingCredit.roles().forEach(r -> rolesToDelete.add(r.id()));
        existingCredit.order(newCredit.order().orElse(null));
        var newRoles = newCredit.roles();
        newRoles.forEach(r -> r.seriesCredit(existingCredit));
        existingCredit.roles(newRoles);
        creditsToUpdate.add(existingCredit);
        rolesToInsert.addAll(newRoles);        
      }
    }
    
    if (!rolesToDelete.isEmpty()) {
      int count = roleRepositoy.deleteIn(rolesToDelete);
      LOGGER.infof("Deleted: %d roles.", count);
    }
    
    if (!existingCredits.isEmpty()) {
      var orphanedRoleIds = existingCredits.values().stream()
          .flatMap(c -> c.roles().stream())
          .map(Role::id)
          .toList();      
      if (!orphanedRoleIds.isEmpty()) {
        roleRepositoy.deleteIn(orphanedRoleIds);
      }
      creditRepository.deleteAll(new ArrayList<>(existingCredits.values()));
      LOGGER.infof("Deleted: %d series credits.", existingCredits.size());
    }
    
    if (!creditsToInsert.isEmpty()) {
      var insertedCredits = creditRepository.insertAll(creditsToInsert);
      LOGGER.infof("Inserted: %d series credits", insertedCredits.size());
    }
    
    if (!creditsToUpdate.isEmpty()) {
      var updatedCredits = creditRepository.updateAll(creditsToUpdate);
      LOGGER.infof("Updated: %d series credits", updatedCredits.size());
    }
    
    if (!rolesToInsert.isEmpty()) {
      var insertedRoles = roleRepositoy.insertAll(rolesToInsert);
      LOGGER.infof("Inserted: %d roles", insertedRoles.size());
    }    
  }

}
