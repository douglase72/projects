import axios from 'axios';

import { type IngestMedia } from '@/models/IngestMedia';

const client = axios.create({
  timeout: 300000,
});

export function useEmdbCommandApi() {

  const ingest = async (command: IngestMedia): Promise<string> => {
    const url = `${import.meta.env.VITE_INGEST_SERVICE_URL}/ingest`;
    const { data } = await client.post<string>(url, command);
    return data;
  };

  return {
    ingest,
  }
}