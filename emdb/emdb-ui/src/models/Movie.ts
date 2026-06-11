import { Gender } from "./Person";
import { ShowStatus } from "./ShowStatus";

export interface CastCredit {
  creditId: string;
  id: number;
  name: string;
  gender: keyof typeof Gender;
  profile: string | null;
  character: string | null;
  order: number | null;
}

export interface CrewCredit {
  creditId: string;
  id: number;
  name: string;
  gender: keyof typeof Gender;
  profile: string | null;
  job: string | null;
}

export interface Movie {
  id: number;
  tmdbId: number;
  title: string;
  releaseDate: string | null;
  score: number;
  status: keyof typeof ShowStatus;
  runtime: number | null;
  budget: number | null;
  revenue: number | null;
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