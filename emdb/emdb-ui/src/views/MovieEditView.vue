<template>
  <main class="m-8">
    <div class="flex flex-col gap-y-6">
      <div class="flex flex-col">
        <label for="title" class="font-bold">Title</label>
        <InputText id="title" 
                   v-model="title" 
                   v-bind="titleAttrs" 
                   :invalid="!!errors.title" />
        <small v-if="errors.title" class="text-red-500">{{ errors.title }}</small>
      </div>

      <div class="flex flex-col items-start">
        <label for="releaseDate" class="font-bold">Release Date</label>
        <DatePicker id="releaseDate" 
                    v-model="releaseDate" 
                    v-bind="releaseDateAttrs" 
                    :invalid="!!errors.releaseDate" 
                    dateFormat="yy-mm-dd" 
                    show-icon />
        <small v-if="errors.releaseDate" class="text-red-500">{{ errors.releaseDate }}</small>
      </div>
      
      <div class="mt-12">
        <div class="flex gap-4">
          <Button label="Save" :loading="isSubmitting" :disabled="isSubmitting" @click="onSubmit" />

          <div class="ml-auto flex gap-4">
            <Button label="Delete" icon="pi pi-trash" @click="onDelete" />
            <Button label="Cancel" severity="secondary" @click="onCancel" />
          </div>
        </div>
      </div>
    </div>    
  </main>
</template>

<script setup lang="ts">
  import { onMounted, ref } from 'vue';
  import { useConfirm } from 'primevue/useconfirm';
  import { toTypedSchema } from '@vee-validate/zod';
  import { useForm } from 'vee-validate';
  import { useRoute, useRouter } from 'vue-router';
  import { z } from 'zod';
  import Button from 'primevue/button';
  import DatePicker from 'primevue/datepicker';
  import InputText from 'primevue/inputtext';

  import { findMovie, type Movie } from '@/lib/emdbQueryApi';
  import { deleteMovie, updateMovie, type UpdateMovieRequest } from '@/lib/emdbCommandApi';
  import { useErrorHandler } from '@/composables/useErrorHandler';

  const { handleError, handleNotFound } = useErrorHandler();

  const route = useRoute();
  const router = useRouter();
  const confirm = useConfirm();
  const movie = ref<Movie>();

  const schema = z.object({
    title: z.string({ required_error: 'Title is required' }).min(1, 'Title is required'),
    releaseDate: z.date().nullable().default(null),
  }); 
  
  type MovieForm = z.infer<typeof schema>;

  const { handleSubmit, errors, defineField, resetForm, isSubmitting } = useForm<MovieForm>({
    validationSchema: toTypedSchema(schema),
  }); 

  const [title, titleAttrs] = defineField('title');
  const [releaseDate, releaseDateAttrs] = defineField('releaseDate'); 

  onMounted(async () => {
    const raw = route.params.id;
    const id = Array.isArray(raw) ? raw[0] : raw;
    if (!id) {
      router.replace('/')
      return;
    }

    try {
      const found = await findMovie(id);
      if (!found) {
        handleNotFound(`No movie exists with id ${id}`);
        router.replace('/');
        return;
      }
      movie.value = found;

      resetForm({
        values: {
          title: found.title,
          releaseDate: found.releaseDate ? toDate(found.releaseDate) : null,
        },
      });      
    } catch (e) {
      handleError(e, 'Failed to load movie');
      router.replace('/');
    }
  });

  const onSubmit = handleSubmit(async (values) => {
    if (!movie.value) return;

    const request: UpdateMovieRequest = {
      version: movie.value.version,
      title: values.title,
      releaseDate: values.releaseDate ? toIso(values.releaseDate) : null,
      originalLanguage: movie.value.originalLanguage
    };

    try {
      const response = await updateMovie(movie.value.id, request);
      movie.value = { ...movie.value, version: response.version };
      resetForm({ values });
    } catch (e) {
      handleError(e, 'Failed to save movie');
    }    
  });

  const onCancel = () => {
    router.back();
  };

  const onDelete = () => {
    if (!movie.value) return;

    confirm.require({
      header: 'Confirm Delete',
      message: `Delete ${movie.value.title}?`,
      icon: 'pi pi-exclamation-triangle',
      rejectProps: { label: 'Cancel', severity: 'secondary', outlined: true },
      acceptProps: { label: 'Delete', severity: 'danger' },       
      accept: async () => {
        if (!movie.value) return;

        try {
          await deleteMovie(movie.value.id);
          router.push('/'); 
        } catch (e) {
          handleError(e, 'Failed to delete movie');
        }        
      },
    });
  };    

  const toDate = (iso: string): Date =>
    new Date(
      Number(iso.slice(0, 4)),
      Number(iso.slice(5, 7)) - 1,
      Number(iso.slice(8, 10)),
    );
  
  const toIso = (date: Date): string =>
    `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`;  
</script>