package com.erdouglass.emdb.media.movie.domain.model;

import java.util.Objects;

import com.erdouglass.emdb.media.kernel.CreditId;

public final class CrewCredit extends MovieCredit {
  private CrewDetails details;
  
  private CrewCredit(CreditId id, CrewDetails details) {
    super(id);
    this.details = Objects.requireNonNull(details, "details are required");
  }
  
  public static CrewCredit create(CrewDetails details) {
    return new CrewCredit(CreditId.newId(), details);
  }
  
  @Override
  public void update(CreditDetails credit) {
    switch (credit) {
      case CastDetails _ -> throw new IllegalArgumentException("invalid credit: " + credit);
      case CrewDetails crew -> details = crew;
    }
  }

  @Override
  public CreditDetails details() {
    return details;
  }
}
