import { z } from 'zod';

import { ShowStatus } from '@emdb/common';

export const UpdateMovieSchema = z.object({
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
}).strict();

export type UpdateMovie = z.infer<typeof UpdateMovieSchema>;