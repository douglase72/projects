<template>
  <header class="m-4">
    <div class="text-4xl font-bold mb-4">EMDB</div>
    <div class="flex flex-col">
      <RouterLink to="/ingest" class="hover:text-zinc-300">Ingest</RouterLink>
    </div>
  </header>

  <main class="flex flex-col m-8">
    <section>
      <Button label="Import" icon="pi pi-upload" @click="showImport = true" />
      <ImportDialog v-model:visible="showImport" />
    </section>
  </main>
</template>

<script setup lang="ts">
  import { ref } from "vue";

  import ImportDialog from '@/components/ImportDialog.vue';

  const showImport = ref(false);

</script>

<style scoped>

</style>