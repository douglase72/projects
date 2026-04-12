package com.erdouglass.emdb.media.mapper;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.mapstruct.Mapping;

@Mapping(source = "backdrop", target = "backdrop", qualifiedByName = "imageToUuid")
@Mapping(source = "backdrop", target = "tmdbBackdrop", qualifiedByName = "imageToTmdbName")
@Mapping(source = "poster", target = "poster", qualifiedByName = "imageToUuid")
@Mapping(source = "poster", target = "tmdbPoster", qualifiedByName = "imageToTmdbName")
@Retention(RetentionPolicy.CLASS)
public @interface ShowImageMapping {}
