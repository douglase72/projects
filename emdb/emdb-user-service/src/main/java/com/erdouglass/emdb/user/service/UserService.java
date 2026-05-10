package com.erdouglass.emdb.user.service;

import java.util.Optional;
import java.util.UUID;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import com.erdouglass.emdb.common.api.logging.Log;
import com.erdouglass.emdb.user.api.command.UpdateUser;
import com.erdouglass.emdb.user.entity.User;
import com.erdouglass.emdb.user.mapper.UserMapper;
import com.erdouglass.emdb.user.repository.UserRepository;
import com.erdouglass.webservices.ResourceNotFoundException;

@ApplicationScoped
public class UserService {
  
  @Inject
  UserMapper mapper;
  
  @Inject
  UserRepository repository;
  
  @Transactional
  @Log("Created:")
  public User create(@NotNull @Valid User user) {
    return repository.insert(user);
  }
  
  @Transactional
  public Optional<User> findById(@NotNull UUID id) {
    return repository.findById(id);
  }
  
  @Transactional
  @Log("Updated:")
  public User update(UUID id, UpdateUser command) {
    var existingUser = repository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("No user found with id: " + id));
    mapper.merge(command, existingUser);
    return repository.update(existingUser);
  }  
}
