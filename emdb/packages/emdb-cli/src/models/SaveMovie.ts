import { ShowStatus } from "@emdb/common";

export interface SaveMovie {
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
  tmdbBackdrop: string | null;
  poster: string | null;
  tmdbPoster: string | null;
  tagline: string | null;
  overview: string | null;  
}