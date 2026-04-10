import { Gender } from "./Gender.js";

export interface UpdatePerson {
  name: string;
  birthDate: string | null;
  deathDate: string | null;
  gender: Gender | null;
  birthPlace: string | null;
  profile: string | null;
  biography: string | null;
}