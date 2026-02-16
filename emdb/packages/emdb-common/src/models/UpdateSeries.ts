import { SeriesType } from "./SeriesType.js";
import { ShowStatus } from "./ShowStatus.js";

export interface UpdateSeries {
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