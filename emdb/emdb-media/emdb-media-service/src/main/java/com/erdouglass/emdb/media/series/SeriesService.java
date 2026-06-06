package com.erdouglass.emdb.media.series;

import java.util.ArrayList;
import java.util.stream.Stream;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import com.erdouglass.emdb.media.PersonCredit;
import com.erdouglass.emdb.media.command.SaveSeries;
import com.erdouglass.emdb.media.command.SaveSeries.CastCredit;
import com.erdouglass.emdb.media.command.SaveSeries.Credits;
import com.erdouglass.emdb.media.command.SaveSeries.CrewCredit;
import com.erdouglass.emdb.media.credit.CreditType;
import com.erdouglass.emdb.media.image.ImageService;
import com.erdouglass.emdb.media.internal.PersonResolver;
import com.erdouglass.emdb.media.logging.Log;

@ApplicationScoped
class SeriesService {
  
  @Inject
  CreditRepository creditRepository;

  @Inject
  ImageService imageService;

  @Inject
  SeriesMapper mapper;
  
  @Inject
  PersonResolver resolver;
  
  @Inject
  RoleRepository roleRepository;
  
  @Inject
  SeriesRepository seriesRepository;
  
  @Log
  @Transactional
  public Series save(final SaveSeries command) {
    Series series;
    var existing = seriesRepository.findByTmdbId(command.tmdbId()).orElse(null);
    if (existing == null) {
      var backdrop = imageService.save(command.backdrop());
      var poster = imageService.save(command.poster());
      series = seriesRepository.insert(mapper.toSeries(command, backdrop, poster));
    } else {
      var backdrop = imageService
          .update(existing.getTmdbBackdrop(), existing.getBackdrop(), command.backdrop());
      var poster = imageService
          .update(existing.getTmdbPoster(), existing.getPoster(), command.poster());
      var cmd = SaveSeries.builder(command)
          .backdrop(backdrop.image())
          .poster(poster.image())
          .build();
      mapper.merge(cmd, existing);
      series = seriesRepository.update(existing);
      backdrop.toDelete().ifPresent(imageService::delete);
      poster.toDelete().ifPresent(imageService::delete);
    } 
    saveCredits(command.credits(), series);
    return series;
  }
  
  /// Replaces all credits for the series: existing credits are deleted, the
  /// referenced people are resolved or created, and a fresh [SeriesCredit] is
  /// inserted for each cast and crew entry.
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
    for (var credit : allCredits) {
      var seriesCredit = new SeriesCredit();
      seriesCredit.setSeries(series);
      seriesCredit.setPerson(people.get(credit.tmdbId()));
      switch (credit) {
        case CastCredit cast -> {
          var roles = cast.roles().stream().map(mapper::toRole).toList();
          rolesToInsert.addAll(roles);
          seriesCredit.setType(CreditType.CAST);
          seriesCredit.setRoles(roles);
          seriesCredit.setTotalEpisodes(roles.stream().mapToInt(Role::getEpisodeCount).sum());
          seriesCredit.setOrder(cast.order());
        }
        case CrewCredit crew -> {
          var jobs = crew.jobs().stream().map(mapper::toRole).toList();
          rolesToInsert.addAll(jobs);
          seriesCredit.setType(CreditType.CREW);
          seriesCredit.setRoles(jobs);
          seriesCredit.setTotalEpisodes(jobs.stream().mapToInt(Role::getEpisodeCount).sum());
        }
        default -> throw new IllegalArgumentException("Invalid credit: " + credit);
      }
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
