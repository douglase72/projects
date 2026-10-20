import { MediaType } from "./MediaType";

export interface IngestRequest {
  tmdbId: number,
  mediaType: MediaType,
}