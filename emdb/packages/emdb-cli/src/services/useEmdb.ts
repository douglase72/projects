import axios, { type AxiosInstance } from 'axios';

import { MovieStatus } from '../models/MovieStatus.js';
import type { SaveMovie } from '../schemas/SaveMovieSchema.js';
import type { 
  IngestMedia,
  Movie, 
  UpdateMovie } from '@emdb/common';

export function useEmdb() {
  const apiUrl = process.env.API_URL;

  if (!apiUrl) {
    throw new Error('Invalid API_URL');
  }

  const client: AxiosInstance = axios.create({
    baseURL: apiUrl,
    timeout: 10000,
  });

  const deleteMovie = async (id: number) => {
    client.delete<number>(`/movies/${id}`);
  };

  const findMovie = async (id: number): Promise<Movie> => {
    const { data: movie } = await client.get<Movie>(`/movies/${id}`);
    return movie;
  };

  const saveMovie = async (command: SaveMovie): Promise<{ movie: Movie; status: MovieStatus }> => {
    const response = await client.post<Movie>('/movies', command);
    return { 
      movie: response.data, 
      status: response.status === 201 ? MovieStatus.CREATED : MovieStatus.UPDATED, 
    };
  };

  const updateMovie = async (id:number, command: UpdateMovie): Promise<Movie> => {
    const { data: movie } = await client.put<Movie>(`/movies/${id}`, command);
    return movie;
  };
  
  const ingest = async (command: IngestMedia): Promise<string> => {
    const { data } = await client.post<string>('/ingest', command);
    return data;    
  }; 

  return {
    deleteMovie,
    ingest,
    findMovie,
    saveMovie,
    updateMovie,
  };

}
