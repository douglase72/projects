package com.erdouglass.emdb.test.gateway.person;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

import jakarta.ws.rs.core.UriBuilder;

import org.jboss.logging.Logger;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestMethodOrder;

import com.erdouglass.emdb.gateway.query.MultiResponse;
import com.erdouglass.emdb.media.api.Gender;
import com.erdouglass.emdb.media.api.Image;
import com.erdouglass.emdb.media.api.command.SavePerson;
import com.erdouglass.emdb.test.gateway.AbstractTest;
import com.fasterxml.jackson.core.type.TypeReference;

@TestInstance(Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PersonSaveIT extends AbstractTest {
  private static final Logger LOGGER = Logger.getLogger(PersonSaveIT.class);
  
  private String token;
  
  @BeforeAll
  void setupSecurity() throws IOException, InterruptedException {
    this.token = getAccessToken();
  }
  
  @Test
  @Order(1)
  void testSavePeople() throws IOException, InterruptedException {
    var commands = List.of(
        SavePerson.builder()
          .tmdbId(148815)
          .name("Harrison Ford")
          .birthDate(LocalDate.parse("1884-03-16"))
          .deathDate(LocalDate.parse("1957-12-02"))
          .gender(Gender.MALE)
          .birthPlace("Kansas City, Missouri, USA")
          .profile(Image.of(UUID.fromString("019d322b-a183-72ac-8fd3-b141badd8067"), "/zp5gxcPnxv6FsDh3l7yRZurlBRr.jpg"))
          .biography("Harrison Edward Ford (March 16, 1884 – December 2, 1957) was an American stage and film actor. He was a leading Broadway theatre performer and a star of the silent film era.")        
          .build(),
        SavePerson.builder()
          .tmdbId(13918)
          .name("Elizabeth Hurley")
          .birthDate(LocalDate.parse("1965-06-10"))
          .gender(Gender.FEMALE)
          .birthPlace("Basingstoke, Hampshire, England, UK")
          .profile(Image.of(UUID.fromString("019d322b-a268-74fd-a17f-4405c0fe2070"), "/mZBy39mFACFHMCqfbsN4Kc2qb2k.jpg"))
          .biography("Elizabeth Jane Hurley (born June 10, 1965) is an English model and actress. She became known as a girlfriend of Hugh Grant in the 1990s after accompanying him to the Los Angeles premiere of Four Weddings and a Funeral in a plunging black Versace dress held together with gold safety pins, which gained her instant media notice.\n\nThe highlight of Hurley's professional life has been her association with the cosmetics company Estée Lauder. The company gave Hurley her first modeling job at the age of 29 and has used her as a model for its products, especially perfumes such as Sensuous, Intuition, and Pleasures, since 1995. Her best known cinematic work was as Vanessa Kensington in Mike Myers' hit spy comedies, Austin Powers: International Man of Mystery (1997) and Austin Powers: The Spy Who Shagged Me (1999). Hurley currently models and designs an eponymous beachwear line.\n\nHurley was born as a middle child in Basingstoke, Hampshire, England, the daughter of Angela and Roy Hurley. Her Irish father was a Major in the British Army, while her Anglican mother was a teacher at Kempshott Infant School. She has an older sister, Kate, and a younger brother Michael James Hurley.\n\nHurley was a struggling actress in 1987, when she met Hugh Grant while working on a Spanish production called Remando Al Viento. While Hurley was his girlfriend, Grant was embroiled in an international scandal for soliciting the services of a female prostitute in 1995. Hurley stood by him and accompanied Grant to the premiere of his movie Nine Months. After 13 years together, Hurley and Grant announced an \"amicable\" split in May 2000. Hurley lived in a London home owned by Grant after the break-up. She invited Grant to her wedding in 2007, but he chose not to attend.\n\nOn 4 April 2002, Hurley gave birth to a son, Damian Charles Hurley. The baby's father, Steve Bing, denied paternity by alleging that he and Hurley had a brief, non-exclusive relationship in 2001. A DNA test, however, established Bing as the child's father. In late 2002, Hurley started dating Indian textile heir Arun Nayar, who runs a small software company since 1998. On 2 March 2007, Hurley and Nayar married at Sudeley Castle and then had a second wedding at Umaid Bhawan Palace in Jodhpur, India.")        
          .build(),
        SavePerson.builder()
          .tmdbId(3)
          .name("Harrison Ford")
          .birthDate(LocalDate.parse("1942-07-13"))
          .birthPlace("Chicago, Illinois, USA")
          .gender(Gender.MALE)
          .profile(Image.of(UUID.fromString("019d322b-a268-74fd-a17f-4405c0fe2071"), "/p7VptoDCwgbdujzEzrFJrHH4pYA.jpg"))
          .biography("Legendary Hollywood Icon Harrison Ford was born on July 13, 1942 in Chicago, Illinois. His family history includes a strong lineage of actors, radio personalities, and models. Ford attended public high school in Park Ridge, Illinois where he was a member of the school Radio Station WMTH. Ford worked as the lead voice for sports reporting at WMTH for several years. Acting wasn't a major interest to Ford until his junior year at Ripon College when he first took an acting class. Ford's career started in 1964 when he travelled to California in search of a voice-over job. He never received that position, but instead signed a contract with Columbia Pictures where he earned $150 weekly to play small fill in roles in various films.\n\nThrough the '60s Ford worked on several TV shows including Gunsmoke, Ironside, Kung Fu, and American Style. It wasn't until 1967 that he received his first credited role in the Western film, A Time for Killing. Dissatisfied with the meager roles he was being offered, Ford took a hiatus from acting to work as a self-employed carpenter. This seemingly odd diversion turned out to be a blessing in disguise for Harrison's acting career when he was soon hired by famous film producer George Lucas. This was a turning point in Ford's life that led to him be casted in milestone roles such as Han Solo and Indiana Jones.\n\nSince his most famous roles in the original Star Wars trilogy and Raiders of the Lost Ark, Ford has appeared in over 40 films. Many criticize his late-career work, saying his performances have been lackluster, leading to commercially disappointing films. Ford has always worked hard to protect his off-screen private life, keeping details about his children and marriages quiet. He has a total of five children including one recent adoption with third and current wife Calista Flockhart. In addition to acting, Ford is passionate about environmental conservation, aviation, and archeology.")        
          .build());
    var request = HttpRequest.newBuilder()
        .uri(UriBuilder.fromUri(PEOPLE_URL).path("batch").build())
        .header("Authorization", "Bearer " + token)
        .POST(HttpRequest.BodyPublishers.ofString(OBJECT_MAPPER.writeValueAsString(commands)))
        .build(); 
    long startTime = System.nanoTime();
    var response = HTTP_CLIENT.send(request, BodyHandlers.ofString());
    long et = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
    assertEquals(207, response.statusCode());
    
    var savedPeople = OBJECT_MAPPER.readValue(response.body(), new TypeReference<List<MultiResponse>>() {})
        .stream().collect(Collectors.toMap(MultiResponse::tmdbId, Function.identity()));
    assertEquals(201, savedPeople.get(3).statusCode());
    assertEquals(201, savedPeople.get(13918).statusCode());
    assertEquals(201, savedPeople.get(148815).statusCode());
    LOGGER.infof("Saved %d people in %d ms", commands.size(), et);
  }
  
  @Test
  @Order(2)
  void testSavePeopleAgain() throws IOException, InterruptedException {
    var commands = List.of(
        SavePerson.builder()
          .tmdbId(148815)
          .name("Harrison Ford")
          .birthDate(LocalDate.parse("1884-03-16"))
          .deathDate(LocalDate.parse("1957-12-02"))
          .gender(Gender.MALE)
          .birthPlace("Kansas City, Missouri, USA")
          .profile(Image.of(UUID.fromString("019d322b-a183-72ac-8fd3-b141badd8067"), "/zp5gxcPnxv6FsDh3l7yRZurlBRr.jpg"))
          .biography("Harrison Edward Ford (March 16, 1884 – December 2, 1957) was an American stage and film actor. He was a leading Broadway theatre performer and a star of the silent film era.")        
          .build(),
        SavePerson.builder()
          .tmdbId(13918)
          .name("Elizabeth Hurley")
          .birthDate(LocalDate.parse("1965-06-10"))
          .gender(Gender.FEMALE)
          .birthPlace("Basingstoke, Hampshire, England, UK")
          .profile(Image.of(UUID.fromString("019d322b-a268-74fd-a17f-4405c0fe2070"), "/mZBy39mFACFHMCqfbsN4Kc2qb2k.jpg"))
          .biography("Elizabeth Jane Hurley (born June 10, 1965) is an English model and actress. She became known as a girlfriend of Hugh Grant in the 1990s after accompanying him to the Los Angeles premiere of Four Weddings and a Funeral in a plunging black Versace dress held together with gold safety pins, which gained her instant media notice.\n\nThe highlight of Hurley's professional life has been her association with the cosmetics company Estée Lauder. The company gave Hurley her first modeling job at the age of 29 and has used her as a model for its products, especially perfumes such as Sensuous, Intuition, and Pleasures, since 1995. Her best known cinematic work was as Vanessa Kensington in Mike Myers' hit spy comedies, Austin Powers: International Man of Mystery (1997) and Austin Powers: The Spy Who Shagged Me (1999). Hurley currently models and designs an eponymous beachwear line.\n\nHurley was born as a middle child in Basingstoke, Hampshire, England, the daughter of Angela and Roy Hurley. Her Irish father was a Major in the British Army, while her Anglican mother was a teacher at Kempshott Infant School. She has an older sister, Kate, and a younger brother Michael James Hurley.\n\nHurley was a struggling actress in 1987, when she met Hugh Grant while working on a Spanish production called Remando Al Viento. While Hurley was his girlfriend, Grant was embroiled in an international scandal for soliciting the services of a female prostitute in 1995. Hurley stood by him and accompanied Grant to the premiere of his movie Nine Months. After 13 years together, Hurley and Grant announced an \"amicable\" split in May 2000. Hurley lived in a London home owned by Grant after the break-up. She invited Grant to her wedding in 2007, but he chose not to attend.\n\nOn 4 April 2002, Hurley gave birth to a son, Damian Charles Hurley. The baby's father, Steve Bing, denied paternity by alleging that he and Hurley had a brief, non-exclusive relationship in 2001. A DNA test, however, established Bing as the child's father. In late 2002, Hurley started dating Indian textile heir Arun Nayar, who runs a small software company since 1998. On 2 March 2007, Hurley and Nayar married at Sudeley Castle and then had a second wedding at Umaid Bhawan Palace in Jodhpur, India.")        
          .build(),
        SavePerson.builder()
          .tmdbId(3)
          .name("Heneritta J. Ford")
          .birthDate(LocalDate.parse("1942-07-13"))
          .birthPlace("Chicago, Illinois, USA")
          .gender(Gender.FEMALE)
          .profile(Image.of(UUID.fromString("019d322b-a268-74fd-a17f-4405c0fe2071"), "/p7VptoDCwgbdujzEzrFJrHH4pYA.jpg"))
          .biography("Legendary Hollywood Icon Harrison Ford was born on July 13, 1942 in Chicago, Illinois. His family history includes a strong lineage of actors, radio personalities, and models. Ford attended public high school in Park Ridge, Illinois where he was a member of the school Radio Station WMTH. Ford worked as the lead voice for sports reporting at WMTH for several years. Acting wasn't a major interest to Ford until his junior year at Ripon College when he first took an acting class. Ford's career started in 1964 when he travelled to California in search of a voice-over job. He never received that position, but instead signed a contract with Columbia Pictures where he earned $150 weekly to play small fill in roles in various films.\n\nThrough the '60s Ford worked on several TV shows including Gunsmoke, Ironside, Kung Fu, and American Style. It wasn't until 1967 that he received his first credited role in the Western film, A Time for Killing. Dissatisfied with the meager roles he was being offered, Ford took a hiatus from acting to work as a self-employed carpenter. This seemingly odd diversion turned out to be a blessing in disguise for Harrison's acting career when he was soon hired by famous film producer George Lucas. This was a turning point in Ford's life that led to him be casted in milestone roles such as Han Solo and Indiana Jones.\n\nSince his most famous roles in the original Star Wars trilogy and Raiders of the Lost Ark, Ford has appeared in over 40 films. Many criticize his late-career work, saying his performances have been lackluster, leading to commercially disappointing films. Ford has always worked hard to protect his off-screen private life, keeping details about his children and marriages quiet. He has a total of five children including one recent adoption with third and current wife Calista Flockhart. In addition to acting, Ford is passionate about environmental conservation, aviation, and archeology.")        
          .build());
    var request = HttpRequest.newBuilder()
        .uri(UriBuilder.fromUri(PEOPLE_URL).path("batch").build())
        .header("Authorization", "Bearer " + token)
        .POST(HttpRequest.BodyPublishers.ofString(OBJECT_MAPPER.writeValueAsString(commands)))
        .build(); 
    long startTime = System.nanoTime();
    var response = HTTP_CLIENT.send(request, BodyHandlers.ofString());
    long et = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
    assertEquals(207, response.statusCode());
    
    var savedPeople = OBJECT_MAPPER.readValue(response.body(), new TypeReference<List<MultiResponse>>() {})
        .stream().collect(Collectors.toMap(MultiResponse::tmdbId, Function.identity()));
    assertEquals(200, savedPeople.get(3).statusCode());
    assertEquals(204, savedPeople.get(13918).statusCode());
    assertEquals(204, savedPeople.get(148815).statusCode());
    LOGGER.infof("Saved %d people again in %d ms", commands.size(), et);
  }
}
