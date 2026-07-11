package com.erdouglass.emdb.media.domain.series;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import com.erdouglass.emdb.media.domain.shared.Credit;

@Entity
public class SeriesCredit extends Credit {
  
  /// The foreign key in the Credits table that points back to the Series table.
  @NotNull
  @Column(name = "series_id", updatable = false)
  private Long seriesId;

  @NotNull
  @PositiveOrZero
  @Column(name = "total_episodes")
  private Integer totalEpisodes;
  
  SeriesCredit() {}
  
  public void setSeriesId(Long seriesId) {
    this.seriesId = seriesId;
  }
  
  public Long getSeriesId() {
    return seriesId;
  }
  
  public void setTotalEpisodes(Integer totalEpisodes) {
    this.totalEpisodes = totalEpisodes;
  }
  
  public Integer getTotalEpisodes() {
    return totalEpisodes;
  }
}
