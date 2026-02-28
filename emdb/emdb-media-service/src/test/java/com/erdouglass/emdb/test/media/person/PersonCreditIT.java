package com.erdouglass.emdb.test.media.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.IOException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import jakarta.ws.rs.core.UriBuilder;

import org.jboss.logging.Logger;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestMethodOrder;

import com.erdouglass.emdb.common.Gender;
import com.erdouglass.emdb.common.SeriesType;
import com.erdouglass.emdb.common.ShowStatus;
import com.erdouglass.emdb.common.comand.SaveMovie;
import com.erdouglass.emdb.common.comand.SaveMovie.Credits;
import com.erdouglass.emdb.common.comand.SaveMovieCastCredit;
import com.erdouglass.emdb.common.comand.SaveMovieCrewCredit;
import com.erdouglass.emdb.common.comand.SavePerson;
import com.erdouglass.emdb.common.comand.SaveSeries;
import com.erdouglass.emdb.common.comand.SaveSeriesCastCredit;
import com.erdouglass.emdb.common.comand.SaveSeriesCrewCredit;
import com.erdouglass.emdb.common.query.MovieDto;
import com.erdouglass.emdb.common.query.PersonDto;
import com.erdouglass.emdb.common.query.PersonMovieCreditDto;
import com.erdouglass.emdb.common.query.PersonSeriesCreditDto;
import com.erdouglass.emdb.common.query.SeriesDto;
import com.erdouglass.emdb.test.media.AbstractTest;

