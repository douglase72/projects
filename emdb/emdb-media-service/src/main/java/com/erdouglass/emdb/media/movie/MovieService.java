package com.erdouglass.emdb.media.movie;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Function;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Status;
import jakarta.transaction.Synchronization;
import jakarta.transaction.TransactionSynchronizationRegistry;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import org.jboss.logging.Logger;

import com.erdouglass.emdb.media.image.ImageService;

@ApplicationScoped
public class MovieService {
  private static final Logger LOGGER = Logger.getLogger(MovieService.class);
  
  @Inject
  ImageService imageService;
  
  @Inject
  MovieMapper mapper;
  
  @Inject
  MovieRepository repository;
  
  @Inject 
  TransactionSynchronizationRegistry txSync;

  
  /// Save the given movie to the database.
  /// 
  /// Update the existing movie in the database if it exists, otherwise insert
  /// the new movie. The entire operation is wrapped in a transaction so that
  /// if any part fails, such as fetching images from the TMDB CDN, the 
  /// transaction is rolled back.
  @Transactional
  public Movie save(@NotNull @Valid final Movie movie) {
    var existingMovie = repository.findByTmdbId(movie.getTmdbId()).orElse(null);
    var pendingDeletes = new ArrayList<UUID>();
    syncImage(existingMovie, movie, pendingDeletes,
        Movie::getTmdbBackdrop, Movie::getBackdrop, Movie::setBackdrop);
    syncImage(existingMovie, movie, pendingDeletes,
        Movie::getTmdbPoster, Movie::getPoster, Movie::setPoster);    
    
    Movie savedMovie;
    if (existingMovie == null) {
      savedMovie = repository.insert(movie);
    } else {
      mapper.merge(movie, existingMovie);
      savedMovie = repository.update(existingMovie);
    }
    
    if (!pendingDeletes.isEmpty()) {
      txSync.registerInterposedSynchronization(new Synchronization() {
        @Override public void beforeCompletion() {}
        @Override public void afterCompletion(int status) {
          if (status == Status.STATUS_COMMITTED) {
            for (var id : pendingDeletes) {
              try {
                imageService.delete(id);
              } catch (RuntimeException e) {
                LOGGER.warnf(e, "Failed to delete orphaned image: %s", id);
              }
            }
          }
        }
      });
    }    
    LOGGER.infof("Saved: %s", savedMovie);
    return savedMovie;
  }

  private void syncImage(
      final Movie existing,
      final Movie incoming,
      final List<UUID> pendingDeletes,
      final Function<Movie, String> tmdbRef,
      final Function<Movie, UUID> storedRef,
      final BiConsumer<Movie, UUID> assignStored) {
    var existingTmdb = existing == null ? null : tmdbRef.apply(existing);
    var incomingTmdb = tmdbRef.apply(incoming);
    if (Objects.equals(existingTmdb, incomingTmdb)) {
      return;
    }
    if (incomingTmdb != null) {
      assignStored.accept(incoming, imageService.save(incomingTmdb));
    }
    if (existing != null && storedRef.apply(existing) != null) {
      pendingDeletes.add(storedRef.apply(existing));
    }
  }
}
