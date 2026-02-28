<template>
  <RouterLink :to="`/person/${actor.id}`" 
              class="group flex flex-col h-full w-44 rounded-lg border border-black dark:border-zinc-500 overflow-hidden">
    <div class="flex justify-center items-center h-66 overflow-hidden rounded-lg">
      <img v-if="actor.profile"
           :src="findImage(actor.profile, ImageSize.W154)"
           :alt="actor.name" 
           loading="lazy"
           class="w-full h-full object-cover transition-transform duration-300 group-hover:scale-110">
      <div v-else class="flex flex-col items-center justify-center w-full h-full text-zinc-500">
        <svg xmlns="http://www.w3.org/2000/svg" 
             class="w-full h-full opacity-50 transition-transform duration-300 group-hover:scale-110" 
             fill="none" 
             viewBox="0 0 24 24" 
             stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
        </svg>
      </div>
    </div>

    <div class="flex flex-col grow pt-3 px-2 pb-1">
      <div class="font-bold">{{ actor.name }}</div>
      <div class="text-sm">{{ actor.character }}</div>
    </div>
  </RouterLink>
</template>

<script setup lang="ts">
  import { useEmdbApi } from '@/composables/useEmdbApi';
  import type { Actor } from '@/models/Actor';
  import { ImageSize } from '@/models/ImageSize';

  const { findImage } = useEmdbApi();

  defineProps<{
    actor: Actor,
  }>();
</script>