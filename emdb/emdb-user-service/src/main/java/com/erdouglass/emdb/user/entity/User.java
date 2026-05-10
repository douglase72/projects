package com.erdouglass.emdb.user.entity;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import com.erdouglass.emdb.user.api.Theme;

@Entity
@Table(name = "Users")
public class User {
  
  @Email
  @Column(unique = true)
  private String email;
  
  private String firstName;
  
  @Id
  @Column(updatable = false, nullable = false)
  private UUID id;
  
  private String lastName;
  
  @NotNull
  @Enumerated(EnumType.STRING)
  private Theme theme;
  
  @NotBlank
  @Column(unique = true, updatable = false)
  private String username;
    
  public User() {

  }
  
  public void setEmail(String email) {
    this.email = email;
  }
  
  public String getEmail() {
    return email;
  }
  
  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }
  
  public String getFirstName() {
    return firstName;
  }
  
  public void setId(UUID id) {
    this.id = id;
  }
  
  public UUID getId() {
    return id;
  }
  
  public void setLastName(String lastName) {
    this.lastName = lastName;
  }
  
  public String getLastName() {
    return lastName;
  }
  
  public void setTheme(Theme theme) {
    this.theme = theme;
  }
  
  public Theme getTheme() {
    return theme;
  }
  
  public void setUsername(String username) {
    this.username = username;
  }
  
  public String getUsername() {
    return username;
  }
  
  @Override
  public String toString() {
    return "User[id=" + id
        + ", username=" + username
        + ", firstName=" + firstName
        + ", lastName=" + lastName
        + ", email=" + email
        + ", theme=" + theme
        + "]";
  }
}
