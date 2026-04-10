package com.erdouglass.emdb.gateway.mapper;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.mapstruct.Mapping;

@Mapping(source = "backdrop", target = "backdrop", qualifiedByName = "imageToString")
@Mapping(source = "poster", target = "poster", qualifiedByName = "imageToString")
@Retention(RetentionPolicy.CLASS)
public @interface ImageMapping {}
