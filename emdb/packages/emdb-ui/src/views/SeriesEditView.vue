<template>
  <header class="m-4">
    <div class="text-4xl font-bold mb-4">Series Edit</div>
    <div class="flex flex-col">
      <RouterLink to="/" class="hover:text-zinc-300">Home</RouterLink>
    </div>
  </header>

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
              <div class="flex justify-end">
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
                           @row-edit-save="onRoleSave($event, slotProps.data.roles)">
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
              <div class="flex justify-end">
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
                           @row-edit-save="onRoleSave($event, slotProps.data.jobs)">
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
  import { onMounted, ref } from 'vue';
  import { useRoute, useRouter } from 'vue-router';
  import { FilterMatchMode } from '@primevue/core/api';
  import * as z from 'zod';

  import { useEmdbApi } from '@/composables/useEmdbApi';
  import { 
    type Series, 
    SeriesType, 
    type UpdateRole, 
    type UpdateSeries,
    type UpdateSeriesCredit } from '@emdb/common';

  const route = useRoute();
  const router = useRouter();
  const { deleteSeries, findSeries, updateRole, updateSeries, updateSeriesCredit } = useEmdbApi();

  const expandedRows = ref({});
  const editingCredits = ref([]);
  const editingRoles = ref([]);
  const filters = ref({
    global: { value: null, matchMode: FilterMatchMode.CONTAINS }
  });  

  const schema = z.object({
    type: z.nativeEnum(SeriesType).nullable(),
  });  

  const series = ref<Series>();
  const typeOptions = Object.values(SeriesType); 

  const onCreditSave = async (event: any, cast: any) => {
    let { newData, index } = event;
    const command: UpdateSeriesCredit = {
      totalEpisodes: newData.totalEpisodes,
      order: newData.order,
    }; 

    try {
      await updateSeriesCredit(newData.creditId, command);
      cast[index] = newData;
    } catch (error) {
      console.error("Failed to update credit.");
    }
  };
  
  const onRoleSave = async (event: any, items: any) => {
    let { newData, index } = event;
    const role = newData.character || newData.title;
    const command: UpdateRole = {
      role: role,
      episodeCount: newData.episodeCount,
    };

    try {
      await updateRole(newData.id, command);
      items[index] = newData;
    } catch (error) {
      console.error("Failed to update role.");
    }
  };

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