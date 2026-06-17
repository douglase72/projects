
export interface OffsetPage<T> {
  results: Array<T>;
  page: number;
  size: number;
  totalResults: number;
}