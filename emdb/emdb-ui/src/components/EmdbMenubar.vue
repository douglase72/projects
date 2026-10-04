<template>
  <Menubar class="px-4 py-2">
    <template #start>
      <RouterLink to="/" class="text-4xl font-bold text-white">EMDB</RouterLink>
    </template>

    <template #end>
      <Button rounded :icon="themeIcon" 
              class="bg-black border-black hover:bg-zinc-600 text-white" 
              @click="toggleTheme" />
    </template>
  </Menubar>
</template>

<script setup lang="ts">
  import { computed, ref } from "vue";

  import { Button, Menubar } from 'primevue';

  const isDark = ref(document.documentElement.classList.contains('dark'));
  const themeIcon = computed(() => isDark.value ? 'pi pi-sun' : 'pi pi-moon');

  const toggleTheme = () => {
    isDark.value = !isDark.value;
    document.documentElement.classList.toggle('dark', isDark.value);
  };
</script>
