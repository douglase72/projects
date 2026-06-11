
export enum MediaType {
  MOVIE = "movie",
  PERSON = "person",
  SERIES = "series",
}

export function fromMediaType(type: string): string {
  return MediaType[type as keyof typeof MediaType] ?? type;
}
