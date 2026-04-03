package com.erdouglass.emdb.media.repository;

import java.util.List;
import java.util.UUID;

import jakarta.data.repository.CrudRepository;
import jakarta.data.repository.Query;
import jakarta.data.repository.Repository;

import com.erdouglass.emdb.media.entity.Role;

@Repository
public interface RoleRepository extends CrudRepository<Role, UUID> {

  @Query("DELETE FROM Role r WHERE r.id IN :ids")
  int deleteIn(List<UUID> ids);
  
}
