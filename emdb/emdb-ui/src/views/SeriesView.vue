<template>
  <main class="m-8">
    <section v-if="series" class="inline-grid grid-cols-[auto_1fr] gap-x-12 gap-y-2 items-center mt-8">
      <div>ID</div>
      <div>{{ series.id }}</div>
      <div>TMDB ID</div>
      <div>{{ series.tmdbId }}</div>
      <div>Title</div>
      <div>{{ series.title }}</div>
      <div>First Air Date</div>
      <div>{{ series.firstAirDate }}</div>
      <div>Last Air Date</div>
      <div>{{ series.lastAirDate }}</div>
      <div>Score</div>
      <div>{{ series.score }}</div> 
      <div>Status</div>
      <div>{{ fromShowStatus(series.status) }}</div> 
      <div>Type</div>
      <div>{{ fromType(series.type) }}</div>
      <div>Backdrop</div>
      <div>{{ series.backdrop }}</div> 
      <div>Poster</div>
      <div>{{ series.poster }}</div>
      <div>Homepage</div>
      <div>{{ series.homepage }}</div>
      <div>Original Language</div>
      <div>{{ fromLanguageCode(series.originalLanguage) }}</div>
      <div>Tagline</div>
      <div>{{ series.tagline }}</div>
      <div>Overview</div>
      <div>{{ series.overview }}</div>                            
    </section>
  </main>
</template>

<script setup lang="ts">
  import { onMounted, ref } from 'vue';
  import { useRoute, useRouter } from 'vue-router';
  import { useLanguage } from '@/composables/useLanguage';
  import { useEmdbQueryApi } from '@/composables/useEmdbQueryApi';
  import { useErrorHandler } from '@/composables/useErrorHandler';
  import { useErrors } from '@/composables/useErrors';

  import { type Series, fromType } from '@/models/Series';
  import { fromShowStatus } from '@/models/ShowStatus';

  const { findSeriesById } = useEmdbQueryApi();
  const { fromLanguageCode } = useLanguage();
  const { handleError } = useErrorHandler();
  const { isResourceNotFound } = useErrors();
  const route = useRoute();
  const router = useRouter();

  const series = ref<Series>();

    onMounted(async () => {
    const id = Number(route.params.id);
    if (Number.isNaN(id)) {
      router.push('/'); 
      return;
    }

    try {
      series.value = await findSeriesById(id);
    } catch (e) {
      if (isResourceNotFound(e)) {
        handleError(e, 'Series not found', 'warn');
      } else {
        handleError(e, 'Failed to load series');
      }      
      router.push('/'); 
    }
  });  
</script>