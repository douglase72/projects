package com.erdouglass.emdb.media.repository;

import java.util.List;
import java.util.UUID;

import jakarta.data.repository.CrudRepository;
import jakarta.data.repository.Query;
import jakarta.data.repository.Repository;

import com.erdouglass.emdb.media.entity.Credit;

@Repository
public interface CreditRepository extends CrudRepository<Credit, UUID> {

  @Query("""
      FROM Credit c LEFT JOIN FETCH c.person p 
                    LEFT JOIN FETCH c.movie m 
                    LEFT JOIN FETCH c.series s 
                    LEFT JOIN FETCH c.roles
      WHERE p.id = :id 
      ORDER BY COALESCE(m.score, s.score) DESC, c.created
      """)
  List<Credit> findByPersonId(Long id);
  
}
