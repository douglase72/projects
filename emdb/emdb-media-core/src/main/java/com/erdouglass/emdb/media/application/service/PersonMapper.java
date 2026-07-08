package com.erdouglass.emdb.media.application.service;

import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ObjectFactory;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.SubclassExhaustiveStrategy;

import com.erdouglass.emdb.media.SavePerson;
import com.erdouglass.emdb.media.application.port.in.PersonView;
import com.erdouglass.emdb.media.domain.person.Person;

@Mapper(
    componentModel = "cdi",
    collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
    unmappedTargetPolicy = ReportingPolicy.ERROR,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
    subclassExhaustiveStrategy = SubclassExhaustiveStrategy.RUNTIME_EXCEPTION
)
interface PersonMapper extends CommonMapper{

  @Mapping(source = "profile.name", target = "profile")
  void merge(SavePerson command, @MappingTarget Person person);
  
  @Mapping(source = "profile.name", target = "profile")
  Person toPerson(SavePerson command);
  
  @Mapping(source = "profile", target = "profile", qualifiedByName = "imageToString")
  PersonView toPersonView(Person person);
  
  @ObjectFactory
  default Person createPerson(SavePerson command) {
    return new Person(command.externalId());
  }
}
