package com.erdouglass.emdb.scraper.service;

import java.time.LocalDate;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.jboss.logging.Logger;

import com.erdouglass.emdb.common.ShowStatus;
import com.erdouglass.emdb.common.command.MovieCreateCommand;

import io.smallrye.reactive.messaging.rabbitmq.OutgoingRabbitMQMetadata;

@ApplicationScoped
public class TmdbMovieScraper {
  private static final Logger LOGGER = Logger.getLogger(TmdbMovieScraper.class);
  private static final String CREATE_KEY = "movie.create";
  
  @Inject
  @Channel("movie-create-out") 
  Emitter<MovieCreateCommand> emitter;
  
  public void onMessage() {
    var cmd = MovieCreateCommand.builder()
        .tmdbId(818)
        .title("Austin Powers in Goldmember")
        .releaseDate(LocalDate.parse("2002-07-26"))
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
    var message = Message.of(cmd)
        .addMetadata(OutgoingRabbitMQMetadata.builder()
            .withRoutingKey(CREATE_KEY)
            .build());
    emitter.send(message);
    LOGGER.infof("Sent: %s", cmd);
  }

}
