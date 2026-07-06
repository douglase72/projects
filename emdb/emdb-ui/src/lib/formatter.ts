import type { Gender, SeriesType, ShowStatus } from "@/gql/graphql";

const GenderMap = {
  FEMALE: 'Female',
  MALE: 'Male',
  NON_BINARY: 'Non-binary',
  UNKNOWN: 'Unknown',
}

const seriesTypeMap = {
  DOCUMENTARY: 'Documentary',
  MINISERIES: 'Miniseries',
  NEWS: 'News',
  REALITY: 'Reality',
  SCRIPTED: 'Scripted',
  TALK_SHOW: 'Talk Show',
  VIDEO: 'Video', 
} as const satisfies Record<SeriesType, string>;

const showStatusMap = {
  CANCELED: 'Canceled',
  ENDED: 'Ended',
  IN_PRODUCTION: 'In Production',
  PILOT: 'Pilot',
  PLANNED: 'Planned',
  POST_PRODUCTION: 'Post Production',
  RELEASED: 'Released',
  RETURNING_SERIES: 'Returning Series',
  RUMORED: 'Rumored',
} as const satisfies Record<ShowStatus, string>;

function fallbackLabel(value: string): string {
  return value.toLowerCase().split('_')
    .map(w => w.charAt(0).toUpperCase() + w.slice(1))
    .join(' ');
}

export function formatGender(gender: Gender): string {
  return GenderMap[gender] ?? fallbackLabel(gender);
}

export function formatSeriesType(type: SeriesType): string {
  return seriesTypeMap[type] ?? fallbackLabel(type);
}

export function formatShowStatus(status: ShowStatus): string {
  return showStatusMap[status] ?? fallbackLabel(status);
}