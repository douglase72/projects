import axios from 'axios';
import { useToast } from "primevue/usetoast";

export function useErrorHandler() {
  const toast = useToast();

  const handleError = (error: unknown, summary: string) => {
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
    toast.add({ severity: 'error', summary: summary, detail: message });
  };

  return { handleError };
}