package com.erdouglass.emdb.scheduler.api.command;

import jakarta.validation.constraints.NotNull;

import com.erdouglass.emdb.common.api.MediaType;

public record ExecuteScheduler(@NotNull MediaType type) {}