import { z } from 'zod';

import { PersonSchema } from './PersonSchema.js';
import { SeriesType, ShowStatus } from '@emdb/common';

export const SaveMovieSchema = z.object({
  tmdbId: z.number().int().positive(),
  title: z.string(),
  releaseDate: z.string().nullable().optional(),
  score: z.number().min(0).max(10).nullable(),
  status: z.enum(Object.values(ShowStatus) as [string, ...string[]]), 
  runtime: z.number().int().nonnegative().nullable(),
  budget: z.number().int().nonnegative().nullable(),
  revenue: z.number().int().nonnegative().nullable(),  
  homepage: z.string().url().nullable(),
  originalLanguage: z.string().length(2).nullable(),
  backdrop: z.string().uuid().nullable(),
  tmdbBackdrop: z.string().nullable(),
  poster: z.string().uuid().nullable(),
  tmdbPoster: z.string().nullable(),
  tagline: z.string().nullable(),
  overview: z.string().nullable(),
  credits: z.object({
    cast: z.array(z.object({
      person: PersonSchema,
      character: z.string(),
      order: z.number().int(),
    })),
    crew: z.array(z.object({
      person: PersonSchema,
      job: z.string(),
    })),
  }).optional()
}).strict();

export type SaveMovie = z.infer<typeof SaveMovieSchema>;
