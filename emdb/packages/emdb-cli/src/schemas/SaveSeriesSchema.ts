import { z } from 'zod';

import { ImageSchema } from './ImageSchema.js';
import { SavePersonSchema } from './SavePersonSchema.js';
import { SeriesType, ShowStatus } from '@emdb/common';

const RoleSchema = z.object({
  character: z.string().nullable().optional(),
  episodeCount: z.number().int().nonnegative(),
});

const JobSchema = z.object({
  title: z.string().nullable().optional(),
  episodeCount: z.number().int().nonnegative(),
});

const CastCreditSchema = z.object({
  tmdbId: z.number().int().positive(),
  roles: z.array(RoleSchema).nonempty(),
  order: z.number().int().nonnegative(),
});

const CrewCreditSchema = z.object({
  tmdbId: z.number().int().positive(),
  jobs: z.array(JobSchema).nonempty(),
});

const CreditsSchema = z.object({
  cast: z.array(CastCreditSchema),
  crew: z.array(CrewCreditSchema),
});

export const SaveSeriesSchema = z.object({
  tmdbId: z.number().int().positive(),
  title: z.string(),
  score: z.number().min(0).max(10).nullable().optional(),
  status: z.enum(Object.values(ShowStatus) as [string, ...string[]]).optional(), 
  type: z.enum(Object.values(SeriesType) as [string, ...string[]]).optional(),
  homepage: z.string().url().nullable().optional(),
  originalLanguage: z.string().length(2).nullable().optional(),
  backdrop: ImageSchema.nullable().optional(),
  poster: ImageSchema.nullable().optional(),
  tagline: z.string().nullable().optional(),
  overview: z.string().nullable().optional(),
}).strict();

export type SaveSeries = z.infer<typeof SaveSeriesSchema>;
