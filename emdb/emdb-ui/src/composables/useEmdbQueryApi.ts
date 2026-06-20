import axios from 'axios';

import { GraphQLError } from './useErrorHandler';
import { type Movie } from '@/models/Movie';
import { type OffsetPage } from '@/models/OffsetPage';
import { type Person } from '@/models/Person';
import { type Series } from '@/models/Series';

const client = axios.create({
  baseURL: `${import.meta.env.VITE_MEDIA_SERVICE_URL}/graphql`,
  timeout: 300000,
});

interface GraphQLResponse<T> {
  data?: T;
  errors?: Array<{ message: string; extensions?: { code?: string } }>;
}

export enum ImageSize {
  W45  = 'w45',
  W92  = 'w92',
  W154 = 'w154',
  W185 = 'w185',
  W300 = 'w300',
  W342 = 'w342',
  W500 = 'w500',
  W780 = 'w780',
  W1280 = 'w1280',
  ORIGINAL = 'original',
}

export function useEmdbQueryApi() {

  const findImage = (image: string, size: ImageSize) => {
    return `${import.meta.env.VITE_IMAGE_URL}/${size}/${image}`;
  };  

  const findMovie = async (id: number): Promise<Movie> => {
    const query = `
      query movie($id: BigInteger!) {
        movie(id: $id) {
          id tmdbId title releaseDate score status runtime budget revenue
          backdrop poster homepage originalLanguage tagline overview
          credits {
            cast { creditId id name gender profile character order }
            crew { creditId id name gender profile job }
          }
        }
      }`;  
    const response = await client.post<GraphQLResponse<{ movie: Movie }>>('', {
      query,
      variables: { id },
    });

    const firstError = response.data.errors?.[0];
    if (firstError) {
      throw new GraphQLError(firstError.message, firstError.extensions?.code);
    }

    const movie = response.data.data?.movie;
    if (!movie) {
      throw new Error(`Movie ${id} not found`);
    }
    return movie;
  };  

  const findAllMovies = async (): Promise<OffsetPage<Movie>> => {
    const query = `
      query {
        allMovies(query: { page: 1, size: 5, sort: SCORE_DESC }) {
          results {
            id title releaseDate score poster
          }
          page size totalResults
        }
      }`;  
    const response = await client.post<GraphQLResponse<{ allMovies: OffsetPage<Movie> }>>('', { query });

    const firstError = response.data.errors?.[0];
    if (firstError) {
      throw new GraphQLError(firstError.message, firstError.extensions?.code);
    }

    const movies = response.data.data?.allMovies;
    if (!movies) {
      throw new Error(`No movies found`);
    }
    return movies;
  };

  const findPerson = async (id: number): Promise<Person> => {
   const query = `
      query person($id: BigInteger!) {
        person(id: $id) {
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
    const response = await client.post<GraphQLResponse<{ person: Person }>>('', {
      query,
      variables: { id },
    });

    const firstError = response.data.errors?.[0];
    if (firstError) {
      throw new GraphQLError(firstError.message, firstError.extensions?.code);
    }

    const person = response.data.data?.person;
    if (!person) {
      throw new Error(`Person ${id} not found`);
    }
    return person; 
  };

  const findAllPeople = async (): Promise<OffsetPage<Person>> => {
    const query = `
      query {
        allPeople(query: { }) {
          results {
            id name birthDate profile
          }
          page size totalResults
        }
      }`;  
    const response = await client.post<GraphQLResponse<{ allPeople: OffsetPage<Person> }>>('', { query });

    const firstError = response.data.errors?.[0];
    if (firstError) {
      throw new GraphQLError(firstError.message, firstError.extensions?.code);
    }

    const people = response.data.data?.allPeople;
    if (!people) {
      throw new Error(`No people found`);
    }
    return people;
  };

  const findSeries = async (id: number): Promise<Series> => {
    const query = `
      query series($id: BigInteger!) {
        series(id: $id) {
          id tmdbId title firstAirDate lastAirDate score status type
          backdrop poster homepage originalLanguage tagline overview
          credits {
            cast { id name gender profile roles { creditId character episodeCount } totalEpisodes order }
            crew { id name gender profile jobs { creditId title episodeCount } totalEpisodes }
          }
        }
      }`;
    const response = await client.post<GraphQLResponse<{ series: Series }>>('', {
      query,
      variables: { id },
    });

    const firstError = response.data.errors?.[0];
    if (firstError) {
      throw new GraphQLError(firstError.message, firstError.extensions?.code);
    }

    const series = response.data.data?.series;
    if (!series) {
      throw new Error(`Series ${id} not found`);
    }
    return series;      
  };

  const findAllSeries = async (): Promise<OffsetPage<Series>> => {
    const query = `
      query {
        allSeries(query: { }) {
          results {
            id title firstAirDate score poster
          }
          page size totalResults
        }
      }`;  
    const response = await client.post<GraphQLResponse<{ allSeries: OffsetPage<Series> }>>('', { query });

    const firstError = response.data.errors?.[0];
    if (firstError) {
      throw new GraphQLError(firstError.message, firstError.extensions?.code);
    }

    const series = response.data.data?.allSeries;
    if (!series) {
      throw new Error(`No series found`);
    }
    return series;
  };

  return {
    findImage,
    findMovie,
    findAllMovies,
    findPerson,
    findAllPeople,
    findSeries,
    findAllSeries,
  }
}