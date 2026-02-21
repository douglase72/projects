<template>
  <RouterLink :to="link(show.id, show.type)" 
              class="group flex flex-col h-full w-44 rounded-lg overflow-hidden">
    <div class="flex justify-center items-center h-66 overflow-hidden rounded-lg">
      <img v-if="show.poster"
           :src="findImage(show.poster, ImageSize.W154)"
           :alt="show.title" 
           loading="lazy"
           class="w-full h-full object-cover transition-transform duration-300 group-hover:scale-110">
    </div>
    
    <div class="text-center pt-3 px-2 pb-1 font-bold">
      {{ show.title }}
    </div>
  </RouterLink>
</template>

<script setup lang="ts">
  import { useEmdbApi } from '@/composables/useEmdbApi';
  import { ImageSize } from '@/models/ImageSize';
  import type { Show } from '@/models/Show';
import { MediaType } from '@emdb/common';

  const { findImage } = useEmdbApi();

  defineProps<{
    show: Show,
  }>();

  const link = (id: number, type: MediaType) => {
    switch (type) {
      case MediaType.MOVIE: return `/movie/${id}`;
      case MediaType.SERIES: return `/series/${id}`;
      default:
        throw new TypeError(`Invalid type: ${type}`);
    }
  };
</script>