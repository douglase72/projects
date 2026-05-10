package com.erdouglass.emdb.user.api.command;

import jakarta.validation.constraints.Email;

import com.erdouglass.emdb.user.api.Theme;

public record UpdateUser(    
    String firstName,
    String lastName,
    @Email String email,
    Theme theme) {
  
  public static Builder builder() {
    return new Builder();
  }
  
  public static final class Builder {
    private String email;
    private String firstName;
    private String lastName;
    private Theme theme;
    
    Builder() {}
    
    public UpdateUser build() {
      return new UpdateUser(firstName, lastName, email, theme);
    }
    
    public Builder email(String email) {
      this.email = email;
      return this;
    }
    
    public Builder firstName(String firstName) {
      this.firstName = firstName;
      return this;
    }
    
    public Builder lastName(String lastName) {
      this.lastName = lastName;
      return this;
    }
    
    public Builder theme(Theme theme) {
      this.theme = theme;
      return this;
    }
  }
}
