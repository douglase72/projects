import axios, { type AxiosInstance } from 'axios';

import { IngestRequest } from '../model/IngestRequest.js';
import { Movie } from '../model/Movie.js';
import { MovieCreateRequest } from '../model/MovieCreateRequest.js';
import { MovieUpdateRequest } from '../model/MovieUpdateRequest.js';

export class MovieService {
  private static readonly LANG_MAPPER = new Intl.DisplayNames(['en'], { type: 'language' });

  private readonly client: AxiosInstance;

    constructor() {
    this.client = axios.create({
      baseURL: process.env.BASE_URL,
    });
  }

  async create(request: MovieCreateRequest): Promise<Movie> {
    const { data: movie } = await this.client.post<Movie>(`/movies`, request);
    if (movie.originalLanguage) {
      movie.originalLanguage = MovieService.LANG_MAPPER.of(movie.originalLanguage) ?? null;
    }    
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

  async findById(id: number): Promise<Movie> {
    const { data: movie } = await this.client.get<Movie>(`/movies/${id}`);
    if (movie.originalLanguage) {
      movie.originalLanguage = MovieService.LANG_MAPPER.of(movie.originalLanguage) ?? null;
    }
    return movie;    
  }

  async update(id: number, request: MovieUpdateRequest): Promise<Movie> {
    const { data: movie } = await this.client.patch<Movie>(`/movies/${id}`, request);
    if (movie.originalLanguage) {
      movie.originalLanguage = MovieService.LANG_MAPPER.of(movie.originalLanguage) ?? null;
    }    
    return movie;
  }

  async deleteById(id: number) {
    await this.client.delete<Movie>(`/movies/${id}`);
  }  
  
}