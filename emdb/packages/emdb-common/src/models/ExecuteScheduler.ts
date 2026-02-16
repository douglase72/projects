
export enum SchedulerType {
  MOVIES = "MOVIES",
  SERIES = "SERIES",
  PEOPLE = "PEOPLE",
}

export interface ExecuteScheduler {
  type: SchedulerType;
}