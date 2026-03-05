import { Gender } from "./Gender.js";
import { SeriesType } from "./SeriesType.js";
import { ShowStatus } from "./ShowStatus.js";

export interface Role {
  id: string;
  character: string | null;
  episodeCount: number;
}

export interface Job {
  id: string;
  title: string | null;
  episodeCount: number;
}

export interface SeriesCastCredit {
  creditId: string;
  id: number;
  name: string;
  gender: Gender;
  profile: string | null;
  roles: Role[];
  totalEpisodes: number;
  order: number | null;
}

export interface SeriesCrewCredit {
  creditId: string;
  id: number;
  name: string;
  gender: Gender;
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
  score: number | null;
  status: ShowStatus;
  type: SeriesType | null;
  homepage: string | null,
  originalLanguage: string | null;
  backdrop: string | null;
  poster: string | null;
  tagline: string | null;
  overview: string | null; 
  credits: {
    cast: SeriesCastCredit[];
    crew: SeriesCrewCredit[];
  }; 
}