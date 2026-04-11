import { z } from 'zod';

import { ShowStatus } from '@emdb/common';

export const UpdateMovieSchema = z.object({
  title: z.string().optional(),
  releaseDate: z.string().nullable().optional().optional(),
  score: z.number().min(0).max(10).nullable().optional(),
  status: z.enum(Object.values(ShowStatus) as [string, ...string[]]).optional(), 
  runtime: z.number().int().nonnegative().nullable().optional(),
  budget: z.number().int().nonnegative().nullable().optional(),
  revenue: z.number().int().nonnegative().nullable().optional(), 
  backdrop: z.string().uuid().nullable().optional(),
  poster: z.string().uuid().nullable().optional(),
  homepage: z.string().url().nullable().optional(),
  originalLanguage: z.string().length(2).nullable().optional(),
  tagline: z.string().nullable().optional(),
  overview: z.string().nullable().optional(),
}).strict();

export type UpdateMovie = z.infer<typeof UpdateMovieSchema>;