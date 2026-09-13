package com.erdouglass.emdb.media;

/// Decouples the Media service public API from the domain allowing them to 
/// evolve independently.
public record SavePersonCommand(
    Integer tmdbId,
    String name,
    String birthDate,
    String deathDate,
    String gender,
    String biography) { }
