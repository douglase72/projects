package com.erdouglass.emdb.media.movie.adapter.in.messaging;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Incoming;

import com.erdouglass.emdb.media.movie.application.port.in.ReportMovieFailureUseCase;
import com.erdouglass.emdb.media.movie.application.port.in.SaveMovieCommand;
import com.erdouglass.emdb.media.movie.application.port.in.SaveMovieUseCase;
import com.erdouglass.emdb.media.movie.domain.event.MovieFailed;
import com.erdouglass.emdb.media.movie.domain.model.MovieDetails;
import com.erdouglass.emdb.media.movie.domain.model.ReleaseDate;
import com.erdouglass.emdb.media.shared.domain.model.LanguageCode;
import com.erdouglass.emdb.media.shared.domain.model.Overview;
import com.erdouglass.emdb.media.shared.domain.model.Score;
import com.erdouglass.emdb.media.shared.domain.model.Title;
import com.erdouglass.emdb.scraper.messaging.MovieScraped;
import com.erdouglass.emdb.shared.kernel.CorrelationId;
import com.erdouglass.emdb.shared.kernel.CorrelationScope;

import io.smallrye.common.annotation.Blocking;

@ApplicationScoped
class MovieScrapedConsumer {
  
  @Inject
  SaveMovieUseCase saveUseCase;
  
  @Inject
  ReportMovieFailureUseCase failures;

  @Blocking
  @Incoming("movies-scraped")
  void onMessage(MovieScraped event) {
    try (var _ = CorrelationScope.open(CorrelationId.of(event.correlationId().value()))) {
      var command = SaveMovieCommand.builder()
          .id(event.id())
          .correlationId(event.correlationId())
          .tmdbId(event.tmdbId())
          .details(toMovieDetails(event))
          .build();
      saveUseCase.save(command);
    } catch (IllegalArgumentException e) {
      failures.report(MovieFailed.builder()
          .id(event.id())
          .correlationId(event.correlationId())
          .tmdbId(event.tmdbId())
          .reason(e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName())
          .build());
      throw e;
    }
  }
  
  private MovieDetails toMovieDetails(MovieScraped event) {
    return MovieDetails.builder()
        .title(Title.of(event.title()))
        .releaseDate(ReleaseDate.from(event.releaseDate()))
        .score(Score.of(event.score()))
        .originalLanguage(LanguageCode.of(event.originalLanguage()))
        .overview(Overview.of(event.overview()))
        .build();
  }
}
