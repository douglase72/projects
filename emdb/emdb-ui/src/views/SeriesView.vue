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
      <div>{{ fromShowStatus(series.status) }}</div> 
      <div>Type</div>
      <div>{{ fromType(series.type) }}</div>
      <div>Backdrop</div>
      <div v-if="series.backdrop">
        <img :src="findImage(series.backdrop, ImageSize.W154)" :alt="series.title">
      </div>  
      <div>Poster</div>
      <div v-if="series.poster">
        <img :src="findImage(series.poster, ImageSize.W92)" :alt="series.title">
      </div>
      <div>Homepage</div>
      <div>{{ series.homepage }}</div>
      <div>Original Language</div>
      <div>{{ fromLanguageCode(series.originalLanguage) }}</div>
      <div>Tagline</div>
      <div>{{ series.tagline }}</div>
      <div>Overview</div>
      <div>{{ series.overview }}</div>                            
    </section>
       <Carousel :value="cast" 
                 :numVisible="6" 
                 :numScroll="4"
                 :showIndicators="false">
        <template #item="slotProps">
          <ActorCard :actor="slotProps.data" />
        </template>         
      </Carousel>
    <section class="mt-8">
 
    </section>    
  </main>
</template>

<script setup lang="ts">
  import { fromShowStatus } from '@/models/ShowStatus';
  import { onMounted, ref } from 'vue';
  import { useRoute, useRouter } from 'vue-router';
  import { useLanguage } from '@/composables/useLanguage';
  import { useEmdbQueryApi, ImageSize  } from '@/composables/useEmdbQueryApi';
  import { useErrorHandler } from '@/composables/useErrorHandler';
  import { useErrors } from '@/composables/useErrors';

  import { type Actor } from '@/models/Actor';
  import ActorCard from '@/components/ActorCard.vue';
  import { Carousel } from 'primevue';
  import { type Series, fromType } from '@/models/Series';

  const { findImage, findSeriesById } = useEmdbQueryApi();
  const { fromLanguageCode } = useLanguage();
  const { handleError } = useErrorHandler();
  const { isResourceNotFound } = useErrors();
  const route = useRoute();
  const router = useRouter();

  const cast = ref<Actor[]>([]);
  const series = ref<Series>();

    onMounted(async () => {
    const id = Number(route.params.id);
    if (Number.isNaN(id)) {
      router.push('/'); 
      return;
    }

    try {
      series.value = await findSeriesById(id);
      cast.value = series.value?.credits.cast.slice(0, 12)
        .map((credit): Actor => ({
          id: credit.id,
          name: credit.name,
          profile: credit.profile,
          character: credit.roles[0]?.character ?? null,
          totalEpisodes: credit.totalEpisodes,
        }));      
    } catch (e) {
      if (isResourceNotFound(e)) {
        handleError(e, 'Series not found', 'warn');
      } else {
        handleError(e, 'Failed to load series');
      }      
      router.push('/'); 
    }
  });  
</script>