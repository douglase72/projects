import { MediaType } from "./MediaType.js"

export enum IngestSource {
  CLI = "CLI",
  CRON = "CRON",
  UI = "UI"
}

export interface IngestMedia {
  tmdbId: number,
  type: MediaType,
  source: IngestSource
}