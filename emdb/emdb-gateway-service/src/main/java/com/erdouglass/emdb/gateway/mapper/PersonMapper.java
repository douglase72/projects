package com.erdouglass.emdb.gateway.mapper;

import java.util.List;

import org.mapstruct.BeanMapping;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import com.erdouglass.emdb.common.comand.SavePerson;
import com.erdouglass.emdb.common.comand.UpdatePerson;
import com.erdouglass.emdb.common.query.MultiResponseDto;
import com.erdouglass.emdb.common.query.PersonDto;
import com.erdouglass.emdb.media.proto.v1.FindPersonRequest;
import com.erdouglass.emdb.media.proto.v1.PersonResponse;
import com.erdouglass.emdb.media.proto.v1.SavePeopleRequest;
import com.erdouglass.emdb.media.proto.v1.SavePersonRequest;
import com.erdouglass.emdb.media.proto.v1.SaveResult;
import com.erdouglass.emdb.media.proto.v1.UpdatePersonRequest;

@Mapper(
    componentModel = "cdi", 
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS 
)
public interface PersonMapper extends CommonMapper{

  SavePersonRequest toSavePersonRequest(SavePerson command); 
  
  FindPersonRequest toFindPersonRequest(Long id, String append);
  
  @Mapping(target = "id", source = "id")
  @Mapping(target = "command", source = "command")
  UpdatePersonRequest toUpdatePersonRequest(Long id, UpdatePerson command);
  
  @BeanMapping(builder = @Builder(disableBuilder = true))  
  @Mapping(target = "profile", expression = "java(response.hasProfile() ? response.getProfile() + \".jpg\" : null)")
  PersonDto toPersonDto(PersonResponse response);
  
  /// MapStruct cant convert a List into a protobuf wrapper message
  default SavePeopleRequest toSavePeopleRequest(List<SavePerson> commands) {
    if (commands == null) return null;
    return SavePeopleRequest.newBuilder()
        .addAllPeople(commands.stream().map(this::toSavePersonRequest).toList())
        .build();    
  }
  
  @Mapping(source = "status", target = "statusCode")
  MultiResponseDto toSaveResultDto(SaveResult result);
  
  List<MultiResponseDto> toMultiResponseDto(List<SaveResult> results);
  
}
