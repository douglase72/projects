import type { MediaType } from "./MediaType";
import { ShowStatus } from "./ShowStatus";

export interface Show {
  id: number;
  tmdbId: number;
  title: string;
  score: number;
  status: keyof typeof ShowStatus;
  backdrop: string | null;
  poster: string | null;
  homepage: string | null,
  originalLanguage: string | null;
  tagline: string | null;
  overview: string | null; 
  mediaType: MediaType;
}