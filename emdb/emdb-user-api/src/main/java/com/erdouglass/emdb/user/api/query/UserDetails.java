package com.erdouglass.emdb.user.api.query;

import java.util.UUID;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import com.erdouglass.emdb.user.api.Theme;

public record UserDetails(
    @NotNull UUID id,
    @NotBlank String username,
    String firstName,
    String lastName,
    @Email String email,
    @NotNull Theme theme) {}
