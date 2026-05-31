package com.erdouglass.emdb.ingest.scraper.person;

import org.mapstruct.BeanMapping;
import org.mapstruct.Builder;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.ReportingPolicy;

import com.erdouglass.emdb.media.Image;
import com.erdouglass.emdb.media.person.SavePerson;

@Mapper(
    componentModel = "cdi", 
    collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
    unmappedTargetPolicy = ReportingPolicy.ERROR,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
interface PersonMapper {

  @BeanMapping(builder = @Builder(disableBuilder = true))
  @Mapping(source = "person.id", target = "tmdbId")
  @Mapping(source = "person.birthday", target = "birthDate")
  @Mapping(source = "person.deathday", target = "deathDate")
  @Mapping(source = "person.place_of_birth", target = "birthPlace")
  SavePerson toSavePerson(Person person, Image profile);
}
