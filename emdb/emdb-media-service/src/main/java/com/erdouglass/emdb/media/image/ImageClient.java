package com.erdouglass.emdb.media.image;

import java.io.InputStream;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

/// MicroProfile REST client for the TMDB image CDN. Configured via the
/// `tmdb-image` config key, which sets the base URL and any required
/// timeouts in `application.properties`.
@RegisterRestClient(configKey = "tmdb-image")
interface ImageClient {

  /// Streams the raw bytes of the image at the given path on the CDN.
  ///
  /// The returned [InputStream] is unbuffered and must be closed by the
  /// caller (typically in a try-with-resources block) to release the
  /// underlying HTTP connection.
  ///
  /// @param name the image path on the CDN (e.g., `/abc123.jpg`)
  /// @return an open [InputStream] over the image bytes  
  @GET
  @Path("/{name}")
  InputStream findByName(@PathParam("name") String name);
}
