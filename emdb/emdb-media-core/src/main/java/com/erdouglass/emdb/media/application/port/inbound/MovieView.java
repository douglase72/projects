package com.erdouglass.emdb.media.application.port.inbound;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.eclipse.microprofile.graphql.NonNull;

import com.erdouglass.emdb.media.Gender;
import com.erdouglass.emdb.media.show.ShowStatus;

public record MovieView(
    @NonNull Long id,
    @NonNull String title,
    LocalDate releaseDate,
    @NonNull BigDecimal score,
    @NonNull ShowStatus status,
    @NonNull Integer runtime,
    @NonNull Long budget,
    @NonNull Long revenue,
    String backdrop,
    String poster,
    String homepage,
    @NonNull String originalLanguage,
    String tagline,
    String overview) {
  
  @Override
  public String toString() {
    return getClass().getSimpleName() + "[id=" + id
        + ", title=" + title
        + ", releaseDate=" + releaseDate
        + "]";
  } 
  
  public record MovieCredits(
      @NonNull List<@NonNull MovieCastCredit> cast,
      @NonNull List<@NonNull MovieCrewCredit> crew) {}
  
  public record MovieCastCredit(
      @NonNull Long id,
      @NonNull String name, 
      @NonNull Gender gender,
      String profile, 
      String character,
      @NonNull Integer order) {}
  
  public record MovieCrewCredit(
      @NonNull Long id,
      @NonNull String name, 
      @NonNull Gender gender,
      String profile, 
      String job) {}  
}
