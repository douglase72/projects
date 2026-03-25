<template>
  <main class="m-8">
    <Tabs value="general">
      <TabList>
        <Tab value="general">General Info</Tab>
        <Tab value="cast">Cast</Tab>
        <Tab value="crew">Crew</Tab>
      </TabList>

      <TabPanels>
        <TabPanel value="general">
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
        </TabPanel>

        <TabPanel value="cast">
          Cast
        </TabPanel>

        <TabPanel value="crew">
          Crew
        </TabPanel>        
      </TabPanels>
    </Tabs>
  </main>
</template>

<script setup lang="ts">
  import { onMounted, ref } from 'vue';
  import { useRoute, useRouter } from 'vue-router';
  import { useToast } from "primevue/usetoast";
  import * as z from 'zod';

  import { useEmdbApi } from '@/composables/useEmdbApi';
  import { useErrorHandler } from '@/composables/useErrorHandler';
  import { 
    type Series, 
    SeriesType, 
    type UpdateSeries } from "@emdb/common";

  const route = useRoute();
  const router = useRouter();
  const toast = useToast();
  const { deleteSeries, findSeries, updateSeries } = useEmdbApi();
  const { handleError } = useErrorHandler();  

  const series = ref<Series>();
  const schema = z.object({
    type: z.nativeEnum(SeriesType).nullable(),
  }); 
  const typeOptions = Object.values(SeriesType); 

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

    try {
      const updatedSeries = await updateSeries(series.value.id, command);
      toast.add({ severity: 'success', summary: 'Success', detail: `Saved ${updatedSeries?.title}`, life: 5000 });
      router.push(`/series/${updatedSeries?.id}`);
    } catch (e) {
      handleError(e, 'Failed to update series');
    }
  };

  const handleDelete = async () => {
    if (!series.value) return;
    try {
      await deleteSeries(series.value.id);
      toast.add({ severity: 'success', summary: 'Success', detail: `Deleted ${series.value.title}`, life: 5000 });
      router.push('/'); 
    } catch (e) {
      handleError(e, 'Failed to delete movie');
    }
  };

  onMounted(async () => {
    const id = Number(route.params.id);
    if (Number.isNaN(id)) {
      router.push('/'); 
      return;
    }

    try {
      series.value = await findSeries(id);
    } catch (e) {
      handleError(e, 'Failed to load series');
      router.push('/'); 
    }
  });
</script>