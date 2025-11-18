import axios, { type AxiosInstance } from 'axios';

import { IngestRequest } from '../model/IngestRequest.js';
import { Series } from '../model/Series.js';
import { SeriesUpdateRequest } from '../model/SeriesUpdateRequest.js';

export class SeriesService {
  private static readonly LANG_MAPPER = new Intl.DisplayNames(['en'], { type: 'language' });

  private readonly client: AxiosInstance;

  constructor() {
    this.client = axios.create({
      baseURL: process.env.BASE_URL,
    });
  }

  async ingest(request: IngestRequest): Promise<number> {
    const { status } = await this.client.post<IngestRequest>('/series/ingest', request);
    return status;
  }

  async findById(id: number): Promise<Series> {
    const { data: series } = await this.client.get<Series>(`/series/${id}`);
    if (series.originalLanguage) {
      series.originalLanguage = SeriesService.LANG_MAPPER.of(series.originalLanguage) ?? null;;
    }
    return series;    
  }

  async update(id: number, request: SeriesUpdateRequest): Promise<Series> {
    const { data: series } = await this.client.patch<Series>(`/series/${id}`, request);
    return series;
  }

  async deleteById(id: number) {
    await this.client.delete<Series>(`/series/${id}`);
  }

}