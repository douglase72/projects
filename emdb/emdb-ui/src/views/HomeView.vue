<template>
  <main class="flex flex-col m-8">
    <RouterLink to="/ingest" class="hover:text-zinc-300">Ingest</RouterLink>
    <RouterLink to="/person/1" class="hover:text-zinc-300">Person</RouterLink>

    <section class="mt-8">
      <div class="text-lg md:text-2xl font-bold mb-4">Movies</div>
      <Carousel :value="movies" 
                :numVisible="6" 
                :numScroll="4"
                :showIndicators="false">
        <template #item="slotProps">
          <ShowCard :show="slotProps.data" />
        </template>         
      </Carousel>     
    </section>
    
    <section class="mt-20">
      <div class="text-lg md:text-2xl font-bold mb-4">Series</div>
      <Carousel :value="series" 
                :numVisible="6" 
                :numScroll="4"
                :showIndicators="false">
        <template #item="slotProps">
          <ShowCard :show="slotProps.data" />
        </template>         
      </Carousel>     
    </section>    
  </main>
</template>

<script setup lang="ts">
  import { onMounted, ref } from 'vue';
  import { useEmdbQueryApi } from '@/composables/useEmdbQueryApi';
  import { useErrorHandler } from '@/composables/useErrorHandler';

  import { Carousel } from 'primevue';
  import { MediaType } from '@/models/MediaType';
  import ShowCard from '@/components/ShowCard.vue';
  import { type ShowView } from '@/models/ShowView';

  const { findAllMovies, findAllSeries } = useEmdbQueryApi();
  const { handleError, isResourceNotFound } = useErrorHandler();

  const movies = ref<ShowView[]>([]);
  const series = ref<ShowView[]>([]);

  onMounted(async () => {
    [movies.value, series.value] = await Promise.all([getAllMovies(), getAllSeries()]);
  });

  const getAllMovies = async (): Promise<ShowView[]> => {
    try {
      const page = await findAllMovies();
      return page.results.map((movie): ShowView => ({
          id: movie.id,
          title: movie.title,
          releaseDate: movie.releaseDate ?? null,
          score: movie.score,
          poster: movie.poster ?? null,
          mediaType: MediaType.MOVIE,
        }));
    } catch (e) {
      if (isResourceNotFound(e)) {
        handleError(e, 'No movies found', 'warn');
      } else {
        handleError(e, 'Failed to load movies');
      } 
      return [];
    }
  };

  const getAllSeries = async (): Promise<ShowView[]> => {
    try {
      const page = await findAllSeries();
      return page.results.map((series): ShowView => ({
          id: series.id,
          title: series.title,
          releaseDate: series.firstAirDate ?? null,
          score: series.score,
          poster: series.poster ?? null,
          mediaType: MediaType.SERIES,
        }));
    } catch (e) {
      if (isResourceNotFound(e)) {
        handleError(e, 'No series found', 'warn');
      } else {
        handleError(e, 'Failed to load series');
      } 
      return [];
    }
  };
</script>