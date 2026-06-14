import { MediaType } from "./MediaType";

export enum IngestSource {
  CLI = "CLI",
  MEDIA = "MEDIA",
  SCHEDULER = "SCHEDULER",
  UI = "UI"
}

export interface IngestMedia {
  tmdbId: number,
  type: MediaType,
  source: IngestSource
}