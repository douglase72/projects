<template>
  <RouterLink :to="link(show.id, show.type)" 
              class="group flex flex-col h-full w-44 rounded-lg overflow-hidden">
    <div class="flex justify-center items-center h-66 overflow-hidden rounded-lg">
      <img v-if="show.poster"
           :src="findImage(show.poster, ImageSize.W154)"
           :alt="show.title" 
           loading="lazy"
           class="w-full h-full object-cover transition-transform duration-300 group-hover:scale-110">
      <div v-else class="w-full h-full bg-neutral-800 flex items-center justify-center">
        <svg xmlns="http://www.w3.org/2000/svg" 
            class="w-12 h-12 text-neutral-500" 
            viewBox="0 0 24 24" 
            fill="none" 
            stroke="currentColor" 
            stroke-width="1.5">
          <rect x="2" y="3" width="20" height="14" rx="2" />
          <path d="M8 21h8M12 17v4" />
        </svg>        
      </div>
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
      default: throw new TypeError(`Invalid type: ${type}`);
    }
  };
</script>