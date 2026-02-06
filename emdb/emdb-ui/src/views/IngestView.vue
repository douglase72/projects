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
                         :min="1" :useGrouping="false" size="small" /> 
            <Button label="Ingest" icon="pi pi-check" @click="ingestMovie" :disabled="!movieId" />        
          </InputGroup>         
          <Button label="Execute Movie Cron" icon="pi pi-check" @click="movieCron" />

          <InputGroup>
            <InputNumber v-model="seriesId" inputId="series" placeholder="TMDB Series ID"
                         :min="1" :useGrouping="false" size="small" /> 
            <Button label="Ingest" icon="pi pi-check" @click="ingestSeries" :disabled="!seriesId" />        
          </InputGroup>
          <Button label="Execute Series Cron" icon="pi pi-check" @click="seriesCron" /> 
          
          <InputGroup>
            <InputNumber v-model="personId" inputId="person" placeholder="TMDB Person ID"
                         :min="1" :useGrouping="false" size="small" /> 
            <Button label="Ingest" icon="pi pi-check" @click="ingestPerson" :disabled="!personId" />        
          </InputGroup>
          <Button label="Execute Person Cron" icon="pi pi-check" @click="personCron" />           
        </div>
      </Fieldset>
    </section>

    <section class="mt-10">
      <DataTable :value="jobs"
       paginator :rows="10" :rowsPerPageOptions="[5, 10, 20, 50]"
       size="small" tableStyle="min-width: 50rem"
      >
        <Column field="status" header="Status">
          <template #body="slotProps">
            <Tag :value="slotProps.data.status" 
                 :severity="ingestStatus(slotProps.data.status)" 
                 rounded />
          </template>
        </Column> 
        <Column field="timestamp" header="Timestamp" class="min-w-50">
          <template #body="slotProps">
            {{ formatTime(slotProps.data.timestamp) }}
          </template>          
        </Column>               
        <Column field="tmdbId" header="TMDB ID" />
        <Column field="name" header="Name">
          <template #body="slotProps">
            <RouterLink :to="url(slotProps.data.type, slotProps.data.emdbId)" class="hover:text-zinc-300">
              {{ slotProps.data.name }}
            </RouterLink>
          </template>   
        </Column>
        <Column field="type" header="Media Type" />
        <Column field="id" header="Job ID" />

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
  import { onMounted, onUnmounted, ref } from 'vue';

  import { useEmdbApi } from '@/composables/useEmdbApi';
  import { useErrorHandler } from '@/composables/useErrorHandler';
  import { ConnectionStatus, type ConnectionStatusValue } from '@/models/ConnectionStatus';
  import { type IngestJob, IngestStatus } from '@/models/IngestJob';
  import { IngestSource, type IngestMedia } from '@/models/IngestMedia';
  import { MediaType } from '@/models/MediaType';

  const { ingest, movieCron, personCron, seriesCron } = useEmdbApi();
  const { handleError } = useErrorHandler();

  const movieId = ref(null);
  const personId = ref(null);
  const seriesId = ref(null);
  const jobs = ref<IngestJob[]>([]);
  const connection = ref<ConnectionStatusValue>(ConnectionStatus.DISCONNECTED);
  let eventSource: EventSource | null = null;

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
      seriesId.value = null;
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

  const formatTime = (time: string) => {
    const d = new Date(time);
    const pad = (n: number) => n.toString().padStart(2, '0');
    const pad3 = (n: number) => n.toString().padStart(3, '0');
    return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}:${pad(d.getSeconds())}.${pad3(d.getMilliseconds())}`;
  }; 
  
  onMounted(() => {
    const url = `${import.meta.env.VITE_API_URL}/ingest/jobs`; 
    eventSource = new EventSource(url);

    eventSource.onopen = () => {
      connection.value = ConnectionStatus.CONNECTED;
      console.log("Connected to emdb-gateway-service");
    };

    eventSource.onmessage = (event) => {
      try {
        // Upsert the job
        const job = JSON.parse(event.data);
        if (job.status !== IngestStatus.HEARTBEAT) {
          const index = jobs.value.findIndex(j => j.id === job.id);
          if (index !== -1) {
            jobs.value[index] = job;
          } else {
            jobs.value.unshift(job);
          }
        }
      } catch (e) {
        handleError(e, 'SSE Failed');
      }   
    };

    eventSource.onerror = (err) => {
      connection.value = ConnectionStatus.DISCONNECTED;
      console.warn("Connection lost, attempting to reconnect...", err);
    };    
  });

  onUnmounted(() => {
    if (eventSource) {
      eventSource.close();
      connection.value = ConnectionStatus.DISCONNECTED;
      console.log("Disconnected from emdb-gateway-service");
    }
  });

  const url = (media: MediaType, id: number) => {
    switch (media) {
      case MediaType.MOVIE: return `/movie/${id}`;
      case MediaType.SERIES: return `/series/${id}`;
      case MediaType.PERSON: return `/person/${id}`;
    }
  }
</script>
