package com.erdouglass.emdb.gateway.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import com.erdouglass.emdb.common.comand.SaveMovie;
import com.erdouglass.emdb.common.query.MovieDto;
import com.erdouglass.emdb.media.proto.v1.MovieResponse;
import com.erdouglass.emdb.media.proto.v1.SaveMovieRequest;

@Mapper(
    componentModel = "cdi", 
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface MovieMapper {

  SaveMovieRequest toSaveMovieRequest(SaveMovie command);
  
  @BeanMapping(builder = @Builder(disableBuilder = true))
  MovieDto toMovieDto(MovieResponse response);
}
