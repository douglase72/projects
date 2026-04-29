import { MediaType } from "@emdb/common";

export enum IngestSource {
  GATEWAY = "emdb-gateway-service",
  MEDIA = "emdb-media-service",
  SCHEDULER = "emdb-scheduler-service",
  SCRAPER = "emdb-scraper-service"
}

export enum IngestStatus {
  SUBMITTED = "Submitted",
  STARTED = "Started",
  EXTRACTED = "Extracted",
  LOADED = "Loaded",
  COMPLETED = "Completed",
  FAILED = "Failed",
}

export interface Ingest {
  id: string;
  status: IngestStatus;
  lastModified: string;
  tmdbId: number;
  source: IngestSource;
  type: MediaType;
  message: string;
  emdbId: number | null;
  name: string | null;
}

export interface IngestHistory {
  id: string;
  changes: IngestStatusChange[];
}

export interface IngestStatusChange {
  lastModified: string;
  source: IngestSource;
  status: IngestStatus;
  message: string;
}