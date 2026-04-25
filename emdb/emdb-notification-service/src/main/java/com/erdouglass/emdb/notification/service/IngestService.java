package com.erdouglass.emdb.notification.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import jakarta.data.Order;
import jakarta.data.page.Page;
import jakarta.data.page.PageRequest;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import com.erdouglass.emdb.notification.entity.Ingest;
import com.erdouglass.emdb.notification.entity.IngestStatusChange;
import com.erdouglass.emdb.notification.entity._Ingest;
import com.erdouglass.emdb.notification.mapper.IngestMapper;
import com.erdouglass.emdb.notification.repository.IngestRepository;
import com.erdouglass.emdb.notification.repository.IngestStatusChangeRepository;

@ApplicationScoped
public class IngestService {
  
  @Inject
  IngestMapper mapper;
  
  @Inject
  IngestRepository repository;
  
  @Inject
  IngestStatusChangeRepository statusRepository;
  
  @Transactional
  public Ingest save(@NotNull @Valid Ingest ingest) {
    var existingIngest = repository.findById(ingest.getId())
        .map(i -> { mapper.merge(ingest, i); return repository.update(i); })
        .orElseGet(() -> repository.insert(ingest));
    var ingestStatusChange = mapper.toIngestStatusChange(existingIngest);
    statusRepository.insert(ingestStatusChange);
    return existingIngest;
  }
  
  @Transactional
  public Page<Ingest> findAll(@NotNull @Positive Integer page, @NotNull @Positive Integer size) {
    var pageRequest = PageRequest.ofPage(page, size, true);
    var order = Order.by(_Ingest.lastModified.desc());
    var results = repository.findAll(pageRequest, order);
    return results;
  }
  
  @Transactional
  public Optional<Ingest> findById(@NotNull UUID id) {
    return repository.findById(id);
  }
  
  @Transactional
  public List<IngestStatusChange> findStatusChanges(@NotNull UUID id) {
    return statusRepository.findByIngestId(id);
  }
}
