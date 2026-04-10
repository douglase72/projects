import { Gender } from "./Gender.js";
import { MediaType } from "./MediaType.js";
import type { Job } from "./Series.js";
import type { Role } from "./Series.js";

export interface BasicPersonCredit {
  creditId: string;
  id: number;
  title: string;
  score: number | null;
  backdrop: string | null;
  poster: string | null;
  overview: string | null;
  type: MediaType; 
}

export interface PersonMovieCredit extends BasicPersonCredit {
  character: string | null;
  job: string | null;
  releaseDate: string | null;
  type: MediaType.MOVIE; 
}

export interface PersonSeriesCredit extends BasicPersonCredit {
  roles: Role[];
  jobs: Job[];
  firstAirDate: string | null;
  type: MediaType.SERIES;
}

export type PersonCredit = PersonMovieCredit | PersonSeriesCredit;

export interface Person {
  id: number;
  tmdbId: number;
  name: string; 
  birthDate: string | null;
  deathDate: string | null;
  gender: Gender | null;
  birthPlace: string | null;
  profile: string | null;
  biography: string | null;
  credits: {
    cast: PersonCredit[];
    crew: PersonCredit[];
  };
}