import axios from 'axios';
import { useRouter } from 'vue-router';
import { useToast } from "primevue/usetoast";

import { useErrorHandler } from '@/composables/useErrorHandler';
import { ImageSize } from '@/models/ImageSize';
import type { IngestMedia } from '@/models/IngestMedia';
import type { Movie } from '@/models/Movie';
import type { Person } from '@/models/Person';
import type { SaveMovie } from '@/models/SaveMovie.js';
import type { SavePerson } from '@/models/SavePerson';
import type { SaveSeries } from '@/models/SaveSeries';
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
        detail: `Deleted movie ${movie.title}`, 
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
        detail: `Deleted person ${person.name}`, 
        life: 5000});
    } catch (e) {
      handleError(e, 'Delete Failed');
    }
  };

  const deleteSeries = async (series: Series) => {
    try {
      await client.delete<number>(`/series/${series.id}`);
      toast.add({ 
        severity: 'success', 
        summary: 'Success', 
        detail: `Deleted series ${series.title}`, 
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

  const ingest = async (command: IngestMedia) => {
    try {
      const { data: jobId } = await client.post<string>('/ingest', command);
      const msg = `Ingest job ${jobId} submitted for TMDB ${command.type} ${command.tmdbId}`;
      toast.add({ severity: 'info', summary: msg, life: 5000 }); 
    } catch (e) {
      handleError(e, 'Ingest Failed');
    }           
  };

  const movieCron = async () => {
    try {
      const request = { command: "Execute now" };
      await client.post<number>('/ingest/movie/cron', request);
      const msg = `Movie cron job submitted`;
      toast.add({ severity: 'info', summary: msg, life: 5000 }); 
    } catch (e) {
      handleError(e, 'Movie Cron Failed');
    }         
  }

 const personCron = async () => {
    try {
      const request = { command: "Execute now" };
      await client.post<number>('/ingest/person/cron', request);
      const msg = `Person cron job submitted`;
      toast.add({ severity: 'info', summary: msg, life: 5000 }); 
    } catch (e) {
      handleError(e, 'Person Cron Failed');
    }         
  }  

  const saveMovie = async (movie: SaveMovie): Promise<Movie | undefined> => {
    try {
      const { data: savedMovie } = await client.post<Movie>('/movies', movie);
      toast.add({ 
        severity: 'success', 
        summary: 'Success', 
        detail: `Saved movie ${savedMovie.title}`, 
        life: 5000 });
      return savedMovie;
    } catch (e) {
      handleError(e, 'Save Failed');
    }        
  }

  const savePerson = async (person: SavePerson): Promise<Person | undefined> => {
    try {
      const { data: savedPerson } = await client.post<Person>('/people', person);
      toast.add({ 
        severity: 'success', 
        summary: 'Success', 
        detail: `Saved person ${savedPerson.name}`, 
        life: 5000 });
      return savedPerson;
    } catch (e) {
      handleError(e, 'Save Failed');
    }        
  }

  const saveSeries = async (series: SaveSeries): Promise<Series | undefined> => {
    try {
      const { data: savedSeries } = await client.post<Series>('/series', series);
      toast.add({ 
        severity: 'success', 
        summary: 'Success', 
        detail: `Saved series ${savedSeries.title}`, 
        life: 5000 });
      return savedSeries;
    } catch (e) {
      handleError(e, 'Save Failed');
    }        
  }  

  const seriesCron = async () => {
    try {
      const request = { command: "Execute now" };
      await client.post<number>('/ingest/series/cron', request);
      const msg = `Series cron job submitted`;
      toast.add({ severity: 'info', summary: msg, life: 5000 }); 
     } catch (e) {
      handleError(e, 'Series Cron Failed');
    }         
  }

  const toDate = (dateString: string | null | undefined) => {
    if (!dateString) return null;
    const [year, month, day] = dateString.split('-').map(Number) as [number, number, number];
    return new Date(year, month - 1, day); 
  };

  const toDateString = (date: Date | null) => {
    return date ? date.toLocaleDateString('en-CA') : null;
  };  

  return {
    deleteMovie,
    deletePerson,
    deleteSeries,
    findImage,
    findMovie,
    findPerson, 
    findSeries,
    ingest,
    movieCron,
    personCron,
    saveMovie,
    savePerson,
    saveSeries,
    seriesCron,
    toDate,
    toDateString,
  }

}