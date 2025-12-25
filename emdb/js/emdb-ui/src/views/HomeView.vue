<template>
  <main class="m-8">
    <h1 class="text-4xl font-bold mb-4">EMDB</h1>

    <div v-if="movie" class="inline-grid grid-cols-[auto_1fr] gap-x-12">
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

    <div class="mt-4">
      <Button label="Submit" @click="findById(1)"/>
    </div>
  </main>
</template>

<script setup lang="ts">
  import axios, { type AxiosInstance } from 'axios';
  import { ref, type Ref } from 'vue';
  import { useToast } from "primevue/usetoast";

  import type { Movie } from '@emdb/common';

  const client: AxiosInstance  = axios.create({
    baseURL: "http://localhost:60330/emdb/api",
  });

  const movie: Ref<Movie | undefined> = ref();
  const toast = useToast();

  async function findById(id: number) {
    try {
      const { data } = await client.get<Movie>(`/movies/${id}?append=credits`);
      movie.value = data;
    } catch (e) {
      toast.add({ severity: 'error', summary: 'Error', detail: 'Could not fetch movie' });
      console.error(e);
    }
  }
</script>

<style scoped>

</style>