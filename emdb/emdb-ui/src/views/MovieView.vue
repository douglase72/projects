<template>
  <header class="m-4">
    <div class="text-4xl font-bold mb-4">Movie</div>
    <div class="flex flex-col">
      <RouterLink to="/" class="hover:text-zinc-300">Home</RouterLink>
      <RouterLink v-if="movie" :to="url()" class="hover:text-zinc-300">Movie Edit</RouterLink>
    </div>
  </header>

  <main class="m-8">
    <div v-if="movie" class="inline-grid grid-cols-[auto_1fr] gap-x-12 gap-y-2 items-center mt-8">
      <div>ID</div>
      <div>{{ movie.id }}</div>
      <div>TMDB ID</div>
      <div>{{ movie.tmdbId }}</div>
      <div>Title</div>
      <div>{{ movie.title }}</div>
      <div>Release Date</div>
      <div>{{ movie.releaseDate }}</div>
      <div>Score</div>
      <div>{{ movie.score }}</div>
      <div>Status</div>
      <div>{{ movie.status }}</div>
      <div>Runtime</div>
      <div>{{ movie.runtime }}</div>
      <div>Budget</div>
      <div>{{ movie.budget }}</div> 
      <div>Revenue</div>
      <div>{{ movie.revenue }}</div> 
      <div>Homepage</div>
      <div>{{ movie.homepage }}</div>
      <div>Original Language</div>
      <div>{{ formatLanguage(movie.originalLanguage) }}</div>
      <div>Backdrop</div>
      <div v-if="movie.backdrop">
        <img :src="findImage(movie.backdrop, ImageSize.W154)" :alt="movie.title">
      </div>  
      <div>Poster</div>
      <div v-if="movie.poster">
        <img :src="findImage(movie.poster, ImageSize.W92)" :alt="movie.title">
      </div>        
      <div>Tagline</div>
      <div>{{ movie.tagline }}</div>
      <div>Overview</div>
      <div>{{ movie.overview }}</div>
    </div>
  </main>
</template>

<script setup lang="ts">
  import { onMounted, ref } from 'vue';
  import { useRoute, useRouter } from 'vue-router';

  import { useEmdbApi } from '@/composables/useEmdbApi';
  import { useLanguage } from '@/composables/useLanguage';
  import { ImageSize } from '@/models/ImageSize';
  import type { Movie } from '@/models/Movie';

  const { findImage, findMovie } = useEmdbApi();
  const { formatLanguage } = useLanguage();
  const route = useRoute();
  const router = useRouter();

  const movie = ref<Movie>();

  onMounted(async () => {
    const routeId = route.params.id;
    const id = Number(routeId);
    if (Number.isNaN(id)) {
      router.push('/'); 
      return;
    }
    movie.value = await findMovie(id);
  }); 
  
  const url = () => {
    return `/movie/${movie.value?.id}/edit`;
  }
</script>