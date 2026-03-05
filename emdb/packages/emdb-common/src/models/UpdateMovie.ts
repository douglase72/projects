import { ShowStatus } from "./ShowStatus.js";

export interface UpdateMovieCredit {
  role: string;
  order: number;
}

export interface UpdateMovie {
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
}