package com.erdouglass.emdb.gateway.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.ValueMapping;

import com.erdouglass.emdb.common.ShowStatus;
import com.erdouglass.emdb.common.comand.SaveMovie;
import com.erdouglass.emdb.common.comand.UpdateMovie;
import com.erdouglass.emdb.common.query.MovieDto;
import com.erdouglass.emdb.media.proto.v1.FindMovieRequest;
import com.erdouglass.emdb.media.proto.v1.MovieResponse;
import com.erdouglass.emdb.media.proto.v1.SaveMovieRequest;
import com.erdouglass.emdb.media.proto.v1.UpdateMovieRequest;

@Mapper(
    componentModel = "cdi", 
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS 
)
public interface MovieMapper extends CommonMapper {

  SaveMovieRequest toSaveMovieRequest(SaveMovie command);
  
  FindMovieRequest toFindMovieRequest(Long id, String append);
  
  @Mapping(target = "id", source = "id")
  @Mapping(target = "command", source = "command")
  UpdateMovieRequest toUpdateMovieRequest(Long id, UpdateMovie command);

  @Mapping(target = "backdrop", expression = "java(response.hasBackdrop() ? response.getBackdrop() + \".jpg\" : null)")
  @Mapping(target = "poster", expression = "java(response.hasPoster() ? response.getPoster() + \".jpg\" : null)")
  @BeanMapping(builder = @Builder(disableBuilder = true))  
  MovieDto toMovieDto(MovieResponse response);
  
  @ValueMapping(source = "SHOW_STATUS_UNSPECIFIED", target = MappingConstants.NULL)
  @ValueMapping(source = "UNRECOGNIZED", target = MappingConstants.NULL)
  ShowStatus mapProtoToJava(com.erdouglass.emdb.media.proto.v1.ShowStatus protoStatus);
  
  @ValueMapping(source = MappingConstants.NULL, target = "SHOW_STATUS_UNSPECIFIED")
  com.erdouglass.emdb.media.proto.v1.ShowStatus mapJavaToProto(ShowStatus javaStatus);
}
