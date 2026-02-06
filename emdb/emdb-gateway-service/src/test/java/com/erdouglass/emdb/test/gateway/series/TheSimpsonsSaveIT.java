package com.erdouglass.emdb.test.gateway.series;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import jakarta.ws.rs.core.UriBuilder;

import org.jboss.logging.Logger;
import org.junit.jupiter.api.Test;

import com.erdouglass.emdb.common.SeriesType;
import com.erdouglass.emdb.common.ShowStatus;
import com.erdouglass.emdb.common.comand.SaveSeries;
import com.erdouglass.emdb.common.query.SeriesDto;
import com.erdouglass.emdb.test.gateway.AbstractTest;

class TheSimpsonsSaveIT extends AbstractTest {
  private static final Logger LOGGER = Logger.getLogger(TheSimpsonsSaveIT.class);
  
  @Test
  void testSaveCommand() throws IOException, InterruptedException {
    var command = SaveSeries.builder()
        .tmdbId(456)
        .title("The Simpsons")
        .score(8.015f)
        .status(ShowStatus.RETURNING_SERIES)
        .type(SeriesType.SCRIPTED)
        .homepage("http://www.thesimpsons.com/")
        .originalLanguage("en")
        .backdrop(UUID.fromString("fca95726-f1f9-492b-b0c4-bc126792fd48"))
        .poster(UUID.fromString("72a875ef-2764-4128-a3a9-54dbdf210035"))
        .overview("Set in Springfield, the average American town, the show focuses on the antics and everyday adventures of the Simpson family; Homer, Marge, Bart, Lisa and Maggie, as well as a virtual cast of thousands. Since the beginning, the series has been a pop culture icon, attracting hundreds of celebrities to guest star. The show has also made name for itself in its fearless satirical take on politics, media and American life in general.")
        .build();
    var request = HttpRequest.newBuilder().uri(UriBuilder.fromUri(SERIES_URL).build())
        .POST(HttpRequest.BodyPublishers.ofString(OBJECT_MAPPER.writeValueAsString(command))).build();
    long startTime = System.nanoTime();
    var response = HTTP_CLIENT.send(request, BodyHandlers.ofString());
    long et = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
    var series = OBJECT_MAPPER.readValue(response.body(), SeriesDto.class);
    assertEquals(200, response.statusCode());
    assertEquals(456, series.tmdbId());
    assertEquals(456, series.tmdbId());
    assertEquals("The Simpsons", series.title());
    assertEquals(8.015f, series.score());
    assertEquals(ShowStatus.RETURNING_SERIES, series.status());
    assertEquals(SeriesType.SCRIPTED, series.type());
    assertEquals("http://www.thesimpsons.com/", series.homepage());
    assertEquals("fca95726-f1f9-492b-b0c4-bc126792fd48.jpg", series.backdrop());
    assertEquals("72a875ef-2764-4128-a3a9-54dbdf210035.jpg", series.poster());
    assertEquals("Set in Springfield, the average American town, the show focuses on the antics and everyday adventures of the Simpson family; Homer, Marge, Bart, Lisa and Maggie, as well as a virtual cast of thousands. Since the beginning, the series has been a pop culture icon, attracting hundreds of celebrities to guest star. The show has also made name for itself in its fearless satirical take on politics, media and American life in general.", series.overview());    
    LOGGER.infof("Saved series %d in %d ms", series.id(), et);    
  }

}
