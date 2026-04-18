package com.erdouglass.emdb.notification.mapper;

import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import com.erdouglass.emdb.common.event.IngestStatusChanged;
import com.erdouglass.emdb.notification.entity.Ingest;
import com.erdouglass.emdb.notification.entity.StatusEvent;

@Mapper(
    componentModel = "cdi", 
    collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
    unmappedTargetPolicy = ReportingPolicy.ERROR,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface IngestMapper {
  
  void merge(IngestStatusChanged event, @MappingTarget Ingest entity);

  Ingest toIngest(IngestStatusChanged event);
  
  @Mapping(source = "lastModified", target = "created")
  @Mapping(target = "ingest", ignore = true)
  StatusEvent toStatusEvent(IngestStatusChanged event);
}
