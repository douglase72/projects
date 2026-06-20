import { MediaType } from "./MediaType";

export interface ShowView {
  id: number;
  title: string; 
  releaseDate: string | null; 
  score: number;
  poster: string | null;
  mediaType: MediaType;
}