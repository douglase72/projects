package com.erdouglass.emdb.job.repository;

import java.util.List;

import jakarta.data.repository.Insert;
import jakarta.data.repository.Query;
import jakarta.data.repository.Repository;

import com.erdouglass.emdb.job.entity.JobLog;

@Repository
public interface JobRepository {

  @Insert
  JobLog insert(JobLog log);
  
  @Query("WHERE jobId = :jobId ORDER BY timestamp")
  List<JobLog> findByJobId(String jobId);
  
}
