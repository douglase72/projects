import { z } from 'zod';

export const PersonSchema = z.object({
  tmdbId: z.number().int().positive(),
  name: z.string(),
  birthDate: z.string().nullable(),
  deathDate: z.string().nullable(),
  gender: z.string().nullable(),
  birthPlace: z.string().nullable(),
  profile: z.string().uuid().nullable(),
  tmdbProfile: z.string().nullable(),
  biography: z.string().nullable(),
});

export type SavePerson = z.infer<typeof PersonSchema>;