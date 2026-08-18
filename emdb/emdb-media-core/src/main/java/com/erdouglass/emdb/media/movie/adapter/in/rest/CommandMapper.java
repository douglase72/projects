package com.erdouglass.emdb.media.movie.adapter.in.rest;

import java.util.Optional;

import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import com.erdouglass.emdb.media.TmdbId;
import com.erdouglass.emdb.media.movie.SaveMovieCommand;
import com.erdouglass.emdb.media.movie.application.port.in.UpdateMovieCommand;

@Mapper(
    componentModel = "cdi", 
    collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
    unmappedTargetPolicy = ReportingPolicy.ERROR
)
interface CommandMapper {

  SaveMovieCommand toSaveMovieCommand(TmdbId tmdbId, SaveMovieRequest request);
  
  UpdateMovieCommand toUpdateMovieCommand(String publicId, UpdateMovieRequest request);
  
  default <T> Optional<T> toOptional(T value) {
    return Optional.ofNullable(value);
  }
}
