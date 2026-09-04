package com.erdouglass.emdb.media.movie.domain.model;

import com.erdouglass.emdb.media.kernel.AggregateId;
import com.erdouglass.emdb.media.kernel.Credit;
import com.erdouglass.emdb.media.kernel.Credit.CrewDto;
import com.erdouglass.emdb.media.kernel.CreditId;
import com.erdouglass.emdb.media.kernel.Department;
import com.erdouglass.emdb.media.kernel.Name;
import com.erdouglass.emdb.media.kernel.Role;
import com.erdouglass.emdb.media.kernel.TmdbCreditId;

import lombok.Builder;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Builder
@Accessors(fluent = true)
public final class CrewCredit implements MovieCredit {
  private final CreditId id;
  private final TmdbCreditId tmdbId;
  private final AggregateId personId;
  private final Name name;
  private Role job;
  private Department department;
  
  public static CrewCredit create(CrewDto credit) {
    return builder()
        .id(CreditId.newId())
        .tmdbId(credit.tmdbId())
        .personId(credit.personId())
        .name(credit.name())
        .job(credit.job())
        .department(credit.department())
        .build();
  }
  
  @Override
  public void update(Credit credit) {
    if (credit instanceof CrewDto crew) {
      this.job = crew.job();
      this.department = crew.department();
    } else {
      throw new IllegalArgumentException("invalid credit: " + credit);
    }
  }
}
