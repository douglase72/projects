import ApolloLinkTimeout from 'apollo-link-timeout'
import { ApolloClient, InMemoryCache, createHttpLink, type DefaultOptions } from '@apollo/client/core'
import type { ResultOf } from '@graphql-typed-document-node/core'
import { graphql } from '@/gql'

export const MovieDocument = graphql(`
  query Movie($id: String!) {
    movie(id: $id) {
      id version title releaseDate originalLanguage
    }
  }
`)
export type Movie = NonNullable<ResultOf<typeof MovieDocument>['movie']>

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

export const findMovie = async (id: string): Promise<Movie | null> => {
  const { data } = await client.query<{ movie: Movie | null }>({
    query: MovieDocument,
    variables: { id },
  });
  return data.movie;
};
