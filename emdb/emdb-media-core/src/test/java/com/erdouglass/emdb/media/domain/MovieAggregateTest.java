package com.erdouglass.emdb.media.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.erdouglass.emdb.media.MediaType;
import com.erdouglass.emdb.media.SaveMovieCommand;
import com.erdouglass.emdb.media.domain.movie.Movie;
import com.erdouglass.emdb.media.domain.movie.MovieId;
import com.erdouglass.emdb.media.domain.movie.ReleaseDate;
import com.erdouglass.emdb.media.domain.shared.OriginalLanguage;
import com.erdouglass.emdb.media.domain.shared.PublicId;
import com.erdouglass.emdb.media.domain.shared.SourceId;
import com.erdouglass.emdb.media.domain.shared.SourceId.Source;
import com.erdouglass.emdb.media.domain.shared.Title;

class MovieAggregateTest {
  
  @Test
  void testMerge() {
    Movie movie = Movie.builder()
        .id(MovieId.of(UUID.fromString("019f7610-29d0-7282-894f-36226da0256d")))
        .publicId(PublicId.of(MediaType.MOVIE, 666L))
        .sourceId(SourceId.of(Source.TMDB, "78"))
        .title(Title.of("Blade Runner"))
        .releaseDate(ReleaseDate.of(LocalDate.parse("1982-06-25")))
        .originalLanguage(OriginalLanguage.of("en"))
        .build();
    assertEquals("019f7610-29d0-7282-894f-36226da0256d", movie.id().toString());
    assertEquals("mv_666", movie.publicId().map(PublicId::toString).orElseThrow());
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
    movie.merge(command);
    assertEquals("019f7610-29d0-7282-894f-36226da0256d", movie.id().toString());
    assertEquals("mv_666", movie.publicId().map(PublicId::toString).orElseThrow());
    assertEquals("78", movie.sourceId().id());
    assertEquals("Blade Runner: Directors Cut", movie.title().toString());
    assertEquals("2003-10-09", movie.releaseDate().map(ReleaseDate::toString).orElseThrow());
    assertEquals("fr", movie.originalLanguage().toString());    
  }
}
