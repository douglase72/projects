import { MediaType } from "@emdb/common";

export interface JobStatus {
  status: IngestStatus;
  timestamp: string;
  source: IngestSource;
}

export enum IngestSource {
  GATEWAY = "emdb-gateway-service",
  MEDIA = "emdb-media-service",
  SCHEDULER = "emdb-scheduler-service",
  USER = "emdb-user-service"
}

export enum IngestStatus {
  SUBMITTED = "Submitted",
  STARTED = "Started",
  EXTRACTED = "Extracted",
  COMPLETED = "Completed",
  FAILED = "Failed",
  HEARTBEAT = "Heartbeat",
}

export interface IngestJob {
  id: string;
  emdbId: number | null;
  tmdbId: number | null;
  timestamp: string;
  source: IngestSource;
  status: IngestStatus;
  type: MediaType;
  name: string | null;
  history: JobStatus[];
}