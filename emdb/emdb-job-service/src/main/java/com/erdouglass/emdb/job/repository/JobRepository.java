package com.erdouglass.emdb.job.repository;

import java.util.List;
import java.util.UUID;

import jakarta.data.repository.Find;
import jakarta.data.repository.Insert;
import jakarta.data.repository.OrderBy;
import jakarta.data.repository.Query;
import jakarta.data.repository.Repository;

import com.erdouglass.emdb.job.entity.JobLog;

@Repository
public interface JobRepository {

  @Insert
  JobLog insert(JobLog log);
  
  @Find
  @OrderBy("timestamp")
  List<JobLog> findAll();
  
  @Query("WHERE jobId = :jobId ORDER BY timestamp")
  List<JobLog> findById(UUID jobId);
  
}
