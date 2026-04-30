package com.erdouglass.emdb.scraper.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import com.erdouglass.emdb.common.Gender;
import com.erdouglass.emdb.common.Image;
import com.erdouglass.emdb.common.comand.SavePerson;
import com.erdouglass.emdb.scraper.query.TmdbPerson;

@Mapper(
    componentModel = "cdi", 
    unmappedTargetPolicy = ReportingPolicy.ERROR,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface TmdbPersonMapper {
  
  @Named("genderFromId")
  default Gender genderFromId(Integer id) {
    return Gender.from(id);
  }

  @BeanMapping(builder = @Builder(disableBuilder = true))
  @Mapping(source = "person.id", target = "tmdbId")
  @Mapping(source = "person.name", target = "name")
  @Mapping(source = "person.birthday", target = "birthDate")
  @Mapping(source = "person.deathday", target = "deathDate")
  @Mapping(source = "person.place_of_birth", target = "birthPlace")
  @Mapping(source = "person.gender", target = "gender", qualifiedByName = "genderFromId")
  SavePerson toSavePerson(TmdbPerson person, Image profile);
}
