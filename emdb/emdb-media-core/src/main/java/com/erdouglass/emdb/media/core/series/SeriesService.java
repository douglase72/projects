package com.erdouglass.emdb.media.core.series;

import java.util.ArrayList;
import java.util.stream.Stream;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import com.erdouglass.common.graphql.ResourceNotFoundException;
import com.erdouglass.emdb.media.core.ImageService;
import com.erdouglass.emdb.media.core.credit.CreditType;
import com.erdouglass.emdb.media.core.logging.Log;
import com.erdouglass.emdb.media.core.person.PersonResolver;
import com.erdouglass.emdb.media.person.PersonCredit;
import com.erdouglass.emdb.media.series.SaveSeries;
import com.erdouglass.emdb.media.series.SaveSeries.Credits;
import com.erdouglass.emdb.media.series.SeriesCommandService;
import com.erdouglass.emdb.media.series.SeriesDto;
import com.erdouglass.emdb.media.series.SeriesDto.SeriesCredits;
import com.erdouglass.emdb.media.series.SeriesQueryService;
import com.erdouglass.emdb.media.series.UpdateSeries;

@ApplicationScoped
class SeriesService implements SeriesCommandService, SeriesQueryService {
  
  @Inject
  ImageService imageService;
  
  @Inject
  SeriesMapper mapper;
  
  @Inject
  PersonResolver resolver;
  
  @Inject
  SeriesRepository repository;
  
  @Inject
  SeriesCreditRepository creditRepository;
  
  @Inject
  RoleRepository roleRepository;

  @Override
  @Log("Saved:")
  @Transactional
  public SeriesDto save(SaveSeries command) {
    Series series;
    var existing = repository.findByTmdbId(command.tmdbId()).orElse(null);
    if (existing == null) {
      imageService.save(command.backdrop());
      imageService.save(command.poster());
      series = repository.insert(mapper.toSeries(command));
    } else {
      var backdrop = imageService.update(existing.getBackdrop(), command.backdrop());
      var poster = imageService.update(existing.getPoster(), command.poster());
      var cmd = SaveSeries.builder(command)
          .backdrop(backdrop.image())
          .poster(poster.image())
          .build();
      mapper.merge(cmd, existing);
      series = repository.update(existing);      
      backdrop.toDelete().ifPresent(imageService::delete);
      poster.toDelete().ifPresent(imageService::delete);      
    }
    saveCredits(command.credits(), series);
    return mapper.toSeriesDto(series);
  }
  
  @Override
  @Log("Found:")
  @Transactional
  public SeriesDto findById(Long id) {
    return repository.findById(id)
        .map(mapper::toSeriesView)
        .orElseThrow(() -> new ResourceNotFoundException("No series found with id: " + id)); 
  }
  
  @Override
  public SeriesCredits findCreditsBySeriesId(Long id) {
    return mapper.toCredits(creditRepository.findBySeriesId(id));
  }

  @Override
  public SeriesDto update(UpdateSeries command) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void delete(Long id) {
    throw new UnsupportedOperationException();
  }
  
  private void saveCredits(Credits credits, Series series) {
    roleRepository.deleteBySeries(series);
    creditRepository.deleteBySeries(series);
    var allCredits = Stream.concat(
        credits.cast().stream().map(c -> (PersonCredit) c), 
        credits.crew().stream().map(c -> (PersonCredit) c))
        .toList();
    var people = resolver.findOrCreate(allCredits);
    var creditsToInsert = new ArrayList<SeriesCredit>();
    var rolesToInsert = new ArrayList<Role>();
    for (var credit : credits.cast()) {
      var roles = credit.roles().stream().map(mapper::toRole).toList();
      rolesToInsert.addAll(roles);
      var seriesCredit = new SeriesCredit();
      seriesCredit.setType(CreditType.CAST);
      seriesCredit.setSeries(series);
      seriesCredit.setPerson(people.get(credit.tmdbId()));
      seriesCredit.setRoles(roles);
      seriesCredit.setTotalEpisodes(roles.stream().mapToInt(Role::getEpisodeCount).sum());
      seriesCredit.setOrder(credit.order());
      creditsToInsert.add(seriesCredit); 
    }
    
    for (var credit : credits.crew()) {
      var jobs = credit.jobs().stream().map(mapper::toRole).toList();
      rolesToInsert.addAll(jobs);
      var seriesCredit = new SeriesCredit();
      seriesCredit.setType(CreditType.CREW);
      seriesCredit.setSeries(series);
      seriesCredit.setPerson(people.get(credit.tmdbId()));
      seriesCredit.setRoles(jobs);
      seriesCredit.setTotalEpisodes(jobs.stream().mapToInt(Role::getEpisodeCount).sum());
      creditsToInsert.add(seriesCredit); 
    }
    
    if (!creditsToInsert.isEmpty()) {
      series.setCredits(creditRepository.insertAll(creditsToInsert));
    }    
    if (!rolesToInsert.isEmpty()) {
      roleRepository.insertAll(rolesToInsert);
    }    
  }
}
