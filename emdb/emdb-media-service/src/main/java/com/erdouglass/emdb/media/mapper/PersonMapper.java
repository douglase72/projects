package com.erdouglass.emdb.media.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.ValueMapping;

import com.erdouglass.emdb.common.Gender;
import com.erdouglass.emdb.common.comand.SavePerson;
import com.erdouglass.emdb.media.entity.Person;
import com.erdouglass.emdb.media.proto.v1.PersonResponse;
import com.erdouglass.emdb.media.proto.v1.SavePersonRequest;

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
  
  @BeanMapping(builder = @Builder(disableBuilder = true))
  SavePerson toSavePerson(SavePersonRequest request);
  
  @ValueMapping(source = "GENDER_UNSPECIFIED", target = MappingConstants.NULL)
  @ValueMapping(source = "UNRECOGNIZED", target = MappingConstants.NULL)
  Gender mapProtoToJava(com.erdouglass.emdb.media.proto.v1.Gender gender);
  
  @ValueMapping(source = MappingConstants.NULL, target = "GENDER_UNSPECIFIED")
  com.erdouglass.emdb.media.proto.v1.Gender mapJavaToProto(Gender gender);
  
}
