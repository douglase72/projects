import axios from 'axios';
import { useRouter } from 'vue-router';
import { useToast } from "primevue/usetoast";

import { useErrorHandler } from '@/composables/useErrorHandler';
import { ImageSize } from '@/models/ImageSize';
import {
  type ExecuteScheduler,
  type IngestMedia,
  type Movie, 
  type Person,
  type Role,
  SchedulerType,
  type Series, 
  type UpdateMovie,
  type UpdateMovieCredit, 
  type UpdatePerson,
  type UpdateRole,
  type UpdateSeries,
  type UpdateSeriesCredit } from '@emdb/common';

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
      toast.add({ severity: 'success', summary: 'Success', detail: `Deleted ${movie.title}`, life: 5000 });
    } catch (e) {
      handleError(e, 'Delete Failed');
      throw e;
    }
  };

  const deleteSeries = async (series: Series) => {
    try {
      await client.delete<number>(`/series/${series.id}`);
      toast.add({ severity: 'success', summary: 'Success', detail: `Deleted ${series.title}`, life: 5000 });
    } catch (e) {
      handleError(e, 'Delete Failed');
      throw e;
    }
  };
  
  const deletePerson = async (person: Person) => {
    try {
      await client.delete<number>(`/people/${person.id}`);
      toast.add({ severity: 'success', summary: 'Success', detail: `Deleted ${person.name}`, life: 5000 });
    } catch (e) {
      handleError(e, 'Delete Failed');
      throw e;
    }
  };

  const executeMovieScheduler = async () => {
    try {
      const command: ExecuteScheduler = { type: SchedulerType.MOVIES };
      await client.post('/scheduler', command);
      const msg = `Movie scheduler job submitted`;
      toast.add({ severity: 'info', summary: msg, life: 5000 }); 
    } catch (e) {
      handleError(e, 'Movie Scheduler Failed');
      throw e;
    }     
  };

  const executePersonScheduler = async () => {
    try {
      const command: ExecuteScheduler = { type: SchedulerType.PEOPLE };
      await client.post('/scheduler', command);
      const msg = `Person scheduler job submitted`;
      toast.add({ severity: 'info', summary: msg, life: 5000 }); 
    } catch (e) {
      handleError(e, 'Person Scheduler Failed');
      throw e;
    }     
  };  

  const executeSeriesScheduler = async () => {
    try {
      const command: ExecuteScheduler = { type: SchedulerType.SERIES };
      await client.post('/scheduler', command);
      const msg = `Series scheduler job submitted`;
      toast.add({ severity: 'info', summary: msg, life: 5000 }); 
    } catch (e) {
      handleError(e, 'Series Scheduler Failed');
      throw e;
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
      throw e;
    }
  };

  const findPerson = async (id: number): Promise<Person | undefined> => {
    try {
      const { data: person } = await client.get<Person>(`/people/${id}?append=credits`);
      return person;
    } catch (e) {
      handleError(e, 'Load Failed');
      throw e;
    }
  };  

  const findSeries = async (id: number): Promise<Series | undefined> => {
    try {
      const { data: series } = await client.get<Series>(`/series/${id}?append=credits`);
      return series;
    } catch (e) {
      handleError(e, 'Load Failed');
      throw e;
    }    
  };

  const ingest = async (command: IngestMedia) => {
    try {
      const { data: jobId } = await client.post<string>('/ingest', command);
      const msg = `Ingest job ${jobId} submitted for TMDB ${command.type} ${command.tmdbId}`;
      toast.add({ severity: 'info', summary: msg, life: 5000 }); 
    } catch (e) {
      handleError(e, 'Ingest Failed');
      throw e;
    }           
  };  

  const updateMovie = async (id: number, command: UpdateMovie): Promise<Movie | undefined> => {
    try {
      const { data: movie } = await client.put<Movie>(`/movies/${id}`, command);
      toast.add({ severity: 'success', summary: 'Success', detail: `Saved ${movie.title}`, life: 5000 });      
      return movie;
    } catch (e) {
      handleError(e, 'Update Failed');
      throw e;
    }
  };

  const updateMovieCredit = async (id: string, command: UpdateMovieCredit) => {
    try {
      await client.put(`/movies/credits/${id}`, command);
      toast.add({ severity: 'success', summary: 'Success', detail: `Saved credit`, life: 5000 });
    } catch (e) {
      handleError(e, 'Update Failed');
      throw e;
    }  
  };  

  const updatePerson = async (id: number, command: UpdatePerson): Promise<Person | undefined> => {
    try {
      const { data: person } = await client.put<Person>(`/people/${id}`, command);
      toast.add({ severity: 'success', summary: 'Success', detail: `Saved ${person.name}`, life: 5000 });
      return person;
    } catch (e) {
      handleError(e, 'Update Failed');
      throw e;
    }
  };

    const updateSeries = async (id: number, command: UpdateSeries): Promise<Series | undefined> => {
    try {
      const { data: series } = await client.put<Series>(`/series/${id}`, command);
      toast.add({ severity: 'success', summary: 'Success', detail: `Saved ${series.title}`, life: 5000 });      
      return series;
    } catch (e) {
      handleError(e, 'Update Failed');
      throw e;
    }
  };

  const updateSeriesCredit = async (id: string, command: UpdateSeriesCredit) => {
    try {
      await client.put(`/series/credits/${id}`, command);
      toast.add({ severity: 'success', summary: 'Success', detail: `Saved credit`, life: 5000 });
    } catch (e) {
      handleError(e, 'Update Failed');
      throw e;
    }  
  };  

  const updateRole = async (id: string, command: UpdateRole): Promise<Role | undefined> => {
    try {
      const { data: role } = await client.put<Role>(`/roles/${id}`, command);
      toast.add({ severity: 'success', summary: 'Success', detail: `Saved ${role.character}`, life: 5000 });
      return role;
    } catch (e) {
      handleError(e, 'Update Failed');
      throw e;
    }  
  };

  return {
    deleteMovie,
    deletePerson,
    deleteSeries,
    executeMovieScheduler,
    executePersonScheduler,
    executeSeriesScheduler,
    ingest,
    findImage,
    findMovie,
    findPerson, 
    findSeries,
    updateMovie,
    updateMovieCredit,
    updatePerson,
    updateRole,
    updateSeries,
    updateSeriesCredit,
  }

}