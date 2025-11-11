package com.erdouglass.emdb.common.configuration;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class Configuration {
	public static final int ISO_639_1_LENGTH  = 2;
	public static final int SOURCE_MAX_LENGTH = 10;
	public static final int URL_MAX_LENGTH    = 2048;
}
