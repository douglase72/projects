package com.erdouglass.emdb.media.movie.domain.model;

import java.util.Objects;

import com.erdouglass.emdb.media.kernel.CreditId;
import com.erdouglass.emdb.media.kernel.Name;
import com.erdouglass.emdb.media.kernel.TmdbCreditId;
import com.erdouglass.emdb.media.person.domain.model.PersonPublicId;

public abstract sealed class MovieCredit permits CastCredit, CrewCredit {
  public static final int LENGTH = 64;
  
  private final CreditId id;
  
  protected MovieCredit(CreditId id) {
    this.id = Objects.requireNonNull(id, "credit id is required"); 
  }
  
  public abstract void update(CreditDetails details);
  
  public CreditId id() { return id; }
  public abstract CreditDetails details();
  public TmdbCreditId tmdbId() { return details().tmdbId(); }
  public PersonPublicId personId() { return details().personId(); }
  public Name name() { return details().name(); }
  
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
