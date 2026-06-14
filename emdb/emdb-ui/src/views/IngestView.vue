<template>
  <main class="m-8">
    <section class="flex gap-x-8 mt-12 items-start">
      <Fieldset legend="Ingest">
        <div class="inline-grid grid-cols-[1fr_auto] gap-y-4 gap-x-6 items-center">
          <div>TMDB Movie</div>
          <InputGroup>
            <InputNumber v-model="movieId" inputId="movie" placeholder="ID" :min="1" :useGrouping="false" /> 
            <Button label="Ingest" icon="pi pi-check" @click="ingestMovie" :disabled="!movieId" />            
          </InputGroup>

          <div>TMDB Series</div>
          <InputGroup>
            <InputNumber v-model="seriesId" inputId="series" placeholder="ID" :min="1" :useGrouping="false" /> 
            <Button label="Ingest" icon="pi pi-check" @click="ingestSeries" :disabled="!seriesId" />            
          </InputGroup>          
        </div>
      </Fieldset>
    </section>
  </main>
</template>

<script setup lang="ts">
  import { ref } from 'vue';
  import { useEmdbCommandApi } from '@/composables/useEmdbCommandApi';
  import { useToast } from "primevue/usetoast";

  import { Button, Fieldset, InputGroup, InputNumber } from 'primevue';
  import { type IngestMedia, IngestSource } from '@/models/IngestMedia';
  import { MediaType } from '@/models/MediaType';

  const { ingest } = useEmdbCommandApi();
  const movieId = ref(null);
  const seriesId = ref(null);
  const toast = useToast();

  const ingestMovie = async () => {
    if (movieId.value !== null) {
      const command: IngestMedia = {
        tmdbId: movieId.value,
        type: MediaType.MOVIE,
        source: IngestSource.UI,
      };
      const ingestId = await ingest(command);
      movieId.value = null;
      toast.add({ severity: 'info', summary: 'Info', detail: `Ingest ${ingestId} submitted.` });
    }
  }; 
  
  const ingestSeries = async () => {
    if (seriesId.value !== null) {
      const command: IngestMedia = {
        tmdbId: seriesId.value,
        type: MediaType.SERIES,
        source: IngestSource.UI,
      };
      const ingestId = await ingest(command);
      seriesId.value = null;
      toast.add({ severity: 'info', summary: 'Info', detail: `Ingest ${ingestId} submitted.` });
    }
  };   
</script>