import axios from 'axios'
import { useToast } from 'primevue/usetoast';
import { ApolloError } from '@apollo/client/core';

export function useNotificationService() {
  const toast = useToast();

  const info = (summary: string) => {
    toast.add({ severity: 'info', summary: summary });
  }

  const warn = (detail: string) => {
    toast.add({ severity: 'warn', summary: 'Not found', detail: detail });
    console.warn(detail);    
  }

  const error = (summary:string, e: unknown) => {
    if (isTimeout(e)) {
      toast.add({ severity: 'error', summary: 'Timeout error', detail: 'The server took too long to respond.' });
      return
    }
    const message = serverMessage(e);
    toast.add({ severity: 'error', summary: summary, detail: message })
    console.error(message);
  }

  return { info, error, warn }
}

function isTimeout(e: unknown): boolean {
  if (e instanceof ApolloError) return e.networkError?.name === 'TimeoutError'
  return (e as { name?: string } | null)?.name === 'TimeoutError'
}

function serverMessage(e: unknown): string | undefined {
  if (axios.isAxiosError(e)) {
    const data = e.response?.data;
    if (typeof data === 'string' && data.length > 0) return data;
    if (data && typeof data === 'object') { 
      const obj = data as { error?: string; message?: string }
      return obj.error ?? obj.message ?? e.message;
    }
    return e.message;
  }

  if (!(e instanceof ApolloError)) return e instanceof Error ? e.message : undefined
  const gqlErr = e.graphQLErrors[0];
  if (gqlErr) return gqlErr.message;
  const ne = e.networkError as
    | { result?: { errors?: { message: string }[] }; message?: string }
    | null
  const netGqlErr = ne?.result?.errors?.[0];
  if (netGqlErr) return netGqlErr.message;
  return ne?.message ?? e.message;
}