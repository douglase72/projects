package com.erdouglass.emdb.media.series;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import com.erdouglass.emdb.media.credit.Credit;

@Entity
class SeriesCredit extends Credit {

  /// The @JoinColumn annotation maps the {@link Series#id} primary key to the 
  /// foreign key in the Credits table. A {@code SeriesCredit} can't exist 
  /// without a {@link Series}.
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "series_id", updatable = false)
  private Series series;
  
  @NotNull
  @PositiveOrZero
  @Column(name = "total_episodes")
  private Integer totalEpisodes;
  
  protected SeriesCredit() {}
  
  protected SeriesCredit(final String creditId) {
    super(creditId);
  }
  
  public void setSeries(Series series) {
    this.series = series;
  }
  
  public Series getSeries() {
    return series;
  }
  
  public void setTotalEpisodes(Integer totalEpisodes) {
    this.totalEpisodes = totalEpisodes;
  }
  
  public Integer getTotalEpisodes() {
    return totalEpisodes;
  }
  
  @Override
  public String toString() {
    return getClass().getSimpleName() + "[id=" + getId()
      + ", type=" + getType()
      + ", order=" + getOrder()
      + ", totalEpisodes=" + getTotalEpisodes()
      + "]";
  }  
}
