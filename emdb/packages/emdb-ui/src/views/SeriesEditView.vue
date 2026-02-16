<template>
  <header class="m-4">
    <div class="text-4xl font-bold mb-4">Movie Edit</div>
    <div class="flex flex-col">
      <RouterLink to="/" class="hover:text-zinc-300">Home</RouterLink>
    </div>
  </header>

  <main class="m-8">
    <ShowEditForm :data="series" 
                  :specificSchema="schema"
                  @save="handleSave"
                  @delete="handleDelete">
      <template #other="{ values, errors, setFieldValue }">
        <div class="flex flex-col items-start">
          <label for="type" class="font-bold">Series Type</label>
          <Select :model-value="values.type" 
                  @update:model-value="(val) => setFieldValue('type', val)"
                  :options="typeOptions" />
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
  import { type Series, SeriesType, type UpdateSeries } from '@emdb/common';

  const route = useRoute();
  const router = useRouter();
  const { deleteSeries, findSeries, updateSeries } = useEmdbApi();

  const schema = z.object({
    type: z.nativeEnum(SeriesType).nullable(),
  });  

  const series = ref<Series>();
  const typeOptions = Object.values(SeriesType);   

  const handleDelete = async () => {
    if (!series.value) return;
    deleteSeries(series.value);
    router.push('/'); 
  };

  const handleSave = async (form: any) => {
    if (!series.value) return;

    const command: UpdateSeries = {
      title: form.title,
      score: form.score,
      status: form.status,
      type: form.type,
      homepage: form.homepage,
      originalLanguage: form.language,
      backdrop: form.backdrop.replace(/\.jpg$/i, ''),
      poster: form.poster.replace(/\.jpg$/i, ''),
      tagline: form.tagline,
      overview: form.overview,
    };
    const updatedSeries = await updateSeries(series.value.id, command);
    if (updatedSeries) {
      router.push(`/series/${updatedSeries.id}`); 
    }    
  };  

  onMounted(async () => {
    const id = Number(route.params.id);
    if (Number.isNaN(id)) {
      router.push('/'); 
      return;
    }
    series.value = await findSeries(id);
  });
</script>