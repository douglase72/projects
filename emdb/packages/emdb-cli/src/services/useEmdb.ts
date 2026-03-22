import axios, { type AxiosInstance } from 'axios';

import type { 
  Movie, 
  Person, 
  Series} from '@emdb/common';
import type { SaveMovie } from '../schemas/SaveMovieSchema.js';
import type { SavePerson } from '../schemas/SavePersonSchema.js';
import type { SaveSeries } from '../schemas/SaveSeriesSchema.js';
import type { UpdateMovie } from '../schemas/UpdateMovieSchema.js';
import type { UpdatePerson } from '../schemas/UpdatePersonSchema.js';
import type { UpdateSeries } from '../schemas/UpdateSeriesSchema.js';
import type { IngestMedia, MultiResponse } from '@emdb/common';

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

  const ingest = async (command: IngestMedia): Promise<string> => {
    const { data } = await client.post<string>('/ingest', command);
    return data;    
  };
  
  const saveMovie = async (command: SaveMovie): Promise<{ status: number, movie?: Movie }> => {
    const response = await client.post<Movie>('/movies', command);
    return { status: response.status, movie: response.data };
  };  

  const findMovie = async (id: number): Promise<Movie> => {
    const { data: movie } = await client.get<Movie>(`/movies/${id}`);
    return movie;
  };

  const updateMovie = async (id: number, command: UpdateMovie): Promise<{ status: number, movie?: Movie }> => {
    const response = await client.put<Movie>(`/movies/${id}`, command);
    return { status: response.status, movie: response.data };
  };

 const deleteMovie = async (id: number): Promise<{ status: number }> => {
    const response = await client.delete<number>(`/movies/${id}`);
    return { status: response.status };
  };    

  const savePerson = async (command: SavePerson): Promise<{ status: number, person?: Person }> => {
    const response = await client.post<Person>('/people', command);
    return { status: response.status, person: response.data };
  };  

  const savePeople = async (command: SavePerson[]): Promise<MultiResponse[]> => {
    const { data: results } = await client.post<MultiResponse[]>('/people/batch', command);
    return results;
  }; 

 const findPerson = async (id: number): Promise<Person> => {
    const { data: person } = await client.get<Person>(`/people/${id}`);
    return person;
  };  

  const updatePerson = async (id: number, command: UpdatePerson): Promise<{ status: number, person?: Person }> => {
    const response = await client.put<Person>(`/people/${id}`, command);
    return { status: response.status, person: response.data };
  };

  const deletePerson = async (id: number): Promise<{ status: number }> => {
    const response = await client.delete<number>(`/people/${id}`);
    return { status: response.status };
  };  
  
  const saveSeries = async (command: SaveSeries): Promise<{ status: number, series?: Series }> => {
    const response = await client.post<Series>('/series', command);
    return { status: response.status, series: response.data };
  };
  
  const findSeries = async (id: number): Promise<Series> => {
    const { data: series } = await client.get<Series>(`/series/${id}`);
    return series;
  };  

  const updateSeries = async (id: number, command: UpdateSeries): Promise<{ status: number, series?: Series }> => {
    const response = await client.put<Series>(`/series/${id}`, command);
    return { status: response.status, series: response.data };
  };

 const deleteSeries = async (id: number): Promise<{ status: number }> => {
    const response = await client.delete<number>(`/series/${id}`);
    return { status: response.status };
  };  

  return {
    deleteMovie,
    deletePerson,
    deleteSeries,
    ingest,
    findMovie,
    findPerson,
    findSeries,
    saveMovie,
    savePerson,
    savePeople,
    saveSeries,
    updateMovie,
    updatePerson,
    updateSeries,
  };

}
