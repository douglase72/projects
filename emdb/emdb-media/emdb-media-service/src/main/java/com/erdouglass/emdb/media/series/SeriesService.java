package com.erdouglass.emdb.media.series;

import java.util.ArrayList;
import java.util.stream.Stream;

import jakarta.data.page.PageRequest;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import com.erdouglass.common.graphql.ResourceNotFoundException;
import com.erdouglass.emdb.media.PersonCredit;
import com.erdouglass.emdb.media.command.SaveSeries;
import com.erdouglass.emdb.media.command.SaveSeries.CastCredit;
import com.erdouglass.emdb.media.command.SaveSeries.Credits;
import com.erdouglass.emdb.media.command.SaveSeries.CrewCredit;
import com.erdouglass.emdb.media.credit.CreditType;
import com.erdouglass.emdb.media.image.ImageService;
import com.erdouglass.emdb.media.internal.PersonResolver;
import com.erdouglass.emdb.media.logging.Log;
import com.erdouglass.emdb.media.query.OffsetPage;
import com.erdouglass.emdb.media.query.SeriesResponse;

/// Application service that orchestrates persistence of [Series] aggregates,
/// including their poster/backdrop images and cast & crew credits with their
/// per-role episode counts. Reconciles each [SaveSeries] command against
/// existing records, inserting a new series or merging an update, and cleaning
/// up any images it replaces.
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
    
  /// Persists a series from the command, creating it when no series with the
  /// same TMDB id exists and updating the existing one otherwise. Poster and
  /// backdrop images are saved or replaced as needed, and the full cast & crew —
  /// including each credit's roles and episode counts — is rebuilt from the
  /// command.
  ///
  /// @param command the series data to persist
  /// @return the saved series, with generated identifiers and credits populated
  @Log
  @Transactional
  public SeriesResponse save(final SaveSeries command) {
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
    return mapper.toSeriesResponse(series);
  }
  
  @Log("Found:")
  @Transactional
  public OffsetPage<SeriesResponse> findAll(final SeriesQuery query) {
    var pageRequest = PageRequest.ofPage(query.page(), query.size(), true);
    var seriesPage = seriesRepository.findAll(pageRequest, query.sort().sortOrder());
    var results = seriesPage.stream()
        .map(mapper::toSeriesView)
        .toList();
    return new OffsetPage<>(results, query.page().intValue(), results.size(), seriesPage.totalElements());
  }
  
  /// Looks up a single series by id for read/query use.
  ///
  /// @param id the series id
  /// @return the series view
  /// @throws ResourceNotFoundException if no series has the given id
  @Log
  @Transactional
  public SeriesResponse findById(final Long id) {
    return seriesRepository.findById(id)
      .map(mapper::toSeriesView)
      .orElseThrow(() -> new ResourceNotFoundException("No series found with id: " + id));    
  }
  
  /// Resolves the cast & crew credits for a series, with each credit's roles.
  ///
  /// @param seriesId the series id
  /// @return the series' credits
  @Transactional
  public SeriesResponse.Credits findCreditsBySeriesId(final Long seriesId) {
    return mapper.toCredits(creditRepository.findBySeriesId(seriesId));
  }
  
  /// Replaces all credits for the series: existing roles and credits are
  /// deleted, the referenced people are resolved or created, and a fresh
  /// [SeriesCredit] — with its [Role] entries and summed {@code totalEpisodes} —
  /// is inserted for each cast and crew entry. Credits and roles are inserted in
  /// separate batches.
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
