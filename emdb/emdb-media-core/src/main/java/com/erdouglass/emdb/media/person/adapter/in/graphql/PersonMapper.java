package com.erdouglass.emdb.media.person.adapter.in.graphql;

import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.ReportingPolicy;

import com.erdouglass.emdb.media.person.application.port.out.PersonView;
import com.erdouglass.emdb.media.person.domain.PersonPublicId;

@Mapper(
    componentModel = "cdi", 
    collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
    unmappedTargetPolicy = ReportingPolicy.ERROR,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
interface PersonMapper {

  @Mapping(target = "id", source = "id", qualifiedByName = "toPublicId")
  PersonResponse toPersonResponse(PersonView view);
  
  @Named("toPublicId")
  default String toPublicId(Long id) {
    return PersonPublicId.from(id).value();
  }
}
