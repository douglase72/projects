<template>
  <header class="m-4">
    <div class="text-4xl font-bold mb-4">Series Edit</div>
    <div class="flex flex-col">
      <RouterLink to="/" class="hover:text-zinc-300">Home</RouterLink>
    </div>
  </header>

  <main class="m-8">
    <EditForm :data="series" 
              :specificSchema="schema" 
              @delete="handleDelete" 
              @export="handleExport"
              @save="handleSave">
      <template #other="{ values, errors, setFieldValue }">
        <div class="flex flex-col items-start">
          <label for="type" class="font-bold">Series Type</label>
          <Select :model-value="values.type" 
                  @update:model-value="(val) => setFieldValue('type', val)"
                  :options="typeOptions" />
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
  import type { SaveSeries } from '@/models/SaveSeries';
  import type { Series } from '@/models/Series';
  import { SeriesType } from '@/models/SeriesType';

  const schema = z.object({
    type: z.nativeEnum(SeriesType).nullable(),
  });

  const { deleteSeries, findSeries, saveSeries } = useEmdbApi();
  const route = useRoute();
  const router = useRouter();

  const series = ref<Series>();
  const typeOptions = Object.values(SeriesType);        

  onMounted(async () => {
    const routeId = route.params.id;
    const id = Number(routeId);
    if (Number.isNaN(id)) {
      router.push('/'); 
      return;
    }

    let data;
    const importedData = history.state.importedData;
    if (importedData) {
      data = importedData;
    } else if (id > 0) {
      data = await findSeries(id);
    } else {
      router.push('/');
      return;        
    }
    series.value = data;
  });

  const handleDelete = async (form: any) => {
    if (!series.value) return;
    deleteSeries(series.value);
    router.push('/'); 
  };  

  const handleExport = async (form: any) => {
    if (!series.value) return;

    const dataToExport = {
      ...series.value,
      ...form,
    };   
    const json = JSON.stringify(dataToExport, null, 2);
    const blob = new Blob([json], { type: 'application/json' });
    const url = URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = `${(form.title || 'movie')
      .replace(/[^a-z0-9\s]/gi, '')
      .trim() 
      .replace(/[\s+]/g, '-')}.json`;
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    URL.revokeObjectURL(url);    
  };

  const handleSave = async (form: any) => {
    if (!series.value) return; 

    const command: SaveSeries = {
      tmdbId: series.value.tmdbId,
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
    const savedSeries = await saveSeries(command);
    if (savedSeries) {
      router.push(`/series/${savedSeries.id}`); 
    }
  };
</script>