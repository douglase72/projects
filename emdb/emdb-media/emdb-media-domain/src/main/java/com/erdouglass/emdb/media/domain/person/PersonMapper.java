package com.erdouglass.emdb.media.domain.person;

import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import com.erdouglass.emdb.media.api.Image;
import com.erdouglass.emdb.media.api.command.SavePerson;
import com.erdouglass.emdb.media.api.query.PersonResponse;

@Mapper(
    componentModel = "cdi", 
    collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
    unmappedTargetPolicy = ReportingPolicy.ERROR,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
interface PersonMapper {
  
  @Mapping(source = "profile.tmdbName", target = "tmdbProfile")
  @Mapping(source = "profile.emdbName", target = "profile")
  void merge(SavePerson command, @MappingTarget Person person);

  @Mapping(source = "profile.tmdbName", target = "tmdbProfile")
  @Mapping(source = "profile.emdbName", target = "profile")
  Person toPerson(SavePerson command, Image profile);
  
  PersonResponse toPersonResponse(Person person );
}
