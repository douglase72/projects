import axios, { type AxiosInstance } from 'axios';

import { IngestRequest } from '../model/IngestRequest.js';
import { Movie } from "../model/Movie.js";

/**
 * Service class for interacting with the Movie Media API.
 * Provides methods for CRUD (Create, Read, Update, Delete) operations on movies.
 */
export class MovieService {
  private readonly client: AxiosInstance;

  constructor() {
    this.client = axios.create({
      baseURL: "http://localhost:60310/emdb/api",
    });
  }

  async ingest(request: IngestRequest): Promise<number> {
    const { status } = await this.client.post<IngestRequest>('/movies/ingest', request);
    return status;
  }

  async findById(id: number): Promise<Movie> {
    const { data: movie } = await this.client.get<Movie>(`/movies/${id}`);
    return movie;    
  }

}