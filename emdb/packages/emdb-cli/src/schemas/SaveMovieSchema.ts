import { z } from 'zod';

import { ShowStatus } from '@emdb/common';
import { SavePersonSchema } from './SavePersonSchema.js';

const CastCreditSchema = z.object({
  tmdbId: z.number().int().positive(),
  character: z.string().nullable().optional(),
  order: z.number().int().nonnegative(),
});

const CrewCreditSchema = z.object({
  tmdbId: z.number().int().positive(),
  job: z.string().nullable().optional(),
});

const CreditsSchema = z.object({
  cast: z.array(CastCreditSchema),
  crew: z.array(CrewCreditSchema),
});

export const SaveMovieSchema = z.object({
  tmdbId: z.number().int().positive(),
  title: z.string(),
  releaseDate: z.string().nullable().optional(),
  score: z.number().min(0).max(10).nullable(),
  status: z.enum(Object.values(ShowStatus) as [string, ...string[]]), 
  runtime: z.number().int().nonnegative().nullable(),
  budget: z.number().int().nonnegative().nullable(),
  revenue: z.number().int().nonnegative().nullable(), 
  backdrop: z.string().uuid().nullable(),
  poster: z.string().uuid().nullable(),
  homepage: z.string().url().nullable(),
  originalLanguage: z.string().length(2).nullable(),
  tagline: z.string().nullable(),
  overview: z.string().nullable(),
  credits: CreditsSchema,
  people: z.array(SavePersonSchema),
}).strict();

export type SaveMovie = z.infer<typeof SaveMovieSchema>;
