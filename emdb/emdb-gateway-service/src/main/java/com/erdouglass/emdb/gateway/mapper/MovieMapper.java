package com.erdouglass.emdb.gateway.mapper;

import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import com.erdouglass.emdb.common.comand.SaveMovie;
import com.erdouglass.emdb.common.comand.UpdateMovie;
import com.erdouglass.emdb.common.query.MovieDetails;
import com.erdouglass.emdb.common.query.MovieView;
import com.erdouglass.emdb.gateway.query.MovieQueryParams;
import com.erdouglass.emdb.gateway.query.Page;
import com.erdouglass.emdb.media.proto.v1.FindAllMovieRequest;
import com.erdouglass.emdb.media.proto.v1.FindMovieRequest;
import com.erdouglass.emdb.media.proto.v1.MovieResponse;
import com.erdouglass.emdb.media.proto.v1.PageResponse;
import com.erdouglass.emdb.media.proto.v1.SaveMovieRequest;
import com.erdouglass.emdb.media.proto.v1.UpdateMovieRequest;

@Mapper(
    componentModel = "cdi", 
    collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS 
)
public interface MovieMapper extends CommonMapper {
  
  SaveMovieRequest toSaveMovieRequest(SaveMovie command);
  
  FindMovieRequest toFindMovieRequest(Long id, String append);
  
  @ImageMapping
  MovieDetails toMovieDetails(MovieResponse response);
  
  FindAllMovieRequest toFindAllMovieRequest(MovieQueryParams parameters);
  
  Page<MovieView> toPage(PageResponse response);
  
  @Mapping(target = "id", source = "id")
  @Mapping(target = "command", source = "command")
  UpdateMovieRequest toUpdateMovieRequest(Long id, UpdateMovie command);
}
