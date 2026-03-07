import axios from 'axios';

import keycloak from '@/auth/keycloak';
import { 
  type Movie,
  type UpdateMovie } from '@emdb/common';

const client = axios.create({
  baseURL: import.meta.env.VITE_API_URL,
  timeout: 300000,
});

client.interceptors.request.use(
  async (config) => {
    if (keycloak.authenticated) {
      try {
        await keycloak.updateToken(30);
        config.headers.Authorization = `Bearer ${keycloak.token}`;
      } catch (error) {
        console.error('Failed to refresh Keycloak token', error);
        keycloak.clearToken();
      }
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

export function useEmdbApi() {

  const findMovie = async (id: number): Promise<Movie | undefined> => {
    const { data: movie } = await client.get<Movie>(`/movies/${id}?append=credits`);
    return movie;
  };

  const updateMovie = async (id: number, command: UpdateMovie): Promise<Movie | undefined> => {
    const { data: movie } = await client.put<Movie>(`/movies/${id}`, command);
    return movie;
  };

  const deleteMovie = async (id: number) => {
    await client.delete<number>(`/movies/${id}`);
  };

  return {
    deleteMovie,
    findMovie,
    updateMovie,
  }

}