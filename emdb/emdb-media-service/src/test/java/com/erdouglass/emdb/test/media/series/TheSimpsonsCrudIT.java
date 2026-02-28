package com.erdouglass.emdb.test.media.series;

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
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestMethodOrder;

import com.erdouglass.emdb.common.Gender;
import com.erdouglass.emdb.common.SeriesType;
import com.erdouglass.emdb.common.ShowStatus;
import com.erdouglass.emdb.common.comand.SavePerson;
import com.erdouglass.emdb.common.comand.SaveSeries;
import com.erdouglass.emdb.common.comand.SaveSeries.Credits;
import com.erdouglass.emdb.common.comand.SaveSeriesCastCredit;
import com.erdouglass.emdb.common.comand.SaveSeriesCrewCredit;
import com.erdouglass.emdb.common.comand.UpdateSeries;
import com.erdouglass.emdb.common.query.SeriesDto;
import com.erdouglass.emdb.test.media.AbstractTest;

@TestInstance(Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TheSimpsonsCrudIT extends AbstractTest {
  private static final Logger LOGGER = Logger.getLogger(TheSimpsonsCrudIT.class);
  private static final UUID BACKDROP = UUID.fromString("fca95726-f1f9-492b-b0c4-bc126792fd48");
  private static final UUID POSTER = UUID.fromString("72a875ef-2764-4128-a3a9-54dbdf210035");
  
  private Long seriesId;  

  @Test
  @Order(1)
  void testSaveSeries() throws IOException, InterruptedException {
    var credits = new Credits(List.of(
        SaveSeriesCastCredit.builder()
          .person(SavePerson.builder()
              .tmdbId(198)
              .name("Dan Castellaneta")
              .birthDate(LocalDate.parse("1957-10-29"))
              .gender(Gender.MALE)
              .birthPlace("Chicago, Illinois, USA")
              .profile(UUID.fromString("44e52ae5-79b7-406d-96a8-9ec8545c4dad"))
              .biography("Dan Castellaneta (born October 29, 1957) is an American film, theatre and television actor, comedian, voice artist, singer and television writer. Noted for his long-running role as Homer Simpson on The Simpsons, he voices many other regular characters on it.")
              .build())
          .role("Homer Simpson / Abe Simpson / Barney Gumble / Krusty (voice)", 786)
          .order(0)
          .build(),
        SaveSeriesCastCredit.builder()
          .person(SavePerson.builder()
              .tmdbId(6009)
              .name("Pamela Hayden")
              .birthDate(LocalDate.parse("1953-11-28"))
              .gender(Gender.FEMALE)
              .birthPlace("Windham, Maine, USA")
              .profile(UUID.fromString("abe52ae5-79b7-406d-96a8-9ec8545c4dad"))
              .biography("From Wikipedia, the free encyclopedia.\\n\\nPamela Hayden (born November 27, 1953) is an American actress, best known for providing various voices for the animated television show The Simpsons. She is also a stand up comedian.\\n\\nDescription above from the Wikipedia article Pamela Hayden, licensed under CC-BY-SA, full list of contributors on Wikipedia")
              .build())
          .role("Milhouse Van Houten (voice)", 80)
          .role("Milhouse Van Houten / Jimbo Jones (voice)", 39)
          .role("Milhouse (voice)", 59)
          .order(1771)
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
        .tmdbId(456)
        .title("The Simpsons")
        .score(8.015f)
        .status(ShowStatus.RETURNING_SERIES)
        .type(SeriesType.SCRIPTED)
        .homepage("http://www.thesimpsons.com/")
        .originalLanguage("en")
        .backdrop(BACKDROP)
        .poster(POSTER)
        .overview("Set in Springfield, the average American town, the show focuses on the antics and everyday adventures of the Simpson family; Homer, Marge, Bart, Lisa and Maggie, as well as a virtual cast of thousands. Since the beginning, the series has been a pop culture icon, attracting hundreds of celebrities to guest star. The show has also made name for itself in its fearless satirical take on politics, media and American life in general.")
        .credits(credits)
        .build();
    var request = HttpRequest.newBuilder().uri(UriBuilder.fromUri(SERIES_URL).build())
        .POST(HttpRequest.BodyPublishers.ofString(OBJECT_MAPPER.writeValueAsString(command))).build();
    long startTime = System.nanoTime();
    var response = HTTP_CLIENT.send(request, BodyHandlers.ofString());
    long et = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
    var series = OBJECT_MAPPER.readValue(response.body(), SeriesDto.class);
    seriesId = series.id();
    assertEquals(200, response.statusCode());
    assertEquals(456, series.tmdbId());
    assertEquals("The Simpsons", series.title());
    assertEquals(8.015f, series.score());
    assertEquals(ShowStatus.RETURNING_SERIES, series.status());
    assertEquals(SeriesType.SCRIPTED, series.type());
    assertEquals("http://www.thesimpsons.com/", series.homepage());
    assertEquals("fca95726-f1f9-492b-b0c4-bc126792fd48.jpg", series.backdrop());
    assertEquals("72a875ef-2764-4128-a3a9-54dbdf210035.jpg", series.poster());
    assertEquals("Set in Springfield, the average American town, the show focuses on the antics and everyday adventures of the Simpson family; Homer, Marge, Bart, Lisa and Maggie, as well as a virtual cast of thousands. Since the beginning, the series has been a pop culture icon, attracting hundreds of celebrities to guest star. The show has also made name for itself in its fearless satirical take on politics, media and American life in general.", series.overview());    
    LOGGER.infof("Saved series %d in %d ms", seriesId, et);
  }
  
  @Test
  @Order(2)
  void testFindSeries() throws IOException, InterruptedException {
    var request = HttpRequest.newBuilder()
        .uri(UriBuilder.fromUri(SERIES_URL).path(seriesId.toString()).build())
        .build();
    long startTime = System.nanoTime();
    var response = HTTP_CLIENT.send(request, BodyHandlers.ofString());
    long et = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
    var series = OBJECT_MAPPER.readValue(response.body(), SeriesDto.class);
    assertEquals(200, response.statusCode());
    assertEquals(456, series.tmdbId());
    assertEquals("The Simpsons", series.title());
    assertEquals(8.015f, series.score());
    assertEquals(ShowStatus.RETURNING_SERIES, series.status());
    assertEquals(SeriesType.SCRIPTED, series.type());
    assertEquals("http://www.thesimpsons.com/", series.homepage());
    assertEquals("fca95726-f1f9-492b-b0c4-bc126792fd48.jpg", series.backdrop());
    assertEquals("72a875ef-2764-4128-a3a9-54dbdf210035.jpg", series.poster());
    assertEquals("Set in Springfield, the average American town, the show focuses on the antics and everyday adventures of the Simpson family; Homer, Marge, Bart, Lisa and Maggie, as well as a virtual cast of thousands. Since the beginning, the series has been a pop culture icon, attracting hundreds of celebrities to guest star. The show has also made name for itself in its fearless satirical take on politics, media and American life in general.", series.overview());    
    LOGGER.infof("Found series %d details in: %d ms", seriesId, et);
  }
  
  @Test
  @Order(3)
  void testFindAllDetails() throws IOException, InterruptedException {
    var request = HttpRequest.newBuilder()
        .uri(UriBuilder.fromUri(SERIES_URL).path(seriesId.toString()).queryParam("append", "credits").build())
        .build();
    long startTime = System.nanoTime();
    var response = HTTP_CLIENT.send(request, BodyHandlers.ofString());
    long et = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
    var series = OBJECT_MAPPER.readValue(response.body(), SeriesDto.class);
    assertEquals(200, response.statusCode());
    assertEquals(456, series.tmdbId());
    assertEquals("The Simpsons", series.title());
    assertEquals(8.015f, series.score());
    assertEquals(ShowStatus.RETURNING_SERIES, series.status());
    assertEquals(SeriesType.SCRIPTED, series.type());
    assertEquals("http://www.thesimpsons.com/", series.homepage());
    assertEquals("fca95726-f1f9-492b-b0c4-bc126792fd48.jpg", series.backdrop());
    assertEquals("72a875ef-2764-4128-a3a9-54dbdf210035.jpg", series.poster());
    assertEquals("Set in Springfield, the average American town, the show focuses on the antics and everyday adventures of the Simpson family; Homer, Marge, Bart, Lisa and Maggie, as well as a virtual cast of thousands. Since the beginning, the series has been a pop culture icon, attracting hundreds of celebrities to guest star. The show has also made name for itself in its fearless satirical take on politics, media and American life in general.", series.overview());    
    var cast = series.cast();
    assertEquals(2, cast.size());
    assertEquals("Homer Simpson / Abe Simpson / Barney Gumble / Krusty (voice)", cast.get(0).roles().get(0).character());
    assertEquals("Milhouse Van Houten (voice)", cast.get(1).roles().get(0).character());
    assertEquals("Milhouse (voice)", cast.get(1).roles().get(1).character());
    assertEquals("Milhouse Van Houten / Jimbo Jones (voice)", cast.get(1).roles().get(2).character());  
    var crew = series.crew();
    assertEquals(1, crew.size());
    assertEquals("Executive Producer", crew.get(0).jobs().get(0).title());
    assertEquals("Writer", crew.get(0).jobs().get(1).title());
    assertEquals("Creator", crew.get(0).jobs().get(2).title());
    LOGGER.infof("Found all series %d details in: %d ms", seriesId, et);
  }
  
  @Test
  @Order(4)
  void testUpdateSeries() throws IOException, InterruptedException {
    var command = UpdateSeries.builder()
        .title("The Simpsons")
        .score(6.5f)
        .status(ShowStatus.RETURNING_SERIES)
        .type(SeriesType.SCRIPTED)
        .homepage("http://www.thesimpsons.com/")
        .originalLanguage("en")
        .backdrop(BACKDROP)
        .poster(POSTER)
        .overview("Set in Springfield, the average American town, the show focuses on the antics and everyday adventures of the Simpson family; Homer, Marge, Bart, Lisa and Maggie, as well as a virtual cast of thousands. Since the beginning, the series has been a pop culture icon, attracting hundreds of celebrities to guest star. The show has also made name for itself in its fearless satirical take on politics, media and American life in general.")
        .build();
    var request = HttpRequest.newBuilder().uri(UriBuilder.fromUri(SERIES_URL).path(seriesId.toString()).build())
        .PUT(HttpRequest.BodyPublishers.ofString(OBJECT_MAPPER.writeValueAsString(command))).build();
    long startTime = System.nanoTime();
    var response = HTTP_CLIENT.send(request, BodyHandlers.ofString());
    long et = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
    var series = OBJECT_MAPPER.readValue(response.body(), SeriesDto.class);
    seriesId = series.id();
    assertEquals(200, response.statusCode());
    assertEquals(456, series.tmdbId());
    assertEquals("The Simpsons", series.title());
    assertEquals(6.5f, series.score());
    assertEquals(ShowStatus.RETURNING_SERIES, series.status());
    assertEquals(SeriesType.SCRIPTED, series.type());
    assertEquals("http://www.thesimpsons.com/", series.homepage());
    assertEquals("fca95726-f1f9-492b-b0c4-bc126792fd48.jpg", series.backdrop());
    assertEquals("72a875ef-2764-4128-a3a9-54dbdf210035.jpg", series.poster());
    assertEquals("Set in Springfield, the average American town, the show focuses on the antics and everyday adventures of the Simpson family; Homer, Marge, Bart, Lisa and Maggie, as well as a virtual cast of thousands. Since the beginning, the series has been a pop culture icon, attracting hundreds of celebrities to guest star. The show has also made name for itself in its fearless satirical take on politics, media and American life in general.", series.overview());    
    LOGGER.infof("Updated series %d in %d ms", seriesId, et);
  }
  
  @Test
  @Order(5)
  void testDeleteSeries() throws IOException, InterruptedException {
    var request = HttpRequest.newBuilder()
        .uri(UriBuilder.fromUri(SERIES_URL).path(seriesId.toString()).build())
        .DELETE()
        .build();
    long startTime = System.nanoTime();
    var response = HTTP_CLIENT.send(request, BodyHandlers.ofString());
    long et = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
    assertEquals(204, response.statusCode());
    LOGGER.infof("Deleted series %d in: %d ms", seriesId, et);    
  }

}
