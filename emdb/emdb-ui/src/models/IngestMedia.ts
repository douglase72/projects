import { MediaType } from "./MediaType";

export enum IngestSource {
  CRON = "CRON",
  UI = "UI",
}

export interface IngestMedia {
  source: IngestSource;
  tmdbId: number;
  type: MediaType;
}