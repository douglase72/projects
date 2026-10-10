package com.erdouglass.emdb.scraper.adapter.out.persistence;

import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "scraper_command")
class CommandEntity {

  @Id
  private UUID id;
 
  CommandEntity() { }
}
