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
      <div v-if="movie.backdrop">
        <img :src="findImage(movie.backdrop, ImageSize.W154)" :alt="movie.title">
      </div>  
      <div>Poster</div>
      <div v-if="movie.poster">
        <img :src="findImage(movie.poster, ImageSize.W92)" :alt="movie.title">
      </div>
      <div>Homepage</div>
      <div>{{ movie.homepage }}</div>
      <div>Original Language</div>
      <div>{{ fromLanguageCode(movie.originalLanguage) }}</div>        
      <div>Tagline</div>
      <div>{{ movie.tagline }}</div>
      <div>Overview</div>
      <div>{{ movie.overview }}</div>
    </section>

    <section class="mt-8">
       <Carousel :value="cast" 
                 :numVisible="6" 
                 :numScroll="4"
                 :showIndicators="false">
        <template #item="slotProps">
          <ActorCard :actor="slotProps.data" />
        </template>         
      </Carousel>     
    </section>
  </main>
</template>

<script setup lang="ts">
  import { fromShowStatus } from '@/models/ShowStatus';
  import { onMounted, ref } from 'vue';
  import { useRoute, useRouter } from 'vue-router';
  import { useLanguage } from '@/composables/useLanguage';
  import { useEmdbQueryApi, ImageSize } from '@/composables/useEmdbQueryApi';
  import { useErrorHandler } from '@/composables/useErrorHandler';

  import { type Actor } from '@/models/Actor';
  import ActorCard from '@/components/ActorCard.vue';
  import { Carousel } from 'primevue';
  import { type Movie } from '@/models/Movie';

  const { findImage, findMovie } = useEmdbQueryApi();
  const { fromLanguageCode } = useLanguage();
  const { handleError, isResourceNotFound } = useErrorHandler();
  const route = useRoute();
  const router = useRouter();

  const cast = ref<Actor[]>([]);
  const movie = ref<Movie>();

  onMounted(async () => {
    const id = Number(route.params.id);
    if (Number.isNaN(id)) {
      router.push('/'); 
      return;
    }

    try {
      movie.value = await findMovie(id);
      cast.value = movie.value?.credits.cast.slice(0, 12)
        .map((credit): Actor => ({
          id: credit.id,
          name: credit.name,
          profile: credit.profile,
          character: credit.character ?? null,
          totalEpisodes: null,
        }));
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