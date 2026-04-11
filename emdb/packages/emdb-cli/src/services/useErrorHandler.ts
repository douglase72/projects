import axios from 'axios';
export function useErrorHandler() {

  const handleError = (error: unknown, summary?: string) => {
    let message = 'An unexpected error occurred.';

    if (axios.isAxiosError(error)) {
      const data = error.response?.data as any; 

      if (error.code === 'ECONNABORTED') {
        message = `Request timed out after ${error.config?.timeout} ms.`;
      } 
      else if (error.response) {
        if (data && Array.isArray(data.errors) && data.errors.length > 0) {
          message = `Error (${error.response.status}): ` + data.errors.join('\n  • ');
        } else if (data && data.error) {
          message = `Error (${error.response.status}): ${data.error}`;
        } else {
          message = `Error (${error.response.status}): ${error.response.statusText}`;
        }
      } 
      else if (error.request) {
        message = `Network Error: No response from server. Please check your connection. (${error.message})`;
      } else {
        message = `Axios Setup Error: ${error.message}`;
      }
    } else if (error instanceof Error) {
      message = error.message;
    } else {
      message = String(error);
    }

    if (summary) {
      console.error(`❌ ${summary} ${message}`);
    } else {
      console.error(`❌ ${message}`);
    }
    process.exit(1);
  };

  return { handleError };
}