<template>
  <header class="m-4">
    <div class="text-4xl font-bold mb-4">Movie Edit</div>
    <div class="flex flex-col">
      <RouterLink to="/" class="hover:text-zinc-300">Home</RouterLink>
    </div>
  </header>

  <main class="m-8">
    <ShowEditForm :data="movie" 
                  :specificSchema="schema"
                  @save="handleSave"
                  @delete="handleDelete">
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
        <div class="flex gap-x-8">
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
        </div>        
      </template>      
    </ShowEditForm>
  </main>
</template>

<script setup lang="ts">
  import { onMounted, ref } from 'vue';
  import { useRoute, useRouter } from 'vue-router';
  import * as z from 'zod';

  import { useEmdbApi } from '@/composables/useEmdbApi';
  import { CreditType, type Movie, type UpdateMovie } from '@emdb/common';

  const route = useRoute();
  const router = useRouter();
  const { deleteMovie, findMovie, toDateString , updateMovie } = useEmdbApi();

  const schema = z.object({
    releaseDate: z.date().nullable(),
    runtime: z.number().int().positive().nullable(),
    budget: z.number().int().positive().nullable(),
    revenue: z.number().int().positive().nullable(),    
  });    

  const movie = ref<Movie>();

  onMounted(async () => {
    const id = Number(route.params.id);
    if (Number.isNaN(id)) {
      router.push('/'); 
      return;
    }
    movie.value = await findMovie(id);
  });

  const handleDelete = async () => {
    if (!movie.value) return;
    deleteMovie(movie.value);
    router.push('/'); 
  };
  
  const handleSave = async (form: any) => {
    if (!movie.value) return;

    const cast = (movie.value.credits.cast || []).map(c => ({
      id: c.creditId,
      personId: c.id,
      type: CreditType.CAST,
      role: c.character,
      order: c.order
    }));
    const crew = (movie.value.credits.crew || []).map(c => ({
      id: c.creditId,
      personId: c.id,
      type: CreditType.CREW,
      role: c.job,
      order: c.order
    }));

    const command: UpdateMovie = {
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
      poster: form.poster.replace(/\.jpg$/i, ''),
      tagline: form.tagline,
      overview: form.overview,
      credits: [...cast, ...crew]
    };
    const updatedMovie = await updateMovie(movie.value.id, command);
    if (updatedMovie) {
      router.push(`/movie/${updatedMovie.id}`); 
    }    
  };
</script>

