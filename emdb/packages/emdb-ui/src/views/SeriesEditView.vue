<template>
  <main class="m-8">
    <Tabs value="details">
      <TabList>
        <Tab value="details">Series</Tab>
        <Tab value="cast">Cast</Tab>
        <Tab value="crew">Crew</Tab>
      </TabList>

      <TabPanels>
        <TabPanel value="details">
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
          CAST         
        </TabPanel>

        <TabPanel value="crew">
          CREW        
        </TabPanel>        
      </TabPanels>
    </Tabs>
  </main>
</template>

<script setup lang="ts">
  import { FilterMatchMode } from '@primevue/core/api';
  import { onMounted, ref } from 'vue';
  import { useRoute, useRouter } from 'vue-router';
  import { useToast } from "primevue/usetoast";
  import * as z from 'zod';

  import { useEmdbApi } from '@/composables/useEmdbApi';
  import { useErrorHandler } from '@/composables/useErrorHandler';
  import { 
    type Series, 
    SeriesType, 
    type UpdateSeries,
    type UpdateSeriesCredit,
    type UpdateRole } from "@emdb/common";

  const route = useRoute();
  const router = useRouter();
  const toast = useToast();
  const { deleteSeries, findSeries, updateSeries, updateSeriesCredit, updateRole } = useEmdbApi();
  const { handleError } = useErrorHandler();  

  const series = ref<Series>();
  const expandedRows = ref({});
  const editingCredits = ref([]);
  const editingRoles = ref([]);
  const filters = ref({
    global: { value: null, matchMode: FilterMatchMode.CONTAINS }
  });  
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

  const onCreditSave = async (event: any, cast: any) => {
    if (!series.value) return;
    let { newData, index } = event;
    const command: UpdateSeriesCredit = {
      totalEpisodes: newData.totalEpisodes,
      order: newData.order,
    };

    try {
      await updateSeriesCredit(series.value.id, newData.creditId, command);
      cast[index] = newData;
    } catch (e) {
      handleError(e, 'Failed to update credit');
    }
  }; 
  
  const onRoleSave = async (event: any, roles: any, creditId: string) => {
    if (!series.value) return;
    let { newData, index } = event;
    const role = newData.character || newData.title;
    const command: UpdateRole = {
      role: role,
      episodeCount: newData.episodeCount,
    };

    try {
      await updateRole(series.value.id, creditId, newData.id, command);
      roles[index] = newData;
    } catch (e) {
      handleError(e, 'Failed to update role');
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