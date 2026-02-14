<template>
  <header class="m-4">
    <div class="text-4xl font-bold mb-4">Person Edit</div>
    <div class="flex flex-col">
      <RouterLink to="/" class="hover:text-zinc-300">Home</RouterLink>
    </div>
  </header>

  <main class="m-8">
    <section class="flex flex-col gap-y-6">
      <div class="flex flex-col">
        <label for="name" class="font-bold">Name</label>
        <InputText id="name" v-model="name" v-bind="nameAttrs" :invalid="!!errors.name" />
        <small class="text-red-500">{{ errors.name }}</small>        
      </div>

      <div class="flex gap-x-8">
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
      </div>

      <div class="flex flex-col">
        <label for="birthPlace" class="font-bold">Place of Birth</label>
        <InputText id="birthPlace" v-model="birthPlace" :invalid="!!errors.name" />
        <small class="text-red-500">{{ errors.birthPlace }}</small>        
      </div> 
        
      <div class="flex flex-col max-w-90">
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

    <div class="mt-12">
      <div class="flex gap-4">
        <Button label="Save" icon="pi pi-check" @click="onSave" :disabled="isSubmitting" />

        <div class="ml-auto flex gap-4">
          <Button label="Delete" icon="pi pi-trash" @click="confirmDelete" />
          <Button label="Cancel" severity="secondary" @click="onCancel" />
        </div>  
      </div>
    </div>    
  </main>

  <ConfirmDialog />
</template>

<script setup lang="ts">
  import { onMounted, ref } from 'vue'; 
  import { toTypedSchema } from '@vee-validate/zod';
  import { useConfirm } from "primevue/useconfirm";
  import { useRoute, useRouter } from 'vue-router';
  import { useForm } from 'vee-validate';
  import * as z from 'zod';

  import { useEmdbApi } from '@/composables/useEmdbApi';
  import { useTime } from '@/composables/useTime';
  import { Gender, type Person, type UpdatePerson } from '@emdb/common';

  const confirm = useConfirm();
  const route = useRoute();
  const router = useRouter();
  const { deletePerson, findPerson, updatePerson } = useEmdbApi();
  const { toDate, toDateString } = useTime();

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

  const confirmDelete = () => {
    confirm.require({
      header: 'Confirm Delete',
      message: `Delete ${person.value?.name}?`,
      icon: 'pi pi-exclamation-triangle',
      rejectLabel: 'Cancel',
      acceptLabel: 'Delete',
      rejectClass: 'p-button-secondary p-button-outlined',
      accept: async () => {
        if (!person.value) return;
        await deletePerson(person.value);
        router.push('/'); 
      },
    });
  };

  onMounted(async () => {
    const id = Number(route.params.id);
    if (Number.isNaN(id)) {
      router.push('/'); 
      return;
    }
    person.value = await findPerson(id);
    resetForm({
      values: {
        ...person.value,
        birthDate: toDate(person.value?.birthDate),
        deathDate: toDate(person.value?.deathDate),
    }});    
  });

  const onCancel = () => {
    router.back();
  };  

  const onSave = handleSubmit(async (form: any) => {
    if (!person.value) return; 

    const command: UpdatePerson = {
      name: form.name,
      birthDate: toDateString(form.birthDate),
      deathDate: toDateString(form.deathDate),
      gender: form.gender,
      birthPlace: form.birthPlace,
      profile: form.profile.replace(/\.jpg$/i, ''),
      biography: form.biography,
    };
   const updatedPerson = await updatePerson(person.value.id, command);
    if (updatedPerson) {
      router.push(`/person/${updatedPerson.id}`); 
    }
  });
</script>