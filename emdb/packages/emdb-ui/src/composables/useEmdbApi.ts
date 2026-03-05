import axios from 'axios';

import {
  type Movie } from '@emdb/common';

const client = axios.create({
  baseURL: import.meta.env.VITE_API_URL,
  timeout: 300000,
});

export function useEmdbApi() {

  const findMovie = async (id: number): Promise<Movie | undefined> => {
    const { data: movie } = await client.get<Movie>(`/movies/${id}?append=credits`);
    return movie;
  };

  return {
    findMovie,
  }

}