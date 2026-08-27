package com.erdouglass.emdb.media.movie.domain;

import com.erdouglass.emdb.media.kernel.SourceId;
import com.erdouglass.emdb.media.movie.domain.command.SaveMovieCommand.CastMember;
import com.erdouglass.emdb.media.person.domain.Name;

import lombok.Getter;

@Getter
public final class CastCredit extends MovieCredit {
  private final Role character;
  private final CastOrder order;
  
  private CastCredit(
      CreditId id, 
      SourceId sourceId, 
      String personId, 
      Name name,
      Role character,
      CastOrder order) {
    super(id, sourceId, personId, name);
    this.character = character; 
    this.order = order; 
  }
  
  public static CastCredit create(CreditId id, SourceId sourceId, CastMember member) {
    return new CastCredit(
        id, 
        sourceId, 
        member.personId(), 
        Name.of(member.name()), 
        Role.of(member.character()), 
        CastOrder.of(member.order()));
  }
  
  public CastCredit update(SourceId sourceId, CastMember member) {
    return new CastCredit(
        id(), 
        sourceId, 
        member.personId(), 
        Name.of(member.name()), 
        Role.of(member.character()), 
        CastOrder.of(member.order()));
  }
}
