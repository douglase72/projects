package com.erdouglass.emdb.gateway.mapper;

import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import com.erdouglass.emdb.common.comand.SaveSeries;
import com.erdouglass.emdb.common.comand.UpdateSeries;
import com.erdouglass.emdb.common.query.SeriesDto;
import com.erdouglass.emdb.common.query.SeriesDto.CastCredit;
import com.erdouglass.emdb.common.query.SeriesDto.CrewCredit;
import com.erdouglass.emdb.media.proto.v1.FindSeriesRequest;
import com.erdouglass.emdb.media.proto.v1.SaveSeriesRequest;
import com.erdouglass.emdb.media.proto.v1.SeriesCastCreditResponse;
import com.erdouglass.emdb.media.proto.v1.SeriesCrewCreditResponse;
import com.erdouglass.emdb.media.proto.v1.SeriesResponse;
import com.erdouglass.emdb.media.proto.v1.UpdateSeriesRequest;

@Mapper(
    componentModel = "cdi", 
    collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS 
)
public interface SeriesMapper extends CommonMapper {
  
  SaveSeriesRequest toSaveSeriesRequest(SaveSeries command);
  
  FindSeriesRequest toFindSeriesRequest(Long id, String append);
  
  @Mapping(target = "id", source = "id")
  @Mapping(target = "command", source = "command")
  UpdateSeriesRequest toUpdateSeriesRequest(Long id, UpdateSeries command);
  
  @Mapping(target = "profile", expression = "java(credit.hasProfile() ? credit.getProfile() + \".jpg\" : null)")
  CastCredit toSeriesCastCredit(SeriesCastCreditResponse credit);

  @Mapping(target = "profile", expression = "java(credit.hasProfile() ? credit.getProfile() + \".jpg\" : null)")
  CrewCredit toSeriesCrewCredit(SeriesCrewCreditResponse credit);

  @Mapping(target = "backdrop", expression = "java(response.hasBackdrop() ? response.getBackdrop() + \".jpg\" : null)")
  @Mapping(target = "poster", expression = "java(response.hasPoster() ? response.getPoster() + \".jpg\" : null)")
  SeriesDto toSeriesDto(SeriesResponse response);
  
}
