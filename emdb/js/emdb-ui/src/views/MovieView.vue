<template>
  <main class="m-8">
    <div class="text-4xl font-bold mb-4">Movie</div>
    <div>
      <RouterLink to="/" class="hover:text-zinc-300">Home</RouterLink>
    </div>

    <div v-if="movie" class="inline-grid grid-cols-[auto_1fr] gap-x-12 mt-8">
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
      <div>{{ movie.originalLanguage }}</div>  
      <div>Backdrop</div>
      <div>{{ movie.backdrop }}</div>  
      <div>Poster</div>
      <div>{{ movie.poster }}</div> 
      <div>Tagline</div>
      <div>{{ movie.tagline }}</div>                             
    </div>    
  </main>
</template>

<script setup lang="ts">
  import { onMounted } from 'vue';
  import { ref, type Ref } from 'vue';
  import { useRouter } from 'vue-router';
  import { useToast } from "primevue/usetoast";

  import { type Movie, MovieService } from '@emdb/common';

  const movie: Ref<Movie | undefined> = ref();
  const router = useRouter();
  const service = new MovieService(import.meta.env.VITE_BASE_URL);
  const toast = useToast();

  const props = defineProps<{
    id: number
  }>();

  onMounted(async () => {
    if (Number.isNaN(props.id)) {
      router.push('/');
    } else {
      try {
        movie.value = await service.findById(props.id);
      } catch (e) {
        toast.add({ severity: 'error', summary: 'Error', detail: 'Could not fetch movie' });
        console.error(e);
      }
    }
  });
</script>

<style scoped>

</style>