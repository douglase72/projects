package com.erdouglass.emdb.ingest.adapter.out.media;

import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.ReportingPolicy;

import com.erdouglass.emdb.ingest.application.dto.Movie;
import com.erdouglass.emdb.ingest.application.dto.Person;
import com.erdouglass.emdb.media.movie.SaveMovieCommand;
import com.erdouglass.emdb.media.person.SavePersonCommand;

@Mapper(
    componentModel = "cdi", 
    collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
    unmappedTargetPolicy = ReportingPolicy.ERROR,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
interface MediaMapper {

  SaveMovieCommand toSaveMovieCommand(Movie movie);
  
  SavePersonCommand toSavePersonCommand(Person person);
}
