import { SeriesType } from "./SeriesType";
import { ShowStatus } from "./ShowStatus";

export interface Series {
  id: number;
  tmdbId: number;
  title: string;
  firstAirDate: string | null;
  lastAirDate: string | null;
  score: number | null;
  status: ShowStatus;
  type: SeriesType | null;
  homepage: string | null,
  originalLanguage: string | null;
  backdrop: string | null;
  poster: string | null;
  tagline: string | null;
  overview: string | null;  
}