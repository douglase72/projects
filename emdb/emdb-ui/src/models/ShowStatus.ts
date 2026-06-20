
export enum ShowStatus {
  CANCELED = "Canceled",
  ENDED = "Ended",
  IN_PRODUCTION = "In Production",
  PILOT = "Pilot",
  PLANNED = "Planned",
  POST_PRODUCTION = "Post Production",
  RELEASED = "Released",
  RETURNING_SERIES = "Returning Series",
  RUMORED = "Rumored",
}

export function fromShowStatus(status: string): string {
  return ShowStatus[status as keyof typeof ShowStatus] ?? status;
}