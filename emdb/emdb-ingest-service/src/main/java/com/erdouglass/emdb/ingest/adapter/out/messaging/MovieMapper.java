package com.erdouglass.emdb.ingest.adapter.out.messaging;

import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.ReportingPolicy;

import com.erdouglass.emdb.ingest.application.port.out.Movie;
import com.erdouglass.emdb.media.SaveMovieCommand;

@Mapper(
    componentModel = "cdi", 
    collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
    unmappedTargetPolicy = ReportingPolicy.ERROR,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface MovieMapper {

  SaveMovieCommand toSaveMovieCommand(Movie moive);
}
