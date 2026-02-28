import axios, { type AxiosInstance } from 'axios';

import type { SaveMovie } from '../schemas/SaveMovieSchema.js';
import type { SavePerson } from '../schemas/PersonSchema.js';
import type { SaveSeries } from '../schemas/SaveSeriesSchema.js';
import type { 
  ExecuteScheduler,
  IngestMedia, 
  Movie, 
  Person,
  Series, 
  UpdateMovie,
  UpdatePerson,
  UpdateSeries } from '@emdb/common';

export function useEmdb() {
  const apiUrl = process.env.API_URL;

  if (!apiUrl) {
    throw new Error('Invalid API_URL');
  }

  const client: AxiosInstance = axios.create({
    baseURL: apiUrl,
  });

  const deleteMovie = async (id: number) => {
    client.delete<number>(`/movies/${id}`);
  };

  const deleteSeries = async (id: number) => {
    client.delete<number>(`/series/${id}`);
  };

  const deletePerson = async (id: number) => {
    client.delete<number>(`/people/${id}`);
  };

  const executeScheduler = async (command: ExecuteScheduler): Promise<number> => {
    const { status } = await client.post('/scheduler', command);
    return status;
  };

  const findMovie = async (id: number): Promise<Movie> => {
    const { data: movie } = await client.get<Movie>(`/movies/${id}`);
    return movie;
  }; 
  
  const findSeries = async (id: number): Promise<Series> => {
    const { data: series } = await client.get<Series>(`/series/${id}`);
    return series;
  };   

  const findPerson = async (id: number): Promise<Person> => {
    const { data: person } = await client.get<Person>(`/people/${id}`);
    return person;
  };  

  const ingest = async (command: IngestMedia): Promise<string> => {
    const { data } = await client.post<string>('/ingest', command);
    return data;    
  };

  const saveMovie = async (command: SaveMovie): Promise<Movie> => {
    const { data: movie } = await client.post<Movie>('/movies', command);
    return movie;
  };  
  
  const saveSeries = async (command: SaveSeries): Promise<Series> => {
    const { data: series } = await client.post<Series>('/series', command);
    return series;
  };

  const savePerson = async (command: SavePerson): Promise<Person> => {
    const { data: person } = await client.post<Person>('/people', command);
    return person;
  };  

  const updateMovie = async (id:number, command: UpdateMovie): Promise<Movie> => {
    const { data: movie } = await client.put<Movie>(`/movies/${id}`, command);
    return movie;
  };
  
  const updateSeries = async (id:number, command: UpdateSeries): Promise<Series> => {
    const { data: series } = await client.put<Series>(`/series/${id}`, command);
    return series;
  };  

  const updatePerson = async (id:number, command: UpdatePerson): Promise<Person> => {
    const { data: person } = await client.put<Person>(`/people/${id}`, command);
    return person;
  };    

  return {
    deleteMovie,
    deletePerson,
    deleteSeries,
    executeScheduler,
    findMovie,
    findPerson,
    ingest,
    saveMovie,
    savePerson,
    saveSeries,
    findSeries,
    updateMovie,
    updatePerson,
    updateSeries
  };

}