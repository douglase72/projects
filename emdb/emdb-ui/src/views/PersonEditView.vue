<template>
  <main class="flex flex-col gap-y-6 m-8">
    <div class="flex flex-col">
      <label for="name" class="font-bold">Name</label>
      <InputText id="title" v-model="name" v-bind="nameAttrs" :invalid="!!errors.name" />
      <small v-if="errors.name" class="text-red-500">{{ errors.name }}</small>
    </div>

    <div class="flex gap-x-8">
      <div class="flex flex-col items-start">
        <label for="birthDate" class="font-bold">Birth Date</label>
        <DatePicker id="birthDate" v-model="birthDate" v-bind="birthDateAttrs" 
                    :invalid="!!errors.birthDate" dateFormat="yy-mm-dd" show-icon />
        <small v-if="errors.birthDate" class="text-red-500">{{ errors.birthDate }}</small>
      </div>

      <div class="flex flex-col items-start">
        <label for="deathDate" class="font-bold">Death Date</label>
        <DatePicker id="deathDate" v-model="deathDate" v-bind="deathDateAttrs" 
                    :invalid="!!errors.deathDate" dateFormat="yy-mm-dd" show-icon />
        <small v-if="errors.deathDate" class="text-red-500">{{ errors.deathDate }}</small>
      </div>      
    </div>

    <div class="flex flex-col">
      <label for="biography" class="font-bold">Biography</label>
      <Textarea id="biography" v-model="biography" rows="10" :invalid="!!errors.biography" />
      <small class="text-red-500">{{ errors.biography }}</small>
    </div>     

    <div class="mt-12 flex gap-4">
      <Button label="Save" :loading="isSubmitting" :disabled="isSubmitting" @click="onSubmit" />
      <Button label="Cancel" severity="secondary" text @click="onCancel" />
      <Button label="Delete" icon="pi pi-trash" severity="danger" outlined class="ml-auto" @click="onDelete" />
    </div>      
  </main>
</template>

<script setup lang="ts">
  import { onMounted, ref } from 'vue';
  import { useConfirm } from 'primevue/useconfirm';
  import { toTypedSchema } from '@vee-validate/zod';
  import { useForm } from 'vee-validate';
  import { useRoute, useRouter } from 'vue-router';
  import { z } from 'zod';
  import Button from 'primevue/button';
  import DatePicker from 'primevue/datepicker';
  import InputText from 'primevue/inputtext';
  import Textarea from 'primevue/textarea';

  import { findPerson, type Person } from '@/lib/emdbQueryApi';
  import { deletePerson, updatePerson, type UpdatePersonRequest } from '@/lib/emdbCommandApi';
  import { toDate, toIso } from '@/lib/formatter';
  import { useNotificationService } from '@/composables/useNotificationService';

  const confirm = useConfirm();
  const notify = useNotificationService();
  const person = ref<Person>();
  const route = useRoute();
  const router = useRouter();

  const schema = z.object({
    name: z.string({ required_error: 'Name is required' }).min(1, 'Name is required'),
    birthDate: z.date().nullable().default(null),
    deathDate: z.date().nullable().default(null),
    biography: z.string().nullable(),  
  });

  type PersonForm = z.infer<typeof schema>;

  const { handleSubmit, errors, defineField, resetForm, isSubmitting } = useForm<PersonForm>({
    validationSchema: toTypedSchema(schema),
  }); 
  
  const [name, nameAttrs] = defineField('name');
  const [birthDate, birthDateAttrs] = defineField('birthDate'); 
  const [deathDate, deathDateAttrs] = defineField('deathDate'); 
  const [biography] = defineField('biography');

  const onCancel = () => {
    router.back();
  };

  const onDelete = () => {
    if (!person.value) return;

    confirm.require({
      header: 'Confirm Delete',
      message: `Delete ${person.value.name}?`,
      icon: 'pi pi-exclamation-triangle',
      rejectProps: { label: 'Cancel', severity: 'secondary', outlined: true },
      acceptProps: { label: 'Delete', severity: 'danger' },       
      accept: async () => {
        if (!person.value) return;

        try {
          await deletePerson(person.value.id);
          router.push('/');
          notify.info(`Deleted ${person.value.name}`);
        } catch (e) {
          notify.error(`Failed to delete ${person.value.name}`, e);
        }        
      },
    });
  };  

  onMounted(async () => {
    const raw = route.params.id;
    const id = Array.isArray(raw) ? raw[0] : raw;
    if (!id) {
      router.replace('/')
      return;
    }

    try {
      const found = await findPerson(id);
      if (!found) {
        notify.warn(`No person exists with id ${id}`);
        router.replace('/');
        return;
      }
      person.value = found;

      resetForm({
        values: {
          name: found.name,
          birthDate: found.birthDate ? toDate(found.birthDate) : null,
          deathDate: found.deathDate ? toDate(found.deathDate) : null,
          biography: found.biography,
        },
      });
    } catch (e) {
      notify.error('Failed to load person', e);
      router.replace('/');
    }
  });

  const onSubmit = handleSubmit(async (values) => {
    if (!person.value) return;

    const request: UpdatePersonRequest = {
      version: person.value.version,
      name: values.name,
      birthDate: values.birthDate ? toIso(values.birthDate) : null,
      deathDate: values.deathDate ? toIso(values.deathDate) : null,
      gender: person.value.gender,
      biography: values.biography,
    };

    try {
      const response = await updatePerson(person.value.id, request);
      person.value = { ...person.value, version: response.version };
      resetForm({ values });
      notify.info(`Saved ${person.value.name}`);
    } catch (e) {
      notify.error(`Failed to save ${person.value.name}`, e);
    }    
  });
</script>