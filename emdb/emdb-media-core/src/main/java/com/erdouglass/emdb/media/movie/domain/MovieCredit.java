package com.erdouglass.emdb.media.movie.domain;

import java.util.Objects;

import com.erdouglass.emdb.media.kernel.SourceId;
import com.erdouglass.emdb.media.person.domain.Name;

import lombok.Getter;

@Getter
public abstract sealed class MovieCredit permits CastCredit, CrewCredit {  
  private final CreditId id;
  private final SourceId sourceId;
  private final String personId;
  private final Name name;
  
  protected MovieCredit(CreditId id, SourceId sourceId, String personId, Name name) {
    this.id = Objects.requireNonNull(id, "credit id is required"); 
    this.sourceId = Objects.requireNonNull(sourceId, "source id is required"); 
    this.personId = Objects.requireNonNull(personId, "person id is required");
    this.name = Objects.requireNonNull(name, "name is required");
  }
  
  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
  
  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    MovieCredit other = (MovieCredit) obj;
    return Objects.equals(id, other.id);
  }
}
