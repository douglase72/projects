package com.erdouglass.emdb.media.series;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import com.erdouglass.emdb.media.credit.Credit;

@Entity
class SeriesCredit extends Credit {
  
  /// The roles collection in a series credit is a bidirectional association 
  /// specified by the mappedBy field which maps the {@link SeriesCredit#id} primary 
  /// key to the foreign key in the {@code Roles} table.
  @OneToMany(mappedBy = _Role.SERIES_CREDIT)
  private List<Role> roles = new ArrayList<>();  

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
  
  public void setRoles(List<Role> roles) {
    this.roles = new ArrayList<>(roles);
    this.roles.forEach(r -> r.setSeriesCredit(this));
  }
  
  public List<Role> getRoles() {
    return roles;
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
