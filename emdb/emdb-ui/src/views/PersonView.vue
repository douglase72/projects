<template>
  <header class="m-4">
    <div class="text-4xl font-bold mb-4">Person</div>
    <div class="flex flex-col">
      <RouterLink to="/" class="hover:text-zinc-300">Home</RouterLink>
      <RouterLink v-if="person" :to="url()" class="hover:text-zinc-300">Person Edit</RouterLink>
    </div>
  </header>

  <main class="m-8">
    <div v-if="person" class="inline-grid grid-cols-[auto_1fr] gap-x-12 gap-y-2 items-center mt-8">
      <div>ID</div>
      <div>{{ person.id }}</div>
      <div>TMDB ID</div>
      <div>{{ person.tmdbId }}</div>
      <div>Name</div>
      <div>{{ person.name }}</div>
      <div>Birth Date</div>
      <div>{{ person.birthDate }}</div>
      <div>Death Date</div>
      <div>{{ person.deathDate }}</div> 
      <div>Gender</div>
      <div>{{ person.gender }}</div>
      <div>Place of Birth</div>
      <div>{{ person.birthPlace }}</div>
      <div>Profile</div>
      <div v-if="person.profile">
        <img :src="findImage(person.profile, ImageSize.W154)" :alt="person.name">
      </div>  
      <div>Biography</div>
      <div>{{ person.biography }}</div>                 
    </div>
  </main>
</template>

<script setup lang="ts">
  import { onMounted, ref } from 'vue';
  import { useRoute, useRouter } from 'vue-router';

  import { useEmdbApi } from '@/composables/useEmdbApi';
  import { ImageSize } from '@/models/ImageSize';
  import type { Person } from '@/models/Person';

  const { findImage, findPerson } = useEmdbApi();
  const route = useRoute();
  const router = useRouter();

  const person = ref<Person>();

  onMounted(async () => {
    const routeId = route.params.id;
    const id = Number(routeId);
    if (Number.isNaN(id)) {
      router.push('/'); 
      return;
    }
    person.value = await findPerson(id);
  }); 
  
  const url = () => {
    return `/person/${person.value?.id}/edit`;
  }
</script>