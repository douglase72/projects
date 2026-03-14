import axios, { type AxiosInstance } from 'axios';

import type { 
  Movie, 
  Person } from '@emdb/common';
import type { SaveMovie } from '../schemas/SaveMovieSchema.js';
import type { SavePerson } from '../schemas/SavePersonSchema.js';
import type { MultiResponse } from '@emdb/common';

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

  const findMovie = async (id: number): Promise<Movie> => {
    const { data: movie } = await client.get<Movie>(`/movies/${id}`);
    return movie;
  };

  const saveMovie = async (command: SaveMovie): Promise<{ status: number, movie?: Movie }> => {
    const response = await client.post<Movie>('/movies', command);
    return { status: response.status, movie: response.data };
  };

  const savePerson = async (command: SavePerson): Promise<{ status: number, person?: Person }> => {
    const response = await client.post<Person>('/people', command);
    return { status: response.status, person: response.data };
  };  

  const savePeople = async (command: SavePerson[]): Promise<MultiResponse[]> => {
    const { data: results } = await client.post<MultiResponse[]>('/people/batch', command);
    return results;
  };    

  return {
    findMovie,
    saveMovie,
    savePerson,
    savePeople,
  };

}