@TestInstance(Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PersonCreditIT extends AbstractTest {
  private static final Logger LOGGER = Logger.getLogger(PersonCreditIT.class);
  private static final UUID BACKDROP = UUID.fromString("03592ec0-7ce4-4343-8fcf-8965887be2e6");
  private static final UUID POSTER = UUID.fromString("f4435126-75ca-4e44-9ceb-b414662b7164");

  @Test
  @Order(1)
  void testSaveGoldmember() throws IOException, InterruptedException {
    var credits =  new Credits(List.of(
        SaveMovieCastCredit.builder()
          .character("Austin Powers / Dr. Evil / Goldmember / Fat Bastard")
          .person(SavePerson.builder()
              .tmdbId(12073)
              .name("Mike Myers")
              .birthDate(LocalDate.parse("1963-05-25"))
              .gender(Gender.MALE)
              .birthPlace("Scarborough, Ontario, Canada")
              .profile(UUID.fromString("44e52ae5-79b7-406d-96a8-9ec8545c4dad"))
              .biography("Michael John Myers, OC (born May 25, 1963) is a Canadian actor, comedian, and filmmaker. His accolades include seven MTV Movie & TV Awards, a Primetime Emmy Award, and a Screen Actors Guild Award. In 2002, he was awarded the Hollywood Walk of Fame star. In 2017, he was named an Officer of the Order of Canada for \"his extensive and acclaimed body of comedic work as an actor, writer, and producer.\"\n\nFollowing a series of appearances on several Canadian television programs, Mike Myers attained recognition during his six seasons as a cast member on the NBC sketch comedy series Saturday Night Live from 1989 to 1995, which won him the Primetime Emmy Award for Outstanding Writing for a Variety Series. He subsequently earned praise and numerous accolades for playing the title roles in the Wayne's World (1992–1993), Austin Powers (1997–2002), and Shrek (2001–present) franchises, the latter of which is the second highest-grossing animated film franchise. Myers also played the titular character in the 2003 live-action adaptation of the Dr. Seussbook The Cat in the Hat.\n\nMyers acted sporadically in the 2010s, having supporting roles in Terminal and Bohemian Rhapsody (both 2018). He made his directorial debut with the documentary Supermensch: The Legend of Shep Gordon (2013), which premiered at the Toronto International Film Festival. He created and starred in the 2022 Netflix original series, The Pentaverate, and appeared in David O. Russell's comedy thriller Amsterdam.\n\nDescription above from the Wikipedia article Mike Myers, licensed under CC-BY-SA, full list of contributors on Wikipedia.")        
              .build())
          .order(0)
          .build()),
      List.of(
        SaveMovieCrewCredit.builder()
          .job("Screenplay")
          .person(SavePerson.builder()
              .tmdbId(12073)
              .name("Mike Myers")
              .birthDate(LocalDate.parse("1963-05-25"))
              .gender(Gender.MALE)
              .birthPlace("Scarborough, Ontario, Canada")
              .profile(UUID.fromString("44e52ae5-79b7-406d-96a8-9ec8545c4dad"))
              .biography("Michael John Myers, OC (born May 25, 1963) is a Canadian actor, comedian, and filmmaker. His accolades include seven MTV Movie & TV Awards, a Primetime Emmy Award, and a Screen Actors Guild Award. In 2002, he was awarded the Hollywood Walk of Fame star. In 2017, he was named an Officer of the Order of Canada for \"his extensive and acclaimed body of comedic work as an actor, writer, and producer.\"\n\nFollowing a series of appearances on several Canadian television programs, Mike Myers attained recognition during his six seasons as a cast member on the NBC sketch comedy series Saturday Night Live from 1989 to 1995, which won him the Primetime Emmy Award for Outstanding Writing for a Variety Series. He subsequently earned praise and numerous accolades for playing the title roles in the Wayne's World (1992–1993), Austin Powers (1997–2002), and Shrek (2001–present) franchises, the latter of which is the second highest-grossing animated film franchise. Myers also played the titular character in the 2003 live-action adaptation of the Dr. Seussbook The Cat in the Hat.\n\nMyers acted sporadically in the 2010s, having supporting roles in Terminal and Bohemian Rhapsody (both 2018). He made his directorial debut with the documentary Supermensch: The Legend of Shep Gordon (2013), which premiered at the Toronto International Film Festival. He created and starred in the 2022 Netflix original series, The Pentaverate, and appeared in David O. Russell's comedy thriller Amsterdam.\n\nDescription above from the Wikipedia article Mike Myers, licensed under CC-BY-SA, full list of contributors on Wikipedia.")        
              .build())
          .build()));
    
    var command = SaveMovie.builder()
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
        .backdrop(BACKDROP)
        .poster(POSTER)
        .tagline("The grooviest movie of the summer has a secret, baby!")
        .overview("The world's most shagadelic spy continues his fight against Dr. Evil. This time, the diabolical doctor and his clone, Mini-Me, team up with a new foe—'70s kingpin Goldmember. While pursuing the team of villains to stop them from world domination, Austin gets help from his dad and an old girlfriend.")
        .credits(credits)
        .build();
    var request = HttpRequest.newBuilder().uri(UriBuilder.fromUri(MOVIES_URL).build())
        .POST(HttpRequest.BodyPublishers.ofString(OBJECT_MAPPER.writeValueAsString(command))).build();
    long startTime = System.nanoTime();
    var response = HTTP_CLIENT.send(request, BodyHandlers.ofString());
    long et = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
    var movie = OBJECT_MAPPER.readValue(response.body(), MovieDto.class);
    assertEquals(200, response.statusCode());
    assertEquals(818, movie.tmdbId());
    assertEquals("Austin Powers in Goldmember", movie.title());
    assertEquals("2002-07-26", movie.releaseDate().toString());
    assertEquals(5.992f, movie.score());
    assertEquals(ShowStatus.RELEASED, movie.status());
    assertEquals(94, movie.runtime());
    assertEquals(63000000, movie.budget());
    assertEquals(296938801, movie.revenue());
    assertEquals("https://www.warnerbros.com/movies/austin-powers-goldmember", movie.homepage());
    assertEquals("en", movie.originalLanguage());
    assertEquals("03592ec0-7ce4-4343-8fcf-8965887be2e6.jpg", movie.backdrop());
    assertEquals("f4435126-75ca-4e44-9ceb-b414662b7164.jpg", movie.poster());
    assertEquals("The grooviest movie of the summer has a secret, baby!", movie.tagline());
    assertEquals("The world's most shagadelic spy continues his fight against Dr. Evil. This time, the diabolical doctor and his clone, Mini-Me, team up with a new foe—'70s kingpin Goldmember. While pursuing the team of villains to stop them from world domination, Austin gets help from his dad and an old girlfriend.", movie.overview());    
    LOGGER.infof("Saved Austin Powers in Goldmember in %d ms", et);
  }
  
  @Test
  @Order(2)
  void testSaveInternationalManOfMystery() throws IOException, InterruptedException {
    var credits = new Credits(List.of(
        SaveMovieCastCredit.builder()
          .character("Austin Powers / Dr. Evil")
          .person(SavePerson.builder()
              .tmdbId(12073)
              .name("Mike Myers")
              .birthDate(LocalDate.parse("1963-05-25"))
              .gender(Gender.MALE)
              .birthPlace("Scarborough, Ontario, Canada")
              .profile(UUID.fromString("44e52ae5-79b7-406d-96a8-9ec8545c4dad"))
              .biography("Michael John Myers, OC (born May 25, 1963) is a Canadian actor, comedian, and filmmaker. His accolades include seven MTV Movie & TV Awards, a Primetime Emmy Award, and a Screen Actors Guild Award. In 2002, he was awarded the Hollywood Walk of Fame star. In 2017, he was named an Officer of the Order of Canada for \"his extensive and acclaimed body of comedic work as an actor, writer, and producer.\"\n\nFollowing a series of appearances on several Canadian television programs, Mike Myers attained recognition during his six seasons as a cast member on the NBC sketch comedy series Saturday Night Live from 1989 to 1995, which won him the Primetime Emmy Award for Outstanding Writing for a Variety Series. He subsequently earned praise and numerous accolades for playing the title roles in the Wayne's World (1992–1993), Austin Powers (1997–2002), and Shrek (2001–present) franchises, the latter of which is the second highest-grossing animated film franchise. Myers also played the titular character in the 2003 live-action adaptation of the Dr. Seussbook The Cat in the Hat.\n\nMyers acted sporadically in the 2010s, having supporting roles in Terminal and Bohemian Rhapsody (both 2018). He made his directorial debut with the documentary Supermensch: The Legend of Shep Gordon (2013), which premiered at the Toronto International Film Festival. He created and starred in the 2022 Netflix original series, The Pentaverate, and appeared in David O. Russell's comedy thriller Amsterdam.\n\nDescription above from the Wikipedia article Mike Myers, licensed under CC-BY-SA, full list of contributors on Wikipedia.")        
              .build())
          .order(0)
          .build()),
      List.of(
        SaveMovieCrewCredit.builder()
          .job("Writing")
          .person(SavePerson.builder()
              .tmdbId(12073)
              .name("Mike Myers")
              .birthDate(LocalDate.parse("1963-05-25"))
              .gender(Gender.MALE)
              .birthPlace("Scarborough, Ontario, Canada")
              .profile(UUID.fromString("44e52ae5-79b7-406d-96a8-9ec8545c4dad"))
              .biography("Michael John Myers, OC (born May 25, 1963) is a Canadian actor, comedian, and filmmaker. His accolades include seven MTV Movie & TV Awards, a Primetime Emmy Award, and a Screen Actors Guild Award. In 2002, he was awarded the Hollywood Walk of Fame star. In 2017, he was named an Officer of the Order of Canada for \"his extensive and acclaimed body of comedic work as an actor, writer, and producer.\"\n\nFollowing a series of appearances on several Canadian television programs, Mike Myers attained recognition during his six seasons as a cast member on the NBC sketch comedy series Saturday Night Live from 1989 to 1995, which won him the Primetime Emmy Award for Outstanding Writing for a Variety Series. He subsequently earned praise and numerous accolades for playing the title roles in the Wayne's World (1992–1993), Austin Powers (1997–2002), and Shrek (2001–present) franchises, the latter of which is the second highest-grossing animated film franchise. Myers also played the titular character in the 2003 live-action adaptation of the Dr. Seussbook The Cat in the Hat.\n\nMyers acted sporadically in the 2010s, having supporting roles in Terminal and Bohemian Rhapsody (both 2018). He made his directorial debut with the documentary Supermensch: The Legend of Shep Gordon (2013), which premiered at the Toronto International Film Festival. He created and starred in the 2022 Netflix original series, The Pentaverate, and appeared in David O. Russell's comedy thriller Amsterdam.\n\nDescription above from the Wikipedia article Mike Myers, licensed under CC-BY-SA, full list of contributors on Wikipedia.")        
              .build())
          .build()));
    
    var command = SaveMovie.builder()
        .tmdbId(816)
        .title("Austin Powers: International Man of Mystery")
        .releaseDate(LocalDate.parse("1997-05-02"))
        .score(6.6f)
        .status(ShowStatus.RELEASED)
        .runtime(94)
        .credits(credits)
        .build();
    var request = HttpRequest.newBuilder().uri(UriBuilder.fromUri(MOVIES_URL).build())
        .POST(HttpRequest.BodyPublishers.ofString(OBJECT_MAPPER.writeValueAsString(command))).build();
    long startTime = System.nanoTime();
    var response = HTTP_CLIENT.send(request, BodyHandlers.ofString());
    long et = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
    var movie = OBJECT_MAPPER.readValue(response.body(), MovieDto.class);
    assertEquals(200, response.statusCode());
    assertEquals(816, movie.tmdbId());
    LOGGER.infof("Saved Austin Powers: International Man of Mystery in %d ms", et);
  }
  
  @Test
  @Order(3)
  void testSaveTheGongShow() throws IOException, InterruptedException {
    var credits = new SaveSeries.Credits(List.of(
        SaveSeriesCastCredit.builder()
          .person(SavePerson.builder()
            .tmdbId(12073)
            .name("Mike Myers")
            .birthDate(LocalDate.parse("1963-05-25"))
            .gender(Gender.MALE)
            .birthPlace("Scarborough, Ontario, Canada")
            .profile(UUID.fromString("44e52ae5-79b7-406d-96a8-9ec8545c4dad"))
            .biography("Michael John Myers, OC (born May 25, 1963) is a Canadian actor, comedian, and filmmaker. His accolades include seven MTV Movie & TV Awards, a Primetime Emmy Award, and a Screen Actors Guild Award. In 2002, he was awarded the Hollywood Walk of Fame star. In 2017, he was named an Officer of the Order of Canada for \"his extensive and acclaimed body of comedic work as an actor, writer, and producer.\"\n\nFollowing a series of appearances on several Canadian television programs, Mike Myers attained recognition during his six seasons as a cast member on the NBC sketch comedy series Saturday Night Live from 1989 to 1995, which won him the Primetime Emmy Award for Outstanding Writing for a Variety Series. He subsequently earned praise and numerous accolades for playing the title roles in the Wayne's World (1992–1993), Austin Powers (1997–2002), and Shrek (2001–present) franchises, the latter of which is the second highest-grossing animated film franchise. Myers also played the titular character in the 2003 live-action adaptation of the Dr. Seussbook The Cat in the Hat.\n\nMyers acted sporadically in the 2010s, having supporting roles in Terminal and Bohemian Rhapsody (both 2018). He made his directorial debut with the documentary Supermensch: The Legend of Shep Gordon (2013), which premiered at the Toronto International Film Festival. He created and starred in the 2022 Netflix original series, The Pentaverate, and appeared in David O. Russell's comedy thriller Amsterdam.\n\nDescription above from the Wikipedia article Mike Myers, licensed under CC-BY-SA, full list of contributors on Wikipedia.")        
            .build())
          .role("Tommy Maitland - Host", 17)
          .order(0)
          .build()), 
      List.of(
        SaveSeriesCrewCredit.builder()
          .person(SavePerson.builder()
            .tmdbId(5741)
            .name("Matt Groening")
            .birthDate(LocalDate.parse("1954-02-15"))
            .gender(Gender.MALE)
            .birthPlace("Portland, Oregon, USA")
            .profile(UUID.fromString("69e52ae5-79b7-406d-96a8-9ec8545c4dad"))
            .biography("Matthew Abram Groening (/ˈɡreɪnɪŋ/ GRAY-ning; born February 15, 1954) is an American cartoonist, writer, producer, and animator. He is best known as the creator of the television series The Simpsons (1989–present), Futurama (1999–2003, 2008–2013, 2023–present), and Disenchantment (2018–2023), as well as the comic strip Life in Hell (1977–2012). The Simpsons is the longest-running American primetime television series in history, as well as the longest-running American animated series and sitcom.\\n\\nBorn in Portland, Oregon, Groening made his first professional cartoon sale, of Life in Hell, to the avant-garde magazine Wet in 1978. At its peak, it was carried in 250 weekly newspapers and caught the attention of American producer James L. Brooks, who contacted Groening in 1985 about adapting it for animated sequences for the Fox 21st Century variety show The Tracey Ullman Show. Fearing the loss of ownership rights, Groening created a new set of characters, the Simpson family. The shorts were spun off into their own series, The Simpsons, which has since aired 791 episodes.\\n\\nIn 1997, Groening and former Simpsons writer David X. Cohen developed Futurama, an animated series about life in the year 3000, which premiered in 1999. It ran for four years on Fox, was picked up in 2008 by Comedy Central for another five years, and was finally picked up by Hulu for another revival in 2023. In 2016, Groening developed a new series for Netflix, Disenchantment, which premiered in August 2018.\\n\\nGroening has won 14 Primetime Emmy Awards, 12 for The Simpsons and 2 for Futurama, and a British Comedy Award for \\\"outstanding contribution to comedy\\\" in 2004. In 2002, he won the National Cartoonist Society Reuben Award for his work on Life in Hell. He received a star on the Hollywood Walk of Fame on February 14, 2012.\\n\\nDescription above from the Wikipedia article Matt Groening, licensed under CC-BY-SA, full list of contributors on Wikipedia.")
            .build())
          .job("Creator", 0)
          .job("Writer", 15)
          .job("Executive Producer", 411)
          .build()));
    
    var command = SaveSeries.builder()
        .tmdbId(72368)
        .title("The Gong Show")
        .score(5.6f)
        .status(ShowStatus.ENDED)
        .type(SeriesType.SCRIPTED)
        .homepage("http://abc.go.com/shows/the-gong-show")
        .originalLanguage("en")
        .backdrop(BACKDROP)
        .poster(POSTER)
        .overview("Unique performers demonstrate their artistry in front of Hollywood judges in this update of the kitschy '70s talent show, which features the worst performances being cut short by bangs on the titular gong.")
        .credits(credits)
        .build();
    var request = HttpRequest.newBuilder().uri(UriBuilder.fromUri(SERIES_URL).build())
        .POST(HttpRequest.BodyPublishers.ofString(OBJECT_MAPPER.writeValueAsString(command))).build();
    long startTime = System.nanoTime();
    var response = HTTP_CLIENT.send(request, BodyHandlers.ofString());
    long et = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
    var series = OBJECT_MAPPER.readValue(response.body(), SeriesDto.class);
    assertEquals(200, response.statusCode());
    assertEquals(72368, series.tmdbId());
     LOGGER.infof("Saved The Gong Show %d in %d ms", series.id(), et);
  }  
  
  /**
   * This should result in 2 database queries:
   * 1. Person details
   * 2. Person credits
   */
  @Test
  @Order(4)
  void testFindMikeMyers() throws IOException, InterruptedException {
    var request = HttpRequest.newBuilder()
        .uri(UriBuilder.fromUri(PEOPLE_URL).path("1").queryParam("append", "credits").build())
        .build();
    long startTime = System.nanoTime();
    var response = HTTP_CLIENT.send(request, BodyHandlers.ofString());
    long et = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
    var person = OBJECT_MAPPER.readValue(response.body(), PersonDto.class);
    assertEquals(200, response.statusCode());   
    assertEquals(12073, person.tmdbId());
    assertEquals("Mike Myers", person.name());
    assertEquals("1963-05-25", person.birthDate().toString());
    assertNull(person.deathDate());
    assertEquals(Gender.MALE, person.gender());
    var castCredits = person.credits().cast();
    assertEquals(3, castCredits.size()); 
    assertEquals("Austin Powers / Dr. Evil", ((PersonMovieCreditDto) castCredits.get(0)).character());
    assertEquals("Austin Powers / Dr. Evil / Goldmember / Fat Bastard", ((PersonMovieCreditDto) castCredits.get(1)).character());
    assertEquals("Tommy Maitland - Host", ((PersonSeriesCreditDto) castCredits.get(2)).roles().get(0).character());
    var crewCredits = person.credits().crew();
    assertEquals(2, crewCredits.size()); 
    assertEquals("Writing", ((PersonMovieCreditDto) crewCredits.get(0)).job());
    assertEquals("Screenplay", ((PersonMovieCreditDto) crewCredits.get(1)).job());    
    LOGGER.infof("Found Mike Myers in: %d ms", et);    
  }

}
