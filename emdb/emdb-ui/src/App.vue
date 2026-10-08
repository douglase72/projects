<template>
  <EmdbMenubar />
  <RouterView />
  <Toast />
  <ConfirmDialog />
</template>

<script setup lang="ts">
  import ConfirmDialog from 'primevue/confirmdialog';
  import EmdbMenubar from './components/EmdbMenubar.vue';
  import Toast from 'primevue/toast'

</script>

