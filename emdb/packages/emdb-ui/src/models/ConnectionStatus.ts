
export const ConnectionStatus = {
  CONNECTED: { 
    label: "Connected", 
    severity: "success" 
  },
  DISCONNECTED: { 
    label: "Disconnected", 
    severity: "danger" 
  },
} as const;

export type ConnectionStatusKey = keyof typeof ConnectionStatus;
export type ConnectionStatusValue = typeof ConnectionStatus[ConnectionStatusKey];