import axios from 'axios';

import { GraphQLError } from './useErrors';
import { type Movie } from '@/models/Movie';
import { type Series } from '@/models/Series';

const client = axios.create({
  baseURL: import.meta.env.VITE_API_URL,
  timeout: 300000,
});

interface GraphQLResponse<T> {
  data?: T;
  errors?: Array<{ message: string; extensions?: { code?: string } }>;
}

export function useEmdbApi() {

  const findMovie = async (id: number): Promise<Movie> => {
    const query = `
      query FindMovie($id: BigInteger!) {
        findMovieById(id: $id) {
          id tmdbId title releaseDate score status runtime budget revenue
          backdrop poster homepage originalLanguage tagline overview
          credits {
            cast { creditId id name gender profile character order }
            crew { creditId id name gender profile job }
          }
        }
      }`;  
    const response = await client.post<GraphQLResponse<{ findMovieById: Movie }>>('', {
      query,
      variables: { id },
    });

    const firstError = response.data.errors?.[0];
    if (firstError) {
      throw new GraphQLError(firstError.message, firstError.extensions?.code);
    }

    const movie = response.data.data?.findMovieById;
    if (!movie) {
      throw new Error(`Movie ${id} not found`);
    }
    return movie;
  };  

  const findSeries = async (id: number): Promise<Series> => {
    const query = `
      query FindSeries($id: BigInteger!) {
        findSeriesById(id: $id) {
          id tmdbId title firstAirDate lastAirDate score status type
          backdrop poster homepage originalLanguage tagline overview
          credits {
            cast { id name gender profile roles { creditId character episodeCount } totalEpisodes order }
            crew { id name gender profile jobs { creditId title episodeCount } totalEpisodes }
          }
        }
      }`;
      const response = await client.post<GraphQLResponse<{ findSeriesById: Series }>>('', {
        query,
        variables: { id },
      });

    const firstError = response.data.errors?.[0];
    if (firstError) {
      throw new GraphQLError(firstError.message, firstError.extensions?.code);
    }

    const series = response.data.data?.findSeriesById;
    if (!series) {
      throw new Error(`Series ${id} not found`);
    }
    return series;      
  }

  return {
    findMovie,
    findSeries
  }
}