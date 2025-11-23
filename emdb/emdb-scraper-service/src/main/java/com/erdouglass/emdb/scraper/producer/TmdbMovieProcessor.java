package com.erdouglass.emdb.scraper.producer;

import java.time.LocalDate;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;

import com.erdouglass.emdb.common.ShowStatus;
import com.erdouglass.emdb.common.command.MovieCreateCommand;
import com.erdouglass.emdb.common.query.MovieStatus;
import com.erdouglass.emdb.common.query.MovieStatus.MessageStatus;
import com.erdouglass.emdb.common.query.MovieStatus.MessageType;

import io.smallrye.common.annotation.RunOnVirtualThread;
import io.vertx.core.json.JsonObject;

@ApplicationScoped
public class TmdbMovieProcessor {
  private static final Logger LOGGER = Logger.getLogger(TmdbMovieProcessor.class);
  
  @Inject
  @Channel("movie-status-out") 
  Emitter<MovieStatus> statusEmitter;
  
  @Inject
  @Channel("movie-create-out") 
  Emitter<MovieCreateCommand> createEmitter;
  
  @RunOnVirtualThread
  @Incoming("movie-request-in")
  public void scrape(JsonObject jsonObject) {
    MovieStatus message = jsonObject.mapTo(MovieStatus.class);
    switch (message.type()) {
      case INGEST -> ingest(message.tmdbId());
      case SYNCHRONIZE -> { }
    };
  }
  
  private void ingest(int tmdbId) {
    statusEmitter.send(MovieStatus.of(tmdbId, MessageType.INGEST, MessageStatus.RECEIVED));

    // Get the movie details plus cast and crew from TMDB.
    LOGGER.infof("Found: %d", tmdbId);
    statusEmitter.send(MovieStatus.of(tmdbId, MessageType.INGEST, MessageStatus.EXTRACTED));

    // Send the command to create the movie to EMDB.
    var command = MovieCreateCommand.builder()
        .tmdbId(tmdbId)
        .title("Austin Powers in Goldmember")
        .releaseDate(LocalDate.parse("1702-07-26"))
        .score(5.992f)
        .status(ShowStatus.RELEASED)
        .runtime(94)
        .budget(63000000)
        .revenue(296938801)
        .homepage("https://www.warnerbros.com/movies/austin-powers-goldmember")
        .originalLanguage("en")
        .backdrop("/kuPpElzfYnzsCye0hF8EbJSrvwo.jpg")
        .poster("/n8V61f1v7idya4WJzGEJNoIp9iL.jpg")
        .tagline("The grooviest movie of the summer has a secret, baby!")
        .overview("The world's most shagadelic spy continues his fight against Dr. Evil. This time, the diabolical doctor and his clone, Mini-Me, team up with a new foe—'70s kingpin Goldmember. While pursuing the team of villains to stop them from world domination, Austin gets help from his dad and an old girlfriend.")        
        .build();
    createEmitter.send(command);
  }

}
