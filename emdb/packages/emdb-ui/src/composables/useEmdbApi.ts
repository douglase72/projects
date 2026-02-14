import axios from 'axios';
import { useRouter } from 'vue-router';
import { useToast } from "primevue/usetoast";

import { useErrorHandler } from '@/composables/useErrorHandler';
import { ImageSize } from '@/models/ImageSize';
import type { Movie, Person, UpdateMovie, UpdatePerson } from '@emdb/common';
import type { Series } from '@/models/Series';

const client = axios.create({
  baseURL: import.meta.env.VITE_API_URL
});

export function useEmdbApi() {
  const { handleError } = useErrorHandler();
  const router = useRouter();
  const toast = useToast();
  
  const deleteMovie = async (movie: Movie) => {
    try {
      await client.delete<number>(`/movies/${movie.id}`);
      toast.add({ 
        severity: 'success', 
        summary: 'Success', 
        detail: `Deleted ${movie.title}`, 
        life: 5000});
    } catch (e) {
      handleError(e, 'Delete Failed');
    }
  };
  
  const deletePerson = async (person: Person) => {
    try {
      await client.delete<number>(`/people/${person.id}`);
      toast.add({ 
        severity: 'success', 
        summary: 'Success', 
        detail: `Deleted ${person.name}`, 
        life: 5000});
    } catch (e) {
      handleError(e, 'Delete Failed');
    }
  };  
  
  const findImage = (image: string, size: ImageSize) => {
    return `${import.meta.env.VITE_IMAGE_URL}/${size}/${image}`;
  };

  const findMovie = async (id: number): Promise<Movie | undefined> => {
    try {
      const { data: movie } = await client.get<Movie>(`/movies/${id}?append=credits`);
      return movie;
    } catch (e) {
      handleError(e, 'Load Failed');
    }
  };

  const findPerson = async (id: number): Promise<Person | undefined> => {
    try {
      const { data: person } = await client.get<Person>(`/people/${id}`);
      return person;
    } catch (e) {
      handleError(e, 'Load Failed');
    }
  };  

  const findSeries = async (id: number): Promise<Series | undefined> => {
    try {
      const { data: series } = await client.get<Series>(`/series/${id}`);
      return series;
    } catch (e) {
      handleError(e, 'Load Failed');
    }    
  };

  const updateMovie = async (id: number, command: UpdateMovie): Promise<Movie | undefined> => {
    try {
      const { data: movie } = await client.put<Movie>(`/movies/${id}`, command);
      toast.add({ 
        severity: 'success', 
        summary: 'Success', 
        detail: `Saved ${movie.title}`, 
        life: 5000 });      
      return movie;
    } catch (e) {
      handleError(e, 'Update Failed');
    }
  };

  const updatePerson = async (id: number, command: UpdatePerson): Promise<Person | undefined> => {
    try {
      const { data: person } = await client.put<Person>(`/people/${id}`, command);
      toast.add({ 
        severity: 'success', 
        summary: 'Success', 
        detail: `Saved ${person.name}`, 
        life: 5000 });
      return person;
    } catch (e) {
      handleError(e, 'Update Failed');
    }
  };

  return {
    deleteMovie,
    deletePerson,
    findImage,
    findMovie,
    findPerson, 
    findSeries,
    updateMovie,
    updatePerson,
  }

}