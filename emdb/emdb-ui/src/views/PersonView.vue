<template>
  <main class="m-8">
    <section v-if="person" class="inline-grid grid-cols-[auto_1fr] gap-x-12 gap-y-2 items-center mt-8">
      <div>ID</div>
      <div>{{ person.id }}</div>
      <div>Name</div>
      <div>{{ person.name }}</div>
      <div>Birth Date</div>
      <div>{{ person.birthDate }}</div>
      <div>Death Date</div>
      <div>{{ person.deathDate }}</div>
      <div>Gender</div>
      <div>{{ formatGender(person.gender) }}</div>
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

  import { findImage, findPerson, ImageSize, type Person } from '@/lib/emdbQueryApi';
  import { formatGender } from '@/lib/formatter';
  import { useErrorHandler } from '@/composables/useErrorHandler'; 
  
  const { handleError } = useErrorHandler();
  const route = useRoute();
  const router = useRouter();

  const person = ref<Person>();

    onMounted(async () => {
    const id = Number(route.params.id);
    if (Number.isNaN(id)) {
      router.replace('/')
      return;
    }

    try {
      person.value = await findPerson(id);
    } catch (e) {
      handleError(e, 'Failed to load person');
      router.replace('/');
    }
  });
</script>