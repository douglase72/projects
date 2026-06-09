import axios from 'axios';

import { type Movie } from '@/models/Movie';

const client = axios.create({
  baseURL: import.meta.env.VITE_API_URL,
  timeout: 300000,
});

interface GraphQLResponse<T> {
  data?: T;
  errors?: Array<{ message: string }>;
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
      throw new Error(firstError.message);
    }

    const movie = response.data.data?.findMovieById;
    if (!movie) {
      throw new Error(`Movie ${id} not found`);
    }
    return movie;
  };  

  return {
    findMovie,
  }
}