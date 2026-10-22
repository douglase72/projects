<template>
  <main class="m-8">
    <section class="flex gap-x-8 mt-12 items-start">
      <Fieldset legend="Ingest">
        <div class="inline-grid grid-cols-[1fr_auto] gap-y-4 gap-x-6 items-center">
          <div>TMDB Movie</div>
          <InputGroup>
            <InputNumber inputId="movie" v-model="movieId" placeholder="ID" :min="1" :useGrouping="false" /> 
            <Button label="Ingest" icon="pi pi-check" @click="ingestMovie" :disabled="!movieId" />            
          </InputGroup>

          <div>TMDB Series</div>
          <InputGroup>
            <InputNumber inputId="series" placeholder="ID" :min="1" :useGrouping="false" /> 
            <Button label="Ingest" icon="pi pi-check" />            
          </InputGroup> 
          
          <div>TMDB Person</div>
          <InputGroup>
            <InputNumber inputId="person" placeholder="ID" :min="1" :useGrouping="false" /> 
            <Button label="Ingest" icon="pi pi-check" />            
          </InputGroup>          
        </div>
      </Fieldset>
    </section>
  </main>
</template>

<script setup lang="ts">
  import { ref } from 'vue';
  import { Button, Fieldset, InputGroup, InputNumber } from 'primevue';

  import { type IngestRequest, ingest } from '@/lib/emdbIngestApi';
  import { MediaType } from '@/models/MediaType';
  import { useNotificationService } from '@/composables/useNotificationService';

  const movieId = ref(null);
  const notify = useNotificationService();

  const ingestMovie = async () => {
    if (movieId.value !== null) {
      try {
        const request: IngestRequest = {
          tmdbId: movieId.value,
          mediaType: MediaType.MOVIE,
        };
        const ingestId = await ingest(request);
        notify.info(`Ingest job submitted: ${ingestId}`);
        movieId.value = null;
      } catch (e) {
        notify.error('Failed to ingest movie', e);
      }
    }
  };
</script>