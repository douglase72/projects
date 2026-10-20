import axios from 'axios';

import { type IngestRequest } from '@/models/IngestRequest';

const client = axios.create({
  baseURL: import.meta.env.VITE_EMDB_INGEST_URL,
  timeout: 10_000,
});

export const ingest = async (request: IngestRequest): Promise<string> => {
  const { data: response } = await client.post<string>(`/ingest`, request);
  return response;
};