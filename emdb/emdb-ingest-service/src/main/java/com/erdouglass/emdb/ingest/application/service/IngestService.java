package com.erdouglass.emdb.ingest.application.service;

import java.math.BigDecimal;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.jboss.logging.Logger;

import com.erdouglass.emdb.ingest.IngestMediaCommand;
import com.erdouglass.emdb.ingest.application.port.in.IngestMediaUseCase;
import com.erdouglass.emdb.ingest.application.port.out.Movie;
import com.erdouglass.emdb.ingest.application.port.out.MovieRepository;
import com.erdouglass.emdb.ingest.application.port.out.Person;
import com.erdouglass.emdb.ingest.application.port.out.PersonRepository;
import com.erdouglass.emdb.ingest.domain.model.Ingest;
import com.erdouglass.emdb.ingest.domain.model.IngestId;
import com.erdouglass.emdb.ingest.domain.model.TmdbId;

@ApplicationScoped
class IngestService implements IngestMediaUseCase {
  private static final Logger LOGGER = Logger.getLogger(IngestService.class);
  
  @Inject
  MovieRepository movies;
  
  @Inject
  PersonRepository people;

  @Override
  public IngestId ingest(IngestMediaCommand command) {
    var ingest = Ingest.submit(TmdbId.of(command.tmdbId()), command.ingestType());
    LOGGER.infof("ingest: %s", ingest);

    switch (command.ingestType()) {
      case MOVIE -> ingestMovie(ingest.id());
      case PERSON -> ingestPerson(ingest.id());
      case SERIES -> throw new UnsupportedOperationException();
    }
    return ingest.id();
  }
  
  private void ingestMovie(IngestId id) {
    var movie = Movie.builder()
        .tmdbId(78)
        .title("Blade Runner")
        .releaseDate("1982-06-25")
        .score(BigDecimal.valueOf(7.893))
        .originalLanguage("en")
        .overview("In the smog-choked dystopian Los Angeles of 2019, blade runner Rick Deckard is called out of retirement to terminate a quartet of replicants who have escaped to Earth seeking their creator for a way to extend their short life spans.")        
        .build();
    movies.save(id, movie);    
  }
  
  private void ingestPerson(IngestId id) {
    var person = Person.builder()
        .tmdbId(3)
        .name("Harrison Ford")
        .birthDate("1942-07-13")
        .gender("male")
        .biography("Legendary Hollywood Icon Harrison Ford was born on July 13, 1942 in Chicago, Illinois. His family history includes a strong lineage of actors, radio personalities, and models. Ford attended public high school in Park Ridge, Illinois where he was a member of the school Radio Station WMTH. Ford worked as the lead voice for sports reporting at WMTH for several years. Acting wasn't a major interest to Ford until his junior year at Ripon College when he first took an acting class. Ford's career started in 1964 when he travelled to California in search of a voice-over job. He never received that position, but instead signed a contract with Columbia Pictures where he earned $150 weekly to play small fill in roles in various films.\\n\\nThrough the '60s Ford worked on several TV shows including Gunsmoke, Ironside, Kung Fu, and American Style. It wasn't until 1967 that he received his first credited role in the Western film, A Time for Killing. Dissatisfied with the meager roles he was being offered, Ford took a hiatus from acting to work as a self-employed carpenter. This seemingly odd diversion turned out to be a blessing in disguise for Harrison's acting career when he was soon hired by famous film producer George Lucas. This was a turning point in Ford's life that led to him be casted in milestone roles such as Han Solo and Indiana Jones.\\n\\nSince his most famous roles in the original Star Wars trilogy and Raiders of the Lost Ark, Ford has appeared in over 40 films. Many criticize his late-career work, saying his performances have been lackluster, leading to commercially disappointing films. Ford has always worked hard to protect his off-screen private life, keeping details about his children and marriages quiet. He has a total of five children including one recent adoption with third and current wife Calista Flockhart. In addition to acting, Ford is passionate about environmental conservation, aviation, and archeology.")
        .build();
    people.save(id, person);
  }
}
