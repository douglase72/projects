package com.erdouglass.emdb.test.gateway.person;

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
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestMethodOrder;

import com.erdouglass.emdb.common.Gender;
import com.erdouglass.emdb.common.SeriesType;
import com.erdouglass.emdb.common.ShowStatus;
import com.erdouglass.emdb.common.comand.Image;
import com.erdouglass.emdb.common.comand.SaveMovie;
import com.erdouglass.emdb.common.comand.SaveMovie.CastCredit;
import com.erdouglass.emdb.common.comand.SaveMovie.Credits;
import com.erdouglass.emdb.common.comand.SaveMovie.CrewCredit;
import com.erdouglass.emdb.common.comand.SavePerson;
import com.erdouglass.emdb.common.comand.SaveSeries;
import com.erdouglass.emdb.common.comand.SaveSeries.CastCredit.Role;
import com.erdouglass.emdb.common.comand.SaveSeries.CrewCredit.Job;
import com.erdouglass.emdb.common.query.MovieDto;
import com.erdouglass.emdb.common.query.PersonDto;
import com.erdouglass.emdb.common.query.PersonDto.MovieCredit;
import com.erdouglass.emdb.common.query.PersonDto.SeriesCredit;
import com.erdouglass.emdb.common.query.SeriesDto;
import com.erdouglass.emdb.test.gateway.AbstractTest;

