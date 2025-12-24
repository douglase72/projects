import { Gender } from "./Gender.js";
import { ShowStatus } from "./ShowStatus.js";

interface MovieCredit {
  creditId: number;
  id: number;
  name: string;
  gender: Gender;
  profile: string | null;
  character: string | null;
  job: string | null;
  order: number | null;
}

interface Credits {
  cast: Array<MovieCredit>;
  crew: Array<MovieCredit>;
}

export interface Movie {
  id: number;
  tmdbId: number;
  title: string;
  releaseDate: string | null;
  score: number | null;
  status: ShowStatus;
  runtime: number | null;
  budget: number | null;
  revenue: number | null;
  homepage: string | null,
  originalLanguage: string | null;
  backdrop: string | null;
  poster: string | null;
  tagline: string | null;
  overview: string | null;
  credits: Credits | null;
}