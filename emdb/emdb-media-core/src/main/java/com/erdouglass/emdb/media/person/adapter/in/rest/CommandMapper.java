package com.erdouglass.emdb.media.person.adapter.in.rest;

import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.ReportingPolicy;

import com.erdouglass.emdb.media.TmdbId;
import com.erdouglass.emdb.media.person.SavePersonCommand;

@Mapper(
    componentModel = "cdi", 
    collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
    unmappedTargetPolicy = ReportingPolicy.ERROR,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
interface CommandMapper {

  SavePersonCommand toSavePersonCommand(TmdbId tmdbId, SavePersonRequest request);
}
