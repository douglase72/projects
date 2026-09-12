package com.erdouglass.emdb.media.movie.adapter.in.rest;

import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.ReportingPolicy;

import com.erdouglass.emdb.media.SaveMovieCommand;

@Mapper(
    componentModel = "cdi", 
    collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
    unmappedTargetPolicy = ReportingPolicy.ERROR,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface CommandMapper {

  SaveMovieCommand toSaveMovieCommand(Integer tmdbId, SaveMovieRequest request);
}
