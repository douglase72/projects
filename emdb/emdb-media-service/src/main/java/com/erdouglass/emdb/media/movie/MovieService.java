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

import com.erdouglass.emdb.media.ImageService;

/// Application service for [Movie] persistence. Coordinates between the
/// [MovieRepository] for database operations and the [ImageService] for
/// image lifecycle management, ensuring image fetching and orphan deletion
/// stay consistent with the transactional outcome.
@ApplicationScoped
class MovieService {
  private static final Logger LOGGER = Logger.getLogger(MovieService.class);
  
  @Inject
  ImageService imageService;
  
  @Inject
  MovieMapper mapper;
  
  @Inject
  MovieRepository repository;
  
  @Inject 
  TransactionSynchronizationRegistry txSync;

  
  /// Persists the given movie, inserting it if no row with the same TMDB
  /// identifier exists or updating the existing row otherwise.
  ///
  /// The entire operation runs in a single transaction so that if any step
  /// fails — including fetching images from the TMDB CDN — all database
  /// changes are rolled back. Newly orphaned images from a prior version of
  /// the movie are deleted only *after* the transaction commits, so a
  /// rollback never leaves the image store in a torn state.
  ///
  /// @param movie the movie to save; must be non-null and pass bean validation
  /// @return the persisted movie with database-assigned fields populated
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

  /// Reconciles a single image field between an existing and incoming movie.
  ///
  /// If the TMDB reference has changed, fetches the new image via
  /// [ImageService#save] and assigns the returned [UUID] to the incoming
  /// movie. The previously-stored image (if any) is appended to
  /// `pendingDeletes` so it can be removed only after the surrounding
  /// transaction commits.
  ///
  /// @param existing the existing movie from the database, or `null` for a new movie
  /// @param incoming the incoming movie being saved
  /// @param pendingDeletes list to which orphaned stored image IDs are appended
  /// @param tmdbRef accessor for the external TMDB image reference
  /// @param storedRef accessor for the locally-stored image UUID
  /// @param assignStored mutator that assigns a newly-saved stored image UUID  
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
