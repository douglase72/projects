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
