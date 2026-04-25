<template>
  <main class="m-8">
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
            <Tag :value="slotProps.data.status" 
                 :severity="ingestStatus(slotProps.data.status)" 
                 rounded />            
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
      </DataTable>
    </section>
  </main>
</template>

<script setup lang="ts">
  import { onMounted, ref } from 'vue';
  import { useRouter } from 'vue-router';
  import type { DataTablePageEvent, DataTableRowExpandEvent } from 'primevue/datatable';

  import { type Ingest, type IngestHistory, IngestStatus } from '@/models/Ingest';
  import { useEmdbApi } from '@/composables/useEmdbApi';
  import { useErrorHandler } from '@/composables/useErrorHandler';
  import { useTime } from '@/composables/useTime';

  const router = useRouter();
  const { findAllIngests, findIngestHistory } = useEmdbApi();
  const { handleError } = useErrorHandler();
  const { toDateTime } = useTime();

  const ingests = ref<Ingest[]>([]);
  const historyByIngest = ref<Record<string, IngestHistory>>({});
  const first = ref(0);
  const rows = ref(20);
  const totalRecords = ref(0);
  const loading = ref(false);
  const historyLoading = ref<Record<string, boolean>>({});
  const expandedRows = ref({});

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

  const ingestStatus = (status: IngestStatus) => {
    switch (status) {
      case IngestStatus.COMPLETED: return 'success';
      case IngestStatus.EXTRACTED: return 'info';
      case IngestStatus.FAILED: return 'danger';
      case IngestStatus.STARTED: return 'info';
      case IngestStatus.SUBMITTED: return 'contrast';
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
      return; // already loaded
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

  onMounted(() => loadIngests(0, rows.value));
</script>