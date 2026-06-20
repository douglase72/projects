
export enum MediaType {
  MOVIE = "MOVIE",
  PERSON = "PERSON",
  SERIES = "SERIES",
}

export function fromMediaType(type: string): string {
  return MediaType[type as keyof typeof MediaType] ?? type;
}
