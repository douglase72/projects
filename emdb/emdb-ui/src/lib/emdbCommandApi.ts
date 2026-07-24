import axios from 'axios';

const client = axios.create({
  baseURL: import.meta.env.VITE_EMDB_COMMAND_URL,
  timeout: 10_000,
});

export interface UpdateMovieRequest {
  version: number;
  title: string;
  releaseDate: string | null;
  originalLanguage: string | null;
}

export interface UpdateMovieResponse {
  id: string,
  version: number;
}

export const updateMovie = async (id: string, request: UpdateMovieRequest): Promise<UpdateMovieResponse> => {
  const { data: response} = await client.put<UpdateMovieResponse>(`/movies/${id}`, request);
  return response;
};