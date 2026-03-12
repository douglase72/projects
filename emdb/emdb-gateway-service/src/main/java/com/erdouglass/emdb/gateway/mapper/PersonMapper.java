package com.erdouglass.emdb.gateway.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.ValueMapping;

import com.erdouglass.emdb.common.Gender;
import com.erdouglass.emdb.common.comand.SavePerson;
import com.erdouglass.emdb.common.query.PersonDto;
import com.erdouglass.emdb.media.proto.v1.PersonResponse;
import com.erdouglass.emdb.media.proto.v1.SavePersonRequest;

@Mapper(
    componentModel = "cdi", 
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS 
)
public interface PersonMapper {

  SavePersonRequest toSavePersonRequest(SavePerson command);
  
  @BeanMapping(builder = @Builder(disableBuilder = true))  
  @Mapping(target = "profile", expression = "java(response.hasProfile() ? response.getProfile() + \".jpg\" : null)")
  PersonDto toPersonDto(PersonResponse response);
  
  @ValueMapping(source = "GENDER_UNSPECIFIED", target = MappingConstants.NULL)
  @ValueMapping(source = "UNRECOGNIZED", target = MappingConstants.NULL)
  Gender mapProtoToJava(com.erdouglass.emdb.media.proto.v1.Gender gender);
  
  @ValueMapping(source = MappingConstants.NULL, target = "GENDER_UNSPECIFIED")
  com.erdouglass.emdb.media.proto.v1.Gender mapJavaToProto(Gender gender);
  
}
