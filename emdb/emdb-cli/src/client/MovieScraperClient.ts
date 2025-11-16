import axios, { type AxiosInstance } from 'axios';

import { IngestRequest } from '../model/IngestRequest.js';

/**
 * This is a temporary class until the emdb-gateway-service is created providing
 * a single URL for the EMDB application.
 */
export class MovieScraperClient {
  private readonly client: AxiosInstance;

  constructor() {
    this.client = axios.create({
      baseURL: "http://localhost:60336/api/movies",
    });
  }

  async ingest(request: IngestRequest): Promise<number> {
    const { status } = await this.client.post<IngestRequest>('/ingest', request);
    return status;
  }

}