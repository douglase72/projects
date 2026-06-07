package com.erdouglass.emdb.media.person;

import java.util.List;
import java.util.UUID;

import jakarta.data.repository.CrudRepository;
import jakarta.data.repository.Query;
import jakarta.data.repository.Repository;

import com.erdouglass.emdb.media.credit.Credit;

@Repository
interface CreditRepository extends CrudRepository<Credit, UUID> {
  
  @Query("""
      SELECT c FROM Credit c
      JOIN FETCH c.person
      LEFT JOIN FETCH TREAT(c AS SeriesCredit).roles
      WHERE c.person.id = :personId
      ORDER BY c.order
      """)
  List<Credit> findByPersonId(Long personId);  
}
