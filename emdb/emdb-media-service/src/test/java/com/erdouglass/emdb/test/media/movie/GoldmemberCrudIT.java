package com.erdouglass.emdb.test.media.movie;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import jakarta.ws.rs.core.UriBuilder;

import org.jboss.logging.Logger;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestMethodOrder;

import com.erdouglass.emdb.common.Gender;
import com.erdouglass.emdb.common.ShowStatus;
import com.erdouglass.emdb.common.comand.SaveMovie;
import com.erdouglass.emdb.common.comand.SavePerson;
import com.erdouglass.emdb.common.query.MovieDto;
import com.erdouglass.emdb.test.media.AbstractTest;

@TestInstance(Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class GoldmemberCrudIT extends AbstractTest {
  private static final Logger LOGGER = Logger.getLogger(GoldmemberCrudIT.class);
  private static final UUID BACKDROP = UUID.fromString("03592ec0-7ce4-4343-8fcf-8965887be2e6");
  private static final UUID POSTER = UUID.fromString("f4435126-75ca-4e44-9ceb-b414662b7164");
  
  private Long movieId;
  
  @Test
  @Order(1)
  void testSaveMovie() throws IOException, InterruptedException {
    var people = List.of(
        SavePerson.builder()
          .tmdbId(12073)
          .name("Mike Myers")
          .birthDate(LocalDate.parse("1963-05-25"))
          .gender(Gender.MALE)
          .birthPlace("Scarborough, Ontario, Canada")
          .profile(UUID.fromString("44e52ae5-79b7-406d-96a8-9ec8545c4dad"))
          .biography("Michael John Myers, OC (born May 25, 1963) is a Canadian actor, comedian, and filmmaker. His accolades include seven MTV Movie & TV Awards, a Primetime Emmy Award, and a Screen Actors Guild Award. In 2002, he was awarded the Hollywood Walk of Fame star. In 2017, he was named an Officer of the Order of Canada for \"his extensive and acclaimed body of comedic work as an actor, writer, and producer.\"\n\nFollowing a series of appearances on several Canadian television programs, Mike Myers attained recognition during his six seasons as a cast member on the NBC sketch comedy series Saturday Night Live from 1989 to 1995, which won him the Primetime Emmy Award for Outstanding Writing for a Variety Series. He subsequently earned praise and numerous accolades for playing the title roles in the Wayne's World (1992–1993), Austin Powers (1997–2002), and Shrek (2001–present) franchises, the latter of which is the second highest-grossing animated film franchise. Myers also played the titular character in the 2003 live-action adaptation of the Dr. Seussbook The Cat in the Hat.\n\nMyers acted sporadically in the 2010s, having supporting roles in Terminal and Bohemian Rhapsody (both 2018). He made his directorial debut with the documentary Supermensch: The Legend of Shep Gordon (2013), which premiered at the Toronto International Film Festival. He created and starred in the 2022 Netflix original series, The Pentaverate, and appeared in David O. Russell's comedy thriller Amsterdam.\n\nDescription above from the Wikipedia article Mike Myers, licensed under CC-BY-SA, full list of contributors on Wikipedia.")        
          .build(),
        SavePerson.builder()
          .tmdbId(13918)
          .name("Elizabeth Hurley")
          .birthDate(LocalDate.parse("1965-06-10"))
          .gender(Gender.FEMALE)
          .birthPlace("Basingstoke, Hampshire, England, UK")
          .profile(UUID.fromString("10a76f68-a40f-47ad-97a2-17b05936ca28"))
          .biography("Elizabeth Jane Hurley (born June 10, 1965) is an English model and actress. She became known as a girlfriend of Hugh Grant in the 1990s after accompanying him to the Los Angeles premiere of Four Weddings and a Funeral in a plunging black Versace dress held together with gold safety pins, which gained her instant media notice.\n\nThe highlight of Hurley's professional life has been her association with the cosmetics company Estée Lauder. The company gave Hurley her first modeling job at the age of 29 and has used her as a model for its products, especially perfumes such as Sensuous, Intuition, and Pleasures, since 1995. Her best known cinematic work was as Vanessa Kensington in Mike Myers' hit spy comedies, Austin Powers: International Man of Mystery (1997) and Austin Powers: The Spy Who Shagged Me (1999). Hurley currently models and designs an eponymous beachwear line.\n\nHurley was born as a middle child in Basingstoke, Hampshire, England, the daughter of Angela and Roy Hurley. Her Irish father was a Major in the British Army, while her Anglican mother was a teacher at Kempshott Infant School. She has an older sister, Kate, and a younger brother Michael James Hurley.\n\nHurley was a struggling actress in 1987, when she met Hugh Grant while working on a Spanish production called Remando Al Viento. While Hurley was his girlfriend, Grant was embroiled in an international scandal for soliciting the services of a female prostitute in 1995. Hurley stood by him and accompanied Grant to the premiere of his movie Nine Months. After 13 years together, Hurley and Grant announced an \"amicable\" split in May 2000. Hurley lived in a London home owned by Grant after the break-up. She invited Grant to her wedding in 2007, but he chose not to attend.\n\nOn 4 April 2002, Hurley gave birth to a son, Damian Charles Hurley. The baby's father, Steve Bing, denied paternity by alleging that he and Hurley had a brief, non-exclusive relationship in 2001. A DNA test, however, established Bing as the child's father. In late 2002, Hurley started dating Indian textile heir Arun Nayar, who runs a small software company since 1998. On 2 March 2007, Hurley and Nayar married at Sudeley Castle and then had a second wedding at Umaid Bhawan Palace in Jodhpur, India.")        
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
        .backdrop(BACKDROP)
        .poster(POSTER)
        .tagline("The grooviest movie of the summer has a secret, baby!")
        .overview("The world's most shagadelic spy continues his fight against Dr. Evil. This time, the diabolical doctor and his clone, Mini-Me, team up with a new foe—'70s kingpin Goldmember. While pursuing the team of villains to stop them from world domination, Austin gets help from his dad and an old girlfriend.")
        .people(people)
        .build();
    var request = HttpRequest.newBuilder().uri(UriBuilder.fromUri(MOVIES_URL).build())
        .POST(HttpRequest.BodyPublishers.ofString(OBJECT_MAPPER.writeValueAsString(command))).build();
    long startTime = System.nanoTime();
    var response = HTTP_CLIENT.send(request, BodyHandlers.ofString());
    long et = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
    var movie = OBJECT_MAPPER.readValue(response.body(), MovieDto.class);
    movieId = movie.id();
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
    LOGGER.infof("Saved movie %d in %d ms", movieId, et);
  }
  
  @Disabled
  @Test
  @Order(2)
  void testFindById() throws IOException, InterruptedException {
    var request = HttpRequest.newBuilder()
        .uri(UriBuilder.fromUri(MOVIES_URL).path(movieId.toString()).build())
        .build();
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
    LOGGER.infof("Found movie %d details in: %d ms", movieId, et);    
  }

}
