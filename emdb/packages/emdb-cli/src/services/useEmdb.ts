import axios, { type AxiosInstance } from 'axios';

import type { SaveMovie } from "../models/SaveMovie.js";
import type { Movie } from '@emdb/common';

export function useEmdb() {
  const apiUrl = process.env.API_URL;

  if (!apiUrl) {
    throw new Error('Invalid API_URL');
  }

  const client: AxiosInstance = axios.create({
    baseURL: apiUrl,
  });

  const saveMovie = async (command: SaveMovie): Promise<Movie> => {
    const { data: movie } = await client.post<Movie>('/movies', command);
    return movie;
  };

  const findMovie = async (id: number): Promise<Movie> => {
    const { data: movie } = await client.get<Movie>(`/movies/${id}`);
    return movie;
  };

  return {
    findMovie,
    saveMovie
  };

}