import { Gender } from "./Person";
import { type Job } from "./Job";
import { MediaType } from "./MediaType";
import { type Show } from "./Show";
import { type Role } from "./Role";

export enum SeriesType {
  SCRIPTED = "Scripted",
  REALITY = "Reality",
  DOCUMENTARY = "Documentary",
  NEWS = "News",
  TALK_SHOW = "Talk Show",
  MINISERIES = "Miniseries",
  VIDEO = "Video",
}

export function fromType(type: string): string {
  return SeriesType[type as keyof typeof SeriesType] ?? type;
}

export interface CastCredit {
  id: number;
  name: string;
  gender: keyof typeof Gender;
  profile: string | null;
  roles: Role[];
  totalEpisodes: number;
  order: number | null;
}

export interface CrewCredit {
  creditId: string;
  id: number;
  name: string;
  gender: keyof typeof Gender;
  profile: string | null;
  jobs: Job[];
  totalEpisodes: number;
}

export interface Series extends Show {
  mediaType: MediaType.SERIES;
  firstAirDate: string | null;
  lastAirDate: string | null;
  type: keyof typeof SeriesType
  credits: {
    cast: CastCredit[];
    crew: CrewCredit[];
  };
}