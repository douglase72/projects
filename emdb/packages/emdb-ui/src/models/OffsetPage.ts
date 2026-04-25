
export interface OffsetPage<T> {
  results: T[];
  page: number;
  size: number;
  totalResults: number;
}