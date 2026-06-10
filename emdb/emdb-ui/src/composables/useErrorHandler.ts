import axios from 'axios';
import { useToast } from "primevue/usetoast";

export function useErrorHandler() {
  type Severity = 'warn' | 'error';
  const toast = useToast();

  const handleError = (error: unknown, summary: string, severity: Severity = 'error') => {
    let message = 'An unexpected error occurred.';

    if (axios.isAxiosError(error)) {
      const data = error.response?.data as any; 

      if (data && Array.isArray(data.errors) && data.errors.length > 0) {
        message = data.errors.join('\n');
      } else if (error.response) {
        message = error.response.statusText;
      } else if (error.request) {
        message = 'No response from server. Please check your connection.';
      } else {
        message = error.message;
      }
    } else if (error instanceof Error) {
      message = error.message;
    }
    console.error(error);
    toast.add({ severity, summary, detail: message });
  };

  return { handleError };
}