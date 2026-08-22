package com.erdouglass.emdb.media.movie.adapter.in.graphql;

import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.ReportingPolicy;

import com.erdouglass.emdb.media.movie.application.port.out.MovieView;
import com.erdouglass.emdb.media.movie.domain.MoviePublicId;

@Mapper(
    componentModel = "cdi", 
    collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
    unmappedTargetPolicy = ReportingPolicy.ERROR,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
interface MovieMapper {

  @Mapping(target = "id", source = "id", qualifiedByName = "toPublicId")
  MovieResponse toMovieResponse(MovieView view);
  
  @Named("toPublicId")
  default String toPublicId(Long id) {
    return MoviePublicId.from(id).value();
  }
}
