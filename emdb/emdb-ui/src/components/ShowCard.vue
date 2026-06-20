<template>
  <RouterLink :to="link(show)" 
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
    
    <div class="pt-3 pb-1 font-bold">{{ show.title }}</div>
    <div class=" text-sm">{{ show.releaseDate }}</div>
  </RouterLink>
</template>

<script setup lang="ts">
  import { useEmdbQueryApi, ImageSize } from '@/composables/useEmdbQueryApi';
  import { MediaType } from '@/models/MediaType';
  import { type ShowView } from '@/models/ShowView';
  const { findImage } = useEmdbQueryApi();

  defineProps<{
    show: ShowView,
  }>();

  const link = (show: ShowView) => {
    if (show.mediaType === MediaType.MOVIE) {
      return `/movie/${show.id}`;
    } else {
      return `/series/${show.id}`;
    }
  };  
</script>