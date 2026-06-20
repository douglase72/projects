import { Gender } from "./Person";
import { MediaType } from "./MediaType";
import { type Show } from "./Show";

export interface CastCredit {
  creditId: string;
  id: number;
  name: string;
  gender: keyof typeof Gender;
  profile: string | null;
  character: string | null;
  order: number | null;
}

export interface CrewCredit {
  creditId: string;
  id: number;
  name: string;
  gender: keyof typeof Gender;
  profile: string | null;
  job: string | null;
}

export interface Movie extends Show {
  mediaType: MediaType.MOVIE;
  releaseDate: string | null;
  runtime: number | null;
  budget: number | null;
  revenue: number | null;
  credits: {
    cast: CastCredit[];
    crew: CrewCredit[];
  };
}