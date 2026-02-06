package com.erdouglass.emdb.job.repository;

import java.util.List;

import jakarta.data.repository.Find;
import jakarta.data.repository.Repository;
import jakarta.data.repository.Save;

import com.erdouglass.emdb.job.entity.IngestJob;

@Repository
public interface IngestJobRepository {
  
  @Find
  List<IngestJob> findAll();
  
  @Save
  IngestJob save(IngestJob job);
  
}
