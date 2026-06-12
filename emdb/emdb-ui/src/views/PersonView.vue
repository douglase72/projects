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
      <div>{{ fromGender(person.gender) }}</div>
      <div>Profile</div>
      <div v-if="person.profile">
        <img :src="findImage(person.profile, ImageSize.W154)" :alt="person.name">
      </div>       
      <div>Birth Place</div>
      <div>{{ person.birthPlace }}</div>
      <div>Biography</div>
      <div>{{ person.biography }}</div>           
    </section>
  </main>
</template>

<script setup lang="ts">
  import { onMounted, ref } from 'vue';
  import { useRoute, useRouter } from 'vue-router';
  import { useEmdbQueryApi, ImageSize } from '@/composables/useEmdbQueryApi';
  import { useErrorHandler } from '@/composables/useErrorHandler';
  import { useErrors } from '@/composables/useErrors';

  import { type Person, fromGender } from '@/models/Person';

  const { findImage, findPersonById } = useEmdbQueryApi();
  const { handleError } = useErrorHandler();
  const { isResourceNotFound } = useErrors();
  const route = useRoute();
  const router = useRouter();

  const person = ref<Person>();

  onMounted(async () => {
    const id = Number(route.params.id);
    if (Number.isNaN(id)) {
      router.push('/'); 
      return;
    }

    try {
      person.value = await findPersonById(id);
    } catch (e) {
      if (isResourceNotFound(e)) {
        handleError(e, 'Person not found', 'warn');
      } else {
        handleError(e, 'Failed to load person');
      } 
      router.push('/'); 
    }
  });  
</script>