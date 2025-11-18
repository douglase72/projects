import { ShowStatus } from "./ShowStatus.js";

export interface Series {
  id: number;
  tmdbId: number;
  name: string;
  firstAirDate: string | null;
  lastAirDate: string | null;
  score: number | null;
  status: ShowStatus;
  type: number | null;
  homepage: string | null,
  originalLanguage: string | null;
  backdrop: string | null;
  poster: string | null;
  tagline: string | null;
  overview: string | null;
}