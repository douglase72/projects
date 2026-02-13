
export enum SchedulerType {
  MOVIES = "MOVIES",
  SEROES = "SERIES",
  PEOPLE = "PEOPLE",
}

export interface ExecuteScheduler {
  type: SchedulerType;
}