import axios, { type AxiosInstance } from 'axios';

import { IngestRequest } from '../model/IngestRequest.js';

export class MovieService {
  private readonly client: AxiosInstance;

    constructor() {
    this.client = axios.create({
      baseURL: process.env.BASE_URL,
    });
  }

  async ingest(request: IngestRequest): Promise<string> {
    const { data } = await this.client.post<string>('/movies/ingest', request);
    return data;
  }
  
}