@TestInstance(Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PersonCreditIT extends AbstractTest {
  private static final Logger LOGGER = Logger.getLogger(PersonCreditIT.class);
  private static final UUID BACKDROP = UUID.fromString("03592ec0-7ce4-4343-8fcf-8965887be2e6");
  private static final UUID POSTER = UUID.fromString("f4435126-75ca-4e44-9ceb-b414662b7164");
  
  private String token;
  
  @BeforeAll
  void setupSecurity() throws IOException, InterruptedException {
    this.token = getAccessToken();
  }

  @Test
  @Order(1)
  void testSaveGoldmember() throws IOException, InterruptedException {
    var credits = new Credits(
        List.of(new CastCredit(12073, "Austin Powers / Dr. Evil / Goldmember / Fat Bastard", 0)),
        List.of(new CrewCredit(12073, "Screenplay")));
    
    var people = List.of(
        SavePerson.builder()
          .tmdbId(12073)
          .name("Mike Myers")
          .birthDate(LocalDate.parse("1963-05-25"))
          .gender(Gender.MALE)
          .birthPlace("Scarborough, Ontario, Canada")
          .profile(Image.of(UUID.fromString("019d322b-a183-72ac-8fd3-b141badd8067"), "/zp5gxcPnxv6FsDh3l7yRZurlBRr.jpg"))
          .biography("Michael John Myers, OC (born May 25, 1963) is a Canadian actor, comedian, and filmmaker. His accolades include seven MTV Movie & TV Awards, a Primetime Emmy Award, and a Screen Actors Guild Award. In 2002, he was awarded the Hollywood Walk of Fame star. In 2017, he was named an Officer of the Order of Canada for \"his extensive and acclaimed body of comedic work as an actor, writer, and producer.\"\n\nFollowing a series of appearances on several Canadian television programs, Mike Myers attained recognition during his six seasons as a cast member on the NBC sketch comedy series Saturday Night Live from 1989 to 1995, which won him the Primetime Emmy Award for Outstanding Writing for a Variety Series. He subsequently earned praise and numerous accolades for playing the title roles in the Wayne's World (1992–1993), Austin Powers (1997–2002), and Shrek (2001–present) franchises, the latter of which is the second highest-grossing animated film franchise. Myers also played the titular character in the 2003 live-action adaptation of the Dr. Seussbook The Cat in the Hat.\n\nMyers acted sporadically in the 2010s, having supporting roles in Terminal and Bohemian Rhapsody (both 2018). He made his directorial debut with the documentary Supermensch: The Legend of Shep Gordon (2013), which premiered at the Toronto International Film Festival. He created and starred in the 2022 Netflix original series, The Pentaverate, and appeared in David O. Russell's comedy thriller Amsterdam.\n\nDescription above from the Wikipedia article Mike Myers, licensed under CC-BY-SA, full list of contributors on Wikipedia.")        
          .build());    
    
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
        .backdrop(Image.of(BACKDROP, "/mPMtuVB6AEulRhlfn69y5RvgmNT.jpg"))
        .poster(Image.of(POSTER, "/zp5gxcPnxv6FsDh3l7yRZurlBRr.jpg"))
        .tagline("The grooviest movie of the summer has a secret, baby!")
        .overview("The world's most shagadelic spy continues his fight against Dr. Evil. This time, the diabolical doctor and his clone, Mini-Me, team up with a new foe—'70s kingpin Goldmember. While pursuing the team of villains to stop them from world domination, Austin gets help from his dad and an old girlfriend.")
        .credits(credits)
        .people(people)
        .build();
    var request = HttpRequest.newBuilder().uri(UriBuilder.fromUri(MOVIES_URL).build())
        .header("Authorization", "Bearer " + token)
        .POST(HttpRequest.BodyPublishers.ofString(OBJECT_MAPPER.writeValueAsString(command))).build();
    long startTime = System.nanoTime();
    var response = HTTP_CLIENT.send(request, BodyHandlers.ofString());
    long et = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
    assertEquals(201, response.statusCode(), "Server failed with response: " + response.body());
    
    var movie = OBJECT_MAPPER.readValue(response.body(), MovieDto.class);
    LOGGER.infof("Saved movie %d in %d ms", movie.id(), et);
  }
  
  @Test
  @Order(2)
  void testSaveInternationalManOfMystery() throws IOException, InterruptedException {
    var credits = new Credits(
        List.of(new CastCredit(12073, "Austin Powers / Dr. Evil", 0)),
        List.of(new CrewCredit(12073, "Writing")));
    
    var people = List.of(
        SavePerson.builder()
          .tmdbId(12073)
          .name("Mike Myers")
          .birthDate(LocalDate.parse("1963-05-25"))
          .gender(Gender.MALE)
          .birthPlace("Scarborough, Ontario, Canada")
          .profile(Image.of(UUID.fromString("019d322b-a183-72ac-8fd3-b141badd8067"), "/zp5gxcPnxv6FsDh3l7yRZurlBRr.jpg"))
          .biography("Michael John Myers, OC (born May 25, 1963) is a Canadian actor, comedian, and filmmaker. His accolades include seven MTV Movie & TV Awards, a Primetime Emmy Award, and a Screen Actors Guild Award. In 2002, he was awarded the Hollywood Walk of Fame star. In 2017, he was named an Officer of the Order of Canada for \"his extensive and acclaimed body of comedic work as an actor, writer, and producer.\"\n\nFollowing a series of appearances on several Canadian television programs, Mike Myers attained recognition during his six seasons as a cast member on the NBC sketch comedy series Saturday Night Live from 1989 to 1995, which won him the Primetime Emmy Award for Outstanding Writing for a Variety Series. He subsequently earned praise and numerous accolades for playing the title roles in the Wayne's World (1992–1993), Austin Powers (1997–2002), and Shrek (2001–present) franchises, the latter of which is the second highest-grossing animated film franchise. Myers also played the titular character in the 2003 live-action adaptation of the Dr. Seussbook The Cat in the Hat.\n\nMyers acted sporadically in the 2010s, having supporting roles in Terminal and Bohemian Rhapsody (both 2018). He made his directorial debut with the documentary Supermensch: The Legend of Shep Gordon (2013), which premiered at the Toronto International Film Festival. He created and starred in the 2022 Netflix original series, The Pentaverate, and appeared in David O. Russell's comedy thriller Amsterdam.\n\nDescription above from the Wikipedia article Mike Myers, licensed under CC-BY-SA, full list of contributors on Wikipedia.")        
          .build());    
    
    var command = SaveMovie.builder()
        .tmdbId(816)
        .title("Austin Powers: International Man of Mystery")
        .releaseDate(LocalDate.parse("1997-05-02"))
        .score(6.6f)
        .status(ShowStatus.RELEASED)
        .credits(credits)
        .people(people)
        .build();
    var request = HttpRequest.newBuilder().uri(UriBuilder.fromUri(MOVIES_URL).build())
        .header("Authorization", "Bearer " + token)
        .POST(HttpRequest.BodyPublishers.ofString(OBJECT_MAPPER.writeValueAsString(command))).build();
    long startTime = System.nanoTime();
    var response = HTTP_CLIENT.send(request, BodyHandlers.ofString());
    long et = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
    assertEquals(201, response.statusCode(), "Server failed with response: " + response.body());
    
    var movie = OBJECT_MAPPER.readValue(response.body(), MovieDto.class);
    LOGGER.infof("Saved movie %d in %d ms", movie.id(), et);
  }
  
  @Test
  @Order(3)
  void testSaveTheGongShow() throws IOException, InterruptedException {
    var credits = new SaveSeries.Credits(
        List.of(new SaveSeries.CastCredit(12073, List.of(new Role("Tommy Maitland - Host", 17)), 0)),
        List.of(new SaveSeries.CrewCredit(12073, List.of(new Job("Creator", 0)))));
    
    var people = List.of(
        SavePerson.builder()
          .tmdbId(12073)
          .name("Mike Myers")
          .birthDate(LocalDate.parse("1963-05-25"))
          .gender(Gender.MALE)
          .birthPlace("Scarborough, Ontario, Canada")
          .profile(Image.of(UUID.fromString("019d322b-a183-72ac-8fd3-b141badd8067"), "/zp5gxcPnxv6FsDh3l7yRZurlBRr.jpg"))
          .biography("Michael John Myers, OC (born May 25, 1963) is a Canadian actor, comedian, and filmmaker. His accolades include seven MTV Movie & TV Awards, a Primetime Emmy Award, and a Screen Actors Guild Award. In 2002, he was awarded the Hollywood Walk of Fame star. In 2017, he was named an Officer of the Order of Canada for \"his extensive and acclaimed body of comedic work as an actor, writer, and producer.\"\n\nFollowing a series of appearances on several Canadian television programs, Mike Myers attained recognition during his six seasons as a cast member on the NBC sketch comedy series Saturday Night Live from 1989 to 1995, which won him the Primetime Emmy Award for Outstanding Writing for a Variety Series. He subsequently earned praise and numerous accolades for playing the title roles in the Wayne's World (1992–1993), Austin Powers (1997–2002), and Shrek (2001–present) franchises, the latter of which is the second highest-grossing animated film franchise. Myers also played the titular character in the 2003 live-action adaptation of the Dr. Seussbook The Cat in the Hat.\n\nMyers acted sporadically in the 2010s, having supporting roles in Terminal and Bohemian Rhapsody (both 2018). He made his directorial debut with the documentary Supermensch: The Legend of Shep Gordon (2013), which premiered at the Toronto International Film Festival. He created and starred in the 2022 Netflix original series, The Pentaverate, and appeared in David O. Russell's comedy thriller Amsterdam.\n\nDescription above from the Wikipedia article Mike Myers, licensed under CC-BY-SA, full list of contributors on Wikipedia.")        
          .build());  
    
    var command = SaveSeries.builder()
        .tmdbId(72368)
        .title("The Gong Show")
        .score(5.6f)
        .status(ShowStatus.ENDED)
        .type(SeriesType.SCRIPTED)
        .homepage("http://abc.go.com/shows/the-gong-show")
        .originalLanguage("en")
        .overview("Unique performers demonstrate their artistry in front of Hollywood judges in this update of the kitschy '70s talent show, which features the worst performances being cut short by bangs on the titular gong.")
        .credits(credits)
        .people(people)
        .build(); 
    var request = HttpRequest.newBuilder().uri(UriBuilder.fromUri(SERIES_URL).build())
        .header("Authorization", "Bearer " + token)
        .POST(HttpRequest.BodyPublishers.ofString(OBJECT_MAPPER.writeValueAsString(command))).build();
    long startTime = System.nanoTime();
    var response = HTTP_CLIENT.send(request, BodyHandlers.ofString());
    long et = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
    assertEquals(201, response.statusCode(), "Server failed with response: " + response.body());
    
    var series = OBJECT_MAPPER.readValue(response.body(), SeriesDto.class);
    LOGGER.infof("Saved series %d in %d ms", series.id(), et);    
  }
  
  /**
   * This should result in 3 database queries:
   * 1. Person details
   * 2. Person movie credits
   * 3. Person series credits
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
    assertEquals("Austin Powers / Dr. Evil", ((MovieCredit) castCredits.get(0)).character());
    assertEquals("Austin Powers / Dr. Evil / Goldmember / Fat Bastard", ((MovieCredit) castCredits.get(1)).character());
    assertEquals("Tommy Maitland - Host", ((SeriesCredit) castCredits.get(2)).roles().get(0).character());
    var crewCredits = person.credits().crew();
    assertEquals(3, crewCredits.size()); 
    assertEquals("Writing", ((MovieCredit) crewCredits.get(0)).job());
    assertEquals("Screenplay", ((MovieCredit) crewCredits.get(1)).job()); 
    assertEquals("Creator", ((SeriesCredit) crewCredits.get(2)).jobs().get(0).title()); 
    LOGGER.infof("Found Mike Myers in: %d ms", et);    
  }

}
