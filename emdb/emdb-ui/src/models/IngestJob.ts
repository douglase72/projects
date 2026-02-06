import { MediaType } from "./MediaType";

export enum IngestStatus {
  SUBMITTED = "Submitted",
  STARTED = "Started",
  EXTRACTED = "Extracted",
  COMPLETED = "Completed",
  FAILED = "Failed",
  HEARTBEAT = "Heartbeat",
}

export interface IngestJob {
  emdbId: number | null;
  id: string;
  name: string | null;
  source: string
  status: IngestStatus;
  timestamp: string;
  tmdbId: number;
  type: MediaType;
}