import axios, { type AxiosInstance } from 'axios';

import type { SaveMovie } from "../models/SaveMovie.js";
import type { SavePerson } from '../models/SavePerson.js';
import type { 
  ExecuteScheduler,
  IngestMedia, 
  Movie, 
  Person, 
  UpdateMovie,
  UpdatePerson } from '@emdb/common';

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

  const savePerson = async (command: SavePerson): Promise<Person> => {
    const { data: person } = await client.post<Person>('/people', command);
    return person;
  };  

  const updateMovie = async (id:number, command: UpdateMovie): Promise<Movie> => {
    const { data: movie } = await client.put<Movie>(`/movies/${id}`, command);
    return movie;
  };  

  const updatePerson = async (id:number, command: UpdatePerson): Promise<Person> => {
    const { data: person } = await client.put<Person>(`/people/${id}`, command);
    return person;
  };    

  return {
    deleteMovie,
    deletePerson,
    executeScheduler,
    findMovie,
    findPerson,
    ingest,
    saveMovie,
    savePerson,
    updateMovie,
    updatePerson
  };

}