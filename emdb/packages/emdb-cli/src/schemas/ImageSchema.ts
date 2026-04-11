import { z } from 'zod';

export const ImageSchema = z.object({
  name: z.string().uuid(),
  tmdbName: z.string(),
});