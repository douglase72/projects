<template>
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
      <Carousel :value="shows" 
                :numVisible="6" 
                :numScroll="4"
                :showIndicators="false">
        <template #item="slotProps">
          <ShowCard :show="slotProps.data" />
        </template>
      </Carousel>
    </section>    

    <section class="mt-8">
      <Button label="Edit" 
              v-if="person && isAdmin" 
              @click="router.push({ name: 'PersonEdit', params: { id: person.id } })" />
    </section>
  </main>
</template>

<script setup lang="ts">
  import { computed, onMounted, ref } from 'vue';
  import { useRoute, useRouter } from 'vue-router';

  import keycloak from '@/auth/keycloak';
  import { useEmdbApi } from '@/composables/useEmdbApi';
  import { useErrorHandler } from '@/composables/useErrorHandler';
  import { ImageSize } from '@/models/ImageSize';
  import { MediaType } from '@emdb/common';
  import type { Person, PersonCredit } from '@emdb/common';
  import type { Show } from '@/models/Show';

  const { findImage, findPerson } = useEmdbApi();
  const { handleError } = useErrorHandler();
  const route = useRoute();
  const router = useRouter();

  const person = ref<Person>();
  const shows = ref<Show[]>();

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
      person.value = await findPerson(id);
      shows.value = person.value?.credits.cast.slice(0, 18)
        .map((credit: PersonCredit): Show => ({
          id: credit.id,
          title: credit.title,
          poster: credit.poster,
          date: credit.type === MediaType.MOVIE ? credit.releaseDate : credit.firstAirDate,
          score: credit.score,
          type: credit.type,
      }));
    } catch (e) {
      handleError(e, 'Failed to load person');
      router.push('/'); 
    }
  });  
</script>