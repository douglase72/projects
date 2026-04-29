import { IngestStatus } from "@/models/Ingest";

export enum ConnectionStatus {
  CONNECTED = "Connected",
  DISCONNECTED = "Disconnected",
}

export function useStatus() {

  const connectionStatus = (status: ConnectionStatus): string => {
    switch (status) {
      case ConnectionStatus.CONNECTED: return 'success';
      case ConnectionStatus.DISCONNECTED: return 'danger';
    }
  };

  const ingestStatus = (status: IngestStatus): string => {
    switch (status) {
      case IngestStatus.COMPLETED: return 'success';
      case IngestStatus.EXTRACTED: return 'info';
      case IngestStatus.FAILED: return 'danger';
      case IngestStatus.LOADED: return 'info';
      case IngestStatus.STARTED: return 'info';
      case IngestStatus.SUBMITTED: return 'contrast';
    }
  };   

  return {
    connectionStatus,
    ingestStatus
  }
}