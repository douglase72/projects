
export enum JobStatus {
  SUBMITTED = "Submitted",
  STARTED = "Started",
  FETCHED = "Fetched",
  PROGRESS = "Progress",
  COMPLETED = "Completed",
  FAILED = "Failed",
  HEARTBEAT = "Heartbeat",
}

export interface Job {
  content: string;
  id: string;
  progress: number;
  source: string;
  status: JobStatus;
  timestamp: string;
  tmdbId: number;
}