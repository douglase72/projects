import { z } from 'zod';

import { ImageSchema } from './ImageSchema.js';
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
  releaseDate: z.string().nullable().optional().optional(),
  score: z.number().min(0).max(10).nullable().optional(),
  status: z.enum(Object.values(ShowStatus) as [string, ...string[]]).optional(), 
  runtime: z.number().int().nonnegative().nullable().optional(),
  budget: z.number().int().nonnegative().nullable().optional(),
  revenue: z.number().int().nonnegative().nullable().optional(), 
  backdrop: ImageSchema.nullable().optional(),
  poster: ImageSchema.nullable().optional(),
  homepage: z.string().url().nullable().optional(),
  originalLanguage: z.string().length(2).nullable().optional(),
  tagline: z.string().nullable().optional(),
  overview: z.string().nullable().optional(),
}).strict();

export type SaveMovie = z.infer<typeof SaveMovieSchema>;
