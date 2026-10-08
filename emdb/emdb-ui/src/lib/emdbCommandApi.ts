import axios from 'axios';

const client = axios.create({
  baseURL: import.meta.env.VITE_EMDB_COMMAND_URL,
  timeout: 10_000,
});

export interface UpdateMovieRequest {
  version: number;
  title: string;
  releaseDate: string | null;
  score: number | null;
  originalLanguage: string | null;
  overview: string | null;
}

export interface UpdatePersonRequest {
  version: number;
  name: string;
  birthDate: string | null;
  deathDate: string | null;
  gender: string | null;
  biography: string | null;
}

export interface UpdateResponse {
  id: string,
  version: number;
}

export const updateMovie = async (id: string, request: UpdateMovieRequest): Promise<UpdateResponse> => {
  const { data: response} = await client.put<UpdateResponse>(`/movies/${id}`, request);
  return response;
};

export const deleteMovie = async (id: string) => {
  await client.delete(`/movies/${id}`);
}

export const updatePerson = async (id: string, request: UpdatePersonRequest): Promise<UpdateResponse> => {
  const { data: response} = await client.put<UpdateResponse>(`/people/${id}`, request);
  return response;
};

export const deletePerson = async (id: string) => {
  await client.delete(`/people/${id}`);
}