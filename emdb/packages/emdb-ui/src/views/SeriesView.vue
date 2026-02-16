<template>
  <header class="m-4">
    <div class="text-4xl font-bold mb-4">Series</div>
    <div class="flex flex-col">
      <RouterLink to="/" class="hover:text-zinc-300">Home</RouterLink>
      <RouterLink v-if="series" 
                  :to="`/series/${series.id}/edit`" 
                  class="hover:text-zinc-300">
        Series Edit
      </RouterLink>      
    </div>    
  </header>

  <main class="m-8">
    <div v-if="series" class="inline-grid grid-cols-[auto_1fr] gap-x-12 gap-y-2 items-center mt-8">
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
      <div>{{ series.status }}</div> 
       <div>Type</div>
      <div>{{ series.type }}</div>
      <div>Homepage</div>
      <div>{{ series.homepage }}</div>
      <div>Original Language</div>
      <div>{{ formatLanguage(series.originalLanguage) }}</div>
      <div>Backdrop</div>
      <div v-if="series.backdrop">
        <img :src="findImage(series.backdrop, ImageSize.W154)" :alt="series.title">
      </div>  
      <div>Poster</div>
      <div v-if="series.poster">
        <img :src="findImage(series.poster, ImageSize.W92)" :alt="series.title">
      </div>
      <div>Tagline</div>
      <div>{{ series.tagline }}</div>
      <div>Overview</div>
      <div>{{ series.overview }}</div>                    
    </div>
  </main>
</template>

<script setup lang="ts">
  import { onMounted, ref } from 'vue';
  import { useRoute, useRouter } from 'vue-router';

  import { useEmdbApi } from '@/composables/useEmdbApi';
  import { useLanguage } from '@/composables/useLanguage';
  import { ImageSize } from '@/models/ImageSize';
  import type { Series } from '@emdb/common';

  const { findImage, findSeries } = useEmdbApi();
  const { formatLanguage } = useLanguage();
  const route = useRoute();
  const router = useRouter();

  const series = ref<Series>();

  onMounted(async () => {
    const routeId = route.params.id;
    const id = Number(routeId);
    if (Number.isNaN(id)) {
      router.push('/'); 
      return;
    }
    series.value = await findSeries(id);
  });
</script>