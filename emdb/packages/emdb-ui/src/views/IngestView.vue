<template>
  <main class="m-8">
    <section class="flex gap-x-8 mt-12 items-start">
      <Fieldset legend="Ingest">
        <div class="inline-grid grid-cols-1 gap-y-4 gap-x-6">
          <InputGroup>
            <InputNumber v-model="movieId" inputId="movie" placeholder="TMDB Movie ID"
                         :min="1" :useGrouping="false" /> 
            <Button label="Ingest" icon="pi pi-check" @click="ingestMovie" :disabled="!movieId" />          
          </InputGroup>

          <InputGroup>
            <InputNumber v-model="seriesId" inputId="series" placeholder="TMDB Series ID"
                         :min="1" :useGrouping="false" /> 
            <Button label="Ingest" icon="pi pi-check" @click="ingestSeries" :disabled="!seriesId" />          
          </InputGroup>

          <InputGroup>
            <InputNumber v-model="personId" inputId="person" placeholder="TMDB Person ID"
                         :min="1" :useGrouping="false" /> 
            <Button label="Ingest" icon="pi pi-check" @click="ingestPerson" :disabled="!personId" />        
          </InputGroup>          
        </div>
      </Fieldset>
    </section>

    <section class="mt-10">
      <DataTable :value="ingests"
                 dataKey="id"
                 lazy
                 paginator
                 :rows="rows"
                 :first="first"
                 :totalRecords="totalRecords"
                 :loading="loading"
                 :rowsPerPageOptions="[10, 20, 50]"
                 @page="onPage"
                 paginatorTemplate="FirstPageLink PrevPageLink PageLinks NextPageLink LastPageLink CurrentPageReport"
                 currentPageReportTemplate="{first} – {last} of {totalRecords}"
                 v-model:expandedRows="expandedRows"
                 @row-expand="onRowExpand">
        <Column expander style="width: 5rem" />

        <Column field="lastModified" header="Time">
          <template #body="slotProps">
            {{ toDateTime(slotProps.data.lastModified) }}
          </template>
        </Column>

        <Column field="id" header="Ingest ID" />

        <Column field="status" header="Status">
          <template #body="slotProps">
            <Tag :value="slotProps.data.status" :severity="ingestStatus(slotProps.data.status)" rounded />            
          </template>
        </Column>

        <Column field="tmdbId" header="TMDB ID" />

        <Column field="type" header="Media Type" />

        <Column field="name" header="Name" class="min-w-60" />

        <template #expansion="slotProps">
          <div class="mx-25">
            <DataTable :value="historyByIngest[slotProps.data.id]?.changes ?? []"
                       :loading="historyLoading[slotProps.data.id]">
              <Column field="lastModified" header="Time">
                <template #body="changeProps">
                  {{ toDateTime(changeProps.data.lastModified) }}
                </template>
              </Column>

              <Column field="status" header="Status">
                <template #body="changeProps">
                  <Tag :value="changeProps.data.status"
                       :severity="ingestStatus(changeProps.data.status)"
                       rounded />
                </template>
              </Column>

              <Column field="source" header="Source" />

              <Column field="message" header="Message" />
            </DataTable>
          </div>
        </template>

        <template #footer>
          <div class="flex justify-end">
            <Tag :value="connection" :severity="connectionStatus(connection)" rounded />
          </div>
        </template>        
      </DataTable>
    </section>
  </main>
</template>

<script setup lang="ts">
  import { onMounted, onUnmounted, ref } from 'vue';
  import { useRouter } from 'vue-router';
  import type { DataTablePageEvent, DataTableRowExpandEvent } from 'primevue/datatable';

  import { type Ingest, type IngestHistory, IngestStatus } from '@/models/Ingest';
  import { type IngestMedia, IngestSource, MediaType } from '@emdb/common';
  import { useEmdbApi } from '@/composables/useEmdbApi';
  import { useErrorHandler } from '@/composables/useErrorHandler';
  import { useStatus, ConnectionStatus } from '@/composables/useStatus';
  import { useTime } from '@/composables/useTime';

  const router = useRouter();
  const { connectionStatus, ingestStatus } = useStatus();
  const { findAllIngests, findIngestHistory, ingest } = useEmdbApi();
  const { handleError } = useErrorHandler();
  const { toDateTime } = useTime();

  const movieId = ref(null);  
  const seriesId = ref(null);
  const personId = ref(null);
  const ingests = ref<Ingest[]>([]);
  const historyByIngest = ref<Record<string, IngestHistory>>({});
  const first = ref(0);
  const rows = ref(20);
  const totalRecords = ref(0);
  const loading = ref(false);
  const historyLoading = ref<Record<string, boolean>>({});
  const expandedRows = ref({});
  const connection = ref(ConnectionStatus.DISCONNECTED);

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
  
  const loadIngests = async (pageZeroBased: number, size: number) => {
    loading.value = true;
    try {
      // Backend is 1-based, PrimeVue is 0-based
      const page = await findAllIngests(pageZeroBased + 1, size);
      ingests.value = page.results;
      totalRecords.value = page.totalResults;
    } catch (e) {
      handleError(e, 'Failed to load ingests');
      router.push('/');
    } finally {
      loading.value = false;
    }
  };

  const onPage = (event: DataTablePageEvent) => {
    first.value = event.first;
    rows.value = event.rows;
    loadIngests(event.page ?? 0, event.rows);
  };

  const onRowExpand = async (event: DataTableRowExpandEvent) => {
    const ingest = event.data as Ingest;
    if (historyByIngest.value[ingest.id]) {
      return;
    }
    historyLoading.value[ingest.id] = true;
    try {
      historyByIngest.value[ingest.id] = await findIngestHistory(ingest.id);
    } catch (e) {
      handleError(e, 'Failed to load ingest history');
    } finally {
      historyLoading.value[ingest.id] = false;
    }
  };  

  onMounted(async () => {
    await loadIngests(0, rows.value);

    const url = `${import.meta.env.VITE_API_URL}/ingests/stream`;
    eventSource = new EventSource(url);

    eventSource.onopen = () => {
      console.log("Connected to emdb-gateway-service");
      connection.value = ConnectionStatus.CONNECTED;
    };

    eventSource.onerror = (err) => {
      connection.value = ConnectionStatus.DISCONNECTED;
      console.warn("Connection lost", err);
    };
    
    eventSource.onmessage = (event) => {
      try {
        const incoming: Ingest = JSON.parse(event.data);
        const index = ingests.value.findIndex(i => i.id === incoming.id);

        if (index === -1) {
          // New ingest — prepend so it appears at the top
          if (first.value === 0) {
            ingests.value.unshift(incoming);
          }
          totalRecords.value += 1;
        } else {
          // Existing ingest — only update if the incoming event is newer
          const existing = ingests.value[index]!;
          if (incoming.lastModified >= existing.lastModified) {
            ingests.value[index] = incoming;
          }
        }

        const cached = historyByIngest.value[incoming.id];
        if (cached) {
          cached.changes.unshift({
            status: incoming.status,
            lastModified: incoming.lastModified,
            source: incoming.source,
            message: incoming.message,
          });
        }        
      } catch (e) {
        handleError(e, 'Failed to process event.');
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