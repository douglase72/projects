package com.erdouglass.emdb.media.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import com.erdouglass.emdb.media.entity.Movie;
import com.erdouglass.emdb.media.proto.v1.MovieResponse;
import com.erdouglass.emdb.media.proto.v1.SaveMovieRequest;

@Mapper(
    componentModel = "cdi", 
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface MovieMapper {
  
  Movie toMovie(SaveMovieRequest request);
  
  MovieResponse toMovieDto(Movie movie);
  
}
