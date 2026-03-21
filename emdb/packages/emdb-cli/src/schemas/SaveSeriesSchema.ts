import { z } from 'zod';

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
  score: z.number().min(0).max(10).nullable(),
  status: z.enum(Object.values(ShowStatus) as [string, ...string[]]), 
  type: z.enum(Object.values(SeriesType) as [string, ...string[]]),
  homepage: z.string().url().nullable(),
  originalLanguage: z.string().length(2).nullable(),
  backdrop: z.string().uuid().nullable(),
  poster: z.string().uuid().nullable(),
  tagline: z.string().nullable(),
  overview: z.string().nullable(),
  credits: CreditsSchema,
  people: z.array(SavePersonSchema),
}).strict();

export type SaveSeries = z.infer<typeof SaveSeriesSchema>;
