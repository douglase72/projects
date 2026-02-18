<template>
  <header class="m-4">
    <div class="text-4xl font-bold mb-4">Ingest</div>
    <div class="flex flex-col">
      <RouterLink to="/" class="hover:text-zinc-300">Home</RouterLink>
    </div>
  </header>

  <main class="m-8">
    <section class="flex gap-x-8 mt-12 items-start">
      <Fieldset legend="Ingest">
        <div class="inline-grid grid-cols-[1fr_auto] gap-y-4 gap-x-6">
          <InputGroup>
            <InputNumber v-model="movieId" inputId="movie" placeholder="TMDB Movie ID"
                         :min="1" :useGrouping="false" /> 
            <Button label="Ingest" icon="pi pi-check" @click="ingestMovie" :disabled="!movieId" />                  
          </InputGroup>
          <Button label="Movie Scheduler" icon="pi pi-check" @click="executeMovieScheduler" />

          <InputGroup>
            <InputNumber v-model="seriesId" inputId="series" placeholder="TMDB Series ID"
                         :min="1" :useGrouping="false" size="small" /> 
            <Button label="Ingest" icon="pi pi-check" @click="ingestSeries" :disabled="!seriesId" />        
          </InputGroup>
          <Button label="Series Scheduler" icon="pi pi-check" @click="executeSeriesScheduler" />
          
          <InputGroup>
            <InputNumber v-model="personId" inputId="person" placeholder="TMDB Person ID"
                         :min="1" :useGrouping="false" size="small" /> 
            <Button label="Ingest" icon="pi pi-check" @click="ingestPerson" :disabled="!personId" />        
          </InputGroup>
          <Button label="Execute Person Cron" icon="pi pi-check" @click="executePersonScheduler" />    
        </div>
      </Fieldset>
    </section>

    <section class="mt-10">
      <DataTable :value="jobs"
                 dataKey="id"
                 v-model:expandedRows="expandedRows"
                 paginator :rows="10" :rowsPerPageOptions="[5, 10, 20, 50]"
                 tableStyle="min-width: 50rem">

        <Column expander style="width: 5rem" />

        <Column field="timestamp" header="Timestamp">
          <template #body="slotProps">
            {{ toDateTime(slotProps.data.timestamp) }}
          </template>          
        </Column>

        <Column field="status" header="Status">
          <template #body="slotProps">
            <Tag :value="slotProps.data.status" 
                 :severity="ingestStatus(slotProps.data.status)" 
                 rounded />
          </template>
        </Column>

        <Column field="name" header="Name">
          <template #body="slotProps">
            <RouterLink v-if="slotProps.data.emdbId"
                        :to="link(slotProps.data.type, slotProps.data.emdbId)"
                        class="hover:text-zinc-300">
              {{ slotProps.data.name }}
            </RouterLink>
          </template>   
        </Column> 
        
        <Column field="tmdbId" header="TMDB ID" />
        
        <Column field="type" header="Media Type" />

        <template #expansion="slotProps">
          <div class="mx-25">
            <DataTable :value="slotProps.data.history">
              <template #header>Job Id: {{ slotProps.data.id }}</template>
              <Column field="timestamp" header="Timestamp">
                <template #body="slotProps">
                  {{ toDateTime(slotProps.data.timestamp) }}
                </template>          
              </Column>
              <Column field="status" header="Status">
                <template #body="slotProps">
                  <Tag :value="slotProps.data.status" 
                       :severity="ingestStatus(slotProps.data.status)" 
                       rounded />
                </template>
              </Column>              
              <Column field="message" header="Message" />
            </DataTable>
          </div>
        </template>
        
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

  import { useEmdbApi } from '@/composables/useEmdbApi';
  import { useErrorHandler } from '@/composables/useErrorHandler';
  import { useTime } from '@/composables/useTime';
  import { ConnectionStatus, type ConnectionStatusValue } from '@/models/ConnectionStatus';
  import { type IngestJob, IngestStatus} from '@/models/IngestJob';
  import { type IngestMedia, IngestSource, MediaType } from '@emdb/common';

  const { executeMovieScheduler, executePersonScheduler, executeSeriesScheduler, ingest } = useEmdbApi();
  const { handleError } = useErrorHandler();
  const { toDateTime } = useTime();

  const movieId = ref(null);
  const personId = ref(null);
  const seriesId = ref(null);
  const connection = ref<ConnectionStatusValue>(ConnectionStatus.DISCONNECTED);
  let eventSource: EventSource | null = null;
  const jobs = ref<IngestJob[]>([]);
  const expandedRows = ref({});

  const ingestMovie = async () => {
    if (movieId.value !== null) {
      const command: IngestMedia = {
        tmdbId: movieId.value,
        type: MediaType.MOVIE,
        source: IngestSource.UI,
      };
      await ingest(command);
      movieId.value = null;
    }
  };
  
  const ingestPerson = async () => {
    if (personId.value !== null) {
      const command: IngestMedia = {
        tmdbId: personId.value,
        type: MediaType.PERSON,
        source: IngestSource.UI,
      };
      await ingest(command);
      personId.value = null;
    }
  };    
  
  const ingestSeries = async () => {
    if (seriesId.value !== null) {
      const command: IngestMedia = {
        tmdbId: seriesId.value,
        type: MediaType.SERIES,
        source: IngestSource.UI,
      };
      await ingest(command);
      movieId.value = null;
    }
  };   

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
          const existingJob = jobs.value[index];

          if (index !== -1 && existingJob) {
            const historyMap = new Map();
            if (existingJob.history) {
              existingJob.history.forEach(h => historyMap.set(h.status, h));
            }
            if (job.history) {
              job.history.forEach(h => historyMap.set(h.status, h));
            }
            if (!job.history) {
              historyMap.set(job.status, { 
                status: job.status, 
                timestamp: job.timestamp, 
                source: job.source,
                message: job.message });
            } 
            
            existingJob.history = Array.from(historyMap.values()).sort((a, b) => 
              new Date(a.timestamp).getTime() - new Date(b.timestamp).getTime()
            );            
            const incomingTime = new Date(job.timestamp).getTime();
            const existingTime = new Date(existingJob.timestamp).getTime();
            if (incomingTime > existingTime) {
              existingJob.status = job.status;
              existingJob.timestamp = job.timestamp;
              existingJob.source = job.source;
              existingJob.message = job.message;
              if (job.emdbId) existingJob.emdbId = job.emdbId;
              if (job.name) existingJob.name = job.name;
            }
          } else {
            if (!job.history) {
              job.history = [{ 
                status: job.status, 
                timestamp: job.timestamp, 
                source: job.source,
                message: job.message }];
            } else {
              job.history.sort((a, b) => 
                new Date(a.timestamp).getTime() - new Date(b.timestamp).getTime()
              );              
            }
            jobs.value.unshift(job);           
          }

          jobs.value.sort((a, b) => 
            new Date(b.timestamp).getTime() - new Date(a.timestamp).getTime()
          );           
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