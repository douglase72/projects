package com.erdouglass.emdb.scraper.adapter.out.messaging;

import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.ReportingPolicy;

import com.erdouglass.emdb.scraper.domain.model.Movie;
import com.erdouglass.emdb.scraper.messaging.MovieScrapedMessage;

@Mapper(
    componentModel = "cdi", 
    collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
    unmappedTargetPolicy = ReportingPolicy.ERROR,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
interface MovieMapper {

  MovieScrapedMessage toMovieScraped(Movie movie);
}
