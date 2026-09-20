package com.erdouglass.emdb.media.movie.domain.model;

import java.util.Objects;

import com.erdouglass.emdb.media.kernel.CastOrder;
import com.erdouglass.emdb.media.kernel.PublicId;
import com.erdouglass.emdb.media.kernel.Role;
import com.erdouglass.emdb.media.kernel.TmdbCreditId;
import com.erdouglass.emdb.media.person.domain.model.Name;

public final class CastCredit implements MovieCredit {
  private final PublicId id;
  private CastDetails details;

  private CastCredit(
      PublicId id, 
      CastDetails details) {
    this.id = Objects.requireNonNull(id, "id is required");
    this.details = Objects.requireNonNull(details, "details are required");
  }
  
  public static CastCredit create(CastDetails details) {
    return new CastCredit(PublicId.newId(), details);
  }
  
  @Override
  public void update(CreditDetails details) {
    if (details instanceof CastDetails cast) {
      this.details = cast;
    } else {
      throw new IllegalArgumentException("Invalid details: " + details);
    }
  }
  
  @Override public PublicId id() { return id; }
  @Override public CastDetails details() { return details; }
  @Override public TmdbCreditId tmdbId() { return details.tmdbId(); }
  @Override public PublicId personId() { return details.personId(); }
  @Override public Name name() { return details.name(); }
  public Role character() { return details.character(); }
  public CastOrder order() { return details.order(); }
}
