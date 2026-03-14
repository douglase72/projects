package com.erdouglass.emdb.media.mapper;

import java.util.List;

import org.mapstruct.BeanMapping;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.ValueMapping;

import com.erdouglass.emdb.common.Gender;
import com.erdouglass.emdb.common.comand.SavePerson;
import com.erdouglass.emdb.media.dto.SaveResult;
import com.erdouglass.emdb.media.entity.Person;
import com.erdouglass.emdb.media.proto.v1.PeopleResponse;
import com.erdouglass.emdb.media.proto.v1.PersonResponse;
import com.erdouglass.emdb.media.proto.v1.SavePersonRequest;
import com.erdouglass.emdb.media.proto.v1.SavePersonResponse;
import com.erdouglass.emdb.media.proto.v1.SavePersonResult;

@Mapper(
    componentModel = "cdi", 
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface PersonMapper {
  
  void merge(SavePerson command, @MappingTarget Person person);

  Person toPerson(SavePerson command);
    
  PersonResponse toPersonResponse(Person person);
  
  @Mapping(source = "entity", target = "person")
  SavePersonResponse toSavePersonResponse(SaveResult<Person> result);
  
  @Mapping(source = "entity.id", target = "id")
  @Mapping(source = "entity.tmdbId", target = "tmdbId")
  SavePersonResult toSavePersonResult(SaveResult<Person> result);
  
  default PeopleResponse toPeopleResponse(List<SaveResult<Person>> resultsList) {
    if (resultsList == null || resultsList.isEmpty()) {
      return PeopleResponse.getDefaultInstance();
    }    
    var mappedResults = resultsList.stream().map(this::toSavePersonResult).toList();       
    return PeopleResponse.newBuilder().addAllResults(mappedResults).build();
  }
  
  @BeanMapping(builder = @Builder(disableBuilder = true))
  SavePerson toSavePerson(SavePersonRequest request);
  
  @BeanMapping(builder = @Builder(disableBuilder = true))
  List<SavePerson> toSavePeople(List<SavePersonRequest> requests);
  
  @ValueMapping(source = "GENDER_UNSPECIFIED", target = MappingConstants.NULL)
  @ValueMapping(source = "UNRECOGNIZED", target = MappingConstants.NULL)
  Gender mapProtoToJava(com.erdouglass.emdb.media.proto.v1.Gender gender);
  
  @ValueMapping(source = MappingConstants.NULL, target = "GENDER_UNSPECIFIED")
  com.erdouglass.emdb.media.proto.v1.Gender mapJavaToProto(Gender gender);
  
  @ValueMapping(source = "STATUS_UNSPECIFIED", target = MappingConstants.NULL)
  @ValueMapping(source = "UNRECOGNIZED", target = MappingConstants.NULL)
  SaveResult.Status mapProtoStatusToJava(com.erdouglass.emdb.media.proto.v1.SaveStatus status);
  
  @ValueMapping(source = MappingConstants.NULL, target = "STATUS_UNSPECIFIED")
  com.erdouglass.emdb.media.proto.v1.SaveStatus mapJavaStatusToProto(SaveResult.Status status);
  
}
