<template>
  <header class="m-4">
    <div class="text-4xl font-bold mb-4">Movie Edit</div>
    <div class="flex flex-col">
      <RouterLink to="/" class="hover:text-zinc-300">Home</RouterLink>
    </div>
  </header>

  <main class="m-8">
    <EditForm :data="command" 
              :specificSchema="schema"
              @save="handleSave">
      <template #date="{ values, errors, setFieldValue }">
        <div class="flex flex-col items-start">
          <label for="releaseDate" class="font-bold">Release Date</label>
          <DatePicker :model-value="values.releaseDate" 
                      @update:model-value="(val) => setFieldValue('releaseDate', val)"
                      dateFormat="yy-mm-dd" 
                      showIcon 
                      :invalid="!!errors.releaseDate" />
          <small class="text-red-500">{{ errors.releaseDate }}</small>
        </div>
      </template>

      <template #other="{ values, errors, setFieldValue }">
        <div class="flex flex-col items-start">
          <label for="runtime" class="font-bold">Runtime</label>
          <InputNumber :model-value="values.runtime" 
                       @update:model-value="(val) => setFieldValue('runtime', val)"
                       suffix=" min" 
                       :invalid="!!errors.runtime" />
          <small class="text-red-500">{{ errors.runtime }}</small>
        </div>

        <div class="flex flex-col items-start">
          <label for="budget" class="font-bold">Budget</label>
          <InputNumber :model-value="values.budget"
                       @update:model-value="(val) => setFieldValue('budget', val)"
                       :invalid="!!errors.budget" 
                       mode="currency" 
                       currency="USD" 
                       locale="en-US" />
          <small class="text-red-500">{{ errors.budget }}</small>
        </div> 
      
        <div class="flex flex-col items-start">
          <label for="revenue" class="font-bold">Revenue</label>
          <InputNumber :model-value="values.revenue"
                       @update:model-value="(val) => setFieldValue('revenue', val)" 
                       :invalid="!!errors.revenue" 
                       mode="currency" 
                       currency="USD" 
                       locale="en-US" />
          <small class="text-red-500">{{ errors.revenue }}</small>
        </div>        
      </template>
    </EditForm>
  </main>
</template>

<script setup lang="ts">
  import { onMounted, ref } from 'vue';
  import { useRoute, useRouter } from 'vue-router';
  import * as z from 'zod';

  import { useEmdbApi } from '@/composables/useEmdbApi';
  import EditForm from '@/components/EditForm.vue';
  import type { SaveMovie } from '@/models/SaveMovie';

  const schema = z.object({
    releaseDate: z.date().nullable(),
    runtime: z.number().int().positive().nullable(),
    budget: z.number().int().positive().nullable(),
    revenue: z.number().int().positive().nullable(),
  });  

  const { deleteMovie, findMovie, saveMovie, toDateString } = useEmdbApi();
  const route = useRoute();
  const router = useRouter();

  const command = ref<SaveMovie>();

  onMounted(async () => {
    const routeId = route.params.id;
    const id = Number(routeId);
    if (Number.isNaN(id)) {
      router.push('/'); 
      return;
    }

    const importedData = history.state.importedData;
    if (importedData) {
      command.value = importedData;
    } else if (id > 0) {
      const data = await findMovie(id);
    } else {
      router.push('/');
      return;       
    }
  });

  const handleSave = async (form: any) => {
    if (!command.value) return; 

    const saveCommand: SaveMovie = {
      tmdbId: command.value.tmdbId,
      title: form.title,
      releaseDate: toDateString(form.releaseDate),
      score: form.score,
      status: form.status,
      runtime: form.runtime,
      budget: form.budget,
      revenue: form.revenue,
      homepage: form.homepage,
      originalLanguage: form.language,
      backdrop: form.backdrop.replace(/\.jpg$/i, ''),
      tmdbBackdrop: command.value.tmdbBackdrop,
      poster: form.poster.replace(/\.jpg$/i, ''),
      tmdbPoster: command.value.tmdbBackdrop,
      tagline: form.tagline,
      overview: form.overview,
      people: command.value.people,
    };  
    const savedMovie = await saveMovie(saveCommand);
    if (savedMovie) {
      router.push(`/movie/${savedMovie.id}`); 
    }
  };  
</script>