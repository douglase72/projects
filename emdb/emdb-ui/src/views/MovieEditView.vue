<template>
  <main class="flex flex-col gap-y-6 m-8">
    <div class="flex flex-col">
      <label for="title" class="font-bold">Title</label>
      <InputText id="title" v-model="title" v-bind="titleAttrs" :invalid="!!errors.title" />
      <small v-if="errors.title" class="text-red-500">{{ errors.title }}</small>
    </div>

    <div class="flex gap-x-8">
      <div class="flex flex-col items-start">
        <label for="releaseDate" class="font-bold">Release Date</label>
        <DatePicker id="releaseDate" v-model="releaseDate" v-bind="releaseDateAttrs" 
                    :invalid="!!errors.releaseDate" dateFormat="yy-mm-dd" show-icon />
        <small v-if="errors.releaseDate" class="text-red-500">{{ errors.releaseDate }}</small>
      </div>

      <div class="flex flex-col items-start">
        <label for="score" class="font-bold">Score</label>
        <InputNumber inputId="score" v-model="score" :maxFractionDigits="6" :invalid="!!errors.score" />
        <small class="text-red-500">{{ errors.score }}</small>
      </div>
        
      <div class="flex flex-col items-start">
        <label for="language" class="font-bold">Original Language</label>
        <Select inputId="language" v-model="language"  v-bind="languageAttrs"
                :options="languageCodes" optionLabel="label" optionValue="value" 
                :invalid="!!errors.language" filter />
        <small v-if="errors.language" class="text-red-500">{{ errors.language }}</small>  
      </div>        
    </div>

    <div class="flex flex-col">
      <label for="overview" class="font-bold">Overview</label>
      <Textarea id="overview" v-model="overview" rows="5" :invalid="!!errors.overview" />
      <small class="text-red-500">{{ errors.overview }}</small>
    </div>  

    <div class="mt-12 flex gap-4">
      <Button label="Save" :loading="isSubmitting" :disabled="isSubmitting" @click="onSubmit" />
      <Button label="Cancel" severity="secondary" text @click="onCancel" />
      <Button label="Delete" icon="pi pi-trash" severity="danger" outlined class="ml-auto" @click="onDelete" />
    </div>
  </main>
</template>

<script setup lang="ts">
  import { onMounted, ref } from 'vue';
  import { useConfirm } from 'primevue/useconfirm';
  import { toTypedSchema } from '@vee-validate/zod';
  import { useForm } from 'vee-validate';
  import { useRoute, useRouter } from 'vue-router';
  import { useToast } from 'primevue/usetoast'
  import { z } from 'zod';
  import Button from 'primevue/button';
  import DatePicker from 'primevue/datepicker';
  import InputNumber from 'primevue/inputnumber';
  import InputText from 'primevue/inputtext';
  import Select from 'primevue/select';
  import Textarea from 'primevue/textarea';

  import { findMovie, type Movie } from '@/lib/emdbQueryApi';
  import { deleteMovie, updateMovie, type UpdateMovieRequest } from '@/lib/emdbCommandApi';
  import { toDate, toIso } from '@/lib/formatter';
  import { useErrorHandler } from '@/composables/useErrorHandler';
  import { SUPPORTED_CODES, useLanguage } from '@/composables/useLanguage';

  const { handleError, handleNotFound } = useErrorHandler();

  const confirm = useConfirm();
  const movie = ref<Movie>();
  const { languageCodes, toLanguageCode } = useLanguage();
  const route = useRoute();
  const router = useRouter();
  const toast = useToast();

  const schema = z.object({
    title: z.string({ required_error: 'Title is required' }).min(1, 'Title is required'),
    releaseDate: z.date().nullable().default(null),
    score: z.number().min(0).max(10).nullable(),
    language: z.enum(SUPPORTED_CODES).nullable(),
    overview: z.string().nullable(),  
  }); 
  
  type MovieForm = z.infer<typeof schema>;

  const { handleSubmit, errors, defineField, resetForm, isSubmitting } = useForm<MovieForm>({
    validationSchema: toTypedSchema(schema),
  }); 

  const [title, titleAttrs] = defineField('title');
  const [releaseDate, releaseDateAttrs] = defineField('releaseDate'); 
  const [score] = defineField('score');
  const [language, languageAttrs] = defineField('language');
  const [overview] = defineField('overview');

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
          toast.add({ severity: 'info', summary: `${movie.value.title} deleted` });
        } catch (e) {
          handleError(e, 'Failed to delete movie');
        }        
      },
    });
  };   

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
          score: found.score,
          language: toLanguageCode(found.originalLanguage),
          overview: found.overview,
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
      score: values.score,
      originalLanguage: values.language,
      overview: values.overview,
    };

    try {
      const response = await updateMovie(movie.value.id, request);
      movie.value = { ...movie.value, version: response.version };
      resetForm({ values });
      toast.add({ severity: 'info', summary: `${movie.value.title} saved` });
    } catch (e) {
      handleError(e, 'Failed to save movie');
    }    
  });
</script>