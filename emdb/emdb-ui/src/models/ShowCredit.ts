import { MediaType } from "./MediaType";

export interface ShowCredit {
  id: number;
  title: string;  
  score: number;
  poster: string | null;
  mediaType: MediaType;
}