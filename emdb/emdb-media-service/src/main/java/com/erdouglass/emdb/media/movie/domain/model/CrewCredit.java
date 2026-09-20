package com.erdouglass.emdb.media.movie.domain.model;

import java.util.Objects;

import com.erdouglass.emdb.media.kernel.Department;
import com.erdouglass.emdb.media.kernel.PublicId;
import com.erdouglass.emdb.media.kernel.Role;
import com.erdouglass.emdb.media.kernel.TmdbCreditId;
import com.erdouglass.emdb.media.person.domain.model.Name;

public final class CrewCredit implements MovieCredit {
  private final PublicId id;
  private CrewDetails details;
  
  private CrewCredit(
      PublicId id, 
      CrewDetails details) {
    this.id = Objects.requireNonNull(id, "id is required");
    this.details = Objects.requireNonNull(details, "details are required");
  }
  
  public static CrewCredit create(CrewDetails details) {
    return new CrewCredit(PublicId.newId(), details);
  }
  
  @Override
  public void update(CreditDetails details) {
    if (details instanceof CrewDetails crew) {
      this.details = crew;
    } else {
      throw new IllegalArgumentException("Invalid details: " + details);
    }
  }
  
  @Override public PublicId id() { return id; }
  @Override public CrewDetails details() { return details; }
  @Override public TmdbCreditId tmdbId() { return details.tmdbId(); }
  @Override public PublicId personId() { return details.personId(); }
  @Override public Name name() { return details.name(); }
  public Role job() { return details.job(); }
  public Department department() { return details.department(); }
}
