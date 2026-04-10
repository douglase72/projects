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
          <DataTable :value="series?.credits.cast"
                     dataKey="creditId"
                     editMode="row"
                     v-model:editingRows="editingCredits"
                     @row-edit-save="onCreditSave($event, series?.credits.cast)"
                     paginator :rows="20" :rowsPerPageOptions="[10, 20, 50, 100]"
                     v-model:expandedRows="expandedRows"
                     v-model:filters="filters" 
                     :globalFilterFields="['name']"
                     size="small"> 
            <template #header>
              <div class="flex">
                <span class="relative">
                  <i class="pi pi-search absolute top-2/4 -mt-2 left-3 text-surface-400 dark:text-surface-600" />
                  <InputText v-model="filters['global'].value" placeholder="Search cast..." class="pl-10 font-normal" />
                </span>
              </div>
            </template>
            <Column expander style="width: 5rem" />

            <Column field="name" header="Name"></Column>

            <Column field="order" header="Order">
              <template #editor="{ data, field }">
                <InputNumber v-model="data[field]" class="w-full" />
              </template>            
            </Column>

            <Column field="totalEpisodes" header="Total Episodes">
              <template #editor="{ data, field }">
                <InputNumber v-model="data[field]" class="w-full" />
              </template>
            </Column>

            <Column :rowEditor="true" style="width: 10%; min-width: 8rem" bodyStyle="text-align:center"></Column>

            <template #expansion="slotProps">
              <div class="mx-15">
                <DataTable :value="slotProps.data.roles"
                           dataKey="id"
                           editMode="row"
                           v-model:editingRows="editingRoles"
                           @row-edit-save="onRoleSave($event, slotProps.data.roles, slotProps.data.creditId)">
                  <Column field="character" header="Character">
                    <template #editor="{ data, field }">
                      <InputText v-model="data[field]" autofocus class="w-full" />
                    </template>
                  </Column>

                  <Column field="episodeCount" header="Number of Episodes">
                    <template #editor="{ data, field }">
                      <InputNumber v-model="data[field]" class="w-full" />
                    </template>
                  </Column>

                  <Column :rowEditor="true" style="width: 10%; min-width: 8rem" bodyStyle="text-align:center"></Column>
                </DataTable>
              </div>
            </template>
          </DataTable>          
        </TabPanel>

        <TabPanel value="crew">
          <DataTable :value="series?.credits.crew"
                     dataKey="creditId"
                     editMode="row"
                     v-model:editingRows="editingCredits"
                     @row-edit-save="onCreditSave($event, series?.credits.crew)"
                     paginator :rows="20" :rowsPerPageOptions="[10, 20, 50, 100]"
                     v-model:expandedRows="expandedRows"
                     v-model:filters="filters" 
                     :globalFilterFields="['name']"
                     size="small"> 
            <template #header>
              <div class="flex">
                <span class="relative">
                  <i class="pi pi-search absolute top-2/4 -mt-2 left-3 text-surface-400 dark:text-surface-600" />
                  <InputText v-model="filters['global'].value" placeholder="Search cast..." class="pl-10 font-normal" />
                </span>
              </div>
            </template>
            <Column expander style="width: 5rem" />

            <Column field="name" header="Name"></Column>

            <Column field="totalEpisodes" header="Total Episodes">
              <template #editor="{ data, field }">
                <InputNumber v-model="data[field]" class="w-full" />
              </template>
            </Column>

            <Column :rowEditor="true" style="width: 10%; min-width: 8rem" bodyStyle="text-align:center"></Column>

            <template #expansion="slotProps">
              <div class="mx-15">
                <DataTable :value="slotProps.data.jobs"
                           dataKey="id"
                           editMode="row"
                           v-model:editingRows="editingRoles"
                           @row-edit-save="onRoleSave($event, slotProps.data.jobs, slotProps.data.creditId)">
                  <Column field="title" header="Job">
                    <template #editor="{ data, field }">
                      <InputText v-model="data[field]" autofocus class="w-full" />
                    </template>
                  </Column>

                  <Column field="episodeCount" header="Number of Episodes">
                    <template #editor="{ data, field }">
                      <InputNumber v-model="data[field]" class="w-full" />
                    </template>
                  </Column>

                  <Column :rowEditor="true" style="width: 10%; min-width: 8rem" bodyStyle="text-align:center"></Column>
                </DataTable>
              </div>
            </template>
          </DataTable>          
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