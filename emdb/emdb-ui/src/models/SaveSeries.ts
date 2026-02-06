import { ShowStatus } from "./ShowStatus.js";

export interface SaveSeries {
  tmdbId: number;
  title: string;
  score: number | null;
  status: ShowStatus | null;
  type: string | null;
  homepage: string | null,
  originalLanguage: string | null;
  backdrop: string | null;
  poster: string | null;
  tagline: string | null;
  overview: string | null;
}