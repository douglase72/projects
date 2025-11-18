import { ShowStatus } from "./ShowStatus.js";

export interface SeriesUpdateRequest {
  name: string | null;
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