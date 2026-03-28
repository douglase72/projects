package com.erdouglass.emdb.test.gateway.movie;

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
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestMethodOrder;

import com.erdouglass.emdb.common.Gender;
import com.erdouglass.emdb.common.ShowStatus;
import com.erdouglass.emdb.common.comand.Image;
import com.erdouglass.emdb.common.comand.SaveMovie;
import com.erdouglass.emdb.common.comand.SaveMovie.CastCredit;
import com.erdouglass.emdb.common.comand.SaveMovie.Credits;
import com.erdouglass.emdb.common.comand.SaveMovie.CrewCredit;
import com.erdouglass.emdb.common.comand.SavePerson;
import com.erdouglass.emdb.common.query.MovieDto;
import com.erdouglass.emdb.test.gateway.AbstractTest;

@TestInstance(Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AustinPowersGoldmemberSaveIT extends AbstractTest {
  private static final Logger LOGGER = Logger.getLogger(AustinPowersGoldmemberSaveIT.class);
  private static final UUID BACKDROP = UUID.fromString("03592ec0-7ce4-4343-8fcf-8965887be2e6");
  private static final UUID POSTER = UUID.fromString("f4435126-75ca-4e44-9ceb-b414662b7164");  
  
  private Long movieId;
  private String token;
  
  @BeforeAll
  void setupSecurity() throws IOException, InterruptedException {
    this.token = getAccessToken();
  }
  
  @Test
  @Order(1)
  void testSaveMovie() throws IOException, InterruptedException {
    var credits = new Credits(
        List.of(
            new CastCredit(12073, "Austin Powers / Dr. Evil / Goldmember / Fat Bastard", 0),
            new CastCredit(13922, "Scott Evil", 2),
            new CastCredit(14386, "Foxxy Cleopatra", 1)),
        List.of(
            new CrewCredit(12073, "Screenplay")));
    
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
          .tmdbId(13922)
          .name("Seth Green")
          .birthDate(LocalDate.parse("1974-02-08"))
          .gender(Gender.MALE)
          .profile(UUID.fromString("5e013296-f98f-475c-81da-80c99f57c961"))
          .biography("Seth Benjamin Green (né Gesshel-Green; born February 8, 1974) is an American actor. His film debut came with a role in the comedy-drama film The Hotel New Hampshire (1984), and he went on to have supporting roles in comedy films throughout the 1980s, including Radio Days (1987) and Big Business (1988).\\n\\nDuring the 1990s and 2000s, Green began starring in comedy films such as Idle Hands (1999), Rat Race (2001), Without a Paddle (2004), and Be Cool (2005). He also became known for his portrayal of Scott Evil, Dr. Evil's son, in the Austin Powers film series (1997–2002). Green has also taken serious roles in films, including The Attic Expeditions (2001) and Party Monster (2003). He has provided the voice for Howard the Duck in a number of Marvel Cinematic Universe films and series, including Guardians of the Galaxy (2014), Guardians of the Galaxy Vol. 2 (2017), and Guardians of the Galaxy Vol. 3 (2023) and in the animated series What If...? (2021–present). In 2019, he wrote, directed, and starred in the comedy-drama film Changeland.\\n\\nGreen's first lead role on television was on the ABC sitcom Good & Evil in 1991, for which he won a Young Artist Award. Green later gained attention for his supporting roles as Oz, a teenage guitarist and the boyfriend of Willow Rosenberg, on the fantasy television series Buffy the Vampire Slayer (1997–2000), and as the voice of Chris Griffin on the Fox adult animated comedy series Family Guy (1999–present). He also voiced Leonardo in the Nickelodeon animated series Teenage Mutant Ninja Turtles (2014–2017) and the Joker in the Mass Effect video game series (2007–2012). Green created, directed, wrote, and produced the adult animated comedy series Robot Chicken and its spinoffs (2005–present), which have earned him three Primetime Emmy Awards and five Annie Awards.\\n\\nDescription above from the Wikipedia article Seth Green, licensed under CC-BY-SA, full list of contributors on Wikipedia.")          
          .build(),
        SavePerson.builder()
          .tmdbId(14386)
          .name("Beyoncé")
          .birthDate(LocalDate.parse("1981-09-04"))
          .gender(Gender.FEMALE)
          .profile(UUID.fromString("10a76f68-a40f-47ad-97a2-17b05936ca28"))
          .biography("Beyoncé Giselle Knowles-Carter (born September 4, 1981), often known simply as Beyoncé, is an American R&B and pop recording artist and actress.\\n\\nBorn and raised in Houston, Texas, she enrolled in various performing arts schools and was first exposed to singing and dancing competitions as a child. Knowles rose to fame in the late 1990s as the lead singer of the R&B girl group Destiny's Child, one of the world's best-selling girl groups of all time. During the hiatus of Destiny's Child, Knowles released her debut solo album Dangerously in Love (2003), which spawned the number one hits \\\"Crazy in Love\\\" and \\\"Baby Boy\\\" and became one of the most successful albums of that year, earning her a then record-tying five Grammy Awards.\\n\\nFollowing the group's disbandment in 2005, Knowles released B'Day in 2006. It debuted at number one on the Billboard charts and included the hits \\\"Déjà Vu\\\", \\\"Irreplaceable\\\" and \\\"Beautiful Liar\\\". Her third solo album I Am... Sasha Fierce, released in November 2008, included the anthemic \\\"Single Ladies (Put a Ring on It)\\\". The album and its singles earned her six Grammy Awards, breaking the record for most Grammy Awards won by a female artist in one night. Knowles is one of the most honored artists by the Grammys with 16 awards—13 as a solo artist and three as a member of Destiny's Child.\\n\\nKnowles began her acting career in 2001, appearing in the musical film Carmen: A Hip Hopera. In 2006, she starred in the lead role in the film adaptation of the 1981 Broadway musical Dreamgirls, for which she earned two Golden Globe nominations. Knowles launched her family's fashion line, House of Deréon, in 2004, and has endorsed such brands as Pepsi, Tommy Hilfiger, Armani and L'Oréal.\\n\\nIn 2010, Forbes ranked Knowles at number two on its list of the 100 Most Powerful and Influential Celebrities in the world; she was also listed as the most powerful and influential musician in the world. Time also included Knowles on its list of the \\\"100 Most Influential People in the World\\\". Knowles has attained five Hot 100 number one singles as a solo performer and four with Destiny's Child, and as a solo artist, has sold over 35 million albums and singles in the U.S.; according to Sony, her total record sales, when combined with the group, have surpassed 100 million. On December 11, 2009, Billboard listed Knowles as the most successful female artist of the 2000s decade and the top Radio Artist of the decade. In February 2010, the RIAA listed her as the top certified artist of the decade.")
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
    movieId = movie.id();
    LOGGER.infof("Saved movie %d in %d ms", movieId, et);
  }

  @Test
  @Order(2)
  void testUpdateMikeMyersCredits() throws IOException, InterruptedException {
    var credits = new Credits(
        List.of(
            new CastCredit(12073, "Test Character", 0),
            new CastCredit(13922, "Scott Evil", 2),
            new CastCredit(14386, "Foxxy Cleopatra", 1)),
        List.of(
            new CrewCredit(12073, "Screenplay")));
    
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
          .tmdbId(13922)
          .name("Seth Green")
          .birthDate(LocalDate.parse("1974-02-08"))
          .gender(Gender.MALE)
          .profile(UUID.fromString("5e013296-f98f-475c-81da-80c99f57c961"))
          .biography("Seth Benjamin Green (né Gesshel-Green; born February 8, 1974) is an American actor. His film debut came with a role in the comedy-drama film The Hotel New Hampshire (1984), and he went on to have supporting roles in comedy films throughout the 1980s, including Radio Days (1987) and Big Business (1988).\\n\\nDuring the 1990s and 2000s, Green began starring in comedy films such as Idle Hands (1999), Rat Race (2001), Without a Paddle (2004), and Be Cool (2005). He also became known for his portrayal of Scott Evil, Dr. Evil's son, in the Austin Powers film series (1997–2002). Green has also taken serious roles in films, including The Attic Expeditions (2001) and Party Monster (2003). He has provided the voice for Howard the Duck in a number of Marvel Cinematic Universe films and series, including Guardians of the Galaxy (2014), Guardians of the Galaxy Vol. 2 (2017), and Guardians of the Galaxy Vol. 3 (2023) and in the animated series What If...? (2021–present). In 2019, he wrote, directed, and starred in the comedy-drama film Changeland.\\n\\nGreen's first lead role on television was on the ABC sitcom Good & Evil in 1991, for which he won a Young Artist Award. Green later gained attention for his supporting roles as Oz, a teenage guitarist and the boyfriend of Willow Rosenberg, on the fantasy television series Buffy the Vampire Slayer (1997–2000), and as the voice of Chris Griffin on the Fox adult animated comedy series Family Guy (1999–present). He also voiced Leonardo in the Nickelodeon animated series Teenage Mutant Ninja Turtles (2014–2017) and the Joker in the Mass Effect video game series (2007–2012). Green created, directed, wrote, and produced the adult animated comedy series Robot Chicken and its spinoffs (2005–present), which have earned him three Primetime Emmy Awards and five Annie Awards.\\n\\nDescription above from the Wikipedia article Seth Green, licensed under CC-BY-SA, full list of contributors on Wikipedia.")          
          .build(),
        SavePerson.builder()
          .tmdbId(14386)
          .name("Beyoncé")
          .birthDate(LocalDate.parse("1981-09-04"))
          .gender(Gender.FEMALE)
          .profile(UUID.fromString("10a76f68-a40f-47ad-97a2-17b05936ca28"))
          .biography("Beyoncé Giselle Knowles-Carter (born September 4, 1981), often known simply as Beyoncé, is an American R&B and pop recording artist and actress.\\n\\nBorn and raised in Houston, Texas, she enrolled in various performing arts schools and was first exposed to singing and dancing competitions as a child. Knowles rose to fame in the late 1990s as the lead singer of the R&B girl group Destiny's Child, one of the world's best-selling girl groups of all time. During the hiatus of Destiny's Child, Knowles released her debut solo album Dangerously in Love (2003), which spawned the number one hits \\\"Crazy in Love\\\" and \\\"Baby Boy\\\" and became one of the most successful albums of that year, earning her a then record-tying five Grammy Awards.\\n\\nFollowing the group's disbandment in 2005, Knowles released B'Day in 2006. It debuted at number one on the Billboard charts and included the hits \\\"Déjà Vu\\\", \\\"Irreplaceable\\\" and \\\"Beautiful Liar\\\". Her third solo album I Am... Sasha Fierce, released in November 2008, included the anthemic \\\"Single Ladies (Put a Ring on It)\\\". The album and its singles earned her six Grammy Awards, breaking the record for most Grammy Awards won by a female artist in one night. Knowles is one of the most honored artists by the Grammys with 16 awards—13 as a solo artist and three as a member of Destiny's Child.\\n\\nKnowles began her acting career in 2001, appearing in the musical film Carmen: A Hip Hopera. In 2006, she starred in the lead role in the film adaptation of the 1981 Broadway musical Dreamgirls, for which she earned two Golden Globe nominations. Knowles launched her family's fashion line, House of Deréon, in 2004, and has endorsed such brands as Pepsi, Tommy Hilfiger, Armani and L'Oréal.\\n\\nIn 2010, Forbes ranked Knowles at number two on its list of the 100 Most Powerful and Influential Celebrities in the world; she was also listed as the most powerful and influential musician in the world. Time also included Knowles on its list of the \\\"100 Most Influential People in the World\\\". Knowles has attained five Hot 100 number one singles as a solo performer and four with Destiny's Child, and as a solo artist, has sold over 35 million albums and singles in the U.S.; according to Sony, her total record sales, when combined with the group, have surpassed 100 million. On December 11, 2009, Billboard listed Knowles as the most successful female artist of the 2000s decade and the top Radio Artist of the decade. In February 2010, the RIAA listed her as the top certified artist of the decade.")
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
    assertEquals(200, response.statusCode(), "Server failed with response: " + response.body());
    
    var movie = OBJECT_MAPPER.readValue(response.body(), MovieDto.class);
    movieId = movie.id();
    LOGGER.infof("Updated Mike Myers credits in %d ms", et);
  }
  
}
