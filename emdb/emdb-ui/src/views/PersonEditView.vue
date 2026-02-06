<template>
  <header class="m-4">
    <div class="text-4xl font-bold mb-4">Person Edit</div>
    <div class="flex flex-col">
      <RouterLink to="/" class="hover:text-zinc-300">Home</RouterLink>
    </div>
  </header>

  <main class="m-8">
    <section class="flex flex-col gap-y-4">
      <div class="flex flex-col">
        <label for="name" class="font-bold">Name</label>
        <InputText id="name" v-model="name" v-bind="nameAttrs" :invalid="!!errors.name" />
        <small class="text-red-500">{{ errors.name }}</small>        
      </div>

      <div class="flex flex-col items-start">
        <label for="birthDate" class="font-bold">Birth Date</label>
        <DatePicker v-model="birthDate"
                    dateFormat="yy-mm-dd" 
                    showIcon 
                    :invalid="!!errors.birthDate" />
        <small class="text-red-500">{{ errors.birthDate }}</small>
      </div>

      <div class="flex flex-col items-start">
        <label for="deathDate" class="font-bold">Death Date</label>
        <DatePicker v-model="deathDate"
                    dateFormat="yy-mm-dd" 
                    showIcon 
                    :invalid="!!errors.deathDate" />
        <small class="text-red-500">{{ errors.deathDate }}</small>
      </div> 
      
      <div class="flex flex-col items-start">
        <label for="gender" class="font-bold">Gender</label>
        <Select inputId="gender" v-model="gender" :options="genderOptions" />
      </div> 
      
      <div class="flex flex-col">
        <label for="birthPlace" class="font-bold">Place of Birth</label>
        <InputText id="birthPlace" v-model="birthPlace" :invalid="!!errors.name" />
        <small class="text-red-500">{{ errors.birthPlace }}</small>        
      </div>
      
      <div class="flex flex-col max-w-110">
        <label for="profile" class="font-bold">Profile</label>
        <InputText id="profile" v-model="profile" :invalid="!!errors.profile" />
        <small class="text-red-500">{{ errors.profile }}</small>        
      </div>
      
      <div class="flex flex-col">
        <label for="biography" class="font-bold">Biography</label>
        <Textarea id="biography" v-model="biography" rows="5" :invalid="!!errors.biography" />
        <small class="text-red-500">{{ errors.biography }}</small>
      </div>      
    </section>

    <section class="mt-12">
      <EditToolbar :title="values.name"
                   :is-valid="meta.valid" 
                   :is-submitting="isSubmitting"
                   @save="onSave"
                   @export="onExport"
                   @delete="onDelete" />
    </section>
  </main>
</template>

<script setup lang="ts">
  import { onMounted, ref } from 'vue';
  import { toTypedSchema } from '@vee-validate/zod';
  import * as z from 'zod';
  import { useForm } from 'vee-validate';
  import { useRoute, useRouter } from 'vue-router';

  import { useEmdbApi } from '@/composables/useEmdbApi';
  import { Gender } from '@/models/Gender';
  import type { Person } from '@/models/Person';
  import type { SavePerson } from '@/models/SavePerson';

  const { deletePerson, findPerson, savePerson, toDate, toDateString } = useEmdbApi();
  const route = useRoute();
  const router = useRouter();

  const person = ref<Person>();
  const genderOptions = Object.values(Gender);

  const pattern = /^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}(\.jpg)?$/i;
  const validationSchema = toTypedSchema(
    z.object({
      name: z.string().min(1, 'Name is required'),
      birthDate: z.date().nullable(),
      deathDate: z.date().nullable(),
      gender: z.nativeEnum(Gender).nullable(),
      birthPlace: z.string().nullable(),
      profile: z.string().regex(pattern).nullable().or(z.literal('')), 
      biography: z.string().nullable(), 
    })
  );

  const { defineField, 
          handleSubmit, 
          errors, 
          resetForm, 
          meta, 
          isSubmitting, 
          values } = useForm({
    validationSchema,
  });
  
  const [name, nameAttrs] = defineField('name');
  const [birthDate] = defineField('birthDate');
  const [deathDate] = defineField('deathDate');
  const [gender] = defineField('gender');
  const [birthPlace] = defineField('birthPlace');
  const [profile] = defineField('profile');
  const [biography] = defineField('biography');

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
      data = await findPerson(id);
    } else {
      router.push('/');
      return;      
    }
    person.value = data;
    resetForm({
      values: {
        ...data,
        birthDate: toDate(data?.birthDate),
        deathDate: toDate(data?.deathDate),
    }});
  }); 

  const onDelete = () => {
    if (!person.value) return;
    deletePerson(person.value);
    router.push('/'); 
  };  

  const onExport = handleSubmit((form) => {
    if (!person.value) return;

    const dataToExport = {
      ...person.value,
      ...form,
      birthDate: toDateString(form.birthDate),
      deathDate: toDateString(form.deathDate),
    };   
    const json = JSON.stringify(dataToExport, null, 2);
    const blob = new Blob([json], { type: 'application/json' });
    const url = URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = `${(form.name)
      .replace(/[^a-z0-9\s]/gi, '')
      .trim() 
      .replace(/[\s+]/g, '-')}-${form.birthDate?.getFullYear()}.json`;
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    URL.revokeObjectURL(url);
  });

  const onSave = handleSubmit(async (form: any) => {
    if (!person.value) return; 

    const command: SavePerson = {
      tmdbId: person.value.tmdbId,
      name: form.name,
      birthDate: toDateString(form.birthDate),
      deathDate: toDateString(form.deathDate),
      gender: form.gender ?? null,
      birthPlace: form.birthPlace,
      profile: form.profile.replace(/\.jpg$/i, ''),
      biography: form.biography,
    }; 
    const savedPerson = await savePerson(command);
    if (savedPerson) {
      router.push(`/person/${savedPerson.id}`); 
    }       
  });  
</script>