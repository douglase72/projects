import axios, { type AxiosInstance } from 'axios';

import { Movie } from "../model/Movie.js";
import { MovieCreateRequest } from '../model/MovieCreateRequest.js';
import { MovieUpdateRequest } from '../model/MovieUpdateRequest.js';

/**
 * Service class for interacting with the Movie Media API.
 * Provides methods for CRUD (Create, Read, Update, Delete) operations on movies.
 */
export class MovieService {
  private readonly client: AxiosInstance;

  constructor() {
    this.client = axios.create({
      baseURL: "http://localhost:60332/api",
    });
  }

  /**
   * Creates a new movie record in the database.
   *
   * @param request - The movie data to create.
   * @returns A promise that resolves to the newly created Movie object.
   */
  async create(request: MovieCreateRequest): Promise<Movie> {
    const start = performance.now();
    const { data: movie } = await this.client.post<Movie>('/movies', request);
    const et = (performance.now() - start).toLocaleString(undefined, {
      minimumFractionDigits: 1,
      maximumFractionDigits: 1
    });
    console.log(`Created ${movie.title} in: ${et} ms.`);
    return movie;
  }

  /**
   * Finds and returns a specific movie by its ID.
   *
   * @param id - The unique identifier of the movie to find.
   * @returns A promise that resolves to the found Movie object.
   */
  async findById(id: number): Promise<Movie> {
    const start = performance.now();
    const { data: movie } = await this.client.get<Movie>(`/movies/${id}`);
    const et = (performance.now() - start).toLocaleString(undefined, {
      minimumFractionDigits: 1,
      maximumFractionDigits: 1
    });
    console.log(`Found ${movie.title} in: ${et} ms.`);
    return movie;    
  }

  /**
   * Updates an existing movie's details by its ID.
   *
   * @param id - The unique identifier of the movie to update.
   * @param request - The movie data to update.
   * @returns A promise that resolves to the updated Movie object.
   */
  async update(id: number, request: MovieUpdateRequest): Promise<Movie> {
    const start = performance.now();
    const { data: movie } = await this.client.patch<Movie>(`/movies/${id}`, request);
    const et = (performance.now() - start).toLocaleString(undefined, {
      minimumFractionDigits: 1,
      maximumFractionDigits: 1
    });
    console.log(`Updated ${movie.title} in: ${et} ms.`);
    return movie;
  }

  /**
   * Deletes a movie record by its ID.
   *
   * @param id - The unique identifier of the movie to delete.
   * @returns A promise that resolves when the deletion is complete.
   */
  async deleteById(id: number) {
    const start = performance.now();
    await this.client.delete<Movie>(`/movies/${id}`);
    const et = (performance.now() - start).toLocaleString(undefined, {
      minimumFractionDigits: 1,
      maximumFractionDigits: 1
    });
    console.log(`Deleted movie: ${id} in: ${et} ms.`);
  } 

}