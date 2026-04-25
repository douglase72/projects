package com.erdouglass.emdb.gateway.mapper;

import java.util.UUID;

import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import com.erdouglass.emdb.common.event.IngestStatusChanged;
import com.erdouglass.emdb.gateway.query.IngestHistory;
import com.erdouglass.emdb.gateway.query.OffsetPage;
import com.erdouglass.emdb.notification.proto.v1.FindAllRequest;
import com.erdouglass.emdb.notification.proto.v1.FindByIdRequest;
import com.erdouglass.emdb.notification.proto.v1.FindHistoryRequest;
import com.erdouglass.emdb.notification.proto.v1.FindHistoryResponse;
import com.erdouglass.emdb.notification.proto.v1.IngestResponse;
import com.erdouglass.emdb.notification.proto.v1.PageResponse;

@Mapper(
    componentModel = "cdi", 
    collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
    unmappedTargetPolicy = ReportingPolicy.ERROR,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS 
)
public interface IngestMapper {

  FindAllRequest toFindAllRequest(Integer page, Integer size);
  
  FindByIdRequest toFindByIdRequest(UUID id);
  
  OffsetPage<IngestStatusChanged> toOffsetPage(PageResponse response);
  
  IngestStatusChanged toIngestStatusChanged(IngestResponse response);
  
  FindHistoryRequest toFindHistoryRequest(UUID id);
  
  IngestHistory toIngestHistory(FindHistoryResponse response);
}
