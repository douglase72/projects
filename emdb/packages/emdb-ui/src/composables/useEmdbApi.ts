import axios from 'axios';
import { useRouter } from 'vue-router';
import { useToast } from "primevue/usetoast";

import { useErrorHandler } from '@/composables/useErrorHandler';
import { ImageSize } from '@/models/ImageSize';
import type { Movie } from '@emdb/common';
import type { Person } from '@/models/Person';
import type { Series } from '@/models/Series';

const client = axios.create({
  baseURL: import.meta.env.VITE_API_URL
});

export function useEmdbApi() {
  const { handleError } = useErrorHandler();
  const router = useRouter();
  const toast = useToast(); 
  
  const findImage = (image: string, size: ImageSize) => {
    return `${import.meta.env.VITE_IMAGE_URL}/${size}/${image}`;
  };

  const findMovie = async (id: number): Promise<Movie | undefined> => {
    try {
      const { data: movie } = await client.get<Movie>(`/movies/${id}`);
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

  const toDate = (dateString: string | null | undefined) => {
    if (!dateString) return null;
    const [year, month, day] = dateString.split('-').map(Number) as [number, number, number];
    return new Date(year, month - 1, day); 
  };

  const toDateString = (date: Date | null) => {
    return date ? date.toLocaleDateString('en-CA') : null;
  };  

  return {
    findImage,
    findMovie,
    findPerson, 
    findSeries,
    toDate,
    toDateString,
  }

}