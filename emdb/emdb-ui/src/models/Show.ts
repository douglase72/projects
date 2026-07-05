import { type MediaType } from "@/gql/graphql";

export const Media = {
  Movie: 'MOVIE', 
  Person: 'PERSON', 
  Series: 'SERIES',
} as const satisfies Record<string, MediaType>

export interface Show {
  id: number;
  title: string; 
  releaseDate: string | null; 
  score: number;
  poster: string | null;
  mediaType: MediaType;
}