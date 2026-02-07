import axios, { type AxiosInstance } from 'axios';

import type { IngestMedia } from '@emdb/common';
import type { Movie } from '@emdb/common';
import type { SaveMovie } from "../models/SaveMovie.js";

export function useEmdb() {
  const apiUrl = process.env.API_URL;

  if (!apiUrl) {
    throw new Error('Invalid API_URL');
  }

  const client: AxiosInstance = axios.create({
    baseURL: apiUrl,
  });

 const findMovie = async (id: number): Promise<Movie> => {
    const { data: movie } = await client.get<Movie>(`/movies/${id}`);
    return movie;
  };  

  const ingest = async (command: IngestMedia): Promise<string> => {
    const { data } = await client.post<string>('/ingest', command);
    return data;    
  };

  const saveMovie = async (command: SaveMovie): Promise<Movie> => {
    const { data: movie } = await client.post<Movie>('/movies', command);
    return movie;
  };

  return {
    findMovie,
    ingest,
    saveMovie
  };

}