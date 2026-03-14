package com.erdouglass.emdb.gateway.mapper;

import java.util.List;

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
import com.erdouglass.emdb.common.query.MultiResponseDto;
import com.erdouglass.emdb.common.query.PersonDto;
import com.erdouglass.emdb.media.proto.v1.PersonResponse;
import com.erdouglass.emdb.media.proto.v1.SavePeopleRequest;
import com.erdouglass.emdb.media.proto.v1.SavePersonRequest;
import com.erdouglass.emdb.media.proto.v1.SavePersonResult;

@Mapper(
    componentModel = "cdi", 
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS 
)
public interface PersonMapper extends CommonMapper{

  SavePersonRequest toSavePersonRequest(SavePerson command);
  
  default SavePeopleRequest toSavePeopleRequest(List<SavePerson> commands) {
    if (commands == null || commands.isEmpty()) {
      return SavePeopleRequest.getDefaultInstance();
    }    
    var requests = commands.stream().map(this::toSavePersonRequest).toList();     
    return SavePeopleRequest.newBuilder().addAllPeople(requests).build();
  }  
  
  @BeanMapping(builder = @Builder(disableBuilder = true))  
  @Mapping(target = "profile", expression = "java(response.hasProfile() ? response.getProfile() + \".jpg\" : null)")
  PersonDto toPersonDto(PersonResponse response);
  
  @Mapping(source = "status", target = "statusCode")
  MultiResponseDto toSaveResultDto(SavePersonResult result);
  
  List<MultiResponseDto> toMultiResponseDto(List<SavePersonResult> results);
  
  @ValueMapping(source = "GENDER_UNSPECIFIED", target = MappingConstants.NULL)
  @ValueMapping(source = "UNRECOGNIZED", target = MappingConstants.NULL)
  Gender mapProtoToJava(com.erdouglass.emdb.media.proto.v1.Gender gender);
  
  @ValueMapping(source = MappingConstants.NULL, target = "GENDER_UNSPECIFIED")
  com.erdouglass.emdb.media.proto.v1.Gender mapJavaToProto(Gender gender);
  
}
