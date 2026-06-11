import { type Job } from "./Job";
import { MediaType } from "./MediaType";
import { type Role } from "./Role";

export enum Gender {
  UNKNOWN = "Unknown",
  FEMALE = "Female",
  MALE = "Male",
  NON_BINARY = "Non-Binary"
}

export function fromGender(gender: string): string {
  return Gender[gender as keyof typeof Gender] ?? gender;
}

export interface PersonCredit {
  creditId: string;
  id: number;
  title: string;
  score: number;
  backdrop: string | null;
  poster: string | null;
  overview: string | null;
  type: MediaType; 
}

export interface PersonMovieCastCredit extends PersonCredit {
  releaseDate: string | null;
  character: string | null;
  type: MediaType.MOVIE;
}

export interface PersonMovieCrewCredit extends PersonCredit {
  releaseDate: string | null;
  job: string | null;
  type: MediaType.MOVIE;
}

export interface PersonSeriesCastCredit extends PersonCredit {
  firstAirDate: string | null;
  roles: Role[];
  type: MediaType.SERIES;
}

export interface PersonSeriesCrewCredit extends PersonCredit {
  firstAirDate: string | null;
  jobs: Job[];
  type: MediaType.SERIES;
}

export type PersonCastCredit = PersonMovieCastCredit | PersonSeriesCastCredit;
export type PersonCrewCredit = PersonMovieCrewCredit | PersonSeriesCrewCredit;

export interface Person {
  id: number;
  tmdbId: number;
  name: string; 
  birthDate: string | null;
  deathDate: string | null;
  gender: Gender;
  profile: string | null;
  birthPlace: string | null;
  biography: string | null;
  credits: {
    cast: PersonCastCredit[];
    crew: PersonCrewCredit[];
  };  
}