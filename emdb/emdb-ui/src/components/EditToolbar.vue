<template>
  <div class="flex gap-4">
    <Button label="Save" icon="pi pi-check" @click="$emit('save')" :disabled="!isValid || isSubmitting" />
    <Button label="Export" icon="pi pi-download" @click="$emit('export')" :disabled="!isValid || isSubmitting" />
    
    <div class="ml-auto flex gap-4">
      <Button label="Delete" icon="pi pi-trash" @click="confirmDelete" />
      <Button label="Cancel" severity="secondary" @click="onCancel" />
    </div>  
  </div>

  <ConfirmDialog />
</template>

<script setup lang="ts">
  import { useConfirm } from "primevue/useconfirm";
  import { useRouter } from 'vue-router'; 
  
  const confirm = useConfirm();
  const router = useRouter();

  const props = defineProps<{
    title?: string;
    isValid: boolean;
    isSubmitting: boolean;
  }>();

  const emit = defineEmits<{
    (e: 'save'): void;
    (e: 'export'): void;
    (e: 'delete'): void;
  }>(); 

  const confirmDelete = () => {
    confirm.require({
      header: 'Confirm Delete',
      message: `Delete ${props.title}?`,
      icon: 'pi pi-exclamation-triangle',
      rejectLabel: 'Cancel',
      acceptLabel: 'Delete',
      rejectClass: 'p-button-secondary p-button-outlined',
      accept: async () => {
        emit('delete');
      },
    });
  };  

  const onCancel = () => {
    router.back();
  };
</script>