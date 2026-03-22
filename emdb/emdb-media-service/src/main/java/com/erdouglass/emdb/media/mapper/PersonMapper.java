package com.erdouglass.emdb.media.mapper;

import java.util.List;

import org.mapstruct.BeanMapping;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import com.erdouglass.emdb.common.comand.SavePerson;
import com.erdouglass.emdb.common.comand.UpdatePerson;
import com.erdouglass.emdb.media.dto.SaveResult;
import com.erdouglass.emdb.media.entity.Person;
import com.erdouglass.emdb.media.proto.v1.PeopleResponse;
import com.erdouglass.emdb.media.proto.v1.PersonResponse;
import com.erdouglass.emdb.media.proto.v1.SavePersonRequest;
import com.erdouglass.emdb.media.proto.v1.SavePersonResponse;
import com.erdouglass.emdb.media.proto.v1.UpdatePersonCommand;

@Mapper(
    componentModel = "cdi", 
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface PersonMapper {
  
  void merge(SavePerson command, @MappingTarget Person person);
  void merge(UpdatePerson command, @MappingTarget Person person);
  
  @BeanMapping(builder = @Builder(disableBuilder = true))
  SavePerson toSavePerson(SavePersonRequest request);
  
  Person toPerson(SavePerson command);
  
  PersonResponse toPersonResponse(Person person);
  
  @BeanMapping(builder = @Builder(disableBuilder = true))
  UpdatePerson toUpdatePerson(UpdatePersonCommand command);
  
  @Mapping(source = "entity", target = "person")
  SavePersonResponse toSavePersonResponse(SaveResult<Person> result);
  
  /// These methods are for batch processing
  @BeanMapping(builder = @Builder(disableBuilder = true))
  List<SavePerson> toSavePeople(List<SavePersonRequest> requests);
  
  @Mapping(source = "entity.id", target = "id")
  @Mapping(source = "entity.tmdbId", target = "tmdbId")
  com.erdouglass.emdb.media.proto.v1.SaveResult toSavePersonResult(SaveResult<Person> result);
  
  /// MapStruct cant convert a List into a protobuf wrapper message
  default PeopleResponse toPeopleResponse(List<SaveResult<Person>> results) {
    if (results == null) return null;
    var builder = PeopleResponse.newBuilder();
    results.stream()
        .map(this::toSavePersonResult)
        .forEach(builder::addResults);
    return builder.build();
  }
  
}
