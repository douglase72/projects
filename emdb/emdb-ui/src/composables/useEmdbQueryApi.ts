import axios from 'axios';

import { GraphQLError } from './useErrors';
import { type Movie } from '@/models/Movie';
import { type Person } from '@/models/Person';
import { type Series } from '@/models/Series';

const client = axios.create({
  baseURL: import.meta.env.VITE_API_URL,
  timeout: 300000,
});

interface GraphQLResponse<T> {
  data?: T;
  errors?: Array<{ message: string; extensions?: { code?: string } }>;
}

export function useEmdbQueryApi() {

  const findMovieById = async (id: number): Promise<Movie> => {
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

  const findPersonById = async (id: number): Promise<Person> => {
   const query = `
      query FindPerson($id: BigInteger!) {
        findPersonById(id: $id) {
          id tmdbId name birthDate deathDate gender profile birthPlace biography
          credits {
            cast {
              ... on PersonMovieCastCredit { creditId id title releaseDate score backdrop poster overview character type }
              ... on PersonSeriesCastCredit { creditId id title firstAirDate score backdrop poster overview roles { creditId character episodeCount } type }
            }
            crew {
              ... on PersonMovieCrewCredit { creditId id title releaseDate score backdrop poster overview job type }
              ... on PersonSeriesCrewCredit { creditId id title firstAirDate score backdrop poster overview jobs { creditId title episodeCount } type }
            }
          }
        }
      }`;  
    const response = await client.post<GraphQLResponse<{ findPersonById: Person }>>('', {
      query,
      variables: { id },
    });

    const firstError = response.data.errors?.[0];
    if (firstError) {
      throw new GraphQLError(firstError.message, firstError.extensions?.code);
    }

    const person = response.data.data?.findPersonById;
    if (!person) {
      throw new Error(`Person ${id} not found`);
    }
    return person; 
  };

  const findSeriesById = async (id: number): Promise<Series> => {
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
  };

  return {
    findMovieById,
    findPersonById,
    findSeriesById,
  }
}