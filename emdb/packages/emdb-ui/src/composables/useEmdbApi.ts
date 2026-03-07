import axios from 'axios';

import keycloak from '@/auth/keycloak';
import { type Movie } from '@emdb/common';

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

  return {
    findMovie,
  }

}