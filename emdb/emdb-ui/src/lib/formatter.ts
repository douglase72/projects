import type { Gender } from "@/gql/graphql";

const GenderMap = {
  FEMALE: 'Female',
  MALE: 'Male',
  NON_BINARY: 'Non-binary',
  UNKNOWN: 'Unknown',
}

function fallbackLabel(value: string): string {
  return value.toLowerCase().split('_')
    .map(w => w.charAt(0).toUpperCase() + w.slice(1))
    .join(' ');
}

export function formatGender(gender: Gender): string {
  return GenderMap[gender] ?? fallbackLabel(gender);
}

export const toDate = (iso: string): Date => new Date(
  Number(iso.slice(0, 4)),
  Number(iso.slice(5, 7)) - 1,
  Number(iso.slice(8, 10)),
);

export const toIso = (date: Date): string =>
  `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`;
