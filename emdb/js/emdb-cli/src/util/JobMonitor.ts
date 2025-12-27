import { EventSource } from 'eventsource';
import { sprintf } from 'sprintf-js';

import { Job, JobStatus } from '@emdb/common';

export function monitorJob(id: string): Promise<void> {
  return new Promise((resolve, reject) => {
    const url = `${process.env.BASE_URL}/jobs/${id}`;
    const eventSource = new EventSource(url);

    eventSource.onmessage = (event: MessageEvent) => {
      const job: Job = JSON.parse(event.data);
      if (job.status !== JobStatus.HEARTBEAT) {
        const progress = `[${job.progress}%]`;
        console.log(sprintf("%-7s %-10s %s", progress, job.status, job.content));

        // Close the connection.
        if (job.status === JobStatus.COMPLETED) {
          eventSource.close();
          resolve();
        } else if (job.status === JobStatus.FAILED) {
          eventSource.close();
          reject(new Error(`Job ${id} Failed`));
        }
      }
    }

    eventSource.onerror = (error: any) => {
      console.log('Raw Error:', error);
      eventSource.close();
      reject(new Error('Failed to connect.'));
    }   
  });
}