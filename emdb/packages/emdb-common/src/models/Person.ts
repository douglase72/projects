import { Gender } from "./Gender.js";

export interface PersonCredit {
  creditId: string;
  id: number;
  title: string;
  character: string | null;
  job: string | null;
  releaseDate: string | null;
  firstAirDate: string | null;
  score: number | null;
  backdrop: string | null;
  poster: string | null;
  overview: string | null;
}

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