package com.erdouglass.emdb.gateway.mapper;

import java.util.List;

import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import com.erdouglass.emdb.common.comand.SavePerson;
import com.erdouglass.emdb.common.comand.UpdatePerson;
import com.erdouglass.emdb.common.query.PersonDetails;
import com.erdouglass.emdb.common.query.PersonView;
import com.erdouglass.emdb.gateway.query.MultiResponse;
import com.erdouglass.emdb.gateway.query.Page;
import com.erdouglass.emdb.gateway.query.PersonQueryParams;
import com.erdouglass.emdb.media.proto.v1.FindAllPersonRequest;
import com.erdouglass.emdb.media.proto.v1.FindRequest;
import com.erdouglass.emdb.media.proto.v1.PersonPageResponse;
import com.erdouglass.emdb.media.proto.v1.PersonResponse;
import com.erdouglass.emdb.media.proto.v1.SavePeopleRequest;
import com.erdouglass.emdb.media.proto.v1.SavePersonRequest;
import com.erdouglass.emdb.media.proto.v1.SaveResult;
import com.erdouglass.emdb.media.proto.v1.UpdatePersonRequest;

@Mapper(
    componentModel = "cdi", 
    collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS 
)
public interface PersonMapper extends CommonMapper {

  // Request
  
  SavePersonRequest toSavePersonRequest(SavePerson command);
  
  default SavePeopleRequest toSavePeopleRequest(List<SavePerson> commands) {
    var builder = SavePeopleRequest.newBuilder();
    for (var command : commands) {
      builder.addPeople(toSavePersonRequest(command));
    }
    return builder.build();
  } 
  
  FindRequest toFindRequest(Long id, String append);
  
  FindAllPersonRequest toFindAllPersonRequest(PersonQueryParams parameters);
    
  @Mapping(source = "id", target = "id")
  @Mapping(source = "command", target = "command")
  UpdatePersonRequest toUpdatePersonRequest(Long id, UpdatePerson command);
  
  // Response
  
  @Mapping(source = "profile", target = "profile", qualifiedByName = "imageToString")
  PersonDetails toPersonDetails(PersonResponse response);
  
  @Mapping(source = "status", target = "statusCode")
  MultiResponse toMultiResponse(SaveResult result);
  
  List<MultiResponse> toMultiResponse(List<SaveResult> results);
  
  Page<PersonView> toPage(PersonPageResponse response);
}
