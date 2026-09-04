package com.erdouglass.emdb.media.movie.domain.model;

import com.erdouglass.emdb.media.kernel.AggregateId;
import com.erdouglass.emdb.media.kernel.CastOrder;
import com.erdouglass.emdb.media.kernel.Credit;
import com.erdouglass.emdb.media.kernel.Credit.CastDto;
import com.erdouglass.emdb.media.kernel.CreditId;
import com.erdouglass.emdb.media.kernel.Name;
import com.erdouglass.emdb.media.kernel.Role;
import com.erdouglass.emdb.media.kernel.TmdbCreditId;

import lombok.Builder;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Builder
@Accessors(fluent = true)
public final class CastCredit implements MovieCredit {
  private final CreditId id;
  private final TmdbCreditId tmdbId;
  private final AggregateId personId;
  private final Name name;
  private Role character;
  private CastOrder order;
  
  public static CastCredit create(CastDto credit) {
    return builder()
        .id(CreditId.newId())
        .tmdbId(credit.tmdbId())
        .personId(credit.personId())
        .name(credit.name())
        .character(credit.character())
        .order(credit.order())
        .build();
  }

  @Override
  public void update(Credit credit) {
    if (credit instanceof CastDto cast) {
      this.character = cast.character();
      this.order = cast.order();
    } else {
      throw new IllegalArgumentException("invalid credit: " + credit);
    }
  }
}
