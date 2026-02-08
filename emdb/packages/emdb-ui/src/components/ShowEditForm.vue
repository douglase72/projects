<template>
  <div class="flex flex-col gap-y-6">
    <div class="flex flex-col">
      <label for="title" class="font-bold">Title</label>
      <InputText id="title" v-model="title" v-bind="titleAttrs" :invalid="!!errors.title" />
      <small class="text-red-500">{{ errors.title }}</small> 
    </div>
    
    <div class="flex gap-x-8">
      <slot name="date" :values="values" :errors="errors" :setFieldValue="setFieldValue"></slot>

      <div class="flex flex-col items-start">
        <label for="score" class="font-bold">Score</label>
        <InputNumber inputId="score" v-model="score" :maxFractionDigits="6" :invalid="!!errors.score" />
        <small class="text-red-500">{{ errors.score }}</small>
      </div> 
    
      <div class="flex flex-col items-start">
        <label for="status" class="font-bold">Status</label>
        <Select inputId="status" v-model="status" :options="statusOptions" />
      </div>

      <div class="flex flex-col items-start">
        <label for="language" class="font-bold">Original Language</label>
        <Select v-model="language" 
                :options="languageCodes" 
                optionLabel="label" 
                optionValue="value" 
                filter />
      </div> 
    </div> 
    
    <div class="flex flex-col">
      <label for="homepage" class="font-bold">Homepage</label>
      <InputText id="homepage" v-model="homepage" :invalid="!!errors.homepage" />
      <small class="text-red-500">{{ errors.homepage }}</small>        
    </div> 
    
    <slot name="other" :values="values" :errors="errors" :setFieldValue="setFieldValue"></slot>
    
    <div class="flex gap-x-8">
      <div class="flex flex-col min-w-90">
        <label for="backdrop" class="font-bold">Backdrop</label>
        <InputText id="backdrop" v-model="backdrop" :invalid="!!errors.backdrop" />
        <small class="text-red-500">{{ errors.backdrop }}</small>        
      </div>
      
      <div class="flex flex-col min-w-90">
        <label for="poster" class="font-bold">Poster</label>
        <InputText id="poster" v-model="poster" :invalid="!!errors.poster" />
        <small class="text-red-500">{{ errors.poster }}</small>        
      </div>
    </div>

    <div class="flex flex-col">
      <label for="tagline" class="font-bold">Tagline</label>
      <InputText id="tagline" v-model="tagline" v-bind="taglineAttrs" :invalid="!!errors.tagline" />
      <small class="text-red-500">{{ errors.tagline }}</small>        
    </div>

    <div class="flex flex-col">
      <label for="overview" class="font-bold">Overview</label>
      <Textarea id="overview" v-model="overview" rows="5" :invalid="!!errors.overview" />
      <small class="text-red-500">{{ errors.overview }}</small>
    </div>    
  </div>

  <div class="mt-12">
    <div class="flex gap-4">
      <Button label="Save" icon="pi pi-check" @click="save" :disabled="isSubmitting" />

      <div class="ml-auto flex gap-4">
        <Button label="Delete" icon="pi pi-trash" @click="confirmDelete" />
        <Button label="Cancel" severity="secondary" @click="onCancel" />
      </div>  
    </div>
  </div>

  <ConfirmDialog />
</template>

<script setup lang="ts">
  import { computed, watch } from 'vue';
  import { toTypedSchema } from '@vee-validate/zod';
  import { useConfirm } from "primevue/useconfirm";
  import { useRouter } from 'vue-router'; 
  import { useForm } from 'vee-validate';
  import * as z from 'zod';

  const confirm = useConfirm();
  const router = useRouter();

  import { useEmdbApi } from '@/composables/useEmdbApi';
  import { ShowStatus } from '@emdb/common';
  import { SUPPORTED_CODES, useLanguage } from '@/composables/useLanguage';

  const { toDate } = useEmdbApi();
  const { languageCodes } = useLanguage();
  const statusOptions = Object.values(ShowStatus);  

  const props = defineProps<{
    data?: Record<string, any>;
    specificSchema?: z.ZodObject<any, any>; 
  }>();

  const emit = defineEmits<{
    (e: 'save', values: any): void;
    (e: 'delete', values?: any): void;
  }>();
  
  defineSlots<{
    date(props: { 
      values: Record<string, any>, 
      errors: Record<string, string | undefined>,
      setFieldValue: (field: string, value: any) => void
    }): any

    other(props: { 
      values: Record<string, any>, 
      errors: Record<string, string | undefined>,
      setFieldValue: (field: string, value: any) => void
    }): any    
  }>();   

  const pattern = /^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}(\.jpg)?$/i;
  const validationSchema = computed(() => {
    const schema = z.object({
      title: z.string().min(1, 'Title is required'),
      score: z.number().min(0).max(10).nullable(),
      status: z.nativeEnum(ShowStatus).nullable(),
      homepage: z.string().url().nullable().or(z.literal('')),
      language: z.enum(SUPPORTED_CODES).nullable(),      
      backdrop: z.string().regex(pattern).nullable().or(z.literal('')),
      poster: z.string().regex(pattern).nullable().or(z.literal('')), 
      tagline: z.string().nullable(),
      overview: z.string().nullable(),                    
    });
    return toTypedSchema(props.specificSchema ? schema.merge(props.specificSchema) : schema);  
  });
  
   const { 
    defineField, 
    handleSubmit, 
    errors, 
    resetForm, 
    meta, 
    isSubmitting, 
    values,
    setFieldValue } = useForm({
    validationSchema,
  });
  
  const [title, titleAttrs] = defineField('title');
  const [score] = defineField('score');
  const [status] = defineField('status');
  const [homepage] = defineField('homepage');
  const [language] = defineField('language');  
  const [backdrop] = defineField('backdrop');
  const [poster] = defineField('poster'); 
  const [tagline, taglineAttrs] = defineField('tagline');
  const [overview] = defineField('overview');

  const confirmDelete = () => {
    confirm.require({
      header: 'Confirm Delete',
      message: `Delete ${props.data?.title}?`,
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

  const save = handleSubmit((values) => {
    emit('save', values); 
  });  

  watch(() => props.data, (newData) => {
    if (newData) {
      resetForm({ 
        values: {
          ...newData,
          releaseDate: toDate(newData.releaseDate),
          language: newData.originalLanguage,
        } 
      });
    }
  }, { immediate: true });
</script>