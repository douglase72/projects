import { z } from 'zod';

export const UpdatePersonSchema = z.object({
  name: z.string(),
  birthDate: z.string().optional().nullable(),
  deathDate: z.string().optional().nullable(),
  gender: z.string().optional().nullable(),
  birthPlace: z.string().optional().nullable(),
  profile: z.string().uuid().optional().nullable(),
  homepage: z.string().url().optional().nullable(),
  biography: z.string().optional().nullable(),
});

export type UpdatePerson = z.infer<typeof UpdatePersonSchema>;