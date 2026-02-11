import { ShowStatus } from "./ShowStatus.js";

export enum CreditType {
  CAST = "cast",
  CREW = "crew",
}

export interface UpdateMovieCredit {
  id: string | null;
  personId: number;
  type: CreditType;
  role: string | null;
  order: number | null;
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
  credits: UpdateMovieCredit[]; 
}