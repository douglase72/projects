import type { MovieQuery } from '@/gql/graphql'

export type Movie = NonNullable<MovieQuery['movie']>
export type CastCredit = NonNullable<Movie['credits']>['cast'][number]