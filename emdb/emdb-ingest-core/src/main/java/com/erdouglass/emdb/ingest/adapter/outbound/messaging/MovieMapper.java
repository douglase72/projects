package com.erdouglass.emdb.ingest.adapter.outbound.messaging;

import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.ReportingPolicy;

import com.erdouglass.emdb.ingest.application.port.outbound.Movie;
import com.erdouglass.emdb.media.MovieExtractedEvent;

@Mapper(
    componentModel = "cdi", 
    collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
    unmappedTargetPolicy = ReportingPolicy.ERROR,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
interface MovieMapper {

  MovieExtractedEvent toMovieExtractedEvent(Movie movie);
}
