package com.erdouglass.emdb.media.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.erdouglass.emdb.media.SaveMovieCommand;
import com.erdouglass.emdb.media.application.port.inbound.UpdateMovieCommand;
import com.erdouglass.emdb.media.domain.movie.Movie;
import com.erdouglass.emdb.media.domain.movie.MovieId;
import com.erdouglass.emdb.media.domain.movie.MoviePublicId;
import com.erdouglass.emdb.media.domain.movie.ReleaseDate;
import com.erdouglass.emdb.media.domain.shared.OriginalLanguage;
import com.erdouglass.emdb.media.domain.shared.SourceId;
import com.erdouglass.emdb.media.domain.shared.SourceId.Source;
import com.erdouglass.emdb.media.domain.shared.Title;
import com.erdouglass.emdb.media.domain.shared.Version;

class MovieAggregateTest {
  private final MovieMapper mapper = new MovieMapper();
  
  @Test
  void testSave() {
    Movie movie = Movie.builder()
        .id(MovieId.of(UUID.fromString("019f7610-29d0-7282-894f-36226da0256d")))
        .publicId(MoviePublicId.of(666L))
        .sourceId(SourceId.of(Source.TMDB, "78"))
        .title(Title.of("Blade Runner"))
        .releaseDate(ReleaseDate.of(LocalDate.parse("1982-06-25")))
        .originalLanguage(OriginalLanguage.of("en"))
        .build();
    assertEquals("019f7610-29d0-7282-894f-36226da0256d", movie.id().toString());
    assertEquals("mv_666", movie.publicId().map(MoviePublicId::toString).orElseThrow());
    assertEquals("78", movie.sourceId().id());
    assertEquals("Blade Runner", movie.title().toString());
    assertEquals("1982-06-25", movie.releaseDate().map(ReleaseDate::toString).orElseThrow());
    assertEquals("en", movie.originalLanguage().toString());
    
    var command = SaveMovieCommand.builder()
        .sourceId("tmdb", "78")
        .title("Blade Runner: Directors Cut")
        .releaseDate(LocalDate.parse("2003-10-09"))
        .originalLanguage("fr")        
        .build();
    var merged = mapper.merge(movie, command);
    assertEquals("019f7610-29d0-7282-894f-36226da0256d", merged.id().toString());
    assertEquals("mv_666", merged.publicId().map(MoviePublicId::toString).orElseThrow());
    assertEquals("78", merged.sourceId().id());
    assertEquals("Blade Runner: Directors Cut", merged.title().toString());
    assertEquals("2003-10-09", merged.releaseDate().map(ReleaseDate::toString).orElseThrow());
    assertEquals("fr", merged.originalLanguage().toString());    
  }
  
  @Test
  void testUpdate() {
    Movie movie = Movie.builder()
        .id(MovieId.of(UUID.fromString("019f7610-29d0-7282-894f-36226da0256d")))
        .publicId(MoviePublicId.of(666L))
        .sourceId(SourceId.of(Source.TMDB, "78"))
        .version(Version.of(0L))
        .title(Title.of("Blade Runner"))
        .releaseDate(ReleaseDate.of(LocalDate.parse("1982-06-25")))
        .originalLanguage(OriginalLanguage.of("en"))
        .build();
    assertEquals("019f7610-29d0-7282-894f-36226da0256d", movie.id().toString());
    assertEquals("mv_666", movie.publicId().map(MoviePublicId::toString).orElseThrow());
    assertEquals("78", movie.sourceId().id());
    assertEquals("Blade Runner", movie.title().toString());
    assertEquals("1982-06-25", movie.releaseDate().map(ReleaseDate::toString).orElseThrow());
    assertEquals("en", movie.originalLanguage().toString());
    
    var command = UpdateMovieCommand.builder()
        .version(0L)
        .title("Blade Runner: Directors Cut")
        .releaseDate(LocalDate.parse("2003-10-09"))
        .originalLanguage("fr")        
        .build();
    var merged = mapper.merge(movie, command);
    assertEquals("019f7610-29d0-7282-894f-36226da0256d", merged.id().toString());
    assertEquals("mv_666", merged.publicId().map(MoviePublicId::toString).orElseThrow());
    assertEquals("78", merged.sourceId().id());
    assertEquals(0, merged.version().get().value());
    assertEquals("Blade Runner: Directors Cut", merged.title().toString());
    assertEquals("2003-10-09", merged.releaseDate().map(ReleaseDate::toString).orElseThrow());
    assertEquals("fr", merged.originalLanguage().toString());    
  }
}
