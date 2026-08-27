package com.erdouglass.emdb.media.movie.adapter.in.rest;

import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import com.erdouglass.emdb.media.kernel.SourceId;
import com.erdouglass.emdb.media.movie.application.port.in.SaveMovieCommandRecord;

@Mapper(
    componentModel = "cdi", 
    collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
    unmappedTargetPolicy = ReportingPolicy.ERROR
)
interface CommandMapper {

  SaveMovieCommandRecord toSaveMovieCommand(SourceId sourceId, SaveMovieRequest request);
}
