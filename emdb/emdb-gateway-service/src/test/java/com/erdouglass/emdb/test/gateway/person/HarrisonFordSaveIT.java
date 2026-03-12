package com.erdouglass.emdb.test.gateway.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.IOException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.LocalDate;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import jakarta.ws.rs.core.UriBuilder;

import org.jboss.logging.Logger;
import org.junit.jupiter.api.Test;

import com.erdouglass.emdb.common.Gender;
import com.erdouglass.emdb.common.comand.SavePerson;
import com.erdouglass.emdb.common.query.PersonDto;
import com.erdouglass.emdb.test.gateway.AbstractTest;

class HarrisonFordSaveIT extends AbstractTest {
  private static final Logger LOGGER = Logger.getLogger(HarrisonFordSaveIT.class);
  private static final UUID PROFILE = UUID.fromString("f4435126-75ca-4e44-9ceb-b414662b7164");  
    
  @Test
  void testSavePersonCommand() throws IOException, InterruptedException {
    var command = SavePerson.builder()
        .tmdbId(3)
        .name("Harrison Ford")
        .birthDate(LocalDate.parse("1942-07-13"))
        .birthPlace("Chicago, Illinois, USA")
        .gender(Gender.MALE)
        .profile(PROFILE)
        .biography("Legendary Hollywood Icon Harrison Ford was born on July 13, 1942 in Chicago, Illinois. His family history includes a strong lineage of actors, radio personalities, and models. Ford attended public high school in Park Ridge, Illinois where he was a member of the school Radio Station WMTH. Ford worked as the lead voice for sports reporting at WMTH for several years. Acting wasn't a major interest to Ford until his junior year at Ripon College when he first took an acting class. Ford's career started in 1964 when he travelled to California in search of a voice-over job. He never received that position, but instead signed a contract with Columbia Pictures where he earned $150 weekly to play small fill in roles in various films.\n\nThrough the '60s Ford worked on several TV shows including Gunsmoke, Ironside, Kung Fu, and American Style. It wasn't until 1967 that he received his first credited role in the Western film, A Time for Killing. Dissatisfied with the meager roles he was being offered, Ford took a hiatus from acting to work as a self-employed carpenter. This seemingly odd diversion turned out to be a blessing in disguise for Harrison's acting career when he was soon hired by famous film producer George Lucas. This was a turning point in Ford's life that led to him be casted in milestone roles such as Han Solo and Indiana Jones.\n\nSince his most famous roles in the original Star Wars trilogy and Raiders of the Lost Ark, Ford has appeared in over 40 films. Many criticize his late-career work, saying his performances have been lackluster, leading to commercially disappointing films. Ford has always worked hard to protect his off-screen private life, keeping details about his children and marriages quiet. He has a total of five children including one recent adoption with third and current wife Calista Flockhart. In addition to acting, Ford is passionate about environmental conservation, aviation, and archeology.")        
        .build();
    var request = HttpRequest.newBuilder()
        .uri(UriBuilder.fromUri(PEOPLE_URL).build())
        .POST(HttpRequest.BodyPublishers.ofString(OBJECT_MAPPER.writeValueAsString(command)))
        .build();
    long startTime = System.nanoTime();
    var response = HTTP_CLIENT.send(request, BodyHandlers.ofString());
    long et = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
    assertEquals(200, response.statusCode(), "Server failed with response: " + response.body());
    
    var person = OBJECT_MAPPER.readValue(response.body(), PersonDto.class);
    assertEquals(3, person.tmdbId());
    assertEquals("Harrison Ford", person.name());
    assertEquals("1942-07-13", person.birthDate().toString());
    assertNull(person.deathDate());
    assertEquals(Gender.MALE, person.gender());
    assertEquals("Chicago, Illinois, USA", person.birthPlace());
    assertEquals("f4435126-75ca-4e44-9ceb-b414662b7164.jpg", person.profile());
    assertEquals("Legendary Hollywood Icon Harrison Ford was born on July 13, 1942 in Chicago, Illinois. His family history includes a strong lineage of actors, radio personalities, and models. Ford attended public high school in Park Ridge, Illinois where he was a member of the school Radio Station WMTH. Ford worked as the lead voice for sports reporting at WMTH for several years. Acting wasn't a major interest to Ford until his junior year at Ripon College when he first took an acting class. Ford's career started in 1964 when he travelled to California in search of a voice-over job. He never received that position, but instead signed a contract with Columbia Pictures where he earned $150 weekly to play small fill in roles in various films.\n\nThrough the '60s Ford worked on several TV shows including Gunsmoke, Ironside, Kung Fu, and American Style. It wasn't until 1967 that he received his first credited role in the Western film, A Time for Killing. Dissatisfied with the meager roles he was being offered, Ford took a hiatus from acting to work as a self-employed carpenter. This seemingly odd diversion turned out to be a blessing in disguise for Harrison's acting career when he was soon hired by famous film producer George Lucas. This was a turning point in Ford's life that led to him be casted in milestone roles such as Han Solo and Indiana Jones.\n\nSince his most famous roles in the original Star Wars trilogy and Raiders of the Lost Ark, Ford has appeared in over 40 films. Many criticize his late-career work, saying his performances have been lackluster, leading to commercially disappointing films. Ford has always worked hard to protect his off-screen private life, keeping details about his children and marriages quiet. He has a total of five children including one recent adoption with third and current wife Calista Flockhart. In addition to acting, Ford is passionate about environmental conservation, aviation, and archeology.", person.biography());
    LOGGER.infof("Saved person %d in %d ms", person.id(), et);    
  }

}
