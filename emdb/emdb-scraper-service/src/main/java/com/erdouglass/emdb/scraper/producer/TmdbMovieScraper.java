package com.erdouglass.emdb.scraper.producer;

import org.jboss.logging.Logger;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class TmdbMovieScraper {
	private static final Logger LOGGER = Logger.getLogger(TmdbMovieScraper.class);
	
	public void ingest(int tmdbId) {
		LOGGER.infof("Ingesting TMDB movie id: %d", tmdbId);
	}
	
	public void synchronize(long emdbId, int tmdbId) {
		LOGGER.infof("Synchronizing EMDB movie id: %d with TMDB movie id: %d", emdbId, tmdbId);
	}

}
