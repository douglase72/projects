package com.erdouglass.emdb.gateway.mapper;

import java.util.UUID;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.ValueMapping;

import com.erdouglass.emdb.common.Gender;
import com.erdouglass.emdb.common.comand.UpdateMovieCredit;
import com.erdouglass.emdb.common.query.CreditType;
import com.erdouglass.emdb.common.query.MovieCreditDto;
import com.erdouglass.emdb.media.proto.v1.UniMovieCreditResponse;
import com.erdouglass.emdb.media.proto.v1.UpdateMovieCreditRequest;

@Mapper(
    componentModel = "cdi", 
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS 
)
public interface MovieCreditMapper extends CommonMapper {
  
  @ValueMapping(source = "GENDER_UNSPECIFIED", target = MappingConstants.NULL)
  @ValueMapping(source = "UNRECOGNIZED", target = MappingConstants.NULL)
  Gender mapGender(com.erdouglass.emdb.media.proto.v1.Gender gender);
  
  UpdateMovieCreditRequest toUpdateMovieCreditRequest(Long movieId, UUID creditId, UpdateMovieCredit command);
  
  default MovieCreditDto toMovieCreditDto(UniMovieCreditResponse response) {
    if (response == null) return null;
    var type = response.getType();
    return new MovieCreditDto(
        UUID.fromString(response.getCreditId()),
        response.getId(),
        response.getName(),
        mapGender(response.getGender()),
        response.hasProfile() ? response.getProfile() + ".jpg" : null,
        type == com.erdouglass.emdb.media.proto.v1.CreditType.CAST ? response.getRole() : null,
        type == com.erdouglass.emdb.media.proto.v1.CreditType.CREW ? response.getRole() : null,
        response.hasOrder() ? response.getOrder() : null,
        CreditType.from(type.name()));
  }

}
