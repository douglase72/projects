package com.erdouglass.emdb.media.movie.domain.model;

import java.util.Objects;

import com.erdouglass.emdb.media.kernel.CreditId;

public final class CastCredit extends MovieCredit {
  private CastDetails details;
  
  private CastCredit(CreditId id, CastDetails details) {
    super(id);
    this.details = Objects.requireNonNull(details, "details are required");
  }
  
  public static CastCredit create(CastDetails details) {
    return new CastCredit(CreditId.newId(), details);
  }
  
  @Override
  public void update(CreditDetails credit) {
    switch (credit) {
      case CastDetails cast -> details = cast;
      case CrewDetails _ -> throw new IllegalArgumentException("invalid credit: " + credit);
    }
  }

  @Override
  public CreditDetails details() {
    return details;
  }
}
