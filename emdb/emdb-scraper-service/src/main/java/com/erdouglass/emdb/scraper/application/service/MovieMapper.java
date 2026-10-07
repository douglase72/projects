package com.erdouglass.emdb.scraper.application.service;

import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.ReportingPolicy;

import com.erdouglass.common.messaging.MessageId;
import com.erdouglass.emdb.scraper.domain.model.Movie;
import com.erdouglass.emdb.scraper.messaging.MovieExtracted;
import com.erdouglass.emdb.shared.kernel.PublicId;

@Mapper(
    componentModel = "cdi", 
    collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
    unmappedTargetPolicy = ReportingPolicy.ERROR,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
interface MovieMapper {
  
  MovieExtracted toMovieExtracted(MessageId messageId, PublicId correlationId, Movie movie);
}
