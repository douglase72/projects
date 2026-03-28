package com.erdouglass.emdb.scraper.mapper;

import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import com.erdouglass.emdb.common.Gender;
import com.erdouglass.emdb.common.comand.Image;
import com.erdouglass.emdb.common.comand.SavePerson;
import com.erdouglass.emdb.scraper.query.TmdbPerson;

@Mapper(
    componentModel = "cdi", 
    unmappedTargetPolicy = ReportingPolicy.ERROR,
    builder = @Builder(disableBuilder = true)
)
public interface TmdbPersonMapper {

  @Mapping(source = "person.id", target = "tmdbId")
  @Mapping(source = "person.name", target = "name")
  @Mapping(source = "person.birthday", target = "birthDate")
  @Mapping(source = "person.deathday", target = "deathDate")
  @Mapping(source = "person.place_of_birth", target = "birthPlace")
  SavePerson toSavePerson(TmdbPerson person, Image profile);
  
  default Gender toGender(Integer gender) {
    if (gender == null) {
      return null;
    }
    return Gender.from(gender);
  }
  
}
