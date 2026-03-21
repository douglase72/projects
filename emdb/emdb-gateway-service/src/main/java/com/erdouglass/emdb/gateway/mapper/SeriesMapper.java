package com.erdouglass.emdb.gateway.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Builder;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import com.erdouglass.emdb.common.comand.SaveSeries;
import com.erdouglass.emdb.common.query.SeriesDto;
import com.erdouglass.emdb.media.proto.v1.SaveSeriesRequest;
import com.erdouglass.emdb.media.proto.v1.SeriesResponse;

@Mapper(
    componentModel = "cdi", 
    collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS 
)
public interface SeriesMapper extends CommonMapper {
  
  SaveSeriesRequest toSaveSeriesRequest(SaveSeries command);
  
  @Mapping(target = "backdrop", expression = "java(response.hasBackdrop() ? response.getBackdrop() + \".jpg\" : null)")
  @Mapping(target = "poster", expression = "java(response.hasPoster() ? response.getPoster() + \".jpg\" : null)")
  @BeanMapping(builder = @Builder(disableBuilder = true))
  SeriesDto toSeriesDto(SeriesResponse response);
  
}
