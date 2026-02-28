package com.erdouglass.emdb.media.mapper;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import com.erdouglass.emdb.common.CreditType;
import com.erdouglass.emdb.common.comand.SaveSeriesCastCredit;
import com.erdouglass.emdb.common.comand.SaveSeriesCrewCredit;
import com.erdouglass.emdb.common.query.JobDto;
import com.erdouglass.emdb.common.query.RoleDto;
import com.erdouglass.emdb.common.query.SeriesCastCreditDto;
import com.erdouglass.emdb.common.query.SeriesCrewCreditDto;
import com.erdouglass.emdb.media.entity.Person;
import com.erdouglass.emdb.media.entity.Role;
import com.erdouglass.emdb.media.entity.Series;
import com.erdouglass.emdb.media.entity.SeriesCredit;

@ApplicationScoped
public class SeriesCreditMapper {
  
  @Inject
  PersonMapper mapper;
  
  public SeriesCredit toSeriesCredit(SaveSeriesCastCredit command, Series series, Person person) {    
    var credit = new SeriesCredit();
    credit.series(series);
    credit.person(person);
    credit.type(CreditType.CAST);
    credit.roles(command.roles().stream().map(r -> new Role(r.character(), r.episodeCount())).toList());
    credit.order(command.order());
    credit.totalEpisodes(credit.roles().stream().mapToInt(Role::episodeCount).sum());
    return credit;
  }
  
  public SeriesCredit toSeriesCredit(SaveSeriesCrewCredit command, Series series, Person person) {    
    var credit = new SeriesCredit();
    credit.series(series);
    credit.person(person);
    credit.type(CreditType.CREW);
    credit.roles(command.jobs().stream().map(j -> new Role(j.title(), j.episodeCount())).toList());
    credit.totalEpisodes(credit.roles().stream().mapToInt(Role::episodeCount).sum());
    return credit;
  }  
  
  public SeriesCastCreditDto toSeriesCastCreditDto(SeriesCredit credit) {
    return SeriesCastCreditDto.builder()
        .creditId(credit.id())
        .id(credit.person().id())
        .name(credit.person().name())
        .gender(credit.person().gender().orElse(null))
        .profile(credit.person().profile().map(p -> String.format("%s.jpg", p)).orElse(null))
        .roles(credit.roles().stream()
            .map(r -> RoleDto.of(r.id(), r.role(), r.episodeCount())).toList())
        .totalEpisodes(credit.totalEpisodes())
        .order(credit.order().orElse(null))
        .build();
  }
  
  public SeriesCrewCreditDto toSeriesCrewCreditDto(SeriesCredit credit) {
    return SeriesCrewCreditDto.builder()
        .creditId(credit.id())
        .id(credit.person().id())
        .name(credit.person().name())
        .gender(credit.person().gender().orElse(null))
        .profile(credit.person().profile().map(p -> String.format("%s.jpg", p)).orElse(null))
        .jobs(credit.roles().stream().map(r -> JobDto.of(r.id(), r.role(), r.episodeCount())).toList())
        .totalEpisodes(credit.totalEpisodes())
        .build();
  }

}
