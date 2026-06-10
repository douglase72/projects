
export const NOT_FOUND = 'not-found';

export class GraphQLError extends Error {
  readonly code?: string;
  constructor(message: string, code?: string) {
    super(message);
    this.name = 'GraphQLError';
    this.code = code;
  }
}

export function useErrors() {

  const isResourceNotFound = (error: unknown): error is GraphQLError => {
    return error instanceof GraphQLError && error.code === NOT_FOUND;
  }

  return {
    isResourceNotFound
  }
}