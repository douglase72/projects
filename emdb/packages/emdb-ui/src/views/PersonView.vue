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
        {{ person.profile }}
      </div>  
      <div>Biography</div>
      <div>{{ person.biography }}</div>
    </section>

    <Button label="Edit" 
            v-if="person && isAdmin" 
            @click="router.push({ name: 'PersonEdit', params: { id: person.id } })" />
  </main>
</template>

<script setup lang="ts">
  import { computed, onMounted, ref } from 'vue';
  import { useRoute, useRouter } from 'vue-router';

  import keycloak from '@/auth/keycloak';
  import { useEmdbApi } from '@/composables/useEmdbApi';
  import { useErrorHandler } from '@/composables/useErrorHandler';
  import type { Person } from '@emdb/common';

  const { findPerson } = useEmdbApi();
  const { handleError } = useErrorHandler();
  const route = useRoute();
  const router = useRouter();

  const person = ref<Person>();
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
    } catch (e) {
      handleError(e, 'Failed to load person');
      router.push('/'); 
    }
  });  
</script>