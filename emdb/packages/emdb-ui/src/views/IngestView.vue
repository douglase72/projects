<template>
  <header class="m-4">
    <div class="text-4xl font-bold mb-4">Ingest</div>
    <div class="flex flex-col">
      <RouterLink to="/" class="hover:text-zinc-300">Home</RouterLink>
    </div>
  </header>

  <main class="m-8">
    <section>
      <DataTable :value="jobs"
                 paginator :rows="10" :rowsPerPageOptions="[5, 10, 20, 50]"
                 tableStyle="min-width: 50rem">

        <Column field="status" header="Status">
          <template #body="slotProps">
            <Tag :value="slotProps.data.status" 
                 :severity="ingestStatus(slotProps.data.status)" 
                 rounded />
          </template>
        </Column>

        <Column field="id" header="Job ID" />

        <Column field="timestamp" header="Timestamp" class="min-w-50">
          <template #body="slotProps">
            {{ toDateTime(slotProps.data.timestamp) }}
          </template>          
        </Column> 
        
        <Column field="tmdbId" header="TMDB ID" />

        <Column field="name" header="Name">
          <template #body="slotProps">
            <RouterLink v-if="slotProps.data.emdbId"
                        :to="link(slotProps.data.type, slotProps.data.emdbId)"
                        class="hover:text-zinc-300">
              {{ slotProps.data.name }}
            </RouterLink>
          </template>   
        </Column>
        
        <Column field="type" header="Media Type" />

        <template #footer>
          <div class="flex justify-end">
            <Tag :value="connection.label" :severity="connection.severity" rounded />
          </div>
        </template>        
      </DataTable>     
    </section>
  </main>
</template>

<script setup lang="ts">
  import { onMounted, onUnmounted, ref} from 'vue';

  import { useErrorHandler } from '@/composables/useErrorHandler';
  import { useTime } from '@/composables/useTime';
  import { ConnectionStatus, type ConnectionStatusValue } from '@/models/ConnectionStatus';
  import { type IngestJob, IngestStatus} from '@/models/IngestJob';
  import { MediaType } from '@emdb/common';

  const { handleError } = useErrorHandler();
  const { toDateTime } = useTime();
  const connection = ref<ConnectionStatusValue>(ConnectionStatus.DISCONNECTED);
  let eventSource: EventSource | null = null;
  const jobs = ref<IngestJob[]>([]);

  const ingestStatus = (status: IngestStatus) => {
    switch (status) {
      case IngestStatus.COMPLETED: return 'success';
      case IngestStatus.EXTRACTED: return 'info';
      case IngestStatus.FAILED: return 'danger';
      case IngestStatus.STARTED: return 'info';
      case IngestStatus.SUBMITTED: return 'contrast';
    }
  };
  
  const link = (type: MediaType, id: number) => {
     switch (type) {
      case MediaType.MOVIE: return `/movie/${id}`;
      case MediaType.SERIES: return `/series/${id}`;
      case MediaType.PERSON: return `/person/${id}`;
    }
  };

  onMounted(async () => {
    const url = `${import.meta.env.VITE_API_URL}/ingest/jobs`;
    eventSource = new EventSource(url);

    eventSource.onopen = () => {
      connection.value = ConnectionStatus.CONNECTED;
      console.log("Connected to emdb-gateway-service");
    };

    eventSource.onerror = (err) => {
      connection.value = ConnectionStatus.DISCONNECTED;
      console.warn("Connection lost", err);
    };
    
    eventSource.onmessage = (event) => {
      try {
        const job: IngestJob = JSON.parse(event.data);
        if (job.status !== IngestStatus.HEARTBEAT) {
          const index = jobs.value.findIndex(j => j.id === job.id);
          if (index !== -1) {
            jobs.value[index] = job;
          } else {
            jobs.value.unshift(job);
          }          
        }
      } catch (e) {
        handleError(e, 'Failed to process server sent event.');
      }
    };
  });

  onUnmounted(() => {
    if (eventSource) {
      eventSource.close();
      connection.value = ConnectionStatus.DISCONNECTED;
      console.log("Disconnected from emdb-gateway-service");
    }
  });
</script>