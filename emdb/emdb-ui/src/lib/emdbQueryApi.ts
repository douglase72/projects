import ApolloLinkTimeout from 'apollo-link-timeout'
import { ApolloClient, InMemoryCache, createHttpLink, type DefaultOptions } from '@apollo/client/core'
import type { ResultOf } from '@graphql-typed-document-node/core'
import { graphql } from '@/gql'

export enum ImageSize {
  W45  = 'w45',
  W92  = 'w92',
  W154 = 'w154',
  W185 = 'w185',
  W300 = 'w300',
  W342 = 'w342',
  W500 = 'w500',
  W780 = 'w780',
  W1280 = 'w1280',
  ORIGINAL = 'original',
}

export const MovieDocument = graphql(`
  query Movie($id: BigInteger!) {
    movie(id: $id) {
      id tmdbId title releaseDate score status runtime budget revenue
      backdrop poster homepage originalLanguage tagline overview
      credits { cast { id name profile character order } }
    }
  }
`)
export type Movie = NonNullable<ResultOf<typeof MovieDocument>['movie']>

export const PersonDocument = graphql(`
  query Person($id: BigInteger!) {
    person(id: $id) {
      id tmdbId name birthDate deathDate gender profile birthPlace biography
      credits { 
        cast { ... on PersonMovieCastCredit { id title releaseDate score poster character type } } }
    }
  }
`)
export type Person = NonNullable<ResultOf<typeof PersonDocument>['person']>

const defaultOptions: DefaultOptions = {
  query: {
    fetchPolicy: 'network-only',
    errorPolicy: 'none',
  },
}
const timeoutLink = new ApolloLinkTimeout(10_000)
const httpLink = createHttpLink({ uri: import.meta.env.VITE_EMDB_QUERY_URL })
const client = new ApolloClient({
  link: timeoutLink.concat(httpLink),
  cache: new InMemoryCache(),
  defaultOptions: defaultOptions,
});

export const findImage = (image: string, size: ImageSize) => {
  return `${import.meta.env.VITE_IMAGE_URL}/${size}/${image}`;
};  

export const findMovie = async (id: number): Promise<Movie> => {
  const { data } = await client.query({
    query: MovieDocument,
    variables: { id },
  });
  return data.movie!;
};

export const findPerson = async (id: number): Promise<Person> => {
  const { data } = await client.query({
    query: PersonDocument,
    variables: { id },
  });
  return data.person!;
};