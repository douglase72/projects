import type { PersonQuery } from '@/gql/graphql';

type PersonCastCredit = NonNullable<PersonQuery['person']>['credits']['cast'][number];

export const CreditType = {
  Movie: 'PersonMovieCastCredit',
  Series: 'PersonSeriesCastCredit',
} as const satisfies Record<string, PersonCastCredit['__typename']>;