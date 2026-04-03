package com.erdouglass.emdb.gateway.mapper;

import java.util.UUID;

import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import com.erdouglass.emdb.common.comand.UpdateRole;
import com.erdouglass.emdb.common.comand.UpdateSeriesCredit;
import com.erdouglass.emdb.common.query.RoleDto;
import com.erdouglass.emdb.common.query.SeriesCreditDto;
import com.erdouglass.emdb.media.proto.v1.UpdateRoleRequest;
import com.erdouglass.emdb.media.proto.v1.UpdateRoleResponse;
import com.erdouglass.emdb.media.proto.v1.UpdateSeriesCreditRequest;
import com.erdouglass.emdb.media.proto.v1.UpdateSeriesCreditResponse;

@Mapper(
    componentModel = "cdi", 
    collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS 
)
public interface SeriesCreditMapper {
  
  UpdateSeriesCreditRequest toUpdateSeriesCreditRequest(Long seriesId, UUID creditId, UpdateSeriesCredit command);
  
  SeriesCreditDto toSeriesCreditDto(UpdateSeriesCreditResponse response);
  
  UpdateRoleRequest toUpdateRoleRequest(Long seriesId, UUID creditId, UUID roleId, UpdateRole command);
    
  RoleDto toRoleDto(UpdateRoleResponse role);

}
