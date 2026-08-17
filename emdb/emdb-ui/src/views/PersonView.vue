<template>
  <main class="m-8">
        <section v-if="person" class="inline-grid grid-cols-[auto_1fr] gap-x-12 gap-y-2 items-center mt-8">
      <div>ID</div>
      <div>{{ person.id }}</div>
      <div>Version</div>
      <div>{{ person.version }}</div>
      <div>Name</div>
      <div>{{ person.name }}</div>
      <div>Birth Date</div>
      <div>{{ person.birthDate }}</div>
      <div>Death Date</div>
      <div>{{ person.deathDate }}</div>
      <div>Gender</div>
      <div>{{ formatGender(person.gender) }}</div>
      <div>Biography</div>
      <div>{{ person.biography }}</div>           
    </section>
  </main>
</template>

<script setup lang="ts">
  import { onMounted, ref } from 'vue';
  import { useRoute, useRouter } from 'vue-router';

  import { findPerson, type Person } from '@/lib/emdbQueryApi';
  import { formatGender } from '@/lib/formatter';
  import { useErrorHandler } from '@/composables/useErrorHandler';

  const { handleError, handleNotFound } = useErrorHandler();

  const person = ref<Person>();
  const route = useRoute();
  const router = useRouter();

  onMounted(async () => {
    const raw = route.params.id;
    const id = Array.isArray(raw) ? raw[0] : raw;
    if (!id) {
      router.replace('/')
      return;
    }

    try {
      const found = await findPerson(id);
      if (!found) {
        handleNotFound(`No person exists with id ${id}`);
        router.replace('/');
        return;
      }
      person.value = found;
    } catch (e) {
      handleError(e, 'Failed to load person');
      router.replace('/');
    }
  });
</script>