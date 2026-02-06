<template>
  <Dialog v-model:visible="isVisible" modal header="Import" class="min-w-100 w-auto"> 
    <div class="flex flex-col gap-y-4">
      <div class="flex gap-4">
        <div class="flex items-center gap-2">
          <RadioButton v-model="importType" inputId="typeMovie" :value="MediaType.MOVIE" />
          <label for="typeMovie">Movie</label>              
        </div>

        <div class="flex items-center gap-2">
          <RadioButton v-model="importType" inputId="typeSeries" :value="MediaType.SERIES" />
          <label for="typeSeries">Series</label>
        </div>

        <div class="flex items-center gap-2">
          <RadioButton v-model="importType" inputId="typePerson" :value="MediaType.PERSON" />
          <label for="typePerson">Person</label>
        </div>        
      </div>

      <div>
        <FileUpload mode="basic" @select="onFileSelect" :disabled="!importType" />
      </div>

      <div class="flex justify-end gap-2">
        <Button label="Cancel" severity="secondary" @click="onCancel" />
        <Button label="Import" icon="pi pi-upload" @click="onImport" :disabled="!selectedFile" />
      </div>
    </div>
  </Dialog>
</template>

<script setup lang="ts">
  import { computed, ref } from "vue";
  import { useRouter } from 'vue-router';
  import type { FileUploadSelectEvent } from 'primevue/fileupload'; 

  import { useErrorHandler } from '@/composables/useErrorHandler';
  import { MediaType } from "@/models/MediaType";

  const { handleError } = useErrorHandler();
  const router = useRouter();

  const routeMap: Record<string, string> = {
    [MediaType.MOVIE]: 'MovieEdit',
    [MediaType.SERIES]: 'SeriesEdit',
    [MediaType.PERSON]: 'PersonEdit'
  };  

  const props = defineProps<{
    visible: boolean;
  }>();

  const emit = defineEmits<{
    (e: 'update:visible', value: boolean): void;
    (e: 'import', selectedFile: string): void;
  }>();  

  const isVisible = computed({
    get: () => props.visible,
    set: (value) => emit('update:visible', value),  
  }); 
  const importType = ref<MediaType | null>(null);
  const selectedFile = ref<File | null>(null);   
    
  const onFileSelect = (event: FileUploadSelectEvent) => {
    selectedFile.value = event.files[0]; 
  }

  const onCancel = () => {
    selectedFile.value = null;
    importType.value = null;
    isVisible.value = false;
  }

  const onImport = () => {
    if (!selectedFile.value || !importType.value) {
      return; 
    }
    const type = importType.value; 
    const reader = new FileReader();

    reader.onload = async (event) => {
      try {
        const content = event.target?.result as string;
        const data = JSON.parse(content);
        await router.push({
          name: routeMap[type], 
          params: { id: data.id || 0 },
          state: { importedData: data } 
        }); 
        selectedFile.value = null;
        importType.value = null;
        isVisible.value = false;
      } catch (error) {
        handleError(error, "Import Failed");
      }     
    };

    reader.readAsText(selectedFile.value);
  };
</script>