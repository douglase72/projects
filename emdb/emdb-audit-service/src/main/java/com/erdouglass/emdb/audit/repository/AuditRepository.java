package com.erdouglass.emdb.audit.repository;

import java.util.List;

import jakarta.data.repository.Insert;
import jakarta.data.repository.Query;
import jakarta.data.repository.Repository;

import com.erdouglass.emdb.audit.entity.AuditLog;

@Repository
public interface AuditRepository {

  @Insert
  AuditLog insert(AuditLog log);
  
  @Query("WHERE traceId = :traceId ORDER BY timestamp")
  List<AuditLog> findByTraceId(String traceId);
  
}
