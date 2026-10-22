import axios from 'axios';

import { MediaType } from '@/models/MediaType';

const client = axios.create({
  baseURL: import.meta.env.VITE_EMDB_INGEST_URL,
  timeout: 10_000,
});

export interface IngestRequest {
  tmdbId: number,
  mediaType: MediaType,
}

export const ingest = async (request: IngestRequest): Promise<string> => {
  const { data: response } = await client.post<string>(`/ingest`, request);
  return response;
};