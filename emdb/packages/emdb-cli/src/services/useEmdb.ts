import axios, { type AxiosInstance } from 'axios';

import { MovieStatus } from '../models/MovieStatus.js';
import type { SaveMovie } from '../schemas/SaveMovieSchema.js';
import type { 
  IngestMedia,
  Movie, 
  UpdateMovie } from '@emdb/common';

let cachedToken: string | null = null;

export function useEmdb() {
  const apiUrl = process.env.API_URL;
  const keycloakTokenUrl = process.env.KEYCLOAK_TOKEN_URL;
  const clientId = process.env.EMDB_CLIENT_ID;
  const clientSecret = process.env.EMDB_CLIENT_SECRET;

  if (!apiUrl) {
    throw new Error('Invalid API_URL');
  }

  const client: AxiosInstance = axios.create({
    baseURL: apiUrl,
    timeout: 10000,
  });

  client.interceptors.request.use(async (config) => {
    if (!cachedToken) {
      if (!keycloakTokenUrl || !clientId || !clientSecret) {
        throw new Error('Missing Keycloak environment variables for authentication.');
      }

      const params = new URLSearchParams();
      params.append('grant_type', 'client_credentials');
      params.append('client_id', clientId);
      params.append('client_secret', clientSecret);
      const tokenResponse = await axios.post(keycloakTokenUrl, params, {
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
      });
      cachedToken = tokenResponse.data.access_token;
    }
    config.headers.Authorization = `Bearer ${cachedToken}`;
    return config;
  }, (error) => {
    return Promise.reject(error);
  });

  const deleteMovie = async (id: number) => {
    client.delete<number>(`/movies/${id}`);
  };

  const findMovie = async (id: number): Promise<Movie> => {
    const { data: movie } = await client.get<Movie>(`/movies/${id}`);
    return movie;
  };

  const saveMovie = async (command: SaveMovie): Promise<Movie> => {
    const { data: movie } = await client.post<Movie>('/movies', command);
    return movie;
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
