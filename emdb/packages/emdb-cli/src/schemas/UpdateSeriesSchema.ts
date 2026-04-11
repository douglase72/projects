import { z } from 'zod';

import { SeriesType, ShowStatus } from '@emdb/common';

export const UpdateSeriesSchema = z.object({
  title: z.string().optional(),
  score: z.number().min(0).max(10).nullable().optional(),
  status: z.enum(Object.values(ShowStatus) as [string, ...string[]]).optional(), 
  type: z.enum(Object.values(SeriesType) as [string, ...string[]]).optional(),
  backdrop: z.string().uuid().nullable().optional(),
  poster: z.string().uuid().nullable().optional(),
  homepage: z.string().url().nullable().optional(),
  originalLanguage: z.string().length(2).nullable().optional(),
  tagline: z.string().nullable().optional(),
  overview: z.string().nullable().optional(),
}).strict();

export type UpdateSeries = z.infer<typeof UpdateSeriesSchema>;