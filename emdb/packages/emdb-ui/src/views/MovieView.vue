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

    <section class="mt-8">
      <Button label="Edit" 
              v-if="movie && isAdmin" 
              @click="router.push({ name: 'MovieEdit', params: { id: movie.id } })" />      
    </section>
  </main>
</template>

<script setup lang="ts">
  import { computed, onMounted, ref } from 'vue';
  import { useRoute, useRouter } from 'vue-router';

  import keycloak from '@/auth/keycloak';
  import { useEmdbApi } from '@/composables/useEmdbApi';
  import { useErrorHandler } from '@/composables/useErrorHandler';
  import { useLanguage } from '@/composables/useLanguage';
  import { ImageSize } from '@/models/ImageSize';
  import type { Actor } from '@/models/Actor';
  import type { Movie, MovieCredit } from "@emdb/common";

  const { findImage, findMovie } = useEmdbApi();
  const { formatLanguage } = useLanguage();
  const { handleError } = useErrorHandler();
  const route = useRoute();
  const router = useRouter();

  const movie = ref<Movie>();
  const cast = ref<Actor[]>();

  const isAdmin = computed(() => {
    return keycloak.authenticated && keycloak.hasRealmRole('admin');
  });

  onMounted(async () => {
    const id = Number(route.params.id);
    if (Number.isNaN(id)) {
      router.push('/'); 
      return;
    }

    try {
      movie.value = await findMovie(id);
      cast.value = movie.value?.credits.cast.slice(0, 18)
        .map((credit: MovieCredit) => ({
          id: credit.id,
          name: credit.name,
          profile: credit.profile,
          character: credit.character,
          numberOfEpisodes: null,
        }));
    } catch (e) {
      handleError(e, 'Failed to load movie');
      router.push('/'); 
    }
  });
</script>