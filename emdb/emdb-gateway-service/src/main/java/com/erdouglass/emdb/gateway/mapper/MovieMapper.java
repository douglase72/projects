package com.erdouglass.emdb.gateway.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Builder;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import com.erdouglass.emdb.common.comand.SaveMovie;
import com.erdouglass.emdb.common.comand.SavePerson;
import com.erdouglass.emdb.common.comand.UpdateMovie;
import com.erdouglass.emdb.common.query.MovieDto;
import com.erdouglass.emdb.media.proto.v1.FindMovieRequest;
import com.erdouglass.emdb.media.proto.v1.MovieResponse;
import com.erdouglass.emdb.media.proto.v1.SaveMovieRequest;
import com.erdouglass.emdb.media.proto.v1.SavePersonRequest;
import com.erdouglass.emdb.media.proto.v1.UpdateMovieRequest;

@Mapper(
    componentModel = "cdi", 
    collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS 
)
public interface MovieMapper extends CommonMapper {
  
  SavePersonRequest toSavePersonRequest(SavePerson person);

  SaveMovieRequest toSaveMovieRequest(SaveMovie command);
  
  FindMovieRequest toFindMovieRequest(Long id, String append);
  
  @Mapping(target = "id", source = "id")
  @Mapping(target = "command", source = "command")
  UpdateMovieRequest toUpdateMovieRequest(Long id, UpdateMovie command);

  @Mapping(target = "backdrop", expression = "java(response.hasBackdrop() ? response.getBackdrop() + \".jpg\" : null)")
  @Mapping(target = "poster", expression = "java(response.hasPoster() ? response.getPoster() + \".jpg\" : null)")
  @BeanMapping(builder = @Builder(disableBuilder = true))  
  MovieDto toMovieDto(MovieResponse response);
  
}
