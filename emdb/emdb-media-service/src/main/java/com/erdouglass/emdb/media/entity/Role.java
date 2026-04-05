package com.erdouglass.emdb.media.entity;

import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import com.erdouglass.emdb.common.ShowConstants;

@Entity
@Table(name = "Roles")
public class Role extends UuidEntity {

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
  @JoinColumn(name = "seriesCredit_id", updatable = false, nullable = false)
  private SeriesCredit seriesCredit;
  
  Role() {
    
  }
  
  private Role(String role, Integer episodeCount) {
    this.role = role;
    this.episodeCount = episodeCount;
  }
  
  public static Role of(String role, Integer episodeCount) {
    return new Role(role, episodeCount);
  }
  
  public void setEpisodeCount(Integer episodeCount) {
    this.episodeCount = episodeCount;
  }
  
  public Integer getEpisodeCount() {
    return episodeCount;
  }
  
  public boolean isEqualTo(Role other) {
    return Objects.equals(this.getRole(), other.getRole())
        && Objects.equals(this.episodeCount, other.episodeCount);
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
