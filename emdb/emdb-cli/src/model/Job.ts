
export enum JobStatus {
  STARTED = "Started",
  FETCHED = "Fetched",
  PROGRESS = "Progress",
  COMPLETED = "Completed",
  FAILED = "Failed"
}

export interface Job {
  content: string;
  id: string;
  progress: number;
  source: string;
  status: JobStatus;
  timestamp: string;
}