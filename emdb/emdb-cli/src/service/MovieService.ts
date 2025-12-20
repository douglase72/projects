import axios, { type AxiosInstance } from 'axios';

import { IngestRequest } from '../model/IngestRequest.js';
import { Movie } from '../model/Movie.js';
import { MovieCreateRequest } from '../model/MovieCreateRequest.js';

export class MovieService {
  private readonly client: AxiosInstance;

    constructor() {
    this.client = axios.create({
      baseURL: process.env.BASE_URL,
    });
  }

  async create(request: MovieCreateRequest): Promise<Movie> {
    const { data: movie } = await this.client.post<Movie>(`/movies`, request);
    return movie;
  }

  async cron(): Promise<number> {
    const { status } = await this.client.post<number>('/movies/cron');
    return status;    
  } 

  async ingest(request: IngestRequest): Promise<string> {
    const { data } = await this.client.post<string>('/movies/ingest', request);
    return data;
  }
  
}