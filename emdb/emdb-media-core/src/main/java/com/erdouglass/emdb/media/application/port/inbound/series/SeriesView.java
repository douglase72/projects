package com.erdouglass.emdb.media.application.port.inbound.series;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.eclipse.microprofile.graphql.NonNull;

import com.erdouglass.emdb.media.Gender;
import com.erdouglass.emdb.media.SeriesType;
import com.erdouglass.emdb.media.show.ShowStatus;

public record SeriesView(
    @NonNull Long id,
    @NonNull String title,
    LocalDate firstAirDate,
    LocalDate lastAirDate,    
    @NonNull BigDecimal score,
    @NonNull ShowStatus status,
    @NonNull SeriesType type,
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
        + ", firstAirDate=" + firstAirDate
        + ", lastAirDate=" + lastAirDate
        + "]";
  }  
  
  public record SeriesCredits(
      @NonNull List<@NonNull SeriesCastCredit> cast, 
      @NonNull List<@NonNull SeriesCrewCredit> crew) {}
  
  public record SeriesCastCredit(
      @NonNull Long id,
      @NonNull String name, 
      @NonNull Gender gender,
      String profile, 
      @NonNull List<@NonNull Role> roles,
      @NonNull Integer totalEpisodes,
      @NonNull Integer order) {
    
    public record Role(String character, @NonNull Integer episodeCount) {}
  } 
  
  public record SeriesCrewCredit(
      @NonNull Long id,
      @NonNull String name, 
      @NonNull Gender gender,
      String profile, 
      @NonNull List<@NonNull Job> jobs,
      @NonNull Integer totalEpisodes) {
    
    public record Job(String title, @NonNull Integer episodeCount) {}
  }   
}
