import { SeriesType, ShowStatus } from "@emdb/common";

export interface SaveSeries {
  tmdbId: number;
  title: string;
  score: number | null;
  status: ShowStatus;
  type: SeriesType;
  homepage: string | null,
  originalLanguage: string | null;
  backdrop: string | null;
  poster: string | null;
  tagline: string | null;
  overview: string | null;
}