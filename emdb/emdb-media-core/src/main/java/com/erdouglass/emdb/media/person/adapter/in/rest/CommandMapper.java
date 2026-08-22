package com.erdouglass.emdb.media.person.adapter.in.rest;

import java.util.Optional;

import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import com.erdouglass.emdb.media.SourceId;
import com.erdouglass.emdb.media.person.SavePersonCommand;
import com.erdouglass.emdb.media.person.application.port.in.UpdatePersonCommand;

@Mapper(
    componentModel = "cdi", 
    collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
    unmappedTargetPolicy = ReportingPolicy.ERROR
)
interface CommandMapper {

  SavePersonCommand toSavePersonCommand(SourceId sourceId, SavePersonRequest request);
  
  UpdatePersonCommand toUpdatePersonCommand(String publicId, UpdatePersonRequest request);
  
  default <T> Optional<T> toOptional(T value) {
    return Optional.ofNullable(value);
  }
}