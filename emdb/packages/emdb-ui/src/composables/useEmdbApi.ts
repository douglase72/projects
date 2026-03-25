import axios from 'axios';

import keycloak from '@/auth/keycloak';
import { 
  type Movie,
  type UpdateMovie,
  type Person,
  type UpdatePerson,
  type Series,
  type UpdateSeries } from '@emdb/common';

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
        keycloak.clearToken();
        keycloak.login();
        return Promise.reject(error);
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

  const findPerson = async (id: number): Promise<Person | undefined> => {
    const { data: person } = await client.get<Person>(`/people/${id}?append=credits`);
    return person;
  }; 
  
  const updatePerson = async (id: number, command: UpdatePerson): Promise<Person | undefined> => {
    const { data: person } = await client.put<Person>(`/people/${id}`, command);
    return person;
  };

  const deletePerson = async (id: number) => {
    await client.delete<number>(`/people/${id}`);
  };  

  const findSeries = async (id: number): Promise<Series | undefined> => {
    const { data: series } = await client.get<Series>(`/series/${id}?append=credits`);
    return series;
  };
  
  const updateSeries = async (id: number, command: UpdateSeries): Promise<Series | undefined> => {
    const { data: series } = await client.put<Series>(`/series/${id}`, command);
    return series;
  };
  
  const deleteSeries = async (id: number) => {
    await client.delete<number>(`/series/${id}`);
  };

  return {
    deleteMovie,
    deletePerson,
    deleteSeries,
    findMovie,
    findPerson,
    updatePerson,
    findSeries,
    updateMovie,
    updateSeries,
  }

}