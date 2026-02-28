<template>
  <header class="m-4">
    <div class="text-4xl font-bold mb-4">Movie Edit</div>
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
        </TabPanel>

        <TabPanel value="cast">
          <DataTable :value="movie?.credits.cast"
                     dataKey="creditId"
                     editMode="row"
                     v-model:editingRows="editingCredits"
                     @row-edit-save="onCreditSave($event, movie?.credits.cast)"
                     paginator :rows="20" :rowsPerPageOptions="[10, 20, 50, 100]"
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

            <Column field="name" header="Name"></Column>

            <Column field="character" header="Character">
              <template #editor="{ data, field }">
                <InputText v-model="data[field]" autofocus class="w-full" />
              </template>
            </Column>

            <Column field="order" header="Order">
              <template #editor="{ data, field }">
                <InputNumber v-model="data[field]" class="w-full" />
              </template>            
            </Column>

            <Column :rowEditor="true" style="width: 10%; min-width: 8rem" bodyStyle="text-align:center"></Column>
          </DataTable>
        </TabPanel>

        <TabPanel value="crew">
          <DataTable :value="movie?.credits.crew"
                     dataKey="creditId"
                     editMode="row"
                     v-model:editingRows="editingCredits"
                     @row-edit-save="onCreditSave($event, movie?.credits.crew)"
                     paginator :rows="20" :rowsPerPageOptions="[10, 20, 50, 100]"
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

            <Column field="name" header="Name"></Column>

            <Column field="job" header="Job">
              <template #editor="{ data, field }">
                <InputText v-model="data[field]" autofocus class="w-full" />
              </template>
            </Column>

            <Column :rowEditor="true" style="width: 10%; min-width: 8rem" bodyStyle="text-align:center"></Column>
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
  import { useTime } from '@/composables/useTime';
  import { 
    type Movie, 
    type UpdateMovie,
    type UpdateMovieCredit } from '@emdb/common';

  const route = useRoute();
  const router = useRouter();
  const { deleteMovie, findMovie, updateMovie, updateMovieCredit } = useEmdbApi();
  const { toDateString } = useTime();

  const movie = ref<Movie>();
  const editingCredits = ref([]);
  const filters = ref({
    global: { value: null, matchMode: FilterMatchMode.CONTAINS }
  });    

  const schema = z.object({
    releaseDate: z.date().nullable(),
    runtime: z.number().int().positive().nullable(),
    budget: z.number().int().positive().nullable(),
    revenue: z.number().int().positive().nullable(),    
  });

  const onCreditSave = async (event: any, cast: any) => {
    let { newData, index } = event;
    const role = newData.character || newData.job;
    const command: UpdateMovieCredit = {
      role: role,
      order: newData.order,
    }; 

    try {
      await updateMovieCredit(newData.creditId, command);
      cast[index] = newData;
    } catch (error) {
      console.error("Failed to update credit.");
    }
  };

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
      overview: form.overview
    };
    const updatedMovie = await updateMovie(movie.value.id, command);
    if (updatedMovie) {
      router.push(`/movie/${updatedMovie.id}`); 
    }    
  };
</script>

