package com.erdouglass.emdb.test.gateway.series;

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
import com.erdouglass.emdb.common.SeriesType;
import com.erdouglass.emdb.common.ShowStatus;
import com.erdouglass.emdb.common.comand.Image;
import com.erdouglass.emdb.common.comand.SavePerson;
import com.erdouglass.emdb.common.comand.SaveSeries;
import com.erdouglass.emdb.common.comand.SaveSeries.CastCredit;
import com.erdouglass.emdb.common.comand.SaveSeries.CastCredit.Role;
import com.erdouglass.emdb.common.comand.SaveSeries.Credits;
import com.erdouglass.emdb.common.comand.SaveSeries.CrewCredit;
import com.erdouglass.emdb.common.comand.SaveSeries.CrewCredit.Job;
import com.erdouglass.emdb.common.query.SeriesDto;
import com.erdouglass.emdb.test.gateway.AbstractTest;

@TestInstance(Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TheSimpsonsSaveIT extends AbstractTest {
  private static final Logger LOGGER = Logger.getLogger(TheSimpsonsSaveIT.class);
  private static final UUID BACKDROP = UUID.fromString("019d3220-adb3-75ba-b1b2-1619de2a2fef");
  private static final UUID POSTER = UUID.fromString("019d3220-aead-702b-a997-5700e9a2076a");
  
  private String token;
  
  @BeforeAll
  void setupSecurity() throws IOException, InterruptedException {
    this.token = getAccessToken();
  }
  
  @Test
  @Order(1)
  void testSaveSeriesCommand() throws IOException, InterruptedException {
    var credits = new Credits(
        List.of(
            new CastCredit(198, List.of(
                new Role("Homer Simpson / Abe Simpson / Barney Gumble / Krusty (voice)", 786)), 0),
            new CastCredit(6009, List.of(
                new Role("Milhouse Van Houten (voice)", 80),
                new Role("Milhouse Van Houten / Jimbo Jones (voice)", 39),
                new Role("Milhouse (voice)", 59)), 1771)),
        List.of(
            new CrewCredit(5741, List.of(
                new Job("Creator", 0), 
                new Job("Writer", 15), 
                new Job("Executive Producer", 411)))));
    
    var people = List.of(
        SavePerson.builder()
          .tmdbId(198)
          .name("Dan Castellaneta")
          .birthDate(LocalDate.parse("1957-10-29"))
          .gender(Gender.MALE)
          .birthPlace("Chicago, Illinois, USA")
          .profile(Image.of(UUID.fromString("019d322b-a183-72ac-8fd3-b141badd8067"), "/zp5gxcPnxv6FsDh3l7yRZurlBRr.jpg"))
          .biography("Dan Castellaneta (born October 29, 1957) is an American film, theatre and television actor, comedian, voice artist, singer and television writer. Noted for his long-running role as Homer Simpson on The Simpsons, he voices many other regular characters on it.")
          .build(),
        SavePerson.builder()
          .tmdbId(6009)
          .name("Pamela Hayden")
          .birthDate(LocalDate.parse("1953-11-28"))
          .gender(Gender.FEMALE)
          .birthPlace("Windham, Maine, USA")
          .profile(Image.of(UUID.fromString("019d322b-a268-74fd-a17f-4405c0fe2070"), "/mZBy39mFACFHMCqfbsN4Kc2qb2k.jpg"))
          .biography("From Wikipedia, the free encyclopedia.\\n\\nPamela Hayden (born November 27, 1953) is an American actress, best known for providing various voices for the animated television show The Simpsons. She is also a stand up comedian.\\n\\nDescription above from the Wikipedia article Pamela Hayden, licensed under CC-BY-SA, full list of contributors on Wikipedia")          
          .build(),
        SavePerson.builder()
          .tmdbId(5741)
          .name("Matt Groening")
          .birthDate(LocalDate.parse("1954-02-15"))
          .gender(Gender.MALE)
          .birthPlace("Portland, Oregon, USA")
          .profile(Image.of(UUID.fromString("019d322b-a268-74fd-a17f-4405c0fe2071"), "/p7VptoDCwgbdujzEzrFJrHH4pYA.jpg"))
          .biography("Matthew Abram Groening (/ˈɡreɪnɪŋ/ GRAY-ning; born February 15, 1954) is an American cartoonist, writer, producer, and animator. He is best known as the creator of the television series The Simpsons (1989–present), Futurama (1999–2003, 2008–2013, 2023–present), and Disenchantment (2018–2023), as well as the comic strip Life in Hell (1977–2012). The Simpsons is the longest-running American primetime television series in history, as well as the longest-running American animated series and sitcom.\\n\\nBorn in Portland, Oregon, Groening made his first professional cartoon sale, of Life in Hell, to the avant-garde magazine Wet in 1978. At its peak, it was carried in 250 weekly newspapers and caught the attention of American producer James L. Brooks, who contacted Groening in 1985 about adapting it for animated sequences for the Fox 21st Century variety show The Tracey Ullman Show. Fearing the loss of ownership rights, Groening created a new set of characters, the Simpson family. The shorts were spun off into their own series, The Simpsons, which has since aired 791 episodes.\\n\\nIn 1997, Groening and former Simpsons writer David X. Cohen developed Futurama, an animated series about life in the year 3000, which premiered in 1999. It ran for four years on Fox, was picked up in 2008 by Comedy Central for another five years, and was finally picked up by Hulu for another revival in 2023. In 2016, Groening developed a new series for Netflix, Disenchantment, which premiered in August 2018.\\n\\nGroening has won 14 Primetime Emmy Awards, 12 for The Simpsons and 2 for Futurama, and a British Comedy Award for \\\"outstanding contribution to comedy\\\" in 2004. In 2002, he won the National Cartoonist Society Reuben Award for his work on Life in Hell. He received a star on the Hollywood Walk of Fame on February 14, 2012.\\n\\nDescription above from the Wikipedia article Matt Groening, licensed under CC-BY-SA, full list of contributors on Wikipedia.")         
          .build());
    
    var command = SaveSeries.builder()
        .tmdbId(456)
        .title("The Simpsons")
        .score(8.015f)
        .status(ShowStatus.RETURNING_SERIES)
        .type(SeriesType.SCRIPTED)
        .homepage("http://www.thesimpsons.com/")
        .originalLanguage("en")
        .backdrop(Image.of(BACKDROP, "/mPMtuVB6AEulRhlfn69y5RvgmNT.jpg"))
        .poster(Image.of(POSTER, "/zp5gxcPnxv6FsDh3l7yRZurlBRr.jpg"))
        .overview("Set in Springfield, the average American town, the show focuses on the antics and everyday adventures of the Simpson family; Homer, Marge, Bart, Lisa and Maggie, as well as a virtual cast of thousands. Since the beginning, the series has been a pop culture icon, attracting hundreds of celebrities to guest star. The show has also made name for itself in its fearless satirical take on politics, media and American life in general.")
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
  
  /// This should rename the role for Dan Castellaneta and delete the credit for Pamela Hayden.
  @Test
  @Order(2)
  void testSaveCredits() throws IOException, InterruptedException {
    var credits = new Credits(
        List.of(
            new CastCredit(198, List.of(
                new Role("Test Character", 786)), 0)),
        List.of(
            new CrewCredit(5741, List.of(
                new Job("Creator", 0), 
                new Job("Writer", 15), 
                new Job("Executive Producer", 411)))));
    
    var people = List.of(
        SavePerson.builder()
          .tmdbId(198)
          .name("Dan Castellaneta")
          .birthDate(LocalDate.parse("1957-10-29"))
          .gender(Gender.MALE)
          .birthPlace("Chicago, Illinois, USA")
          .profile(Image.of(UUID.fromString("019d322b-a183-72ac-8fd3-b141badd8067"), "/zp5gxcPnxv6FsDh3l7yRZurlBRr.jpg"))
          .biography("Dan Castellaneta (born October 29, 1957) is an American film, theatre and television actor, comedian, voice artist, singer and television writer. Noted for his long-running role as Homer Simpson on The Simpsons, he voices many other regular characters on it.")
          .build(),
        SavePerson.builder()
          .tmdbId(6009)
          .name("Pamela Hayden")
          .birthDate(LocalDate.parse("1953-11-28"))
          .gender(Gender.FEMALE)
          .birthPlace("Windham, Maine, USA")
          .profile(Image.of(UUID.fromString("019d322b-a268-74fd-a17f-4405c0fe2070"), "/mZBy39mFACFHMCqfbsN4Kc2qb2k.jpg"))
          .biography("From Wikipedia, the free encyclopedia.\\n\\nPamela Hayden (born November 27, 1953) is an American actress, best known for providing various voices for the animated television show The Simpsons. She is also a stand up comedian.\\n\\nDescription above from the Wikipedia article Pamela Hayden, licensed under CC-BY-SA, full list of contributors on Wikipedia")          
          .build(),          
        SavePerson.builder()
          .tmdbId(5741)
          .name("Matt Groening")
          .birthDate(LocalDate.parse("1954-02-15"))
          .gender(Gender.MALE)
          .birthPlace("Portland, Oregon, USA")
          .profile(Image.of(UUID.fromString("019d322b-a268-74fd-a17f-4405c0fe2071"), "/p7VptoDCwgbdujzEzrFJrHH4pYA.jpg"))
          .biography("Matthew Abram Groening (/ˈɡreɪnɪŋ/ GRAY-ning; born February 15, 1954) is an American cartoonist, writer, producer, and animator. He is best known as the creator of the television series The Simpsons (1989–present), Futurama (1999–2003, 2008–2013, 2023–present), and Disenchantment (2018–2023), as well as the comic strip Life in Hell (1977–2012). The Simpsons is the longest-running American primetime television series in history, as well as the longest-running American animated series and sitcom.\\n\\nBorn in Portland, Oregon, Groening made his first professional cartoon sale, of Life in Hell, to the avant-garde magazine Wet in 1978. At its peak, it was carried in 250 weekly newspapers and caught the attention of American producer James L. Brooks, who contacted Groening in 1985 about adapting it for animated sequences for the Fox 21st Century variety show The Tracey Ullman Show. Fearing the loss of ownership rights, Groening created a new set of characters, the Simpson family. The shorts were spun off into their own series, The Simpsons, which has since aired 791 episodes.\\n\\nIn 1997, Groening and former Simpsons writer David X. Cohen developed Futurama, an animated series about life in the year 3000, which premiered in 1999. It ran for four years on Fox, was picked up in 2008 by Comedy Central for another five years, and was finally picked up by Hulu for another revival in 2023. In 2016, Groening developed a new series for Netflix, Disenchantment, which premiered in August 2018.\\n\\nGroening has won 14 Primetime Emmy Awards, 12 for The Simpsons and 2 for Futurama, and a British Comedy Award for \\\"outstanding contribution to comedy\\\" in 2004. In 2002, he won the National Cartoonist Society Reuben Award for his work on Life in Hell. He received a star on the Hollywood Walk of Fame on February 14, 2012.\\n\\nDescription above from the Wikipedia article Matt Groening, licensed under CC-BY-SA, full list of contributors on Wikipedia.")         
          .build());
    
    var command = SaveSeries.builder()
        .tmdbId(456)
        .title("The Simpsons")
        .score(8.015f)
        .status(ShowStatus.RETURNING_SERIES)
        .type(SeriesType.SCRIPTED)
        .homepage("http://www.thesimpsons.com/")
        .originalLanguage("en")
        .backdrop(Image.of(BACKDROP, "/mPMtuVB6AEulRhlfn69y5RvgmNT.jpg"))
        .poster(Image.of(POSTER, "/zp5gxcPnxv6FsDh3l7yRZurlBRr.jpg"))
        .overview("Set in Springfield, the average American town, the show focuses on the antics and everyday adventures of the Simpson family; Homer, Marge, Bart, Lisa and Maggie, as well as a virtual cast of thousands. Since the beginning, the series has been a pop culture icon, attracting hundreds of celebrities to guest star. The show has also made name for itself in its fearless satirical take on politics, media and American life in general.")
        .credits(credits)
        .people(people)
        .build(); 
    var request = HttpRequest.newBuilder().uri(UriBuilder.fromUri(SERIES_URL).build())
        .header("Authorization", "Bearer " + token)
        .POST(HttpRequest.BodyPublishers.ofString(OBJECT_MAPPER.writeValueAsString(command))).build();
    long startTime = System.nanoTime();
    var response = HTTP_CLIENT.send(request, BodyHandlers.ofString());
    long et = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
    assertEquals(200, response.statusCode(), "Server failed with response: " + response.body());
    
    var series = OBJECT_MAPPER.readValue(response.body(), SeriesDto.class);
    LOGGER.infof("Saved series %d in %d ms", series.id(), et);    
  }

}
