<template>
  <main class="m-8">
    <section v-if="series" class="inline-grid grid-cols-[auto_1fr] gap-x-12 gap-y-2 items-center mt-8">
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
              v-if="series && isAdmin" 
              @click="router.push({ name: 'SeriesEdit', params: { id: series.id } })" />
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
  import type { Series, SeriesCastCredit } from "@emdb/common";

  const { findImage, findSeries } = useEmdbApi();
  const { formatLanguage } = useLanguage();
  const { handleError } = useErrorHandler();
  const route = useRoute();
  const router = useRouter();

  const series = ref<Series>();
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
      series.value = await findSeries(id);
      cast.value = series.value?.credits.cast.slice(0, 18)
        .map((credit: SeriesCastCredit) => ({
          id: credit.id,
          name: credit.name,
          profile: credit.profile,
          character: credit.roles[0]?.character ?? null,
          numberOfEpisodes: credit.totalEpisodes,
        }));
    } catch (e) {
      handleError(e, 'Failed to load series');
      router.push('/'); 
    }
  });
</script>