package com.erdouglass.emdb.job.repository;

import java.util.UUID;

import jakarta.data.repository.CrudRepository;
import jakarta.data.repository.Repository;

import com.erdouglass.emdb.job.entity.IngestJob;

@Repository
public interface IngestJobRepository extends CrudRepository<IngestJob, UUID> {
  
}
