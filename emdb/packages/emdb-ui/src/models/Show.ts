import { MediaType } from "@emdb/common";

export interface Show {
  id: number;
  title: string;
  poster: string | null;
  date: string | null;
  score: number | null;
  type: MediaType;
}