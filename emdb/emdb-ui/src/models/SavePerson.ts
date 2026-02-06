import { Gender } from "./Gender";

export interface SavePerson {
  tmdbId: number;
  name: string;
  birthDate: string | null;
  deathDate: string | null;
  gender: Gender | null;
  birthPlace: string | null;
  profile: string | null;
  biography: string | null;
}