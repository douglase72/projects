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

/// Domain service for ingest persistence and lookup.
///
/// Owns the upsert logic that keeps the [Ingest] projection in sync with
/// the latest status event while also appending to [IngestStatusChange]
/// for the audit trail. Both writes happen in a single transaction so the
/// projection and history can never disagree.
@ApplicationScoped
public class IngestService {
  
  @Inject
  IngestMapper mapper;
  
  @Inject
  IngestRepository repository;
  
  @Inject
  IngestStatusChangeRepository statusRepository;
  
  /// Upserts the projection and appends a history row for the given event.
  ///
  /// If an [Ingest] with the same ID already exists, its mutable fields are
  /// merged from the incoming value and the row is updated; otherwise a new
  /// row is inserted. A new [IngestStatusChange] is always appended.
  ///
  /// @param ingest the ingest projection materialized from the inbound event
  /// @return the persisted ingest (newly inserted or updated)  
  @Transactional
  public Ingest save(@NotNull @Valid Ingest ingest) {
    var existingIngest = repository.findById(ingest.getId())
        .map(i -> { mapper.merge(ingest, i); return repository.update(i); })
        .orElseGet(() -> repository.insert(ingest));
    var ingestStatusChange = mapper.toIngestStatusChange(existingIngest);
    statusRepository.insert(ingestStatusChange);
    return existingIngest;
  }
  
  /// Returns a page of ingests sorted by last-modified descending.
  ///
  /// @param page one-based page number
  /// @param size page size
  /// @return the requested page of ingests  
  @Transactional
  public Page<Ingest> findAll(@NotNull @Positive Integer page, @NotNull @Positive Integer size) {
    var pageRequest = PageRequest.ofPage(page, size, true);
    var order = Order.by(_Ingest.lastModified.desc());
    var results = repository.findAll(pageRequest, order);
    return results;
  }
  
  /// Returns a single ingest by ID, if it exists.  
  @Transactional
  public Optional<Ingest> findById(@NotNull UUID id) {
    return repository.findById(id);
  }
  
  /// Returns the full status-change history for an ingest, oldest first.  
  @Transactional
  public List<IngestStatusChange> findStatusChanges(@NotNull UUID id) {
    return statusRepository.findByIngestId(id);
  }
}
