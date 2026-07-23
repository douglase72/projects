import { useToast } from 'primevue/usetoast'
import { ApolloError } from '@apollo/client/core'

export function useErrorHandler() {
  const toast = useToast()

  const handleError = (e: unknown, fallbackSummary = 'Something went wrong') => {
    if (isTimeout(e)) {
      toast.add({ severity: 'error', summary: 'Timeout error',
                  detail: 'The server took too long to respond. Please try again.' })
      return
    }
    console.error(e);
    toast.add({ severity: 'error', summary: fallbackSummary, detail: serverMessage(e) })
  }

  const handleNotFound = (detail = 'The requested resource does not exist.') => {
     toast.add({ severity: 'warn', summary: 'Not found', detail: detail });
     console.warn(detail);    
  }

  return { handleError, handleNotFound }
}

function isTimeout(e: unknown): boolean {
  if (e instanceof ApolloError) return e.networkError?.name === 'TimeoutError'
  return (e as { name?: string } | null)?.name === 'TimeoutError'
}

function isNotFound(e: unknown): boolean {
  return e instanceof ApolloError &&
    e.graphQLErrors.some(g => g.extensions?.code === 'NOT_FOUND')
}

function serverMessage(e: unknown): string | undefined {
  if (!(e instanceof ApolloError)) return e instanceof Error ? e.message : undefined
  const gqlErr = e.graphQLErrors[0]
  if (gqlErr) return gqlErr.message
  const ne = e.networkError as
    | { result?: { errors?: { message: string }[] }; message?: string }
    | null
  const netGqlErr = ne?.result?.errors?.[0]
  if (netGqlErr) return netGqlErr.message
  return ne?.message ?? e.message
}