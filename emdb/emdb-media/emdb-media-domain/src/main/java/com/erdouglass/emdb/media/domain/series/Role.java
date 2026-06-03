package com.erdouglass.emdb.media.domain.series;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import com.erdouglass.emdb.media.domain.internal.UuidMedia;
import com.erdouglass.emdb.media.show.ShowConstants;

@Entity
@Table(name = "Roles")
class Role extends UuidMedia {
  
  @NotNull
  @PositiveOrZero
  @Column(name = "episode_count")
  private Integer episodeCount;
  
  @Size(max = ShowConstants.ROLE_MAX_LENGTH)
  private String role;
  
  /// The @JoinColumn annotation maps the {@link SeriesCredit#id} primary key to the
  /// foreign key in the Roles table. A {@code Role} can't exist without a 
  /// {@link SeriesCredit}.
  @ManyToOne
  @JoinColumn(name = "series_credit_id", updatable = false, nullable = false)
  private SeriesCredit seriesCredit;
  
  protected Role() {}
    
  protected Role(final String creditId) {
    super(creditId);
  }
  
  public void setEpisodeCount(Integer episodeCount) {
    this.episodeCount = episodeCount;
  }
  
  public Integer getEpisodeCount() {
    return episodeCount;
  }  
 
  public void setRole(String role) {
    this.role = role;
  }
  
  public String getRole() {
    return role;
  }
  
  public void setSeriesCredit(SeriesCredit seriesCredit) {
    this.seriesCredit = seriesCredit;
  }
  
  public SeriesCredit getSeriesCredit() {
    return seriesCredit;
  }
  
  @Override
  public String toString() {
    return "Role[id=" + getId()
      + ", role=" + getRole()
      + ", episodeCount=" + getEpisodeCount()
      + "]";
  }
}
