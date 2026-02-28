package com.erdouglass.emdb.media.service;

import java.util.UUID;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import com.erdouglass.emdb.common.comand.UpdateRole;
import com.erdouglass.emdb.common.query.RoleDto;
import com.erdouglass.emdb.media.repository.RoleRepository;
import com.erdouglass.webservices.ResourceNotFoundException;

@ApplicationScoped
public class RoleService {
  
  @Inject
  RoleRepository repository;
  
  @Transactional
  public RoleDto update(UUID id, UpdateRole command) {
    var role = repository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("No role found with id: " + id));
    role.role(command.role());
    role.episodeCount(command.episodeCount());
    repository.update(role);
    return RoleDto.of(role.id(), role.role(), role.episodeCount());
  }

}
