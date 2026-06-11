<template>
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
      <div>{{ fromShowStatus(movie.status) }}</div>
      <div>Runtime</div>
      <div>{{ movie.runtime }}</div>
      <div>Budget</div>
      <div>{{ movie.budget }}</div> 
      <div>Revenue</div>
      <div>{{ movie.revenue }}</div> 
      <div>Backdrop</div>
      <div>{{ movie.backdrop }}</div> 
      <div>Poster</div>
      <div>{{ movie.poster }}</div>       
      <div>Homepage</div>
      <div>{{ movie.homepage }}</div>
      <div>Original Language</div>
      <div>{{ fromLanguageCode(movie.originalLanguage) }}</div>
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
  import { useLanguage } from '@/composables/useLanguage';
  import { useEmdbQueryApi } from '@/composables/useEmdbQueryApi';
  import { useErrorHandler } from '@/composables/useErrorHandler';
  import { useErrors } from '@/composables/useErrors';

  import { type Movie } from '@/models/Movie';
  import { fromShowStatus } from '@/models/ShowStatus';

  const { findMovieById } = useEmdbQueryApi();
  const { fromLanguageCode } = useLanguage();
  const { handleError } = useErrorHandler();
  const { isResourceNotFound } = useErrors();
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
      movie.value = await findMovieById(id);
    } catch (e) {
      if (isResourceNotFound(e)) {
        handleError(e, 'Movie not found', 'warn');
      } else {
        handleError(e, 'Failed to load movie');
      } 
      router.push('/'); 
    }
  });  
</script>