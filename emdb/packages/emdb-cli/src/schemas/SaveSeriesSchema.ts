import { z } from 'zod';

import { SavePersonSchema } from './SavePersonSchema.js';
import { SeriesType, ShowStatus } from '@emdb/common';

export const SaveSeriesSchema = z.object({
  tmdbId: z.number().int().positive(),
  title: z.string(),
  score: z.number().min(0).max(10).nullable(),
  status: z.enum(Object.values(ShowStatus) as [string, ...string[]]), 
  type: z.enum(Object.values(SeriesType) as [string, ...string[]]),
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
      person: SavePersonSchema,
      roles: z.array(z.object({
        character: z.string(),
        episodeCount: z.number().int().nonnegative(),
      })),
      order: z.number().int(),
    })),
    crew: z.array(z.object({
      person: SavePersonSchema,
      jobs: z.array(z.object({
        title: z.string(),
        episodeCount: z.number().int().nonnegative(),
      })),
    })),
  }).optional()
}).strict();

export type SaveSeries = z.infer<typeof SaveSeriesSchema>;
