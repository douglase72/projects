<template>
  <main class="m-8">
    <div class="text-4xl font-bold mb-4">System</div>
    <div>
      <RouterLink to="/" class="hover:text-zinc-300">Home</RouterLink>
    </div>

    <div class="mt-8">
      <DataTable :value="jobs" 
        sortMode="multiple" :multiSortMeta="defaultSort"
        paginator :rows="16" :rowsPerPageOptions="[8, 16, 32, 64]"
        size="small" tableStyle="min-width: 50rem"
      >
        <Column field="timestamp" header="Time" sortable class="max-w-35">
          <template #body="slotProps">
            {{ formatTime(slotProps.data.timestamp) }}
          </template>
        </Column>
        <Column field="id" header="ID" sortable class="max-w-50.25"></Column>
        <Column field="tmdbId" header="TMDB ID" sortable class="min-w-34"></Column>
        <Column field="source" header="Source" class="min-w-52"></Column>
        <Column field="progress" header="Complete (%)" class="min-w-34"></Column>
        <Column field="status" header="Status">
          <template #body="slotProps">
            <Tag :value="slotProps.data.status" :severity="getStatus(slotProps.data.status)" rounded />
          </template>
        </Column>
        <Column field="content" header="Message"></Column>
        <template #footer>
          <div class="flex justify-end items-center gap-2">
            <Checkbox v-model="connected" inputId="connected-status" binary disabled />
            <label for="connected-status">Connected</label>
          </div>         
        </template>
      </DataTable>
    </div>
  </main>
</template>

<script setup lang="ts">
  import type { DataTableSortMeta } from 'primevue/datatable';
  import { onMounted, onUnmounted, ref } from 'vue';
  import { type Job, JobStatus } from '@emdb/common';

  const jobs = ref<Job[]>([]);
  const connected = ref(false);
  let eventSource: EventSource | null = null;

  /**
   * 1. Group by TMDB ID first (so they stick together)
   * 2. Sort by Time descending (newest logs for that movie at the top)
   */
  const defaultSort = ref<DataTableSortMeta[]>([
    { field: 'timestamp', order: -1 } 
  ]);  

  const formatTime = (time: string) => {
    const d = new Date(time);
    const pad = (n: number) => n.toString().padStart(2, '0');
    const pad3 = (n: number) => n.toString().padStart(3, '0');
    return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}:${pad(d.getSeconds())}.${pad3(d.getMilliseconds())}`;
  };  

  const getStatus = (status: JobStatus) => {
    switch (status) {
      case JobStatus.COMPLETED: return 'success';
      case JobStatus.FAILED: return 'danger';
      case JobStatus.PROGRESS: return 'info';
      case JobStatus.STARTED: return 'warn';
      case JobStatus.SUBMITTED: return 'contrast';
      default: return 'secondary';
    }
  };

  onMounted(() => {
    const url = `${import.meta.env.VITE_BASE_URL}/jobs`; 
    eventSource = new EventSource(url);
    
    eventSource.onopen = () => {
      connected.value = true;
      console.log("Connected to emdb-job-service");
    };

    eventSource.onmessage = (event) => {
      try {
        const job: Job = JSON.parse(event.data);
        jobs.value.unshift(job);
      } catch (e) {
        console.error("Failed to parse SSE message", e);
      }
    };

    eventSource.onerror = (err) => {
      connected.value = false;
      eventSource?.close();
      console.error("SSE Error:", err);
    };
  });

  onUnmounted(() => {
    if (eventSource) {
      // Close the connection when leaving the page.
      eventSource.close();
      connected.value = false;
      console.log("Disconnected from emdb-job-service");
    }
  });
</script>

<style scoped>

</style>