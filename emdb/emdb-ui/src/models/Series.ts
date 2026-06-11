import { Gender } from "./Person";
import { type Job } from "./Job";
import { type Role } from "./Role";
import { ShowStatus } from "./ShowStatus";

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

export interface Series {
  id: number;
  tmdbId: number;
  title: string;
  firstAirDate: string | null;
  lastAirDate: string | null;
  score: number;
  status: keyof typeof ShowStatus;
  type: keyof typeof SeriesType
  backdrop: string | null;
  poster: string | null;
  homepage: string | null,
  originalLanguage: string | null;
  tagline: string | null;
  overview: string | null; 
  credits: {
    cast: CastCredit[];
    crew: CrewCredit[];
  };
}