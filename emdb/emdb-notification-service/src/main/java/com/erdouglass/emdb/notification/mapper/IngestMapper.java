package com.erdouglass.emdb.notification.mapper;

import java.util.List;

import jakarta.data.page.Page;

import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import com.erdouglass.emdb.common.event.IngestStatusChanged;
import com.erdouglass.emdb.notification.entity.Ingest;
import com.erdouglass.emdb.notification.entity.IngestStatusChange;
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
  
  void merge(Ingest source, @MappingTarget Ingest target);

  Ingest toIngest(IngestStatusChanged event);
  
  @Mapping(source = "ingest", target = "ingest")
  IngestStatusChange toIngestStatusChange(Ingest ingest);
  
  IngestResponse toIngestResponse(Ingest ingest);
  
  default PageResponse toPageResponse(Page<Ingest> page) {
    return PageResponse.newBuilder()
        .addAllResults(page.content().stream().map(this::toIngestResponse).toList())
        .setPage((int) page.pageRequest().page())
        .setSize(page.pageRequest().size())
        .setTotalResults(page.totalElements())
        .build();    
  }
  
  FindHistoryResponse toFindHistoryResponse(String id, List<IngestStatusChange> changes);
}
