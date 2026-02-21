<template>
  <header class="m-4">
    <div class="text-4xl font-bold mb-4">Person</div>
    <div class="flex flex-col">
      <RouterLink to="/" class="hover:text-zinc-300">Home</RouterLink>
      <RouterLink v-if="person" 
                  :to="`/person/${person.id}/edit`" 
                  class="hover:text-zinc-300">
        Person Edit
      </RouterLink>      
    </div>    
  </header>

  <main class="m-8">
    <section v-if="person" class="inline-grid grid-cols-[auto_1fr] gap-x-12 gap-y-2 items-center mt-8">
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
    </section>

    <section class="mt-8">
      <Carousel :value="credits" 
                :numVisible="6" 
                :numScroll="4"
                :showIndicators="false">
        <template #item="slotProps">
          <ShowCard :show="slotProps.data" />
        </template>
      </Carousel>
    </section>    
  </main>
</template>

<script setup lang="ts">
  import { computed, onMounted, ref } from 'vue';
  import { useRoute, useRouter } from 'vue-router';

  import { useEmdbApi } from '@/composables/useEmdbApi';
  import { ImageSize } from '@/models/ImageSize';
  import { MediaType, type Person, type PersonCredit } from '@emdb/common';
  import type { Show } from '@/models/Show';

  const { findImage, findPerson } = useEmdbApi();
  const route = useRoute();
  const router = useRouter();

  const person = ref<Person>();
  const credits = computed<Show[]>(() => {
    if (!person.value?.credits?.cast) return [];
    return person.value.credits.cast.slice(0, 18).map((credit: PersonCredit) => ({
      id: credit.id,
      title: credit.title,
      poster: credit.poster,
      date: credit.releaseDate,
      score: credit.score,
      type: MediaType.MOVIE,
    }));
  });

  onMounted(async () => {
    const routeId = route.params.id;
    const id = Number(routeId);
    if (Number.isNaN(id)) {
      router.push('/'); 
      return;
    }
    person.value = await findPerson(id);
    console.log(person.value);
  });
</script>