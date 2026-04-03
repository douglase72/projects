package com.erdouglass.emdb.media.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import org.jboss.logging.Logger;

import com.erdouglass.emdb.common.comand.SaveSeries.Credits;
import com.erdouglass.emdb.common.comand.UpdateRole;
import com.erdouglass.emdb.common.comand.UpdateSeriesCredit;
import com.erdouglass.emdb.media.entity.Person;
import com.erdouglass.emdb.media.entity.Role;
import com.erdouglass.emdb.media.entity.Series;
import com.erdouglass.emdb.media.entity.SeriesCredit;
import com.erdouglass.emdb.media.mapper.SeriesCreditMapper;
import com.erdouglass.emdb.media.repository.RoleRepository;
import com.erdouglass.emdb.media.repository.SeriesCreditRepository;
import com.erdouglass.webservices.ResourceNotFoundException;

@ApplicationScoped
public class SeriesCreditService {
  private static final Logger LOGGER = Logger.getLogger(SeriesCreditService.class);
  
  @Inject
  SeriesCreditMapper mapper;
  
  @Inject
  SeriesCreditRepository repository;
  
  @Inject
  RoleRepository roleRepository;
  
  @Transactional
  public boolean saveAll(Series series, Map<Integer, Person> people, Credits credits) {
    List<SeriesCredit> creditsToInsert = new ArrayList<>();
    List<SeriesCredit> creditsToUpdate = new ArrayList<>();
    List<Role> rolesToInsert = new ArrayList<>();
    List<UUID> rolesToDelete = new ArrayList<>();
    var existingCredits = repository.findBySeriesId(series.getId()).stream()
        .collect(Collectors.toMap(c -> c.getPerson().getTmdbId() + "-" + c.getType(), Function.identity()));
    
    List<SeriesCredit> allCredits = new ArrayList<>(); 
    for (var credit : credits.cast()) {
      var person = Optional.ofNullable(people.get(credit.tmdbId()))
          .orElseThrow(() -> new ResourceNotFoundException("Person not found: " + credit.tmdbId())); 
      allCredits.add(mapper.toSeriesCredit(series, person, credit));
    }
    
    for (var credit : credits.crew()) {
      var person = Optional.ofNullable(people.get(credit.tmdbId()))
          .orElseThrow(() -> new ResourceNotFoundException("Person not found: " + credit.tmdbId())); 
      allCredits.add(mapper.toSeriesCredit(series, person, credit));
    }
    
    for (var credit : allCredits) {
      var key = credit.getPerson().getTmdbId() + "-" + credit.getType();
      var existingCredit = existingCredits.remove(key);
      if (existingCredit == null) {
        rolesToInsert.addAll(credit.getRoles());
        creditsToInsert.add(credit);
      } else if (!existingCredit.isEqualTo(credit)) {
        existingCredit.getRoles().forEach(r -> rolesToDelete.add(r.getId()));
        mapper.merge(credit, existingCredit);
        for (var role : existingCredit.getRoles()) {
          role.setSeriesCredit(existingCredit);
        }
        rolesToInsert.addAll(existingCredit.getRoles());
        creditsToUpdate.add(existingCredit);
      }
    }
    
    if (!rolesToDelete.isEmpty()) {
      int count = roleRepository.deleteIn(rolesToDelete);
      LOGGER.infof("Deleted: %d roles.", count);
    }
    
    if (!existingCredits.isEmpty()) {
      var orphanedRoleIds = existingCredits.values().stream()
          .flatMap(c -> c.getRoles().stream())
          .map(Role::getId)
          .toList();      
      if (!orphanedRoleIds.isEmpty()) {
        roleRepository.deleteIn(orphanedRoleIds);
      }      
      repository.deleteAll(new ArrayList<>(existingCredits.values()));
      LOGGER.infof("Deleted: %d series credits.", existingCredits.size());
    }
    
    if (!creditsToInsert.isEmpty()) {
      var insertedCredits = repository.insertAll(creditsToInsert);
      LOGGER.infof("Inserted: %d series credits", insertedCredits.size());
    }
    
    if (!creditsToUpdate.isEmpty()) {
      var updatedCredits = repository.updateAll(creditsToUpdate);
      LOGGER.infof("Updated: %d series credits", updatedCredits.size());
    }
    
    if (!rolesToInsert.isEmpty()) {
      var insertedRoles = roleRepository.insertAll(rolesToInsert);
      LOGGER.infof("Inserted: %d roles", insertedRoles.size());
    }     
    return !creditsToInsert.isEmpty() || !creditsToUpdate.isEmpty() || !existingCredits.isEmpty();
  }
  
  @Transactional
  public List<SeriesCredit> findBySeriesId(Long id) {
    return repository.findBySeriesId(id);
  }
  
  @Transactional
  public SeriesCredit update(Long id, UUID creditId, UpdateSeriesCredit command) {
    var credit = repository.findById(creditId)
        .orElseThrow(() -> new ResourceNotFoundException("No credit found with id: " + creditId));
    if (!credit.getId().equals(creditId)) {
      throw new IllegalArgumentException("Credit mismatch: " + creditId);
    }
    mapper.merge(command, credit);
    return repository.update(credit);
  }
  
  @Transactional
  public Role updateRole(Long id, UUID creditId, UUID roleId, UpdateRole command) {
    var role = roleRepository.findById(roleId)
        .orElseThrow(() -> new ResourceNotFoundException("No role found with id: " + roleId));
    if (!role.getId().equals(roleId) || !role.getSeriesCredit().getId().equals(creditId)) {
      throw new IllegalArgumentException("Role mismatch: " + roleId);
    }
    mapper.merge(command, role);
    return roleRepository.update(role);
  }
  
}
