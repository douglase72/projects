package com.erdouglass.emdb.ingest.core.series;

import java.io.IOException;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolationException;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.reactive.messaging.Message;

import com.erdouglass.emdb.ingest.core.IngestHandler;
import com.erdouglass.emdb.ingest.logging.Log;
import com.erdouglass.emdb.media.IngestMedia;
import com.erdouglass.emdb.media.series.SaveSeries;
import com.erdouglass.emdb.media.series.SeriesCommandService;
import com.erdouglass.emdb.media.series.SeriesDto;

@ApplicationScoped
public class SeriesIngestHandler extends IngestHandler<SaveSeries, SeriesDto> {
  
  @ConfigProperty(name = "emdb.series.data")
  String path;
  
  @Inject
  SeriesScraper scraper;
  
  @Inject
  SeriesCommandService service;

  @Log
  @Override
  public SeriesDto ingest(Message<IngestMedia> message) throws IOException {
    var payload = message.getPayload();
    var command = scraper.scrape(payload.tmdbId());
    
    try {
      return service.save(command);
    } catch (ConstraintViolationException e) {
      var cmd = SaveSeries.builder(command)
          .backdrop(saveImage(command.backdrop()))
          .poster(saveImage(command.poster()))
          .build();
      saveMessage(message, cmd);
      throw e;
    }
  }

  @Override
  protected String mediaPath() {
    return path;
  }
}
