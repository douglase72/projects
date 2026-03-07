<template>
  <header class="m-8">
    <div class="flex flex-col">
      <RouterLink v-if="movie" :to="`/movie/${movie.id}/edit`" class="hover:text-zinc-300">
        Movie Edit
      </RouterLink>
    </div>
  </header>

  <main class="m-8">
    <section v-if="movie" class="inline-grid grid-cols-[auto_1fr] gap-x-12 gap-y-2 items-center mt-8">
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
        {{ movie.backdrop }}
      </div>  
      <div>Poster</div>
      <div v-if="movie.poster">
        {{ movie.poster }}
      </div>        
      <div>Tagline</div>
      <div>{{ movie.tagline }}</div>
      <div>Overview</div>
      <div>{{ movie.overview }}</div>
    </section>
  </main>
</template>

<script setup lang="ts">
  import { onMounted, ref } from 'vue';
  import { useRoute, useRouter } from 'vue-router';

  import { useEmdbApi } from '@/composables/useEmdbApi';
  import { useErrorHandler } from '@/composables/useErrorHandler';
  import { useLanguage } from '@/composables/useLanguage';
  import type { Movie } from "@emdb/common";

  const { findMovie } = useEmdbApi();
  const { formatLanguage } = useLanguage();
  const { handleError } = useErrorHandler();
  const route = useRoute();
  const router = useRouter();

  const movie = ref<Movie>();

  onMounted(async () => {
    const id = Number(route.params.id);
    if (Number.isNaN(id)) {
      router.push('/'); 
      return;
    }

    try {
      movie.value = await findMovie(id);
    } catch (e) {
      handleError(e, 'Failed to load movie');
      router.push('/'); 
    }
  });
</script>