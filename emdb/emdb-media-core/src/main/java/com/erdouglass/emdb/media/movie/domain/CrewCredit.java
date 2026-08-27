package com.erdouglass.emdb.media.movie.domain;

import java.util.Objects;

import com.erdouglass.emdb.media.kernel.SourceId;
import com.erdouglass.emdb.media.movie.domain.command.SaveMovieCommand.CrewMember;
import com.erdouglass.emdb.media.person.domain.Name;

import lombok.Builder;
import lombok.Getter;

@Getter
public final class CrewCredit extends MovieCredit {
  private final Role job;
  private final Department department;

  @Builder
  private CrewCredit(
      CreditId id, 
      SourceId sourceId, 
      String personId, 
      Name name,
      Role job,
      Department department) {
    super(id, sourceId, personId, name);
    this.job = Objects.requireNonNull(job, "job is required"); 
    this.department = Objects.requireNonNull(department, "department is required"); 
  }
  
  public static CrewCredit create(CreditId id, SourceId sourceId, CrewMember member) {
    return new CrewCredit(
        id, 
        sourceId,
        member.personId(), 
        Name.of(member.name()), 
        Role.of(member.job()), 
        Department.of(member.department()));
  }
  
  public CrewCredit update(SourceId sourceId, CrewMember member) {
    return new CrewCredit(
        id(), 
        sourceId,
        member.personId(), 
        Name.of(member.name()), 
        Role.of(member.job()), 
        Department.of(member.department()));
  }
}
