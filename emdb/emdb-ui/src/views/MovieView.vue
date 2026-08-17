<template>
   <main class="m-8">
    <section v-if="movie" class="inline-grid grid-cols-[auto_1fr] gap-x-12 gap-y-2 items-center mt-8">
      <div>ID</div>
      <div>{{ movie.id }}</div>
      <div>Version</div>
      <div>{{ movie.version }}</div>      
      <div>Title</div>
      <div>{{ movie.title }}</div>
      <div>Release Date</div>
      <div>{{ movie.releaseDate }}</div>
      <div>Score</div>
      <div>{{ movie.score }}</div>
      <div>Original Language</div>
      <div>{{ fromLanguageCode(movie.originalLanguage) }}</div>
      <div>Overview</div>
      <div>{{ movie.overview }}</div>
    </section>

    <section class="mt-8">
      <Button label="Edit" 
              v-if="movie" 
              @click="router.push({ name: 'MovieEdit', params: { id: movie.id } })" />      
    </section>    
  </main>
</template>

<script setup lang="ts">
  import { onMounted, ref } from 'vue';
  import { useRoute, useRouter } from 'vue-router';
  import Button from 'primevue/button';

  import { findMovie, type Movie } from '@/lib/emdbQueryApi';
  import { useErrorHandler } from '@/composables/useErrorHandler';
  import { useLanguage } from '@/composables/useLanguage';

  const { fromLanguageCode } = useLanguage();
  const { handleError, handleNotFound } = useErrorHandler();

  const movie = ref<Movie>();
  const route = useRoute();
  const router = useRouter();
  
  onMounted(async () => {
    const raw = route.params.id;
    const id = Array.isArray(raw) ? raw[0] : raw;
    if (!id) {
      router.replace('/')
      return;
    }

    try {
      const found = await findMovie(id);
      if (!found) {
        handleNotFound(`No movie exists with id ${id}`);
        router.replace('/');
        return;
      }
      movie.value = found;
    } catch (e) {
      handleError(e, 'Failed to load movie');
      router.replace('/');
    }
  });
</script>