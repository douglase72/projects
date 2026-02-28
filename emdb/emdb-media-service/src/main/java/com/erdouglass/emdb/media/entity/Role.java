package com.erdouglass.emdb.media.entity;

import java.util.Objects;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import org.hibernate.annotations.UuidGenerator;

import com.erdouglass.emdb.common.ShowConstants;

@Entity
@Table(name = "Roles")
public class Role {
  
  @NotNull
  @PositiveOrZero
  @Column(name = "episode_count")
  private Integer episodeCount;  

  /// The surrogate primary key has no business meaning.
  @Id
  @GeneratedValue
  @UuidGenerator(style = UuidGenerator.Style.TIME)
  private UUID id;
  
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
  
  public Role(String role, Integer episodeCount) {
    this.role = role;
    this.episodeCount = episodeCount;
  }
  
  public void episodeCount(int episodeCount) {
    this.episodeCount = episodeCount;
  }
  
  public Integer episodeCount() {
    return episodeCount;
  }
  
  public UUID id() {
    return id;
  }
  
  public boolean isEqualTo(Role other) {
    return Objects.equals(this.role(), other.role())
        && Objects.equals(this.episodeCount, other.episodeCount);
  }
  
  public void role(String role) {
    this.role = role;
  }
  
  public String role() {
    return role;
  }
  
  public void seriesCredit(SeriesCredit seriesCredit) {
    this.seriesCredit = seriesCredit;
  }
  
  public SeriesCredit seriesCredit() {
    return seriesCredit;
  }
  
  @Override
  public String toString() {
    return "Role[id=" + id()
      + ", role=" + role()
      + ", episodeCount=" + episodeCount()
      + "]";
  }
  
}
