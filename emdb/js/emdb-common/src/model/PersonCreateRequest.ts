import { Gender } from "./Gender.js";

export interface PersonCreateRequest {
  tmdbId: number;
  name: string;
  birthDate: string | null;
  deathDate: string | null;
  gender: Gender;
  birthPlace: string | null;
  profile: string | null;
  biography: string | null;  
}