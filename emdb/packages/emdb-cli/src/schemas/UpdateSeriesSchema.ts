import { z } from 'zod';

import { SeriesType, ShowStatus } from '@emdb/common';

export const UpdateSeriesSchema = z.object({
  title: z.string(),
  score: z.number().min(0).max(10).nullable(),
  status: z.enum(Object.values(ShowStatus) as [string, ...string[]]), 
  type: z.enum(Object.values(SeriesType) as [string, ...string[]]),
  backdrop: z.string().uuid().nullable(),
  poster: z.string().uuid().nullable(),
  homepage: z.string().url().nullable(),
  originalLanguage: z.string().length(2).nullable(),
  tagline: z.string().nullable(),
  overview: z.string().nullable(),
}).strict();

export type UpdateSeries = z.infer<typeof UpdateSeriesSchema>;