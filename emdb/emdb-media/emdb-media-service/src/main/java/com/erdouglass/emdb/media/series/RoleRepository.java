package com.erdouglass.emdb.media.series;

import java.util.UUID;

import jakarta.data.repository.CrudRepository;
import jakarta.data.repository.Query;
import jakarta.data.repository.Repository;

@Repository
interface RoleRepository extends CrudRepository<Role, UUID> {

  @Query("""
    DELETE FROM Role r WHERE r.seriesCredit in 
    (SELECT sc FROM SeriesCredit sc WHERE sc.series = :series)
         """)
  void deleteBySeries(Series series);
}
