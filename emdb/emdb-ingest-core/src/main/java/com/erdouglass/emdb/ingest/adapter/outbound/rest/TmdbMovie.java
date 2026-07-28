package com.erdouglass.emdb.ingest.adapter.outbound.rest;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import com.erdouglass.emdb.media.OriginalLanguage;
import com.erdouglass.emdb.media.Title;

public record TmdbMovie(    
    @NotNull @Positive Integer id,
    @NotBlank @Size(max = Title.MAX_LENGTH) String title,
    LocalDate release_date,
    @NotBlank @Size(min = OriginalLanguage.LENGTH, max = OriginalLanguage.LENGTH) String original_language) {

}
