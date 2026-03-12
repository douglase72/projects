package com.erdouglass.emdb.media.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.ValueMapping;

import com.erdouglass.emdb.common.ShowStatus;
import com.erdouglass.emdb.common.comand.SaveMovie;
import com.erdouglass.emdb.media.entity.Movie;
import com.erdouglass.emdb.media.proto.v1.MovieResponse;
import com.erdouglass.emdb.media.proto.v1.SaveMovieRequest;

@Mapper(
    componentModel = "cdi", 
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface MovieMapper {
  
  void merge(SaveMovie command, @MappingTarget Movie movie);
  
  @BeanMapping(builder = @Builder(disableBuilder = true))
  SaveMovie toSaveMovie(SaveMovieRequest request);
  
  MovieResponse toMovieResponse(Movie movie);
  
  Movie toMovie(SaveMovie command);
  
  @ValueMapping(source = "SHOW_STATUS_UNSPECIFIED", target = MappingConstants.NULL)
  @ValueMapping(source = "UNRECOGNIZED", target = MappingConstants.NULL)
  ShowStatus mapProtoToJava(com.erdouglass.emdb.media.proto.v1.ShowStatus protoStatus);
  
  @ValueMapping(source = MappingConstants.NULL, target = "SHOW_STATUS_UNSPECIFIED")
  com.erdouglass.emdb.media.proto.v1.ShowStatus mapJavaToProto(ShowStatus javaStatus);
}
