package com.erdouglass.emdb.scraper.producer;

import java.time.LocalDate;
import java.util.List;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.jboss.logging.Logger;

import com.erdouglass.emdb.common.CreditType;
import com.erdouglass.emdb.common.ShowStatus;
import com.erdouglass.emdb.common.command.MovieCreateCommand;
import com.erdouglass.emdb.common.command.MovieCreditCreateCommand;
import com.erdouglass.emdb.common.command.MovieMessage;
import com.erdouglass.emdb.common.command.PersonCreateCommand;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class TmdbMovieScraper {
  private static final Logger LOGGER = Logger.getLogger(TmdbMovieScraper.class);

  @Inject
  @Channel("movies")
  Emitter<MovieMessage> emitter;

  public void ingest(int tmdbId) {
    LOGGER.infof("Ingesting TMDB movie id: %d", tmdbId);
    var movie = findMovie(tmdbId);
    var credits = findCredits(movie);
    var people = findPeople();
    var message = new MovieMessage(movie, credits, people);
    emitter.send(message);
    LOGGER.infof("Sent: %s", message);
  }

  public void synchronize(long emdbId, int tmdbId) {
    LOGGER.infof("Synchronizing EMDB movie id: %d with TMDB movie id: %d", emdbId, tmdbId);
  }
  
  private List<MovieCreditCreateCommand> findCredits(MovieCreateCommand movie) {
    var credits = List.of(
        MovieCreditCreateCommand.builder()
          .creditType(CreditType.CAST)
          .personId(34)
          .role("Austin Powers / Dr. Evil / Goldmember / Fat Bastard")
          .order(0)
          .build(),
        MovieCreditCreateCommand.builder()
          .creditType(CreditType.CAST)
          .personId(565)
          .role("Scott Evil")
          .order(2)
          .build());
    LOGGER.infof("Found: %d credits", credits.size());
    return credits;
  }

  private MovieCreateCommand findMovie(int tmdbId) {
    var movie = MovieCreateCommand.builder()
        .tmdbId(tmdbId)
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
    LOGGER.infof("Found: %s", movie);
    return movie;
  }

  private List<PersonCreateCommand> findPeople() {
    var people = List.of(
        PersonCreateCommand.builder().tmdbId(3).name("Harrison Ford").build(),
        PersonCreateCommand.builder().tmdbId(565).name("Rutger Hauer").build(),
        PersonCreateCommand.builder().tmdbId(1893).name("Elizabeth Hurely").build());
    LOGGER.infof("Found: %d people", people.size());
    return people;
  }

}
