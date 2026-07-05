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
      <div>{{ formatShowStatus(series.status) }}</div>
      <div>Type</div>
      <div>{{ formatSeriesType(series.type) }}</div>
      <div>Homepage</div>
      <div>{{ series.homepage }}</div>
      <div>Original Language</div>
      <div>{{ fromLanguageCode(series.originalLanguage) }}</div>
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
  </main>
</template>

<script setup lang="ts">
  import { onMounted, ref } from 'vue';
  import { useRoute, useRouter } from 'vue-router';
  import { Carousel } from 'primevue';

  import { findImage, findSeries, ImageSize, type Series } from '@/lib/emdbQueryApi';
  import { formatSeriesType, formatShowStatus } from '@/lib/formatter';
  import { useErrorHandler } from '@/composables/useErrorHandler';
  import { useLanguage } from '@/composables/useLanguage';
  import { type Actor } from '@/models/Actor';
  import ActorCard from '@/components/ActorCard.vue';

  const { fromLanguageCode } = useLanguage();
  const { handleError } = useErrorHandler();
  const route = useRoute();
  const router = useRouter();

  const cast = ref<Actor[]>([]);
  const series = ref<Series>();

  onMounted(async () => {
    const id = Number(route.params.id);
    if (Number.isNaN(id)) {
      router.replace('/')
      return;
    }

    try {
      series.value = await findSeries(id);
      cast.value = series.value.credits.cast.slice(0, 12)
        .map((credit): Actor => ({
          id: credit.id,
          name: credit.name,
          profile: credit.profile,
          character: credit.roles[0]?.character ?? null,
          totalEpisodes: null,
        }));
    } catch (e) {
      handleError(e, 'Failed to load series');
      router.replace('/');
    }
  });
</script>